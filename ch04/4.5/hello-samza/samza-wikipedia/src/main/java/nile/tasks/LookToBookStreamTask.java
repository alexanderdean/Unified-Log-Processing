package nile.tasks;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.apache.samza.task.WindowableTask;

public class LookToBookStreamTask
  implements StreamTask, InitableTask, WindowableTask {

  private KeyValueStore<String, Integer> store;
  private Set<String> products = new HashSet<String>();

  public void init(Config config, TaskContext context) {
    this.store = (KeyValueStore<String, Integer>)
      context.getStore("nile-looktobook");
  }

  @SuppressWarnings("unchecked")
  @Override
  public void process(IncomingMessageEnvelope envelope,
    MessageCollector collector, TaskCoordinator coordinator) {

    Map<String, Object> event =
      (Map<String, Object>) envelope.getMessage();
    String verb = (String) event.get("verb");
    
    if (verb.equals("view")) {                                         // a
      String product = (String) ((Map<String, Object>)
        event.get("directObject")).get("product");
      incrementViews(product);

    } else if (verb.equals("place")) {                                 // b
      Collection<Map<String, Object>> items =
        (Collection<Map<String, Object>>)
        ((Map<String, Object>) ((Map<String, Object>)
        event.get("directObject")).get("order")).get("items");
      for (Map<String, Object> item : items) {
        String product = (String) item.get("product");
        Integer quantity = (Integer) item.get("quantity");
        incrementPurchases(product, quantity);
      }
    }
  }

  @Override
  public void window(MessageCollector collector,
    TaskCoordinator coordinator) {

    Map<String, HashMap<String, Integer>> allCounts =
      new HashMap<String, HashMap<String, Integer>>();

    for (String product : products) {                                  // c
      HashMap<String, Integer> counts = new HashMap<String, Integer>();
      counts.put("views", store.get(asViewKey(product)));
      counts.put("purchases", store.get(asPurchaseKey(product)));
      allCounts.put(product, counts);
    }

    collector.send(new OutgoingMessageEnvelope(
      new SystemStream("kafka", "nile-looktobook-stats"), allCounts));

    products.clear();
  }

  private static String asViewKey(String product) {                    // d
    return product + "-views";
  }

  private static String asPurchaseKey(String product) {                // e
    return product + "-purchases";
  }

  private void incrementViews(String product) {                        // f
    String viewKey = asViewKey(product);
    Integer viewsLifetime = store.get(viewKey);
    if (viewsLifetime == null) viewsLifetime = 0;
    
    store.put(viewKey, viewsLifetime + 1);
    products.add(product);
  }

  private void incrementPurchases(String product,
    Integer quantity) {                                                // g

    String purchaseKey = asPurchaseKey(product);
    Integer purchasesLifetime = store.get(purchaseKey);
    if (purchasesLifetime == null) purchasesLifetime = 0;
    
    store.put(purchaseKey, purchasesLifetime + quantity);
    products.add(product);
  }
}

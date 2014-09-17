package nile.tasks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.Entry;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.apache.samza.task.WindowableTask;
import nile.events.AbandonedCartEvent;
import nile.events.AbandonedCartEvent.DirectObject.Cart;

public class AbandonedCartsStreamTask
  implements StreamTask, InitableTask, WindowableTask {

  private KeyValueStore<String, String> store;

  public void init(Config config, TaskContext context) {
    this.store = (KeyValueStore<String, String>)
      context.getStore("nile-abandonedcarts");
  }

  @SuppressWarnings("unchecked")
  @Override
  public void process(IncomingMessageEnvelope envelope,
    MessageCollector collector, TaskCoordinator coordinator) {

    Map<String, Object> event = (Map<String, Object>) envelope.getMessage();
    String verb = (String) event.get("verb");
    String cookieId = (String) ((Map<String, Object>)
      event.get("subject")).get("cookieId");
    
    if (verb.equals("add")) {                                            // a
      String timestamp = (String) ((Map<String, Object>)
        event.get("context")).get("timestamp");

      Map<String, Object> item = (Map<String, Object>)
        ((Map<String, Object>) event.get("directObject")).get("item");
      Cart cart = new Cart(store.get(asCartKey(cookieId)));
      cart.addItem(item);

      store.put(asTimestampKey(cookieId), timestamp);
      store.put(asCartKey(cookieId), cart.asJson());
    
    } else if (verb.equals("place")) {                                   // b
      resetShopper(cookieId);
    }
  }

  @Override
  public void window(MessageCollector collector,
    TaskCoordinator coordinator) {

    KeyValueIterator<String, String> entries = store.all();
    while (entries.hasNext()) {                                          // c
      Entry<String, String> entry = entries.next();
      String key = entry.getKey();
      String value = entry.getValue();
      if (isTimestampKey(key) && Cart.isAbandoned(value)) {
        String cookieId = extractCookieId(key);
        String cart = store.get(asCartKey(cookieId));
        
        AbandonedCartEvent event = new AbandonedCartEvent(cookieId, cart);
        collector.send(new OutgoingMessageEnvelope(
          new SystemStream("kafka", "nile-derivedevents"), event));
        
        resetShopper(cookieId);
      }
    }
  }

  private static String asTimestampKey(String cookieId) {
    return cookieId + "-ts";
  }

  private static boolean isTimestampKey(String key) {
    return key.endsWith("-ts");
  }

  private static String extractCookieId(String key) {
    return key.substring(0, key.lastIndexOf('-'));
  }

  private static String asCartKey(String cookieId) {
    return cookieId + "-cart";
  }

  private void resetShopper(String cookieId) {
    store.delete(asTimestampKey(cookieId));
    store.delete(asCartKey(cookieId));    
  }
}

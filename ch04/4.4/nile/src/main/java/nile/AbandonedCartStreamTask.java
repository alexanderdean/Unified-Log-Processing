package nile;

import java.util.*;

import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.*;
import org.apache.samza.system.*;
import org.apache.samza.task.*;

import nile.entities.*;

public class AbandonedCartStreamTask
  implements StreamTask, InitableTask, WindowableTask {

  private KeyValueStore<String, String> store;

  public void init(Config config, TaskContext context) {
    this.store = (KeyValueStore<String, String>)
      context.getStore("abandoned-cart-detector");
  }

  @SuppressWarnings("unchecked")
  @Override
  public void process(IncomingMessageEnvelope envelope,
    MessageCollector collector, TaskCoordinator coordinator) {

    String rawEvent = (String) envelope.getMessage();
    Event e = Event.fromJson(raw);

    if (e.event.equals("SHOPPER_ADDED_ITEM_TO_CART")) {

      String rawCart = store.get(asCartKey(e.shopper));
      Cart c = Cart.fromJson(rawCart);

      for (Item i : e.items) {
        c.addItem(i);
      }

      store.put(asTimestampKey(e.shopper), timestamp);
      store.put(asCartKey(e.shopper), c.asJson());
    
    } else if (e.event.equals("SHOPPER_PLACED_ORDER")) {
      resetShopper(e.shopper);
    }
  }

  @Override
  public void window(MessageCollector collector,
    TaskCoordinator coordinator) {

    KeyValueIterator<String, String> entries = store.all();
    while (entries.hasNext()) {                                        // c
      Entry<String, String> entry = entries.next();
      String key = entry.getKey();
      String value = entry.getValue();
      if (isTimestampKey(key) && Cart.isAbandoned(value)) {            // d
        String shopper = extractShopper(key);
        String cart = store.get(asCartKey(shopper));
        
        AbandonedCartEvent event =
          new AbandonedCartEvent(shopper, cart);
        collector.send(new OutgoingMessageEnvelope(
          new SystemStream("kafka", "nile-derivedevents"), event));    // e
        
        resetShopper(shopper);
      }
    }
  }

  private static String asTimestampKey(Shopper shopper) {
    return shopper.id + "-ts";
  }

  private static boolean isTimestampKey(String key) {
    return key.endsWith("-ts");
  }

  private static String extractShopper(String key) {                   // f
    return key.substring(0, key.lastIndexOf('-'));
  }

  private static String asCartKey(Shopper shopper) {
    return shopper.id + "-cart";
  }

  private void resetShopper(Shopper shopper) {
    store.delete(asTimestampKey(shopper.id));
    store.delete(asCartKey(shopper.id));
  }
}

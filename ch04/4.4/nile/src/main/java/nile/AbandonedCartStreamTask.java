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
    MessageCollector mc, TaskCoordinator tc) {

    String rawEvent = (String) envelope.getMessage();
    Event e = Event.fromJson(raw);

    if (e.event.equals("SHOPPER_ADDED_ITEM_TO_CART")) {
      String rawCart = store.get(asCartKey(e.shopper));
      Cart c = Cart.fromJson(rawCart);

      for (Item i : e.items) c.addItem(i);

      store.put(asTimestampKey(e.shopper),
        (LocalDateTime)IJsonable.asJson(timestamp));
      store.put(asCartKey(e.shopper), c.asJson());
    } else if (e.event.equals("SHOPPER_PLACED_ORDER")) {
      resetShopper(e.shopper);
    }
  }

  @Override
  public void window(MessageCollector collector, TaskCoordinator tc) {

    KeyValueIterator<String, String> entries = store.all();
    while (entries.hasNext()) {
      Entry<String, String> entry = entries.next();
      String key = entry.getKey();
      if (!isTimestampKey(key)) continue;

      LocalDateTime timestamp = IJsonable.fromJson(entry.getValue(),
        LocalDateTime.class);
      if (Cart.isAbandoned(timestamp)) {
        String shopperId = extractShopperId(key);
        String rawCart = store.get(asCartKey(shopperId));

        Cart cart = Cart.fromJson(rawCart); 
        Shopper shopper = new Shopper(shopperId);
        Event event = new Event(shopper, "SHOPPER_ABANDONED_CART",
          cart.items);
        collector.send(new OutgoingMessageEnvelope(
          new SystemStream("kafka", "derived-events"), event.asJson()));
        
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

  private static String extractShopperId(String key) {
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

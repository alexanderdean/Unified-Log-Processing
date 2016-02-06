package nile;

import java.time.Instant;
import java.util.Optional;

import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.*;
import org.apache.samza.system.*;
import org.apache.samza.task.*;

import nile.entities.*;

public class AbandonedCartStreamTask
  implements StreamTask, InitableTask, WindowableTask {

  private KeyValueStore<String, String> store;

  public void init(Config config, TaskContext context) {          // a
    this.store = (KeyValueStore<String, String>)
      context.getStore("abandoned-cart-detector");
  }

  @Override public void process(IncomingMessageEnvelope envelope,
    MessageCollector mc, TaskCoordinator tc) {                    // b

    String rawEvent = (String) envelope.getMessage();

	try {

      Event e = Jsonable.fromJson(rawEvent, Event.class);

      if (e.event.equals("SHOPPER_ADDED_ITEM_TO_CART")) {           // c
        Optional<String> rawCart = Optional.ofNullable(
          store.get(asCartKey(e.shopper.id)));
        Cart c = rawCart
          .map(rc -> Jsonable.fromJson(rc, Cart.class))
          .orElse(new Cart());

        for (Item i : e.items) c.addItem(i);

        store.put(asTimestampKey(e.shopper.id),
          Jsonable.asJson(e.timestamp));
        store.put(asCartKey(e.shopper.id), c.asJson());
      } else if (e.event.equals("SHOPPER_PLACED_ORDER")) {          // d
        resetShopper(e.shopper.id);
      }

	} catch (Exception e) {
      //mc.send(new OutgoingMessageEnvelope(
      //  new SystemStream("kafka", "bad-events"), rawEvent));
	}

  }

  @Override public void window(MessageCollector coll,
    TaskCoordinator tc) {                                         // e

    KeyValueIterator<String, String> entries = store.all();
    while (entries.hasNext()) {                                   // f
      Entry<String, String> entry = entries.next();
      String key = entry.getKey();
      if (!isTimestampKey(key)) continue;                         // f

      Instant timestamp = Jsonable.fromJson(
        entry.getValue(), Instant.class);
      if (Cart.isAbandoned(timestamp)) {                          // g
        String shopperId = extractShopperId(key);
        String rawCart = store.get(asCartKey(shopperId));

        Cart cart = Jsonable.fromJson(rawCart, Cart.class);
        Event event = new Event(new Shopper(shopperId),
          "SHOPPER_ABANDONED_CART", cart.items);                  // h
        coll.send(new OutgoingMessageEnvelope(
          new SystemStream("kafka", "derived-events"), event.asJson()));
        
        resetShopper(shopperId);
      }
    }
  }

  private static String asTimestampKey(String shopperId) {
    return shopperId + "-ts";
  }

  private static boolean isTimestampKey(String key) {
    return key.endsWith("-ts");
  }

  private static String extractShopperId(String key) {            // i
    return key.substring(0, key.lastIndexOf('-'));
  }

  private static String asCartKey(String shopperId) {
    return shopperId + "-cart";
  }

  private void resetShopper(String shopperId) {
    store.delete(asTimestampKey(shopperId));
    store.delete(asCartKey(shopperId));
  }
}

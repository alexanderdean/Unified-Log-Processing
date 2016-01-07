package nile.events;

import java.io.IOException;

import java.util.*;

import org.joda.time.*;

import org.codehaus.jackson.type.TypeReference;

import nile.events.Event;

public class AbandonedCartEvent extends Event {
  public final DirectObject directObject;

  public AbandonedCartEvent(String shopper, String cart) {
    super(shopper, "abandon");
    this.directObject = new DirectObject(cart);
  }

  public static final class DirectObject {
    public final Cart cart;

    public DirectObject(String cart) {
      this.cart = new Cart(cart);
    }

    public static final class Cart {

      private static final int ABANDONED_AFTER_SECS = 1800;            // a

      public List<Map<String, Object>> items =
        new ArrayList<Map<String, Object>>();

      public Cart(String json) {
        if (json != null) {
          try {
            this.items = MAPPER.readValue(json,
              new TypeReference<List<Map<String, Object>>>() {});
          } catch (IOException ioe) {
            throw new RuntimeException("Problem parsing JSON cart", ioe);
          }
        }
      }

      public void addItem(Map<String, Object> item) {                  // b
        this.items.add(item);
      }

      public String asJson() {                                         // c
        try {
          return MAPPER.writeValueAsString(this.items);
        } catch (IOException ioe) {
          throw new RuntimeException("Problem writing JSON cart", ioe);
        }
      }

      public static boolean isAbandoned(String timestamp) {            // d
        DateTime ts = EVENT_DTF.parseDateTime(timestamp);
        DateTime cutoff = new DateTime(DateTimeZone.UTC)
          .minusSeconds(ABANDONED_AFTER_SECS);
        return ts.isBefore(cutoff);
      }
    }
  }
}

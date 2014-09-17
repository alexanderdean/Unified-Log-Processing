package nile.events;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.codehaus.jackson.type.TypeReference;
import nile.events.Event;

public class AbandonedCartEvent extends Event {
  public final DirectObject directObject;

  public AbandonedCartEvent(String cookieId, String cart) {
    super(cookieId, "abandon");
    this.directObject = new DirectObject(cart);
  }

  public static final class DirectObject {
    public final Cart cart;

    public DirectObject(String cart) {
      this.cart = new Cart(cart);
    }

    public static final class Cart {

      private static final int ABANDONED_AFTER_SECS = 2700;              // a

      public List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();

      public Cart(String json) {
        if (json != null) {
          try {
            this.items = MAPPER.readValue(json,
              new TypeReference<List<Map<String, Object>>>() {});
          } catch (IOException ioe) {
            throw new RuntimeException("Problem reading cart from JSON", ioe);
          }
        }
      }

      public void addItem(Map<String, Object> item) {                    // b
        this.items.add(item);
      }

      public String asJson() {                                           // c
        try {
          return MAPPER.writeValueAsString(this.items);
        } catch (IOException ioe) {
          throw new RuntimeException("Problem converting cart to JSON", ioe);
        }
      }

      public static boolean isAbandoned(String timestamp) {              // d
        DateTime ts = EVENT_DTF.parseDateTime(timestamp);
        DateTime cutoff = new DateTime(DateTimeZone.UTC)
          .minusSeconds(ABANDONED_AFTER_SECS);
        return ts.isBefore(cutoff);
      }
    }
  }

}

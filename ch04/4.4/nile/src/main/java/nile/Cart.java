package nile;

import java.time.*;
// import java.util.*;

public class Cart implements IJsonable {

  public List<Item> items;

  private static final int ABANDONED_AFTER_SECS = 1800;            // a

  public Cart(List<Item> items) {
    this.items = items;
  }

  public void addItem(Item item) {                  // b
    this.items.add(item);
  }

  public static boolean isAbandoned(LocalDateTime timestamp) {            // d
    DateTime cutoff = LocalDateTime.now(ZoneOffset.UTC)
      .minusSeconds(ABANDONED_AFTER_SECS);
    return timestamp.isBefore(cutoff);
  }
}

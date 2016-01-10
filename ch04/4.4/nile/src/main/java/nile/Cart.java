package nile;

import java.time.*;
// import java.util.*;

public class Cart implements IJsonable {

  public List<Item> items;

  private static final int ABANDONED_AFTER_SECS = 1800;

  public Cart(List<Item> items) {
    this.items = items;
  }

  public void addItem(Item item) {
    this.items.add(item);
  }

  public static boolean isAbandoned(LocalDateTime timestamp) {
    DateTime cutoff = LocalDateTime.now(ZoneOffset.UTC)
      .minusSeconds(ABANDONED_AFTER_SECS);
    return timestamp.isBefore(cutoff);
  }

  public static Cart fromJson(String json) {
    return IJsonable.fromJson(json, Cart.class);
  }
}

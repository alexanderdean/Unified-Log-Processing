package nile;

import java.time.*;
import java.util.List;

import nile.entities.*;

public class Cart implements Jsonable {

  public List<Item> items;

  private static final int ABANDONED_AFTER_SECS = 1800;

  public Cart(List<Item> items) {
    this.items = items;
  }

  public void addItem(Item item) {
    this.items.add(item);
  }

  public static Boolean isAbandoned(LocalDateTime timestamp) {
    LocalDateTime cutoff = LocalDateTime.now(ZoneOffset.UTC)
      .minusSeconds(ABANDONED_AFTER_SECS);
    return timestamp.isBefore(cutoff);
  }
}

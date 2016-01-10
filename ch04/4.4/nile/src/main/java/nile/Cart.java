package nile;

import java.time.*;
import java.util.List;

import nile.entities.*;

public class Cart implements Jsonable {

  public List<Item> items;

  private static final int ABANDONED_AFTER_SECS = 1800;       // a

  public Cart(List<Item> items) {
    this.items = items;
  }

  public void addItem(Item item) {                            // b
    this.items.add(item);
  }

  public static Boolean isAbandoned(Instant timestamp) {      // c
    Instant cutoff = Instant.now()
      .minusSeconds(ABANDONED_AFTER_SECS);
    return timestamp.isBefore(cutoff);
  }
}

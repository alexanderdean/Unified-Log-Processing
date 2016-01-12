package nile;

import java.time.*;
import java.util.*;

import nile.entities.*;

public class Cart implements Jsonable {

  public List<Item> items = new ArrayList<Item>();

  private static final int ABANDONED_AFTER_SECS = 1800;       // a

  public void addItem(Item item) {                            // b
    this.items.add(item);
  }

  public static Boolean isAbandoned(Instant timestamp) {      // c
    Instant cutoff = Instant.now()
      .minusSeconds(ABANDONED_AFTER_SECS);
    return timestamp.isBefore(cutoff);
  }
}

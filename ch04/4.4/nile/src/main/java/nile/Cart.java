package nile;

import java.io.IOException;

import java.util.*;

import org.joda.time.*;

import org.codehaus.jackson.type.TypeReference;

public class Cart implements IJsonable {

  public Item[] items;

  private static final int ABANDONED_AFTER_SECS = 1800;            // a

  public Cart(Item[] items) {
    this.items = items;
  }

  public void addItem(Map<String, Object> item) {                  // b
    this.items.add(item);
  }

  public static boolean isAbandoned(String timestamp) {            // d
    DateTime ts = EVENT_DTF.parseDateTime(timestamp);
    DateTime cutoff = new DateTime(DateTimeZone.UTC)
      .minusSeconds(ABANDONED_AFTER_SECS);
    return ts.isBefore(cutoff);
  }

}

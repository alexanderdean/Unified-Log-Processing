package nile;

import org.joda.time.*;
import org.joda.time.format.*;

import org.codehaus.jackson.map.ObjectMapper;

import nile.entities.*;

public class Event implements IJsonable {

  public Shopper shopper;
  public String event;
  public Item[] items;
  public Optional<Order> order;
  public DateTime timestamp;

  public Event(Shopper shopper, String event, Item[] items,
    Optional<Order> order, DateTime timestamp) {

    this.shopper = shopper;
    this.event = event;
    this.items = items;
    this.order = order;
    this.timestamp = timestamp;
  }
}

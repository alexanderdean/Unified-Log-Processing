package nile;

import java.time.*;
import java.util.Optional;

import nile.entities.*;

public class Event implements IJsonable {

  public Shopper shopper;
  public String event;
  public List<Item> items;
  public Optional<Order> order;
  public LocalDateTime timestamp;

  public Event(Shopper shopper, String event, List<Item> items,
    Optional<Order> order, LocalDateTime timestamp) {

    this.shopper = shopper;
    this.event = event;
    this.items = items;
    this.order = order;
    this.timestamp = timestamp;
  }

  public Event(Shopper shopper, String event, List<Item> items) {
    this(shopper, event, items,
      Optional.empty(), LocalDateTime.now(ZoneOffset.UTC));
  }
}

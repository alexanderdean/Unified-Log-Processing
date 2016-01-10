package nile;

import java.time.*;
import java.util.*;

import nile.entities.*;

public class Event implements Jsonable {

  public Shopper shopper;
  public String event;
  public List<Item> items;
  public Optional<Order> order;
  public Instant timestamp;

  public Event() {
    this.shopper = null;
    this.event = null;
    this.items = Collections.emptyList();
    this.order = Optional.empty();
    this.timestamp = null;
  }

  public Event(Shopper shopper, String event, List<Item> items) {
    this.shopper = shopper;
    this.event = event;
    this.items = items;
    this.order = Optional.empty();
    this.timestamp = Instant.now();
  }
}

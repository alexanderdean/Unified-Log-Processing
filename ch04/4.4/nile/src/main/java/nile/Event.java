package nile;

import org.joda.time.*;
import org.joda.time.format.*;

import org.codehaus.jackson.map.ObjectMapper;

public class Event {

  public Shopper shopper;
  public String event;
  public Item[] items;
  public Optional<Order> order;
  public DateTime timestamp;

  protected static final ObjectMapper MAPPER = new ObjectMapper();
  protected static final DateTimeFormatter EVENT_DTF = DateTimeFormat
    .forPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(DateTimeZone.UTC);

  public Event(Shopper shopper, String event, Item[] items,
    Optional<Order> order, DateTime timestamp) {

    this.shopper = shopper;
    this.event = event;
    this.items = items;
    this.order = order;
    this.timestamp = timestamp;
  }

  public static class Shopper {
    public final String id;
    public final String name;
    public final String ipAddress;

    public Shopper(String id, String name, String ipAddress) {
      this.id = id;
      this.name = name;
      this.ipAddress = ipAddress;
    }
  }

  public static class Product {
    public final String sku;
    public final String name;

    public Product(sku, name) {
      this.sku = sku;
      this.name = name;
    }
  }

  public static class Item {
    public final Product product;
    public final Integer quantity;

    public Shopper(Product product, Integer quantity) {
      this.product = product;
      this.quantity = quantity;
    }
  }

  public static class Order {
    public final String id;
    public final Double value;

    public Order(String id, Double value) {
      this.id = id;
      this.value = value;
    }
  }
}

package nile.entities;

public static class Order {

  public final String id;
  public final Double value;

  public Order(String id, Double value) {
    this.id = id;
    this.value = value;
  }
}

package nile.entities;

public static class Item {

  public final Product product;
  public final Integer quantity;

  public Shopper(Product product, Integer quantity) {
    this.product = product;
    this.quantity = quantity;
  }
}
 
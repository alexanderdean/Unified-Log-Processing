package nile.entities;

public class Item {

  public final Product product;
  public final Integer quantity;

  public Item(Product product, Integer quantity) {
    this.product = product;
    this.quantity = quantity;
  }
}
 
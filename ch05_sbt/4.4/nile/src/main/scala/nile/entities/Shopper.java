package nile.entities;

import java.util.Optional;

public class Shopper {

  public final String id;
  public final Optional<String> name;                         // a
  public final Optional<String> ipAddress;

  public Shopper() {                                          // b
    this.id = null;
    this.name = Optional.empty();
    this.ipAddress = Optional.empty();
  }

  public Shopper(String id) {                                 // c
    this.id = id;
    this.name = Optional.empty();
    this.ipAddress = Optional.empty();
  }
}

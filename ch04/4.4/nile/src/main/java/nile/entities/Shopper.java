package nile.entities;

import java.util.Optional;

public class Shopper {

  public final String id;
  public final Optional<String> name;
  public final Optional<String> ipAddress;

  public Shopper(String id, Optional<String> name, Optional<String> ipAddress) {
    this.id = id;
    this.name = name;
    this.ipAddress = ipAddress;
  }

  public Shopper(String id) {
    this(id, Optional.empty(), Optional.empty());
  }
}

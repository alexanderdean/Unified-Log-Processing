package nile.entities;

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

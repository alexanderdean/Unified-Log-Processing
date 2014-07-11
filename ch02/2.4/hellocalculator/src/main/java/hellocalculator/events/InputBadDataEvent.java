package hellocalculator.events;

public class InputBadDataEvent extends Event {

  public final DirectObject directObject;

  public InputBadDataEvent(String[] args, String reason) {
    super("input");
    this.directObject = new DirectObject(args, reason);
  }

  public static class DirectObject {
    public final BadData badData;

    public DirectObject(String[] args, String reason) {
      this.badData = new BadData(args, reason);
    }

    public static class BadData {
      public final String[] args;
      public final String reason;

      public BadData(String[] args, String reason) {
        this.args = args;
        this.reason = reason;
      }
    }
  }
}

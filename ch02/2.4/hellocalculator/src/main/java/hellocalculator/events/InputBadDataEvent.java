package hellocalculator.events;

public class InputBadDataEvent extends Event {

  private final DirectObject directObject;

  public InputBadDataEvent(String[] args, String reason) {
    super("input");
    this.directObject = new DirectObject(args, reason);
  }

  private class DirectObject {
    private final BadData badData;

    public DirectObject(String[] args, String reason) {
      this.badData = new BadData(args, reason);
    }

    private class BadData {
      private final String[] args;
      private final String reason;

      public BadData(String[] args, String reason) {
        this.args = args;
        this.reason = reason;
      }
    }
  }
}

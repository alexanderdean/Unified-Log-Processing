package events.ch02.events;

import com.google.gson.Gson;

public class InputBadDataEvent extends Event {

  private final DirectObject directObject;

  public InputBadDataEvent(String hostname, String[] args, String reason) {
    super(hostname, "input");
    this.directObject = new DirectObject(args, reason);
  }

  private class DirectObject {
    private final BadData badData;

    public DirectObject(String[] args, String reason) {
      this.badData = new BadData(args, reason);
    }
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

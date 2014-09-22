package nile.events;

import org.joda.time.DateTime;                                         // a
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.codehaus.jackson.map.ObjectMapper;

public abstract class Event {                                          // b

  public Subject subject;
  public String verb;
  public Context context;

  protected static final ObjectMapper MAPPER = new ObjectMapper();
  protected static final DateTimeFormatter EVENT_DTF = DateTimeFormat
    .forPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(DateTimeZone.UTC);

  public Event(String shopper, String verb) {
    this.subject = new Subject(shopper);
    this.verb = verb;
    this.context = new Context();
  }

  public static class Subject {
    public final String shopper;                                      // c
    
    public Subject() {
      this.shopper = null;
    }

    public Subject(String shopper) {
      this.shopper = shopper;
    }
  }

  public static class Context {
    public final String timestamp;

    public Context() {
      this.timestamp = EVENT_DTF.print(
        new DateTime(DateTimeZone.UTC));                               // d
    }
  }
}


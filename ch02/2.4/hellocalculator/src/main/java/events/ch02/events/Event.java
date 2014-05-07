package events.ch02.events;

import java.util.Date;
import java.text.SimpleDateFormat;
import com.google.gson.Gson;

public abstract class Event {

  private final Subject subject;
  private final String verb;
  private final String timestamp;

  public Event(String hostname, String verb) {
    this.subject = new Subject(hostname);
    this.verb = verb;
    this.timestamp = asJsonDatetime(new Date());
  }

  public String asJson() {
    return new Gson().toJson(this);
  }

  private String asJsonDatetime(Date dt) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:MM:ss");
    return sdf.format(date);
  }

  private class Subject {
    private final String hostname;
    
    public Subject(String hostname) {
      this.hostname = hostname;
    }
  }
}

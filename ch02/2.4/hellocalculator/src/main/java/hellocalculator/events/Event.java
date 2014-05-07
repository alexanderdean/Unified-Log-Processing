package hellocalculator.events;

import java.util.Date;
import java.text.SimpleDateFormat;

import com.google.gson.Gson;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

public abstract class Event {

  private final Subject subject;
  private final String verb;
  private final String timestamp;

  private static final String STREAM = "calc_events";

  public Event(String hostname, String verb) {
    this.subject = new Subject(hostname);
    this.verb = verb;
    this.timestamp = asJsonDatetime(new Date());
  }

  public void sendTo(Producer<String, String> producer) {
    KeyedMessage<String, String> data = new KeyedMessage<String, String>(
      STREAM, this.subject.hostname, this.toJson());
    producer.send(data);
  }

  private String toJson() {
    return new Gson().toJson(this);
  }

  private String asJsonDatetime(Date dt) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:MM:ss");
    return sdf.format(date);
  }

  private class Subject {
    public final String hostname;
    
    public Subject(String hostname) {
      this.hostname = hostname;
    }
  }

}

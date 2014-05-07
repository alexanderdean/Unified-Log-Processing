package hellocalculator.events;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.net.InetAddress;

import com.google.gson.Gson;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

public abstract class Event {

  private final Subject subject;
  private final String verb;
  private final String timestamp;

  private static final String STREAM = "calc_events";

  public Event(String hostname, String verb) {
    this.subject = new Subject(getHostname());
    this.verb = verb;
    this.timestamp = getTimestamp();
  }

  public void sendTo(Producer<String, String> producer) {
    String key = this.subject.hostname;
    String message = new Gson().toJson(this);
    KeyedMessage<String, String> data = new KeyedMessage<String, String>(
      STREAM, key, message);
    producer.send(data);
  }

  private String getTimestamp() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:MM:ss");
    return sdf.format(new Date());
  }

  private String getHostname() {
    try {
      return InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException uhe) {
      return "unknown";
    }
  }

  private class Subject {
    public final String hostname;
    
    public Subject(String hostname) {
      this.hostname = hostname;
    }
  }
}

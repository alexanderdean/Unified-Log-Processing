package hellocalculator.events;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.gson.Gson;

import kafka.producer.ProducerConfig;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

public abstract class Event {

  private final Subject subject;
  private final String verb;
  private final String timestamp;

  private static final String STREAM = "calc_events";

  public Event(String verb) {
    this.subject = new Subject(getHostname());
    this.verb = verb;
    this.timestamp = getTimestamp();
  }

  public static Producer<String, String> createProducer(String brokers) {
    Properties props = new Properties();
    props.put("metadata.broker.list", brokers);
    props.put("serializer.class", "kafka.serializer.StringEncoder");
    props.put("request.required.acks", "1");
    ProducerConfig config = new ProducerConfig(props);
    return new Producer<String, String>(config);
  }

  public void sendTo(Producer<String, String> producer) {
    String key = this.subject.hostname;
    String message = this.asJson();
    KeyedMessage<String, String> data = new KeyedMessage<String, String>(
      STREAM, key, message);
    producer.send(data);
  }

  String asJson() {
    return new Gson().toJson(this);
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

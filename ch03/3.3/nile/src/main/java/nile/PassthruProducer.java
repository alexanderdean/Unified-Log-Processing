package nile;

import org.apache.kafka.clients.producer.*;

public class PassthruProducer implements IProducer {

  private final KafkaProducer<String, String> producer;
  private final String topic;

  public PassthruProducer(String servers, String topic) {
    this.producer = new KafkaProducer(
      IProducer.createConfig(servers));                             // a
    this.topic = topic;
  }

  public void process(String message) {
    IProducer.write(this.producer, this.topic, message);            // b
  }
}
package nile;

import org.apache.kafka.clients.producer.*;

public class PassthruProducer implements INileProducer {

  private final KafkaProducer<byte[], String> producer;
  private final String topic;

  public PassthruProducer(String brokers, String topic) {
    this.producer = new KafkaProducer(
      INileProducer.createConfig(brokers));
    this.topic = topic;
  }

  public void process(byte[] message) {
    ProducerRecord<byte[], String> pr = new ProducerRecord<byte[], String>(
      this.topic, new String(message));
    this.producer.send(pr);
  }
}
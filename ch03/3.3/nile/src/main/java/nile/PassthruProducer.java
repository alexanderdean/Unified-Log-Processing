package nile;

import java.util.Properties;

import kafka.producer.*;
import kafka.javaapi.producer.Producer;

public class PassthruProducer implements INileProducer {

  private final Producer<byte[], byte[]> producer;
  private final String topic;

  public PassthruProducer(String brokers, String topic) {
    this.producer = new Producer<byte[], byte[]>(
      createConfig(brokers));
    this.topic = topic;
  }

  public void write(byte[] message) {
    KeyedMessage<byte[], byte[]> km = new KeyedMessage<byte[], byte[]>(
      this.topic, message);
    this.producer.send(km);
  }

  private static ProducerConfig createConfig(String brokers) {
    Properties props = new Properties();
    props.put("metadata.broker.list", brokers);
    props.put("request.required.acks", "1");
    return new ProducerConfig(props);
  }
}
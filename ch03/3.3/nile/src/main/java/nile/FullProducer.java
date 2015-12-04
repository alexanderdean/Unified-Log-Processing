package nile;

import java.util.Properties;

import kafka.producer.*;
import kafka.javaapi.producer.Producer;

/*

public class NileProducer {

  private final Producer<byte[], byte[]> producer;
  private final String goodTopic;
  private final String badTopic;
  private final LookupService maxmind;

  public NileProducer(String brokers, String goodTopic,
    String badTopic, LookupService maxmind) {
    this.producer = new Producer<byte[], String>(
      createConfig(brokers));
    this.goodTopic = goodTopic;
    this.badTopic = badTopic;
    this.maxmind = maxmind;
  }

  public void write(byte[] message) {

    // First we validate the message

    // Now we fetch the 


    KeyedMessage<byte[], String> km = new KeyedMessage<byte[], String>(
      this.goodTopic, message);
    this.producer.send(km);
  }

  private static ProducerConfig createConfig(String brokers) {
    Properties props = new Properties();
    props.put("metadata.broker.list", brokers);
    props.put("serializer.class", "kafka.serializer.StringEncoder");
    props.put("request.required.acks", "1");
    return new ProducerConfig(props);
  }
}

*/
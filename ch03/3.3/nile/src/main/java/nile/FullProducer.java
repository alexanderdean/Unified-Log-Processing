package nile;

import java.io.IOException;
import java.util.Properties;

import kafka.producer.*;
import kafka.javaapi.producer.Producer;

import com.fasterxml.jackson.databind.*;
import com.github.fge.jackson.JacksonUtils;

import com.maxmind.geoip.LookupService;

public class FullProducer implements INileProducer {

  private final Producer<byte[], String> producer;
  private final String goodTopic;
  private final String badTopic;
  private final LookupService maxmind;

  protected static final ObjectMapper MAPPER = JacksonUtils.newMapper();

  public FullProducer(String brokers, String goodTopic,
    String badTopic, LookupService maxmind) {
    this.producer = new Producer<byte[], String>(
      createConfig(brokers));
    this.goodTopic = goodTopic;
    this.badTopic = badTopic;
    this.maxmind = maxmind;
  }

  public void process(byte[] message) {

    // Parse the message and check we can retrieve a 
    try {
      JsonNode node = MAPPER.readTree(message);
      JsonNode ipAddress = node.path("customer").path("ipAddress");
      if (ipAddress.isMissingNode()) {
        write(this.badTopic, "customer.ipAddress property is missing");
      } else {
        String ip = ipAddress.getTextValue();
      }
    } catch (IOException e) {
      // root.with("address").put("zip", 98040);
    }
  }

  private void write(String topic, String message) {
    KeyedMessage<byte[], String> km = new KeyedMessage<byte[], String>(
      this.goodTopic, new String(message));
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
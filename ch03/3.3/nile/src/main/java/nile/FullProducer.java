package nile;

import java.util.Properties;

import kafka.producer.*;
import kafka.javaapi.producer.Producer;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JacksonUtils;

import com.maxmind.geoip.*;

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

    try {
      JsonNode root = MAPPER.readTree(message);
      JsonNode ipAddressNode = root.path("shopper").path("ipAddress");
      if (ipAddressNode.isMissingNode()) {
        write(this.badTopic, "{\"error\": \"shopper.ipAddress missing\"}");
      } else {
        String ipAddress = ipAddressNode.textValue();
        Location country = maxmind.getLocation(ipAddress);
        ((ObjectNode)root).with("shopper").put("country", country.countryName);
        write(this.goodTopic, MAPPER.writeValueAsString(root));
      }
    } catch (Exception e) {
      write(this.badTopic, "{\"error\": \"" + e.getClass().getSimpleName() +
        " processing JSON: " + e.getMessage() + "\"}");
    }
  }

  private void write(String topic, String message) {
    KeyedMessage<byte[], String> km = new KeyedMessage<byte[], String>(
      topic, message);
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
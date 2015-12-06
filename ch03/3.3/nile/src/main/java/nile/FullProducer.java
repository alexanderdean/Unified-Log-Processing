package nile;

import org.apache.kafka.clients.producer.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.maxmind.geoip.*;

public class FullProducer implements INileProducer {

  private final KafkaProducer<byte[], String> producer;
  private final String goodTopic;
  private final String badTopic;
  private final LookupService maxmind;

  protected static final ObjectMapper MAPPER = new ObjectMapper();

  public FullProducer(String brokers, String goodTopic,
    String badTopic, LookupService maxmind) {
    this.producer = new KafkaProducer(
      INileProducer.createConfig(brokers));
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
    ProducerRecord<byte[], String> pr = new ProducerRecord<byte[], String>(
      topic, message);
    this.producer.send(pr);
  }
}
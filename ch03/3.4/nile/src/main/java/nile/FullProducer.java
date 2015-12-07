package nile;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.kafka.clients.producer.*;

import com.maxmind.geoip.*;

public class FullProducer implements IProducer {

  private final KafkaProducer<String, String> producer;
  private final String goodTopic;
  private final String badTopic;
  private final LookupService maxmind;

  protected static final ObjectMapper MAPPER = new ObjectMapper();

  public FullProducer(String servers, String goodTopic,
    String badTopic, LookupService maxmind) {
    this.producer = new KafkaProducer(
      IProducer.createConfig(servers));
    this.goodTopic = goodTopic;
    this.badTopic = badTopic;
    this.maxmind = maxmind;
  }

  public void process(String message) {

    try {
      JsonNode root = MAPPER.readTree(message);
      JsonNode ipAddressNode = root.path("shopper").path("ipAddress");
      if (ipAddressNode.isMissingNode()) {
        IProducer.write(this.producer, this.badTopic,
          "{\"error\": \"shopper.ipAddress missing\"}");
      } else {
        String ipAddress = ipAddressNode.textValue();
        Location country = maxmind.getLocation(ipAddress);
        ((ObjectNode)root).with("shopper").put("country", country.countryName);
        IProducer.write(this.producer, this.goodTopic,
          MAPPER.writeValueAsString(root));
      }
    } catch (Exception e) {
      IProducer.write(this.producer, this.badTopic, "{\"error\": \"" +
        e.getClass().getSimpleName() + " thrown: " + e.getMessage() + "\"}");
    }
  }
}
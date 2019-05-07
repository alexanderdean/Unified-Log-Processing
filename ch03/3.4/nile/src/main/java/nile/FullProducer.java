package nile;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.InetAddress;
import org.apache.kafka.clients.producer.*;
import com.maxmind.geoip2.*;
import com.maxmind.geoip2.model.*;

public class FullProducer implements IProducer {

  private final KafkaProducer<String, String> producer;
  private final String goodTopic;
  private final String badTopic;
  private final DatabaseReader maxmind;

  protected static final ObjectMapper MAPPER = new ObjectMapper();

  public FullProducer(String servers, String goodTopic,
    String badTopic, DatabaseReader maxmind) {                          // a
    this.producer = new KafkaProducer(
      IProducer.createConfig(servers));
    this.goodTopic = goodTopic;
    this.badTopic = badTopic;
    this.maxmind = maxmind;
  }

  public void process(String message) {

    try {
      JsonNode root = MAPPER.readTree(message);
      JsonNode ipNode = root.path("shopper").path("ipAddress"); // b
      if (ipNode.isMissingNode()) {
        IProducer.write(this.producer, this.badTopic,
          "{\"error\": \"shopper.ipAddress missing\"}");               // c
      } else {
        InetAddress ip = InetAddress.getByName(ipNode.textValue());
        CityResponse resp = maxmind.city(ip);// d
        ((ObjectNode)root).with("shopper").put(
          "country", resp.getCountry().getName());                            // e
        ((ObjectNode)root).with("shopper").put(
          "city", resp.getCity().getName());                                      // e
        IProducer.write(this.producer, this.goodTopic,
          MAPPER.writeValueAsString(root));                            // f
      }
    } catch (Exception e) {
      IProducer.write(this.producer, this.badTopic, "{\"error\": \"" +
        e.getClass().getSimpleName() + ": " + e.getMessage() + "\"}"); // c
    }
  }
}
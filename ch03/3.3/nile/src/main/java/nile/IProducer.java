package nile;

import java.util.Properties;

public interface INileProducer {

  public void process(byte[] message);

  public static Properties createConfig(String brokers) {
    Properties props = new Properties();
    props.put("bootstrap.servers", brokers);
    props.put("acks", "all");
    props.put("key.serializer",
      "org.apache.kafka.common.serialization.ByteArraySerializer");
    props.put("value.serializer",
      "org.apache.kafka.common.serialization.StringSerializer");
    return props;
  }
}
package plum;

import java.util.Properties;

import org.apache.kafka.clients.producer.*;

public interface IExecutor {

  public void execute(String command);                              // a

  public static void write(KafkaProducer<String, String> producer,
    String topic, String message) {
    ProducerRecord<String, String> pr = new ProducerRecord(
      topic, message);
    producer.send(pr);
  }

  public static Properties createConfig(String servers) {
    Properties props = new Properties();
    props.put("bootstrap.servers", servers);
    props.put("acks", "all");
    props.put("retries", 0);
    props.put("batch.size", 1000);
    props.put("linger.ms", 1);
    props.put("key.serializer",
      "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer",
      "org.apache.kafka.common.serialization.StringSerializer");
    return props;
  }
}

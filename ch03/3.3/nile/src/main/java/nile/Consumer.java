package nile;

import java.util.*;

import org.apache.kafka.clients.consumer.*;

public class Consumer {

  private final KafkaConsumer<String, String> consumer;             // a
  private final String topic;

  public Consumer(String servers, String groupId, String topic) {
    this.consumer = new KafkaConsumer<String, String>(
      createConfig(servers, groupId));
    this.topic = topic;
  }

  public void run(IProducer producer) {
    this.consumer.subscribe(Arrays.asList(this.topic));             // b
    while (true) {
      ConsumerRecords<String, String> records = consumer.poll(100); // c
      for (ConsumerRecord<String, String> record : records) {
        producer.process(record.value());                           // d
      }
    }
  }

  private static Properties createConfig(
    String servers, String groupId) {

    Properties props = new Properties();
    props.put("bootstrap.servers", servers);
    props.put("group.id", groupId);                                 // e
    props.put("enable.auto.commit", "true");
    props.put("auto.commit.interval.ms", "1000");
    props.put("auto.offset.reset", "earliest");
    props.put("session.timeout.ms", "30000");
    props.put("key.deserializer",
      "org.apache.kafka.common.serialization.StringDeserializer");  // a
    props.put("value.deserializer",
      "org.apache.kafka.common.serialization.StringDeserializer");  // a
    return props;
  }
}
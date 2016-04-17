package plum;                                                       // a

import java.util.*;

import org.apache.kafka.clients.consumer.*;

public class Consumer {

  private final KafkaConsumer<String, String> consumer;
  private final String topic;

  public Consumer(String servers, String groupId, String topic) {
    this.consumer = new KafkaConsumer<String, String>(
      createConfig(servers, groupId));
    this.topic = topic;
  }

  public void run(IExecutor executor) {                             // b
    this.consumer.subscribe(Arrays.asList(this.topic));
    while (true) {
      ConsumerRecords<String, String> records = consumer.poll(100);
      for (ConsumerRecord<String, String> record : records) {
        executor.execute(record.value());                           // b

      }
    }
  }

  private static Properties createConfig(
    String servers, String groupId) {

    Properties props = new Properties();
    props.put("bootstrap.servers", servers);
    props.put("group.id", groupId);
    props.put("enable.auto.commit", "true");
    props.put("auto.commit.interval.ms", "1000");
    props.put("auto.offset.reset", "earliest");
    props.put("session.timeout.ms", "30000");
    props.put("key.deserializer",
      "org.apache.kafka.common.serialization.StringDeserializer");
    props.put("value.deserializer",
      "org.apache.kafka.common.serialization.StringDeserializer");
    return props;
  }
}

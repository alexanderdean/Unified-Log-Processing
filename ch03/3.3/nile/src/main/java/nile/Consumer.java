package nile;

import java.util.*;

import org.apache.kafka.clients.consumer.*;

public class Consumer {

  private final KafkaConsumer<byte[], byte[]> consumer;
  private final String topic;

  public Consumer(String zookeeper, String groupId,
    String topic) {

    this.consumer = new KafkaConsumer<byte[], byte[]>(
      createConfig(zookeeper, groupId));
    this.topic = topic;
  }

  public void run(INileProducer producer) {
    this.consumer.subscribe(Arrays.asList(this.topic));
    while (true) {
      ConsumerRecords<byte[], byte[]> records = consumer.poll(100);
      for (ConsumerRecord<byte[], byte[]> record : records) {
        producer.process(record.value());
      }
    }
  }

  private static Properties createConfig(String zookeeper, String groupId) {
    Properties props = new Properties();
    props.put("bootstrap.servers", zookeeper);
    props.put("group.id", groupId);
    props.put("enable.auto.commit", "true");
    props.put("auto.commit.interval.ms", "1000");
    props.put("session.timeout.ms", "4000");
    props.put("key.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
    props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
    return props;
  }
}
package nile;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class NileConsumerGroup {

  private final ConsumerConnector consumer;
  private final String topic;
  private final int numThreads;

  public NileConsumerGroup(String zookeeper, String groupId,
    String topic, int numThreads) {

    this.consumer = Consumer.createJavaConsumerConnector(
      createConfig(zookeeper, groupId));
    this.topic = topic;
    this.numThreads = numThreads;
  }

  public void run(NileProducer producer) {
    Map<String, Integer> topicMap = new HashMap<String, Integer>();
    topicMap.put(this.topic, new Integer(this.numThreads));
    List<KafkaStream<byte[], byte[]>> streams = consumer
      .createMessageStreams(topicMap)
      .get(this.topic);
 
    ExecutorService executor = Executors.newFixedThreadPool(this.numThreads);
 
    int threadIndex = 0;
    for (final KafkaStream stream : streams) {
      executor.submit(new NileConsumer(stream, producer));
      threadIndex++;
    }
    try {
      executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static ConsumerConfig createConfig(String zookeeper, String groupId) {
    Properties props = new Properties();
    props.put("zookeeper.connect", zookeeper);
    props.put("group.id", groupId);
    props.put("zookeeper.session.timeout.ms", "400");
    props.put("zookeeper.sync.time.ms", "200");
    props.put("auto.commit.interval.ms", "1000");
    return new ConsumerConfig(props);
  }
}
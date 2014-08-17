package weatherenrich;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.Optional;
import weatherenrich.events.RawEvent;
import weatherenrich.events.EnrichedEvent;                           // Added for third version

public class WeatherEnrich
{
  private static final String RAW_STREAM = "calc_events";            // a

  public static void main(String[] args) {
    
    ConsumerIterator<byte[], byte[]> it;

    ConsumerConnector consumer =
      kafka.consumer.Consumer.createJavaConsumerConnector(createConfig());

    Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
    topicCountMap.put(RAW_STREAM, new Integer(1));
    Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap =
      consumer.createMessageStreams(topicCountMap);
    KafkaStream<byte[], byte[]> stream =
      consumerMap.get(RAW_STREAM).get(0);

    while (true) {                                                   // b
      it = stream.iterator();
      while (it.hasNext()) {
        processEvent3(new String(it.next().message()));
      }
    }
  }

  private static ConsumerConfig createConfig() {                     // d
    Properties props = new Properties();
    props.put("zookeeper.connect", "127.0.0.1:2181");
    props.put("group.id", "group1");
    props.put("zookeeper.session.timeout.ms", "400");
    props.put("zookeeper.sync.time.ms", "200");
    props.put("auto.commit.interval.ms", "1000");

    return new ConsumerConfig(props);
  }

  private static void processEvent1(String raw) {                     // c
    System.out.println(raw);
  }

  // Second version
  private static void processEvent2(String raw) {
    Optional<RawEvent> rawEvent = RawEvent.parse(raw);
    rawEvent.ifPresent(r ->
      System.out.println(r.asJson()));
  }

  // Third version
  private static void processEvent3(String raw) {
    Optional<RawEvent> rawEvent = RawEvent.parse(raw);
    rawEvent.ifPresent(r -> {
      List<String> conditions = new ArrayList<String>();
      EnrichedEvent enriched = new EnrichedEvent(r, 20.0d, conditions);
      System.out.println(enriched.asJson());
    });
  }
}

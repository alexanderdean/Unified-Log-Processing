package weatherenrich;

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
import weatherenrich.events.EnrichedEvent;

import WeatherAPI.*;                                                   // a
import java.util.stream.Collectors;

import weatherenrich.events.Event;                                     // b
import kafka.javaapi.producer.Producer;

public class WeatherEnrich
{
  private static final String RAW_STREAM = "calc_events";

  public static void main(String[] args) {
    
    Producer<String, String> producer = Event.createProducer("localhost:9092"); // c

    ConsumerIterator<byte[], byte[]> it;

    ConsumerConnector consumer =
      kafka.consumer.Consumer.createJavaConsumerConnector(createConfig());

    Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
    topicCountMap.put(RAW_STREAM, new Integer(1));
    Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap =
      consumer.createMessageStreams(topicCountMap);
    KafkaStream<byte[], byte[]> stream =
      consumerMap.get(RAW_STREAM).get(0);

    while (true) {
      it = stream.iterator();
      while (it.hasNext()) {
        processEvent2(new String(it.next().message()), producer);
      }
    }
  }

  private static ConsumerConfig createConfig() {
    Properties props = new Properties();
    props.put("zookeeper.connect", "127.0.0.1:2181");
    props.put("group.id", "group1");
    props.put("zookeeper.session.timeout.ms", "400");
    props.put("zookeeper.sync.time.ms", "200");
    props.put("auto.commit.interval.ms", "1000");

    return new ConsumerConfig(props);
  }

  private static void processEvent1(String raw) {                      // d
    Optional<RawEvent> rawEvent = RawEvent.parse(raw);
    rawEvent.ifPresent(r -> {
      IWeather weather = WeatherAPI.getWeather("New York City", "NY");
      List<String> conditions = weather.getConditions()
        .stream()
        .map(o -> o.toString())
        .collect(Collectors.toList());
      double temp = weather.getDegreesCelsius();
      EnrichedEvent enriched = new EnrichedEvent(r, temp, conditions);
      System.out.println(enriched.asJson());
    });
  }

  private static void processEvent2(String raw, Producer producer) {   // e
    Optional<RawEvent> rawEvent = RawEvent.parse(raw);
    rawEvent.ifPresent(r -> {
      IWeather weather = WeatherAPI.getWeather("New York City", "NY");
      List<String> conditions = weather.getConditions()
        .stream()
        .map(o -> o.toString())
        .collect(Collectors.toList());
      double temp = weather.getDegreesCelsius();
      EnrichedEvent enriched = new EnrichedEvent(r, temp, conditions);
      System.out.println("<<sending event to Kafka enriched stream>>");  // f
      enriched.sendTo(producer);
    });
  }
}

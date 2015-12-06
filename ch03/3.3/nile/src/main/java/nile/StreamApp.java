package nile;

import java.io.IOException;

import com.maxmind.geoip.LookupService;

public class StreamApp {

  // wget "http://geolite.maxmind.com/download/geoip/database/GeoLiteCity.dat.gz"
  // gunzip GeoLiteCity.dat.gz
  // e.g. java -jar ./build/libs/nile-0.1.0.jar "127.0.0.1:2181" "localhost:9092" "stream-app-pt1" "raw-events" "enriched-events" "bad-events" "/tmp/GeoLiteCity.dat"
  public static void main(String[] args) throws IOException {
    String zookeeper = args[0];
    String brokers = args[1];
    String groupId = args[2];
    String inTopic = args[3];
    String goodTopic = args[4];
    String badTopic = args[5];
    LookupService maxmind = new LookupService(args[6],
      LookupService.GEOIP_MEMORY_CACHE);

    NileConsumerGroup consumerGroup = new NileConsumerGroup(
      zookeeper, groupId, inTopic);
    PassthruProducer producer = new PassthruProducer(brokers, goodTopic);
    //FullProducer producer = new FullProducer(brokers, goodTopic, badTopic, maxmind);
    consumerGroup.run(producer);
  }
}

/*


bin/kafka-console-consumer.sh --topic enriched-events --from-beginning \
  --zookeeper localhost:2181

bin/kafka-console-consumer.sh --topic bad-events --from-beginning \
  --zookeeper localhost:2181

bin/kafka-console-producer.sh --topic raw-events \
  --broker-list localhost:9092


*/
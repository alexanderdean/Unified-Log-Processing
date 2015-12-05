package nile;

import java.io.IOException;

import com.maxmind.geoip.LookupService;

public class StreamApp {

  // wget "http://geolite.maxmind.com/download/geoip/database/GeoLiteCity.dat.gz"
  // gunzip GeoLiteCity.dat.gz
  // e.g. java -jar ./build/libs/nile-0.1.0.jar "127.0.0.1:2181" "localhost:9092" "stream-app-pt1" 2 "raw-events" "enriched-events" "bad-events" "/tmp/GeoLiteCity.dat"
  public static void main(String[] args) throws IOException {
    String zookeeper = args[0];
    String brokers = args[1];
    String groupId = args[2];
    int numThreads = Integer.parseInt(args[3]);
    String inTopic = args[4];
    String goodTopic = args[5];
    String badTopic = args[6];
    LookupService maxmind = new LookupService(args[7],
      LookupService.GEOIP_MEMORY_CACHE);

    NileConsumerGroup consumerGroup = new NileConsumerGroup(
      zookeeper, groupId, inTopic, numThreads);
    // PassthruProducer producer = new PassthruProducer(brokers, goodTopic);
    FullProducer producer = new FullProducer(brokers, goodTopic, badTopic, maxmind);
    consumerGroup.run(producer);
  }
}
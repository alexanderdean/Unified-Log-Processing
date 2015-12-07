package nile;

import java.io.IOException;

import com.maxmind.geoip.LookupService;

public class StreamApp {

  public static void main(String[] args) throws IOException {
    String consumerServers = args[0];
    String producerServers = args[1];
    String groupId         = args[2];
    String inTopic         = args[3];
    String goodTopic       = args[4];
    String badTopic        = args[5];
    LookupService maxmind  = new LookupService(args[6],
      LookupService.GEOIP_MEMORY_CACHE);

    Consumer consumer = new Consumer(consumerServers, groupId, inTopic);
    FullProducer producer = new FullProducer(
      producerServers, goodTopic, badTopic, maxmind);
    consumer.run(producer);
  }
}
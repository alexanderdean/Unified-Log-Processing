package nile;

import java.io.IOException;                                         // a

import com.maxmind.geoip.LookupService;

public class StreamApp {

  public static void main(String[] args) throws IOException {       // a
    String consumerServers = args[0];
    String producerServers = args[1];
    String groupId         = args[2];
    String inTopic         = args[3];
    String goodTopic       = args[4];
    String badTopic        = args[5];                               // b
    String maxmindFile     = args[6];                               // b

    Consumer consumer = new Consumer(consumerServers, groupId, inTopic);
    LookupService maxmind  = new LookupService(maxmindFile,
      LookupService.GEOIP_MEMORY_CACHE);                            // c
    FullProducer producer = new FullProducer(
      producerServers, goodTopic, badTopic, maxmind);               // d
    consumer.run(producer);
  }
}
package nile;

import java.io.*;                                         // a

import com.maxmind.geoip2.DatabaseReader;

public class StreamApp {

  public static void main(String[] args) throws IOException {       // a
    String servers     = args[0];
    String groupId     = args[1];
    String inTopic     = args[2];
    String goodTopic   = args[3];
    String badTopic    = args[4];                                   // b
    String maxmindFile = args[5];                                   // b

    Consumer consumer = new Consumer(servers, groupId, inTopic);
    DatabaseReader maxmind = new DatabaseReader
            .Builder(new File(maxmindFile)).build();                // c
    FullProducer producer = new FullProducer(
      servers, goodTopic, badTopic, maxmind);                       // d
    consumer.run(producer);
  }
}
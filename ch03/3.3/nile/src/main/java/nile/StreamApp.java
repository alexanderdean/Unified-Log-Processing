package nile;

public class StreamApp {

  public static void main(String[] args){
    String consumerServers = args[0];
    String producerServers = args[1];
    String groupId         = args[2];
    String inTopic         = args[3];
    String goodTopic       = args[4];

    Consumer consumer = new Consumer(consumerServers, groupId, inTopic);
    PassthruProducer producer = new PassthruProducer(
      producerServers, goodTopic);
    consumer.run(producer);
  }
}
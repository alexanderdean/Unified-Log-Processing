package nile;

public class StreamApp {

  public static void main(String[] args){
    String servers   = args[0];
    String groupId   = args[1];
    String inTopic   = args[2];
    String goodTopic = args[3];

    Consumer consumer = new Consumer(servers, groupId, inTopic);
    PassthruProducer producer = new PassthruProducer(
      servers, goodTopic);
    consumer.run(producer);
  }
}
package plum;

import java.util.Properties;

public class ExecutorApp {

  public static void main(String[] args){
    String servers       = args[0];
    String groupId       = args[1];
    String commandsTopic = args[2];
    String eventsTopic   = args[3];

    Consumer consumer = new Consumer(servers, groupId, commandsTopic);
    Properties props = new Properties();
    EchoExecutor executor = new EchoExecutor(
      servers, eventsTopic, props);
    consumer.run(executor);
  }
}

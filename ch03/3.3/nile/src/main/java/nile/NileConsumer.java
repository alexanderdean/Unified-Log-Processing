package nile;

import kafka.consumer.*;
import kafka.javaapi.producer.Producer;

public class NileConsumer implements Runnable {

  private final KafkaStream stream;
  private final INileProducer producer;

  public NileConsumer(KafkaStream stream, INileProducer producer) {
    this.stream = stream;
    this.producer = producer;
  }

  public void run() {
    while (true) {
      ConsumerIterator<byte[], byte[]> ci = stream.iterator();
      while (ci.hasNext()) {
        this.producer.process(ci.next().message());
      }
    }
  }
}

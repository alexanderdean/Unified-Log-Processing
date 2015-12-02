package nile;

import kafka.consumer.KafkaStream;
import kafka.consumer.ConsumerIterator;
import kafka.javaapi.producer.Producer;

public class NileConsumer implements Runnable {

  private KafkaStream stream;
  private NileProducer producer;

  public NileConsumer(KafkaStream stream, NileProducer producer) {
    this.stream = stream;
    this.producer = producer;
  }

  public void run() {
    while (true) {
      ConsumerIterator<byte[], byte[]> ci = stream.iterator();
      while (ci.hasNext()) {
        this.producer.write(ci.next().message());
      }
    }
  }
}

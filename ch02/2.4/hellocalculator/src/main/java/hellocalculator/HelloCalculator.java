package hellocalculator;

import java.util.Arrays;
import java.util.Properties;
import kafka.producer.ProducerConfig;
import kafka.javaapi.producer.Producer;

import events.PerformCalculationEvent;
import events.InputBadDataEvent;

public class HelloCalculator {
  
  public static void main(String[] args) throws IOException, InterruptedException {

    Producer<String, String> producer = EventHelpers.createProducer("localhost:9092");
    String hostname = getHostname();

    if (args.length < 2) {
      String err = "too few inputs (" + args.length + ")";
      System.out.println("ERROR: " + err);
      new InputBadDataEvent(hostname, args, err).sendTo(producer);
    } else {
      try {
        Integer sum = sum(args);
        System.out.println("SUM: " + sum);
        new PerformCalculationEvent(hostname, "addition", args, sum).sendTo(producer);
      } catch (NumberFormatException nfe) {
        String err = "not all inputs parseable to Integers";
        System.out.println("ERROR: " + err);
        new InputBadDataEvent(hostname, args, err).sendTo(producer);
      }
    }
  }

  static Integer sum(String[] args) throws NumberFormatException {
    return Arrays.asList(args)
      .stream()
      .mapToInt(str -> Integer.parseInt(str))
      .sum();
  }

  static Producer<String, String> createProducer(String brokerList) {
    Properties props = new Properties();
    props.put("metadata.broker.list", brokerList);
    props.put("serializer.class", "kafka.serializer.StringEncoder");
    props.put("request.required.acks", "1");
    ProducerConfig config = new ProducerConfig(props);
    return new Producer<String, String>(config);
  }
}

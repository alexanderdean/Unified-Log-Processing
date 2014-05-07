package hellocalculator;

import java.util.Arrays;

import events.Event;
import events.PerformCalculationEvent;
import events.InputBadDataEvent;

public class HelloCalculator {
  
  public static void main(String[] args) throws IOException, InterruptedException {

    Producer<String, String> producer = Event.createProducer("localhost:9092");

    if (args.length < 2) {
      String err = "too few inputs (" + args.length + ")";
      System.out.println("ERROR: " + err);
      new InputBadDataEvent(args, err).sendTo(producer);
    } else {
      try {
        Integer sum = sum(args);
        System.out.println("SUM: " + sum);
        new PerformCalculationEvent("addition", args, sum).sendTo(producer);
      } catch (NumberFormatException nfe) {
        String err = "not all inputs parseable to Integers";
        System.out.println("ERROR: " + err);
        new InputBadDataEvent(args, err).sendTo(producer);
      }
    }
  }

  static Integer sum(String[] args) throws NumberFormatException {
    return Arrays.asList(args)
      .stream()
      .mapToInt(str -> Integer.parseInt(str))
      .sum();
  }
}

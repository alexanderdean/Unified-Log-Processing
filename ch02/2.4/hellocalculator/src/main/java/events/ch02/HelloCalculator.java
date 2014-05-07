package events.ch02;

import java.util.List;
import java.util.Arrays;
import java.util.Properties;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import kafka.javaapi.producer.Producer;

import events.ch02.events.PerformCalculationEvent;
import events.ch02.events.InputBadDataEvent;

public class HelloCalculator {
  
  public static void main(String[] args) throws IOException, InterruptedException {

    Producer<String, String> producer = EventHelpers.createProducer("localhost:9092");
    String hostname = getHostname();

    if (args.length < 2) {
      String err = "too few inputs (" + args.length + ")";
      System.out.println("ERROR: " + err);
      InputBadDataEvent event = new InputBadDataEvent(hostname, args, err);
      EventHelpers.trackEvent(producer, hostname, event.asJson());
    } else {
      try {
        Integer sum = sum(args);
        System.out.println("SUM: " + sum);
        PerformCalculationEvent event = new PerformCalculationEvent(hostname, "addition", args, sum);
        EventHelpers.trackEvent(producer, hostname, event.asJson());
      } catch (NumberFormatException nfe) {
        String err = "not all inputs parseable to Integers";
        System.out.println("ERROR: " + err);
        InputBadDataEvent event = new InputBadDataEvent(hostname, args, err);
        EventHelpers.trackEvent(producer, hostname, event.asJson());
      }
    }
  }

  static Integer sum(String[] args) throws NumberFormatException {
    return Arrays.asList(args)
      .stream()
      .mapToInt(str -> Integer.parseInt(str))
      .sum();
  }

  static String getHostname() throws IOException, InterruptedException {
    Process process = Runtime.getRuntime().exec("hostname");
    process.waitFor();
    BufferedReader reader = new BufferedReader(new
      InputStreamReader(process.getInputStream()));
    return reader.readLine();
  }
}

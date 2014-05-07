package events.ch02;

import java.util.List;
import java.util.Arrays;

public class HelloCalculator {
  
  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("ERROR: too few inputs (" + args.length + ")");
    } else {
      try {
        System.out.println("SUM: " + sum(Arrays.asList(args)));
      } catch (NumberFormatException nfe) {
        System.out.println("ERROR: not all inputs parseable to Integers");
      }
    }
  }

  static Integer sum(List<String> values) throws NumberFormatException {
    return values
      .stream()
      .mapToInt(str -> Integer.parseInt(str))
      .sum();
  }
}

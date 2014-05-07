package hellocalculator;

import java.util.List;
import java.util.Arrays;

public class HelloCalculator {
  
  public static void main(String[] args) {
    if (args.length < 2) {
      String err = "too few inputs (" + args.length + ")";
      System.out.println("ERROR: " + err);
    } else {
      try {
        Integer sum = sum(args);
        System.out.println("SUM: " + sum);
      } catch (NumberFormatException nfe) {
        String err = "not all inputs parseable to Integers";
        System.out.println("ERROR: " + err);
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

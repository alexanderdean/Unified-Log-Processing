package hellocalculator;

import java.util.Arrays;

public class HelloCalculator {
  
  public static void main(String[] args) {
    if (args.length < 2) {
      String err = "too few inputs (" + args.length + ")";
      throw new IllegalArgumentException(err);                     // a
    } else {
      try {
        Integer sum = sum(args);
        System.out.println("SUM: " + sum);
      } catch (NumberFormatException nfe) {                        // b
        String err = "not all inputs parseable to Integers";
        throw new IllegalArgumentException(err);                   // a
      }
    }
  }

  static Integer sum(String[] args) throws NumberFormatException { // c
    return Arrays.asList(args)
      .stream()
      .mapToInt(str -> Integer.parseInt(str))                      // d
      .sum();
  }
}

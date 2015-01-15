package hellocalculator;

import java.util.Arrays;
import java.util.logging.Logger;                                 // a

public class HelloIncrementor {
  
  private final static Logger LOGGER =
    Logger.getLogger(HelloIncrementor.class.getName()); 

  public static void main(String[] args) {
    Arrays.asList(args)
      .stream()
      .forEach((arg) -> {                                        // b
        try {
          Integer i = incr(arg);
          System.out.println("INCREMENTED TO: " + i);            // c
        } catch (NumberFormatException nfe) {
          String err = "input not parseable to Integer: " + arg;
          LOGGER.severe(err);                                    // d
        }
      });
  }

  static Integer incr(String s) throws NumberFormatException {
    return Integer.parseInt(s) + 1;
  }
}

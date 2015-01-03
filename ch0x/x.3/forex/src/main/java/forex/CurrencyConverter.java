package forex;

import java.util.Locale;

public class CurrencyConverter {
  public enum Currency {USD, GPB, EUR}                             // a

  public static class UnsupportedCurrencyException
    extends Exception {                                            // b
    public UnsupportedCurrencyException(String raw) {
      super("Currency must be USD, EUR or GBP, not " + raw);
    }
  }

  public static Currency validateCurrency(String raw)
    throws UnsupportedCurrencyException {                          // c

    String rawUpper = raw.toUpperCase(Locale.ENGLISH);
    try {
      return Currency.valueOf(rawUpper);
    } catch (IllegalArgumentException iae) {                       // d
      throw new UnsupportedCurrencyException(raw);
    }
  }
}

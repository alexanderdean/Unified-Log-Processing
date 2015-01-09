package forex

object CurrencyConverter1 {                                        // a

  object Currency extends Enumeration {                            // b
    type Currency = Value
    val Usd = Value("USD")
    val Gbp = Value("GBP")
    val Eur = Value("EUR")
  }

  case class UnsupportedCurrencyException(raw: String)
    extends Exception("Currency must be USD/EUR/GBP, not " + raw)  // c

  @throws(classOf[UnsupportedCurrencyException])                   // d
  def validateCurrency(raw: String): Currency.Value = {

    val rawUpper = raw.toUpperCase(java.util.Locale.ENGLISH)
    try {
      Currency.withName(rawUpper)
    } catch {
      case nsee: NoSuchElementException =>                         // e
        throw new UnsupportedCurrencyException(raw)
    }
  }
}

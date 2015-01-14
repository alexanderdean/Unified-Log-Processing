package forex

object Currency extends Enumeration {                              // a
  type Currency = Value
  val Usd = Value("USD")
  val Gbp = Value("GBP")
  val Eur = Value("EUR")
}

case class UnsupportedCurrencyException(raw: String)
  extends Exception("Currency must be USD/EUR/GBP, not " + raw)    // b

object CurrencyValidator1 {                                        // c

  @throws(classOf[UnsupportedCurrencyException])
  def validateCurrency(raw: String): Currency.Value = {

    val rawUpper = raw.toUpperCase(java.util.Locale.ENGLISH)
    try {
      Currency.withName(rawUpper)                                  // d
    } catch {
      case nsee: NoSuchElementException =>                         // e
        throw new UnsupportedCurrencyException(raw)
    }
  }
}

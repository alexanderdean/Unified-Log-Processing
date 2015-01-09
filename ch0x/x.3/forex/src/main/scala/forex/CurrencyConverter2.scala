package forex

import scalaz._                                                    // a
import Scalaz._                                                    // a

object CurrencyConverter2 {

  object Currency extends Enumeration {
    type Currency = Value
    val Usd = Value("USD")
    val Gbp = Value("GBP")
    val Eur = Value("EUR")
  }

  def validateCurrency(raw: String):
    Validation[String, Currency.Value] = {                         // b

    val rawUpper = raw.toUpperCase(java.util.Locale.ENGLISH)
    try {
      Success(Currency.withName(rawUpper))                         // c
    } catch {
      case nsee: NoSuchElementException =>
        Failure("Currency must be USD/EUR/GBP, not " + raw)        // d
    }
  }
}

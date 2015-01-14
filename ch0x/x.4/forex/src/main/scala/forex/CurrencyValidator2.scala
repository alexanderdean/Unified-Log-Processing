package forex

import scalaz._                                                    // a
import Scalaz._                                                    // a

object CurrencyValidator2 {

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

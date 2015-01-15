package forex

import util.Random

import scalaz._
import Scalaz._

object ExchangeRateLookup {

  def lookup(currency: Currency.Value):
    Validation[String, Double] = {                                 // a

    currency match {
      case Currency.Eur     => 1D.success                          // b
      case _ if isUnlucky() => "Network error".failure             // c
      case Currency.Usd     => 0.85D.success
      case Currency.Gbp     => 1.29D.success
      case _                => "Unsupported currency".failure      // d
    }
  }

  private def isUnlucky(): Boolean =
    Random.nextInt(5) == 4
}

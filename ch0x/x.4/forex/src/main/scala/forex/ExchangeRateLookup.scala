package forex

import util.Random

import scalaz._
import Scalaz._

object ExchangeRateLookup {

  def lookupRate(currency: Currency.Value):
    Validation[String, Double] = {                                 // a

    currency match {
      case Currency.Eur     => 1D.success
      case _ if isUnlucky() => "Network error".fail                // b
      case Currency.Usd     => 0.85D.success                       // c
      case Currency.Gbp     => 1.29D.success
      case _                => "Unsupported currency".fail         // d
    }
  }

  private def isUnlucky(): Boolean =
    Random.nextInt(5) == 4
}

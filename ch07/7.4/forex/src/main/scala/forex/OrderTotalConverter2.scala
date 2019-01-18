package forex

import scalaz._
import Scalaz._
import scalaz.Validation.FlatMap._

import forex.{OrderTotal => OT}
import forex.{ExchangeRateLookup => ERL}

object OrderTotalConverter2 {

  def convert(rawCurrency: String, rawAmount: String):
    ValidationNel[String, OrderTotal] = {

    OT.parse(rawCurrency, rawAmount).flatMap(total =>              // a
      ERL.lookup(total.currency).toValidationNel.map((rate: Double) => // b
        OT(Currency.Eur, total.amount * rate)))                    // c
  }
}

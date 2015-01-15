package forex

import scalaz._
import Scalaz._
import scalaz.Validation.FlatMap._

import forex.{OrderTotal => OT}                                    // a
import forex.{ExchangeRateLookup => ERL}

object OrderTotalConverter1 {

  def convert(rawCurrency: String, rawAmount: String):
    ValidationNel[String, OrderTotal] = {

    for {
      total <- OT.parse(rawCurrency, rawAmount)                    // b
      rate  <- ERL.lookup(total.currency).toValidationNel          // c
      base   = OT(Currency.Eur, total.amount * rate)               // d
    } yield base
  }
}

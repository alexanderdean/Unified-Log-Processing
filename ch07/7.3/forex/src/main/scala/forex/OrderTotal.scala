package forex

import scalaz._
import Scalaz._

case class OrderTotal(currency: Currency.Value, amount: Double)    // a

object OrderTotal {

  def parse(rawCurrency: String, rawAmount: String):
    Validation[NonEmptyList[String], OrderTotal] = {               // b

    val c = CurrencyValidator2.validateCurrency(rawCurrency)       // c
    val a = AmountValidator.validateAmount(rawAmount)              // d

    (c.toValidationNel |@| a.toValidationNel) {                    // e
      OrderTotal(_, _)
    }
  }
}

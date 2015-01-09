package forex

import scalaz._
import Scalaz._

object AmountValidator {

  def validateAmount(raw: String): Validation[String, Double] = {  // a

    try {
      Success(raw.toDouble)                                        // b
    } catch {
      case nfe: NumberFormatException =>
        Failure("Amount must be parseable to Double, not " + raw)  // c
    }
  }
}

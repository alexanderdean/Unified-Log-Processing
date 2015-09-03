package aowlambda

import awscala._, dynamodbv2._

object Writer {

  private val table = {
    val ddb = DynamoDB.at(Region.US_EAST_1)
    ddb.table("aow-lambda").getOrElse(throw new
      RuntimeException("Couldn't get table"))
  }

  def write(row: Row) {

  }
}

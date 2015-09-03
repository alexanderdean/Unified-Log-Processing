package aowlambda

import awscala._, dynamodbv2._

object Writer {

  private implicit val ddb = DynamoDB.at(Region.US_EAST_1)
  private val table = ddb.table("aow-lambda").getOrElse(throw new
    RuntimeException("Couldn't get table"))

  def write(row: Row) {

  }
}

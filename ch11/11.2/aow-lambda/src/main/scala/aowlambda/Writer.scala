package aowlambda

import awscala._, dynamodbv2.{AttributeValue => AttrVal, _}
import com.amazonaws.services.dynamodbv2.model._
import scala.collection.JavaConverters._

object Writer {
  private val ddb = DynamoDB.at(Region.US_EAST_1)

  private def updateIf(key: AttributeValue, updExpr: String,
    condExpr: String, values: Map[String, AttributeValue],
    names: Map[String, String]) {                                  // a

    val updateRequest = new UpdateItemRequest()
      .withTableName("oops-trucks")
      .addKeyEntry("vin", key)
      .withUpdateExpression(updExpr)
      .withConditionExpression(condExpr)
      .withExpressionAttributeValues(values.asJava)
      .withExpressionAttributeNames(names.asJava)

    try {
      ddb.updateItem(updateRequest)
    } catch { case ccfe: ConditionalCheckFailedException => }      // b
  }

  def conditionalWrite(row: Row) {
    val vin = AttrVal.toJavaValue(row.vin)

    updateIf(vin, "SET #m = :m",
      "attribute_not_exists(#m) OR #m < :m",
      Map(":m" -> AttrVal.toJavaValue(row.mileage)),
      Map("#m" -> "mileage"))                                      // c

    for (maoc <- row.mileageAtOilChange) {                         // d
      updateIf(vin, "SET #maoc = :maoc",
        "attribute_not_exists(#maoc) OR #maoc < :m",
        Map(":maoc" -> AttrVal.toJavaValue(maoc)),
        Map("#maoc" -> "mileage-at-oil-change"))
    }

    for ((loc, ts) <- row.locationTs) {                            // e
      updateIf(vin, "SET #ts = :ts, #lat = :lat, #long = :long",
        "attribute_not_exists(#ts) OR #ts < :ts",
        Map(":ts"   -> AttrVal.toJavaValue(ts.toString),
            ":lat"  -> AttrVal.toJavaValue(loc.latitude),
            ":long" -> AttrVal.toJavaValue(loc.longitude)),
        Map("#ts"   -> "location-timestamp", "#lat" -> "latitude",
            "#long" -> "longitude"))
    }
  }
}

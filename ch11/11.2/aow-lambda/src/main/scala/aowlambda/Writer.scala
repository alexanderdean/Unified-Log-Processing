package aowlambda

import awscala._, dynamodbv2._
import com.amazonaws.services.dynamodbv2.model.{AttributeAction => _,
  AttributeValue => AwsAttributeValue, _}
import scala.collection.JavaConverters._

object Writer {

  private val ddb = DynamoDB.at(Region.US_EAST_1)

  private def att(av: AwsAttributeValue) = new AttributeValueUpdate()
    .withValue(av)
    .withAction(AttributeAction.Put)

  private def exp(av: AwsAttributeValue) = new ExpectedAttributeValue()
    .withValue(av)
    .withComparisonOperator(ComparisonOperator.LT)

  def conditionalWrite(row: Row) {

    def stubUIR() = new UpdateItemRequest()                        // a
      .withTableName("oops-trucks")
      .addKeyEntry("vin", AttributeValue.toJavaValue(row.vin))

    val mileageAV = AttributeValue.toJavaValue(row.mileage)
    try { ddb.updateItem(stubUIR                                   // b
      .addAttributeUpdatesEntry("mileage", att(mileageAV))
      .withConditionExpression("""attribute_not_exists(mileage) OR
        mileage < :m""")
      .withExpressionAttributeValues(Map(":m" -> mileageAV).asJava))
    } catch { case ccfe: ConditionalCheckFailedException => }

    for (maoc <- row.mileageAtOilChange) {                         // c
      val maocAV = AttributeValue.toJavaValue(row.mileage)
      val _ = ddb.updateItem(stubUIR
        .addAttributeUpdatesEntry("mileage-at-oil-change", att(maocAV)))
        //.addExpectedEntry("mileage-at-oil-change", exp(maocAV)))
    } 

    for ((loc, ts) <- row.locationTs) {                            // d
      val tsAV = AttributeValue.toJavaValue(ts.toString)
      val latAV = AttributeValue.toJavaValue(loc.latitude)
      val longAV = AttributeValue.toJavaValue(loc.longitude)
      val _ = ddb.updateItem(stubUIR
        .addAttributeUpdatesEntry("location-timestamp", att(tsAV))
        .addAttributeUpdatesEntry("latitude", att(latAV))
        .addAttributeUpdatesEntry("longitude", att(longAV)))
        //.addExpectedEntry("location-timestamp", exp(tsAV)))
    }
  }
}

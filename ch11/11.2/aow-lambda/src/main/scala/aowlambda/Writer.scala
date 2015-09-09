package aowlambda

import awscala._, dynamodbv2._
import com.amazonaws.services.dynamodbv2.model.{AttributeAction => _,
  AttributeValue => AwsAttributeValue, _}

object Writer {

  private val ddb = DynamoDB.at(Region.US_EAST_1)

  private def att(av: AwsAttributeValue) = new AttributeValueUpdate()
    .withValue(av)
    .withAction(AttributeAction.Add)

  private def exp(av: AwsAttributeValue) = new ExpectedAttributeValue()
    .withValue(av)
    .withComparisonOperator(ComparisonOperator.LT)

  def conditionalWrite(row: Row) {

    val partialUIR = new UpdateItemRequest()                       // a
      .withTableName("oops-trucks")
      .addKeyEntry("vin", AttributeValue(s = Some(row.vin)))

    val mileageAV = AttributeValue.toJavaValue(row.mileage)
    val _ = ddb.updateItem(partialUIR                              // b
      .addAttributeUpdatesEntry("mileage", att(mileageAV))
      .addExpectedEntry("mileage", exp(mileageAV)))

    for (maoc <- row.mileageAtOilChange) {                         // c
      val maocAV = AttributeValue.toJavaValue(row.mileage)
      val _ = ddb.updateItem(partialUIR
        .addAttributeUpdatesEntry("mileage-at-oil-change", att(maocAV))
        .addExpectedEntry("mileage-at-oil-change", exp(maocAV)))
    } 

    for ((loc, ts) <- row.locationTs) {                            // d
      val tsAV = AttributeValue.toJavaValue(ts.toString)
      val latAV = AttributeValue.toJavaValue(loc.latitude.toString)
      val longAV = AttributeValue.toJavaValue(loc.longitude.toString)
      val _ = ddb.updateItem(partialUIR
        .addAttributeUpdatesEntry("location-timestamp", att(tsAV))
        .addAttributeUpdatesEntry("latitude", att(latAV))
        .addAttributeUpdatesEntry("longitude", att(longAV))        
        .addExpectedEntry("location-timestamp", exp(tsAV)))
    }
  }
}

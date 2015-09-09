package aowlambda

import awscala._, dynamodbv2._
import com.amazonaws.services.dynamodbv2.model.{AttributeValue => _,
  AttributeAction => _, _}

object Writer {

  private val ddb = DynamoDB.at(Region.US_EAST_1)

  private def avu(av: AttributeValue) = new AttributeValueUpdate()
    .withValue(mileageAV)
    .withAction(AttributeAction.Add)

  private def eav(av: AttributeValue) = new ExpectedAttributeValue()
    .withValue(av)
    .withComparisonOperator(ComparisonOperator.GT)

  def write(row: Row) {
    val vinAV = AttributeValue(s = Some(row.vin))

    val mileageAV = AttributeValue(n = Some(row.mileage.toString))
    val _ = ddb.updateItem(new UpdateItemRequest()            // a
      .withTableName("oops-trucks")
      .addKeyEntry("vin", vinAV)
      .addAttributeUpdatesEntry("mileage", avu(mileageAV))
      .addExpectedEntry("mileage", eav(mileageAV)))

    for (maoc <- row.mileageAtOilChange) {                         // b
      val maocAV = AttributeValue(n = Some(row.mileage.toString))
      val _ = ddb.updateItem(new UpdateItemRequest()
        .withTableName("oops-trucks")
        .addKeyEntry("vin", vinAV)
        .addAttributeUpdatesEntry("mileage-at-oil-change", avu(maocAV))
        .addExpectedEntry("mileage-at-oil-change", eav(maocAV)))

    }

/*
            UpdateItemResult result = dynamodb.updateItem(new UpdateItemRequest()
                .withTableName("Game")
                .withReturnValues(ReturnValue.ALL_NEW)
                .addKeyEntry("GameId", new AttributeValue("abc"))
                .addAttributeUpdatesEntry(
                     "Player1-Position", new AttributeValueUpdate()
                         .withValue(new AttributeValue().withN("1"))
                         .withAction(AttributeAction.ADD))
                .addExpectedEntry(
                     "Player1-Position", new ExpectedAttributeValue()
                         .withValue(new AttributeValue().withN("20"))
                         .withComparisonOperator(ComparisonOperator.LT))
                .addExpectedEntry(
                     "Player2-Position", new ExpectedAttributeValue()
                         .withValue(new AttributeValue().withN("20"))
                         .withComparisonOperator(ComparisonOperator.LT))
                .addExpectedEntry(
                     "Status", new ExpectedAttributeValue()
                         .withValue(new AttributeValue().withS("IN_PROGRESS"))
                         .withComparisonOperator(ComparisonOperator.EQ))
 */

  }
}

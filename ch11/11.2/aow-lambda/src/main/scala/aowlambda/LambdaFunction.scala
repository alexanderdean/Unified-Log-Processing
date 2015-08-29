package aowlambda

import com.amazonaws.services.lambda.runtime.events.KinesisEvent
import awscala._
import dynamodbv2._

import scala.collection.JavaConversions._
import scala.collection.mutable.Buffer

class LambdaFunction {

  private val AwsRegion = Region.US_EAST_1
  private val AwsTable = "my-table"

  import com.fasterxml.jackson.databind.ObjectMapper
  import com.fasterxml.jackson.module.scala.DefaultScalaModule
  
  val scalaMapper = {  
    new ObjectMapper().registerModule(new DefaultScalaModule)
  }

  def recordHandler(microBatch: KinesisEvent) {

  }
    /* What does getKinesis return?
    val events = for (record <- microBatch.getRecords) {


      getKinesis.getData.array()

    val convertedRecords =
      for {
        rec <- event.getRecords 
        val record = new String(rec.getKinesis.getData.array())
        val event = scalaMapper.readValue(record, classOf[SimpleEvent])
      } yield event
    
    /*
    def aggregateRecords(converted: Buffer[SimpleEvent]) {
      val eventArray = converted.groupBy(_.bucket).mapValues(_.map(x => x.eventType))
      val counted = eventArray.mapValues(_.groupBy(identity).mapValues(_.size))

      implicit val dynamoDB = DynamoDB.at(AwsRegion)
      val table: Table = dynamoDB.table(AwsTable).get

      // Stomic increments with addAttributes
      for (bucket <- counted)
        bucket._2.map( { 
          case (key, value) =>
          table.addAttributes(bucket._1, key, Seq("Count" -> value))
      })
    }
    aggregateRecords(convertedRecords) */
  }

  */
}


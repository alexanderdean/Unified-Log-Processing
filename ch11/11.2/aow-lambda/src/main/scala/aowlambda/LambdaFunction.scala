package aowlambda

import com.amazonaws.services.lambda.runtime.events.KinesisEvent
import scala.collection.JavaConverters._

class LambdaFunction {

  def recordHandler(microBatch: KinesisEvent) {

    val allRows = for {
      rec <- microBatch.getRecords.asScala.toList
      bytes = rec.getKinesis.getData.array 
      event = Event.fromBytes(bytes)
      row = Aggregator.map(event)
    } yield row

    val reducedRows = Aggregator.reduce(allRows)
  }
}

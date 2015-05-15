package nile

// Spark
import org.apache.spark.{SparkContext, SparkConf}
import SparkContext._
import org.apache.spark.sql._
import functions._

// Hadoop
import org.apache.hadoop.io.BytesWritable

object NileShopperAnalysisJob {

  def main(args: Array[String]) {

    val (inFile, outFolder) = {                                    // a
      val a = args.toList
      (a(0), a(1))
    }

    val sparkConf = new SparkConf()
      .setAppName("NileShopperAnalysis")
      .setJars(List(SparkContext.jarOfObject(this).get))           // b
    val sparkContext = new SparkContext(sparkConf)

    val file = sparkContext.sequenceFile[Long, BytesWritable](inFile)
    val jsons = file.map {
      case (_, bw) => toJson(bw)
    }

    val sqlContext = new SQLContext(sparkContext)
    val events = sqlContext.jsonRDD(jsons)

    val (shopper, item, order) =
      ("subject.shopper", "directObject.item", "directObject.order")
    events
      .filter(s"${shopper} is not null")
      .groupBy(shopper)
      .agg(
        col(shopper),
        sum(s"${item}.quantity"),
        sum(col(s"${item}.quantity") * col(s"${item}.price")),
        count(order),
        sum(s"${order}.value")
      )

    events.rdd.saveAsTextFile(outFolder)                           // c
  }

  private def toJson(bytes: BytesWritable): String =               // d
    new String(bytes.getBytes, 0, bytes.getLength, "UTF-8")
}

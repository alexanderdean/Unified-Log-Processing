package nile

import org.apache.spark.{SparkContext, SparkConf}
import SparkContext._
import org.apache.spark.sql._
import functions._
import org.apache.hadoop.io.BytesWritable

object ShopperAnalysisJob {

  def main(args: Array[String]) {

    val (inFile, outFolder) = {                                    // a
      val a = args.toList
      (a(0), a(1))
    }

    val sparkConf = new SparkConf()
      .setAppName("ShopperAnalysis")
      .setJars(List(SparkContext.jarOfObject(this).get))           // b
    val spark = SparkSession.builder()
      .config(sparkConf)
      .getOrCreate()
    val sparkContext = spark.sparkContext

    val file = sparkContext.sequenceFile[Long, BytesWritable](inFile)
    val jsons = file.map {
      case (_, bw) => toJson(bw)
    }

    val sqlContext = spark.sqlContext
    val events = sqlContext.read.json(jsons)

    val (shopper, item, order) =
      ("subject.shopper", "directObject.item", "directObject.order")
    val analysis = events
      .filter(s"${shopper} is not null")
      .groupBy(shopper)
      .agg(
        col(shopper),
        sum(s"${item}.quantity"),
        sum(col(s"${item}.quantity") * col(s"${item}.price")),
        count(order),
        sum(s"${order}.value")
      )

    analysis.rdd.saveAsTextFile(outFolder)                         // c
  }

  private def toJson(bytes: BytesWritable): String =               // d
    new String(bytes.getBytes, 0, bytes.getLength, "UTF-8")
}

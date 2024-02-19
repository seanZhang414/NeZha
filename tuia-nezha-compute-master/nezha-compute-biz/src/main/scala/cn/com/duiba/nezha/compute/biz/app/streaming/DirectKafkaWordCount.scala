package cn.com.duiba.nezha.compute.biz.app.streaming

import _root_.kafka.serializer.StringDecoder
import cn.com.duiba.nezha.compute.biz.constant.ProjectConstant
import cn.com.duiba.nezha.compute.common.util.conf.ConfigFactory
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._

/**
 * Created by pc on 2017/1/19.
 */
object DirectKafkaWordCount {

  def main(args: Array[String]) {


    // config
    val brokers = ConfigFactory.getInstance.getConfigProperties(ProjectConstant.CONFIG_PATH).getProperty(ProjectConstant.KAFKA_BROKERS)
    val topics = ConfigFactory.getInstance.getConfigProperties(ProjectConstant.CONFIG_PATH).getProperty(ProjectConstant.KAFKA_TOPICS_TUIA_LAUNCH_LOG)

    // Create context with 2 second batch interval

    val sparkConf = new SparkConf().setAppName("nezhaDirectKafkaWordCount").setMaster("local[1]")
    val ssc = new StreamingContext(sparkConf, Seconds(2))


    // Create direct kafka stream with brokers and topics
    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers)
    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topicsSet)

    // Get the lines, split them into words, count the words and print
    val lines = messages.map(_._2)
    val words = lines.flatMap(_.split(" "))
    val wordCounts = words.map(x => (x, 1L)).reduceByKey(_ + _)
    wordCounts.print()

    // Start the computation
    ssc.start()
    ssc.awaitTermination()
  }
}
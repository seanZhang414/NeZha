package cn.com.duiba.nezha.compute.biz.app.streaming

import cn.com.duiba.nezha.compute.biz.conf.KafkaConf
import cn.com.duiba.nezha.compute.common.params.Params
import Params.AdvertLogParams
import cn.com.duiba.nezha.compute.biz.server.distribute.LogDistributeServer
import cn.com.duiba.nezha.compute.biz.support.Topic
import cn.com.duiba.nezha.compute.common.params.Params
import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka.KafkaUtils

/**
 * Created by pc on 2017/1/19.
 */
object DirectKafkaTuiaAdvertLog2 {

  def run(params: AdvertLogParams) {

    // config
    val brokers = KafkaConf.brokers

    val topic = Topic.getTopic(params.topic)

    if (topic == null) {
      println("get topic null,app break...")
      return
    }

    // Create direct kafka stream with brokers and topics
    val topicsSet = topic.getTopic.split(",").toSet
    val appName = "DirectKafkaTuiaAdvertLog_Topic_" + topic.getTopic
    val groupId = "DirectKafkaTuiaAdvertLog_Topic_" + topic.getTopic
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers,"group.id"->groupId)

    // 1.初始spark context
    println("init spark context ...,appName= " + appName)

    // System.setProperty("spark.default.parallelism", "10")
    var sparkConf = new SparkConf().setAppName(appName).setMaster("local[2]")
    if (!params.localRun) {
      sparkConf = new SparkConf().setAppName(appName) // 集群运行模式，读取spark集群的环境变量
    }

    // Create context with 2 second batch interval
    val ssc = new StreamingContext(sparkConf, Seconds(params.interval))

    // We use a broadcast variable to share a pool of Kafka producers, which we use to write data from Spark to Kafka.

    // Get the messages
    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topicsSet)


    // Get the lines
    val lines = messages.map(_._2).filter(_ != null)

    lines.foreachRDD(wd =>
      wd.foreachPartition(partitionOfRecords=>
        LogDistributeServer.distribute(partitionOfRecords,topic,params)))

    // Start the computation
    ssc.start()
    ssc.awaitTermination()
  }


}

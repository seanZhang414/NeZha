package cn.com.duiba.nezha.compute.biz.app.streaming

import cn.com.duiba.nezha.compute.biz.conf.KafkaConf
import cn.com.duiba.nezha.compute.biz.server.distribute.LogDistributeServer
import cn.com.duiba.nezha.compute.biz.support.Topic
import cn.com.duiba.nezha.compute.common.params.Params.AdvertLogParams
import kafka.serializer.StringDecoder
import org.apache.log4j.Logger
import org.apache.spark.SparkConf
import org.apache.spark.streaming._

/**
 * Created by pc on 2017/1/19.
 */
object DirectKafkaTuiaAdvertLog4 {


  def run(params: AdvertLogParams) {
    val logger = Logger.getLogger(DirectKafkaTuiaAdvertLog.getClass)

    // config
    val brokers = KafkaConf.brokers

    val topic = Topic.getTopic(params.topic)

    if (topic == null) {
      println("get topic null,app break...")
      return
    }

    // Create direct kafka stream with brokers and topics
    val topicsSet = topic.getTopic.split(",").toSet
    val statType = params.statType
    val appName = "tuia_advertLog_topic_2" + topic.getTopic + "_stat_type_" + statType
    val groupId = "tuia_advertLog_topic_2" + topic.getTopic + "_stat_type_" + statType
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers,"group.id"->groupId)


    // 1.初始spark context
    println("init spark context ...,appName= " + appName)

    // System.setProperty("spark.default.parallelism", "10")
    var sparkConf = new SparkConf().setAppName(appName).setMaster("local[1]")
    if (!params.localRun) {
      sparkConf = new SparkConf().setAppName(appName) // 集群运行模式，读取spark集群的环境变量


    }
    // 限制每秒钟从topic的每个partition最多消费的消息条数
    sparkConf.set("spark.streaming.kafka.maxRatePerPartition", "2000")

    // Create context with 2 second batch interval
    val ssc = new StreamingContext(sparkConf, Seconds(params.interval))
    val km = new KafkaManager(kafkaParams)
    val messages = km.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topicsSet)

    try {

      messages.foreachRDD(rdd => {
        if (!rdd.isEmpty()) {
          rdd.map(_._2).repartition(params.partitionNums).foreachPartition(partitionOfRecords =>
            LogDistributeServer.distribute(partitionOfRecords, topic, params))

          km.updateZKOffsets(rdd)
        }

      }
      )

    }
    catch {
      case ex: Exception => logger.error(ex)
    }

    // Start the computation
    ssc.start()
    ssc.awaitTermination()
  }


}

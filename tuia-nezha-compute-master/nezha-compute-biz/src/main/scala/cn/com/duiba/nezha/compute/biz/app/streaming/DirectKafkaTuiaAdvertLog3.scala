package cn.com.duiba.nezha.compute.biz.app.streaming

import cn.com.duiba.nezha.compute.common.enums.DateStyle
import cn.com.duiba.nezha.compute.common.util.DateUtil
import kafka.common.TopicAndPartition
import cn.com.duiba.nezha.compute.biz.conf.KafkaConf
import cn.com.duiba.nezha.compute.common.params.Params
import Params.AdvertLogParams
import cn.com.duiba.nezha.compute.biz.server.distribute.LogDistributeServer
import cn.com.duiba.nezha.compute.biz.support.Topic

import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.log4j.{Level, Logger}
import org.apache.spark.streaming.kafka.{OffsetRange, HasOffsetRanges, KafkaUtils}

/**
 * Created by pc on 2017/1/19.
 */
object DirectKafkaTuiaAdvertLog3 {


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
    val appName = "DirectKafkaTuiaAdvertLog_Topic_" + topic.getTopic + "_statType_" + statType
    val groupId = "DirectKafkaTuiaAdvertLog_Topic_11" + topic.getTopic + "_statType_" + statType
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers,"group.id"->groupId)


    // 1.初始spark context
    println("init spark context ...,appName= " + appName)

    // System.setProperty("spark.default.parallelism", "10")
    var sparkConf = new SparkConf().setAppName(appName).setMaster("local[1]")
    if (!params.localRun) {
      sparkConf = new SparkConf().setAppName(appName) // 集群运行模式，读取spark集群的环境变量
    }
    // 限制每秒钟从topic的每个partition最多消费的消息条数
    sparkConf.set("spark.streaming.kafka.maxRatePerPartition", "10")
    // Create context with 2 second batch interval
    val ssc = new StreamingContext(sparkConf, Seconds(params.interval))

    // We use a broadcast variable to share a pool of Kafka producers, which we use to write data from Spark to Kafka.
    // Get the messages
    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topicsSet)

    // Get the lines
    messages.repartition(params.partitionNums).foreachRDD(wd => {
      val kc = new KafkaCluster2(kafkaParams)
      wd.map(_._2).foreachPartition(partitionOfRecords =>
        LogDistributeServer.distribute(partitionOfRecords, topic, params))

    })
    messages.foreachRDD(wd => {
      val kc = new KafkaCluster2(kafkaParams)
      println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + " update off set")
      val offsetsList = wd.asInstanceOf[HasOffsetRanges].offsetRanges
      for (offsets <- offsetsList) {
        val topicAndPartition = TopicAndPartition(topic.getTopic, offsets.partition)

        val o = kc.setConsumerOffsets(groupId, Map((topicAndPartition, offsets.untilOffset)),0)
        if (o.isLeft) {
          println(s"Error updating the offset to Kafka cluster: ${o.left.get}")
        }
      }

    })

    // Hold a reference to the current offset ranges, so it can be used downstream
    var offsetRanges = Array[OffsetRange]()

    messages.transform { rdd =>
      offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      rdd
    }.foreachRDD { rdd =>
      for (o <- offsetRanges) {
        println(s"${o.topic} ${o.partition} ${o.fromOffset} ${o.untilOffset}")
      }
    }


    // Start the computation
    ssc.start()
    ssc.awaitTermination()
  }


}

package cn.com.duiba.nezha.compute.biz.spark.streaming

import cn.com.duiba.nezha.compute.biz.app.SparseFMWithFTRLApp
import cn.com.duiba.nezha.compute.biz.bo.SyncBo
import cn.com.duiba.nezha.compute.biz.conf.KafkaConf
import cn.com.duiba.nezha.compute.biz.kafka.KafkaManager
import cn.com.duiba.nezha.compute.core.enums.DateStyle
import cn.com.duiba.nezha.compute.core.util.DateUtil
import kafka.serializer.StringDecoder
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}


object PsModelStreaming {

  Logger.getLogger("org").setLevel(Level.ERROR)

  def run(params: PSModelParams): Unit = {
    val logger = Logger.getLogger(PsModelStreaming.getClass)

    if (params.topic == null) {
      println("get topic null,app break...")
      return
    }
    // Create direct kafka stream with brokers and topics
    val topicsSet = Set(params.topic)

    val appName = "ps_model_" + params.featureModelId+"_on_model_"+params.onLineModelId+ "_data_" + params.topic
    // 1.初始spark context
    println("init spark context ...,KafkaConf.brokers = " + KafkaConf.brokers)
    val kafkaParams = Map[String, String]("metadata.broker.list" -> KafkaConf.brokers, "group.id" -> appName)


    // 1.初始spark context
    println("init spark context ...,appName= " + appName)

    // System.setProperty("spark.default.parallelism", "10")
    var sparkConf = new SparkConf().setAppName(appName).setMaster("local[2]")
    if (!params.isLocal) {
      sparkConf = new SparkConf().setAppName(appName) // 集群运行模式，读取spark集群的环境变量

    }
    // 限制每秒钟从topic的每个partition最多消费的消息条数
    sparkConf.set("spark.streaming.kafka.maxRatePerPartition", "2000")

    // Create context with 2 second batch interval
    val ssc = new StreamingContext(sparkConf, Seconds(params.batchInterval))
    val km = new KafkaManager(kafkaParams)
    val messages = km.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topicsSet)

    try {
      messages.foreachRDD(rdd => {
        if (!rdd.isEmpty()) {
          rdd.map(_._2).repartition(params.partNums).foreachPartition(partitionOfRecords =>
            SparseFMWithFTRLApp.runOnLine(params.featureModelId, params.isCtr, partitionOfRecords, params.delay,params.isReplay)
          )
          km.updateZKOffsets(rdd)
        } else println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + "  data empty")

        SyncBo.syncModel(params.onLineModelId,params.featureModelId,params.isSync)
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

case class PSModelParams(
                          isLocal: Boolean = false,
                          topic: String = null,
                          batchInterval: Int = 5,
                          featureModelId: String = null,
                          onLineModelId: String = null,
                          partNums: Int = 1,
                          isCtr: Boolean = true,
                          delay: Int = 120,
                          startTime: String = "2018-02-28 00:00:00",
                          stepSize: Int = 1,
                          isReplay:Boolean = false,
                          isSync:Boolean =false,
                          sampleRatio: Double =1.0
                        )
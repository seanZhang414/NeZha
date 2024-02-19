package cn.com.duiba.nezha.compute.biz.spark

import cn.com.duiba.nezha.compute.biz.app.SparseFMWithFTRLApp
import cn.com.duiba.nezha.compute.biz.bo.SyncBo
import cn.com.duiba.nezha.compute.biz.conf.KafkaConf
import cn.com.duiba.nezha.compute.biz.params.PSModelParams
import cn.com.duiba.nezha.compute.core.enums.DateStyle
import cn.com.duiba.nezha.compute.core.util.DateUtil
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}


object PsModelStreaming {

  Logger.getLogger("org").setLevel(Level.ERROR)

  def run(params: PSModelParams,sampleRatio:Double): Unit = {
    val logger = Logger.getLogger(PsModelStreaming.getClass)

    if (params.topic == null) {
      println("get topic null,app break...")
      return
    }
    // Create direct kafka stream with brokers and topics
    val topicsSet = Set(params.topic)

    val appName = "ps_model_" + params.featureModelId + "_on_model_" + params.onLineModelId + "_data_" + params.topic
    // 1.初始spark context
    println("init spark context ...,KafkaConf.brokers = " + KafkaConf.brokers)

    val kafkaParams = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> KafkaConf.brokers,
      ConsumerConfig.GROUP_ID_CONFIG -> appName,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG  -> "latest",
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG ->  (false: java.lang.Boolean) // 新增
    )


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
    //    val km = new KafkaManager(kafkaParams)

    val messages = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topicsSet, kafkaParams))

    val lines = messages.map(_.value())

    try {
      lines.foreachRDD(rdd => {
        if (!rdd.isEmpty()) {
          rdd.repartition(params.partNums).foreachPartition(partitionOfRecords =>
            SparseFMWithFTRLApp.runOnLine(params.featureModelId, params.isCtr, partitionOfRecords, params.delay, params.isReplay,params.partNums,sampleRatio)
          )
        } else {
          println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + "  data empty")
        }

        SyncBo.syncModel(params.onLineModelId, params.featureModelId, params.isSync)

//        val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
//
//        // some time later, after outputs have completed
//
//        lines.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)

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


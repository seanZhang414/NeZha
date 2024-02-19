package cn.com.duiba.nezha.compute.biz.spark

import cn.com.duiba.nezha.compute.biz.app.SparseFMWithFTRLApp
import cn.com.duiba.nezha.compute.biz.bo.SampleBo
import cn.com.duiba.nezha.compute.biz.params.PSModelParams
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer

object PsModelTest {

  Logger.getLogger("org").setLevel(Level.ERROR)

  def run(params: PSModelParams): Unit = {
    val logger = Logger.getLogger(PsModelTest.getClass)

    if (params.topic == null) {
      println("get topic null,app break...")
      return
    }
    // Create direct kafka stream with brokers and topics
    val topicsSet = Set(params.topic)

    val appName = "ps_model_" + params.featureModelId + "_on_model_" + params.onLineModelId + "_data_" + params.topic
    // 1.初始spark context

    // 1.初始spark context
    println("init spark context ...,appName= " + appName)



    // System.setProperty("spark.default.parallelism", "10")
    var sparkConf = new SparkConf().setAppName(appName).setMaster("local[3]")
    if (!params.isLocal) {
      sparkConf = new SparkConf().setAppName(appName) // 集群运行模式，读取spark集群的环境变量

    }
    val sc = new SparkContext(sparkConf) // 获取 sc
    import collection.JavaConversions._
    try {
      //      val baseTime = startTime //"2018-02-26 12:30:00"
      for (i <- 0 to 60 * 24 / params.stepSize) {

        var time = new ArrayBuffer[String]()
        for (j <- 0 to params.stepSize - 1) {
          if (math.random > (1-params.sampleRatio)) {
            val times = SampleBo.getTimeInterval(params.startTime, i * params.stepSize + j)
            time ++= times
          }
        }


        // read order ids

        var orderIds = SampleBo.getOrderList(time)

//        orderIds = orderIds.slice(0,1)

        // rdd

        if (orderIds != null && orderIds.size > 0) {


//          println("orderIds.size=" + orderIds.size)
          sc.parallelize(orderIds).repartition(params.partNums).foreachPartition(
            partitionOfRecords => SparseFMWithFTRLApp.runTest(params.featureModelId, params.isCtr, partitionOfRecords, params.sampleRatio, params.isReplay,params.partNums)
          )
        }


        // train
      }

    }
    catch {
      case ex: Exception => logger.error(ex)
    }


  }


}

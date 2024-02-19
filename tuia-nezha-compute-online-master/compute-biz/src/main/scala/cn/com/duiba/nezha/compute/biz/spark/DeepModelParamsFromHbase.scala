package cn.com.duiba.nezha.compute.biz.spark

import cn.com.duiba.nezha.compute.biz.bo.{SampleBo, SyncBo}
import cn.com.duiba.nezha.compute.core.CollectionUtil
import cn.com.duiba.nezha.compute.core.enums.DateStyle
import cn.com.duiba.nezha.compute.core.util.DateUtil
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

import scala.collection.mutable.ArrayBuffer

object DeepModelParamsFromHbase {

  Logger.getLogger("org").setLevel(Level.ERROR)

  def run(featureModelId: String, onLineModelId: String, parNums: Int, isCtr: Boolean, pathProfix: String, isLocal: Boolean, isSave: Boolean, sampleRatio: Double): Unit = {
    val logger = Logger.getLogger(DeepModelParamsFromHbase.getClass)


    val appName = "dlm_params_hbase_fm_" + featureModelId + "om_" + onLineModelId
    // 1.初始spark context

    // 1.初始spark context
    println("init spark context ...,appName= " + appName)

    var sparkConf = new SparkConf().setAppName(appName).setMaster("local[1]")
    if (!isLocal) {
      sparkConf = new SparkConf().setAppName(appName) // 集群运行模式，读取spark集群的环境变量

    }
    val sc = new SparkContext(sparkConf) // 获取 sc

    import collection.JavaConversions._
    try {
      val date = DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD)
      //val time = DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS)
      val time = date + " 00:00:01"
      var minuteList = new ArrayBuffer[String]()
      for (j <- 1 to 60 * 24) {
        val minuteSubList = SampleBo.getTimeIntervalWithRatio(time, -j, sampleRatio)

        minuteList ++= minuteSubList
      }

      // read order ids


      val orderIds = sc.parallelize(minuteList).repartition(parNums).mapPartitions(
        partitionOfRecords => {
          val sList = SampleBo.getOrderList(CollectionUtil.toList(partitionOfRecords))
          sList.toIterator
        }
      )

      val samples = orderIds.mapPartitions(
        partitionOfRecords => {
          val sList = SampleBo.getSampleStrByOrderIdList(isCtr, CollectionUtil.toList(partitionOfRecords))
          val ps = SyncBo.getFeatureCode(featureModelId, sList.toList)
          ps.toIterator
        }
      )

      if (isSave) {
        val path = pathProfix + date + "/" + onLineModelId + "/"
        println("sava sample file at path" + path)
        samples.repartition(1).saveAsTextFile(path)
      } else {
        samples.count()
      }


    }

    catch {
      case ex: Exception => logger.error(ex)
    }


  }


}

package cn.com.duiba.nezha.compute.biz.spark

import cn.com.duiba.nezha.compute.biz.utils.hive.HiveUtil
import cn.com.duiba.nezha.compute.core.enums.DateStyle
import cn.com.duiba.nezha.compute.core.util.DateUtil
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

object DeepModelParamsFromHive {

  Logger.getLogger("org").setLevel(Level.ERROR)

  def run(featureModelId: String, onLineModelId: String, parNums: Int): Unit = {
    val logger = Logger.getLogger(PsModelTest.getClass)


    val appName = "dlm_params_hive_fm_" + featureModelId + "om_" + onLineModelId
    // 1.初始spark context

    // 1.初始spark context
    println("init spark context ...,appName= " + appName)
    SparkSession.builder().enableHiveSupport()


    val sc = SparkSession
      .builder()
      .appName(appName)
      .enableHiveSupport()
      .getOrCreate()


    try {
      val date = DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD)
      val tableName = "logs.dwd_nezha_feature_log_di"
      val data = HiveUtil.selectTableWithDt(tableName, date, sc)
      val sample = data.map(row => row.getAs[String]("json"))
//      DeepModelApp.run(sample, featureModelId, onLineModelId, parNums, date)
    }
    catch {
      case ex: Exception => logger.error(ex)
    }


  }


}

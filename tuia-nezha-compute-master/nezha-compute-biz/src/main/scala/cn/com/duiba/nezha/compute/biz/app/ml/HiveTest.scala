package cn.com.duiba.nezha.compute.biz.app.ml

import cn.com.duiba.nezha.compute.api.enums.{ModelKeyEnum, RunEnvEnum}
import cn.com.duiba.nezha.compute.api.point.Point.ModelReplay
import cn.com.duiba.nezha.compute.biz.save.ReplayerSave

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}


/**
 * Created by pc on 2016/12/14.
 */
object HiveTest {

//  Logger.getLogger("org").setLevel(Level.ERROR)

  //  var logger = LoggerFactory.getLogger(AdvertCTRLR.getClass)

  def main(args:Array[String]): Unit = {

    // 1.初始spark context
    println("init spark context ... ")
    //    System.setProperty("spark.default.parallelism", "10")
    var conf = new SparkConf().setAppName("FM").setMaster("local[3]") // 本地运行模式，读取本地的spark主目录

    val sc = new SparkContext(conf) // 获取 sc
    val hiveContext = new org.apache.spark.sql.hive.HiveContext(sc)
    import hiveContext.implicits._
//    hiveContext.sql("use tmp")
    val a= 1 to 100
    val data = sc.parallelize(a).map(x=>ModelReplay(x+"",x+0.1))
    val modelKey =ModelKeyEnum.LR_CVR_MODEL_v004.getIndex
    val dt="2017-08-08"
    ReplayerSave.save(data,modelKey,dt)
  }


}

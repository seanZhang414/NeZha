package cn.com.duiba.nezha.compute.biz.app.init


import cn.com.duiba.nezha.compute.biz.load.DataLoader
import cn.com.duiba.nezha.compute.common.params.Params
import Params.AdvertLogParams
import cn.com.duiba.nezha.compute.biz.server.process.UserInfoLandingPageInitProcessServer
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}


/**
 * Created by pc on 2016/12/14.
 */
object LandingPageFeatureInit {

//  Logger.getLogger("org").setLevel(Level.ERROR)

  //  var logger = LoggerFactory.getLogger(AdvertCTRLR.getClass)

  def run(inputPath:String): Unit = {

    // 1.初始spark context
    println("init spark context ... ")
    val defaultParams = AdvertLogParams()
//    val conf = new SparkConf().setAppName("LandingPageFeatureInit").setMaster("local[3]") // 本地运行模式，读取本地的spark主目录
     val conf = new SparkConf().setAppName("LandingPageFeatureInit") // 集群运行模式，读取spark集群的环境变量

    val sc = new SparkContext(conf) // 获取 sc

    // 3.加载数据
    val data = DataLoader.dataLoad(sc,inputPath).repartition(300).cache()
    println("data.count="+data.count())

    data.foreachPartition(partitionOfRecords=>
      UserInfoLandingPageInitProcessServer.getInstance().run(partitionOfRecords,2L,"init", defaultParams)
    )


  }


}

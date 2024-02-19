package cn.com.duiba.nezha.compute.common

import cn.com.duiba.nezha.compute.biz.load.DataLoader
import cn.com.duiba.nezha.compute.biz.server.process.UserInfoLandingPageInitProcessServer
import cn.com.duiba.nezha.compute.common.params.Params.ModelParams

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by jiali on 2017/11/7.
  */
package object ParamSearch {

  Logger.getLogger("org").setLevel(Level.ERROR)

  //var logger = LoggerFactory.getLogger(ParamSearch.getClass)

  def run(inputPath:String): Unit = {

    val conf = new SparkConf().setAppName("ParamSearch")

    val sc = new SparkContext(conf)


    val data = DataLoader.dataLoad(sc,inputPath).repartition(300).cache()

    val adScoreMap = Map()

    data.foreachPartition(partitionOfRecords=> partitionOfRecords.toString().split("\001"))


  }

}

package cn.com.duiba.nezha.compute.mllib.test

import cn.com.duiba.nezha.compute.api.point.Point.LabeledSPoint
import cn.com.duiba.nezha.compute.mllib.algorithm.SparseFM
import cn.com.duiba.nezha.compute.mllib.evaluater.ClassifierEvaluater
import cn.com.duiba.nezha.compute.mllib.util.SparseDate
import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by pc on 2017/6/21.
 */
object Test {
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.ERROR)
    // 1.初始spark context
    println("init spark context ... ")
    //    System.setProperty("spark.default.parallelism", "10")
    var conf = new SparkConf().setAppName("AdvertCTRLR").setMaster("local[3]") // 本地运行模式，读取本地的spark主目录


    val sc = new SparkContext(conf) // 获取 sc
    val batchWeightArray = Array.tabulate[Double](100.toInt)(i=>1.0/100)


    println("batchWeightArray = "+batchWeightArray.toList)


    val s = 11

println("Math.round(11.5)="+Math.round(11.5))

  }


}

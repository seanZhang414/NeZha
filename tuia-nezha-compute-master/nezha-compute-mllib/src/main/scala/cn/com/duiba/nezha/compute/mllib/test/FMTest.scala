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
object FMTest {
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.ERROR)
    // 1.初始spark context
    println("init spark context ... ")
    //    System.setProperty("spark.default.parallelism", "10")
    var conf = new SparkConf().setAppName("AdvertCTRLR").setMaster("local[3]") // 本地运行模式，读取本地的spark主目录


    val sc = new SparkContext(conf) // 获取 sc


    // Number of data points
    val N = 50000
    // Number of dimensions
    val D = 50
    val F = 0
    val R = 3.0
    // Scaling factor
    val a = 0.6
    val r2 = 0.01
    val r1 = 0.01
    val threshold = 0.5




    val training_dataRdd = sc.parallelize( SparseDate.generateData2(D, N, R, 42)).persist()


    val model = new SparseFM()
      .setLearningRate(0.025)
      .setAdRate(0.7)
      .setMtRate(0.7)
      .setBeta1(0.9)
      .setBeta2(0.999)
      .setDeltaThreshold(0.000001)
      .setMinIterations(1)
      .setMaxIterations(4)
      .setBatchSize(500)
      .setFactorNums(3)
      .setReg1(0.000001)
      .setReg2(0.000001)
      .runADSGD(training_dataRdd)

    val fm_params = model.getFMParams

//    println("w0= " + fm_params.w0)
//    println("w= " + fm_params.w)
//    println("v= " + fm_params.v)


    model.clearThreshold()

    val test_dataRdd = sc.parallelize(SparseDate.generateData2(D, 20000, R, 40))

//        val test_dataRdd = training_dataRdd

    val pAndL = test_dataRdd.map(p => {
      model.predictPoint(p)
    })

    ClassifierEvaluater.calMulticlassMetrics(pAndL, 0.5)

    val predictionAndLabels2 = test_dataRdd.map { case (LabeledSPoint(features, label)) =>
      (model.predict(features), label)
    }.cache()

//    predictionAndLabels2.foreach(println)
    //使用了一个BinaryClassificationMetrics来评估
    val metrics2 = new BinaryClassificationMetrics(predictionAndLabels2)
    val auRoc = ClassifierEvaluater.calAuROC(metrics2)

    val params =  model.params
    println("params.w0 = "+params)





  }


}

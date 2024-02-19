package cn.com.duiba.nezha.compute.mllib.test

import cn.com.duiba.nezha.compute.api.point.Point
import cn.com.duiba.nezha.compute.mllib.evaluater.ClassifierEvaluater
import cn.com.duiba.nezha.compute.mllib.util.SparseDate
import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by pc on 2017/6/21.
 */
object LRTest {
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.ERROR)
    // 1.初始spark context
    println("init spark context ... ")
    //    System.setProperty("spark.default.parallelism", "10")
    var conf = new SparkConf().setAppName("AdvertCTRLR").setMaster("local[3]") // 本地运行模式，读取本地的spark主目录


    val sc = new SparkContext(conf) // 获取 sc


    // Number of data points
    val N = 100000
    // Number of dimensions
    val D = 50
    val F = 5
    val R = 3.0
    // Scaling factor




    val training_data = SparseDate.generateData3(D, N, R, 42)

    val training_dataRdd = sc.parallelize(training_data).cache()

    val training_data2 = SparseDate.generateData2(D, N, R, 42)
    val training_dataRdd2 = sc.parallelize(training_data2).map(s=>LabeledPoint(s.y,s.x)).cache()

    val model = new LogisticRegressionWithLBFGS()
      .setNumClasses(2)
      .run(training_dataRdd2)


    val fm_params = model.weights

    println("fm_params " + fm_params)



    model.clearThreshold()

    val test_dataRdd = sc.parallelize(SparseDate.generateData3(D, 10000, R, 42))

    val pAndL = test_dataRdd.map(p => {
      (model.predict(p.features),p.label)
    })

    ClassifierEvaluater.calMulticlassMetrics(pAndL, 0.5)

    val predictionAndLabels2 = test_dataRdd.map { p  =>
      (model.predict(p.features), p.label)
    }.cache()

    //使用了一个BinaryClassificationMetrics来评估
    val metrics2 = new BinaryClassificationMetrics(predictionAndLabels2)
    val auRoc = ClassifierEvaluater.calAuROC(metrics2)


    

  }


}

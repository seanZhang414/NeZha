package cn.com.duiba.nezha.compute.mllib.evaluater

import breeze.linalg.{DenseMatrix => BDM, DenseVector => BDV}
import org.apache.spark.mllib.evaluation.{BinaryClassificationMetrics, MulticlassMetrics}
import org.apache.spark.rdd.RDD

/**
 * Created by pc on 2016/11/22.
 */
object ClassifierEvaluater {





  /**
   * 二分类混淆矩阵
   *
   */
  def calMulticlassMetrics(predictionAndLabels: RDD[(Double, Double)], threshold: Double): Unit = {
    val pAndLables = predictionAndLabels.map { case (pre, lable) => {
      (if (pre > threshold) 1.0 else 0.0, lable)
    }
    }
    val metrics = new MulticlassMetrics(pAndLables)
    println(s"Performance with threshold : --- $threshold ---")
    println(s"matrix = \n${metrics.confusionMatrix}")
    println(s"recall(1.0) = ${metrics.recall(1.0)}")
    println(s"precision(1.0) = ${metrics.precision(1.0)}")
    println(s"recall(0.0) = ${metrics.recall(0.0)}")
    println(s"precision(0.0) = ${metrics.precision(0.0)}")
    println(s"f1 = ${fScore(metrics.recall(1.0), metrics.precision(1.0), 1.0)}")
    println(s"")
  }

  /**
   * 二分类,预测结果查看
   *
   */
  def showPredictionAndLabels(predictionAndLabels: RDD[(List[String], Double, Double)]): Unit = {

    val num = predictionAndLabels.count
    predictionAndLabels.sample(false, Math.min(1.0, Math.round(100 / num)), 0).collect().map(x => {
      println(x._1 + ":" + x._2 + "-->" + x._3)
    })
  }


  /**
   * 阈值与分类精度
   *
   */
  def precisionByThreshold(metrics: BinaryClassificationMetrics): Unit = {

    // Recall by threshold
    val precision = metrics.precisionByThreshold

    val precisionNum = precision.count

    precision.sample(false, Math.min(1.0, Math.round(100 / precisionNum)), 0).foreach { case (t, r) =>
      println(s"Threshold: $t, Precision: $r")
    }
  }

  /**
   * 阈值与分类召回
   *
   */
  def recallByThreshold(metrics: BinaryClassificationMetrics): Unit = {

    // Recall by threshold
    val recall = metrics.recallByThreshold

    val recallNum = recall.count

    recall.sample(false, Math.min(1.0, Math.round(100 / recallNum)), 0).foreach { case (t, r) =>
      println(s"Threshold: $t, Recall: $r")
    }
  }

  /**
   * 阈值与分类f1值
   *
   */
  def fMeasureByThreshold(metrics: BinaryClassificationMetrics): Unit = {

    //the beta factor in F-Measure computation.
    //beta 表示概率的阈值
    val f1Score = metrics.fMeasureByThreshold

    val f1ScoreNum = f1Score.count

    f1Score.sample(false, Math.min(1.0, Math.round(100 / f1ScoreNum)), 0).foreach { case (t, f) =>
      println(s"Threshold: $t, F-score: $f, Beta = 1")
    }

    val beta = 0.5
    val fScore = metrics.fMeasureByThreshold(beta)

    val fScoreNum = fScore.count

    fScore.sample(false, Math.min(1.0, Math.round(100 / fScoreNum)), 0).foreach { case (t, f) =>
      println(s"Threshold: $t, F-score: $f, Beta = 0.5")
    }
  }

  /**
   * PRC曲线及累计面积
   *
   */
  def calAuPRC(metrics: BinaryClassificationMetrics): Double = {

    // Precision-Recall Curve
    val PRC = metrics.pr

    // AUPRC 精度-召回曲线下的面积
    val auPRC = metrics.areaUnderPR
    println("Area under precision-recall curve = " + auPRC)

    auPRC
  }

  /**
   * ROC曲线及累计面积
   *
   */
  def calAuROC(metrics: BinaryClassificationMetrics): Double = {

    // ROC Curve
    val ROC = metrics.roc
    //    val x = ROC.map(_._1).collect()
    //    val y = ROC.map(_._2).collect()
    //
    //    val f = Figure()
    //    val p = f.subplot(0)
    //
    //    p += plot(x, y)
    //
    //    p.xlabel = "false positive rate"
    //    p.ylabel = "true positive rate"
    //

    // AUROC ROC曲线下面的面积 又称AUC
    val auROC = metrics.areaUnderROC

    println("Area under ROC = " + auROC)

    auROC
  }

  /**
   * F值计算
   *

   * @return
   */
  def fScore(recall: Double, precision: Double, fID: Double): Double = {
    (1 + fID * fID) * ((recall * precision) / (recall + precision))
  }

  //
  def signLabel(w: Double, threshold: Double): Double = {
    if (w >= threshold) 1.0 else -1.0
  }

}
package cn.com.duiba.nezha.compute.mllib.evaluate

import cn.com.duiba.nezha.compute.core.enums.DateStyle
import cn.com.duiba.nezha.compute.core.util.{DataUtil, DateUtil}
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.linalg.Vectors

import scala.collection.mutable.ArrayBuffer

case class Loss(logloss: Double, rmse: Double, rig: Double, auc: Double)

class Evaluater {
  var logLossAccValue = 0.0
  var rmseAccValue = 0.0
  var trainAccCnt = 0
  var trainAccPCnt = 0
  var preLevelPmap: Map[Long, Int] = Map()
  var preLevelNmap: Map[Long, Int] = Map()
  var preLevelCorrectAccMap: Map[Long, Double] = Map()
  var preLevelAccMap: Map[Long, Double] = Map()


  var plArray: ArrayBuffer[(Double, Double)] = new ArrayBuffer[(Double, Double)]()

  def add(label: Double, predVal: Double, correctFactor: Double) = Evaluater.add(label, predVal, correctFactor, this)

  def getLoss(): Loss = Evaluater.getLoss(this)

  def getLevelMap(key: String): Map[Int, Double] = Evaluater.getPreLevel(key, this)

  def getPreLevel(predVal: Double) = Evaluater.getLevel(predVal)

  def print() = Evaluater.print(this)


}

object Evaluater {

  def print(evaluate: Evaluater): Unit = {

    if (evaluate.trainAccCnt > 500) {
      val eloss = evaluate.getLoss()
      println(s"${DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS)} Train Evaluater Info train.Cnt =${evaluate.trainAccCnt},pCnt=${evaluate.trainAccPCnt},auc =${eloss.auc},rmse =${eloss.rmse},logloss=${eloss.logloss},rig =${eloss.rig}")

    }

  }

  def add(label: Double, predVal: Double, correctFactor: Double, evaluater: Evaluater) = {
    val logValue = DataUtil.formatdouble(-label * math.log(predVal + 0.000001) - (1.0 - label) * math.log(1.0 - predVal + 0.000001), 4)
    evaluater.logLossAccValue += logValue

    val rmseValue = DataUtil.formatdouble(math.pow((predVal - label), 2), 4)
    evaluater.rmseAccValue += rmseValue

    evaluater.trainAccCnt += 1
    val preLevel: Long = getLevel(predVal)

    if (label > 0.5) {
      evaluater.trainAccPCnt += 1
      val pCnt = evaluater.preLevelPmap.getOrElse(preLevel, 0)
      evaluater.preLevelPmap += (preLevel -> (pCnt + 1))
    } else {
      val nCnt = evaluater.preLevelNmap.getOrElse(preLevel, 0)
      evaluater.preLevelNmap += (preLevel -> (nCnt + 1))

    }
    val preAcc = evaluater.preLevelAccMap.getOrElse(preLevel, 0.0)
    evaluater.preLevelAccMap += (preLevel -> (preAcc + predVal))

    val factorAcc = evaluater.preLevelCorrectAccMap.getOrElse(preLevel, 0.0)
    evaluater.preLevelCorrectAccMap += (preLevel -> (factorAcc + correctFactor))


    evaluater.plArray += ((label, predVal))

    //


  }


  def getAuc(plArray: ArrayBuffer[(Double, Double)], pCnt: Int, nCnt: Int): Double = {
    val plArray2 = plArray.sortWith(_._2 > _._2)

    val cnt = pCnt + nCnt

    var pRankCnt = 0.0
    for (i <- 0 to cnt - 1) {
      val rank = cnt - i
      val plVal = plArray2(i)

      if (plVal._1 > 0.5) {
        pRankCnt += (rank + 0.0)
      }

    }
    //    println(plArray2)
    //    println(s"(${pRankCnt} - ${pCnt} * (${pCnt} + 1) / 2) / (${pCnt} * ${nCnt})")
    DataUtil.formatDouble((pRankCnt - pCnt * (pCnt + 1) / 2) / (pCnt * nCnt), 4)
  }

  def getLoss(evaluater: Evaluater): Loss = {

    val logLoss = DataUtil.formatdouble(evaluater.logLossAccValue / evaluater.trainAccCnt, 4)

    val rmse = DataUtil.formatdouble(math.sqrt(evaluater.rmseAccValue / evaluater.trainAccCnt), 4)

    val pRatio = (evaluater.trainAccPCnt + 0.0) / evaluater.trainAccCnt
    val hp = -pRatio * math.log(pRatio + 0.000001) - (1 - pRatio) * math.log(1 - pRatio)

    val rig = DataUtil.formatdouble(1 - logLoss / (hp + 0.000001), 4)

    val auc = getAuc(evaluater.plArray, evaluater.trainAccPCnt, evaluater.trainAccCnt - evaluater.trainAccPCnt)
    Loss(logLoss, rmse, rig, auc)
  }


  def getPreLevel(key: String, evaluater: Evaluater): Map[Int, Double] = {

    var retMap: Map[Int, Double] = Map()

    try {
      for (i <- 0 to 100) {
        val level = i + 0L
        val pCnt = evaluater.preLevelPmap.getOrElse(level, 0)
        val nCnt = evaluater.preLevelNmap.getOrElse(level, 0)
        val pAcc = evaluater.preLevelAccMap.getOrElse(level, 0.0)
        val fAcc = evaluater.preLevelCorrectAccMap.getOrElse(level, 0.0)

        val cnt = pCnt + nCnt
        val sCtr = DataUtil.formatDouble(pCnt / (cnt + 0.01), 4)
        val pCtr = DataUtil.formatDouble(pAcc / (cnt + 0.01), 4)
        var gapRatio = 1.0
        var factorRatio = DataUtil.formatDouble(fAcc / (cnt + 0.01), 4)

        if (cnt > 30) {
          gapRatio = DataUtil.formatDouble(sCtr / pCtr, 4)

          retMap += (level.toInt -> gapRatio)
          println(s"key=${key},pLevel=${level},cnt=${cnt},sCtr=${sCtr},pCtr=${pCtr},gapRatio=${gapRatio},factorAvg=${factorRatio}")
        }

      }
    } catch {
      case ex: Exception => ex.printStackTrace()
    }


    retMap
  }


  def getLevel(predVal: Double): Long = {
    //    if (predVal <= 0.1) {
    //      math.round(predVal * 100 / 2)
    //    } else {
    //      math.round(predVal * 100 / 4) + 3
    //    }

    math.round(predVal * 100 / 4)

  }

  def getCorrectFactor(predVal: Double, preLevelU: Map[Int, Double], preLevelD: Map[Int, Double]): Double = {
    val level = getLevel(predVal: Double)
    val lu = preLevelU.getOrElse(level.toInt, 1.0)
    val ld = preLevelD.getOrElse(level.toInt, 1.0)
    (lu + 0.01) / (ld + 0.01)
  }

  def getCorrectVal(predVal: Double, level: Double): Double = {
    val ret = predVal * level
    math.min(ret, 1.0)
  }

  def getCorrectVal(predVal: Double, preLevelU: Map[Int, Double], preLevelD: Map[Int, Double]): (Double, Double) = {
    val level = getCorrectFactor(predVal: Double, preLevelU: Map[Int, Double], preLevelD: Map[Int, Double])
    (getCorrectVal(predVal: Double, level: Double), level)
  }
}

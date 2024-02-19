package cn.com.duiba.nezha.compute.mllib.algorithm

import cn.com.duiba.nezha.compute.api.point.Point
import Point.{FMParams, LabeledSPoint}
import cn.com.duiba.nezha.compute.mllib.model.SparseFMModel
import cn.com.duiba.nezha.compute.mllib.optimizing.ad.SparseFMADSGDOptimizer
import cn.com.duiba.nezha.compute.mllib.optimizing.adam.SparseFMAdamSGDOptimizer
import cn.com.duiba.nezha.compute.mllib.optimizing.gd.SparseFMGDOptimizer
import cn.com.duiba.nezha.compute.mllib.optimizing.mt.SparseFMMTSGDOptimizer
import cn.com.duiba.nezha.compute.mllib.optimizing.sgd.SparseFMSGDOptimizer
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, DataFrame}
import org.apache.spark.storage.StorageLevel

/**
 * Logistic regression based classification.
 */

class SparseFM extends GeneralizedAlgorithm[SparseFMModel] with Serializable {
  private var learningRate: Double = 0.1
  private var mtRate: Double = 0.5
  private var adRate: Double = 0.5
  private var reg1: Double = 0.0
  private var reg2: Double = 0.0

  private var beta1: Double = 0.99
  private var beta2: Double = 0.99


  private var factorNums: Int = 3
  private var maxIterations: Int = 2
  private var minIterations: Int = 0
  private var deltaThreshold: Double = 0.00001
  private var batchSize: Int = 3000


  def getLearningRate: Double = learningRate

  def setLearningRate(learningRate: Double): this.type = {
    this.learningRate = learningRate
    this
  }

  def getMtRate: Double = mtRate

  def setMtRate(mtRate: Double): this.type = {
    this.mtRate = mtRate
    this
  }

  def getAdRate: Double = adRate

  def setAdRate(adRate: Double): this.type = {
    this.adRate = adRate
    this
  }


  def getReg1: Double = reg1

  def setReg1(reg1: Double): this.type = {
    this.reg1 = reg1
    this
  }

  def getReg2: Double = reg2

  def setReg2(reg2: Double): this.type = {
    this.reg2 = reg2
    this
  }



  def getBeta1: Double = beta1

  def setBeta1(beta1: Double): this.type = {
    this.beta1 = beta1
    this
  }

  def getBeta2: Double = beta2

  def setBeta2(beta2: Double): this.type = {
    this.beta2 = beta2
    this
  }




  def getMaxIterations: Int = maxIterations

  def setMaxIterations(maxIterations: Int): this.type = {
    this.maxIterations = maxIterations
    this
  }

  def getMinIterations: Int = minIterations

  def setMinIterations(minIterations: Int): this.type = {
    this.minIterations = minIterations
    this
  }

  def getDeltaThreshold: Double = deltaThreshold

  def setDeltaThreshold(deltaThreshold: Double): this.type = {
    this.deltaThreshold = deltaThreshold
    this
  }


  def getFactorNums: Int = factorNums

  def setFactorNums(factorNums: Int): this.type = {
    this.factorNums = factorNums
    this
  }


  def getBatchSize: Int = batchSize

  def setBatchSize(batchSize: Int): this.type = {
    this.batchSize = batchSize
    this
  }


  def run(data: RDD[LabeledSPoint]): SparseFMModel = {
    runSGD(data: RDD[LabeledSPoint])
  }

  def runGD(data: RDD[LabeledSPoint]): SparseFMModel = {
    if (data.getStorageLevel == StorageLevel.NONE) {
      logWarning("The input data is not directly cached, which may hurt performance if its"
        + " parent RDDs are also uncached.")
    }
    SparseFM.trainGD(data, factorNums, learningRate, reg1, reg2, maxIterations, minIterations, deltaThreshold)
  }

  def runSGD(data: RDD[LabeledSPoint]): SparseFMModel = {
    if (data.getStorageLevel == StorageLevel.NONE) {
      logWarning("The input data is not directly cached, which may hurt performance if its"
        + " parent RDDs are also uncached.")
    }
    SparseFM.trainSGD(data, batchSize, factorNums, learningRate, reg1, reg2, maxIterations, minIterations, deltaThreshold)
  }

  def runMTSGD(data: RDD[LabeledSPoint]): SparseFMModel = {
    if (data.getStorageLevel == StorageLevel.NONE) {
      logWarning("The input data is not directly cached, which may hurt performance if its"
        + " parent RDDs are also uncached.")
    }
    SparseFM.trainMTSGD(data, batchSize, factorNums, mtRate, learningRate, reg1, reg2, maxIterations, minIterations, deltaThreshold)
  }


  def runADSGD(data: RDD[LabeledSPoint]): SparseFMModel = {
    if (data.getStorageLevel == StorageLevel.NONE) {
      logWarning("The input data is not directly cached, which may hurt performance if its"
        + " parent RDDs are also uncached.")
    }
    SparseFM.trainADSGD(data, batchSize, factorNums, adRate,learningRate, reg1, reg2, maxIterations, minIterations, deltaThreshold)
  }

  def runAdam(data: RDD[LabeledSPoint]): SparseFMModel = {
    if (data.getStorageLevel == StorageLevel.NONE) {
      logWarning("The input data is not directly cached, which may hurt performance if its"
        + " parent RDDs are also uncached.")
    }
    SparseFM.trainAdam(data, batchSize, factorNums, beta1,beta2,learningRate, reg1, reg2, maxIterations, minIterations, deltaThreshold)
  }


  def createModel(fm_params: FMParams): SparseFMModel = {
    SparseFM.createModel(fm_params)
  }


}

object SparseFM {

  def createModel(fm_params: FMParams): SparseFMModel = {
    new SparseFMModel(fm_params)
  }


  def trainGD(data: RDD[LabeledSPoint], F: Int, learningRate: Double, r1: Double, r2: Double, MAX_ITERATIONS: Int, MIN_ITERATIONS: Int, DELTA_THRESHOLD: Double): SparseFMModel = {

    val fm_params: FMParams = SparseFMGDOptimizer.run(data, F, learningRate, r1, r2, MAX_ITERATIONS, MIN_ITERATIONS, DELTA_THRESHOLD)
    createModel(fm_params)
  }

  def trainSGD(data: RDD[LabeledSPoint], batchSize: Int, F: Int, learningRate: Double, r1: Double, r2: Double, MAX_ITERATIONS: Int, MIN_ITERATIONS: Int, DELTA_THRESHOLD: Double): SparseFMModel = {

    val fm_params: FMParams = SparseFMSGDOptimizer.run(data, batchSize, F, learningRate, r1, r2, MAX_ITERATIONS, MIN_ITERATIONS, DELTA_THRESHOLD)
    createModel(fm_params)
  }

  def trainMTSGD(data: RDD[LabeledSPoint], batchSize: Int, F: Int, mtRate: Double, learningRate: Double, r1: Double, r2: Double, MAX_ITERATIONS: Int, MIN_ITERATIONS: Int, DELTA_THRESHOLD: Double): SparseFMModel = {

    val fm_params: FMParams = SparseFMMTSGDOptimizer.run(data, batchSize, F, mtRate, learningRate, r1, r2, MAX_ITERATIONS, MIN_ITERATIONS, DELTA_THRESHOLD)
    createModel(fm_params)
  }

  def trainADSGD(data: RDD[LabeledSPoint], batchSize: Int, F: Int, adRate: Double, learningRate: Double,r1: Double, r2: Double, MAX_ITERATIONS: Int, MIN_ITERATIONS: Int, DELTA_THRESHOLD: Double): SparseFMModel = {

    val fm_params: FMParams = SparseFMADSGDOptimizer.run(data, batchSize, F, adRate, learningRate,r1, r2, MAX_ITERATIONS, MIN_ITERATIONS, DELTA_THRESHOLD)
    createModel(fm_params)
  }
  def trainAdam(data: RDD[LabeledSPoint], batchSize: Int, F: Int, beta1: Double,beta2: Double, learningRate: Double,r1: Double, r2: Double, MAX_ITERATIONS: Int, MIN_ITERATIONS: Int, DELTA_THRESHOLD: Double): SparseFMModel = {

    val fm_params: FMParams = SparseFMAdamSGDOptimizer.run(data, batchSize, F, beta1,beta2, learningRate,r1, r2, MAX_ITERATIONS, MIN_ITERATIONS, DELTA_THRESHOLD)
    createModel(fm_params)
  }

}
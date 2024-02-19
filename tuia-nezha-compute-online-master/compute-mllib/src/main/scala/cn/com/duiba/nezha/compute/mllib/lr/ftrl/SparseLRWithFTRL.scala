package cn.com.duiba.nezha.compute.mllib.lr.ftrl

import cn.com.duiba.nezha.compute.core.model.local.{LocalModel, LocalVector}
import cn.com.duiba.nezha.compute.core.model.ops.VectorOps
import cn.com.duiba.nezha.compute.core.util.MathUtil
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint

import scala.collection.mutable.ArrayBuffer

class SparseLRWithFTRL extends FTRL {


  var localIncrModel: LocalModel = null
  var lossCnt = 0.0
  var trainCnt = 0


  def setLocalIncrModel(localIncrModel: LocalModel): Unit = {
    this.localIncrModel = localIncrModel
  }

  def getLocalIncrModel(): LocalModel = {
    this.localIncrModel
  }

  def train(data: Array[LabeledPoint]): Boolean = SparseLRWithFTRL.train(this, data)

}

object SparseLRWithFTRL {


  def train(sFTRL: SparseLRWithFTRL, data: Array[LabeledPoint]): Boolean = {

    if (data == null) {
      return false
    }

    // 参数解析
    var localZ = sFTRL.localZ
    var localN = sFTRL.localN
    val alpha = sFTRL.alpha
    val beta = sFTRL.beta
    val lambda1 = sFTRL.lambda1
    val lambda2 = sFTRL.lambda2

    // 损失累计

    // init the increment of model for z and n
    var incrementZ: Map[Int, Double] = Map()
    var incrementN: Map[Int, Double] = Map()


    for (labeledPoint <- data) {
      val label = labeledPoint.label
      val feature = labeledPoint.features
      if (feature != null) {

        val featureLV = new LocalVector(feature.toSparse)

        //获取权重
        val weight = FTRL.getWeight(featureLV, localZ, localN, alpha, beta, lambda1, lambda2)

        // 预估
        val predVal = FTRL.predict(featureLV.vector, weight)

        //计算累计损失
        sFTRL.lossCnt += Math.abs(predVal - label)
        sFTRL.trainCnt += 1


        //梯度
        val gradLoss = getGradLoss(featureLV, predVal - label)

        //计算增量
        val (incrZ, incrN) = getIncrementZAndN(gradLoss, weight, localN, localZ, alpha: Double)

        //增量累计
        incrementZ = incrementVector(incrementZ, incrZ)
        incrementN = incrementVector(incrementN, incrN)

        //更新
        localZ = incrementVector(localZ, incrZ)
        localN = incrementVector(localN, incrN)

      }

    }
    sFTRL.setLocalZ(localZ)
    sFTRL.setLocalN(localN)

    val localIncrModel = getLocalModel(sFTRL.dim, incrementZ, incrementN)
    sFTRL.setLocalIncrModel(localIncrModel)
    true
  }


  def searchModel(dim: Int): LocalModel = {
    getLocalModel(dim, Map(), Map())

  }


  def searchModel(data: Array[LabeledPoint]): LocalModel = {

    //1 初始化 构造查询 -本地向量、本地矩阵


    if (data != null && data.length > 0) {

      val dim = data.apply(0).features.size
      searchModel(data, dim)

    } else {
      null
    }
  }

  def searchModel(data: Array[LabeledPoint], dim: Int): LocalModel = {

    //1 初始化 构造查询 -本地向量、本地矩阵

    var searchVectorMap: Map[String, LocalVector] = Map()

    if (data != null) {

      val fArray = data.map(data => data.features.toSparse)

      val searchLocalVector = new LocalVector(VectorOps.toIndexSV(fArray, dim))

      searchVectorMap += (FTRL.w_z -> searchLocalVector)
      searchVectorMap += (FTRL.w_n -> searchLocalVector)

    }
    new LocalModel(Map(), searchVectorMap, Map())
  }

  def getLocalModel(vSize: Int, incrZ: Map[Int, Double], incrN: Map[Int, Double]): LocalModel = {
    val incrZLV = LocalVector.toLocalVector(vSize, incrZ)
    val incrNLV = LocalVector.toLocalVector(vSize, incrN)
    var incrVectorMap: Map[String, LocalVector] = Map()
    incrVectorMap += (FTRL.w_z -> incrZLV)
    incrVectorMap += (FTRL.w_n -> incrNLV)
    new LocalModel(Map(), incrVectorMap, Map())
  }

  // add updateInc to incrementedVec
  def incrementVector(incrementedVec: Map[Int, Double], updateInc: Array[(Int, Double)]): Map[Int, Double] = {

    var vecAddResult: Map[Int, Double] = incrementedVec

    updateInc.foreach { case (fId, fInc) =>
      val oriVal = vecAddResult.getOrElse(fId, 0.0)
      val newVal = oriVal + fInc
      vecAddResult += (fId -> newVal)
    }
    vecAddResult
  }


  def getGradLoss(w: LocalVector,
                  label: Double,
                  feature: LocalVector): Map[Int, Double] = {
    val loss = FTRL.predict(feature, w) - label
    getGradLoss(feature, loss)
  }

  def getGradLoss(feature: LocalVector, loss: Double): Map[Int, Double] = {
    feature.mutiply(loss).toMap()
  }


  def getIncrementZAndN(gradLoss: Map[Int, Double], weight: Map[Int, Double],
                        localN: Map[Int, Double], localZ: Map[Int, Double], alpha: Double): (Array[(Int, Double)], Array[(Int, Double)]) = {
    var zIncrValues = new ArrayBuffer[(Int, Double)]()
    var nIncrValues = new ArrayBuffer[(Int, Double)]()

    for (fId <- gradLoss.keySet) {
      val gOnId = gradLoss.getOrElse(fId, 0.0)
      val wOnId = weight.getOrElse(fId, 0.0)
      val nOnId = localN.getOrElse(fId, 0.0)
      val zOnId = localZ.getOrElse(fId, 0.0)
      val sigmaOnId = updateSigmaOnId(gOnId, nOnId, alpha)
      val incrZOnId = incrementZOnId(gOnId, sigmaOnId, wOnId)
      val incrNOnId = incrementNOnId(gOnId)

      zIncrValues.append((fId, incrZOnId))
      nIncrValues.append((fId, incrNOnId))
    }
    (zIncrValues.toArray, nIncrValues.toArray)
  }


  //
  def updateSigmaOnId(gOnId: Double, nOnId: Double, alpha: Double): Double = {
    Math.sqrt(nOnId + gOnId * gOnId) - Math.sqrt(nOnId) / (alpha + 0.00000001)
  }

  def incrementZOnId(gOnId: Double, sigmaOnId: Double, weightOnId: Double): Double = {
    gOnId - sigmaOnId * weightOnId
  }

  def incrementNOnId(gOnId: Double): Double = {
    gOnId * gOnId
  }

}
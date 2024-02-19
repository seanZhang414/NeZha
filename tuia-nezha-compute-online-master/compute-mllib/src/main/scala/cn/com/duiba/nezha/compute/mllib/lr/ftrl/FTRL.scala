package cn.com.duiba.nezha.compute.mllib.lr.ftrl

import cn.com.duiba.nezha.compute.core.model.local.{LocalModel, LocalVector}
import cn.com.duiba.nezha.compute.core.util.MathUtil
import org.apache.spark.mllib.linalg.{SparseVector, Vectors}

import scala.collection.mutable.ArrayBuffer


class FTRL extends Serializable {
  var alpha: Double = 1.0
  var beta: Double = 1.0
  var lambda1: Double = 1.0
  var lambda2: Double = 1.0
  var dim: Int = 1

  var localModel: LocalModel = null

  var localZ: Map[Int, Double] = Map()
  var localN: Map[Int, Double] = Map()

  var localW:Map[Int,Double] =Map()

  def setAlpha(alpha: Double): this.type = {
    this.alpha = alpha
    this
  }

  def setBeta(beta: Double): this.type = {
    this.beta = beta
    this
  }

  def setLambda1(lambda1: Double): this.type = {
    this.lambda1 = lambda1
    this
  }

  def setLambda2(lambda2: Double): this.type = {
    this.lambda2 = lambda2
    this
  }

  def setDim(dim: Int): this.type = {
    this.dim = dim
    this
  }

  def setLocalZ(localZ: Map[Int, Double]): this.type = {
    this.localZ = localZ
    this
  }


  def setLocalN(localN: Map[Int, Double]): this.type = {
    this.localN = localN
    this
  }

  def setLocalModel(localModel: LocalModel): this.type = {
    setLocalZ(localModel.getVector(FTRL.w_z).toMap())
    setLocalN(localModel.getVector(FTRL.w_n).toMap())
    this
  }


  def predict(feature: LocalVector): Double = FTRL.predict(feature, this)


}

case class FTRLHyperParams(alpha: Double = 1.0,
                           beta: Double = 1.0,
                           lambda1: Double = 1.0,
                           lambda2: Double = 1.0,
                           dim: Int = 1)

object FTRL {
  val w_z: String = "w_z"
  val w_n: String = "w_n"

  def predict(feature: LocalVector, fTRL: FTRL): Double = {
    predict(feature, fTRL.localZ, fTRL.localN, fTRL.alpha, fTRL.beta, fTRL.lambda1, fTRL.lambda2)
  }

  def predict(feature: LocalVector, localZ: Map[Int, Double], localN: Map[Int, Double], alpha: Double, beta: Double,
              lambda1: Double, lambda2: Double): Double = {
    val weight = getWeight(feature, localZ, localN, alpha, beta, lambda1, lambda2)
    val wx = feature.dtoMap(weight)
    MathUtil.sigmoid(wx)
  }



  def predict(feature: SparseVector, w: Map[Int, Double]): Double = {
    var ret = 0.0
    if (feature != null) {
      val indices = feature.indices
      for (i <- 0 to indices.length - 1) {
        val idx = indices.apply(i)
        ret += feature.values.apply(i) * w.getOrElse(idx, 0.0)
      }
    }
    MathUtil.sigmoid(ret)
  }

  def predict(feature: LocalVector, w: LocalVector): Double = {
    val wx = feature.dto(w)
    MathUtil.sigmoid(wx)
  }


  def getWeight(feature: LocalVector, localZ: Map[Int, Double], localN: Map[Int, Double], alpha: Double, beta: Double,
                lambda1: Double, lambda2: Double): Map[Int, Double] = {
    // the number of not zero of feature
    val indices = feature.vector.indices
    var values = new ArrayBuffer[Double]()
    var ret: Map[Int, Double] = Map()
    for (i <- 0 to indices.length - 1) {
      val fId = indices.apply(i)
      val zVal = localZ.getOrElse(fId, 0.0)
      val nVal = localN.getOrElse(fId, 0.0)
      val value = updateWeightOnId(zVal, nVal, alpha, beta, lambda1, lambda2)
      ret += (fId -> value)
    }
    ret
  }


  // compute new weight
  def updateWeightOnId(zOnId: Double,
                       nOnId: Double,
                       alpha: Double,
                       beta: Double,
                       lambda1: Double,
                       lambda2: Double): Double = {
    if (Math.abs(zOnId) <= lambda1)
      0.0
    else
      (-1) * (1.0 / (lambda2 + (beta + Math.sqrt(nOnId)) / (alpha + 0.00000001))) * (zOnId - Math.signum(zOnId).toInt * lambda1)
  }

}
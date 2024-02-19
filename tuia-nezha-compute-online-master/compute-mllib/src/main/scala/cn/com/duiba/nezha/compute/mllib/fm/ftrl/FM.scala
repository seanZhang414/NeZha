package cn.com.duiba.nezha.compute.mllib.fm.ftrl

import cn.com.duiba.nezha.compute.core.model.local.{LocalModel, LocalVector}
import cn.com.duiba.nezha.compute.core.util.{DataUtil, MathUtil}
import cn.com.duiba.nezha.compute.milib.ModelCache.ModelCache
import org.apache.spark.mllib.linalg.SparseVector

class FM extends Serializable {

  var dim: Int = 1
  var factorNum: Int = 1

  var w0Local: Double = 0.0

  var vLocal: Map[Int, Map[Int, Double]] = Map()

  var wLocal: Map[Int, Double] = Map()

//  var preLevel: Map[Int, Double] = Map()

  def setDim(dim: Int): this.type = {
    this.dim = dim
    this
  }

  def setFacotrNum(factorNum: Int): this.type = {
    this.factorNum = factorNum
    this
  }


  def setW0Local(local: Double): this.type = {
    this.w0Local = local
    this
  }


  def setWLocal(local: Map[Int, Double]): this.type = {
    this.wLocal = local
    this
  }


  def setVLocal(local: Map[Int, Map[Int, Double]]): this.type = {
    this.vLocal = local
    this
  }

//  def setPreLevel(preLevel: Map[Int, Double]): this.type = {
//    this.preLevel = preLevel
//    this
//  }


  def setModel(localModel: LocalModel): this.type = {

    setW0Local(localModel.getValue(FM.w0))
    setWLocal(localModel.getVector(FM.w).toMap())
    setVLocal(localModel.getMatrix(FM.v).toMap())
    this
  }


  def predict(feature: LocalVector): Double = FM.predict(feature, this)
  def predict(feature: SparseVector): Double = FM.predict(feature, this)

}

object FM {

  val w0 = "w0"
  val w = "w"
  val v = "v"
  val pl ="pl"

  def predict(feature: LocalVector, fm: FM): Double = {
    predict(feature.vector, fm.w0Local, fm.wLocal, fm.vLocal)
  }

  def predict(feature: SparseVector, fm: FM): Double = {
    predict(feature, fm.w0Local, fm.wLocal, fm.vLocal)
  }

  def predict(feature: SparseVector, w0: Double, w: Map[Int, Double], v: Map[Int, Map[Int, Double]]): Double = {
    var ret = 0.0
    var retW0 = w0
    var retW = 0.0
    var retV = 0.0
    if (feature != null) {
      val indices = feature.indices
      val x = feature.values
      for (i <- 0 to indices.length - 1) {
        val fId = indices.apply(i)
        retW += x.apply(i) * w.getOrElse(fId, 0.0)
      }
      for (k <- v.keySet) {
        var k_rp1 = 0.0
        var k_rp2 = 0.0
        val v_k = v.getOrElse(k, Map())
        for (i <- 0 to indices.length - 1) {
          val fId = indices.apply(i)
          val xi = x.apply(i)
          val vif = v_k.getOrElse(fId, 0.0)

          k_rp1 += xi * vif
          k_rp2 += Math.pow(xi * vif, 2)
        }
        retV += 0.5 * (Math.pow(k_rp1, 2) - k_rp2)
      }


    }
//    System.out.println("FM retw0=" + retW0 + ",retw=" + retW + ",retv=" + retV)
    ret = retW0 + retW + retV
    DataUtil.formatdouble(MathUtil.sigmoid(ret), 7)
  }


}
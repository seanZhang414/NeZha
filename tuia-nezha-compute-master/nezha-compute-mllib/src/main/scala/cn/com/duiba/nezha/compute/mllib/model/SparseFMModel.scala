package cn.com.duiba.nezha.compute.mllib.model

import cn.com.duiba.nezha.compute.api.point.Point.{FMModelParams, FMParams, LabeledSPoint}
import cn.com.duiba.nezha.compute.mllib.classification.ClassificationModel
import cn.com.duiba.nezha.compute.mllib.optimizing.FMGD
import org.apache.spark.mllib.linalg._
import org.apache.spark.rdd.RDD

/**
 * Logistic regression based classification.
 */

class SparseFMModel(fmParams: FMParams) extends GeneralizedModel with ClassificationModel with Serializable {
  var numFeatures: Int = -1
  var threshold: Option[Double] = Some(0.5)

  var params: FMModelParams = null

  //  private var fmParams: FMParams = null

  //  def setfmParams(fmParams: FMParams): this.type = {
  //    this.fmParams = fmParams
  //    this
  //  }


  def setThreshold(threshold: Double): this.type = {
    this.threshold = Some(threshold)
    this
  }

  def setParams() = {
    params = FMModelParams(fmParams.w0, fmParams.w.toDense, fmParams.v.toDense)

  }


  def getThreshold: Option[Double] = threshold

  def getFMParams: FMParams = fmParams


  def clearThreshold(): this.type = {
    threshold = None
    this
  }

  def getFMModelParams(): FMModelParams = {
    if (params == null) {
      setParams()
    }
    params
  }

  override def predict(testData: RDD[SparseVector]): RDD[Double] = {
    testData.map(x => SparseFMModel.predict(x, getFMModelParams(), threshold))
  }


  override def predict(sv: SparseVector): Double = {
    SparseFMModel.predict(sv, getFMModelParams(), threshold)
  }

  override def predictPoint(testData: RDD[LabeledSPoint]): RDD[(Double, Double)] = {
    testData.map(p => (SparseFMModel.predict(p.x, getFMModelParams(), threshold), p.y))
  }

  override def predictPoint(point: LabeledSPoint): (Double, Double) = {
    (SparseFMModel.predict(point.x, getFMModelParams(), threshold), point.y)
  }


}

object SparseFMModel {

  //  def predLabel(p: SDataPoint, fm_params: FMParams, threshold: Double): Double = {
  //    ClassifierEvaluater.signLabel(predictPoint(p, fm_params), threshold)
  //  }

  def apply(fm_params: FMParams, vector: SparseVector) = new SparseFMModel(fm_params: FMParams)


  def predict(p: SparseVector, fm_params: FMParams, threshold: Option[Double]): Double = {

    val p_score = FMGD.h(p, fm_params)

    threshold match {
      case Some(t) => if (p_score > t) 1.0 else 0.0
      case None => p_score
    }
  }

  def predict(p: SparseVector, fm_params: FMParams): Double = {
    return predict(p, fm_params, None)
  }

  def predict(p: SparseVector, fm_params: FMModelParams, threshold: Option[Double]): Double = {

    val p_score = FMGD.h(p, fm_params)

    threshold match {
      case Some(t) => if (p_score > t) 1.0 else 0.0
      case None => p_score
    }
  }


  def predict(p: SparseVector, fm_params: FMModelParams): Double = {
    return predict(p, fm_params, None)
  }

}
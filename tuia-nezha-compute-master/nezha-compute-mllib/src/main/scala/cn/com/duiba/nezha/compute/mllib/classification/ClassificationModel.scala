package cn.com.duiba.nezha.compute.mllib.classification

import cn.com.duiba.nezha.compute.api.point.Point
import Point.LabeledSPoint
import org.apache.spark.mllib.linalg.SparseVector
import org.apache.spark.rdd.RDD

/**
 * Created by pc on 2017/6/20.
 */
trait ClassificationModel  extends Serializable {

  def predict(testData: RDD[SparseVector]): RDD[Double]

  def predict(testData: SparseVector): Double

  def predictPoint(testData: RDD[LabeledSPoint]): RDD[(Double,Double)]
  def predictPoint(testData: LabeledSPoint): (Double,Double)


}

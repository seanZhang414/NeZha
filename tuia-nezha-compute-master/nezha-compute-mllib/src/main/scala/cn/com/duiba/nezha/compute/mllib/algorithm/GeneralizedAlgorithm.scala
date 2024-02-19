package cn.com.duiba.nezha.compute.mllib.algorithm

import cn.com.duiba.nezha.compute.api.point.Point
import Point.{FMParams, LabeledSPoint}
import cn.com.duiba.nezha.compute.api.point.Point
import cn.com.duiba.nezha.compute.mllib.model.GeneralizedModel
import org.apache.spark.Logging
import org.apache.spark.rdd.RDD

/**
 * Created by pc on 2017/6/20.
 */
abstract class GeneralizedAlgorithm[M <: GeneralizedModel]
  extends Logging with Serializable {

  def run(data: RDD[LabeledSPoint]): M

  def createModel(fm_params: FMParams): M
}
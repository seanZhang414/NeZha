package cn.com.duiba.nezha.compute.api.point

import org.apache.spark.mllib.linalg.{Matrix, SparseMatrix, SparseVector, Vector}

/**
 * Created by pc on 2017/6/16.
 */
object Point {
  case class LabeledSPoint(x: SparseVector, y: Double)
  case class PredictionAndLabel(py: Double, y: Double)

  case class FMGradParams(grad_w0: Double, grad_w: SparseVector, grad_v: SparseMatrix)
  case class FMParams(w0: Double, w: SparseVector, v: SparseMatrix)

  case class FMModelParams(w0: Double, w: Vector, v: Matrix)

  case class ModelBaseInfo(key:String,idList:List[String],locMap:Map[String,Int],idCollectionList:List[String],iteNum:Int=4)

  case class ModelReplay(order_id:String,pre:Double)

  case class ModelFeature(feature_id:String,category:String,feature_category_size:Long,index:Long,sub_index:Long,intercept:Double,weight:Double,factor:String)

  case class ModelReplayWithMap(order_id:String,featuremap:String,pre:Double)
}

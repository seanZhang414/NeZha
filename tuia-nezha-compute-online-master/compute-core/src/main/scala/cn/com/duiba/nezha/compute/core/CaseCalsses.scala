package cn.com.duiba.nezha.compute.core

import org.apache.spark.mllib.linalg.SparseVector

case class LabeledPoint(label:Double,feature:SparseVector)

case class LabeledFeature(label:Double,feature:String)
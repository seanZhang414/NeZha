package cn.com.duiba.nezha.compute.mllib.util

import breeze.linalg.{DenseMatrix, DenseVector}
import org.apache.spark.rdd.RDD


/**
 * Created by pc on 2017/6/14.
 */
object FMUtil {

  case class DataPoint(x: DenseVector[Double], y: Double)
  //
  case class Prdlabel(py: Double, y: Double)

  case class GradParams(w0: Double, w: DenseVector[Double], v: DenseMatrix[Double])


  //
  def sigmoid(inx: Double): Double = {
    1 / (1 + math.exp(-inx))
  }
  //
  def labelCompare(prdlabel: Prdlabel): Int = {
    val py = prdlabel.py
    val y = prdlabel.y
    val ret = if (math.abs(py - y) < 0.1) 1 else 0
    ret

  }

  //
  //
  def accuracy(da: RDD[Prdlabel]): Double = {
    val count = da.count()
    val ret = da.map(labelCompare).reduce((x, y) => x + y)
    (ret + 0.0) / (count + 1)
  }
  //
  def sign(w: Double): Double = {
    if (w > 0.0) 1.0 else -1.0
  }

  //
  def sign(w: DenseVector[Double]): DenseVector[Double] = {
    w.map(f => {
      if (f > 0) 1.0 else -1.0
    })
  }

  //
  def sign(v: DenseMatrix[Double]): DenseMatrix[Double] = {
    v.map(f => {
      if (f > 0) 1.0 else -1.0
    })
  }

  //
  def signLabel(w: Double, threshold: Double): Double = {
    if (w >= threshold) 1.0 else -1.0
  }


}

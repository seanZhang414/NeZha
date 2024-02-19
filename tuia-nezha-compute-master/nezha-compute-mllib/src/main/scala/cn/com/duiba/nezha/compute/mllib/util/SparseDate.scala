package cn.com.duiba.nezha.compute.mllib.util

import java.util.Random
import cn.com.duiba.nezha.compute.api.point.Point
import Point.LabeledSPoint
import cn.com.duiba.nezha.compute.api.point.Point
import cn.com.duiba.nezha.compute.common.util.LabelUtil
import org.apache.spark.mllib.linalg._
import org.apache.spark.mllib.regression.LabeledPoint

import scala.collection.mutable.ArrayBuffer

/**
 * Logistic regression based classification.
 */
object SparseDate {


  //
  def generateData(D: Int, N: Int, R: Double, seed: Int) = {
    val rand = new Random(seed)
    def generatePoint(i: Int) = {
      val y = if (i % 2 == 0) 0 else 1
      val size = D
      val indices = new ArrayBuffer[Int]()

      val values = new ArrayBuffer[Double]()

      indices += 0
      values += 1.0

      for (j <- 1 to D - 1) {
        val r_v = rand.nextDouble()
        if (Math.abs(r_v) > 0.5) {
          indices += j
          values += r_v + (if (y > 0) 1.0 else -1.0) * R
        }
      }

      val sv = Vectors.sparse(size, indices.toArray, values.toArray).asInstanceOf[SparseVector]
      LabeledSPoint(sv, y)
    }
    Array.tabulate(N)(generatePoint)
  }


  //
  def generateData2(D: Int, N: Int, R: Double, seed: Int) = {
    val rand = new Random(seed)
    def generatePoint(i: Int) = {
      val y = if (i % 2 == 0) 0 else 1
      val size = D
      val indices = new ArrayBuffer[Int]()
      val values = new ArrayBuffer[Double]()



      if (y == 1) {
        for (j <- 0 to (D * 2 / 3 - 1)) {
          val r_v = rand.nextDouble()
          if (Math.abs(r_v) > 0.5) {
            indices += j
            values += 1.0
          }
        }

      }

      if (y == 0) {
        for (j <- (D / 3) to D - 1) {
          val r_v = rand.nextDouble()
          if (Math.abs(r_v) > 0.5) {
            indices += j
            values += 1.0
          }
        }

      }



      val sv = Vectors.sparse(size, indices.toArray, values.toArray).asInstanceOf[SparseVector]
      LabeledSPoint(sv, y)
    }
    Array.tabulate(N)(generatePoint)
  }

  //
  def generateData4(D: Int, N: Int, R: Double, seed: Int,r:Double) = {
    val rand = new Random(seed)
    def generatePoint(i: Int) = {
      val y = if (i % 2 == 0) 0 else 1
      val size = D
      val indices = new ArrayBuffer[Int]()
      val values = new ArrayBuffer[Double]()



      if (y == 1) {
        for (j <- 0 to (D * 2 / 3 - 1)) {
          val r_v = rand.nextDouble()
          if (Math.abs(r_v) > r) {
            indices += j
            values += 1.0
          }
        }

      }

      if (y == 0) {
        for (j <- (D / 3) to D - 1) {
          val r_v = rand.nextDouble()
          if (Math.abs(r_v) > r) {
            indices += j
            values += 1.0
          }
        }

      }



      val sv = Vectors.sparse(size, indices.toArray, values.toArray).asInstanceOf[SparseVector]
      LabeledSPoint(sv, y)
    }
    Array.tabulate(N)(generatePoint)
  }


  //
  def generateData3(D: Int, N: Int, R: Double, seed: Int) = {

    val rand = new Random(seed)
    def generatePoint(i: Int) = {
      val y = if (i % 2 == 0) 0 else 1
      val size = D
      val values = new Array[Double](D)



      if (y == 1) {
        for (j <- 0 to (D * 2 / 3 - 1)) {
          val r_v = rand.nextDouble()
          if (Math.abs(r_v) > 0.5) {
            values.update(j, 1.0)
          }
        }

      }

      if (y == 0) {
        for (j <- (D / 3) to D - 1) {
          val r_v = rand.nextDouble()
          if (Math.abs(r_v) > 0.5) {
            values.update(j, 1.0)
          }
        }

      }
      LabeledPoint(y, Vectors.dense(values))
    }
    Array.tabulate(N)(generatePoint)
  }


}
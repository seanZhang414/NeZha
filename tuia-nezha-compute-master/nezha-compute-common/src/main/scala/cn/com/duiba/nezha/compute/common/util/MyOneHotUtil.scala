package cn.com.duiba.nezha.compute.common.util

import org.apache.spark.mllib.linalg.{SparseVector, Vectors}

import scala.collection.mutable.ArrayBuffer

/**
 * Created by pc on 2016/12/16.
 */
object MyOneHotUtil {

  def getOneHotDouble(idx: Int, size: Int): List[Double] = {
    if (size < 1) {
      return null
    }

    val array = new Array[Double](size + 1)
    if (idx < 0 || idx > size) {
      array(0) = 1.0
    } else {
      array(idx + 1) = 1.0
    }
    array.toList
  }

//  def getOneHotSparseVector(sizeArray: Array[Int], indexArray: Array[Int]): SparseVector = {
//    if (sizeArray.length != indexArray.length) {
//      return null
//    }
//
//    var sizeSum = 0
//
//    val r_indices = new ArrayBuffer[Int]()
//    for (i <- 0 to sizeArray.length - 1) {
//      r_indices += sizeSum + indexArray.apply(i) + 1
//      sizeSum += sizeArray.apply(i) + 1
//    }
//    val r_values = Array.tabulate[Double](sizeArray.length)(i=>1.0)
//
//
//    Vectors.sparse(sizeSum, r_indices.toArray, r_values).asInstanceOf[SparseVector]
//  }


  def getOneHotInt(idx: Int, size: Int): List[Int] = {
    if (size < 1) {
      return null
    }
    val array = new Array[Int](size + 1)
    if (idx < 0 || idx > size) {
      array(0) = 1
    } else {
      array(idx + 1) = 1
    }
    array.toList
  }

}

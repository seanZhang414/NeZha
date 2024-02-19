package cn.com.duiba.nezha.compute.core.model.ops

import cn.com.duiba.nezha.compute.core.enums.DateStyle
import cn.com.duiba.nezha.compute.core.util.DateUtil
import org.apache.spark.mllib.linalg.{Matrices, SparseMatrix, SparseVector, Vectors}

import scala.collection.mutable.ArrayBuffer

class VectorOps {

}

//case class ParVector(parId: Int, lowBound: Int, highBound: Int, vector: SparseVector)

object VectorOps {

  def toCopyMatrix(vector: SparseVector, numCols: Int): SparseMatrix = {


    val colPtrs = new ArrayBuffer[Int]()
    val values = new ArrayBuffer[Double]()
    val rowIndices = new ArrayBuffer[Int]()

    if (vector == null) {
      return null
    }

    colPtrs += 0
    for (col <- 0 to numCols - 1) {
      values ++= vector.values
      rowIndices ++= vector.indices
      val inc = colPtrs.apply(col) + vector.values.size
      colPtrs.append(inc)
    }
    val sm = Matrices.sparse(vector.size, numCols, colPtrs.toArray, rowIndices.toArray, values.toArray)
    sm.asInstanceOf[SparseMatrix]
  }


  def toStrIndex(sp: SparseVector): Array[String] = {

    if (sp == null) {
      return null;
    } else {
      sp.indices.map(i => i + "")
    }
  }


  def toIndexSV(svArray: List[SparseVector], vSize: Int): SparseVector = {

    if (svArray == null) {
      return null;
    } else {
      toIndexSV(svArray.toArray, vSize)
    }
  }


  def toIndexSV(svArray: Array[SparseVector], vSize: Int): SparseVector = {
    var indexSet: Set[Int] = Set()
    if (svArray == null || svArray.length == 0) {
      return null
    }
    for (sv <- svArray) {
      if (sv != null) {
        val pSet = sv.indices.toSet
        indexSet = indexSet ++ pSet
      }


    }

    val indices = indexSet.toList.sorted.toArray
    val values = Array.tabulate[Double](indices.length)(i => 1.0)
    val ret = Vectors.sparse(vSize, indices, values)

    ret.toSparse

  }


  def toMap(sparseVector: SparseVector): Map[Int, Double] = {
    var map: Map[Int, Double] = Map()

    if (sparseVector != null) {
      for (i <- 0 to sparseVector.indices.length - 1) {
        val idx = sparseVector.indices.apply(i)
        val value = sparseVector.values.apply(i)
        map += (idx -> value)
      }
    }

    map
  }


  def toVector(vSize: Int, indices: Array[Int], values: Array[Double]): SparseVector = {

    var indices_b = new ArrayBuffer[Int]()
    var values_b = new ArrayBuffer[Double]()

    if (indices != null && values != null) {
      for (i <- 0 to indices.size - 1) {
        if (math.abs(values.apply(i)) > 0.00000001) {
          indices_b += indices.apply(i)
          values_b += values.apply(i)
        }
      }
    }
    val v = Vectors.sparse(vSize, indices_b.toArray, values_b.toArray)
    v.toSparse
  }


  def toVector(vSize: Int, data: Map[Int, Double]): SparseVector = {

    var indices_b = new ArrayBuffer[Int]()
    var values_b = new ArrayBuffer[Double]()

    if (data != null) {
      val indices = data.keySet.toList.sorted
      for (i <- 0 to indices.size - 1) {
        val key = indices.apply(i)
        if (math.abs(data.getOrElse(key, 0.0)) > 0.00000000000000000000000001) {
          indices_b += key
          values_b += data.getOrElse(key, 0.0)
        }
      }
    }

    val v = Vectors.sparse(vSize, indices_b.toArray, values_b.toArray)
    v.toSparse
  }

  def toVector(parVectorMap: Map[Int, SparseVector], parSize: Int, vSize: Int): SparseVector = {

    var indices = new ArrayBuffer[Int]()
    var values = new ArrayBuffer[Double]()

    if (parVectorMap != null) {
      val parIdArray = parVectorMap.keySet.toArray.sorted
      for (parId <- parIdArray) {
        val parVector = parVectorMap(parId)
        if (parVector != null) {
          indices ++= parVector.indices
          values ++= parVector.values
        }
      }
    }
    val v = Vectors.sparse(vSize, indices.toArray, values.toArray)
    v.toSparse
  }


  def toParVector(vector: SparseVector, parSize: Int): Map[Int, SparseVector] = {
    var vectorMap: Map[Int, SparseVector] = Map()
    if (vector != null) {

//      println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS)+" toParVector 1 wuth ")
//      println("vector.size"+vector.size+",parSize="+parSize)
      val parNums = ArrayOps.getParNums(parSize, vector.size)
//      println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS)+" toParVector 2")
//      println("psModel.vMap.15 org =" + vector)
      for (parId <- 0 to parNums - 1) {
        //        val (lowBound, highBound) = getParBound(parSize, vector.size, parId)
//        println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS)+" toParVector step")
        val parVector = toParVector(vector, parSize, parId)
        //      vectorMap += (parId -> ParVector(parId, lowBound, highBound, parVector))
        vectorMap += (parId -> parVector)
      }
    }

//    println("psModel.vMap.15=" + vectorMap)
    vectorMap
  }


  def toParVector(vector: SparseVector, parSize: Int, parId: Int): SparseVector = {

    if (vector == null) {
      return null
    }
    val parBoundInfo = ArrayOps.getParArray(vector.indices, parSize, vector.size, parId)

//    println("parBoundInfo"+parBoundInfo)
    val pv = Vectors.sparse(vector.size,
      ArrayOps.getSubArray(vector.indices, parBoundInfo.start, parBoundInfo.end+1),
      ArrayOps.getSubArray(vector.values, parBoundInfo.start, parBoundInfo.end+1)
    )
    pv.toSparse
  }


}
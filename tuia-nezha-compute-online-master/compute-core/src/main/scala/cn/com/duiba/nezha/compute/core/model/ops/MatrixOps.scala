package cn.com.duiba.nezha.compute.core.model.ops

import org.apache.spark.mllib.linalg.{Matrices, SparseMatrix, SparseVector, Vectors}

import scala.collection.mutable.ArrayBuffer

class MatrixOps {

}

object MatrixOps {

  def toMap(sm: SparseMatrix): Map[Int, Map[Int, Double]] = {
    var vMap: Map[Int, Map[Int, Double]] = Map()
    if (sm != null) {

      for (col <- 0 to sm.numCols - 1) {
        val vector = toVector(sm, col)
        vMap += (col -> VectorOps.toMap(vector))
      }
    }
    vMap
  }


  def toMatrixOnMap(mMap: Map[Int, Map[Int, Double]], numRows: Int, numCols: Int): SparseMatrix = {


    var data: Map[Int, SparseVector] = Map()
    if (mMap != null) {
      for (col <- 0 to numCols - 1) {
        val vMap = mMap.getOrElse(col,Map())
        val colVector = VectorOps.toVector(numRows, vMap)
        data += (col -> colVector)
      }
    }

    toMatrix(data, numRows, numCols)
  }


  def toMatrix(vmap: Map[Int, SparseVector], numRows: Int, numCols: Int): SparseMatrix = {


    val colPtrs = new ArrayBuffer[Int]()
    val values = new ArrayBuffer[Double]()
    val rowIndices = new ArrayBuffer[Int]()

    colPtrs += 0
    if (vmap != null) {
      for (col <- 0 to numCols - 1) {
        val vector = vmap(col)
        if (vector != null) {
          values ++= vector.values
          rowIndices ++= vector.indices
          val inc = colPtrs.apply(col) + vector.values.size
          colPtrs.append(inc)
        } else {
          val inc = colPtrs.apply(col) + 0
          colPtrs.append(inc)
        }

      }
    }

    val sm = Matrices.sparse(numRows, numCols, colPtrs.toArray, rowIndices.toArray, values.toArray)
    sm.asInstanceOf[SparseMatrix]
  }


  def toParVectors(sm: SparseMatrix): Map[Int, SparseVector] = {
    var vMap: Map[Int, SparseVector] = Map()
    if (sm != null) {
      for (col <- 0 to sm.numCols - 1) {
        val vector = toVector(sm, col)
        vMap += (col -> vector)
      }
    }
    vMap
  }

  def toVector(sm: SparseMatrix, col: Int): SparseVector = {
    if (sm != null) {
      val start = sm.colPtrs(col)
      val end = sm.colPtrs(col + 1)


      val indices = sm.rowIndices.slice(start, end)
      val values = sm.values.slice(start, end)

      val vector = Vectors.sparse(sm.numRows, indices, values)
      vector.toSparse
    } else {
      null
    }
  }

  def unTransposed(sm: SparseMatrix): SparseMatrix = {
    if (sm != null && sm.isTransposed) {
      sm.transpose
    } else {
      sm
    }
  }
}
package cn.com.duiba.nezha.compute.mllib.util

import org.apache.spark.mllib.linalg._

/**
 * Logistic regression based classification.
 */
object MLUtil {


  //
  def sigmoid(inx: Double): Double = {
    var ret = 0.00001
    var v = inx

    if (inx > 100.0) {
      v = 100.0
    }

    if (inx < -100.0) {
      v = -100.0
    }

    ret= 1 / (1 + math.exp(-v))
    ret
  }

  //
  def sign(w: Double): Double = {
    if (w > 0.0) 1.0 else -1.0
  }

  //
  def sign(w: SparseVector): SparseVector = {
    val n_v = w.values.map(f => {
      if (f > 0) 1.0 else -1.0
    })

    Vectors.sparse(w.size, w.indices, n_v).asInstanceOf[SparseVector]
  }

  //
  def sign(v: SparseMatrix): SparseMatrix = {

    val n_v = v.values.map(f => {
      if (f > 0) 1.0 else -1.0
    })

    Matrices.sparse(v.numRows, v.numCols, v.colPtrs, v.rowIndices, n_v).asInstanceOf[SparseMatrix]
  }

}
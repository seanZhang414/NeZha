package cn.com.duiba.nezha.compute.mllib.util

import java.util.Random

import breeze.numerics.abs
import cn.com.duiba.nezha.compute.api.constant.GlobalConstant
import org.apache.spark.mllib.linalg._

import scala.collection.mutable.ArrayBuffer

/**
 * Created by pc on 2017/6/14.
 */
object SparseUtil {


  def sum(sv1: SparseVector): Double = {
    sv1.values.sum
  }

  def sum(sv1: Vector): Double = {
    sv1.toArray.sum
  }


  def sum(sm1: SparseMatrix): Double = {
    sm1.values.sum
  }

  def sum(sm1: Matrix): Double = {
    sm1.toArray.sum
  }


  def sum_row2(sm1_o: SparseMatrix): SparseVector = {

    val sm1 = transposeClean(sm1_o)

    val r_indices = new ArrayBuffer[Int]()
    val r_values = new ArrayBuffer[Double]()

    for (i <- 0 to sm1.numCols - 1) {
      //      val p1_values = sm1.values.slice(sm1.colPtrs.apply(i), sm1.colPtrs.apply(i + 1))
      //      if (p1_values.size > 0) {
      //        r_indices += i
      //        r_values += p1_values.sum
      //      }

      if (sm1.colPtrs.apply(i) < sm1.colPtrs.apply(i + 1)) {
        var s_s = 0.0
        for (j <- sm1.colPtrs.apply(i) to sm1.colPtrs.apply(i + 1) - 1) {
          s_s += sm1.values.apply(j)
        }
        r_indices += i
        r_values += s_s
      }

    }



    Vectors.sparse(sm1.numCols, r_indices.toArray, r_values.toArray).asInstanceOf[SparseVector]

  }


  def sum_row(sm1_o: SparseMatrix): SparseVector = {

    val sm1 = transposeClean(sm1_o)

    val r_indices = new ArrayBuffer[Int]()
    val r_values = new ArrayBuffer[Double]()

    for (i <- 0 to sm1.numCols - 1) {
      val p1_values = sm1.values.slice(sm1.colPtrs.apply(i), sm1.colPtrs.apply(i + 1))
      if (p1_values.size > 0) {
        r_indices += i
        r_values += p1_values.sum
      }
    }

    Vectors.sparse(sm1.numCols, r_indices.toArray, r_values.toArray).asInstanceOf[SparseVector]

  }


  def rand(seed: Int): Double = {
    val rand = new Random(seed)
    rand.nextDouble()
  }


  def rand(seed: Int, proportion: Double): Double = {
    rand(seed: Int) * proportion
  }


  def rand(size: Int, seed: Int): SparseVector = {

    val rand = new Random(seed)

    val ret_indices = Array.range(0, size)

    val ret_values = Array.tabulate[Double](size)(i=>rand.nextDouble())

    Vectors.sparse(size, ret_indices, ret_values).asInstanceOf[SparseVector]

  }


  def rand(size: Int, seed: Int, proportion: Double): SparseVector = {
    multiply(rand(size: Int, seed: Int), proportion)
  }


  def zero(size: Int): SparseVector = {


    val ret_indices = Array.range(0, size)

    val ret_values = Array.tabulate[Double](size)(i=>0.0)

    Vectors.sparse(size, ret_indices, ret_values).asInstanceOf[SparseVector]
  }

  def ones(size: Int, proportion: Double): SparseVector = {


    val ret_indices = Array.range(0, size)

    val ret_values = Array.tabulate[Double](size)(i=>proportion)

    Vectors.sparse(size, ret_indices, ret_values).asInstanceOf[SparseVector]

  }


  def rand(numRows: Int, numCols: Int, seed: Int): SparseMatrix = {

    val rand = new Random(seed)

    val ret_rowIndices = new ArrayBuffer[Int]()

    for (i <- 0 to numCols - 1) {
      ret_rowIndices ++= Array.range(0, numRows)
    }

    val ret_colPtrs = Array.range(0, numCols + 1).map(x => x * numRows)

    val ret_values = Array.tabulate[Double](numRows*numCols)(i=>rand.nextDouble())

    Matrices.sparse(numRows, numCols, ret_colPtrs.toArray, ret_rowIndices.toArray, ret_values.toArray).asInstanceOf[SparseMatrix]


  }

  def rand(numRows: Int, numCols: Int, seed: Int, proportion: Double): SparseMatrix = {

    multiply(rand(numRows: Int, numCols: Int, seed: Int), proportion)
  }


  def zero(numRows: Int, numCols: Int): SparseMatrix = {

    val ret_rowIndices = new ArrayBuffer[Int]()

    for (i <- 0 to numCols - 1) {
      ret_rowIndices ++= Array.range(0, numRows)
    }

    val ret_colPtrs = Array.range(0, numCols + 1).map(x => x * numRows)

    val ret_values = Array.tabulate[Double](numRows*numCols)(i=>0.0)

    Matrices.sparse(numRows, numCols, ret_colPtrs.toArray, ret_rowIndices.toArray, ret_values.toArray).asInstanceOf[SparseMatrix]


  }

  def ones(numRows: Int, numCols: Int, proportion: Double): SparseMatrix = {

    val ret_rowIndices = new ArrayBuffer[Int]()

    for (i <- 0 to numCols - 1) {
      ret_rowIndices ++= Array.range(0, numRows)
    }

    val ret_colPtrs = Array.range(0, numCols + 1).map(x => x * numRows)

    val ret_values = Array.tabulate[Double](numRows*numCols)(i=>proportion)

    Matrices.sparse(numRows, numCols, ret_colPtrs.toArray, ret_rowIndices.toArray, ret_values.toArray).asInstanceOf[SparseMatrix]


  }


  def vector_copy(sv1: SparseVector, numCols: Int): SparseMatrix = {

    val ret_colPtrs = new ArrayBuffer[Int]()
    val ret_rowIndices = new ArrayBuffer[Int]()
    val ret_values = new ArrayBuffer[Double]()
    ret_colPtrs += 0
    for (i <- 0 to numCols - 1) {
      ret_rowIndices ++= sv1.indices
      ret_values ++= sv1.values
      ret_colPtrs += ret_colPtrs(i) + sv1.indices.length
    }
    Matrices.sparse(sv1.size, numCols, ret_colPtrs.toArray, ret_rowIndices.toArray, ret_values.toArray).asInstanceOf[SparseMatrix]
  }

  def vector_copy_test(sv1: SparseVector, numCols: Int): SparseMatrix = {
    val ret_colPtrs = new ArrayBuffer[Int]()
    val ret_rowIndices = new ArrayBuffer[Int]()
    val ret_values = new ArrayBuffer[Double]()
    ret_colPtrs += 0

    for (i <- 0 to numCols - 1) {
      ret_rowIndices ++= sv1.indices
      ret_values ++= sv1.values
      ret_colPtrs += ret_colPtrs(i) + sv1.indices.length
    }


    //    Matrices.sparse(sv1.size, numCols, ret_colPtrs.toArray, ret_rowIndices.toArray, ret_values.toArray).asInstanceOf[SparseMatrix]
    null
  }


  def multiply(sv: Vector, d: Double): SparseVector = {
    multiply(sv.toSparse, d)
  }


  def multiply(sv: SparseVector, d: Double): SparseVector = {
    val m_values = sv.values.map(i => i * d)
    Vectors.sparse(sv.size, sv.indices, m_values).asInstanceOf[SparseVector]
  }

  def multiply(sm: SparseMatrix, d: Double): SparseMatrix = {
    val m_values = sm.values.map(i => i * d)
    Matrices.sparse(sm.numRows, sm.numCols, sm.colPtrs, sm.rowIndices, m_values).asInstanceOf[SparseMatrix]
  }

  def sqrt(sv: SparseVector): SparseVector = {
    val m_values = sv.values.map(i => math.sqrt(i))

    Vectors.sparse(sv.size, sv.indices, m_values).asInstanceOf[SparseVector]
  }

  def sqrt(sv: Vector): SparseVector = {
    sqrt(sv.toSparse)
  }


  def sqrt(sm: SparseMatrix): SparseMatrix = {
    val m_values = sm.values.map(i => math.sqrt(i))
    Matrices.sparse(sm.numRows, sm.numCols, sm.colPtrs, sm.rowIndices, m_values).asInstanceOf[SparseMatrix]
  }


  def inverse(sv: SparseVector): SparseVector = {
    val m_values = sv.values.map(i => 1 / (i + GlobalConstant.EPSILON))

    Vectors.sparse(sv.size, sv.indices, m_values).asInstanceOf[SparseVector]
  }

  def inverse(sm: SparseMatrix): SparseMatrix = {
    val m_values = sm.values.map(i => 1 / (i + GlobalConstant.EPSILON))
    Matrices.sparse(sm.numRows, sm.numCols, sm.colPtrs, sm.rowIndices, m_values).asInstanceOf[SparseMatrix]
  }


  def add(sv: SparseVector, d: Double): SparseVector = {
    val m_values = sv.values.map(i => i + d)
    Vectors.sparse(sv.size, sv.indices, m_values).asInstanceOf[SparseVector]
  }

  def add(sm: SparseMatrix, d: Double): SparseMatrix = {
    val m_values = sm.values.map(i => i + d)
    Matrices.sparse(sm.numRows, sm.numCols, sm.colPtrs, sm.rowIndices, m_values).asInstanceOf[SparseMatrix]
  }


  def add(sv1: SparseVector, sv2: SparseVector): SparseVector = {

    if (sv1.size != sv2.size) {
      throw new Exception("add error ,params valid sv1.size != sv2.size")
    }

    val r_indices = new ArrayBuffer[Int]()
    val r_values = new ArrayBuffer[Double]()

    var i = 0
    var j = 0

    while (i < sv1.indices.length && j < sv2.indices.length) {

      val sv1_idx = sv1.indices.apply(i)
      val sv2_idx = sv2.indices.apply(j)

      if (sv1_idx < sv2_idx) {
        r_indices += sv1_idx
        r_values += sv1.values.apply(i)

        i += 1
      }

      if (sv1_idx == sv2_idx) {
        if (Math.abs(sv1.values.apply(i) + sv2.values.apply(j)) > GlobalConstant.DOUBLE_ZERO) {
          r_indices += sv1_idx
          r_values += sv1.values.apply(i) + sv2.values.apply(j)
        }
        i += 1
        j += 1
      }

      if (sv1_idx > sv2_idx) {
        r_indices += sv2_idx
        r_values += sv2.values.apply(j)
        j += 1
      }

    }
    if (i < sv1.indices.length) {

      r_indices ++= sv1.indices.slice(i, sv1.indices.length)
      r_values ++= sv1.values.slice(i, sv1.indices.length)
    }

    if (j < sv2.indices.length) {

      r_indices ++= sv2.indices.slice(j, sv2.indices.length)
      r_values ++= sv2.values.slice(j, sv2.indices.length)
    }



    Vectors.sparse(sv1.size, r_indices.toArray, r_values.toArray).asInstanceOf[SparseVector]

  }


  def add(sm1_o: SparseMatrix, sm2_o: SparseMatrix): SparseMatrix = {

    if (sm1_o.numRows != sm2_o.numRows || sm1_o.numCols != sm2_o.numCols) {
      throw new Exception("add error ,params valid sm1.numRows!=sm2.numRows || sm1.numCols!=sm2.numCols")
    }

    // 判断是否转置

    val sm1 = transposeClean(sm1_o)
    val sm2 = transposeClean(sm2_o)

    val ret_colPtrs = new ArrayBuffer[Int]()
    val ret_rowIndices = new ArrayBuffer[Int]()
    val ret_values = new ArrayBuffer[Double]()

    ret_colPtrs += 0
    for (i <- 0 to sm1.numCols - 1) {

      val p1_indices = sm1.rowIndices.slice(sm1.colPtrs.apply(i), sm1.colPtrs.apply(i + 1))
      val p1_values = sm1.values.slice(sm1.colPtrs.apply(i), sm1.colPtrs.apply(i + 1))
      val p1_Vector = Vectors.sparse(sm1.numRows, p1_indices, p1_values).asInstanceOf[SparseVector]

      val p2_indices = sm2.rowIndices.slice(sm2.colPtrs.apply(i), sm2.colPtrs.apply(i + 1))
      val p2_values = sm2.values.slice(sm2.colPtrs.apply(i), sm2.colPtrs.apply(i + 1))
      val p2_Vector = Vectors.sparse(sm1.numRows, p2_indices, p2_values).asInstanceOf[SparseVector]

      val p_Vector = add(p1_Vector, p2_Vector)

      ret_rowIndices ++= p_Vector.indices
      ret_values ++= p_Vector.values
      ret_colPtrs += ret_colPtrs(i) + p_Vector.indices.length

    }

    Matrices.sparse(sm1.numRows, sm1.numCols, ret_colPtrs.toArray, ret_rowIndices.toArray, ret_values.toArray).asInstanceOf[SparseMatrix]

  }


  def multiply2(sv1: SparseVector, sv2: SparseVector): SparseVector = {

    if (sv1.size != sv2.size) {
      throw new Exception("add error ,params valid sv1.size != sv2.size")
    }


    val r_indices = new ArrayBuffer[Int]()
    val r_values = new ArrayBuffer[Double]()

    var ret = Vectors.sparse(sv1.size, r_indices.toArray, r_values.toArray).asInstanceOf[SparseVector]


    if (sv1.indices.length != 0 && sv2.indices.length != 0) {

      if (sv1.indices.length <= sv2.indices.length) {
        ret = multiply_v_sv(sv2, sv1)
      } else {
        ret = multiply_v_sv(sv1, sv2)
      }
    }


    ret

  }

  def multiply_v_sv(v1: Vector, sv2: SparseVector): SparseVector = {

    if (v1.size != sv2.size) {
      throw new Exception("multiply_v_sv error ,params valid v1.size != sv2.size")
    }


    val r_indices = new ArrayBuffer[Int]()
    val r_values = new ArrayBuffer[Double]()

    for (i <- 0 to sv2.indices.length - 1) {
      val sv2_idx = sv2.indices.apply(i)

      if (abs(v1.apply(i)) > 0.0000001) {
        r_indices += sv2_idx
        r_values += v1.apply(sv2_idx) * sv2.values.apply(i)

      }

    }




    Vectors.sparse(sv2.size, r_indices.toArray, r_values.toArray).asInstanceOf[SparseVector]

  }


  def multiply(sv1: SparseVector, sv2: SparseVector): SparseVector = {

    if (sv1.size != sv2.size) {
      throw new Exception("add error ,params valid sv1.size != sv2.size")
    }


    val r_indices = new ArrayBuffer[Int]()
    val r_values = new ArrayBuffer[Double]()

    var i = 0
    var j = 0

    while (i < sv1.indices.length && j < sv2.indices.length) {

      val sv1_idx = sv1.indices.apply(i)
      val sv2_idx = sv2.indices.apply(j)

      if (sv1_idx < sv2_idx) i += 1
      if (sv1_idx > sv2_idx) j += 1

      if (sv1_idx == sv2_idx) {
        r_indices += sv1_idx
        r_values += sv1.values.apply(i) * sv2.values.apply(j)
        i += 1
        j += 1
      }

    }
    Vectors.sparse(sv1.size, r_indices.toArray, r_values.toArray).asInstanceOf[SparseVector]

  }


  def multiply(sv1: SparseVector, copy_nums: Int, sm2: Matrix): SparseMatrix = {

    if (sv1.size != sm2.numRows || copy_nums != sm2.numCols) {
      throw new Exception("multiply error ,params valid sm1.numRows!=sm2.numRows || sm1.numCols!=sm2.numCols")
    }


    val sv1_indices_length = sv1.indices.length

    val ret_colPtrs = new ArrayBuffer[Int]()
    val ret_rowIndices = new ArrayBuffer[Int]()
    val ret_values = new ArrayBuffer[Double]()

    ret_colPtrs += 0
    for (i <- 0 to sm2.numCols - 1) {

      val sub_indices = new ArrayBuffer[Int]()
      val sub_values = new ArrayBuffer[Double]()
      for (j <- 0 to sv1_indices_length - 1) {
        val sv1_i = sv1.indices(j)
        val sm2_v = sm2.apply(sv1_i, i)
        if (sm2_v > 0.0 || sm2_v < 0.0) {

          sub_indices += sv1_i
          sub_values += sv1.values(j) * sm2_v
        }

      }

      ret_rowIndices ++= sub_indices
      ret_values ++= sub_values
      ret_colPtrs += ret_colPtrs(i) + sub_indices.length

    }

    Matrices.sparse(sm2.numRows, sm2.numCols, ret_colPtrs.toArray, ret_rowIndices.toArray, ret_values.toArray).asInstanceOf[SparseMatrix]

  }


  def multiply(sm1_o: SparseMatrix, sm2_o: SparseMatrix): SparseMatrix = {

    if (sm1_o.numRows != sm2_o.numRows || sm1_o.numCols != sm2_o.numCols) {
      throw new Exception("multiply error ,params valid sm1.numRows!=sm2.numRows || sm1.numCols!=sm2.numCols")
    }

    // 判断是否转置

    val sm1 = transposeClean(sm1_o)
    val sm2 = transposeClean(sm2_o)



    val ret_colPtrs = new ArrayBuffer[Int]()
    val ret_rowIndices = new ArrayBuffer[Int]()
    val ret_values = new ArrayBuffer[Double]()

    ret_colPtrs += 0
    for (i <- 0 to sm1.numCols - 1) {

      val p1_indices = sm1.rowIndices.slice(sm1.colPtrs.apply(i), sm1.colPtrs.apply(i + 1))
      val p1_values = sm1.values.slice(sm1.colPtrs.apply(i), sm1.colPtrs.apply(i + 1))
      val p1_Vector = Vectors.sparse(sm1.numRows, p1_indices, p1_values).asInstanceOf[SparseVector]

      val p2_indices = sm2.rowIndices.slice(sm2.colPtrs.apply(i), sm2.colPtrs.apply(i + 1))
      val p2_values = sm2.values.slice(sm2.colPtrs.apply(i), sm2.colPtrs.apply(i + 1))
      val p2_Vector = Vectors.sparse(sm2.numRows, p2_indices, p2_values).asInstanceOf[SparseVector]

      val p_Vector = multiply(p1_Vector, p2_Vector)

      ret_rowIndices ++= p_Vector.indices
      ret_values ++= p_Vector.values
      ret_colPtrs += ret_colPtrs(i) + p_Vector.indices.length

    }

    Matrices.sparse(sm1.numRows, sm1.numCols, ret_colPtrs.toArray, ret_rowIndices.toArray, ret_values.toArray).asInstanceOf[SparseMatrix]

  }


  def multiply(sv: SparseVector): SparseVector = {
    Vectors.sparse(sv.size, sv.indices, sv.values.map(i => i * i)).asInstanceOf[SparseVector]
  }

  def multiply(sm_o: SparseMatrix): SparseMatrix = {
    val sm = transposeClean(sm_o)
    val m_values = sm.values.map(i => i * i)
    Matrices.sparse(sm.numRows, sm.numCols, sm.colPtrs, sm.rowIndices, m_values).asInstanceOf[SparseMatrix]

  }

  def dot_col(sparseMatrix: SparseMatrix, sparseVector: SparseVector): SparseVector = {

    if (sparseMatrix.numCols != sparseVector.size) {
      throw new Exception("dot_col error ,params valid sparseMatrix.numRows != sparseVector.size")
    }
    dot_row(sparseMatrix.transpose: SparseMatrix, sparseVector: SparseVector)
  }

  def dot_row(sparseMatrix_o: SparseMatrix, sparseVector: SparseVector): SparseVector = {
    if (sparseMatrix_o.numRows != sparseVector.size) {
      throw new Exception("dot_row error ,params valid sparseMatrix.numRows != sparseVector.size")
    }

    val sparseMatrix = transposeClean(sparseMatrix_o)

    val r_indices = new ArrayBuffer[Int]()
    val r_values = new ArrayBuffer[Double]()
    for (i <- 0 to sparseMatrix.numCols - 1) {

      val offset_start = sparseMatrix.colPtrs.apply(i)
      val offset_end = sparseMatrix.colPtrs.apply(i + 1)

      val p_indices = sparseMatrix.rowIndices.slice(offset_start, offset_end)
      val p_values = sparseMatrix.values.slice(offset_start, offset_end)
      val pVector = Vectors.sparse(sparseMatrix.numRows, p_indices, p_values).asInstanceOf[SparseVector]
      val d_value = dot(pVector, sparseVector: SparseVector)

      if (abs(d_value - 0.0) > 0.000001) {
        r_indices += i
        r_values += d_value
      }

    }

    Vectors.sparse(sparseMatrix.numCols, r_indices.toArray, r_values.toArray).asInstanceOf[SparseVector]

  }

  def dot_row(matrix: Matrix, sparseVector: SparseVector): SparseVector = {
    if (matrix.numRows != sparseVector.size) {
      throw new Exception("dot_row error ,params valid sparseMatrix.numRows != sparseVector.size")
    }


    val r_indices = new ArrayBuffer[Int]()
    val r_values = new ArrayBuffer[Double]()
    val sparse_idx_length = sparseVector.indices.length

    for (i <- 0 to matrix.numCols - 1) {


      var d_value = 0.0
      for (j <- 0 to sparse_idx_length - 1) {
        val s_idx = sparseVector.indices(j)
        val m_v = matrix.apply(s_idx, i)
        if (abs(m_v - 0.0) > 0.000001) {
          d_value += sparseVector.values(j) * m_v
        }
      }

      if (abs(d_value - 0.0) > 0.000001) {
        r_indices += i
        r_values += d_value
      }

    }

    Vectors.sparse(matrix.numCols, r_indices.toArray, r_values.toArray).asInstanceOf[SparseVector]

  }


  def dot(sm1: SparseMatrix, sm2: SparseMatrix): SparseMatrix = {

    if (sm1.numCols != sm2.numRows) {
      throw new Exception("dot error ,params valid sm1.numRows != sm2.numCols || sm1.numCols != sm2.numRows")
    }
    dot_r_r(sm2, sm1.transpose)
  }

  /**
   * 矩阵点乘
   *
   * @param sm1_o
   * @param sm2_o
   * @return
   */
  def dot_r_r(sm1_o: SparseMatrix, sm2_o: SparseMatrix): SparseMatrix = {

    if (sm1_o.numRows != sm2_o.numRows) {
      throw new Exception("dot_r_r error ,params valid sparseMatrix.numRows != sparseVector.size")
    }

    val sm1 = transposeClean(sm1_o)
    val sm2 = transposeClean(sm2_o)

    val ret_colPtrs = new ArrayBuffer[Int]()
    val ret_rowIndices = new ArrayBuffer[Int]()
    val ret_values = new ArrayBuffer[Double]()
    ret_colPtrs += 0

    val lVectorArray = new Array[SparseVector](sm1.numCols)
    val rVectorArray = new Array[SparseVector](sm2.numCols)


    for (i <- 0 to sm1.numCols - 1) {

      val l_indices = sm1.rowIndices.slice(sm1.colPtrs.apply(i), sm1.colPtrs.apply(i + 1))
      val l_values = sm1.values.slice(sm1.colPtrs.apply(i), sm1.colPtrs.apply(i + 1))
      val l_vector = Vectors.sparse(sm1.numRows, l_indices, l_values).asInstanceOf[SparseVector]
      lVectorArray.update(i, l_vector)
    }

    for (j <- 0 to sm2.numCols - 1) {


      val r_indices = sm2.rowIndices.slice(sm2.colPtrs.apply(j), sm2.colPtrs.apply(j + 1))
      val r_values = sm2.values.slice(sm2.colPtrs.apply(j), sm2.colPtrs.apply(j + 1))
      val r_vector = Vectors.sparse(sm2.numRows, r_indices, r_values).asInstanceOf[SparseVector]
      rVectorArray.update(j, r_vector)
    }



    for (i <- 0 to sm1.numCols - 1) {

      val l_vector = lVectorArray.apply(i)


      val rv_indices = new ArrayBuffer[Int]()
      val rv_values = new ArrayBuffer[Double]()

      for (j <- 0 to sm2.numCols - 1) {

        val r_vector = rVectorArray.apply(j)

        val l_r_dot = dot(l_vector, r_vector)
        if (Math.abs(l_r_dot) > GlobalConstant.DOUBLE_ZERO) {
          rv_indices += j
          rv_values += l_r_dot
        }

      }

      ret_rowIndices ++= rv_indices
      ret_values ++= rv_values
      ret_colPtrs += ret_colPtrs(i) + rv_indices.length


    }
    Matrices.sparse(sm2.numCols, sm1.numCols, ret_colPtrs.toArray, ret_rowIndices.toArray, ret_values.toArray).asInstanceOf[SparseMatrix]

  }


  /**
   * 矩阵点乘
   *
   * @param sm1_o
   * @param sm2_o
   * @return
   */
  def dot_r_r2(sm1_o: SparseMatrix, sm2_o: SparseMatrix): SparseMatrix = {

    if (sm1_o.numRows != sm2_o.numRows) {
      throw new Exception("dot_r_r error ,params valid sparseMatrix.numRows != sparseVector.size")
    }

    val sm1 = transposeClean(sm1_o)
    val sm2 = transposeClean(sm2_o)

    val ret_colPtrs = new ArrayBuffer[Int]()
    val ret_rowIndices = new ArrayBuffer[Int]()
    val ret_values = new ArrayBuffer[Double]()
    ret_colPtrs += 0




    for (i <- 0 to sm1.numCols - 1) {


      val l_indices = sm1.rowIndices.slice(sm1.colPtrs.apply(i), sm1.colPtrs.apply(i + 1))
      val l_values = sm1.values.slice(sm1.colPtrs.apply(i), sm1.colPtrs.apply(i + 1))
      val l_vector = Vectors.sparse(sm1.numRows, l_indices, l_values).asInstanceOf[SparseVector]


      val rv_indices = new ArrayBuffer[Int]()
      val rv_values = new ArrayBuffer[Double]()

      for (j <- 0 to sm2.numCols - 1) {


        val r_indices = sm2.rowIndices.slice(sm2.colPtrs.apply(j), sm2.colPtrs.apply(j + 1))
        val r_values = sm2.values.slice(sm2.colPtrs.apply(j), sm2.colPtrs.apply(j + 1))
        val r_vector = Vectors.sparse(sm2.numRows, r_indices, r_values).asInstanceOf[SparseVector]


        val l_r_dot = dot(l_vector, r_vector)
        if (Math.abs(l_r_dot) > GlobalConstant.DOUBLE_ZERO) {
          rv_indices += j
          rv_values += l_r_dot
        }

      }

      ret_rowIndices ++= rv_indices
      ret_values ++= rv_values
      ret_colPtrs += ret_colPtrs(i) + rv_indices.length


    }
    Matrices.sparse(sm2.numCols, sm1.numCols, ret_colPtrs.toArray, ret_rowIndices.toArray, ret_values.toArray).asInstanceOf[SparseMatrix]

  }


  def dot_m(sv1: SparseVector, sv2: SparseVector): SparseMatrix = {

    val ret_colPtrs = new ArrayBuffer[Int]()
    val ret_rowIndices = new ArrayBuffer[Int]()
    val ret_values = new ArrayBuffer[Double]()

    ret_colPtrs += 0
    var last_colPtrs = 0

    var idx_o = -1
    // 初始化
    for (i <- 0 to sv2.indices.length - 1) {

      val idx_n = sv2.indices.apply(i)

      val v_i = sv2.values.apply(i)

      ret_rowIndices ++= sv1.indices
      ret_values ++= sv1.values.map(x => x * v_i)

      for (j <- 0 to (idx_n - idx_o - 2)) {
        ret_colPtrs += last_colPtrs
        last_colPtrs += 0
      }

      last_colPtrs += sv1.indices.length
      ret_colPtrs += last_colPtrs
      idx_o = idx_n

    }

    for (j <- 0 to (sv2.size - idx_o - 2)) {
      ret_colPtrs += last_colPtrs
    }

    Matrices.sparse(sv1.size, sv2.size, ret_colPtrs.toArray, ret_rowIndices.toArray, ret_values.toArray).asInstanceOf[SparseMatrix]
  }

  def dot(sv1: SparseVector, sv2: SparseVector): Double = {

    if (sv1.size != sv2.size) {
      throw new Exception("v_dot error ,params valid sv1.size != sv2.size")
    }

    var d_sum = 0.0
    var i = 0
    var j = 0

    val sv1_idx = sv1.indices.length
    val sv2_idx = sv2.indices.length

    while (i < sv1_idx && j < sv2_idx) {

      val sv1_idx = sv1.indices.apply(i)
      val sv2_idx = sv2.indices.apply(j)

      if (sv1_idx < sv2_idx) {
        i += 1
      }
      if (sv1_idx > sv2_idx) {
        j += 1
      }
      if (sv1_idx == sv2_idx) {
        d_sum += sv1.values.apply(i) * sv2.values.apply(j)
        //        println(s"sv1_idx=${sv1_idx}*  w=${sv2.values.apply(j)},add=${sv1.values.apply(i) * sv2.values.apply(j)}")
        i += 1
        j += 1
      }
    }


    d_sum
  }


  def dot3(sv2: SparseVector, v1: Vector): Double = {

    if (v1.size != sv2.size) {
      throw new Exception("v_dot error ,params valid sv1.size != sv2.size")
    }

    var d_sum = 0.0

    val sv2_idx_length = sv2.indices.length
    for (i <- 0 to sv2_idx_length - 1) {
      val idx = sv2.indices.apply(i)
      d_sum += sv2.values(i) * v1(idx)
    }
    d_sum
  }


  def dot2(sv1: SparseVector, sv2: SparseVector): Double = {

    if (sv1.size != sv2.size) {
      throw new Exception("v_dot error ,params valid sv1.size != sv2.size")
    }
    var d_sum = 0.0

    if (sv1.indices.length != 0 && sv2.indices.length != 0) {

      if (sv1.indices.length <= sv2.indices.length) {
        d_sum = dot_v_sv(sv2, sv1)

      } else {
        d_sum = dot_v_sv(sv1, sv2)
      }


    }

    d_sum
  }


  def dot_v_sv(v1: Vector, sv2: SparseVector): Double = {

    if (v1.size != sv2.size) {
      throw new Exception("v_dot error ,params valid sv1.size != sv2.size")
    }

    var d_sum = 0.0

    val sv2_idx_length = sv2.indices.length
    for (i <- 0 to sv2_idx_length - 1) {
      val idx = sv2.indices.apply(i)
      d_sum += sv2.values(i) * v1(idx)
    }
    d_sum
  }


  def subtraction(sv1: SparseVector, sv2: SparseVector): SparseVector = {

    if (sv1.size != sv2.size) {
      throw new Exception("subtraction error ,params valid sv1.size != sv2.size")
    }

    val sv2_f = Vectors.sparse(sv2.size, sv2.indices, sv2.values.map(-_)).asInstanceOf[SparseVector]
    add(sv1, sv2_f)
  }

  def subtraction(sm1: SparseMatrix, sm2: SparseMatrix): SparseMatrix = {

    if (sm1.numRows != sm2.numRows || sm1.numCols != sm2.numCols) {
      throw new Exception("subtraction error ,params valid sm1.numRows!=sm2.numRows || sm1.numCols!=sm2.numCols")
    }

    val sm2_f = Matrices.sparse(sm2.numRows, sm2.numCols, sm2.colPtrs.toArray, sm2.rowIndices, sm2.values.map(-_)).asInstanceOf[SparseMatrix]
    add(sm1, sm2_f)
  }

  def subtraction(sv: SparseVector, d: Double): SparseVector = {
    val m_values = sv.values.map(i => i - d)
    Vectors.sparse(sv.size, sv.indices, m_values).asInstanceOf[SparseVector]
  }

  def subtraction(sm: SparseMatrix, d: Double): SparseMatrix = {
    val m_values = sm.values.map(i => i - d)
    Matrices.sparse(sm.numRows, sm.numCols, sm.colPtrs, sm.rowIndices, m_values).asInstanceOf[SparseMatrix]
  }


  def transposeClean(sm: SparseMatrix): SparseMatrix = {

    if (sm.isTransposed) {
      val sm1 = sm.transpose


      val ret_colPtrs = new ArrayBuffer[Int]()
      val ret_rowIndices = new ArrayBuffer[Int]()
      val ret_values = new ArrayBuffer[Double]()

      val rowIndices_list = new Array[ArrayBuffer[Int]](sm1.numRows)
      val values_list = new Array[ArrayBuffer[Double]](sm1.numRows)

      // 初始化
      for (i <- 0 to sm1.numRows - 1) {
        rowIndices_list.update(i, new ArrayBuffer[Int])
        values_list.update(i, new ArrayBuffer[Double])
      }


      for (i <- 0 to sm1.numCols - 1) {
        val o_indices = sm1.rowIndices.slice(sm1.colPtrs.apply(i), sm1.colPtrs.apply(i + 1))
        val o_values = sm1.values.slice(sm1.colPtrs.apply(i), sm1.colPtrs.apply(i + 1))

        var j = 0
        while (j < o_indices.length) {

          val idx = o_indices.apply(j)
          rowIndices_list.apply(idx) += i
          values_list.apply(idx) += o_values.apply(j)
          j += 1
        }

      }

      var offSet = 0
      ret_colPtrs += 0

      for (i <- 0 to sm1.numRows - 1) {
        ret_rowIndices ++= rowIndices_list.apply(i)
        ret_values ++= values_list.apply(i)
        offSet += rowIndices_list.apply(i).length
        ret_colPtrs += offSet
      }
      Matrices.sparse(sm1.numCols, sm1.numRows, ret_colPtrs.toArray, ret_rowIndices.toArray, ret_values.toArray).asInstanceOf[SparseMatrix]

    } else {
      sm
    }


  }
}

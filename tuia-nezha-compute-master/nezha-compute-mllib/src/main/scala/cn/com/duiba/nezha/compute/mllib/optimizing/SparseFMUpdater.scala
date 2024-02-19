package cn.com.duiba.nezha.compute.mllib.optimizing

import cn.com.duiba.nezha.compute.api.point.Point
import Point.{FMGradParams, FMParams}
import cn.com.duiba.nezha.compute.api.point.Point
import cn.com.duiba.nezha.compute.mllib.util.{MLUtil, SparseUtil}
import org.apache.spark.mllib.linalg._

/**
 * Logistic regression based classification.
 */
object SparseFMUpdater {


  def update(
              psOld:FMParams,
              grad:FMGradParams,
              learningRate: Double,
             r1: Double,
             r2: Double
              ): FMParams = {

    val w0_new = updateW0(psOld.w0, grad.grad_w0,learningRate, r1, r2)
    val w_new = updateW(psOld.w, grad.grad_w,learningRate, r1, r2)
    val v_new = updateV(psOld.v, grad.grad_v, learningRate, r1, r2)
    FMParams(w0_new, w_new, v_new)
  }



  def updateW0(w: Double, grad: Double, learningRate: Double, r1: Double, r2: Double): Double = {
    // (1 - learningRate * r2) * w  - learningRate * r1 * MLUtil.sign(w) * w - learningRate  * grad
    // w  - learningRate * （r2 * w + r1 * MLUtil.sign(w) * w + grad）

    val p1 = (1 - learningRate * r2) * w
    val p2 = learningRate * r1 * MLUtil.sign(w) * w
    val p3 = learningRate  * grad
    p1 - p2 - p3

  }

  def updateW(w: SparseVector, grad: SparseVector, learningRate: Double, r1: Double, r2: Double): SparseVector = {
    // (1 - learningRate * r2) * w  - learningRate * r1 * MLUtil.sign(w) * w - learningRate  * grad
    // w  - learningRate * （r2 * w + r1 * MLUtil.sign(w) * w + grad）

    val p1 = SparseUtil.multiply(w, 1 - learningRate * r2)
    val p2_1 = SparseUtil.multiply(MLUtil.sign(w), w)
    val p2 = SparseUtil.multiply(p2_1, learningRate * r1 * -1)
    val p3 = SparseUtil.multiply(grad, -1 * learningRate)
    val p1_p2 = SparseUtil.add(p1, p2)
    val p1_p2_p3 = SparseUtil.add(p3, p1_p2)

    p1_p2_p3
  }


  def updateV(v: SparseMatrix, grad: SparseMatrix, learningRate: Double, r1: Double, r2: Double): SparseMatrix = {
    // (1 - learningRate * r2) * w  - learningRate * r1 * MLUtil.sign(w) * w - learningRate  * grad
    // w  - learningRate * （r2 * w + r1 * MLUtil.sign(w) * w + grad）

    val p1 = SparseUtil.multiply(v, 1 - learningRate * r2)
    val p2_1 = SparseUtil.multiply(MLUtil.sign(v), v)
    val p2 = SparseUtil.multiply(p2_1, learningRate * r1 * -1)
    val p3 = SparseUtil.multiply(grad, -1 * learningRate)

    val p1_p2 = SparseUtil.add(p1, p2)
    val p1_p2_p3 = SparseUtil.add(p3, p1_p2)

    p1_p2_p3

  }



}
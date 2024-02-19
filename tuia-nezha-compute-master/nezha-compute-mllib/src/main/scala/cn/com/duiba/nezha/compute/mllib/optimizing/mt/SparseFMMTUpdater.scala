package cn.com.duiba.nezha.compute.mllib.optimizing.mt

import cn.com.duiba.nezha.compute.api.point.Point
import Point.{FMGradParams, FMParams}
import cn.com.duiba.nezha.compute.mllib.util.{MLUtil, SparseUtil}
import org.apache.spark.mllib.linalg._

/**
 * Logistic regression based classification.
 */
object SparseFMMTUpdater {


  def update(
              psOld: FMParams,
              grad: FMGradParams,
              deltaOld: FMGradParams,
              mtRate: Double,
              learningRate: Double,
              r1: Double,
              r2: Double
              ): (FMParams, FMGradParams) = {

    val grad_w0 = deltaW0(psOld.w0, grad.grad_w0, r1, r2)
    val grad_w = deltaW(psOld.w, grad.grad_w, r1, r2)
    val grad_v = deltaV(psOld.v, grad.grad_v, r1, r2)

    val (w0_new, delta_w0) = updateW0(psOld.w0, grad_w0, deltaOld.grad_w0, mtRate, learningRate)

    val (w_new, delta_w) = updateW(psOld.w, grad_w, deltaOld.grad_w, mtRate, learningRate)

    val (v_new, delta_v) = updateV(psOld.v, grad_v, deltaOld.grad_v, mtRate, learningRate)

    (FMParams(w0_new, w_new, v_new), FMGradParams(delta_w0, delta_w, delta_v))
  }


  def updateW0(w: Double, grad: Double, delta_old: Double, mtRate: Double, learningRate: Double): (Double, Double) = {
    // w-(mtRate * delta_old + learningRate * grad)
    val delta = mtRate * delta_old + learningRate * grad
    (w - delta, delta)
  }

  def updateW(w: SparseVector, grad: SparseVector, delta_old: SparseVector, mtRate: Double, learningRate: Double): (SparseVector, SparseVector) = {
    // w-(mtRate * delta_old + learningRate * grad)

    val delta_p1 = SparseUtil.multiply(delta_old, mtRate)
    val delta_p2 = SparseUtil.multiply(grad, learningRate)

    val delta = SparseUtil.add(delta_p1, delta_p2)

    (SparseUtil.subtraction(w, delta), delta)
  }


  def updateV(v: SparseMatrix, grad: SparseMatrix, delta_old: SparseMatrix, mtRate: Double, learningRate: Double): (SparseMatrix, SparseMatrix) = {
    // v-(mtRate * delta_old + learningRate * grad)

    val delta_p1 = SparseUtil.multiply(delta_old, mtRate)
    val delta_p2 = SparseUtil.multiply(grad, learningRate)

    val delta = SparseUtil.add(delta_p1, delta_p2)

    (SparseUtil.subtraction(v, delta), delta)

  }


  def deltaW0(w: Double, grad: Double, r1: Double, r2: Double): Double = {
    val p1 = (r2) * w
    val p2 = r1 * MLUtil.sign(w) * w

    p1 + p2 + grad

  }

  def deltaW(w: SparseVector, grad: SparseVector, r1: Double, r2: Double): SparseVector = {
    // a * r2 * w + a * r1 * sign(w) :* w + a * 1.0 / n * grad
    val p1 = SparseUtil.multiply(w, r2)
    val p2_1 = SparseUtil.multiply(MLUtil.sign(w), w)
    val p2 = SparseUtil.multiply(p2_1, r1)

    val p1_p2 = SparseUtil.add(p1, p2)
    val p1_p2_p3 = SparseUtil.add(grad, p1_p2)

    p1_p2_p3
  }


  def deltaV(v: SparseMatrix, grad: SparseMatrix, r1: Double, r2: Double): SparseMatrix = {
    //     r2 * v + r1 * sign(v) :* v +  1.0 / n * grad

    val p1 = SparseUtil.multiply(v, r2)
    val p2_1 = SparseUtil.multiply(MLUtil.sign(v), v)
    val p2 = SparseUtil.multiply(p2_1, r1)

    val p1_p2 = SparseUtil.add(p1, p2)
    val p1_p2_p3 = SparseUtil.add(grad, p1_p2)

    p1_p2_p3

  }


  def paramsDelta(
                   params_delta_old: FMParams,
                   grad_regularization: FMGradParams,
                   mtRate: Double,
                   learningRate: Double
                   ): FMParams = {

    val params_delta_w0_new = paramsDeltaW0(params_delta_old.w0, grad_regularization.grad_w0, mtRate, learningRate)

    val params_delta_w_new = paramsDeltaW(params_delta_old.w, grad_regularization.grad_w, mtRate, learningRate)
    val params_delta_v_new = paramsDeltaV(params_delta_old.v, grad_regularization.grad_v, mtRate, learningRate)
    FMParams(params_delta_w0_new, params_delta_w_new, params_delta_v_new)
  }


  def paramsDeltaW0(w_delta: Double, grad: Double, mtRate: Double, learningRate: Double): Double = {
    //    a * w_delta +  b * grad

    mtRate * w_delta + -1 *  learningRate * grad


  }

  def paramsDeltaW(w_delta: SparseVector, grad: SparseVector, mtRate: Double, learningRate: Double): SparseVector = {
    //    a * w_delta +  -1* b * grad

    val p1 = SparseUtil.multiply(w_delta, mtRate)
    val p2 = SparseUtil.multiply(grad, -1.0 *  learningRate)
    SparseUtil.add(p1, p2)
  }


  def paramsDeltaV(v_delta: SparseMatrix, grad: SparseMatrix, mtRate: Double, learningRate: Double): SparseMatrix = {
    //    a * w_delta +  b * grad

    val p1 = SparseUtil.multiply(v_delta, mtRate)
    val p2 = SparseUtil.multiply(grad, -1.0 *  learningRate)
    SparseUtil.add(p1, p2)

  }

}
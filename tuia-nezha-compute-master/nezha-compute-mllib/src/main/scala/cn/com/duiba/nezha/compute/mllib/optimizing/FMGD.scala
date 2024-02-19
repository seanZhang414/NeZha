package cn.com.duiba.nezha.compute.mllib.optimizing

import cn.com.duiba.nezha.compute.api.constant.GlobalConstant
import cn.com.duiba.nezha.compute.api.point.Point
import Point._
import cn.com.duiba.nezha.compute.api.point.Point
import cn.com.duiba.nezha.compute.mllib.util.{MLUtil, SparseUtil}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.mllib.linalg.{Vectors, Vector, SparseMatrix, SparseVector}

/**
 * Logistic regression based classification.
 */
object FMGD {

  def p_delta(fp_old: FMParams, fp_new: FMParams): Double = {

    val p_w0 = (fp_old.w0 - fp_new.w0) * (fp_old.w0 - fp_new.w0)
    val o_n_w = SparseUtil.subtraction(fp_old.w, fp_new.w)

    val p_w = SparseUtil.dot(o_n_w, o_n_w)
    val o_n_v = SparseUtil.subtraction(fp_old.v, fp_new.v)
    val o_n_v_2 = SparseUtil.multiply(o_n_v)

    val p_v = SparseUtil.sum(o_n_v_2)

    p_w0 + p_w + p_v

  }

  def p_delta_mse(fp_delta: FMParams): Double = {

    val p_w0 = (fp_delta.w0) * (fp_delta.w0)

    val p_w = SparseUtil.dot(fp_delta.w, fp_delta.w)

    val o_n_v_2 = SparseUtil.multiply(fp_delta.v)
    val p_v = SparseUtil.sum(o_n_v_2)

    Math.sqrt((p_w0 + p_w + p_v))

  }


  def p_rmse(fp_old: FMParams, fp_new: FMParams): Double = {

    val p_w0 = (fp_old.w0 - fp_new.w0) * (fp_old.w0 - fp_new.w0)
    val o_n_w = SparseUtil.subtraction(fp_old.w, fp_new.w)

    val p_w = SparseUtil.dot(o_n_w, o_n_w)
    val o_n_v = SparseUtil.subtraction(fp_old.v, fp_new.v)
    val o_n_v_2 = SparseUtil.multiply(o_n_v)

    val p_v = SparseUtil.sum(o_n_v_2)

    val p_size = 1 + fp_old.w.numActives + fp_old.v.numActives

    Math.sqrt((p_w0 + p_w + p_v) / p_size)

  }

  def g_rmse(fmps: FMGradParams): Double = {

    val p_w0 = (fmps.grad_w0) * (fmps.grad_w0)

    val p_w = SparseUtil.dot(fmps.grad_w, fmps.grad_w)

    val o_n_v_2 = SparseUtil.multiply(fmps.grad_v)
    val p_v = SparseUtil.sum(o_n_v_2)

    val p_size = 1 + fmps.grad_w.numActives + fmps.grad_v.numActives
    Math.sqrt((p_w0 + p_w + p_v) / p_size)

  }

  def grad(fmps: FMGradParams, batchSize: Double): FMGradParams = {

    val grad_w0 = fmps.grad_w0 / batchSize

    val grad_w = SparseUtil.multiply(fmps.grad_w, 1.0 / batchSize)
    val grad_v = SparseUtil.multiply(fmps.grad_v, 1.0 / batchSize)

    FMGradParams(grad_w0, grad_w, grad_v)

  }


  def gradSqrt(grad_1: FMGradParams): FMGradParams = {

    val grad_w0 = Math.sqrt(grad_1.grad_w0)

    val grad_w = SparseUtil.sqrt(grad_1.grad_w)

    val grad_v = SparseUtil.sqrt(grad_1.grad_v)


    FMGradParams(grad_w0, grad_w, grad_v)


  }

  def gradInverse(grad_1: FMGradParams): FMGradParams = {

    val grad_w0 = 1 / (grad_1.grad_w0 + GlobalConstant.EPSILON)

    val grad_w = SparseUtil.inverse(grad_1.grad_w)

    val grad_v = SparseUtil.inverse(grad_1.grad_v)


    FMGradParams(grad_w0, grad_w, grad_v)


  }

  def gradMergeAddUpdate(grad_1: FMGradParams, grad_2: FMGradParams, a: Double, b: Double): FMGradParams = {

    val grad_w0 = a * grad_1.grad_w0 + b * grad_2.grad_w0

    val grad_w_part1 = SparseUtil.multiply(grad_1.grad_w, a)
    val grad_w_part2 = SparseUtil.multiply(grad_2.grad_w, b)
    val grad_w = SparseUtil.add(grad_w_part1, grad_w_part2)

    val grad_v_part1 = SparseUtil.multiply(grad_1.grad_v, a)
    val grad_v_part2 = SparseUtil.multiply(grad_2.grad_v, b)
    val grad_v = SparseUtil.add(grad_v_part1, grad_v_part2)


    FMGradParams(grad_w0, grad_w, grad_v)


  }

  def gradSquare(grad_1: FMGradParams): FMGradParams = {

    val grad_w0 = grad_1.grad_w0 * grad_1.grad_w0

    val grad_w = SparseUtil.multiply(grad_1.grad_w)

    val grad_v = SparseUtil.multiply(grad_1.grad_v)

    FMGradParams(grad_w0, grad_w, grad_v)


  }


  def gradMultiply(grad_1: FMGradParams, grad_2: FMGradParams): FMGradParams = {

    val grad_w0 = grad_1.grad_w0 * grad_2.grad_w0

    val grad_w = SparseUtil.multiply(grad_1.grad_w, grad_2.grad_w)

    val grad_v = SparseUtil.multiply(grad_1.grad_v, grad_2.grad_v)
    FMGradParams(grad_w0, grad_w, grad_v)


  }


  def gradMultiply(grad_1: FMGradParams, factor: Double): FMGradParams = {

    val grad_w0 = grad_1.grad_w0 * factor

    val grad_w = SparseUtil.multiply(grad_1.grad_w, factor)

    val grad_v = SparseUtil.multiply(grad_1.grad_v, factor)

    FMGradParams(grad_w0, grad_w, grad_v)

  }



  def gradWithRegularization(
                              psOld: FMParams,
                              grad: FMGradParams,
                              r1: Double,
                              r2: Double
                              ): FMGradParams = {

    val w0_new = gradWithRegularizationW0(psOld.w0, grad.grad_w0, r1, r2)
    val w_new = gradWithRegularizationW(psOld.w, grad.grad_w, r1, r2)
    val v_new = gradWithRegularizationV(psOld.v, grad.grad_v, r1, r2)
    FMGradParams(w0_new, w_new, v_new)
  }


  def gradWithRegularizationW0(w: Double, grad: Double, r1: Double, r2: Double): Double = {
    //    w -a*       (r2*w+ r1*sign(w) +grad_part)
    val p1 = r2 * w
//    val p2 = r1 * MLUtil.sign(w) * w
    val p2 = r1 * MLUtil.sign(w)
    p1 + p2 + grad

  }

  def gradWithRegularizationW(w: SparseVector, grad: SparseVector, r1: Double, r2: Double): SparseVector = {
    // (r2*w  +  r1*sign(w)*w  +  grad_part)

    val p1 = SparseUtil.multiply(w, r2)
//    val p2_1 = SparseUtil.multiply(MLUtil.sign(w), w)
    val p2 = SparseUtil.multiply(MLUtil.sign(w), r1)

    val p1_p2 = SparseUtil.add(p1, p2)
    val p1_p2_p3 = SparseUtil.add(grad, p1_p2)

    p1_p2_p3
  }


  def gradWithRegularizationV(v: SparseMatrix, grad: SparseMatrix, r1: Double, r2: Double): SparseMatrix = {
    // (r2*w  +  r1*sign(w)*w  +  grad_part)

    val p1 = SparseUtil.multiply(v, r2)
//    val p2_1 = SparseUtil.multiply(MLUtil.sign(v), v)
    val p2 = SparseUtil.multiply(MLUtil.sign(v), r1)

    val p1_p2 = SparseUtil.add(p1, p2)
    val p1_p2_p3 = SparseUtil.add(grad, p1_p2)

    p1_p2_p3

  }


  def computeWithErr(p: LabeledSPoint, fm_ps: Broadcast[FMParams], F: Broadcast[Int]): (FMGradParams, Double) = {

    val err = (h(p.x, fm_ps.value) - p.y)

    // grad w0
    val grad_w0 = err
    // grad w
    val grad_w = SparseUtil.multiply(p.x, err)

    // grad v
    val inter_1 = SparseUtil.dot_row(fm_ps.value.v, p.x)
    val grad_v_p1 = SparseUtil.dot_m(p.x, inter_1)

    val x_2 = SparseUtil.multiply(p.x)
    val grad_v_p3 = SparseUtil.multiply(x_2, F.value, fm_ps.value.v)
    val grad_v_p4 = SparseUtil.subtraction(grad_v_p1, grad_v_p3)

    val grad_v = SparseUtil.multiply(grad_v_p4, err)



    (FMGradParams(grad_w0, grad_w, grad_v), err * err)
  }


  def computeWithErr2(p: LabeledSPoint, fm_ps: Broadcast[FMModelParams], F: Broadcast[Int]): (FMGradParams, Double) = {

    val err = (h(p.x, fm_ps.value) - p.y)

    // grad w0
    val grad_w0 = err
    // grad w
    val grad_w = SparseUtil.multiply(p.x, err)

    // grad v
    val inter_1 = SparseUtil.dot_row(fm_ps.value.v, p.x)
    val grad_v_p1 = SparseUtil.dot_m(p.x, inter_1)
    val x_2 = SparseUtil.multiply(p.x)
    val grad_v_p3 = SparseUtil.multiply(x_2, F.value, fm_ps.value.v)
    val grad_v_p4 = SparseUtil.subtraction(grad_v_p1, grad_v_p3)

    val grad_v = SparseUtil.multiply(grad_v_p4, err)



    (FMGradParams(grad_w0, grad_w, grad_v), err * err)
  }


  def compute(p: LabeledSPoint, fm_ps: Broadcast[FMParams], F: Broadcast[Int]): FMGradParams = {

    val (fm_gps, err2) = computeWithErr(p, fm_ps, F)
    fm_gps
  }


  def paramsUpdate(
                    psOld: FMParams,
                    psDelta: FMParams
                    ): FMParams = {
    val params_w0 = psOld.w0 + psDelta.w0

    val params_w = SparseUtil.add(psOld.w, psDelta.w)
    val params_v = SparseUtil.add(psOld.v, psDelta.v)

    FMParams(params_w0, params_w, params_v)
  }


  def h2(x: SparseVector, fm_ps: FMParams): Double = {

    val inter_1 = SparseUtil.dot_row(fm_ps.v, x)
    val inter_1_2 = SparseUtil.multiply(inter_1)

    val x_2 = SparseUtil.multiply(x)
    val v_2 = SparseUtil.multiply(fm_ps.v)
    val inter_2 = SparseUtil.dot_row(v_2, x_2)

    val interaction = SparseUtil.sum(inter_1_2) - SparseUtil.sum(inter_2)
    //
    val inx = fm_ps.w0 + SparseUtil.dot(x, fm_ps.w) + 0.5 * interaction


    MLUtil.sigmoid(inx)
  }


  def h(x: SparseVector, fm_ps: FMModelParams): Double = {
    //
    //    val x_copy_F = SparseUtil.vector_copy(x, fm_ps.v.numCols)
    //
    //    //    val x_one = SparseUtil.ones(x.size,1.0)

    val v_x = SparseUtil.multiply(x, fm_ps.v.numCols, fm_ps.v)

    val v2_x2 = SparseUtil.multiply(v_x)

    val v_x_rcount = SparseUtil.sum_row(v_x)

    val v_x_rcount2 = SparseUtil.multiply(v_x_rcount)

    val v2_x2_rcount = SparseUtil.sum_row(v2_x2)

    val interaction = SparseUtil.sum(v_x_rcount2) - SparseUtil.sum(v2_x2_rcount)


    //
    val inx = fm_ps.w0 + SparseUtil.dot3(x, fm_ps.w) + 0.5 * interaction

    MLUtil.sigmoid(inx)
  }


  def h(x: SparseVector, fm_ps: FMParams): Double = {

    val x_copy_F = SparseUtil.vector_copy(x, fm_ps.v.numCols)

    //    val x_one = SparseUtil.ones(x.size,1.0)

    val v_x = SparseUtil.multiply(x_copy_F, fm_ps.v)

    val v2_x2 = SparseUtil.multiply(v_x)

    val v_x_rcount = SparseUtil.sum_row(v_x)

    val v_x_rcount2 = SparseUtil.multiply(v_x_rcount)

    val v2_x2_rcount = SparseUtil.sum_row(v2_x2)

    val interaction = SparseUtil.sum(v_x_rcount2) - SparseUtil.sum(v2_x2_rcount)


    //
    val inx = fm_ps.w0 + SparseUtil.dot(x, fm_ps.w) + 0.5 * interaction

    //    println(s"fm_ps.w0=(${fm_ps.w0}) + SparseUtil.dot(x, fm_ps.w)=(${SparseUtil.dot(x, fm_ps.w)}}) + 0.5* interaction=(${interaction}})")
    //
    //    println("x.numActives="+x.numActives)
    //    println("w.numActives="+fm_ps.w.numActives)
    //    println("v.numActives="+fm_ps.v.numActives)
    //    println("x_copy_F.numActives="+x_copy_F.numActives)
    //    println("v_x.numActives="+v_x.numActives)
    //    println("v2_x2.numActives="+v2_x2.numActives)
    //    println("v_x_rcount.numActives="+v_x_rcount.numActives)
    //    println("v_x_rcount2.numActives="+v_x_rcount2.numActives)
    //    println("v2_x2_rcount.numActives="+v2_x2_rcount.numActives)

    MLUtil.sigmoid(inx)
  }


}
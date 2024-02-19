package cn.com.duiba.nezha.compute.mllib.optimizing.ad

import cn.com.duiba.nezha.compute.api.constant.GlobalConstant
import cn.com.duiba.nezha.compute.api.point.Point
import Point.{FMGradParams, FMParams}
import cn.com.duiba.nezha.compute.api.point.Point
import cn.com.duiba.nezha.compute.mllib.util.SparseUtil
import org.apache.spark.mllib.linalg._

/**
 * Logistic regression based classification.
 */
object SparseFMADUpdater {





  def paramsDelta(
                   ps_delta_mse: FMParams,
                   grad: FMGradParams,
                   grad_mse: FMGradParams,
                   r1: Double,
                   r2: Double
                   ): FMParams = {

    val w0_new = paramsDeltaW0(ps_delta_mse.w0, grad.grad_w0, grad_mse.grad_w0, r1, r2)

    val w_new = paramsDeltaW(ps_delta_mse.w, grad.grad_w, grad_mse.grad_w, r1, r2)
    val v_new = paramsDeltaV(ps_delta_mse.v, grad.grad_v, grad_mse.grad_v, r1, r2)
    FMParams(w0_new, w_new, v_new)
  }


  def paramsDeltaW0(w_delta_mse: Double, grad: Double, grad_mse: Double, r1: Double, r2: Double): Double = {
    //    w - RMS[delta_w]/ RMS[g]*grad
    //   -1 * RMS[delta_w]/ RMS[g]*grad


//    println(s"w_delta_mse=${w_delta_mse},grad_mse=${grad_mse}")


    -1 * math.sqrt(w_delta_mse) / (math.sqrt(grad_mse) + GlobalConstant.EPSILON) * grad

  }

  def paramsDeltaW(w_delta_mse: SparseVector, grad: SparseVector, grad_mse: SparseVector, r1: Double, r2: Double): SparseVector = {
    //    w - RMS[delta_w]/ RMS[g]*grad
    //   RMS[delta_w]/ RMS[g]*grad

    val p1 = SparseUtil.multiply(grad, -1.0)

    val p2 = SparseUtil.sqrt(w_delta_mse)

    val grad_rmse = SparseUtil.sqrt(grad_mse)
    val p3 = SparseUtil.inverse(grad_rmse)


    val p1_p2 = SparseUtil.multiply(p1, p2)
    val p1_p2_p3 = SparseUtil.multiply(p1_p2, p3)

    p1_p2_p3

  }


  def paramsDeltaV(w_delta_mse: SparseMatrix, grad: SparseMatrix, grad_mse: SparseMatrix, r1: Double, r2: Double): SparseMatrix = {
    //    w - RMS[delta_w]/ RMS[g]*grad
    //   RMS[delta_w]/ RMS[g]*grad

    val p1 = SparseUtil.multiply(grad, -1.0)

    val p2 = SparseUtil.sqrt(w_delta_mse)

    val grad_rmse = SparseUtil.sqrt(grad_mse)
    val p3 = SparseUtil.inverse(grad_rmse)


    val p1_p2 = SparseUtil.multiply(p1, p2)
    val p1_p2_p3 = SparseUtil.multiply(p1_p2, p3)

    p1_p2_p3

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

  def gradMseUpdate(grad_mse: FMGradParams, grad: FMGradParams, eta: Double): FMGradParams = {

    val grad_w0_mse = eta * grad_mse.grad_w0 + (1 - eta) * grad.grad_w0 * grad.grad_w0

    val grad_w_mse_part1 = SparseUtil.multiply(grad_mse.grad_w, eta)
    val grad_w_mse_part2 = SparseUtil.multiply(grad.grad_w)
    val grad_w_mse_part3 = SparseUtil.multiply(grad_w_mse_part2, (1 - eta))
    val grad_w_mse = SparseUtil.add(grad_w_mse_part1, grad_w_mse_part3)

    val grad_v_mse_part1 = SparseUtil.multiply(grad_mse.grad_v, eta)
    val grad_v_mse_part2 = SparseUtil.multiply(grad.grad_v)
    val grad_v_mse_part3 = SparseUtil.multiply(grad_v_mse_part2, (1 - eta))
    val grad_v_mse = SparseUtil.add(grad_v_mse_part1, grad_v_mse_part3)


    FMGradParams(grad_w0_mse, grad_w_mse, grad_v_mse)


  }

  def paramsDeltaUpdate(params_delta_mse: FMParams, params_delta: FMParams, eta: Double): FMParams = {

    val params_w0_mse = eta * params_delta_mse.w0 + (1 - eta) * params_delta.w0 * params_delta.w0

    val params_w_mse_part1 = SparseUtil.multiply(params_delta_mse.w, eta)
    val params_w_mse_part2 = SparseUtil.multiply(params_delta.w)
    val params_w_mse_part3 = SparseUtil.multiply(params_w_mse_part2, (1 - eta))
    val params_w_mse = SparseUtil.add(params_w_mse_part1, params_w_mse_part3)

    val params_v_mse_part1 = SparseUtil.multiply(params_delta_mse.v, eta)
    val params_v_mse_part2 = SparseUtil.multiply(params_delta.v)
    val params_v_mse_part3 = SparseUtil.multiply(params_v_mse_part2, (1 - eta))
    val params_v_mse = SparseUtil.add(params_v_mse_part1, params_v_mse_part3)


    FMParams(params_w0_mse, params_w_mse, params_v_mse)


  }

  def paramsDelta(params_new: FMParams, params_old: FMParams): FMParams = {

    val params_w0_delta = params_new.w0 - params_old.w0

    val params_w_delta = SparseUtil.subtraction(params_new.w, params_old.w)
    val params_v_delta = SparseUtil.subtraction(params_new.v, params_old.v)



    FMParams(params_w0_delta, params_w_delta, params_v_delta)


  }


}
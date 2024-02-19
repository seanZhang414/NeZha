package cn.com.duiba.nezha.compute.mllib.optimizing.adam

import cn.com.duiba.nezha.compute.api.constant.GlobalConstant
import cn.com.duiba.nezha.compute.api.point.Point
import cn.com.duiba.nezha.compute.api.point.Point.{FMGradParams, FMParams}
import cn.com.duiba.nezha.compute.mllib.optimizing.FMGD
import cn.com.duiba.nezha.compute.mllib.util.SparseUtil
import org.apache.spark.mllib.linalg._

/**
 * Logistic regression based classification.
 */
object SparseFMAdamUpdater {




  def gradNew(grad_1: FMGradParams, grad_2: FMGradParams): FMGradParams = {

    val grad_2_sqrt = FMGD.gradSqrt(grad_2)

    val grad_2_sqrt_ = FMGD.gradInverse(grad_2_sqrt)

    FMGD.gradMultiply(grad_1,grad_2_sqrt_)
  }

  def learningRateUpdate(learningRate: Double,beta1:Double,beta2:Double,t:Int ): Double = {
    val beta1_p= 1-Math.pow(beta1, t)

    val beta2_p= 1-Math.pow(beta2, t)

    learningRate*Math.sqrt(beta2_p)/beta1_p
  }



  def paramsUpdate(params: FMParams,  grad: FMGradParams, learningRate: Double): FMParams = {

    val params_w0 = params.w0 - learningRate*grad.grad_w0

    val params_w_part1 = SparseUtil.multiply(grad.grad_w, -learningRate)
    val params_w = SparseUtil.add(params.w, params_w_part1)

    val params_v_part1 = SparseUtil.multiply(grad.grad_v, -learningRate)
    val params_v = SparseUtil.add(params.v, params_v_part1)

    FMParams(params_w0, params_w, params_v)


  }

  def paramsDelta(params_new: FMParams, params_old: FMParams): FMParams = {

    val params_w0_delta = params_new.w0 - params_old.w0

    val params_w_delta = SparseUtil.subtraction(params_new.w, params_old.w)
    val params_v_delta = SparseUtil.subtraction(params_new.v, params_old.v)



    FMParams(params_w0_delta, params_w_delta, params_v_delta)


  }


}
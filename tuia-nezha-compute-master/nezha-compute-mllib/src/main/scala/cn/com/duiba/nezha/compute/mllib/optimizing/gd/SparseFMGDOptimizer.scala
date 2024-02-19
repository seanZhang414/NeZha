package cn.com.duiba.nezha.compute.mllib.optimizing.gd

import cn.com.duiba.nezha.compute.api.point.Point
import Point.{FMGradParams, FMParams, LabeledSPoint}
import cn.com.duiba.nezha.compute.api.point.Point
import cn.com.duiba.nezha.compute.mllib.optimizing.{FMGD, SparseFMUpdater}
import cn.com.duiba.nezha.compute.mllib.util.SparseUtil
import org.apache.spark.rdd.RDD

/**
 * Logistic regression based classification.
 */
object SparseFMGDOptimizer {

  

  def run(data: RDD[LabeledSPoint], F: Int, learningRate: Double, r1: Double, r2: Double, MAX_ITERATIONS: Int, MIN_ITERATIONS: Int, DELTA_THRESHOLD: Double): FMParams = {
    // Initialize w to a random value

    val D = data.first().x.size
    val N = data.count()

    var fm_params = FMParams(SparseUtil.rand(0), SparseUtil.rand(D, 0), SparseUtil.rand(D, F, 0))



    var delta = 1.0
    var i = 0
    while (delta > DELTA_THRESHOLD && i < MAX_ITERATIONS) {
      //
      println("On iteration " + i)

      // 计算梯度偏差


      val fm_params_b = data.context.broadcast(fm_params)
      val F_b = data.context.broadcast(F)

      // 计算梯度偏差
      val data_g = data.map(p => {
        FMGD.compute(p, fm_params_b, F_b)
      }).cache()

      val grad_batch = data_g.reduce((g1, g2) => FMGradParams((g1.grad_w0 + g2.grad_w0), SparseUtil.add(g1.grad_w, g2.grad_w), SparseUtil.add(g1.grad_v, g2.grad_v)))

      val grad = FMGD.grad(grad_batch,N)

      // 更新参数值

      val params_new = SparseFMUpdater.update(fm_params, grad, learningRate, r1, r2)

      //
      if (i >= MIN_ITERATIONS) {
        delta = FMGD.p_delta(fm_params, params_new)
      }
      //
      fm_params = params_new
      i += 1
    }

    fm_params
  }


}
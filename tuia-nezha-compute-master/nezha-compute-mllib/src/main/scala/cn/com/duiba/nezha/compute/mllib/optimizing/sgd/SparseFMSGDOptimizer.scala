package cn.com.duiba.nezha.compute.mllib.optimizing.sgd

import java.util.Random
import cn.com.duiba.nezha.compute.api.point.Point
import cn.com.duiba.nezha.compute.api.point.Point.{FMModelParams, FMGradParams, FMParams, LabeledSPoint}
import cn.com.duiba.nezha.compute.api.point.Point
import cn.com.duiba.nezha.compute.common.enums.DateStyle
import cn.com.duiba.nezha.compute.common.util.DateUtil
import cn.com.duiba.nezha.compute.mllib.optimizing.{FMGD, SparseFMUpdater}
import cn.com.duiba.nezha.compute.mllib.util.SparseUtil
import org.apache.spark.rdd.RDD

/**
 * Logistic regression based classification.
 */
object SparseFMSGDOptimizer {


  def run(data: RDD[LabeledSPoint], batchSize: Int, F: Int, learningRate: Double, r1: Double, r2: Double, MAX_ITERATIONS: Int, MIN_ITERATIONS: Int, DELTA_THRESHOLD: Double): FMParams = {
    // Initialize w to a random value
    val rand = new Random(11L)
    val D = data.first().x.size
    val N = data.cache().count()

    var fm_params = FMParams(SparseUtil.rand(0), SparseUtil.rand(D, 0), SparseUtil.rand(D, F, 0))

    var delta = 999999.0
    var i = 0


    val batchNums = N / batchSize + 1

    val batchWeightArray = Array.tabulate[Double](batchNums.toInt)(i=>1.0/batchNums)




    while (delta > DELTA_THRESHOLD && i < MAX_ITERATIONS) {
      //

      val splitsData = data.randomSplit(batchWeightArray, seed = rand.nextInt())
      var j = 0
      while (delta > DELTA_THRESHOLD && j < batchNums) {

        println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + s", On iteration(i=${i},j=${j})")

        val F_b = data.context.broadcast(F)
        println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + s",grad computer start ,batchSize=${batchSize}")

        val fm_m_params_b = data.context.broadcast(FMModelParams(fm_params.w0, fm_params.w.toDense, fm_params.v.toDense))
        val grad_and_e = splitsData(j).map(p => {
          FMGD.computeWithErr2(p, fm_m_params_b, F_b)
        }).reduce((ge1, ge2) =>
          (FMGradParams((ge1._1.grad_w0 + ge2._1.grad_w0),
            SparseUtil.add(ge1._1.grad_w, ge2._1.grad_w),
            SparseUtil.add(ge1._1.grad_v, ge2._1.grad_v)
          ), ge1._2 + ge2._2)
        )


        println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + s",grad computer end ")
        val grad = FMGD.grad(grad_and_e._1, batchSize)
        val rmse = math.sqrt(grad_and_e._2 / batchSize)

        // 更新参数
        val params_new = SparseFMUpdater.update(fm_params, grad, learningRate, r1, r2)

        val grad_rmse = FMGD.g_rmse(grad)
        val p_delta_rmse = FMGD.p_rmse(fm_params, params_new)
        val p_delta = FMGD.p_delta(fm_params, params_new)

        println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + s",delta=${delta}, p_delta = ${p_delta}, p_delta_rmse=${p_delta_rmse}, grad_rmse=${grad_rmse},rmse=${rmse}")
        if (i >= MIN_ITERATIONS && j < batchNums - 1) {
          delta = p_delta
        }

        j += 1

        fm_params = params_new
      }

      i += 1
    }
    fm_params


  }

}
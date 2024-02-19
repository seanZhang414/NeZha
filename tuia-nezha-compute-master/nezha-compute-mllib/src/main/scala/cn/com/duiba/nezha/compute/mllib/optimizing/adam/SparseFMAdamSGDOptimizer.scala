package cn.com.duiba.nezha.compute.mllib.optimizing.adam

import java.util.Random

import cn.com.duiba.nezha.compute.api.point.Point.{FMGradParams, FMModelParams, FMParams, LabeledSPoint}
import cn.com.duiba.nezha.compute.common.enums.DateStyle
import cn.com.duiba.nezha.compute.common.util.DateUtil
import cn.com.duiba.nezha.compute.mllib.optimizing.FMGD
import cn.com.duiba.nezha.compute.mllib.util.SparseUtil
import org.apache.spark.rdd.RDD

/**
 * Logistic regression based classification.
 */
object SparseFMAdamSGDOptimizer {


  def run(data: RDD[LabeledSPoint], batchSize: Int, F: Int, beta1: Double, beta2: Double, learningRate: Double, r1: Double, r2: Double, MAX_ITERATIONS: Int, MIN_ITERATIONS: Int, DELTA_THRESHOLD: Double): FMParams = {
    // Initialize w to a random value
    val rand = new Random(11L)
    val D = data.first().x.size
    val N = data.cache().count()
    val F_b = data.context.broadcast(F)

    // 初始化 参数
    var fm_params = FMParams(SparseUtil.rand(0, 0.5), SparseUtil.rand(D, 0, 0.1), SparseUtil.rand(D, F, 0, 0.01))

    var params_new = fm_params

    var delta = 999999.0
    var i = 0
    var t = 0

    var m_t: FMGradParams = FMGradParams(0.0, SparseUtil.zero(D), SparseUtil.zero(D, F))
    var v_t: FMGradParams = FMGradParams(0.0, SparseUtil.zero(D), SparseUtil.zero(D, F))

    var grad_mse: FMGradParams = FMGradParams(0.0, SparseUtil.zero(D), SparseUtil.zero(D, F))

    while (delta > DELTA_THRESHOLD && i < MAX_ITERATIONS) {

      val batch = Math.round(batchSize * (1))
      val batchNums = N / batch + 1
      val batchWeightArray = Array.tabulate[Double](batchNums.toInt)(ti=>1.0/batchNums)
      val splitsData = data.randomSplit(batchWeightArray, seed = rand.nextInt())

      //
      var j = 0
      while (delta > DELTA_THRESHOLD && j < batchNums) {
        println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + s", On iteration(i=${i},j_total=${batchNums},j=${j})")
        t += 1
        // 0 compute gradients gt
        //        val fm_params_b = data.context.broadcast(fm_params)
        val fm_m_params_b = data.context.broadcast(FMModelParams(fm_params.w0, fm_params.w.toDense, fm_params.v.toDense))

        println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + s",grad computer start ,batchSize=${batch}")

        val grad_and_e = splitsData(j).map(p => {
          FMGD.computeWithErr2(p, fm_m_params_b, F_b)
        }).reduce((ge1, ge2) =>
          (FMGradParams((ge1._1.grad_w0 + ge2._1.grad_w0),
            SparseUtil.add(ge1._1.grad_w, ge2._1.grad_w),
            SparseUtil.add(ge1._1.grad_v, ge2._1.grad_v)
          ), ge1._2 + ge2._2)
        )



        println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + s",grad computer end ")
        val grad = FMGD.grad(grad_and_e._1, batch)

        //  0 正则化梯度
        val grad_regularization = FMGD.gradWithRegularization(fm_params, grad, r1, r2)

        // 1 update mt
        m_t = FMGD.gradMergeAddUpdate(m_t, grad_regularization, beta1, 1 - beta1)

        // 2 update vt
        v_t = FMGD.gradMergeAddUpdate(v_t, FMGD.gradSquare(grad_regularization), beta2, 1 - beta2)

        // 3
        val learningRate_t = SparseFMAdamUpdater.learningRateUpdate(learningRate, beta1, beta2, t)

        // 5 params update
        val grad_new = SparseFMAdamUpdater.gradNew(m_t, v_t)
        params_new = SparseFMAdamUpdater.paramsUpdate(fm_params, grad_new, learningRate_t)


        // desc
        val grad_rmse = FMGD.g_rmse(grad)
        val p_delta_rmse = FMGD.p_rmse(fm_params, params_new)
        val p_delta = FMGD.p_delta(fm_params, params_new)
        val rmse = math.sqrt(grad_and_e._2 / batch)

        println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + s", delta=${delta}, p_delta = ${p_delta}, p_delta_rmse=${p_delta_rmse}, grad_rmse=${grad_rmse},rmse=${rmse}")

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


  def comb(ge1: (FMGradParams, Double), ge2: (FMGradParams, Double)): (FMGradParams, Double) = {
    gradAcc(ge1, ge2)
  }


  def gradAcc(ge1: (FMGradParams, Double), ge2: (FMGradParams, Double)): (FMGradParams, Double) = {
    (FMGradParams((ge1._1.grad_w0 + ge2._1.grad_w0),
      SparseUtil.add(ge1._1.grad_w, ge2._1.grad_w),
      SparseUtil.add(ge1._1.grad_v, ge2._1.grad_v)
    ), ge1._2 + ge2._2)
  }
}
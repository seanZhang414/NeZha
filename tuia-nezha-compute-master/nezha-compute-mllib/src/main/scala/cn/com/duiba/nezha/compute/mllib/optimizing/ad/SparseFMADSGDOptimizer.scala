package cn.com.duiba.nezha.compute.mllib.optimizing.ad

import java.util.Random
import cn.com.duiba.nezha.compute.api.point.Point
import cn.com.duiba.nezha.compute.api.point.Point.{FMModelParams, FMGradParams, FMParams, LabeledSPoint}
import cn.com.duiba.nezha.compute.common.enums.DateStyle
import cn.com.duiba.nezha.compute.common.util.DateUtil
import cn.com.duiba.nezha.compute.mllib.optimizing.FMGD
import cn.com.duiba.nezha.compute.mllib.util.SparseUtil
import org.apache.spark.rdd.RDD

/**
 * Logistic regression based classification.
 */
object SparseFMADSGDOptimizer {


  def run(data: RDD[LabeledSPoint], batchSize: Int, F: Int, eta: Double, learningRate: Double, r1: Double, r2: Double, MAX_ITERATIONS: Int, MIN_ITERATIONS: Int, DELTA_THRESHOLD: Double): FMParams = {
    // Initialize w to a random value
    val rand = new Random(11L)
    val D = data.first().x.size
    val N = data.cache().count()
    val F_b = data.context.broadcast(F)

    var fm_params = FMParams(SparseUtil.rand(0, 0.5), SparseUtil.rand(D, 0, 0.1), SparseUtil.rand(D, F, 0, 0.01))


    var params_new = fm_params


    var delta = 999999.0
    var i = 0


    val batchNums = N / batchSize + 1

    val batchWeightArray = Array.tabulate[Double](batchNums.toInt)(i=>1.0/batchNums)

    var grad_mse: FMGradParams = FMGradParams(0.0, SparseUtil.zero(D), SparseUtil.zero(D, F))
    var params_delta_mse = FMParams(learningRate, SparseUtil.ones(D, learningRate), SparseUtil.ones(D, F, learningRate))
    var params_delta = FMParams(0.0, SparseUtil.zero(D), SparseUtil.zero(D, F))


    val splitsData = data.randomSplit(batchWeightArray, seed = rand.nextInt())


    // 数据缓存 并计算每个batch Size
//        val batchSizeArray = new Array[Long](splitsData.length)
//
//        for (i <- 0 to splitsData.length - 1) {
//          splitsData(i).cache
//          batchSizeArray.update(i, splitsData(i).count())
//        }


    while (delta > DELTA_THRESHOLD && i < MAX_ITERATIONS) {
      //

      var j = 0
      while (delta > DELTA_THRESHOLD && j < batchNums) {

        println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + s", On iteration(i=${i},j=${j})")

        // 0 compute gradients gt
        val fm_params_b = data.context.broadcast(fm_params)
        val fm_m_params_b = data.context.broadcast(FMModelParams(fm_params.w0, fm_params.w.toDense, fm_params.v.toDense))



        println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + s",grad computer start ,batchSize=${batchSize}")

        //        val grad_and_e = splitsData(j).treeAggregate((grad_init_b.value, 0.0))(
        //          (ge1, p) => gradAcc(ge1, FMGD.computeWithErr(p, fm_params_b, F_b))
        //          , comb)

        val grad_and_e = splitsData(j).map(p => {
          FMGD.computeWithErr2(p, fm_m_params_b, F_b)
        }).reduce((ge1, ge2) =>
          (FMGradParams((ge1._1.grad_w0 + ge2._1.grad_w0),
            SparseUtil.add(ge1._1.grad_w, ge2._1.grad_w),
            SparseUtil.add(ge1._1.grad_v, ge2._1.grad_v)
          ), ge1._2 + ge2._2)
        )





        println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + s",grad computer end ")
        val grad_batch = grad_and_e._1
        val grad = FMGD.grad(grad_batch, batchSize)

        // 1 compute gradients gt with regularization   gt
        val grad_regularization = FMGD.gradWithRegularization(fm_params, grad, r1, r2)


        // 2 accumulate gradient      E[g2]t
        grad_mse = SparseFMADUpdater.gradMseUpdate(grad_mse, grad_regularization, eta)

        // 3 compute params delta update     E[w2]t
        params_delta = SparseFMADUpdater.paramsDelta(params_delta_mse, grad_regularization, grad_mse, r1, r2)

        // 4 accumulate params delta
        params_delta_mse = SparseFMADUpdater.paramsDeltaUpdate(params_delta_mse, params_delta, eta)

        // 5 params update
        params_new = SparseFMADUpdater.paramsUpdate(fm_params, params_delta)


        val grad_rmse = FMGD.g_rmse(grad_regularization)

        val p_delta_rmse = FMGD.p_rmse(fm_params, params_new)
        val p_delta = FMGD.p_delta(fm_params, params_new)

        val d_p =FMGD.p_delta_mse(params_delta)

        val rmse = math.sqrt(grad_and_e._2 / batchSize)

        println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + s", delta=${delta}, p_delta = ${p_delta}, p_delta_rmse=${p_delta_rmse}, grad_rmse=${grad_rmse},rmse=${rmse},d_p=${d_p}")

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

  //
  //  def seq(ge1: (FMGradParams, Double), p:LabeledSPoint): (FMGradParams, Double) = {
  //    val ge2 =  FMGD.computeWithErr(p, fm_params_b, F_b)
  //    (FMGradParams((ge1._1.grad_w0 + ge2._1.grad_w0),
  //      SparseUtil.add(ge1._1.grad_w, ge2._1.grad_w),
  //      SparseUtil.add(ge1._1.grad_v, ge2._1.grad_v)
  //    ), ge1._2 + ge2._2)
  //  }

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
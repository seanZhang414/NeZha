package cn.com.duiba.nezha.compute.biz.optimizing

import java.util.ArrayList

import cn.com.duiba.nezha.compute.biz.evaluate.ALSEvaluater
import cn.com.duiba.nezha.compute.biz.optimizing.ParameterCombination.ALSBestParams
import cn.com.duiba.nezha.compute.common.params.Params
import Params.ALSOptParams
import cn.com.duiba.nezha.compute.common.params.Params
import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import org.apache.spark.rdd.RDD

/**
 * Created by pc on 2016/11/22.
 */
object ALSRcmdBestParam {

  def getBestParams(data: RDD[Rating], optParams: ALSOptParams):ALSBestParams = {

    // 数据拆分
    val sp = data.randomSplit(optParams.sp)
    val training = sp(0).cache()
    val validation = sp(1).cache()
    val test = sp(2).cache()

    // 数据打印
    val numTraining = training.count()
    val numValidation = validation.count()
    val numTest = test.count()
    println(s"cn.com.duiba.nezha.compute.biz.param opt with  numTraining = $numTraining ,numValidation = $numValidation ,numTest = $numTest")


    // 参数组合赋值
    val ranks = optParams.ranks
    val lambdas = optParams.lambdas
    val numiters = optParams.numiters
    val implicitPrefs = optParams.implicitPrefs

    println(s" cn.com.duiba.nezha.compute.biz.param combination: implicitPrefs = $implicitPrefs")
    println(s" cn.com.duiba.nezha.compute.biz.param combination: lambdas = $lambdas")
    println(s" cn.com.duiba.nezha.compute.biz.param combination: ranks = $ranks")
    println(s" cn.com.duiba.nezha.compute.biz.param combination: numiters = $numiters")

    // 初始化最优模型
    var bestModel: Option[MatrixFactorizationModel] = None

    // 初始化最优参数组合
    val paramsArrayList =new ArrayList[String]();
    var bestValidationRmse = Double.MaxValue
    var bestRank = 0
    var bestLamdba = -1.0
    var bestNumIter = 1

    // 循环遍历所有参数组合,查找最优参数组合
    for (rank <- ranks;numiter <- numiters; lambda <- lambdas ) {
      val model = ALS.train(training, rank, numiter, lambda)
      val valadationRmse = ALSEvaluater.computeRmse(model, validation, implicitPrefs)
      if (valadationRmse < bestValidationRmse) {
        bestModel = Some(model)
        bestValidationRmse = valadationRmse
        bestRank = rank
        bestLamdba = lambda
        bestNumIter = numiter
      }
      //
      val paramC = s"train with params rank = $rank, lambda = $lambda, numiter = $numiter, valadationRmse = $valadationRmse"
      paramsArrayList.add(paramC)
      println(paramC)

    }

    // 测试数据集评估
    val testRmse = ALSEvaluater.computeRmse(bestModel.get, test, implicitPrefs)

    // 提升效果
    // 计算训练样本和验证样本的平均分数
    val meanR = training.union(validation).map(x => x.rating).mean()

    //使用平均分做预测，test样本的rmse
    val baseRmse = math.sqrt(test.map(x => (meanR - x.rating) * (meanR - x.rating)).mean())
    val improvement = (baseRmse - testRmse) / baseRmse * 100


    val bestParams = ALSBestParams(bestNumIter,bestLamdba,bestRank,bestValidationRmse,baseRmse,improvement)
    println(s" bestRank = $bestRank, bestLamdba = $bestLamdba, bestNumIter = $bestNumIter")
    println(s" testALSRmse = $testRmse,testBaseRmse $baseRmse,improvement = $improvement")

    // 返回结果
    bestParams
  }
}

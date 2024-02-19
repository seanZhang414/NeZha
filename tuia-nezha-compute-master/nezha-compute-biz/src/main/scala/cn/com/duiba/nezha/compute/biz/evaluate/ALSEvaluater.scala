package cn.com.duiba.nezha.compute.biz.evaluate

import org.apache.spark.mllib.recommendation.{MatrixFactorizationModel, Rating}
import org.apache.spark.rdd.RDD

/**
 * Created by pc on 2016/11/22.
 */
object ALSEvaluater {


  /** Compute RMSE (Root Mean Squared Error). */
  def computeRmse(model: MatrixFactorizationModel, data: RDD[Rating], implicitPrefs: Boolean): Double
  = {

    // 计算均方差
    val mse = evaluateMse(data, model, implicitPrefs)

    // 计算标准差
    val rmse = math.sqrt(mse)
    // 返回结果
    rmse

  }

  /** Compute MSE (Mean Squared Error). */
  def evaluateMse(data: RDD[Rating], model: MatrixFactorizationModel, implicitPrefs: Boolean): Double = {

    //使用训练数据训练模型
    val usersProducets = data.map(r => r match {
      case Rating(user, product, rate) => (user, product)
    })

    //预测数据
    //mapPredictedRating把预测的分值映射为1或者0
    //join连接原始的分数,连接的key为x.user, x.product
    //values方法表示只保留预测值，真实值
    val predictions = model.predict(usersProducets).map(u => u match {
      case Rating(user, product, rate) => ((user, product), mapPredictedRating(rate, implicitPrefs))
    })

    //将真实分数与预测分数进行合并
    val ratesAndPreds = data.map(r => r match {
      case Rating(user, product, rate) =>
        ((user, product), mapValidationRating(rate,implicitPrefs))
    }).join(predictions)

    //计算均方差
    val mse = ratesAndPreds.map(r => r match {
      case ((user, product), (r1, r2)) =>
        var err = (r1 - r2)
        err * err
    }).mean()

    //return
    mse

  }

  /** 预测值,区间规范化. */
  def mapPredictedRating(r: Double, implicitPrefs: Boolean): Double = {
    // 若implicitPref
    // 开启   分值=预测的分值大于0的为1,小于0的为0
    // 未开启 分值=预测分值
    if (implicitPrefs) math.max(math.min(r, 1.0), 0.0) else r
  }

  /** 真实值,区间规范化. */
  def mapValidationRating(r: Double, implicitPrefs: Boolean): Double = {
    // 若implicitPref
    // 开启   分值=真是分值大于0的为1,其他为0
    // 未开启 分值=预测分值
    if (implicitPrefs)
      if (r > 0) 1.0 else 0.0
    else
      r
  }
}
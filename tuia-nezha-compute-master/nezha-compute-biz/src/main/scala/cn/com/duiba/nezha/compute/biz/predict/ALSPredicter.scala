package cn.com.duiba.nezha.compute.biz.predict

import org.apache.spark.mllib.recommendation.{MatrixFactorizationModel, Rating}
import org.apache.spark.rdd.RDD

import scala.collection.mutable.ArrayBuffer

/**
 * Created by pc on 2016/11/22.
 */
object ALSPredicter {
  /**
   * 预测数据
   */
  def predict(model: MatrixFactorizationModel, data: RDD[Int], numRecommender: Int): ArrayBuffer[(Int, Array[Rating])] = {
    val recommenders = new ArrayBuffer[(Int, Array[Rating])]();
    // 为用户推荐电影

    data.collect().foreach(user => {
      val rs = model.recommendProducts(user, numRecommender)
      recommenders+=((user, rs))
    })
    recommenders
  }
}

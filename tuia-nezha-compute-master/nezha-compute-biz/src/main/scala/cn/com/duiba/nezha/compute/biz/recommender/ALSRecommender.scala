package cn.com.duiba.nezha.compute.biz.recommender

/**
 * Created by pc on 2016/11/21.
 */

import cn.com.duiba.nezha.compute.common.params.Params
import Params.ALSParams
import cn.com.duiba.nezha.compute.common.params.Params
import org.apache.spark.SparkContext
import org.apache.spark.mllib.recommendation._
import org.apache.spark.rdd.RDD


/**
 * moivelens 电影推荐
 *
 */
object ALSRecommender {

  def train(sc:SparkContext,ratings:RDD[Rating],params: ALSParams):MatrixFactorizationModel= {

    //使用ALS建立推荐模型
    //也可以使用简单模式    val model = ALS.train(ratings, ranking, numIterations)
    //setRank设置随机因子，就是隐藏的属性
    //setIterations设置最大迭代次数
    //setLambda设置正则化参数
    //setImplicitPrefs 是否开启分值阈值
    //setUserBlocks设置用户的块数量，并行化计算,当特别大的时候需要设置
    //setProductBlocks设置物品的块数量
    val model = new ALS()
      .setRank(params.rank)
      .setIterations(params.numIterations)
      .setLambda(params.lambda)
      .setImplicitPrefs(params.implicitPrefs)
      .setUserBlocks(params.numUserBlocks)
      .setProductBlocks(params.numProductBlocks)
      .run(ratings)
    model
  }

}


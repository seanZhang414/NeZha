package cn.com.duiba.nezha.compute.biz.app.ml

import cn.com.duiba.nezha.compute.api.enums.RunEnvEnum
import cn.com.duiba.nezha.compute.biz.bo.AdvertCtrLrModelBo
import cn.com.duiba.nezha.compute.biz.evaluate.LREvaluater
import cn.com.duiba.nezha.compute.biz.load.DataLoader
import cn.com.duiba.nezha.compute.biz.predict.LRPredicter
import cn.com.duiba.nezha.compute.biz.replay.LRReplayer
import cn.com.duiba.nezha.compute.biz.support.LRCTRParamsParser
import cn.com.duiba.nezha.compute.common.enums.DateStyle
import cn.com.duiba.nezha.compute.common.params.Params.LRCTRParams
import cn.com.duiba.nezha.compute.common.util.DateUtil
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.recommendation.ALS
import org.apache.spark.mllib.recommendation.Rating

/**
  * Created by jiali on 2018/1/24.
  */
object AdvertMatchALS {
  Logger.getLogger("org").setLevel(Level.ERROR)

  //  var logger = LoggerFactory.getLogger(AdvertCTRLR.getClass)

  def run(params: LRCTRParams): Unit = {

    // 1.初始spark context
    println("init spark context ... ")
    var conf = new SparkConf().setAppName("AdvertMatchALS").setMaster("local[3]")

    if (!params.localRun) {
      conf = new SparkConf().setAppName("AdvertMatchALS")
    }

    val sc = new SparkContext(conf) // 获取 sc

    // 2.参数解析
    val modelBaseInfo = LRCTRParamsParser.getModelBaseInfo(params.modelKeyId)
    val featureIdxList = modelBaseInfo.idList
    val featureIdxLocMap = modelBaseInfo.locMap
    val featureCollectionList = modelBaseInfo.idCollectionList
    val modelKey = modelBaseInfo.key
    val dt = DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD)

    val partitionNums = params.partitionNums

    println(s" featureIdxList = $featureIdxList,featuerIdxLocMap = $featureIdxLocMap,modelKey = $modelKey,dt = $dt")


    // 3.加载数据

    val trainingData = params.inputTraining match {
      case "" => null
      case _ => DataLoader.dataLoadWithSplitAndSample(sc, params.inputTraining, params.sep, 0.999, partitionNums)
    }

    val testData = params.inputTest match {
      case "" => null
      case _ => DataLoader.dataLoadWithSplitAndSample(sc, params.inputTest, params.sep, 0.999, partitionNums)
    }

    if (trainingData == null) {
      println(s" trainingData is null")
    }

    if (testData == null) {
      println(s" testData is null")
    }

    // Load and parse the data
    val data = sc.textFile("mllib/data/als/test.data")
    val ratings = data.map(_.split(',') match {
      case Array(user, item, rate) =>  Rating(user.toInt, item.toInt, rate.toDouble)
    })

    // Build the recommendation model using ALS
    val numIterations = 20
    val model = ALS.train(ratings, 1, 20, 0.01)

    // Evaluate the model on rating data
    val usersProducts = ratings.map{ case Rating(user, product, rate)  => (user, product)}
    val predictions = model.predict(usersProducts).map{
      case Rating(user, product, rate) => ((user, product), rate)
    }
    val ratesAndPreds = ratings.map{
      case Rating(user, product, rate) => ((user, product), rate)
    }.join(predictions)
    val MSE = ratesAndPreds.map{
      case ((user, product), (r1, r2)) =>  math.pow((r1- r2), 2)
    }.reduce(_ + _)/ratesAndPreds.count
    println("Mean Squared Error = " + MSE)


    // 4.模型评估


    if (params.env.equals(RunEnvEnum.ENV_EVA.getDesc) && trainingData != null) {


      if (testData == null) {
        val modelEvaluateEntity = LREvaluater.evaluate(trainingData, modelBaseInfo, dt, partitionNums)
        //        LRModelSave.modelEvaluateSave(modelEvaluateEntity) // 保存结果
      }
      else {
        val modelEvaluateEntity = LREvaluater.evaluate(trainingData, testData, modelBaseInfo, dt, partitionNums)
        //        LRModelSave.modelEvaluateSave(modelEvaluateEntity) // 保存结果

      }

    }

    // 5.模型预测
    if (params.env.equals(RunEnvEnum.ENV_PRED.getDesc) && trainingData != null) {

      // 按比例，划分训练、测试数据集
      val modelEntity = LRPredicter.predict(trainingData,modelBaseInfo, dt, partitionNums)
      //      LRModelSave.modelSave(modelEntity) // 保存结果

      //      println("model sava es start")
      //      // 存入缓存
      //      AdvertCtrLrModelBo.saveCTRDtModelByKeyToES(modelKey, dt, modelEntity)
      //      AdvertCtrLrModelBo.saveCTRLastModelByKeyToES(modelKey, modelEntity)
      //      println("model sava es end")
      //
      //      println("model sava redis start")
      //      // 存入缓存
      //      AdvertCtrLrModelBo.saveCTRDtModelByKeyToRedis(modelKey, dt, modelEntity)
      //      AdvertCtrLrModelBo.saveCTRLastModelByKeyToRedis(modelKey, modelEntity)
      //      println("model sava redis end")

      println("model sava mongodb start")
      AdvertCtrLrModelBo.saveCTRDtModelByKeyToMD(modelKey, dt, modelEntity)
      AdvertCtrLrModelBo.saveCTRLastModelByKeyToMD(modelKey, modelEntity)
      println("model sava  mongodb  end")

      //      BaseReplayer.replay(ModelKeyEnum.LR_CTR_MODEL_v004.getIndex)
    }

    // 6.模型预估重演
    if (params.env.equals(RunEnvEnum.ENV_REPLAY.getDesc) && testData != null) {
      // 按比例，划分训练、测试数据集
      val modelEntity = LRReplayer.repaly(testData,modelBaseInfo, dt, partitionNums)
    }



  }

}

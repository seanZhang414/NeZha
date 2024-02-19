package cn.com.duiba.nezha.compute.biz.app.ml

import cn.com.duiba.nezha.compute.api.enums.{ModelKeyEnum, RunEnvEnum}
import cn.com.duiba.nezha.compute.biz.bo.AdvertCtrLrModelBo
import cn.com.duiba.nezha.compute.biz.evaluate.LREvaluater
import cn.com.duiba.nezha.compute.biz.load.DataLoader
import cn.com.duiba.nezha.compute.biz.replay.{BaseReplayer, LRReplayer}
import cn.com.duiba.nezha.compute.biz.support.LRCTRParamsParser
import cn.com.duiba.nezha.compute.common.params.Params
import Params.LRCTRParams
import cn.com.duiba.nezha.compute.biz.predict.LRPredicter
import cn.com.duiba.nezha.compute.common.enums.DateStyle
import cn.com.duiba.nezha.compute.common.util.DateUtil
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}


/**
 * Created by pc on 2016/12/14.
 */
object AdvertCTRLR {

  Logger.getLogger("org").setLevel(Level.ERROR)

//    var logger = LoggerFactory.getLogger(AdvertCTRLR.getClass)

  def run(params: LRCTRParams): Unit = {

    // 1.初始spark context
    println("init spark context ... ")
    //    System.setProperty("spark.default.parallelism", "10")
    var conf = new SparkConf().setAppName("AdvertCTRLR").setMaster("local[3]") // 本地运行模式，读取本地的spark主目录

    if (!params.localRun) {
      conf = new SparkConf().setAppName("AdvertCTRLR") // 集群运行模式，读取spark集群的环境变量
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

package cn.com.duiba.nezha.compute.biz.evaluate

import cn.com.duiba.nezha.compute.alg.FM
import cn.com.duiba.nezha.compute.alg.util.ReplayerUtil
import cn.com.duiba.nezha.compute.api.enums.SerializerEnum
import cn.com.duiba.nezha.compute.api.point.Point._
import cn.com.duiba.nezha.compute.biz.entity.model.AdvertCtrLrModelEvaluateEntity
import cn.com.duiba.nezha.compute.biz.save.ReplayerSave
import cn.com.duiba.nezha.compute.biz.util.{SampleCategoryFeatureUtil, SampleParser}
import cn.com.duiba.nezha.compute.mllib.algorithm.SparseFM
import cn.com.duiba.nezha.compute.mllib.evaluater.ClassifierEvaluater
import com.alibaba.fastjson.JSONObject
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.rdd.RDD
import org.mortbay.util.ajax.JSON

import scala.collection.JavaConverters._
import scala.collection.Map

/**
 * Created by pc on 2016/11/22.
 */
object FMEvaluater {

  def evaluate(data: RDD[List[String]], modelBaseInfo: ModelBaseInfo, dt: String, partitionNums: Int,batchSize:Int): AdvertCtrLrModelEvaluateEntity = {


    // 1 按比例，划分训练、测试数据集
    val numLines = data.count() //计算一共有多少样本数
    println("DataNums=" + numLines)
    val splitsData = data.randomSplit(Array(0.8, 0.01), seed = 11L)
    val trainingData = splitsData(0)
    val testData = splitsData(1)
    evaluate(trainingData: RDD[List[String]], testData: RDD[List[String]], modelBaseInfo: ModelBaseInfo, dt: String, partitionNums: Int,batchSize:Int)
  }


  def evaluate(trainingData: RDD[List[String]], testData: RDD[List[String]], modelBaseInfo: ModelBaseInfo, dt: String, partitionNums: Int,batchSize:Int): AdvertCtrLrModelEvaluateEntity = {

    val featureIdxList: List[String] = modelBaseInfo.idList
    val featuerIdxLocMap: Map[String, Int] = modelBaseInfo.locMap
    val featureCollectionList = modelBaseInfo.idCollectionList
    val modelKey: String = modelBaseInfo.key
    val iteNum = modelBaseInfo.iteNum
    // 解析数据集
    println("featureCollectionList=" + featureCollectionList.mkString)

    // 按比例，划分训练、测试数据集
    //
    trainingData.cache
    val trainingNums = trainingData.count()
    println("trainingDataNums=" + trainingNums)

    testData.cache
    val testNums = testData.count()
    println("testDataNums=" + testNums)

    val ModelU = new FM()
    val dict = SampleCategoryFeatureUtil.getFeatureDict(trainingData, featureIdxList, featuerIdxLocMap, featureCollectionList.toSet[String])
    ModelU.setFeatureDict(dict)

    val training = SampleParser.sampleParsetoLabeledSPoint(trainingData, featureIdxList, featuerIdxLocMap, featureCollectionList, ModelU.getDictUtil)
      .repartition(partitionNums).persist()

    val featureSize = training.first().x.size
    println("featureSize=" + featureSize)
    println("feature.first=")
    //    println(training.first().x)
    //
    val test = SampleParser.sampleParsetoLabeledSPointWithMap(testData, featureIdxList, featuerIdxLocMap, featureCollectionList, ModelU.getDictUtil)
      .repartition(partitionNums).cache()


    println("start training")

    val model = new SparseFM()
      .setLearningRate(0.005)
      .setAdRate(0.7)
      .setMtRate(0.7)
      .setBeta1(0.9)
      .setBeta2(0.999)
      .setDeltaThreshold(0.00001)
      .setMinIterations(4)
      .setMaxIterations(iteNum)
      .setBatchSize(batchSize)
      .setFactorNums(3)
      .setReg1(0.000001)
      .setReg2(0.00001)
      .runAdam(training)



    model.clearThreshold
    ModelU.setModel(model)
    ModelU.setFeatureIdxList(featureIdxList.asJava)
    ModelU.setFeatureCollectionList(featureCollectionList.asJava)



    // Compute raw scores on the test set
    val predictionAndLabels2 = test.map { case (line, map, LabeledSPoint(features, label)) =>
      (line(1), map, features, model.predict(features).doubleValue(), label)
    }



    val predictionAndLabels3 = predictionAndLabels2.map { case (line, map, features, pre, label) => (pre, label) }.cache()



    //使用了一个BinaryClassificationMetrics来评估
    val metrics2 = new BinaryClassificationMetrics(predictionAndLabels3)

    val auRoc = ClassifierEvaluater.calAuROC(metrics2)

    val auPrc = ClassifierEvaluater.calAuPRC(metrics2)

    for (i <- 1 to 15) {
      ClassifierEvaluater.calMulticlassMetrics(predictionAndLabels3, i * 0.05)
    }


    val result = predictionAndLabels2.map { case (orderId, map, features, pre, label) => ModelReplayWithMap(orderId, JSON.toString(map), pre) }.cache()



    //保存数据 重演数据
    ReplayerSave.saveMap(result, modelKey, dt)


    //保存
    val replayerUtil: ReplayerUtil = new ReplayerUtil
    replayerUtil.setModel(ModelU)

    val featureInfoArray = replayerUtil.getFeatureInfoList

    println("featureInfoArray.size()="+featureInfoArray.size())



    val featureInfoRdd = testData.context.parallelize(featureInfoArray.asScala)

    ReplayerSave.saveFeature(featureInfoRdd, modelKey, dt)


    // 保存
    val entity: AdvertCtrLrModelEvaluateEntity = new AdvertCtrLrModelEvaluateEntity()
    entity.setDt(dt)
    entity.setModelKey(modelKey)
    entity.setTestNums(testNums)
    entity.setTraingNums(trainingNums)
    entity.setFeatureSize(featureSize)
    entity.setTestAuprc(auPrc)
    entity.setTestAuroc(auRoc)


    entity
  }

}
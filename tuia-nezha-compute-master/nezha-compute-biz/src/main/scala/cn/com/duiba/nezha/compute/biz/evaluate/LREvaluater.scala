package cn.com.duiba.nezha.compute.biz.evaluate

import cn.com.duiba.nezha.compute.alg.LR
import cn.com.duiba.nezha.compute.api.point.Point.{ModelBaseInfo, ModelReplay}
import cn.com.duiba.nezha.compute.biz.entity.model.AdvertCtrLrModelEvaluateEntity
import cn.com.duiba.nezha.compute.biz.save.ReplayerSave
import cn.com.duiba.nezha.compute.biz.util.{SampleCategoryFeatureUtil, SampleParser}
import cn.com.duiba.nezha.compute.mllib.evaluater.ClassifierEvaluater
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

import scala.collection.JavaConverters._
/**
 * Created by pc on 2016/11/22.
 */
object LREvaluater {

  def evaluate(data: RDD[List[String]], modelBaseInfo: ModelBaseInfo, dt: String, partitionNums: Int): AdvertCtrLrModelEvaluateEntity = {

    // 1 按比例，划分训练、测试数据集
    val numLines = data.count() //计算一共有多少样本数
    println("DataNums=" + numLines)
    val splitsData = data.randomSplit(Array(0.8, 0.2), seed = 11L)
    val trainingData = splitsData(0)
    val testData = splitsData(1)
    evaluate(trainingData: RDD[List[String]], testData: RDD[List[String]], modelBaseInfo: ModelBaseInfo, dt: String, partitionNums: Int)
  }


  def evaluate(trainingData: RDD[List[String]], testData: RDD[List[String]], modelBaseInfo: ModelBaseInfo, dt: String, partitionNums: Int): AdvertCtrLrModelEvaluateEntity = {

    val featureIdxList: List[String] = modelBaseInfo.idList
    val featuerIdxLocMap: Map[String, Int] = modelBaseInfo.locMap
    val featureCollectionList = modelBaseInfo.idCollectionList
    val modelKey: String = modelBaseInfo.key
    // 解析数据集
    // 按比例，划分训练、测试数据集
    trainingData.cache
    testData.cache
    //
    val trainingNums = trainingData.count()
    println("trainingDataNums=" + trainingNums)

    val testNums = testData.count()
    println("testDataNums=" + testNums)

    val ModelU = new LR()
    val dict = SampleCategoryFeatureUtil.getFeatureDict(trainingData, featureIdxList, featuerIdxLocMap, featureCollectionList.toSet[String])
    ModelU.setFeatureDict(dict)



    val training = SampleParser.sampleParsetoLabeledPoint(trainingData, featureIdxList, featuerIdxLocMap, featureCollectionList,ModelU.getDictUtil)
      .repartition(partitionNums).cache()



    val featureSize = training.first().features.size
    println("featureSize=" + featureSize)
    //
    val test = SampleParser.sampleParsetoLabeledPointWithLine(testData, featureIdxList, featuerIdxLocMap,featureCollectionList, ModelU.getDictUtil)
      .repartition(partitionNums).cache()


    val model = new LogisticRegressionWithLBFGS().setNumClasses(2).setIntercept(true).run(training)


    model.clearThreshold
    ModelU.setModel(model)
    ModelU.setFeatureIdxList(featureIdxList.asJava)
    ModelU.setFeatureCollectionList(featureCollectionList.asJava)


    // Compute raw scores on the test set
    val predictionAndLabels2 = test.map { case (line, LabeledPoint(label, features)) =>
      val prediction = model.predict(features)
      (line(1), prediction.doubleValue(), label)
    }.cache()

    val predictionAndLabels3 = predictionAndLabels2.map { case (line, pre, label) => (pre, label) }.cache()


    //    predictionAndLabels4.saveAsTextFile("hdfs://bigdata01:8020/user/lwj/model/data/output/"+modelKey+"/"+dt)

    //        showPredictionAndLabels(predictionAndLabels2)

    val result = predictionAndLabels2.map { case (orderId, pre, label) => ModelReplay(orderId, pre) }.cache()

    //使用了一个BinaryClassificationMetrics来评估
    val metrics2 = new BinaryClassificationMetrics(predictionAndLabels3)
    //
    // evaluate
    //        precisionByThreshold(metrics2)
    //
    //        recallByThreshold(metrics2)
    //
    //        fMeasureByThreshold(metrics2)

    val auRoc = ClassifierEvaluater.calAuROC(metrics2)

    val auPrc = ClassifierEvaluater.calAuPRC(metrics2)

    for (i <- 1 to 19) {
      ClassifierEvaluater.calMulticlassMetrics(predictionAndLabels3, i * 0.05)
    }

    //保存数据
    ReplayerSave.save(result,modelKey,dt)
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
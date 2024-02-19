package cn.com.duiba.nezha.compute.biz.replay

import cn.com.duiba.nezha.compute.alg.{FMTest, FM}

import cn.com.duiba.nezha.compute.api.point.Point.{ModelReplayWithMap, ModelReplay, LabeledSPoint, ModelBaseInfo}
import cn.com.duiba.nezha.compute.biz.bo.AdvertCtrLrModelBo
import cn.com.duiba.nezha.compute.biz.entity.model.AdvertCtrLrModelEvaluateEntity
import cn.com.duiba.nezha.compute.biz.save.ReplayerSave
import cn.com.duiba.nezha.compute.biz.util.{SampleCategoryFeatureUtil, SampleParser}
import cn.com.duiba.nezha.compute.mllib.evaluater.ClassifierEvaluater
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.rdd.RDD
import org.mortbay.util.ajax.JSON

import scala.collection.JavaConverters._
import scala.collection.Map

/**
 * Created by pc on 2016/11/22.
 */
object FMReplayer {


  def repaly(testData: RDD[List[String]], modelBaseInfo: ModelBaseInfo, dt: String, partitionNums: Int) = {

    // 解析
    val featureIdxList: List[String] = modelBaseInfo.idList
    val featuerIdxLocMap: Map[String, Int] = modelBaseInfo.locMap
    val featureCollectionList = modelBaseInfo.idCollectionList
    val modelKey: String = modelBaseInfo.key

    // 读取模型序列化对象
    println("modelKey=" + modelKey)
    val entity = AdvertCtrLrModelBo.getCTRModelByKeyFromMD(modelKey)

    val fm = new FMTest()
    fm.setEntity(entity)

    // 获取模型
    //    val model = new FM()
    //    model.setEntity(entity)

    // 样本处理
    println("parse sample")
    val test = SampleParser.sampleParsetoLabeledSPointWithMapTest(testData, featureIdxList, featuerIdxLocMap, featureCollectionList, fm)
      .repartition(partitionNums).cache()


    println("replay")

    // Compute raw scores on the test set
    val predictionAndLabels2 = test.map { case (line, map, LabeledSPoint(features, label)) =>
      (line(1), map, features, fm.getModel.getModelUtil.predict(features).doubleValue(), label)
    }
    println("predictionAndLabels2.first() " + predictionAndLabels2.first())

    println("to data")
    val predictionAndLabels3 = predictionAndLabels2.map { case (line, map, features, pre, label) => (pre, label) }.cache()
    println("predictionAndLabels3.first() " + predictionAndLabels3.first())

    println("to BinaryClassification")
    //使用了一个BinaryClassificationMetrics来评估
    val metrics2 = new BinaryClassificationMetrics(predictionAndLabels3)

    val auRoc = ClassifierEvaluater.calAuROC(metrics2)

    val auPrc = ClassifierEvaluater.calAuPRC(metrics2)

    for (i <- 1 to 15) {
      ClassifierEvaluater.calMulticlassMetrics(predictionAndLabels3, i * 0.05)
    }


    val result = predictionAndLabels2.map { case (orderId, map, features, pre, label) => ModelReplayWithMap(orderId, JSON.toString(map), pre) }.cache()



    //保存数据
    ReplayerSave.saveMap(result, modelKey, dt)

  }

}
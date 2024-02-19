package cn.com.duiba.nezha.compute.biz.replay

import cn.com.duiba.nezha.compute.alg.{LR, FM}
import cn.com.duiba.nezha.compute.api.point.Point.{LabeledSPoint, ModelBaseInfo, ModelReplay}
import cn.com.duiba.nezha.compute.biz.bo.AdvertCtrLrModelBo
import cn.com.duiba.nezha.compute.biz.save.ReplayerSave
import cn.com.duiba.nezha.compute.biz.util.SampleParser
import cn.com.duiba.nezha.compute.mllib.evaluater.ClassifierEvaluater
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.rdd.RDD

import scala.collection.Map

/**
 * Created by pc on 2016/11/22.
 */
object LRReplayer {


  def repaly(testData: RDD[List[String]], modelBaseInfo: ModelBaseInfo, dt: String, partitionNums: Int) = {

    // 解析
    val featureIdxList: List[String] = modelBaseInfo.idList
    val featuerIdxLocMap: Map[String, Int] = modelBaseInfo.locMap
    val featureCollectionList = modelBaseInfo.idCollectionList
    val modelKey: String = modelBaseInfo.key

    // 读取模型序列化对象
    val entity = AdvertCtrLrModelBo.getCTRModelByKeyFromMD(modelKey)


    // 获取模型
    val Model: LR = new LR(entity)

    // 样本处理
    val test = SampleParser.sampleParsetoLabeledSPointWithLine(testData, featureIdxList, featuerIdxLocMap, featureCollectionList, Model.getDictUtil)
      .repartition(partitionNums).persist()

    // Compute raw scores on the test set
    val predictionAndLabels2 = test.map { case (line, LabeledSPoint(features, label)) =>
      (line(1), features, Model.getModelUtil.predict(features).doubleValue(), label)
    }
    predictionAndLabels2.collect().foreach(println)

    val result = predictionAndLabels2.map { case (orderId, features, pre, label) => ModelReplay(orderId, pre) }.cache()

    val predictionAndLabels3 = predictionAndLabels2.map { case (orderId, features, pre, label) => (pre, label) }.cache()

    // predictionAndLabels4.saveAsTextFile("hdfs://bigdata01:8020/user/lwj/model/data/output/"+modelKey+"/"+dt)

    //使用了一个BinaryClassificationMetrics来评估
    val metrics2 = new BinaryClassificationMetrics(predictionAndLabels3)


    val auRoc = ClassifierEvaluater.calAuROC(metrics2)

    //保存数据
    ReplayerSave.save(result,modelKey,dt)

  }

}
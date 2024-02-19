package cn.com.duiba.nezha.compute.biz.predict

import cn.com.duiba.nezha.compute.alg.LR
import cn.com.duiba.nezha.compute.api.dto.AdvertModelEntity
import cn.com.duiba.nezha.compute.api.enums.SerializerEnum
import cn.com.duiba.nezha.compute.api.point.Point
import cn.com.duiba.nezha.compute.api.point.Point.ModelBaseInfo
import cn.com.duiba.nezha.compute.biz.util.{SampleCategoryFeatureUtil, SampleParser}
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.rdd.RDD

import scala.collection.JavaConverters._
import scala.collection.Map

/**
 * Created by pc on 2016/11/22.
 */
object LRPredicter {
  /**
   * 预测数据
   */

  def predict(data: RDD[List[String]], modelBaseInfo: ModelBaseInfo, dt: String, partitionNums: Int): AdvertModelEntity = {

    val featureIdxList: List[String] = modelBaseInfo.idList
    val featuerIdxLocMap: Map[String, Int] = modelBaseInfo.locMap
    val featureCollectionList = modelBaseInfo.idCollectionList
    val modelKey: String = modelBaseInfo.key

    val lrModel = new LR()
    data.cache()
    val dict = SampleCategoryFeatureUtil.getFeatureDict(data, featureIdxList, featuerIdxLocMap, featureCollectionList.toSet[String])
    lrModel.setFeatureDict(dict)

    //
    val trainingNums = data.count()
    println("trainingDataNums=" + trainingNums)

    // 构造训练、测试数据集

    val training = SampleParser.sampleParsetoLabeledPoint(data, featureIdxList, featuerIdxLocMap, featureCollectionList, lrModel.getDictUtil)
      .repartition(partitionNums).cache()
//    training.cache
    val featureSize = training.first().features.size
    println("featureSize=" + featureSize)

    // Run training algorithm to build the model
    val model = new LogisticRegressionWithLBFGS()
      .setIntercept(true)
      .setNumClasses(2)
      .run(training)

    model.clearThreshold
    lrModel.setModel(model)
    lrModel.setFeatureIdxList(featureIdxList.asJava)
    lrModel.setFeatureCollectionList(featureCollectionList.asJava)

    // 保存模型
    val entity: AdvertModelEntity = new AdvertModelEntity()
    entity.setDt(dt)
    entity.setSerializerId(SerializerEnum.KRYO.getIndex)
    entity.setFeatureDictStr(lrModel.getFeatureDictStr(SerializerEnum.KRYO))
    entity.setFeatureIdxListStr(lrModel.getFeatureIdxListStr(SerializerEnum.KRYO))
    entity.setFeatureCollectListStr(lrModel.getFeatureCollectionListStr(SerializerEnum.KRYO))
    entity.setModelKey(modelKey)
    entity.setModelStr(lrModel.getModelStr(SerializerEnum.KRYO))
    entity
  }
}

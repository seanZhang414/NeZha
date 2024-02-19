package cn.com.duiba.nezha.compute.biz.predict

import cn.com.duiba.nezha.compute.alg.FM
import cn.com.duiba.nezha.compute.alg.util.ReplayerUtil
import cn.com.duiba.nezha.compute.api.dto.AdvertModelEntity
import cn.com.duiba.nezha.compute.api.enums.SerializerEnum
import cn.com.duiba.nezha.compute.api.point.Point
import cn.com.duiba.nezha.compute.api.point.Point.ModelBaseInfo
import cn.com.duiba.nezha.compute.biz.save.ReplayerSave
import cn.com.duiba.nezha.compute.biz.util.{SampleCategoryFeatureUtil, SampleParser}
import cn.com.duiba.nezha.compute.mllib.algorithm.SparseFM
import org.apache.spark.rdd.RDD

import scala.collection.JavaConverters._
import scala.collection.Map

/**
 * Created by pc on 2016/11/22.
 */
object FMPredicter {
  /**
   * 预测数据
   */

  def predict(data: RDD[List[String]], modelBaseInfo: ModelBaseInfo, dt: String, partitionNums: Int,batchSize:Int): AdvertModelEntity = {
    val featureIdxList: List[String] = modelBaseInfo.idList
    val featuerIdxLocMap: Map[String, Int] = modelBaseInfo.locMap
    val featureCollectionList = modelBaseInfo.idCollectionList
    val modelKey: String = modelBaseInfo.key
    val modelFM = new FM()

    data.cache()
    val dict = SampleCategoryFeatureUtil.getFeatureDict(data, featureIdxList, featuerIdxLocMap, featureCollectionList.toSet[String])
    modelFM.setFeatureDict(dict)
    //
    val trainingNums = data.count()
    println("trainingDataNums=" + trainingNums)

    // 构造训练、测试数据集


    val training = SampleParser.sampleParsetoLabeledSPoint(data, featureIdxList, featuerIdxLocMap, featureCollectionList, modelFM.getDictUtil)
      .repartition(partitionNums).cache()
    //    training.cache
    val featureSize = training.first().x.size
    println("featureSize=" + featureSize)

    // Run training algorithm to build the model
    val model = new SparseFM()
      .setLearningRate(0.005)
      .setAdRate(0.7)
      .setMtRate(0.7)
      .setBeta1(0.9)
      .setBeta2(0.999)
      .setDeltaThreshold(0.00001)
      .setMinIterations(4)
      .setMaxIterations(5)
      .setBatchSize(batchSize)
      .setFactorNums(3)
      .setReg1(0.000001)
      .setReg2(0.00001)
      .runAdam(training)


    model.clearThreshold
    modelFM.setModel(model)
    modelFM.setFeatureIdxList(featureIdxList.asJava)
    modelFM.setFeatureCollectionList(featureCollectionList.asJava)




    //保存
    val replayerUtil: ReplayerUtil = new ReplayerUtil
    replayerUtil.setModel(modelFM)
    val featureInfoArray = replayerUtil.getFeatureInfoList
    println("featureInfoArray.size()="+featureInfoArray.size())
    val featureInfoRdd = training.context.parallelize(featureInfoArray.asScala)
    ReplayerSave.saveFeature(featureInfoRdd, modelKey, dt)

    // 保存模型
    val entity: AdvertModelEntity = new AdvertModelEntity()
    entity.setDt(dt)


    entity.setSerializerId(SerializerEnum.KRYO.getIndex)
    entity.setFeatureDictStr(modelFM.getFeatureDictStr(SerializerEnum.KRYO))
    entity.setFeatureIdxListStr(modelFM.getFeatureIdxListStr(SerializerEnum.KRYO))
    entity.setFeatureCollectListStr(modelFM.getFeatureCollectionListStr(SerializerEnum.KRYO))
    entity.setModelKey(modelKey)
    entity.setModelStr(modelFM.getModelStr(SerializerEnum.KRYO))

    entity
  }
}

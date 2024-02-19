package cn.com.duiba.nezha.compute.biz.util

import java.util

import cn.com.duiba.nezha.compute.alg.FMTest
import cn.com.duiba.nezha.compute.alg.util.CategoryFeatureDictUtil
import cn.com.duiba.nezha.compute.api.dto.AdvertModelEntity
import cn.com.duiba.nezha.compute.api.point.Point
import cn.com.duiba.nezha.compute.api.point.Point.LabeledSPoint
import cn.com.duiba.nezha.compute.common.util.LabelUtil
import org.apache.spark.mllib.recommendation.Rating
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

import scala.collection.JavaConverters._
import scala.collection.Map
import scala.collection.mutable.ListBuffer

/**
 * Created by pc on 2016/11/23.
 */
object SampleParser {


  def sampleParsetoLabeledSPointWithMap(data: RDD[List[String]], featureIdxList: List[String], featuerIdxLocMap: Map[String, Int], featureCollectionList: List[String], dictUtil: CategoryFeatureDictUtil): RDD[(List[String],util.HashMap[String, String],LabeledSPoint)] = {

    val size = getSize(featuerIdxLocMap: Map[String, Int])

    val parsedData = data.filter(_ != null).filter(_.size >= size).map(line => {


      val map = new util.HashMap[String, String]
      val pbuf = new ListBuffer[String]
      featureIdxList.foreach(featureIdx => {
        map.put(featureIdx, toLowerCase(line(featuerIdxLocMap(featureIdx))))
        pbuf.append(toLowerCase(line(featuerIdxLocMap(featureIdx))))
      })

      val value = dictUtil.oneHotSparseVectorEncode(featureIdxList.asJava, pbuf.toList.asJava, featureCollectionList.asJava)
      if (value.getVector != null) {
        (line,map, LabeledSPoint(value.getVector, LabelUtil.getLabel(line(0))))
      } else {
        null
      }

    })
    parsedData.filter(_ != null)
  }
  def sampleParsetoLabeledSPointWithMapTest(data: RDD[List[String]], featureIdxList: List[String], featuerIdxLocMap: Map[String, Int], featureCollectionList: List[String],  fm:FMTest

  ): RDD[(List[String],util.HashMap[String, String],LabeledSPoint)] = {

    val size = getSize(featuerIdxLocMap: Map[String, Int])

    val parsedData = data.filter(_ != null).filter(_.size >= size).map(line => {


      val map = new util.HashMap[String, String]
      val pbuf = new ListBuffer[String]
      featureIdxList.foreach(featureIdx => {
        map.put(featureIdx, toLowerCase(line(featuerIdxLocMap(featureIdx))))
        pbuf.append(toLowerCase(line(featuerIdxLocMap(featureIdx))))
      })

      val value = fm.getModel.getDictUtil.oneHotSparseVectorEncode(featureIdxList.asJava, pbuf.toList.asJava, featureCollectionList.asJava)
      if (value.getVector != null) {
        (line,map, LabeledSPoint(value.getVector, LabelUtil.getLabel(line(0))))
      } else {
        null
      }

    })
    parsedData.filter(_ != null)
  }



  def sampleParsetoLabeledSPointWithLine(data: RDD[List[String]], featureIdxList: List[String], featuerIdxLocMap: Map[String, Int], featureCollectionList: List[String], dictUtil: CategoryFeatureDictUtil): RDD[(List[String], LabeledSPoint)] = {

    val size = getSize(featuerIdxLocMap: Map[String, Int])

    val parsedData = data.filter(_ != null).filter(_.size >= size).map(line => {

      val pbuf = new ListBuffer[String]

      featureIdxList.foreach(featureIdx => {
        pbuf.append(toLowerCase(line(featuerIdxLocMap(featureIdx))))
      })

      val value = dictUtil.oneHotSparseVectorEncode(featureIdxList.asJava, pbuf.toList.asJava, featureCollectionList.asJava)
      if (value.getVector != null) {
        (line, LabeledSPoint(value.getVector, LabelUtil.getLabel(line(0))))
      } else {
        null
      }

    })

    parsedData.filter(_ != null)
  }


  def sampleParsetoLabeledPointWithLine(data: RDD[List[String]], featureIdxList: List[String], featuerIdxLocMap: Map[String, Int], featureCollectionList: List[String], dictUtil: CategoryFeatureDictUtil): RDD[(List[String], LabeledPoint)] = {

    val size = getSize(featuerIdxLocMap: Map[String, Int])

    val parsedData = data.filter(_ != null).filter(_.size >= size).map(line => {

      val pbuf = new ListBuffer[String]

      featureIdxList.foreach(featureIdx => {
        pbuf.append(toLowerCase(line(featuerIdxLocMap(featureIdx))))
      })

      val value = dictUtil.oneHotSparseVectorEncode(featureIdxList.asJava, pbuf.toList.asJava, featureCollectionList.asJava)
      if (value.getVector != null) {
        (line, LabeledPoint(LabelUtil.getLabel(line(0)), value.getVector))
      } else {
        null
      }
    })

    parsedData.filter(_ != null)
  }


  def sampleParsetoLabeledSPoint(data: RDD[List[String]], featureIdxList: List[String], featuerIdxLocMap: Map[String, Int], featureCollectionList: List[String], dictUtil: CategoryFeatureDictUtil): RDD[LabeledSPoint] = {
    sampleParsetoLabeledSPointWithLine(data, featureIdxList, featuerIdxLocMap, featureCollectionList, dictUtil).map { case (line, sample) => sample }
  }

  def sampleParsetoLabeledPoint(data: RDD[List[String]], featureIdxList: List[String], featuerIdxLocMap: Map[String, Int], featureCollectionList: List[String], dictUtil: CategoryFeatureDictUtil): RDD[LabeledPoint] = {
    sampleParsetoLabeledPointWithLine(data, featureIdxList, featuerIdxLocMap, featureCollectionList, dictUtil).map { case (line, sample) => sample }
  }


  def ratingParse(data: RDD[String], implicitPrefs: Boolean, sep: String, reduceValue: Double): RDD[Rating] = {


    val ratings = data.map { line =>
      val fields = line.split(sep)
      if (implicitPrefs) {
        Rating(fields(0).toInt, fields(1).toInt, fields(2).toDouble - reduceValue)
      } else {
        Rating(fields(0).toInt, fields(1).toInt, fields(2).toDouble)
      }
    }
    ratingsDescPrint(ratings)
    ratings
  }

  def appRatingParse(data: RDD[String], implicitPrefs: Boolean, sep: String, reduceValue: Double): RDD[(Int, Rating)] = {

    /**
     * *MovieLens ratings are on a scale of
     * Must see
     * Will enjoy
     * It's okay
     * Fairly bad
     * Awful
     */

    val appRatings = data.map { line =>
      val fields = line.split(sep)
      if (implicitPrefs) {
        (fields(0).toInt, Rating(fields(1).toInt, fields(2).toInt, fields(3).toDouble - reduceValue))
      } else {
        (fields(0).toInt, Rating(fields(1).toInt, fields(2).toInt, fields(3).toDouble))
      }
    }

    appRatings
  }

  def ratingsDescPrint(data: RDD[Rating]): Unit = {
    //计算一共有多少样本数
    val numRatings = data.count()
    //计算一共有多少用户
    val numUsers = data.map(_.user).distinct().count()
    //计算应该有多少物品
    val numActivityTopics = data.map(_.product).distinct().count()

    println(s"Got $numRatings ratings from $numUsers users on $numActivityTopics numActivityTopics.")

  }


  def lRCTRSmapleParse(data: RDD[List[String]], featureIdxList: List[String], featuerIdxLocMap: Map[String, Int], dictUtil: CategoryFeatureDictUtil): RDD[(List[String], LabeledPoint)] = {

    val size = getSize(featuerIdxLocMap: Map[String, Int])

    val parsedData = data.filter(_ != null).filter(_.size >= size).map(line => {

      val pbuf = new ListBuffer[String]

      featureIdxList.foreach(featureIdx => {
        pbuf.append(toLowerCase(line(featuerIdxLocMap(featureIdx))))
      })

      //      val value = dictUtil.oneHotDoubleEncode(featureIdxList.asJava, pbuf.toList.asJava).toList
      val value = dictUtil.oneHotSparseVectorEncode(featureIdxList.asJava, pbuf.toList.asJava)
      if (value != null) {
        (line, LabeledPoint(LabelUtil.getLabel(line(0)), value))
      } else {
        null
      }
    })

    parsedData.filter(_ != null)
  }


  def SparseVectorSmapleParse(data: RDD[List[String]], featureIdxList: List[String], featuerIdxLocMap: Map[String, Int], dictUtil: CategoryFeatureDictUtil): RDD[(List[String], LabeledSPoint)] = {

    val size = getSize(featuerIdxLocMap: Map[String, Int])

    val parsedData = data.filter(_ != null).filter(_.size >= size).map(line => {

      val pbuf = new ListBuffer[String]

      featureIdxList.foreach(featureIdx => {
        pbuf.append(toLowerCase(line(featuerIdxLocMap(featureIdx))))
      })

      val value = dictUtil.oneHotSparseVectorEncode(featureIdxList.asJava, pbuf.toList.asJava)
      if (value != null) {
        (line, LabeledSPoint(value, LabelUtil.getLabel(line(0))))
      } else {
        null
      }

    })

    parsedData.filter(_ != null)
  }


  def lRCTRSmaple(data: RDD[List[String]], featureIdxList: List[String], featuerIdxLocMap: Map[String, Int], dictUtil: CategoryFeatureDictUtil): RDD[LabeledPoint] = {

    val parsedData = lRCTRSmapleParse(data, featureIdxList, featuerIdxLocMap, dictUtil)
    val retData = parsedData.map(_._2)
    retData
  }

  def SparseVectorSmaple(data: RDD[List[String]], featureIdxList: List[String], featuerIdxLocMap: Map[String, Int], dictUtil: CategoryFeatureDictUtil): RDD[LabeledSPoint] = {

    val parsedData = SparseVectorSmapleParse(data, featureIdxList, featuerIdxLocMap, dictUtil)
    val retData = parsedData.map(_._2)
    retData
  }


  def getSize(featuerIdxLocMap: Map[String, Int]): Int = {
    featuerIdxLocMap.values.toList.max + 1
  }

  def toLowerCase(input: String): String = {

    if (input == null) {
      null
    } else {
      input.toLowerCase
    }
  }


}

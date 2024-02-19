package cn.com.duiba.nezha.compute.biz.util

import java.util
import cn.com.duiba.nezha.compute.api.dict.CategoryFeatureDict
import org.apache.spark.rdd.RDD

import scala.collection.JavaConverters._
import scala.collection.Map

/**
 * Created by pc on 2016/12/15.
 */
object SampleCategoryFeatureUtil {


  /**
   * 对字段进行去重、索引、映射
   *
   * @ data
   * @ idx
   */
  def getFeatureMapping(data: RDD[List[String]], idx: Int, mapSize: Int): Map[String, Long] = {
    val map = data.filter(_ != null).filter(_.size >= mapSize).map(line => line(idx).toLowerCase).distinct().filter(_ != null).zipWithIndex().collectAsMap()
    map
  }

  def getFeature(data: RDD[List[String]], idx: Int, mapSize: Int): Seq[String] = {

    val dataFilter = data.
      map(line => toFeature(line, idx, mapSize)).
      filter(_ != null).
      distinct().
      zipWithIndex().
      collectAsMap()

    dataFilter.keys.toSeq.sorted

  }

  def getFeatureWithSep(data: RDD[List[String]], idx: Int, mapSize: Int, sep: String): Seq[String] = {

    val dataFilter = data.
      flatMap(line => toFeatures(line, idx, mapSize, sep)).
      filter(_ != null).
      distinct().
      zipWithIndex().
      collectAsMap()

    dataFilter.keys.toSeq.sorted
  }

  def getFeature(data: RDD[List[String]], idx: Int, mapSize: Int, isCollection: Boolean): Seq[String] = {

    if (isCollection) {
      getFeatureWithSep(data: RDD[List[String]], idx: Int, mapSize: Int, ",")
    } else {
      getFeature(data: RDD[List[String]], idx: Int, mapSize: Int)
    }

  }


  //  def getFeatureWithSep(data: RDD[List[String]], idx: Int, sep: String): Seq[String] = {
  //
  //    val map = data.map(line => line(idx).toLowerCase).flatMap(w => w.split(sep)).filter(_ != null).distinct().zipWithIndex().collectAsMap()
  //    map.keys.toSeq.sorted
  //  }


  def getFeatureDict(data: RDD[List[String]], featureIdxList: List[String], featuerIdxLocMap: Map[String, Int]): CategoryFeatureDict = {
    val dict: CategoryFeatureDict = new CategoryFeatureDict()

    val dictMap = new util.HashMap[String, java.util.List[String]]

    val mapSize = getSize(featuerIdxLocMap)


    featureIdxList.map(featureIdx => {
      val idx = featuerIdxLocMap(featureIdx)
      println("idx=" + idx + ",featureIdx=" + featureIdx)
      val categoryList = getFeature(data, idx, mapSize)
      println("size=" + categoryList.size)
      dictMap.put(featureIdx, categoryList.asJava)
    })

    dict.setFeatureDict(dictMap)
    dict
  }

  /**
   * 构造特征字典
   *
   * @param data
   * @param featureIdxList
   * @param featuerIdxLocMap
   * @param featureCollectionSet
   * @return
   */
  def getFeatureDict(data: RDD[List[String]], featureIdxList: List[String], featuerIdxLocMap: Map[String, Int], featureCollectionSet: Set[String]): CategoryFeatureDict = {
    val dict: CategoryFeatureDict = new CategoryFeatureDict()

    val dictMap = new util.HashMap[String, java.util.List[String]]

    val mapSize = getSize(featuerIdxLocMap)
    val lineSize = data.first().size

    if (lineSize < mapSize) {
      println(s"warn lineSize<mapSize,lineSize=${lineSize},mapSize=${mapSize}")
    }

    featureIdxList.map(featureIdx => {
      val idx = featuerIdxLocMap(featureIdx)
      val isCollection2 = isCollection(featureIdx, featureCollectionSet)
      val categoryList = getFeature(data, idx, mapSize, isCollection2)
      println(s"idx=${idx},featureIdx=${featureIdx},isCollection=${isCollection2},size=${categoryList.size}")
      dictMap.put(featureIdx, categoryList.asJava)
    })

    dict.setFeatureDict(dictMap)
    dict
  }


  def isCollection(featureIdx: String, featureCollectionSet: Set[String]): Boolean = {
    var ret: Boolean = false
    if (featureIdx != null && featureCollectionSet != null && featureCollectionSet.contains(featureIdx)) {
      ret = true
    }
    return ret
  }


  def getSize(featuerIdxLocMap: Map[String, Int]): Int = {
    featuerIdxLocMap.values.toList.max + 1
  }

  def toFeature(line: List[String], idx: Int, mapSize: Int): String = {

    val ret = if (line == null || line(idx) == null) {

      null
    } else {
      toLowerCase(line(idx))
    }
    ret
  }

  def toFeatures(line: List[String], idx: Int, mapSize: Int, sep: String): Array[String] = {

    if (line == null || line(idx) == null) {
      null
    } else {
      toLowerCase(line(idx).split(sep, 0))
    }
  }

  def toFeatures(str: String, sep: String): Array[String] = {

    if (str == null ) {
      Array()
    } else {
      toLowerCase(str.split(sep, 0))
    }
  }

  def toLowerCase(input: String): String = {

    if (input == null || input.isEmpty || input.equalsIgnoreCase("\n") ||input.equalsIgnoreCase("\\n")) {
      null
    } else {
      input.toLowerCase
    }
  }

  def toLowerCase(input: Array[String]): Array[String] = {

    if (input == null || input.isEmpty) {
      Array()
    } else {
      input.map(toLowerCase)
    }
  }

}
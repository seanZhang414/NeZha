package cn.com.duiba.nezha.compute.core.util

import scala.collection.Map

class CollectionSUtil {

}

object CollectionSUtil {

  def mapToJava(map: Map[Int, Double]): java.util.Map[java.lang.Long, java.lang.Double] = {
    val ret: java.util.Map[java.lang.Long, java.lang.Double] = new java.util.HashMap[java.lang.Long, java.lang.Double]

    for (key <- map.keySet) {
      ret.put(0L + key, 0.0 + map.getOrElse(key, 0.0))
    }
    ret
  }

  def mapToJava2(map: Map[Int, Map[Int, Double]]): java.util.Map[java.lang.Long, java.util.Map[java.lang.Long, java.lang.Double]] = {
    val ret: java.util.Map[java.lang.Long, java.util.Map[java.lang.Long, java.lang.Double]] = new java.util.HashMap

    for (key <- map.keySet) {
      ret.put(0L + key, mapToJava(map.getOrElse(key, Map())))
    }
    ret
  }

}
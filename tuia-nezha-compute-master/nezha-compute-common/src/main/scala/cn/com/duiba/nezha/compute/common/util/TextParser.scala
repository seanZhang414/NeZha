package cn.com.duiba.nezha.compute.common.util

import org.apache.spark.mllib.recommendation.Rating
import org.apache.spark.rdd.RDD

/**
 * Created by pc on 2016/11/23.
 */
object TextParser {


  def ratingParse(data: RDD[String], implicitPrefs: Boolean, sep: String, reduceValue: Double): RDD[Rating] = {

    /**
     * *MovieLens ratings are on a scale of
     * Must see
     * Will enjoy
     * It's okay
     * Fairly bad
     * Awful
     */

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




  
  def textLineParse(line: String, sep: String): List[String] = {

    /**
     * 根据分割符 对文本按行分割
     *
     */

    if (line == null) {
      null
    } else {
      line.split(sep, -1).toList
    }
  }


}

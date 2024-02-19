package cn.com.duiba.nezha.compute.biz.load

import cn.com.duiba.nezha.compute.common.util.{MyStringUtil, TextParser}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

/**
 * Created by pc on 2017/1/18.
 */
object DataLoader {


  def dataLoadWithSplit(sc: SparkContext, input: String, sep: String, partitionNums: Int): RDD[List[String]] = {
    // 加载数据
    println("loading data ... ")
    sc.textFile(input).repartition(partitionNums).map(x => TextParser.textLineParse(x, sep)).map(MyStringUtil.stringListStd)

  }

  def dataLoadWithSplitAndSample(sc: SparkContext, input: String, sep: String, sampleRatio: Double, partitionNums: Int): RDD[List[String]] = {

    val ret= input match{
      case null => null
      case _ => sc.textFile(input).sample(false, sampleRatio, 0).map(x => TextParser.textLineParse(x, sep)).map(MyStringUtil.stringListStd).repartition(partitionNums)
    }
    ret
  }


  def dataLoad(sc: SparkContext, input: String): RDD[String] = {

    sc.textFile(input)

  }


}

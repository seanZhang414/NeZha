package cn.com.duiba.nezha.compute.biz.save

import org.apache.spark.mllib.recommendation.Rating
import org.apache.spark.rdd.RDD

/**
 * Created by pc on 2016/11/22.
 */
object ALSSave {
  /**
   * save预测数据
   */
  def rcmdSaveHdfs(data: RDD[(Int, Array[Rating])], output: String) {

    // 封装
    val ret = data.map(r => r match {
      case (user, rArray) => {
        var rString = ""
        // 拼装保存字符串
        rArray.foreach(r => {
          rString = rString + r.product + ":" + r.rating + ","
        })
        (user,rString)
      }
    })

    // 保存
    ret.saveAsTextFile(output)

  }

  /**
   * save预测数据
   */
  def appRcmdSaveHdfs(data: RDD[(Int,Int, Array[Rating])], output: String) {

    // 封装
    val ret = data.map(r => r match {
      case (app,user, rArray) => {
        var rString = ""
        // 拼装保存字符串
        rArray.foreach(r => {
          rString = rString + r.product + ":" + r.rating + ","
        })
        (app,user,rString)
      }
    })

    // 保存
    ret.saveAsTextFile(output)

  }
}
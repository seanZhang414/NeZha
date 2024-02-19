package cn.com.duiba.nezha.compute.deploy

import cn.com.duiba.nezha.compute.biz.params.PSModelParams
import cn.com.duiba.nezha.compute.biz.spark.PsModelStreaming
import cn.com.duiba.nezha.compute.feature.enums.ModelKeyEnum

object ExampleApp {

  def main(args: Array[String]) {
    val model = ModelKeyEnum.FTRL_FM_CVR_MODEL_test
    val isLocal: Boolean = true
    val topic: String = "ocpcadvert_orderlist"
    val batchInterval: Int = 60
    val featureModelId: String = model.getFeatureIndex
    val onlineModelId: String = model.getIndex
    val partNums: Int = 1
    val isCtr: Boolean = model.getIsCtr
    val delay: Int = 60 * 1
    val startTime: String = "2018-03-29 00:00:00"
    val stepSize: Int = 1
    val isRePlay: Boolean = false
    val isSync: Boolean = true
    val sampleRatio = 0.3
    val params = PSModelParams(isLocal, topic, batchInterval,featureModelId,onlineModelId, partNums, isCtr, delay, startTime, stepSize,isRePlay,isSync)
    PsModelStreaming.run(params,sampleRatio)

  }
}

package cn.com.duiba.nezha.compute.deploy

import cn.com.duiba.nezha.compute.biz.params.PSModelParams
import cn.com.duiba.nezha.compute.biz.spark.PsModelStreaming
import cn.com.duiba.nezha.compute.feature.enums.ModelKeyEnum

object FMCVRMV002OLApp {

  def main(args: Array[String]) {
    val model = ModelKeyEnum.FTRL_FM_CVR_MODEL_v002
    val isLocal: Boolean = false
    val topic: String = "ocpcadvert_orderlist"
    val batchInterval: Int = 60*1
    val featureModelId: String = model.getFeatureIndex
    val onlineModelId: String = model.getIndex
    val partNums: Int = 1
    val isCtr: Boolean =  model.getIsCtr
    val delay: Int = 60*5
    val startTime: String = "2018-03-08 00:00:00"
    val stepSize: Int = 2
    val isRePlay:Boolean = false
    val isSync:Boolean =true

    val sampleRatio = 1.0
    val params = PSModelParams(isLocal, topic, batchInterval,featureModelId,onlineModelId, partNums, isCtr, delay, startTime, stepSize,isRePlay,isSync)
    PsModelStreaming.run(params,sampleRatio)

  }
}

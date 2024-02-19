package cn.com.duiba.nezha.compute.deploy

import cn.com.duiba.nezha.compute.biz.params.PSModelParams
import cn.com.duiba.nezha.compute.biz.spark.PsModelStreaming
import cn.com.duiba.nezha.compute.feature.enums.ModelKeyEnum

object FMCTRMV004OLApp {

  def main(args: Array[String]) {
    val model = ModelKeyEnum.FTRL_FM_CTR_MODEL_v004
    val isLocal: Boolean = false
    val topic: String = "ocpcadvert_orderlist"
    val batchInterval: Int = 60
    val featureModelId: String = model.getFeatureIndex
    val onlineModelId: String = model.getIndex
    val partNums: Int = 2
    val isCtr: Boolean =  model.getIsCtr
    val delay: Int = 60*3
    val startTime: String = "2018-03-08 00:00:00"
    val stepSize: Int = 2
    val isRePlay:Boolean = false
    val isSync:Boolean =true

    val sampleRatio = 0.3
    val params = PSModelParams(isLocal, topic, batchInterval,featureModelId,onlineModelId, partNums, isCtr, delay, startTime, stepSize,isRePlay,isSync)
    PsModelStreaming.run(params,sampleRatio)

  }
}

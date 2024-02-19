package cn.com.duiba.nezha.compute.deploy.sample

import cn.com.duiba.nezha.compute.biz.params.PSModelParams
import cn.com.duiba.nezha.compute.biz.spark.{DeepModelParamsFromHbase, PsModelStreaming}
import cn.com.duiba.nezha.compute.feature.enums.ModelKeyEnum

object FMCTRMV001SampleApp {

  def main(args: Array[String]) {
    val model = ModelKeyEnum.FTRL_FM_CTR_MODEL_v001
    val featureModelId: String = model.getFeatureIndex
    val onlineModelId: String = model.getIndex
    val partNums: Int = 2
    val isCtr = model.getIsCtr
    val pathProfix = "file:///data/sample/" //"file:///Users/lwj/Documents/data/model/sample/" //
    val isLocal = true
    val isSave = true
    val sampleRatio = 0.3

    DeepModelParamsFromHbase.run(featureModelId, onlineModelId, partNums, isCtr, pathProfix, isLocal,isSave,sampleRatio)

  }
}

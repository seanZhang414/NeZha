package cn.com.duiba.nezha.compute.deploy.sample

import cn.com.duiba.nezha.compute.biz.spark.DeepModelParamsFromHbase
import cn.com.duiba.nezha.compute.feature.enums.ModelKeyEnum

object ExampleSampleApp {

  def main(args: Array[String]) {
    val model = ModelKeyEnum.FTRL_FM_CTR_MODEL_v001
    val featureModelId: String = model.getFeatureIndex
    val onlineModelId: String = model.getIndex
    val partNums: Int = 2
    val isCtr = model.getIsCtr
    val pathProfix = "file:///Users/lwj/Documents/data/model/sample/" //  "file:///data/model/sample/"
    val isLocal =true
    val isSave = false

    DeepModelParamsFromHbase.run(featureModelId, onlineModelId, partNums, isCtr, pathProfix,isLocal,isSave,0.1)

  }
}

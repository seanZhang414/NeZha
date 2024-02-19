package cn.com.duiba.nezha.compute.deploy

import cn.com.duiba.nezha.compute.biz.bo.SyncBo
import cn.com.duiba.nezha.compute.feature.enums.ModelKeyEnum
import cn.com.duiba.nezha.compute.mllib.fm.ftrl.SparseFMWithFTRL

object SyncApp {

  def main(args: Array[String]) {

    val model = ModelKeyEnum.FTRL_FM_CVR_MODEL_v001

//    SyncBo.syncModel(model.getIndex, model.getFeatureIndex,true)
    SyncBo.delete(model.getFeatureIndex)

  }
}

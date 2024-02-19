package cn.com.duiba.nezha.compute.biz.replay

import cn.com.duiba.nezha.compute.alg.LR
import cn.com.duiba.nezha.compute.biz.bo.AdvertCtrLrModelBo
import com.alibaba.fastjson.JSON


/**
 * Created by pc on 2016/11/22.
 */
object BaseReplayer {


  def replay(modelKey:String) = {


    // 读取模型序列化对象
    val entity = AdvertCtrLrModelBo.getCTRModelByKeyFromMD(modelKey)

    println("entity.getModelKey="+entity.getModelKey)
    println("entity.getFeatureIdxListStr="+entity.getFeatureIdxListStr)
    // 获取模型
    val Model: LR = new LR(entity)

    println("Model.getFeatureCollectionList="+Model.getFeatureCollectionList)
    println("Model.getDictUtil.getFeatureDict="+Model.getDictUtil.getFeatureDict.getFeatureDict)
  }

}
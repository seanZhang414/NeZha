package cn.com.duiba.nezha.compute.examples.fm

import cn.com.duiba.nezha.compute.core.model.local.LocalModel
import cn.com.duiba.nezha.compute.mllib.fm.ftrl.SparseFMWithFTRL
import cn.com.duiba.nezha.compute.mllib.generater.SampleGenerator

object LocalFM {

  def main(args: Array[String]): Unit = {

    val localModel = new LocalModel(Map(), Map(), Map())
    val featureNum = 3
    val boxSize = 10
    val dim = featureNum * boxSize
    val data = SampleGenerator.generateSamples(featureNum, boxSize, 0, 100, 7, 0.5)

    val model = new SparseFMWithFTRL()

    model.setAlpha(1.0).
      setBeta(1.0).
      setFacotrNum(3).
      setLambda1(0.000001).
      setLambda2(0.1).
      setDim(dim).
      setLocalModel(localModel)

    model.train(data)

//        for(i<-0 to 200){
//          val pdata = SampleGenerator.generateSamples(featureNum,  boxSize,0,100, i, 0.3)
//          model.trainCnt=0
//          model.lossCnt=0.0
//          model.train(pdata)
//        }


  }

}
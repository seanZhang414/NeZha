package cn.com.duiba.nezha.compute.mllib.fm.ftrl


import cn.com.duiba.nezha.compute.core.LabeledPoint
import cn.com.duiba.nezha.compute.core.model.local.{LocalMatrix, LocalModel, LocalVector}
import cn.com.duiba.nezha.compute.core.model.ops.VectorOps
import cn.com.duiba.nezha.compute.core.util.{DataUtil, MathUtil}
import cn.com.duiba.nezha.compute.mllib.evaluate.Evaluater

import scala.collection.mutable.ArrayBuffer

class SparseFMWithFTRL extends FMFTRL {


  var localIncrModel: LocalModel = null


  def setLocalIncrModel(localIncrModel: LocalModel): Unit = {
    this.localIncrModel = localIncrModel
  }

  def getLocalIncrModel(): LocalModel = {
    this.localIncrModel
  }


  def train(data: Array[LabeledPoint]): Boolean = SparseFMWithFTRL.train(this, data, true)

  def trainSecond(data: Array[LabeledPoint]): Boolean = SparseFMWithFTRL.train(this, data, false)

}

object SparseFMWithFTRL {


  def train(sFTRL: SparseFMWithFTRL, data: Array[LabeledPoint], first: Boolean): Boolean = {

    if (data == null) {
      return false
    }

    var evaluate = new Evaluater()
    var evaluateC = new Evaluater()

    // 参数解析
    var w0LocalZ = sFTRL.w0LocalZ
    var w0LocalN = sFTRL.w0LocalN

    var wLocalZ = sFTRL.wLocalZ
    var wLocalN = sFTRL.wLocalN

    var vLocalZ = sFTRL.vLocalZ
    var vLocalN = sFTRL.vLocalN

    var factorNum = sFTRL.factorNum

    val alpha = sFTRL.alpha
    val beta = sFTRL.beta
    val lambda1 = sFTRL.lambda1
    val lambda2 = sFTRL.lambda2

    // 损失累计

    // init the increment of model for z and n

    var w0IncrementZ: Double = 0.0
    var w0IncrementN: Double = 0.0

    var wIncrementZ: Map[Int, Double] = Map()
    var wIncrementN: Map[Int, Double] = Map()

    var vIncrementZ: Map[Int, Map[Int, Double]] = Map()
    var vIncrementN: Map[Int, Map[Int, Double]] = Map()


    for (labeledPoint <- data) {
      val label = labeledPoint.label
      val feature = labeledPoint.feature

      if (feature != null) {

        val featureLV = new LocalVector(feature.toSparse)


        //获取权重
        val weight0 = FMFTRL.getWeight0(1: Double, w0LocalZ: Double, w0LocalN: Double, alpha: Double, beta: Double,
          lambda1: Double, lambda2: Double, true)

        val weight = FMFTRL.getWeight(featureLV, wLocalZ: Map[Int, Double], wLocalN: Map[Int, Double], alpha: Double, beta: Double,
          lambda1: Double, lambda2: Double, 0.0, true)

        val vector = FMFTRL.getVector(featureLV, factorNum: Int, vLocalZ: Map[Int, Map[Int, Double]], vLocalN: Map[Int, Map[Int, Double]], alpha: Double, beta: Double,
          lambda1: Double, lambda2: Double, 0.0, true)

        //
        //                        println("w0LocalZ = " + w0LocalZ)
        //                        println("w0LocalN = " + w0LocalN)
        //                        println("wLocalZ = " + wLocalZ)
        //                        println("wLocalN = " + wLocalN)
        //                println("vLocalZ = " + vLocalZ)
        //                        println("vLocalN = " + vLocalN)
        //        //
        //
        //                println("weight0 = " + weight0)
        //                        println("weight = " + weight)
        //        println("vector = " + vector)


        // 预估
        val predVal = FM.predict(featureLV.vector, weight0, weight, vector)




        //        val p_fm = sFTRL.predict(featureLV)
        //        val model = FMFTRL.toJFM(sFTRL.dim,sFTRL.factorNum,weight0,weight,vector)
        //        val p_model = model.predict(new Feature(featureLV.vector.size,featureLV.vector.indices,featureLV.vector.values))
        //
        //        println(s"ori_pre=${p_fm},new_pre=${p_model}")

        //        if (first) {
        //          //计算累计损失
        //          evaluate.add(label, predVal)
        //        } else {
        //          val predValCorrect = Evaluater.getCorrectVal(predVal: Double, sFTRL.preLevelU, sFTRL.preLevelD)
        //          evaluate.add(label, predValCorrect)
        //        }

        val (predValCorrect, cLevel) = Evaluater.getCorrectVal(predVal, sFTRL.preLevelU, sFTRL.preLevelD)
        evaluateC.add(label, predValCorrect, cLevel)
        evaluate.add(label, predVal, 1.0)

        val loss = DataUtil.formatdouble(predVal - label, 7)

        //梯度
        val weight0GradLoss = getWeight0GradLoss(loss)

        val weightGradLoss = getWeightGradLoss(featureLV, loss)

        val vectorGradLoss = getVectorGradLoss(featureLV, factorNum, vector, loss, lambda1 * 10)

        //
        //        println("weight0GradLoss = " + weight0GradLoss)
        //        println("weightGradLoss = " + weightGradLoss)
        //        println("vectorGradLoss = " + vectorGradLoss)


        //计算增量 W0
        val (w0IncrZ, w0IncrN) = getWeight0IncrementZAndN(weight0GradLoss, weight0,
          w0LocalN, w0LocalZ, alpha: Double)

        //增量累计
        w0IncrementZ = incrementVal(w0IncrementZ, w0IncrZ)
        w0IncrementN = incrementVal(w0IncrementN, w0IncrN)

        //更新
        w0LocalZ = incrementVal(w0LocalZ, w0IncrZ)
        w0LocalN = incrementVal(w0LocalN, w0IncrN)


        //计算增量 w
        val (wIncrZ, wIncrN) = getWeightIncrementZAndN(featureLV, weightGradLoss, weight,
          wLocalN, wLocalZ, alpha)

        //增量累计
        wIncrementZ = incrementVector(wIncrementZ, wIncrZ)
        wIncrementN = incrementVector(wIncrementN, wIncrN)

        //更新
        wLocalZ = incrementVector(wLocalZ, wIncrZ)
        wLocalN = incrementVector(wLocalN, wIncrN)


        //计算增量 v
        val (vIncrZ, vIncrN) = getVectorIncrementZAndN(featureLV, factorNum, vectorGradLoss, vector,
          vLocalN, vLocalZ, alpha)

        //增量累计
        vIncrementZ = incrementMatrix(vIncrementZ, vIncrZ)
        vIncrementN = incrementMatrix(vIncrementN, vIncrN)

        //更新
        vLocalZ = incrementMatrix(vLocalZ, vIncrZ)
        vLocalN = incrementMatrix(vLocalN, vIncrN)

        //
        //        println("w0IncrementZ = " + w0IncrementZ)
        //        println("w0IncrementN = " + w0IncrementN)
        //        println("wIncrementZ = " + wIncrementZ)
        //        println("wIncrementN = " + wIncrementN)
        //        println("vIncrementZ = " + vIncrementZ)
        //        println("vIncrementN = " + vIncrementN)
        //
        //
        //        println("new w0LocalZ = " + w0LocalZ)
        //        println("new w0LocalN = " + w0LocalN)
        //        println("new wLocalZ = " + wLocalZ)
        //        println("new wLocalN = " + wLocalN)
        //        println("new vLocalZ = " + vLocalZ)
        //        println("new vLocalN = " + vLocalN)


        // 打印
        //        println("label = " + label + ",predVal = " + predVal + "\n")


      }

    }
    val prelevelNew = evaluate.getLevelMap("origin")
    //        evaluateC.getLevelMap("correct")
    // levelIncr
    val (vIncrementPLU, vIncrementPLD) = incrementPreLevel(sFTRL.preLevelU, sFTRL.preLevelD, prelevelNew, 0.1)

    //    println("vIncrementPLU = " + vIncrementPLU)
    //    println("vIncrementPLD = " + vIncrementPLD)
    //
    //    println("preLevelU = " + sFTRL.preLevelU)
    //    println("preLevelD = " + sFTRL.preLevelD)


    sFTRL.setW0LocalZ(w0LocalZ)
    sFTRL.setW0LocalN(w0LocalN)

    sFTRL.setWLocalZ(wLocalZ)
    sFTRL.setWLocalN(wLocalN)

    sFTRL.setVLocalZ(vLocalZ)
    sFTRL.setVLocalN(vLocalN)

    val localIncrModel = getLocalModel(sFTRL.dim, sFTRL.factorNum,
      w0IncrementZ, w0IncrementN,
      wIncrementZ, wIncrementN,
      vIncrementZ, vIncrementN,
      vIncrementPLU, vIncrementPLD
    )


    sFTRL.setLocalIncrModel(localIncrModel)


    //    println("v_n 2   ---"+localIncrModel.matrixMap)

    evaluate.print();

    true
  }


  def searchModel(dim: Int, factorNum: Int): LocalModel = {

    getLocalModel(dim, factorNum, 0.0, 0.0, Map(), Map(), Map(), Map(), Map(), Map())
  }

  def searchModel(data: Array[LabeledPoint], dim: Int, factorNum: Int): LocalModel = {

    //1 初始化 构造查询 -本地向量、本地矩阵
    var searchValMap: Map[String, Double] = Map()
    var searchVectorMap: Map[String, LocalVector] = Map()
    var searchMatrixMap: Map[String, LocalMatrix] = Map()


    searchValMap += (FMFTRL.w0_n -> 0.0)
    searchValMap += (FMFTRL.w0_z -> 0.0)

    if (data != null) {

      val fArray = data.map(data => data.feature.toSparse)


      val searchSparseVector = VectorOps.toIndexSV(fArray, dim)
      val searchVector = new LocalVector(searchSparseVector)

      searchVectorMap += (FMFTRL.w_n -> searchVector)
      searchVectorMap += (FMFTRL.w_z -> searchVector)


      val searchSparseVectorPL = VectorOps.toVector(dim, Array.tabulate[Int](100)(i => i), Array.tabulate[Double](100)(i => 1.0))
      val searchVectorPL = new LocalVector(searchSparseVectorPL)


      searchVectorMap += (FMFTRL.p_l_u -> searchVectorPL)
      searchVectorMap += (FMFTRL.p_l_d -> searchVectorPL)


      searchMatrixMap += (FMFTRL.v_n -> searchVector.toCopyMatrix(factorNum))

      searchMatrixMap += (FMFTRL.v_z -> searchVector.toCopyMatrix(factorNum))
    }


    new LocalModel(searchValMap, searchVectorMap, searchMatrixMap)
  }

  def getLocalModel(vSize: Int, factorNum: Int,
                    w0Z: Double, w0N: Double,
                    wZ: Map[Int, Double], wN: Map[Int, Double],
                    vZ: Map[Int, Map[Int, Double]], vN: Map[Int, Map[Int, Double]],
                    preLevelU: Map[Int, Double], preLevelD: Map[Int, Double]): LocalModel = {


    var cMap: Map[String, Double] = Map()
    cMap += (FMFTRL.w0_z -> w0Z)
    cMap += (FMFTRL.w0_n -> w0N)

    var vMap: Map[String, LocalVector] = Map()
    vMap += (FMFTRL.w_z -> LocalVector.toLocalVector(vSize, wZ))
    vMap += (FMFTRL.w_n -> LocalVector.toLocalVector(vSize, wN))
    //
    vMap += (FMFTRL.p_l_u -> LocalVector.toLocalVector(vSize, preLevelU))
    vMap += (FMFTRL.p_l_d -> LocalVector.toLocalVector(vSize, preLevelD))

    var mMap: Map[String, LocalMatrix] = Map()
    mMap += (FMFTRL.v_z -> LocalMatrix.toLocalMatrix(vSize, factorNum, vZ))
    mMap += (FMFTRL.v_n -> LocalMatrix.toLocalMatrix(vSize, factorNum, vN))


    new LocalModel(cMap, vMap, mMap)
  }


  // add updateInc to incrementedVal
  def incrementVal(incrementedVal: Double, updateInc: Double): Double = {
    DataUtil.formatdouble(incrementedVal + updateInc, 10)
  }


  // add updateInc to incrementedVal
  def incrementPreLevel(preLevelU: Map[Int, Double], preLevelD: Map[Int, Double], preLevelNew: Map[Int, Double], preLearnRatio: Double): (Map[Int, Double], Map[Int, Double]) = {
    var retUMap: Map[Int, Double] = Map()
    var retDMap: Map[Int, Double] = Map()
    for (level <- preLevelNew.keys) {
      var levelNew = math.min(preLevelNew.getOrElse(level, 0.0), 3.0)
      val levelOldU = preLevelU.get(level)
      val levelOldD = preLevelD.get(level)

      if (levelNew > 0.0) {
        if (levelOldD != None && levelOldU != None) {
          val levelUIncr = DataUtil.formatdouble(preLearnRatio * (levelNew - levelOldU.get), 6)
          val levelDIncr = DataUtil.formatdouble(preLearnRatio * (1 - levelOldD.get), 6)
          retUMap += (level -> levelUIncr)
          retDMap += (level -> levelDIncr)
        } else {
          retUMap += (level -> 1)
          retDMap += (level -> 1)
        }

      }
    }

    (retUMap, retDMap)
  }


  // add updateInc to incrementedVec
  def incrementVector(incrementedVec: Map[Int, Double], updateInc: Array[(Int, Double)]): Map[Int, Double] = {

    var vecAddResult: Map[Int, Double] = incrementedVec

    updateInc.foreach { case (fId, fInc) =>
      val oriVal = vecAddResult.getOrElse(fId, 0.0)
      val newVal = DataUtil.formatdouble(oriVal + fInc, 7)
      vecAddResult += (fId -> newVal)
    }
    vecAddResult
  }


  // add updateInc to incrementedVec
  def incrementMatrix(incrementedVec: Map[Int, Map[Int, Double]], updateInc: Array[(Int, Array[(Int, Double)])]): Map[Int, Map[Int, Double]] = {

    var matrixAddResult: Map[Int, Map[Int, Double]] = incrementedVec

    updateInc.foreach { case (f, fIncArray) =>
      val fMap = matrixAddResult.getOrElse(f, Map())
      matrixAddResult += (f -> incrementVector(fMap, fIncArray))
    }
    matrixAddResult
  }


  def getWeight0GradLoss(loss: Double): Double = {
    loss
  }

  def getWeightGradLoss(feature: LocalVector, loss: Double): Map[Int, Double] = {
    feature.mutiply(loss).toMap()
  }


  def getVectorGradLoss(feature: LocalVector, factorNum: Int, vLocal: Map[Int, Map[Int, Double]], loss: Double, default: Double): Map[Int, Map[Int, Double]] = {

    if (feature.vector != null) {
      val indices = feature.vector.indices
      val x = feature.vector.values

      var v_g: Map[Int, Map[Int, Double]] = Map()

      for (f <- 0 to factorNum - 1) {
        var vjfxj_sum = 0.0
        var v_f = vLocal.getOrElse(f, Map())
        var v_g_f: Map[Int, Double] = Map()


        for (j <- 0 to indices.length - 1) {
          val fId_j = indices.apply(j)
          val xj = x.apply(j)
          val vjf = v_f.getOrElse(fId_j, 0.0)

          vjfxj_sum += xj * vjf
        }


        for (i <- 0 to indices.length - 1) {
          val fId_i = indices.apply(i)
          val xi = x.apply(i)
          val vif = v_f.getOrElse(fId_i, 0.0)

          val value_origin = (xi * vjfxj_sum - vif * xi * xi)

          val value = MathUtil.stdwithBoundary(value_origin, -1.0, 1.0) * loss
          if (math.abs(value) > 0.0) {
            v_g_f += (fId_i -> value)
          } else {
            v_g_f += (fId_i -> default * loss)
          }


        }
        v_g += (f -> v_g_f)


      }
      v_g
    } else {
      Map()
    }

  }


  def getWeight0IncrementZAndN(gradLoss: Double, weight: Double,
                               localN: Double, localZ: Double, alpha: Double): (Double, Double) = {
    getIncrementZAndNOnId(gradLoss, weight, localN, localZ, alpha)
  }

  def getWeightIncrementZAndN(feaure: LocalVector, gradLoss: Map[Int, Double], weight: Map[Int, Double],
                              localN: Map[Int, Double], localZ: Map[Int, Double], alpha: Double): (Array[(Int, Double)], Array[(Int, Double)]) = {
    val indices = feaure.vector.indices
    var zIncrValues = new ArrayBuffer[(Int, Double)]()
    var nIncrValues = new ArrayBuffer[(Int, Double)]()

    for (i <- 0 to indices.length - 1) {
      val fId = indices.apply(i)
      val gOnId = gradLoss.getOrElse(fId, 0.0)
      val wOnId = weight.getOrElse(fId, 0.0)
      val nOnId = localN.getOrElse(fId, 0.0)
      val zOnId = localZ.getOrElse(fId, 0.0)

      val (incrZOnId, incrNOnId) = getIncrementZAndNOnId(gOnId, wOnId, nOnId, zOnId, alpha)

      //      if ((incrNOnId + nOnId) < 0.0 || math.abs(incrNOnId + nOnId) > 100000000.0) {
      //
      //        println(s"w fId=${fId},gOnId=${gOnId},increZ=${incrZOnId},increN=${incrNOnId}, n_old=${nOnId},n_new=${(incrNOnId + nOnId)}")
      //      }

      zIncrValues.append((fId, incrZOnId))
      nIncrValues.append((fId, incrNOnId))
    }
    (zIncrValues.toArray, nIncrValues.toArray)
  }

  def getVectorIncrementZAndN(feaure: LocalVector, factorNum: Int, gradLoss: Map[Int, Map[Int, Double]], weight: Map[Int, Map[Int, Double]],
                              localN: Map[Int, Map[Int, Double]], localZ: Map[Int, Map[Int, Double]], alpha: Double): (Array[(Int, Array[(Int, Double)])], Array[(Int, Array[(Int, Double)])]) = {
    val indices = feaure.vector.indices
    var zIncrValues = new Array[(Int, Array[(Int, Double)])](factorNum)
    var nIncrValues = new Array[(Int, Array[(Int, Double)])](factorNum)

    for (f <- 0 to factorNum - 1) {

      var zfIncrValues = new ArrayBuffer[(Int, Double)]()
      var nfIncrValues = new ArrayBuffer[(Int, Double)]()


      val g_f = gradLoss.getOrElse(f, Map())
      val w_f = weight.getOrElse(f, Map())
      val n_f = localN.getOrElse(f, Map())
      val z_f = localZ.getOrElse(f, Map())

      for (i <- 0 to indices.length - 1) {
        val fId = indices.apply(i)
        val gOnId = g_f.getOrElse(fId, 0.0)
        val wOnId = w_f.getOrElse(fId, 0.0)
        val nOnId = n_f.getOrElse(fId, 0.0)
        val zOnId = z_f.getOrElse(fId, 0.0)


        val (incrZOnId, incrNOnId) = getIncrementZAndNOnId(gOnId, wOnId, nOnId, zOnId, alpha)
        //
        //        if (incrNOnId < 0 || (incrNOnId + nOnId) < 0.0 || math.abs(incrNOnId + nOnId) > 100000000.0) {
        //
        //          println(s"vv  fId=${fId},f=${f},gOnId=${gOnId},increZ=${incrZOnId},increN=${incrNOnId}, n_old=${nOnId},n_new=${(incrNOnId + nOnId)}")
        //        }
        zfIncrValues.append((fId, incrZOnId))
        nfIncrValues.append((fId, incrNOnId))
      }

      zIncrValues(f) = (f -> zfIncrValues.toArray)
      nIncrValues(f) = (f -> nfIncrValues.toArray)


    }


    (zIncrValues, nIncrValues)
  }


  def getIncrementZAndNOnId(gOnId: Double, wOnId: Double,
                            nOnId: Double, zOnId: Double, alpha: Double): (Double, Double) = {

    val sigmaOnId = updateSigmaOnId(gOnId, nOnId, alpha)
    val incrZOnId = incrementZOnId(gOnId, sigmaOnId, wOnId)
    val incrNOnId = incrementNOnId(gOnId)

    //    println(s"gId=${gOnId},wId=${wOnId},nId=${nOnId},zId=${zOnId},sigmaId=${sigmaOnId},incZ=${incrZOnId},incN=${incrNOnId}")
    (incrZOnId, incrNOnId)
  }

  //
  def updateSigmaOnId(gOnId: Double, nOnId: Double, alpha: Double): Double = {


    val ret = (Math.sqrt(nOnId + gOnId * gOnId) - Math.sqrt(nOnId)) / (alpha)
    DataUtil.formatdouble(ret, 7)
  }

  def incrementZOnId(gOnId: Double, sigmaOnId: Double, weightOnId: Double): Double = {
    val ret = gOnId - sigmaOnId * weightOnId
    DataUtil.formatdouble(ret, 7)
  }

  def incrementNOnId(gOnId: Double): Double = {
    val ret = gOnId * gOnId
    DataUtil.formatdouble(ret, 7)
  }

}
package cn.com.duiba.nezha.compute.mllib.fm.ftrl


import cn.com.duiba.nezha.compute.core.LabeledPoint
import cn.com.duiba.nezha.compute.core.model.local.{LocalMatrix, LocalModel, LocalVector}
import cn.com.duiba.nezha.compute.core.model.ops.{MapOps, VectorOps}
import cn.com.duiba.nezha.compute.core.util.{DataUtil, MathUtil}
import cn.com.duiba.nezha.compute.mllib.evaluate.Evaluater
import org.apache.log4j.Logger

import scala.collection.mutable.ArrayBuffer

class SparseFMWithFTRL2 extends FMFTRL {

  var localIncrModel: LocalModel = null


  def setLocalIncrModel(localIncrModel: LocalModel): Unit = {
    this.localIncrModel = localIncrModel
  }

  def getLocalIncrModel(): LocalModel = {
    this.localIncrModel
  }


  def train(data: Array[LabeledPoint], parNums: Int): Boolean = SparseFMWithFTRL2.train(this, data, true, parNums)

  def trainSecond(data: Array[LabeledPoint], parNums: Int): Boolean = SparseFMWithFTRL2.train(this, data, false, 1)

}

object SparseFMWithFTRL2 {

  val logger = Logger.getLogger("SparseFMWithFTRL2.getClass")

  def train(sFTRL: SparseFMWithFTRL2, data: Array[LabeledPoint], first: Boolean, parNums: Int): Boolean = {

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

    val factorNum = sFTRL.factorNum

    val alpha = sFTRL.alpha
    val beta = sFTRL.beta
    val lambda1 = sFTRL.lambda1
    val lambda2 = sFTRL.lambda2


    val rho1 = sFTRL.rho1
    val rho2 = sFTRL.rho2


    println(s"rho1=${rho1},rho2=${rho2}")


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


        // 预估
        val predVal = FM.predict(featureLV.vector, weight0, weight, vector)


        val (predValCorrect, cLevel) = Evaluater.getCorrectVal(predVal, sFTRL.preLevelU, sFTRL.preLevelD)
        evaluateC.add(label, predValCorrect, cLevel)
        evaluate.add(label, predVal, 1.0)

        val loss = DataUtil.formatdouble(predVal - label, 7)


        //梯度
        val weight0GradLoss = getWeight0GradLoss(loss)



        val weightGradLoss = getWeightGradLoss(featureLV, loss)

        val vectorGradLoss = getVectorGradLoss(featureLV, factorNum, vector, loss, lambda1 * 10)


        //计算增量 W0
        val (w0IncrZ, w0IncrN) = getWeight0IncrementZAndN(weight0GradLoss, weight0,
          w0LocalN, w0LocalZ, alpha, beta, rho1, rho2)

        //        println(s"w0LocalZ=${w0LocalZ},w0LocalN=${w0LocalN},w=${weight0},predVal=${predVal},label=${label},w0IncrZ=${w0IncrZ},w0IncrN=${w0IncrN}")

        //增量累计
        w0IncrementZ = incrementVal(w0IncrementZ, w0IncrZ)
        w0IncrementN = incrementVal(w0IncrementN, w0IncrN)

        //更新

        w0LocalZ = incrementVal(w0LocalZ, w0IncrZ)

        w0LocalN = incrementVal(w0LocalN, w0IncrN)

        //        println(s"new w0LocalZ=${w0LocalZ},w0LocalN=${w0LocalN},w=${FMFTRL.getWeight0(1: Double, w0LocalZ: Double, w0LocalN: Double, alpha: Double, beta: Double,
        //          lambda1: Double, lambda2: Double, true)}")

        //计算增量 w
        val (wIncrZ, wIncrN) = getWeightIncrementZAndN(featureLV, weightGradLoss, weight,
          wLocalN, wLocalZ, alpha, beta, rho1, rho2)

        //增量累计
        wIncrementZ = incrementVector(wIncrementZ, wIncrZ)
        wIncrementN = incrementVector(wIncrementN, wIncrN)

        //更新
        wLocalZ = incrementVector(wLocalZ, wIncrZ)
        wLocalN = incrementVector(wLocalN, wIncrN)


        //计算增量 v
        val (vIncrZ, vIncrN) = getVectorIncrementZAndN(featureLV, factorNum, vectorGradLoss, vector,
          vLocalN, vLocalZ, alpha, beta, rho1, rho2)

        //增量累计
        vIncrementZ = incrementMatrix(vIncrementZ, vIncrZ)
        vIncrementN = incrementMatrix(vIncrementN, vIncrN)

        //更新
        vLocalZ = incrementMatrix(vLocalZ, vIncrZ)
        vLocalN = incrementMatrix(vLocalN, vIncrN)


      }

    }
    val prelevelNew = evaluate.getLevelMap("origin")

    val (vIncrementPLU, vIncrementPLD) = incrementPreLevel(sFTRL.preLevelU, sFTRL.preLevelD, prelevelNew, 0.1)


    sFTRL.setW0LocalZ(w0LocalZ)
    sFTRL.setW0LocalN(w0LocalN)

    sFTRL.setWLocalZ(wLocalZ)
    sFTRL.setWLocalN(wLocalN)

    sFTRL.setVLocalZ(vLocalZ)
    sFTRL.setVLocalN(vLocalN)

    val localIncrModel = getLocalModelWithPart(sFTRL.dim, sFTRL.factorNum, parNums,
      w0IncrementZ, w0IncrementN,
      wIncrementZ, wIncrementN,
      vIncrementZ, vIncrementN
    )


    println(s"new w0LocalZ=${w0LocalZ},w0LocalN=${w0LocalN},w0IncrementZ=${w0IncrementZ},w0IncrementN=${w0IncrementN},w=${
      FMFTRL.getWeight0(1: Double, w0LocalZ: Double, w0LocalN: Double, alpha: Double, beta: Double,
        lambda1: Double, lambda2: Double, true)
    }")
    sFTRL.setLocalIncrModel(localIncrModel)

    evaluate.print()

    true
  }


  def searchModel(dim: Int, factorNum: Int): LocalModel = {

    getLocalModel(dim, factorNum, 0.0, 0.0, Map(), Map(), Map(), Map())
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
                    vZ: Map[Int, Map[Int, Double]], vN: Map[Int, Map[Int, Double]]): LocalModel = {


    var cMap: Map[String, Double] = Map()
    cMap += (FMFTRL.w0_z -> w0Z)
    cMap += (FMFTRL.w0_n -> w0N)

    var vMap: Map[String, LocalVector] = Map()
    vMap += (FMFTRL.w_z -> LocalVector.toLocalVector(vSize, wZ))
    vMap += (FMFTRL.w_n -> LocalVector.toLocalVector(vSize, wN))


    var mMap: Map[String, LocalMatrix] = Map()
    mMap += (FMFTRL.v_z -> LocalMatrix.toLocalMatrix(vSize, factorNum, vZ))
    mMap += (FMFTRL.v_n -> LocalMatrix.toLocalMatrix(vSize, factorNum, vN))


    new LocalModel(cMap, vMap, mMap)
  }

  def getLocalModelWithPart(vSize: Int, factorNum: Int, partNums: Int,
                            w0Z: Double, w0N: Double,
                            wZ: Map[Int, Double], wN: Map[Int, Double],
                            vZ: Map[Int, Map[Int, Double]], vN: Map[Int, Map[Int, Double]]): LocalModel = {

        if (partNums == 1) {
          getLocalModel(vSize, factorNum, w0Z, w0N, wZ, wN, vZ, vN)
        } else {
          getLocalModel(vSize, factorNum, partNums, w0Z, w0N, wZ, wN, vZ, vN)
        }
//    getLocalModel(vSize, factorNum, w0Z, w0N, wZ, wN, vZ, vN)
  }

  def getLocalModel(vSize: Int, factorNum: Int, partNums: Int,
                    w0Z: Double, w0N: Double,
                    wZ: Map[Int, Double], wN: Map[Int, Double],
                    vZ: Map[Int, Map[Int, Double]], vN: Map[Int, Map[Int, Double]]): LocalModel = {


    var cMap: Map[String, Double] = Map()
    cMap += (FMFTRL.w0_z -> w0Z / partNums)
    cMap += (FMFTRL.w0_n -> w0N / partNums)

    var vMap: Map[String, LocalVector] = Map()
    vMap += (FMFTRL.w_z -> LocalVector.toLocalVector(vSize, MapOps.multiplication1(wZ, 1 / partNums)))
    vMap += (FMFTRL.w_n -> LocalVector.toLocalVector(vSize, MapOps.multiplication1(wN, 1 / partNums)))
    //

    var mMap: Map[String, LocalMatrix] = Map()
    mMap += (FMFTRL.v_z -> LocalMatrix.toLocalMatrix(vSize, factorNum, MapOps.multiplication2(vZ, 1 / partNums)))
    mMap += (FMFTRL.v_n -> LocalMatrix.toLocalMatrix(vSize, factorNum, MapOps.multiplication2(vN, 1 / partNums)))


    new LocalModel(cMap, vMap, mMap)
  }


  // add updateInc to incrementedVal
  def incrementVal(incrementedVal: Double, updateInc: Double): Double = {
    DataUtil.formatdouble(incrementedVal + updateInc, 7)
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
                               localN: Double, localZ: Double, alpha: Double, beta: Double, rho1: Double, rho2: Double): (Double, Double) = {
    getIncrementZAndNOnId(gradLoss, weight, localN, localZ, alpha, beta, rho1, rho2)
  }

  def getWeightIncrementZAndN(feaure: LocalVector, gradLoss: Map[Int, Double], weight: Map[Int, Double],
                              localN: Map[Int, Double], localZ: Map[Int, Double], alpha: Double, beta: Double, rho1: Double, rho2: Double): (Array[(Int, Double)], Array[(Int, Double)]) = {
    val indices = feaure.vector.indices
    var zIncrValues = new ArrayBuffer[(Int, Double)]()
    var nIncrValues = new ArrayBuffer[(Int, Double)]()

    for (i <- 0 to indices.length - 1) {
      val fId = indices.apply(i)
      val gOnId = gradLoss.getOrElse(fId, 0.0)
      val wOnId = weight.getOrElse(fId, 0.0)
      val nOnId = localN.getOrElse(fId, 0.0)
      val zOnId = localZ.getOrElse(fId, 0.0)

      val (incrZOnId, incrNOnId) = getIncrementZAndNOnId(gOnId, wOnId, nOnId, zOnId, alpha, beta, rho1, rho2)

      zIncrValues.append((fId, incrZOnId))
      nIncrValues.append((fId, incrNOnId))
    }
    (zIncrValues.toArray, nIncrValues.toArray)
  }

  def getVectorIncrementZAndN(feaure: LocalVector, factorNum: Int, gradLoss: Map[Int, Map[Int, Double]], weight: Map[Int, Map[Int, Double]],
                              localN: Map[Int, Map[Int, Double]], localZ: Map[Int, Map[Int, Double]], alpha: Double, beta: Double, rho1: Double, rho2: Double): (Array[(Int, Array[(Int, Double)])], Array[(Int, Array[(Int, Double)])]) = {
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


        val (incrZOnId, incrNOnId) = getIncrementZAndNOnId(gOnId, wOnId, nOnId, zOnId, alpha, beta, rho1, rho2)

        zfIncrValues.append((fId, incrZOnId))
        nfIncrValues.append((fId, incrNOnId))
      }

      zIncrValues(f) = (f -> zfIncrValues.toArray)
      nIncrValues(f) = (f -> nfIncrValues.toArray)


    }


    (zIncrValues, nIncrValues)
  }


  def getIncrementZAndNOnId(gOnId: Double, wOnId: Double,
                            nOnId: Double, zOnId: Double, alpha: Double, beta: Double, rho1: Double, rho2: Double): (Double, Double) = {

    val sigmaOnId = updateSigmaOnId(gOnId, nOnId, alpha, beta)
    val incrZOnId = incrementZOnId(zOnId, gOnId, sigmaOnId, wOnId, rho1)
    val incrNOnId = incrementNOnId(nOnId, gOnId)

    //    println(s"gId=${gOnId},wId=${wOnId},nId=${nOnId},zId=${zOnId},sigmaId=${sigmaOnId},incZ=${incrZOnId},incN=${incrNOnId}")
    (incrZOnId, incrNOnId)
  }

  //
  def updateSigmaOnId(gOnId: Double, nOnId: Double, alpha: Double, beta: Double): Double = {

    val eta_new = FMFTRL.learnRatio(alpha, beta, nOnId + gOnId * gOnId)
    val eta_old = FMFTRL.learnRatio(alpha, beta, nOnId)
    var ret = 1 / (eta_new) - 1 / (eta_old)
    try {
      ret = DataUtil.formatdouble(ret, 7)
    } catch {
      case e: NumberFormatException => {
        logger.warn(s"updateSigmaOnId(${gOnId}, ${nOnId}, ${alpha})=${ret}")
        logger.error("NumberFormatException =" + e)
      }
    }


    ret
  }

  def incrementZOnId(zOnId: Double, gOnId: Double, sigmaOnId: Double, wOnId: Double, rho1: Double): Double = {

    var ret = -(1 - rho1) * zOnId + gOnId - rho1 * sigmaOnId * wOnId

    try {
      ret = DataUtil.formatdouble(ret, 7)
    } catch {
      case e: NumberFormatException => {
        logger.warn(s"incrementZOnId(${gOnId}, ${gOnId}, ${sigmaOnId},${wOnId}, ${rho1})=${ret}")
        logger.error("NumberFormatException =" + e)
      }
    }

    ret


  }

  def incrementNOnId(nOnId: Double, gOnId: Double): Double = {
    var ret = gOnId * gOnId
    DataUtil.formatdouble(ret, 7)
  }


}
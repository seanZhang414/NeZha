package cn.com.duiba.nezha.compute.mllib.fm.ftrl


import cn.com.duiba.nezha.compute.alg.ftrl.FMModel
import cn.com.duiba.nezha.compute.alg.vo.FMDto
import cn.com.duiba.nezha.compute.core.model.local.{LocalModel, LocalVector}
import cn.com.duiba.nezha.compute.core.util.{CollectionSUtil, DataUtil, MathUtil}
import cn.com.duiba.nezha.compute.mllib.fm.ftrl.SparseFMWithFTRL2.logger
import org.apache.log4j.Logger

class FMFTRL extends FM {
  var alpha: Double = 1.0
  var beta: Double = 1.0
  var lambda1: Double = 1.0
  var lambda2: Double = 1.0

  var rho1: Double = 1.0
  var rho2: Double = 1.0

  var w0LocalZ: Double = 0.0
  var w0LocalN: Double = 0.0

  var vLocalZ: Map[Int, Map[Int, Double]] = Map()
  var vLocalN: Map[Int, Map[Int, Double]] = Map()

  var wLocalZ: Map[Int, Double] = Map()
  var wLocalN: Map[Int, Double] = Map()

  var preLevelU: Map[Int, Double] = Map()
  var preLevelD: Map[Int, Double] = Map()


  def setAlpha(alpha: Double): this.type = {
    this.alpha = alpha
    this
  }

  def setBeta(beta: Double): this.type = {
    this.beta = beta
    this
  }

  def setLambda1(lambda1: Double): this.type = {
    this.lambda1 = lambda1
    this
  }

  def setLambda2(lambda2: Double): this.type = {
    this.lambda2 = lambda2
    this
  }


  def setRho1(rho1: Double): this.type = {
    this.rho1 = rho1
    this
  }

  def setRho2(rho2: Double): this.type = {
    this.rho2 = rho2
    this
  }


  def setW0LocalZ(localZ: Double): this.type = {
    this.w0LocalZ = localZ
    this
  }


  def setW0LocalN(localN: Double): this.type = {
    this.w0LocalN = localN
    this
  }


  def setWLocalZ(localZ: Map[Int, Double]): this.type = {
    this.wLocalZ = localZ
    this
  }


  def setWLocalN(localN: Map[Int, Double]): this.type = {
    this.wLocalN = localN
    this
  }

  def setVLocalZ(localZ: Map[Int, Map[Int, Double]]): this.type = {
    this.vLocalZ = localZ
    this
  }


  def setVLocalN(localN: Map[Int, Map[Int, Double]]): this.type = {
    this.vLocalN = localN
    this
  }

  def setPreLevelU(preLevelU: Map[Int, Double]): this.type = {
    this.preLevelU = preLevelU
    this
  }

  def setPreLevelD(preLevelD: Map[Int, Double]): this.type = {
    this.preLevelD = preLevelD
    this
  }


  def setLocalModel(localModel: LocalModel): this.type = {

    setW0LocalZ(localModel.getValue(FMFTRL.w0_z))
    setW0LocalN(localModel.getValue(FMFTRL.w0_n))

    setWLocalZ(localModel.getVector(FMFTRL.w_z).toMap())
    setWLocalN(localModel.getVector(FMFTRL.w_n).toMap())

    setVLocalZ(localModel.getMatrix(FMFTRL.v_z).toMap())
    setVLocalN(localModel.getMatrix(FMFTRL.v_n).toMap())

    setPreLevelU(localModel.getVector(FMFTRL.p_l_u).toMap())
    setPreLevelD(localModel.getVector(FMFTRL.p_l_d).toMap())
    this
  }

  def predict(feature: LocalVector, defaultZN: Double, train: Boolean): Double = FMFTRL.predict(feature, this, defaultZN: Double, train: Boolean)


  def toFM(): FM = FMFTRL.toFM(this)


  def toJFMDto(): FMDto = FMFTRL.toJFMDto(this)

  def toJFM(): FMModel = FMFTRL.toJFM(this)
}

/**
  *
  * @param alpha     学习率参数1
  * @param beta      学习率参数2
  * @param factorNum FM模型因子数
  * @param lambda1   正则1
  * @param lambda2   正则2
  * @param rho1      衰减因子-累计梯度
  * @param rho2      衰减因子-学习率
  */
case class FMFTRLHyperParams(alpha: Double = 1.0,
                             beta: Double = 1.0,
                             factorNum: Int = 3,
                             lambda1: Double = 0.000001,
                             lambda2: Double = 1.0, rho1: Double = 1.0, rho2: Double = 1.0)

object FMFTRL {

  val logger = Logger.getLogger(FMFTRL.getClass)

  val w0_z = "w0_z"
  val w0_n = "w0_n"

  val w_z = "w_z"
  val w_n = "w_n"

  val v_z = "v_z"
  val v_n = "v_n"
  val p_l_u = "p_l_u"
  val p_l_d = "p_l_d"

  def toJFM(fTRL: FMFTRL): FMModel = {

    // weight 0
    val dto = toJFMDto(fTRL)

    val model = new FMModel()
    model.setFMDto(dto)
    model
  }

  def toJFM(dim: Int, factorNum: Int, weight0: Double, weight: Map[Int, Double], vector: Map[Int, Map[Int, Double]]): FMModel = {

    // weight 0
    val dto = toJFMDto(dim: Int, factorNum: Int, weight0: Double, weight: Map[Int, Double], vector: Map[Int, Map[Int, Double]])

    val model = new FMModel()
    model.setFMDto(dto)
    model
  }

  def toJFMDto(fTRL: FMFTRL): FMDto = {

    // weight 0

    val weight0 = getWeight0(1, fTRL.w0LocalZ, fTRL.w0LocalN, fTRL.alpha, fTRL.beta,
      fTRL.lambda1, fTRL.lambda2, false)
    // weight

    val weight = getWeight(fTRL.wLocalZ, fTRL.wLocalN, fTRL.alpha, fTRL.beta,
      fTRL.lambda1, fTRL.lambda2, 0.0, false)

    //vector
    val vector = getVector(fTRL.factorNum, fTRL.vLocalZ, fTRL.vLocalN, fTRL.alpha, fTRL.beta,
      fTRL.lambda1, fTRL.lambda2, 0.0, false)

    //    println("w0LocalZ = " + fTRL.w0LocalZ)
    //    println("w0LocalN = " + fTRL.w0LocalN)
    //    println("wLocalZ = " + fTRL.wLocalZ)
    //    println("wLocalN = " + fTRL.wLocalN)
    //    println("vLocalZ = " + fTRL.vLocalZ)
    //    println("vLocalN = " + fTRL.vLocalN)
    //
    //
    //        println("JFM ori w0 " + weight0)
    //        println("JFM ori w " + weight)
    //        println("JFM ori v " + vector)

    toJFMDto(fTRL.dim, fTRL.factorNum, weight0, weight, vector)
  }


  def toJFMDto(dim: Int, factorNum: Int, weight0: Double, weight: Map[Int, Double], vector: Map[Int, Map[Int, Double]]): FMDto = {


    val fm: FMDto = new FMDto()
    fm.setDim(0L + dim)
    fm.setFactorNum(0L + factorNum)

    fm.setWeight0(weight0)
    fm.setWeight(CollectionSUtil.mapToJava(weight))
    fm.setVector(CollectionSUtil.mapToJava2(vector))
    //
    //
    //
    //        println("JFM w0 " + fm.getWeight0)
    //        println("JFM w " + fm.getWeight)
    //        println("JFM v " + fm.getVector)

    fm
  }


  def toFM(fTRL: FMFTRL): FM = {

    // weight 0

    val weight0 = getWeight0(1, fTRL.w0LocalZ, fTRL.w0LocalN, fTRL.alpha, fTRL.beta,
      fTRL.lambda1, fTRL.lambda2, false)
    // weight

    val weight = getWeight(fTRL.wLocalZ, fTRL.wLocalN, fTRL.alpha, fTRL.beta,
      fTRL.lambda1, fTRL.lambda2, 0, false)

    //vector
    val vector = getVector(fTRL.factorNum, fTRL.vLocalZ, fTRL.vLocalN, fTRL.alpha, fTRL.beta,
      fTRL.lambda1, fTRL.lambda2, 0, false)

    //        println("FM ori w0 " + weight0)
    //        println("FM ori w " + weight)
    //        println("FM ori v " + vector)


    val fm: FM = new FM()
    fm.setDim(fTRL.dim).
      setFacotrNum(fTRL.factorNum).
      setW0Local(weight0).
      setWLocal(weight).
      setVLocal(vector)

    fm
  }

  def predict(feature: LocalVector, fTRL: FMFTRL, defaultZN: Double, train: Boolean): Double = {


    predict(feature,
      fTRL.w0LocalZ, fTRL.w0LocalN,
      fTRL.wLocalZ, fTRL.wLocalN,
      fTRL.vLocalZ, fTRL.vLocalN,
      fTRL.factorNum, fTRL.alpha, fTRL.beta, fTRL.lambda1, fTRL.lambda2, defaultZN: Double, train: Boolean)
  }


  def predict(feature: LocalVector,
              w0LocalZ: Double, w0LocalN: Double,
              wLocalZ: Map[Int, Double], wLocalN: Map[Int, Double],
              vLocalZ: Map[Int, Map[Int, Double]], vLocalN: Map[Int, Map[Int, Double]],
              factorNum: Int, alpha: Double, beta: Double, lambda1: Double, lambda2: Double, defaultZN: Double, train: Boolean): Double = {
    val weight0 = getWeight0(1, w0LocalZ, w0LocalN, alpha, beta, lambda1, lambda2, train: Boolean)
    val weight = getWeight(feature, wLocalZ, wLocalN, alpha, beta, lambda1, lambda2, defaultZN: Double, train: Boolean)
    val vector = getVector(feature, factorNum, vLocalZ, vLocalN, alpha, beta, lambda1, lambda2, defaultZN: Double, train: Boolean)

    FM.predict(feature.vector, weight0, weight, vector)

  }


  def getWeight0(x: Double, localZ: Double, localN: Double, alpha: Double, beta: Double,
                 lambda1: Double, lambda2: Double, train: Boolean): Double = {
    updateWeightOnId(0, localZ, localN, alpha, beta, lambda1, lambda2, train: Boolean)
  }


  def getWeight(localZ: Map[Int, Double], localN: Map[Int, Double], alpha: Double, beta: Double,
                lambda1: Double, lambda2: Double, defaultZN: Double, train: Boolean): Map[Int, Double] = {
    //    val indices = ArrayOps.ones(dim)

    val indices = localZ.keySet.toArray

    getWeight(indices, localZ: Map[Int, Double], localN: Map[Int, Double], alpha: Double, beta: Double,
      lambda1: Double, lambda2: Double, defaultZN: Double, train: Boolean)
  }


  def getWeight(feature: LocalVector, localZ: Map[Int, Double], localN: Map[Int, Double], alpha: Double, beta: Double,
                lambda1: Double, lambda2: Double, defaultZN: Double, train: Boolean): Map[Int, Double] = {
    // the number of not zero of feature
    val indices = feature.vector.indices
    getWeight(indices, localZ: Map[Int, Double], localN: Map[Int, Double], alpha: Double, beta: Double,
      lambda1: Double, lambda2: Double, defaultZN: Double, train: Boolean)
  }

  def getWeight(fIdArr: Array[Int], localZ: Map[Int, Double], localN: Map[Int, Double], alpha: Double, beta: Double,
                lambda1: Double, lambda2: Double, defaultZN: Double, train: Boolean): Map[Int, Double] = {
    // the number of not zero of feature
    var ret: Map[Int, Double] = Map()

    for (fId <- fIdArr) {
      val value = updateWeightOnId(fId, localZ, localN, alpha, beta, lambda1, lambda2, defaultZN: Double, train: Boolean)

      if (math.abs(value) > 0.0) {
        ret += (fId -> value)
      }
    }
    ret
  }

  def getVector(factorNum: Int, localZ: Map[Int, Map[Int, Double]], localN: Map[Int, Map[Int, Double]], alpha: Double, beta: Double,
                lambda1: Double, lambda2: Double, defaultZN: Double, train: Boolean): Map[Int, Map[Int, Double]] = {
    // the number of not zero of feature
    var ret: Map[Int, Map[Int, Double]] = Map()

    for (colNum <- 0 to factorNum - 1) {
      val zRowMap = localZ.getOrElse(colNum, Map())
      val nRowMap = localN.getOrElse(colNum, Map())

      val sub_ret = getWeight(zRowMap: Map[Int, Double], nRowMap: Map[Int, Double], alpha: Double, beta: Double,
        lambda1: Double, lambda2: Double, defaultZN: Double, train: Boolean)

      ret += (colNum -> sub_ret)
    }
    ret
  }

  def getVector(feature: LocalVector, factorNum: Int, localZ: Map[Int, Map[Int, Double]], localN: Map[Int, Map[Int, Double]], alpha: Double, beta: Double,
                lambda1: Double, lambda2: Double, defaultZN: Double, train: Boolean): Map[Int, Map[Int, Double]] = {
    // the number of not zero of feature
    val indices = feature.vector.indices
    getVector(indices, factorNum: Int, localZ: Map[Int, Map[Int, Double]], localN: Map[Int, Map[Int, Double]], alpha: Double, beta: Double,
      lambda1: Double, lambda2: Double, defaultZN: Double, train: Boolean)
  }

  def getVector(fIdArr: Array[Int], factorNum: Int, localZ: Map[Int, Map[Int, Double]], localN: Map[Int, Map[Int, Double]], alpha: Double, beta: Double,
                lambda1: Double, lambda2: Double, defaultZN: Double, train: Boolean): Map[Int, Map[Int, Double]] = {
    // the number of not zero of feature
    var ret: Map[Int, Map[Int, Double]] = Map()

    for (colNum <- 0 to factorNum - 1) {
      var sub_ret: Map[Int, Double] = Map()

      val zRowMap = localZ.getOrElse(colNum, Map())
      val nRowMap = localN.getOrElse(colNum, Map())

      for (fId <- fIdArr) {
        val value = updateWeightOnId(fId, zRowMap, nRowMap, alpha, beta, lambda1, lambda2, defaultZN, train: Boolean)


        if (math.abs(value) > 0.0) {
          sub_ret += (fId -> value)
        }
      }

      ret += (colNum -> sub_ret)
    }

    ret
  }


  // compute new weight
  def updateWeightOnId(fId: Int,
                       zOnId: Double,
                       nOnId: Double,
                       alpha: Double,
                       beta: Double,
                       lambda1: Double,
                       lambda2: Double, train: Boolean): Double = {
    var ret = 0.0
    if (Math.abs(zOnId) > lambda1 || (train && math.random > 0.999999)) {


      val eta = learnRatio(alpha, beta, nOnId)
      ret = (-1) * (1.0 / (lambda2 + 1.0 / eta)) * (zOnId - Math.signum(zOnId).toInt * lambda1)

    }


    try {

      if (nOnId > 100000000 || zOnId > 100000000) {
        logger.warn(s"updateWeightOnId(${fId}, ${zOnId}, ${nOnId}, ${alpha},${beta}, ${lambda1}, ${lambda2}, ${train})=${ret}")

      }
      ret = DataUtil.formatdouble(ret, 7)
    } catch {
      case e: NumberFormatException => {
        logger.warn(s"updateWeightOnId(${fId}, ${zOnId}, ${nOnId}, ${alpha},${beta}, ${lambda1}, ${lambda2}, ${train})=${ret}")
        logger.error("NumberFormatException =" + e)
      }
    }

    ret

  }


  // compute new weight
  def updateWeightOnId(fId: Int,
                       localZ: Map[Int, Double],
                       localN: Map[Int, Double],
                       alpha: Double,
                       beta: Double,
                       lambda1: Double,
                       lambda2: Double, defaultZN: Double, train: Boolean): Double = {
    val zOnId = localZ.getOrElse(fId, defaultZN)
    val nOnId = localN.getOrElse(fId, defaultZN)
    updateWeightOnId(fId, zOnId, nOnId, alpha, beta, lambda1, lambda2, train)
  }

  def learnRatio(alpha: Double, beta: Double, nOnId: Double): Double = {
    0.000001 + alpha / (beta + Math.sqrt(nOnId))
  }

}
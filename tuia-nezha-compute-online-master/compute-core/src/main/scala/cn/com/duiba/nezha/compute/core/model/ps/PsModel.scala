package cn.com.duiba.nezha.compute.core.model.ps

import cn.com.duiba.nezha.compute.core.model.local.{LocalMatrix, LocalModel, LocalVector}


class PsModel(valMap: Map[String, Double], vMap: Map[String, PsVector], mMap: Map[String, PsMatrix], pSize: Int) {

  private var valueMap: Map[String, Double] = valMap

  private var vectorMap: Map[String, PsVector] = vMap

  private var matrixMap: Map[String, PsMatrix] = mMap

  private var parSize = pSize


  def setParSize(ps: Int): this.type = {
    this.parSize = ps
    this
  }

  def setValue(map: Map[String, Double]): this.type = {
    this.valueMap = map
    this
  }

  def addValue(key: String, value: Double): Unit = {
    if (valueMap == null) {
      setValue(Map())
    }
    valueMap += (key -> value)
  }


  def setVector(map: Map[String, PsVector]): this.type = {
    this.vectorMap = map
    this
  }

  def addVector(key: String, vector: PsVector): Unit = {
    if (vectorMap == null) {
      setVector(Map())
    }
    vectorMap += (key -> vector)
  }

  def setMatrix(map: Map[String, PsMatrix]): this.type = {
    this.matrixMap = map
    this
  }

  def addMatrix(key: String, matrix: PsMatrix): Unit = {
    if (matrixMap == null) {
      setMatrix(Map())
    }
    matrixMap += (key -> matrix)
  }

  def getMatrixMap(): Map[String, PsMatrix] = {
    this.matrixMap
  }


  def getVectorMap(): Map[String, PsVector] = {
    this.vectorMap
  }

  def getValueMap(): Map[String, Double] = {
    this.valueMap
  }

  def getValue(key: String): Double = PsModel.getValue(this, key)

  def getVector(key: String): PsVector = PsModel.getVector(this, key)

  def getMatrix(key: String): PsMatrix = PsModel.getMatrix(this, key)

  def toLocalModel(): LocalModel = PsModel.toLocalModel(this)


}

object PsModel {
  def getValue(model: PsModel, key: String): Double = {
    if (model.valueMap != null) {
      model.valueMap(key)
    } else {
      0.0
    }
  }

  def getVector(model: PsModel, key: String): PsVector = {
    if (model.vectorMap != null) {
      model.vectorMap(key)
    } else {
      null
    }
  }

  def getMatrix(model: PsModel, key: String): PsMatrix = {
    if (model.matrixMap != null) {
      model.matrixMap(key)
    } else {
      null
    }
  }

  def toLocalModel(psModel: PsModel): LocalModel = {

    var vectorMap: Map[String, LocalVector] = Map()

    if (psModel.vectorMap != null) {
      for (vkey <- psModel.vectorMap.keys) {
        vectorMap += (vkey -> psModel.vectorMap(vkey).toLocalVector())
      }
    }

    var matrixMap: Map[String, LocalMatrix] = Map()

    if (psModel.matrixMap != null) {
      for (vkey <- psModel.matrixMap.keys) {
        matrixMap += (vkey -> psModel.matrixMap(vkey).toLocalMatrix())
      }
    }
    new LocalModel(psModel.valueMap,vectorMap, matrixMap)
  }
}

//case class PsModelInfo(modelId: String,
//                       parSize: Int = 100000,
//                       version: Int = 0,
//                       updateTime: String,
//                       createTime: String)

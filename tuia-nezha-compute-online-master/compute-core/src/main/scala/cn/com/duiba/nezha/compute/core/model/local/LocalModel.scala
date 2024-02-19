package cn.com.duiba.nezha.compute.core.model.local

import cn.com.duiba.nezha.compute.core.enums.DateStyle
import cn.com.duiba.nezha.compute.core.model.ps._
import cn.com.duiba.nezha.compute.core.util.DateUtil

class LocalModel(cMap: Map[String, Double], vMap: Map[String, LocalVector], mMap: Map[String, LocalMatrix]) {

  private var valueMap: Map[String, Double] = cMap
  private var vectorMap: Map[String, LocalVector] = vMap

  private var matrixMap: Map[String, LocalMatrix] = mMap

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


  def setVector(map: Map[String, LocalVector]): this.type = {
    this.vectorMap = map
    this
  }

  def addVector(key: String, value: LocalVector): Unit = {
    if (vectorMap == null) {
      setVector(Map())
    }
    vectorMap += (key -> value)
  }

  def setMatrix(map: Map[String, LocalMatrix]): this.type = {
    this.matrixMap = map
    this
  }

  def addMatrix(key: String, value: LocalMatrix): Unit = {
    if (matrixMap == null) {
      setMatrix(Map())
    }
    matrixMap += (key -> value)
  }

  def getValue(key: String): Double = LocalModel.getValue(this, key)

  def getVector(key: String): LocalVector = LocalModel.getVector(this, key)

  def getMatrix(key: String): LocalMatrix = LocalModel.getMatrix(this, key)


  def toPsModel(parSize: Int): PsModel = LocalModel.toPsModel(this, parSize)

}

object LocalModel {

  def getInstance(): LocalModel  ={
    new LocalModel(Map(),Map(),Map())
  }
  def getValue(localModel: LocalModel, key: String): Double = {

    if (localModel.valueMap == null) {
      localModel.valueMap = Map()
    }
    if (localModel.valueMap.get(key) == None) {
      localModel.valueMap += (key -> 0.0)
    }

    localModel.valueMap.get(key).get
  }

  def getVector(localModel: LocalModel, key: String): LocalVector = {


    if (localModel.vectorMap == null) {
      localModel.vectorMap = Map()
    }

    if (localModel.vectorMap.get(key) == None) {
      localModel.vectorMap += (key -> new LocalVector(null))
    }
    localModel.vectorMap.get(key).get


  }

  def getMatrix(localModel: LocalModel, key: String): LocalMatrix = {

    if (localModel.matrixMap == null) {
      localModel.matrixMap = Map()
    }

    if (localModel.matrixMap.get(key) == None) {
      localModel.matrixMap += (key -> new LocalMatrix(null))
    }
    localModel.matrixMap.get(key).get



  }

  def toPsModel(model: LocalModel, parSize: Int): PsModel = {

    var vectorMap: Map[String, PsVector] = Map()


    if (model.vectorMap != null) {
      for (vkey <- model.vectorMap.keys) {

        vectorMap += (vkey -> model.vectorMap(vkey).toPsVector(parSize))

      }
    }


    var matrixMap: Map[String, PsMatrix] = Map()

    if (model.matrixMap != null) {

      for (vkey <- model.matrixMap.keys) {
        matrixMap += (vkey -> model.matrixMap(vkey).toPsMatrix(parSize))
      }
    }
    new PsModel(model.valueMap: Map[String, Double], vectorMap, matrixMap, parSize)
  }

}
package cn.com.duiba.nezha.compute.core.model.local

import cn.com.duiba.nezha.compute.core.enums.DateStyle
import cn.com.duiba.nezha.compute.core.model.ps.PsVector
import cn.com.duiba.nezha.compute.core.model.ops.VectorOps
import cn.com.duiba.nezha.compute.core.util.DateUtil
import org.apache.spark.mllib.linalg.{SparseVector, Vectors}

class LocalVector(v: SparseVector) {

  var vector = v

  var map: Map[Int, Double] = null

  def setVector(sv: SparseVector): this.type = {
    this.vector = sv
    this
  }

  def toMap(): Map[Int, Double] = LocalVector.toMap(this)

  def getMap(): Map[Int, Double] = LocalVector.getMap(this)

  def dto(lv2: LocalVector): Double = LocalVector.dto(this, lv2)

  def dtoMap(lv2: Map[Int, Double]): Double = LocalVector.dtoMap(this, lv2)

  def mutiply(m: Double): LocalVector = LocalVector.mutiply(this, m)

  def getOrElse(idx: Int, default: Double): Double = LocalVector.getOrElse(this, idx, default)

  def toPsVector(parSize: Int): PsVector = LocalVector.toPsVector(this, parSize)


  def toCopyMatrix(numCols: Int) = LocalVector.copy(this, numCols)
}


object LocalVector {

  def copy(localVector: LocalVector, numCols: Int): LocalMatrix = {
    val m = VectorOps.toCopyMatrix(localVector.vector, numCols)
    new LocalMatrix(m)
  }

  def dto(lv1: LocalVector, lv2: LocalVector): Double = {

    val m1 = lv1.getMap()
    val m2 = lv2.getMap()
    var ret = 0.0
    if (m1.size <= m2.size) {
      dtoMap(m1, m2)
    } else {
      dtoMap(m2, m1)
    }
  }

  def dtoMap(lv1: LocalVector, lv2: Map[Int, Double]): Double = {
    dtoMap(lv1.getMap(), lv2)
  }


  def mutiply(lv: LocalVector, m: Double): LocalVector = {
    if (lv.vector != null) {

      val m_values = lv.vector.values.map(i => i * m)
      val ret = Vectors.sparse(lv.vector.size, lv.vector.indices, m_values)
      new LocalVector(ret.toSparse)
    } else {
      lv
    }
  }


  def dtoMap(left: Map[Int, Double], right: Map[Int, Double]): Double = {

    var ret = 0.0
    for (idx <- left.keySet) {
      ret += left.getOrElse(idx, 0.0) * right.getOrElse(idx, 0.0)
    }
    ret
  }

  def toLocalVector(vSize: Int, data: Map[Int, Double]): LocalVector = {
    val sv = VectorOps.toVector(vSize, data)
    new LocalVector(sv)
  }

  def toMap(localVector: LocalVector): Map[Int, Double] = {
    VectorOps.toMap(localVector.vector)
  }

  def setMap(localVector: LocalVector): Unit = {
    localVector.map = VectorOps.toMap(localVector.vector)
  }

  def getMap(localVector: LocalVector): Map[Int, Double] = {
    if (localVector.map == null) {
      setMap(localVector)
    }
    localVector.map
  }


  def toPsVector(localVector: LocalVector, parSize: Int): PsVector = {
//    println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS)+" start to  psvector")
    val parVectorMap = VectorOps.toParVector(localVector.vector, parSize)
//    println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS)+" end to  psvector")
    new PsVector(parVectorMap, parSize, localVector.vector.size)

  }


  def getOrElse(localVector: LocalVector, idx: Int, default: Double): Double = {
    if (localVector.map == null) {
      setMap(localVector)
    }
    localVector.map.getOrElse(idx, default)
  }


}


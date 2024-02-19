package cn.com.duiba.nezha.compute.core.model.ps

import cn.com.duiba.nezha.compute.core.model.local.LocalVector
import cn.com.duiba.nezha.compute.core.model.ops.VectorOps
import org.apache.spark.mllib.linalg.SparseVector

class PsVector(pVMap: Map[Int, SparseVector], pSize: Int,vSize:Int) {
  var parVectorMap = pVMap
  var parSize = pSize
  var vectorSize = vSize


  def setParVectorMap(pvm: Map[Int, SparseVector]): this.type = {
    this.parVectorMap = pvm
    this
  }

  def setParSize(ps: Int): this.type = {
    this.parSize = ps
    this
  }

  def setVectorSize(vSize: Int): this.type = {
    this.vectorSize = vSize
    this
  }

//def def

  def getParVectorMap(): Map[Int, SparseVector] = this.parVectorMap
//
//  def getParSize: Int = this.parSize
//
//  def getVectorSize :Int =this.vectorSize
//
  def toLocalVector(): LocalVector = PsVector.toLocalVector(this)
}


object PsVector {

  def toLocalVector(psVector: PsVector): LocalVector = {
    val vector = VectorOps.toVector(psVector.parVectorMap, psVector.parSize,psVector.vectorSize)
    new LocalVector(vector)
  }


  def getParIndices(vmap:Map[Int, SparseVector]): Unit ={
  }

}

//case class ParSparseVector(parId:)



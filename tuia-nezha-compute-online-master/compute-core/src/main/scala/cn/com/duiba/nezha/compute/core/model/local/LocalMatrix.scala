package cn.com.duiba.nezha.compute.core.model.local

import cn.com.duiba.nezha.compute.core.model.ps.{PsMatrix, PsVector}
import cn.com.duiba.nezha.compute.core.model.ops.{MatrixOps, VectorOps}
import org.apache.spark.mllib.linalg.SparseMatrix

class LocalMatrix(sm: SparseMatrix) {

  var matrix = sm

  var map: Map[Int, Map[Int, Double]] = null


  def setMatrix(sm: SparseMatrix): this.type = {
    this.matrix = sm
    this
  }

  def toMap(): Map[Int, Map[Int, Double]] = LocalMatrix.toMap(this)

  def getMap(): Map[Int, Map[Int, Double]] = LocalMatrix.getMap(this)

  //  def getVector(): SparseVector = this.localVector

  def toPsMatrix(parSize: Int): PsMatrix = LocalMatrix.toPsMatrix(this, parSize)
}


object LocalMatrix {


  def toMap(localMatrix: LocalMatrix): Map[Int, Map[Int, Double]] = {
    MatrixOps.toMap(localMatrix.matrix)
  }

  def setMap(localMatrix: LocalMatrix): Unit = {
    if (localMatrix.map == null) {
      localMatrix.map = MatrixOps.toMap(localMatrix.matrix)
    }
  }

  def getMap(localMatrix: LocalMatrix): Map[Int, Map[Int, Double]] = {
    if (localMatrix.map == null) {
      setMap(localMatrix)
    }
    localMatrix.map
  }

  def toLocalMatrix(numRows: Int, numCols: Int, data: Map[Int, Map[Int, Double]]): LocalMatrix = {

    val sm = MatrixOps.toMatrixOnMap(data, numRows, numCols)


    new LocalMatrix(sm)
  }

  def toPsMatrix(localMatrix: LocalMatrix, parSize: Int): PsMatrix = {

    val vMap = MatrixOps.toParVectors(localMatrix.matrix)
    var pVMap: Map[Int, PsVector] = Map()

    for (key <- vMap.keys) {

      val lv = new LocalVector(vMap(key))
      if (lv != null) {
        pVMap += (key -> lv.toPsVector(parSize))
      }
    }
    new PsMatrix(pVMap, localMatrix.matrix.numRows, localMatrix.matrix.numCols)
  }
}

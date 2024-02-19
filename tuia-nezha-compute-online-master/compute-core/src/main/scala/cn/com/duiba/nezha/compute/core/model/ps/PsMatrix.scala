package cn.com.duiba.nezha.compute.core.model.ps

import cn.com.duiba.nezha.compute.core.model.local.LocalMatrix
import cn.com.duiba.nezha.compute.core.model.ops.MatrixOps
import org.apache.spark.mllib.linalg.SparseVector

class PsMatrix(pVMap: Map[Int, PsVector], nRows: Int, nCols: Int) {

  var psVectorMap = pVMap
  var numRows = nRows
  var numCols = nCols


  def setPsVectorMap(pvm: Map[Int, PsVector]): this.type = {
    this.psVectorMap = pvm
    this
  }

  def setNumRows(nrs: Int): this.type = {
    this.numRows = nrs
    this
  }

  def setNumCols(ncs: Int): this.type = {
    this.numCols = ncs
    this
  }

  def toLocalMatrix(): LocalMatrix = PsMatrix.toLocalMatrix(this)
}

object PsMatrix {




  def toLocalMatrix(psMatrix: PsMatrix): LocalMatrix = {
    var vMap: Map[Int, SparseVector] = Map()

    for (key <- psMatrix.psVectorMap.keySet) {
      val sv = psMatrix.psVectorMap(key).toLocalVector().vector
      if(sv!=null){
        vMap += (key -> psMatrix.psVectorMap(key).toLocalVector().vector)
      }
    }
    val sm = MatrixOps.toMatrix(vMap, psMatrix.numRows, psMatrix.numCols)
    new LocalMatrix(sm)
  }
}






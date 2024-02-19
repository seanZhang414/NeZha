package cn.com.duiba.nezha.compute.core.model.ops

import cn.com.duiba.nezha.compute.core.model.ps.{PsMatrix, PsVector}
import org.apache.spark.mllib.linalg.{SparseMatrix, SparseVector}

import scala.collection.mutable.ArrayBuffer

class CollectionOps {

}

case class ParVector(parId: Int, lowBound: Int, highBound: Int, vector: SparseVector)

object CollectionOps {

  def toMap(keys: Array[String], values: Array[Double]): Map[String, Double] = {
    var map: Map[String, Double] = Map()

    if (keys != null && values != null) {
      if (keys.length == values.length) {
        for (i <- 0 to keys.length - 1) {
          map += (keys.apply(i) -> values.apply(i))
        }
      }
    }

    map
  }


  def toMap[T1, T2](keys: Array[T1], values: Array[T2]): Map[T1, T2] = {
    var map: Map[T1, T2] = Map()

    if (keys != null && values != null) {
      if (keys.length == values.length) {
        for (i <- 0 to keys.length - 1) {
          map += (keys.apply(i) -> values.apply(i))
        }
      }
    }

    map
  }


  def toSparseVectorMap(keys: Array[Int], values: Array[SparseVector]): Map[Int, SparseVector] = {
    var map: Map[Int, SparseVector] = Map()

    if (keys != null && values != null) {

      if (keys.length == values.length) {
        for (i <- 0 to keys.length - 1) {
          map += (keys.apply(i) -> values.apply(i))
        }
      }

    }

    map
  }

  def toPsVectorMap(keys: Array[Int], values: Array[PsVector]): Map[Int, PsVector] = {
    var map: Map[Int, PsVector] = Map()

    if (keys != null && values != null) {
      if (keys.length == values.length) {
        for (i <- 0 to keys.length - 1) {
          map += (keys.apply(i) -> values.apply(i))
        }
      }
    }

    map
  }


  def toPsVectorMap(keys: Array[String], values: Array[PsVector]): Map[String, PsVector] = {
    var map: Map[String, PsVector] = Map()
    if (keys != null && values != null) {
      if (keys.length == values.length) {
        for (i <- 0 to keys.length - 1) {
          map += (keys.apply(i) -> values.apply(i))
        }
      }
    }


    map
  }


  def toPsMatrixMap(keys: Array[String], values: Array[PsMatrix]): Map[String, PsMatrix] = {
    var map: Map[String, PsMatrix] = Map()

    if (keys == null || values == null) {
      return map
    }

    if (keys.length == values.length) {
      for (i <- 0 to keys.length - 1) {
        map += (keys.apply(i) -> values.apply(i))
      }
    }
    map
  }


  def getSparseVectorMapKeys(parVectorMap: Map[Int, SparseVector]): Array[Int] = {
    if (parVectorMap != null) {
      parVectorMap.keySet.toArray
    } else {
      Array()
    }

  }

  def getSparseVectorMapValues(parVectorMap: Map[Int, SparseVector], keyArray: Array[Int]): Array[SparseVector] = {
    var valueArray = new ArrayBuffer[SparseVector]()

    if (parVectorMap != null) {
      for (i <- keyArray) {
        valueArray += parVectorMap.get(i).get
      }
    }
    valueArray.toArray
  }


  def getPsVectorMapKeys(parVectorMap: Map[String, PsVector]): Array[String] = {
    if (parVectorMap == null) {
      Array()
    } else {
      parVectorMap.keySet.toArray
    }
  }


  def getPsVectorMapValues(parVectorMap: Map[String, PsVector], keyArray: Array[String]): Array[PsVector] = {
    var valueArray = new ArrayBuffer[PsVector]()
    if (parVectorMap != null) {
      for (i <- keyArray) {
        valueArray += parVectorMap.get(i).get
      }
    }
    valueArray.toArray
  }


  def getPsVectorMapIntKeys(parVectorMap: Map[Int, PsVector]): Array[Int] = {
    if (parVectorMap == null) {
      Array()
    } else {
      parVectorMap.keySet.toArray
    }
  }

  def getStringKeys(map: Map[String, Double]): Array[String] = {
    if (map == null) {
      Array()
    } else {
      map.keySet.toArray
    }
  }

  def getStringMapValues(map: Map[String, Double], keyArray: Array[String]): Array[Double] = {
    var valueArray = new ArrayBuffer[Double]()
    if (map != null) {
      for (i <- keyArray) {
        valueArray += map.get(i).get
      }
    }

    valueArray.toArray
  }

  def getPsVectorMapValues(parVectorMap: Map[Int, PsVector], keyArray: Array[Int]): Array[PsVector] = {
    var valueArray = new ArrayBuffer[PsVector]()
    if (parVectorMap != null) {
      for (i <- keyArray) {
        valueArray += parVectorMap.get(i).get
      }
    }

    valueArray.toArray
  }


  def getPsMatrixMapKeys(map: Map[String, PsMatrix]): Array[String] = {
    if (map == null) {
      Array()
    } else {
      map.keySet.toArray
    }
  }

  def getPsMatrixMapValues(parMatrixMap: Map[String, PsMatrix], keyArray: Array[String]): Array[PsMatrix] = {
    var valueArray = new ArrayBuffer[PsMatrix]()
    if (parMatrixMap != null) {
      for (i <- keyArray) {
        valueArray += parMatrixMap.get(i).get
      }
    }
    valueArray.toArray
  }


  def getSparseMatrixMapKeys(map: Map[String, SparseMatrix]): Array[String] = {
    if (map == null) {
      Array()
    } else {
      map.keySet.toArray
    }
  }

  def getSparseMatrixMapValues(parMatrixMap: Map[String, SparseMatrix], keyArray: Array[String]): Array[SparseMatrix] = {
    var valueArray = new ArrayBuffer[SparseMatrix]()
    if (parMatrixMap != null) {
      for (i <- keyArray) {
        valueArray += parMatrixMap.get(i).get
      }
    }

    valueArray.toArray
  }

}
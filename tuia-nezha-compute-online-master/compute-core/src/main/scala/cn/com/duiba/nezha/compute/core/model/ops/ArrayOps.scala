package cn.com.duiba.nezha.compute.core.model.ops

import scala.collection.mutable.Set // 可以在任何地方引入 可变集合
class ArrayOps {

}

case class BS(status: Boolean, middle: Int)

case class ParBoundInfo(parId: Int, parLength: Int, start: Int, end: Int)

object ArrayOps {

  def ones(size: Int): Array[Int] = {
    Array.tabulate(size)(i => 1)
  }

  def ones(size: Int,value:Double): Array[Double] = {
    Array.tabulate(size)(i => value)
  }
  /**
    * 数组元素查找,返回BSRet（查找位置，小于当前值的最大值，大于当前值的最小值）
    *
    * @param srcArray
    * @param des
    * @return
    */
  def binarySearch(srcArray: Array[Int], des: Int): BS = { //定义初始最小、最大索引


    var low = 0
    var high = srcArray.length - 1
    var middle = -1

    if (srcArray == null) {
      return BS(false, -1)
    }

    //确保不会出现重复查找，越界
    while (low <= high) { //计算出中间索引值
      middle = (high + low) >>> 1 //防止溢出
      if (des == srcArray(middle)) {
        return BS(true, middle)
        //判断下限
      } else if (des < srcArray(middle)) {
        high = middle - 1
        //判断上限
      } else {
        low = middle + 1
      }
    }
    //若没有，则返回-1

    BS(false, middle)
  }

  def searchWithBound(srcArray: Array[Int], idxArray: Array[Int], lowBound: Int, highBound: Int): (Int, Int) = {
    var start = -1
    var end = -1
    var startStatus = false
    var endStatus = false

    if (srcArray != null && srcArray.length > 0) {
      for (i <- 0 to idxArray.length - 1) {
        val idx = idxArray(i)
        val idx_r = idxArray(idxArray.length - 1 - i)

        if (!startStatus & srcArray(idx) >= lowBound & srcArray(idx) < highBound) {
          start = idx
          startStatus = true
        }
        if (!endStatus & srcArray(idx_r) >= lowBound & srcArray(idx_r) < highBound) {
          end = idx_r
          endStatus = true
        }
      }
    }
    (start, end)

  }

  /**
    *
    * @param parSize
    * @param vecSize
    * @return
    */
  def getParNums(parSize: Int, vecSize: Int): Int = {
    if (vecSize % parSize == 0) {
      vecSize / parSize
    } else {
      vecSize / parSize + 1
    }
  }


  def getParArrays(srcArray: Array[Int], parSize: Int, vecSize: Int): Map[Int, ParBoundInfo] = {
    val parNum = getParNums(parSize, vecSize)

    var ret: Map[Int, ParBoundInfo] = Map()

    for (i <- 0 to parNum - 1) {
      ret += ((i + 0) -> getParArray(srcArray, parSize, vecSize, i))
    }
    ret

  }

  def getSubArray(srcArray: Array[Int], start: Int, end: Int): Array[Int] = {
    if (srcArray != null && srcArray.length > 0) {
      srcArray.slice(start, end)
    } else {
      Array()
    }
  }

  def getSubArray(srcArray: Array[Double], start: Int, end: Int): Array[Double] = {
    if (srcArray != null && srcArray.length > 0) {
      srcArray.slice(start, end)
    } else {
      Array()
    }
  }


  //  def getSubArray[T](srcArray:Array[T],start:Int,end:Int): Array[T] ={
  //    if(srcArray.length>0){
  //      srcArray.slice(start,end)
  //    }else{
  //      null
  //    }
  //
  //  }

  /**
    *
    * @param srcArray
    * @param parSize
    * @param vecSize
    * @param parId
    * @return
    */
  def getParArray(srcArray: Array[Int], parSize: Int, vecSize: Int, parId: Int): ParBoundInfo = {

    var ret = ParBoundInfo(parId, 0, -1, -1)
    if (srcArray != null & srcArray.length > 0) {
      val (lowBound, highBound) = getParBound(parSize, vecSize, parId)
      val lowBS = binarySearch(srcArray, lowBound)
      val highBS = binarySearch(srcArray, highBound)


      val idx = Set[Int]()

      idx.add(math.max(lowBS.middle - 1, 0))
      idx.add(math.max(lowBS.middle, 0))
      idx.add(math.min(lowBS.middle + 1, srcArray.length - 1))

      idx.add(math.max(highBS.middle - 1, 0))
      idx.add(math.max(highBS.middle, 0))
      idx.add(math.min(highBS.middle + 1, srcArray.length - 1))

      val (start, end) = searchWithBound(srcArray, idx.toArray.sorted, lowBound, highBound)

      var parLength = 0
      if (start == -1 | end == -1) {
        parLength = 0
      } else {
        parLength = end - start + 1
      }
      ret = ParBoundInfo(parId, parLength, start, end)
    }
    ret

  }

  /**
    *
    * @param parSize
    * @param vecSize
    * @param partId
    * @return
    */
  def getParBound(parSize: Int, vecSize: Int, partId: Int): (Int, Int) = {
    val start = partId * parSize
    val end = math.min(start + parSize, vecSize - 1)
    (start, end)
  }


}
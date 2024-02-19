package cn.com.duiba.nezha.compute.core.model.ops

class MapOps {

}

object MapOps {

  def multiplication1(map: Map[Int, Double], times: Double): Map[Int, Double] = {
    var ret: Map[Int, Double] = Map()
    if (map != null) {
      for ((k, v) <- map) {

        ret += (k -> v * times)
      }
    }

    ret
  }

  def multiplication2(map: Map[Int, Map[Int, Double]], times: Double): Map[Int, Map[Int, Double]] = {
    var ret: Map[Int, Map[Int, Double]] = Map()
    if (map != null) {
      for ((k, v) <- map) {

        ret += (k -> multiplication1(v, times))
      }
    }

    ret
  }

}
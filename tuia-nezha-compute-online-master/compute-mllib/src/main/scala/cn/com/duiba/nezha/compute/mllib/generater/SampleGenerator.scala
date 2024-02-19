package cn.com.duiba.nezha.compute.mllib.generater

import cn.com.duiba.nezha.compute.core.LabeledPoint
import org.apache.spark.mllib.linalg.{SparseVector, Vectors}

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class SampleGenerator {

}

object SampleGenerator {
  var rand = new Random()

  //
  def generateSamples(fDim: Int, boxNum: Int,startId:Int, endId: Int, seed: Int, pRatio: Double): Array[LabeledPoint] = {

    rand = new Random(seed)


    val sigmaP = Array.tabulate(fDim)({ i => 1.0 + rand.nextDouble() })

    val muP = Array.tabulate(fDim)({ i => rand.nextDouble() + 2 })

    val sigmaN = Array.tabulate(fDim)({ i => 1.5 + rand.nextDouble() })

    val muN = Array.tabulate(fDim)({ i => rand.nextDouble() - 2 })

    Array.tabulate(startId)(i=>rand.nextDouble())
    Array.tabulate(endId-startId)(
      i => generatePoint(pRatio: Double, fDim: Int, boxNum: Int,
        sigmaP: Array[Double], muP: Array[Double],
        sigmaN: Array[Double], muN: Array[Double])
    )
  }

  def generatePoint(pRatio: Double, fDim: Int, boxNum: Int,
                    sigmaP: Array[Double], muP: Array[Double],
                    sigmaN: Array[Double], muN: Array[Double]): LabeledPoint = {

    val indices = new ArrayBuffer[Int]()

    val values = new ArrayBuffer[Double]()

    val vSize = fDim * boxNum

    var sigma = sigmaP
    var mu = muP
    var label = 1.0

    if (rand.nextDouble() > pRatio) {
      sigma = sigmaN
      mu = muN
      label = 0.0
    }

    for (fId <- 0 to fDim - 1) {

      val fValue = nextFValue(sigma(fId), mu(fId))
      val fNum = getFNum(fValue, boxNum, sigmaP(fId), sigmaN(fId), muP(fId), muN(fId))
      indices += fId * boxNum + fNum
      values += 1.0
    }
    val sv = Vectors.sparse(vSize, indices.toArray, values.toArray).asInstanceOf[SparseVector]
    LabeledPoint(label, sv)
  }


  def getFNum(value: Double, boxNum: Int, sigma1: Double, sigma2: Double, mu1: Double, mu2: Double): Int = {
    val upLimit = math.max(mu1 + 3 * sigma1, mu2 + 3 * sigma2)
    val downLimit = Math.min(mu1 - 3 * sigma1, mu2 - 3 * sigma2)
    val valueLimit = math.max(downLimit, math.min(value, upLimit))

    //    println(s"upLimit${upLimit},downLimit${downLimit},valueLimit${valueLimit}")
    val ratio = (valueLimit - downLimit) / (upLimit - downLimit)

    val ss = math.floor((ratio) * boxNum)
    math.min(ss.toInt, boxNum - 1)
  }

  def nextFValue(sigma: Double, mu: Double): Double = {

    rand.nextGaussian() * sigma + mu
  }


  def nextGaussian(rand: Random, sigma: Double, mu: Double): Double = {
    rand.nextGaussian() * sigma + mu
  }


  def main(args: Array[String]): Unit = {


    var pMap: Map[Int, Int] = Map()
    var nMap: Map[Int, Int] = Map()

    val data = generateSamples(1, 10, 0,1000, 2, 0.5)

    data.foreach(f => {

      for (c <- f.feature.toSparse.indices) {
        if (f.label > 0.5) {
          val ori = pMap.getOrElse(c, 0)
          pMap += (c -> (ori + 1))
        } else {
          val ori = nMap.getOrElse(c, 0)
          nMap += (c -> (ori + 1))
        }
      }
//      println(f)
    })

    println(pMap)
    println(nMap)

  }
}

package cn.com.duiba.nezha.compute.biz.app.ml

import java.util.Random

import breeze.linalg.{DenseMatrix, DenseVector, sum}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer

/**
 * Logistic regression based classification.
 */
object FM {


  case class DataPoint(x: DenseVector[Double], y: Double)

  //
  case class Prdlabel(py: Double, y: Double)

  case class GradParams(w0: Double, w: DenseVector[Double], v: DenseMatrix[Double])

  //
  //
  def generateData(D: Int, N: Int, R: Double, seed: Int) = {
    val rand = new Random(seed)
    def generatePoint(i: Int) = {
      val y = if (i % 2 == 0) -1 else 1
      val x = DenseVector.rand[Double](D) + y * R
      DataPoint(x, y)
    }
    Array.tabulate(N)(generatePoint)
  }


  //
  def sigmoid(inx: Double): Double = {
    1 / (1 + math.exp(-inx))
  }


  //
  def mertix(w0: Double, w: DenseVector[Double], v: DenseMatrix[Double], data: RDD[DataPoint], threshold: Double): RDD[Prdlabel] = {
    val ret = data.map(p => Prdlabel(predLabel(p, w0, w, v, threshold), p.y))
    ret
  }

  //
  def labelCompare(prdlabel: Prdlabel): Int = {
    val py = prdlabel.py
    val y = prdlabel.y
    val ret = if (math.abs(py - y) < 0.1) 1 else 0
    ret

  }


  //
  //
  def accuracy(da: RDD[Prdlabel]): Double = {
    var count = da.count()
    val ret = da.map(labelCompare).reduce((x, y) => x + y)
    (ret + 0.0) / (count + 1)
  }


  //
  def sign(w: Double): Double = {
    if (w > 0.0) 1.0 else -1.0
  }

  //
  def sign(w: DenseVector[Double]): DenseVector[Double] = {
    w.map(f => {
      if (f > 0) 1.0 else -1.0
    })
  }

  //
  def sign(v: DenseMatrix[Double]): DenseMatrix[Double] = {
    v.map(f => {
      if (f > 0) 1.0 else -1.0
    })
  }

  //
  def signLabel(w: Double, threshold: Double): Double = {
    if (w >= threshold) 1.0 else -1.0
  }


  //
  def multiply(x: DenseMatrix[Double], y: DenseMatrix[Double]): DenseMatrix[Double] = {
    x :* y
  }

  def paramsDelta(w0: Double, w: DenseVector[Double], v: DenseMatrix[Double],
                  new_w0: Double, new_w: DenseVector[Double], new_v: DenseMatrix[Double]): Double = {

    val p_w0 = (w0 - new_w0) * (w0 - new_w0)
    val p_w = (w - new_w) dot (w - new_w)
    val p_v = sum(multiply((v - new_v), v - new_v))

    p_w0 + p_w + p_v

  }


  def gradAscent(data: RDD[DataPoint], D: Int, F: Int, a: Double, r1: Double, r2: Double, MAX_ITERATIONS: Int, MIN_ITERATIONS: Int, DELTA_THRESHOLD: Double): (Double, DenseVector[Double], DenseMatrix[Double]) = {
    // Initialize w to a random value
    var w = DenseVector.rand[Double](D)
    var w0 = 0.0
    var v = DenseMatrix.rand[Double](D,F)

    val N = data.count()


    var delta = 1.0
    var i = 0
    while (delta > DELTA_THRESHOLD && i < MAX_ITERATIONS) {
      //
      println("On iteration " + i)


      // 计算梯度偏差

      // 计算梯度偏差
      val data_g = data.map(p => {
        val inter_1 = m_m_dot(p.x.toDenseMatrix, v)
        val x_2 = multiply(p.x.toDenseMatrix, p.x.toDenseMatrix)
        val v_2 = multiply(v, v)
        val inter_2 = x_2 * v_2
        //
        val interaction = sum(multiply(inter_1, inter_1) - inter_2)
        //
        val inx = w0 + (p.x dot w) + interaction
        val err = (sigmoid(inx) - p.y)

        val grd1 = err
        val grd2 = err * p.x
        val grd3 = err * m_m_sub(p.x.toDenseMatrix.t * inter_1, multiply(denseVector2Matrix(x_2, F).t, v_2))

        GradParams(grd1, grd2, grd3)
      }).cache()

      val grad_ps = data_g.reduce((g1, g2) => GradParams((g1.w0+g2.w0), (g1.w + g2.w), (g1.v + g2.v)))

//      val w0_g = grad_ps.w0
//      val w_g =  data_g.map(g => g.w).reduce((x, y) => x + y)
//      val v_g =  data_g.map(g => g.v).reduce((x, y) => x + y)
//
//      println("w0_g="+w0_g)
//      println("w_g="+w_g)
//      println("v_g="+v_g)
//      println("gps="+grad_ps)

      // 更新参数值
      val new_w0 = updateW0(w0, grad_ps.w0, a, r1, r2, N)
      val new_w = updateW(w, grad_ps.w, a, r1, r2, N)
      val new_v = updateV(v, grad_ps.v, a, r1, r2, N)

      //
      if (i > MIN_ITERATIONS) {
        delta = paramsDelta(w0, w, v, new_w0, new_w, new_v)
      }

      //
      w0 = new_w0
      w = new_w
      v = new_v

      i += 1
    }

    (w0, w, v)

  }

  def m_m_dot(v: DenseMatrix[Double], m: DenseMatrix[Double]): DenseMatrix[Double] = {
    val ret = (v * m)
    ret.asInstanceOf[DenseMatrix[Double]]
  }

  def m_m_sub(v: DenseMatrix[Double], m: DenseMatrix[Double]): DenseMatrix[Double] = {
    val ret = (v - m)
    ret.asInstanceOf[DenseMatrix[Double]]
  }

  def predLabel(p: DataPoint, w0: Double, w: DenseVector[Double], v: DenseMatrix[Double], threshold: Double): Double = {
    signLabel(predDouble(p, w0, w, v), threshold)
  }


  def predDouble(p: DataPoint, w0: Double, w: DenseVector[Double], v: DenseMatrix[Double]): Double = {

    val inter_1 = m_m_dot(p.x.toDenseMatrix, v)
    val x_2 = multiply(p.x.toDenseMatrix, p.x.toDenseMatrix)
    val v_2 = multiply(v, v)
    val inter_2 = x_2 * v_2
    //
    val interaction = sum(multiply(inter_1, inter_1) - inter_2)
    //
    val inx = w0 + (p.x dot w) + interaction


    sigmoid(inx)

  }


  def updateW0(w: Double, grad: Double, a: Double, r1: Double, r2: Double, n: Long): Double = {
    val p1 = (1 - a * r2) * w
    val p2 = a * r1 * sign(w) * w
    val p3 = a * 1.0 / n * grad
    p1 - p2 - p3

  }

  def updateW(w: DenseVector[Double], grad: DenseVector[Double], a: Double, r1: Double, r2: Double, n: Long): DenseVector[Double] = {
    //    (1 - a * r2) * w - a * r1 * sign(w) :* w - a * 1.0 / n * grad

    val p1 = (1 - a * r2) * w
    val p2 = a * r1 * sign(w) :* w
    val p3 = a * 1.0 / n * grad
    p1 - p2 - p3
  }


  def updateV(v: DenseMatrix[Double], grad: DenseMatrix[Double], a: Double, r1: Double, r2: Double, n: Long): DenseMatrix[Double] = {
    //    (1 - a * r2) * v - a * r1 * sign(v) :* v - a * 1.0 / n * grad
    val p1 = (1 - a * r2) * v
    val p2 = a * r1 * sign(v) :* v
    val p3 = a * 1.0 / n * grad
    p1 - p2 - p3


  }

  def denseVector2Matrix(v: DenseVector[Double], rowRepNums: Int): DenseMatrix[Double] = {
    val r_a = ArrayBuffer[Double]()
    val v_a = v.toArray
    for (i <- 1 to rowRepNums) {
      r_a ++= v_a

    }
    val r_m = DenseMatrix(r_a)
    r_m.reshape(v.length, rowRepNums).t

  }

  def denseVector2Matrix(m: DenseMatrix[Double], rowRepNums: Int): DenseMatrix[Double] = {

    val r_a = ArrayBuffer[Double]()
    val rows = m.rows
    val cols = m.cols
    val m_rs = m.t.toDenseVector.toArray
    for (i <- 1 to rowRepNums) {
      r_a ++= m_rs
    }
    val r_m = DenseMatrix(r_a)
    r_m.reshape(cols, rows * rowRepNums).t

  }

  def add(x: Double, y: Double): Double = {
    x + y
  }

  def add(x: DenseVector[Double], y: DenseVector[Double]): DenseVector[Double] = {
    x + y
  }


  def add(x: DenseMatrix[Double], y: DenseMatrix[Double]): DenseMatrix[Double] = {
    x + y
  }


  def main(args: Array[String]) {
//    Logger.getLogger("org").setLevel(Level.ERROR)
    // 1.初始spark context
    println("init spark context ... ")
    //    System.setProperty("spark.default.parallelism", "10")
    var conf = new SparkConf().setAppName("AdvertCTRLR").setMaster("local[3]") // 本地运行模式，读取本地的spark主目录


    val sc = new SparkContext(conf) // 获取 sc


    // Number of data points
    val N = 1000
    // Number of dimensions
    val D = 20
    val F = 5
    val R = 0.7
    // Scaling factor

    val a = 0.05
    val r2 = 2
    val r1 = 0.25
    val threshold = 0.5

    val MAX_ITERATIONS = 100
    val MIN_ITERATIONS = 50

    val training_data = generateData(D, N, R, 42)

    val training_dataRdd = sc.parallelize(training_data)

    val (w0, w, v) = gradAscent(training_dataRdd, D, F, a, r1, r2, MAX_ITERATIONS, MIN_ITERATIONS, 0.00001)

    println("w0= " + w0)
    println("w= " + w)
    println("v= " + v)

    val test_data = generateData(D, 1000, R, 42)

    val test_dataRdd = sc.parallelize(test_data)

    val prdMertix = mertix(w0, w, v, test_dataRdd, threshold)
    val acc = accuracy(prdMertix)
    println("accuracy(prdMertix)= " + acc)


  }


}
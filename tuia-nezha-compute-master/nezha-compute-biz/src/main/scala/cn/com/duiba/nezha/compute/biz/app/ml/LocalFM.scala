package cn.com.duiba.nezha.compute.biz.app.ml

import java.util.Random

import breeze.linalg.{DenseMatrix, DenseVector, sum}

import scala.collection.mutable.ArrayBuffer

/**
 * Logistic regression based classification.
 */
object LocalFM {


  case class DataPoint(x: DenseVector[Double], y: Double)

  //
  case class Prdlabel(py: Double, y: Double)

  //


  //
  def generateData(D: Int, N: Int, R: Double,seed:Int) = {
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
  def mertix(w0: Double, w: DenseVector[Double], v: DenseMatrix[Double], data: Array[DataPoint], threshold: Double): Array[Prdlabel] = {
    val arrBuffer = ArrayBuffer[Prdlabel]()
    for (p <- data) {
      val pv = predLabel(p, w0, w, v, threshold)
      arrBuffer += Prdlabel(pv, p.y)
    }
    val ret = arrBuffer.toArray
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
  def accuracy(da: Array[Prdlabel]): Double = {
    var count = 0
    for (line <- da) {
      val cr = labelCompare(line)
      if (cr == 1) {
        count += 1
      }

    }
    (count + 0.0) / da.length
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


  def gradAscent(data: Array[DataPoint], D: Int, F: Int, a: Double, r1: Double, r2: Double, MAX_ITERATIONS: Int, MIN_ITERATIONS: Int): (Double, DenseVector[Double], DenseMatrix[Double]) = {
    // Initialize w to a random value
    var w = DenseVector.rand[Double](D)
    var w0 = 0.0
    var v = DenseMatrix.rand[Double](D,F)

    val N = data.length


    var delta = 1.0
    var i = 0
    while (delta > 0.0000001 && i < MAX_ITERATIONS) {
      //
      println("On iteration " + i)

      var v_g = DenseMatrix.zeros[Double](D,F)
      var w_g = DenseVector.zeros[Double](D)
      var w0_g = 0.0

      // 计算梯度偏差
      for (p <- data) {
        val inter_1 = m_m_dot(p.x.toDenseMatrix, v)



        val x_2 = multiply(p.x.toDenseMatrix, p.x.toDenseMatrix)
        val v_2 = multiply(v, v)
        val inter_2 = x_2 * v_2
        //
        val interaction = sum(multiply(inter_1, inter_1) - inter_2)
        //
        val inx = w0 + (p.x dot w) + interaction
        val err = (sigmoid(inx) - p.y)

        w0_g += err
        w_g += err * p.x
        v_g += err * (m_m_dot(p.x.toDenseMatrix.t , inter_1) - multiply(denseVector2Matrix(x_2, F).t, v_2))

        println(s"GradParams(grd1,grd2,grd3)=${w0_g},${w_g},${v_g}")

      }


      println(s"GradParams(grd1,grd2,grd3)=${w0_g},${w_g},${v_g}")
      // 更新参数值
      val new_w0 = updateW0(w0, w0_g, a, r1, r2, N)
      val new_w = updateW(w, w_g, a, r1, r2, N)
      val new_v = updateV(v, v_g, a, r1, r2, N)



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

  def m_m_dot(v:DenseMatrix[Double],m:DenseMatrix[Double]): DenseMatrix[Double] ={
    val ret =  (v*m)
    ret.asInstanceOf[DenseMatrix[Double]]
  }

  def predLabel(p: DataPoint, w0: Double, w: DenseVector[Double], v: DenseMatrix[Double], threshold: Double): Double = {
    signLabel(predDouble(p, w0, w, v), threshold)
  }


  def predDouble(p: DataPoint, w0: Double, w: DenseVector[Double], v: DenseMatrix[Double]): Double = {
    val inter_1 = p.x.toDenseMatrix * v

    val x_2 = multiply(p.x.toDenseMatrix, p.x.toDenseMatrix)
    val v_2 = multiply(v, v)

    val inter_2 = x_2 * v_2
    //
    val interaction = sum(multiply(inter_1, inter_1) - inter_2)
    //
    val y = w0 + (p.x dot w) + interaction

    sigmoid(y)

  }


  def updateW0(w: Double, grad: Double, a: Double, r1: Double, r2: Double, n: Int): Double = {
    val p1 = (1 - a * r2) * w
    val p2 = a * r1 * sign(w) * w
    val p3 = a * 1.0 / n * grad
    p1 - p2 - p3

  }

  def updateW(w: DenseVector[Double], grad: DenseVector[Double], a: Double, r1: Double, r2: Double, n: Int): DenseVector[Double] = {
    //    (1 - a * r2) * w - a * r1 * sign(w) :* w - a * 1.0 / n * grad

    val p1 = (1 - a * r2) * w
    val p2 = a * r1 * sign(w) :* w
    val p3 = a * 1.0 / n * grad
    p1 - p2 - p3
  }


  def updateV(v: DenseMatrix[Double], grad: DenseMatrix[Double], a: Double, r1: Double, r2: Double, n: Int): DenseMatrix[Double] = {
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

  def main(args: Array[String]) {
    // Number of data points
    val N = 100
    // Number of dimensions
    val D = 3
    val F = 1
    val R = 0.7
    // Scaling factor

    val a = 0.01
    val r2 = 2
    val r1 = 0.25
    val threshold = 0.5

    val MAX_ITERATIONS = 1
    val MIN_ITERATIONS = 50

    val data = generateData(D, N, R,42)

    val (w0, w, v) = gradAscent(data, D, F, a, r1, r2, MAX_ITERATIONS, MIN_ITERATIONS)

    println("w0= "+w0)
    println("w= "+w)
    println("v= "+v)
    val test = generateData(D, N, R,42)

    val prdMertix = mertix(w0, w, v, test, threshold)
    val acc = accuracy(prdMertix)
    println("accuracy(prdMertix)= "+acc)


  }


}
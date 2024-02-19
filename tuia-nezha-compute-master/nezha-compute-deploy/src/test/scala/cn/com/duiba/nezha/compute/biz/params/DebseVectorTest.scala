package cn.com.duiba.nezha.compute.biz.params

import java.util.Random

import breeze.linalg.{DenseMatrix, DenseVector, sum}

import scala.collection.mutable.ArrayBuffer

/**
 * Created by pc on 2017/6/8.
 */
object DebseVectorTest {


  def main (args: Array[String]){



    //内积
    val r1 = DenseVector(1,2,3,4) dot DenseVector(1,1,1,1)
    println(r1)
    var w = DenseVector(Array(1.0,2.0,3.0))
    var w2 = DenseVector(Array(4.0,5.0,6.0))
    val a_3 = w.asDenseMatrix
    val b_3 = w2.asDenseMatrix


    println("a_3 + b_3="+(a_3 + b_3))
    println("a_3 :* b_3="+(a_3 :* b_3))
    println("a_3 :/ b_3="+(a_3 :/ b_3))
    println("a_3 :< b_3="+(a_3 :< b_3))
    println("a_3 * b_3"+sum(a_3 :* b_3))

//    println("1 / (1 + math.exp(-1 * ((w dot w2)+1.0)))="+ (1 / (1 + math.exp(-1 * ((w dot w2) + 1.0)))))



    val v = DenseMatrix.ones[Double](2,3)*0.1
    println("v="+(v))


    val x = DenseVector.rand[Double](10)+3.1
    println("x="+(x))


    val r = w.map(f => {if (f > 0) 1.0 else -1.0})

    println("r="+(r))


    val a1 = DenseMatrix((1.0,2.0,3.0))
    val a4 = DenseMatrix((4.0,5.0,6.0))
    val a2 =  DenseMatrix.ones[Double](3,2) * (2 * new Random(42).nextDouble - 1)
    val a3 = DenseMatrix((1.0,2.0,3.0),(4.0,5.0,6.0))
    println(" a1 * a2 "+(a1 * a2))
//    println(" a1 dot a2 "+( a1 dot a2))

    multiply(a1,a1)
    println("multiply(a1,a1)"+(multiply(a1,a1)))


    multiply(a1,a1)
    println("multiply(a1,a1)"+(multiply(a1,a1)))

    println("w*a3="+(w.toDenseMatrix*a3.t))

    println("a1*a4="+(a1.t*a4))

    multiply(a1,a1)

    val m_3 = DenseMatrix((1,2,3),(4,5,6),(7,8,9))
    //取上三角和下三角
    //copy生成一个新的矩阵
//    m_3.copy
//    //对角线生成一个向量
//    //改变矩阵里面的元素
//    m_3(::,2) := 5
//    println("m_3="+m_3)
//    m_3(1 to 2,1 to 2) := 5
//    m_3(0,0 to 2) :=9

    println("m_3="+m_3)



    println("v_r_m="+denseVector2Matrix(w,10))


    println("m_r_m="+denseVector2Matrix(a3,3))


    println("sign(a3)="+sign(a3))


    println("w1="+w)
    println("w1*w2="+w.t*w)
    println("w1 dot w2="+(w dot w))


    val D = 4
    val F = 3
    val v2 = DenseMatrix.rand[Double](D,F)

    val p = DenseVector.rand[Double](D)

    val inter_1 = m_m_dot(p.toDenseMatrix,v2)

    println("v2="+v2)
    println("p="+p)
    println("inter_1="+inter_1)

    val m22 = m_m_dot(p.toDenseMatrix.t , inter_1)
    println("m22="+m22)

  }

  def m_m_dot(v:DenseMatrix[Double],m:DenseMatrix[Double]): DenseMatrix[Double] ={
    val ret =  (v*m)
    ret.asInstanceOf[DenseMatrix[Double]]
  }



  def v_m_dot(v:DenseVector[Double],m:DenseMatrix[Double]): DenseMatrix[Double] ={
    val ret =  (v.toDenseMatrix*m)
    ret.asInstanceOf[DenseMatrix[Double]]
  }


  def multiply(x:DenseMatrix[Double],y:DenseMatrix[Double]): DenseMatrix[Double] ={
    x :* y
  }

  def denseVector2Matrix(v:DenseVector[Double], rowRepNums:Int): DenseMatrix[Double] ={
    val r_a = ArrayBuffer[Double]()
    val v_a = v.toArray
    for(i<- 1 to rowRepNums){
      r_a ++= v_a

    }
    val r_m = DenseMatrix(r_a)
    r_m.reshape(v.length,rowRepNums).t

  }
  def denseVector2Matrix(m:DenseMatrix[Double], rowRepNums:Int): DenseMatrix[Double] ={

    val r_a = ArrayBuffer[Double]()
    val rows = m.rows
    val cols = m.cols
    val m_rs =m.t.toDenseVector.toArray
    for(i<- 1 to rowRepNums){
      r_a ++= m_rs
    }
    val r_m = DenseMatrix(r_a)
    r_m.reshape(cols,rows*rowRepNums).t

  }


  //
  def sign(v: DenseMatrix[Double]): DenseMatrix[Double] = {
    v.map(f => {
      if (f > 0) 1.0 else -1.0
    })
  }

}

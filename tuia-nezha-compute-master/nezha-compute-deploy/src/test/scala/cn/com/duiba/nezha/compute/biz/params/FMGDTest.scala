package cn.com.duiba.nezha.compute.biz.params

import cn.com.duiba.nezha.compute.mllib.util.SparseUtil
import org.apache.spark.mllib.linalg._

/**
 * Created by pc on 2017/8/17.
 */
object FMGDTest {
  def main(args: Array[String]): Unit = {


    val w0=0.1
    val w:Vector=  Vectors.sparse(5, Array(0,1,2), Array(1.0,2.0,3.0)).asInstanceOf[SparseVector].toDense
    val v:Matrix=  Matrices.sparse(5, 2, Array(0,2,5),  Array(1,4,0,3,4),  Array(0.2,0.4,0.3,0.5,0.6)).asInstanceOf[SparseMatrix].toDense

    val x=  Vectors.sparse(5, Array(0,1,2,3,4), Array(1.0,2.0,3.0,4.0,5.0)).asInstanceOf[SparseVector]

    println("w0="+w0)
    println("w="+w)
    println("v="+v)
    println("x="+x.toDense)

    // grad v
    val inter_1 = SparseUtil.dot_row(v, x)
    val grad_v_p1 = SparseUtil.dot_m(x, inter_1)

    val x_2= SparseUtil.multiply(x)

    val grad_v_p3 = SparseUtil.multiply(x_2,2,v)
    val grad_v_p4 = SparseUtil.subtraction(grad_v_p1, grad_v_p3)


    //


    println("grad_v_p1="+grad_v_p1.toDense)
    println("grad_v_p3="+grad_v_p3.toDense)
    println("grad_v_p4="+grad_v_p4.toDense)

  }
}

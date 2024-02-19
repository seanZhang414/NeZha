package cn.com.duiba.nezha.compute.biz.params

import cn.com.duiba.nezha.compute.common.enums.DateStyle
import cn.com.duiba.nezha.compute.common.util.DateUtil
import cn.com.duiba.nezha.compute.mllib.util.{SparseDate, SparseUtil}
import org.apache.spark.mllib.linalg._

/**
 * Created by pc on 2017/6/13.
 */
object SparseVectorTest {


  def main(args: Array[String]): Unit = {
    val vs1 = Vectors.sparse(10, Array(0), Array(9.0))

//    val vs2 = Vectors.sparse(3, Array(0, 1, 2), Array(1.0, 2.0, 3.0))
//    vs1.apply(2)
//
//    val vs3 = vs1
//    val vs4 = vs1.toDense
//
//    val vs5 = vs3.asInstanceOf[SparseVector]
//
//    println(vs5.size)
//    vs5.foreachActive((x, y) => {
//      println(x + "," + y)
//    })
//
//    vs4.foreachActive((x, y) => {
//      println(x + "," + y)
//    })
//
//    vs1.foreachActive((x, y) => {
//      println(x + "," + y)
//    })
//
//
//
//    val p1 = LabeledPoint(1.0, Vectors.dense(1.0, 0.0, 3.0))
//    val p2 = LabeledPoint(1.0, Vectors.sparse(4, Array(0, 1, 2, 3), Array(1.0, 2.0, 3.0, 4.0)))
//
//
//    val dm2 = Matrices.dense(3, 2, Array(1.0, 3.0, 5.0, 2.0, 4.0, 6.0))

//    dm2.
//
//    //    println(vs1(0)) //序号访问
//    //    println(vs2.toSparse)
//
//    vs1.toDense.toSparse
//
//
    val sparseMatrix = Matrices.sparse(10, 10, Array(0, 2, 3, 6, 6, 6, 6, 6, 6, 6, 6), Array(0, 2, 1, 0, 1, 2), Array(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)).asInstanceOf[SparseMatrix]
//    val denseMatrix = Matrices.dense(3, 3, Array(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 4.0, 1.0, 3.0))
//

//    denseMatrix.asInstanceOf[DenseMatrix].toSparse
//
//    val s = sparseMatrix
//    val sv = s.values
//
//    val sv2 = sv.map(i => i * i)
//
//
//    val s2 = Matrices.sparse(s.numRows, s.numCols, s.colPtrs, s.rowIndices, sv2).asInstanceOf[SparseMatrix]
//
//    println(s)
//    println(s2)
//    //    println(sparseMatrix)
//    //    println(denseMatrix)
//    //    println(sparseMatrix.multiply(vs3))
//    //    println(denseMatrix.transpose)
//    //    println(sparseMatrix.transpose)
//
//
//    System.out.println("start 1 time = " + DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS))
//    for (i <- 0 to 10) {
//      sparseMatrix.multiply(vs1)
//    }
//    System.out.println("start 2 time = " + DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS))
//    for (i <- 0 to 10) {
//      sparseMatrix.multiply(vs3)
//    }
//    System.out.println("start 3 time = " + DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS))
//    for (i <- 0 to 10) {
//      sparseMatrix.multiply(vs4)
//    }
//    System.out.println("start 4 time = " + DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS))
//
//    //
//
    val sv_1 = Vectors.sparse(1000, Array(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), Array(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)).asInstanceOf[SparseVector]
    val sv_2 = Vectors.sparse(1000, Array(0, 1, 2, 4), Array(4.0, 5.0, 6.0, 8.0)).asInstanceOf[SparseVector]

    val sv = SparseUtil.rand(10000, 0, 0.2)
    val sv2 = sv.toDense

    val m = SparseUtil.rand(10000, 6, 0, 0.1)

    val v_x = SparseUtil.multiply(sv, 6, m)

    val v_x2 = SparseUtil.multiply(SparseUtil.vector_copy(sv, 6),m)






    val training_data = SparseDate.generateData4(10000, 10000, 2, 42,0.995)
    training_data.length

    println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + s",SparseUtil.multiply start ")
    val ret2 =  training_data.map(f=>SparseUtil.dot(sv,f.x))
    ret2.length
    println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + s",SparseUtil.multiply end")

    println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + s",SparseUtil.multiply2 start ")
    val ret1 =  training_data.map(f=>SparseUtil.dot2(sv,f.x))
    ret1.length
    println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + s",SparseUtil.multiply2 end")

    println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + s",SparseUtil.multiply3 start ")
    val ret3 =  training_data.map(f=>SparseUtil.dot3(f.x,sv2))
    ret3.length
    println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + s",SparseUtil.multiply3 end")



    //
//
//    System.out.println("start 5 time = " + DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS))
//    for (i <- 0 to 10000) {
//      SparseUtil.dot(sv_1, sv_2)
//    }
//    System.out.println("start 6 time = " + DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS))
//
//        println("sv_1" + sv_1.toDense)
//        println("sv_2" + sv_2.toDense)
//    println("SparseUtil.v_dot(sv1,sv2)=" + SparseUtil.dot_v_sv(sv_1, sv_2))
//    println("SparseUtil.multiply=" + SparseUtil.multiply(sv_1, sv_2))
//    println("SparseUtil.multiply=" + SparseUtil.multiply2(sv_1, sv_2))
//
//
//    val arr = Array(1, 2, 3, 4, 5, 6)
//
//    val arr2 = arr.slice(3, 6)
//    println("SparseUtil.sub_array(arr,0,3)=" + arr2.foreach(println))
//
//
//    val s_m_00 = Matrices.sparse(3, 2,
//      Array(0, 2, 4),
//      Array(0, 2, 0, 1),
//      Array(1.0, 2.0, 3.0, 4.0))
//      .asInstanceOf[SparseMatrix]
//
//    val s_v_00 = Vectors.sparse(3, Array(0, 1, 2), Array(4.0, 5.0, 6.0)).asInstanceOf[SparseVector]
//
//    val s_v_01 = Vectors.sparse(3, Array(0, 1), Array(4.0, 5.0)).asInstanceOf[SparseVector]


//    val s_v_02 = Vectors.sparse(6, Array(), Array()).asInstanceOf[SparseVector]
//    val s_v_03 = Vectors.sparse(6, Array( 2, 4), Array(1.0, 3.0)).asInstanceOf[SparseVector]
//
//    val s_v_099 = Vectors.sparse(6, Array(), Array())
//    val sss= s_v_099
//    println("SparseUtil.dot_col(s_m_00.transpose,s_v_00)" + SparseUtil.dot_col(s_m_00.transpose, s_v_00))
//    println("SparseUtil.dot_row(s_m_00,s_v_00)" + SparseUtil.dot_row(s_m_00, s_v_00))
//    println("SparseUtil.v_dot(s_v_02,s_v_03)" + SparseUtil.dot(s_v_02, s_v_03))
//
//
//    println("SparseUtil.multiply(s_v_00)" + SparseUtil.multiply(s_v_00))
//    println("SparseUtil.multiply(s_v_00,s_v_00)" + SparseUtil.multiply(s_v_00, s_v_00))
//    println("SparseUtil.multiply(s_v_00,s_v_01)" + SparseUtil.multiply(s_v_00, s_v_01))
//    println("SparseUtil.multiply(s_m_00)" + SparseUtil.multiply(s_m_00))
//    println("SparseUtil.multiply(s_m_00,s_m_00)" + SparseUtil.multiply(s_m_00, s_m_00))

//    println("SparseUtil.reshape_copy(s_v_00,3)" + SparseUtil.vector_copy(s_v_00, 3))

//    println("s_v_02" + s_v_02.toDense)
//    println("s_v_03" + s_v_03.toDense)
//    println("SparseUtil.add(s_v_03, s_v_02)" + SparseUtil.add(s_v_03, s_v_02).toDense)
//
//    println("SparseUtil.add(s_m_00,s_m_00)" + SparseUtil.add(s_m_00, s_m_00))


//    println("SparseUtil.subtraction(s_v_03,s_v_02)" + SparseUtil.subtraction(s_v_03, s_v_02))
//
//    println("SparseUtil.subtraction(s_v_02,s_v_03)" + SparseUtil.subtraction(s_v_02, s_v_03))
//
//    println("SparseUtil.subtraction(s_v_02,s_v_02)" + SparseUtil.subtraction(s_v_02, s_v_02))
//
//    println("SparseUtil.subtraction(s_m_00,s_m_00)" + SparseUtil.subtraction(s_m_00, s_m_00))
//
//
//
//
//    val s_m_04 = Matrices.sparse(2, 3, Array(0, 2, 3, 5), Array(0, 1, 0, 0, 1), Array(9.0, 1.0, 2.0, 3.0, 1.0)).asInstanceOf[SparseMatrix]
//    print("s_m_04.toDense="+s_m_04.toDense)
//    print("SparseUtil.sum_row(s_m_04)="+SparseUtil.sum_row(s_m_04))


//    val s_m_05 = Matrices.sparse(3, 4, Array(0, 3, 5, 6, 7), Array(0, 1, 2, 0, 2, 1, 0), Array(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0)).asInstanceOf[SparseMatrix]
//    val s_m_06 = s_m_05.transpose
//
//    println("printlnMx(s_m_04)=" + println(SparseUtil.transposeClean(s_m_05)))
//    println("printlnMx(s_m_06)=" + println(SparseUtil.transposeClean(s_m_06)))
//
//    println("printlnMx(s_m_04)=" + printlnMx(SparseUtil.transposeClean(s_m_05)))
//    println("printlnMx(s_m_06)=" + printlnMx(SparseUtil.transposeClean(s_m_06)))
//
//
//
//    println("SparseUtil.dot(s_m_04,s_m_05)" + SparseUtil.dot(s_m_04, s_m_05))
//    for (i <- 0 to -2) {
//      println("ssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" + i)
//    }
//
//
//    val s_v_08 = Vectors.sparse(5, Array(1,3), Array(1.0,3.0)).asInstanceOf[SparseVector]
//
//    val s_v_09 = Vectors.sparse(6, Array(0,2,4), Array(1.0,2.0,4.0)).asInstanceOf[SparseVector]
//
//
//    println("SparseUtil.dot_m(s_v_08,s_v_09)=\n" + SparseUtil.dot_m(s_v_08,s_v_09).toDense)
//
//    val s_m_009 = Matrices.sparse(2, 3, Array(0, 2, 3, 5), Array(0, 1, 0, 0, 1), Array(1.0, 1.0, 2.0, 3.0, 1.0)).asInstanceOf[SparseMatrix]
//
//
//
////    println("SparseUtil.rand(3,1)" + SparseUtil.rand(3,1))
//    println("SparseUtil.rand(3,1,1)\n" + SparseUtil.rand(3,2,1).toDense)
  }


  def printlnMx(sm: SparseMatrix): Unit = {
    println(s"---------------")
    println(s"numCols=${sm.numCols},numRows=${sm.numRows}")
    println(s"colPtrs=" + sm.colPtrs.foreach(print))
    println(s"rowIndices=" + sm.rowIndices.foreach(print))
    println(s"values=" + sm.values.foreach(print))
    println(s"isTranspose=" + sm.isTransposed)
  }


}

package cn.com.duiba.nezha.compute.biz.optimizing

/**
 * Created by pc on 2016/11/23.
 */
object ParameterCombination {

  case class ALSBestParams(
                        numIterations: Int,
                        lambda: Double,
                        rank: Int,
                        testRmse:Double,
                        baseRmse:Double,
                        improvement:Double
                        )


}

package cn.com.duiba.nezha.compute.common.params

/**
 * Created by pc on 2016/11/22.
 */
object Params {

  case class ALSParams(
                        numIterations: Int = 15,
                        lambda: Double = 0.1,
                        rank: Int = 10,
                        numUserBlocks: Int = 2,
                        numProductBlocks: Int = 2,
                        implicitPrefs: Boolean = false,
                        reduceValue: Double = 2.5,
                        numRecommender: Int = 10,
                        input: String = null,
                        output: String = null,
                        sep: String = "::",
                        env: String = "opt",
                        saveType: String = "file:///"
                        )

  case class ALSOptParams(
                           sp: Array[Double] = Array(0.7, 0.2, 0.1),
                           ranks: List[Int] = List(5, 10),
                           lambdas: List[Double] = List(0.05, 0.1),
                           numiters: List[Int] = List(15),
                           implicitPrefs: Boolean = false
                           )


  case class LRCTRParams(
                          localRun: Boolean = false,
                          modelKeyId: String = "default",
                          partitionNums: Int = 100,
                          sep: String = ";",
                          env: String = "eva",
                          input: String = "",
                          inputTest: String = "",
                          inputTraining: String = ""
                          )

  case class ModelParams(
                          localRun: Boolean = false,
                          modelKeyId: String = "default",
                          partitionNums: Int = 100,
                          sep: String = ";",
                          env: String = "eva",
                          batchSize: Int = 50000,
                          input: String = "",
                          inputTest: String = "",
                          inputTraining: String = ""
                          )

  case class AdvertLogParams(
                              localRun: Boolean = false,
                              topic: String = null,
                              interval: Int = 5,
                              statType: Int = 0,
                              isTest: Boolean = false,
                              partitionNums: Int = 2
                              )


}
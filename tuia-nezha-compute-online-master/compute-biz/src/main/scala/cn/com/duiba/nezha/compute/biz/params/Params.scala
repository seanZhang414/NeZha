package cn.com.duiba.nezha.compute.biz.params

class Params {

}

case class PSModelParams(
                          isLocal: Boolean = false,
                          topic: String = null,
                          batchInterval: Int = 5,
                          featureModelId: String = null,
                          onLineModelId: String = null,
                          partNums: Int = 1,
                          isCtr: Boolean = true,
                          delay: Int = 120,
                          startTime: String = "2018-02-28 00:00:00",
                          stepSize: Int = 1,
                          isReplay: Boolean = false,
                          isSync: Boolean = false,
                          sampleRatio: Double = 1.0
                        )
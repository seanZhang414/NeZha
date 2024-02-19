package cn.com.duiba.nezha.compute.deploy

import cn.com.duiba.nezha.compute.biz.spark.PsModelTest
import cn.com.duiba.nezha.compute.biz.params.PSModelParams

import org.apache.log4j.{Level, Logger}
import org.slf4j.LoggerFactory
import scopt.OptionParser

object PSModelTestApp {

  Logger.getLogger("org").setLevel(Level.ERROR)

  var logger = LoggerFactory.getLogger(PSModelTestApp.getClass)

  def main(args: Array[String]) {

    val defaultParams = PSModelParams()


    // 输入参数设置
    val parser = new OptionParser[PSModelParams]("FTRLPSModelApp") {
      head("AdvertLogKafkaApp: an app for FTRL PS Model  ")
      opt[Boolean]("isLocal")
        .text(s"执行模式,本地或集群 , default: ${defaultParams.isLocal} (auto)")
        .action((x, c) => c.copy(isLocal = x))
      opt[String]("topic")
        .text(s"样本 Kafka Topic , default: ${defaultParams.topic} (auto)")
        .action((x, c) => c.copy(topic = x))
      opt[Int]("batchInterval")
        .text(s"批处理时间间隔 , default: ${defaultParams.batchInterval} (auto)")
        .action((x, c) => c.copy(batchInterval = x))
      opt[String]("onLineModelId")
        .text(s"线上模型ID , default: ${defaultParams.onLineModelId} (auto)")
        .action((x, c) => c.copy(onLineModelId = x))
      opt[String]("featureModelId")
        .text(s"本地模型ID , default: ${defaultParams.featureModelId} (auto)")

        .action((x, c) => c.copy(featureModelId = x))
      opt[Int]("partNums")
        .text(s"分区数 , default: ${defaultParams.partNums} (auto)")
        .action((x, c) => c.copy(partNums = x))
      opt[Boolean]("isCtr")
        .text(s"ctr or cvr , default: ${defaultParams.isCtr} (auto)")
        .action((x, c) => c.copy(isCtr = x))
      opt[Int]("delay")
        .text(s"延迟处理时间 , default: ${defaultParams.delay} (auto)")
        .action((x, c) => c.copy(delay = x))
      opt[String]("startTime")
        .text(s"数据开始时间 , default: ${defaultParams.startTime} (auto)")
        .action((x, c) => c.copy(startTime = x))
      opt[Int]("stepSize")
        .text(s"批处理时间步长，单位分钟 , default: ${defaultParams.stepSize} (auto)")
        .action((x, c) => c.copy(stepSize = x))
      opt[Boolean]("isReplay")
        .text(s"是否重演 , default: ${defaultParams.isReplay} (auto)")
        .action((x, c) => c.copy(isReplay = x))
      opt[Boolean]("isSync")
        .text(s"是否同步线上 , default: ${defaultParams.isSync} (auto)")
        .action((x, c) => c.copy(isReplay = x))

      note(
        """
          |For example, the following command runs this app on a off-line dataset:
          |
          | bin/spark-submit --class cn.com.duiba.nezha.compute.deploy.PSModelTestApp \
          |   *.jar \
          |   --isLocal  xx \
          |   --topic xx  \
          |   --batchInterval xx \
          |   --modelId xx \
          |   --partNums xx \
          |   --isCtr xx \
          |   --delay xx \
          |   --startTime xx \
          |   --stepSize xx \
          |   --isReplay xx
          |
        """.stripMargin)
    }


    // 启动
    parser.parse(args, defaultParams).map { params =>
      PsModelTest.run(params)
    } getOrElse {
      System.exit(1);
    }


  }

}

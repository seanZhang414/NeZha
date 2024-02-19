package cn.com.duiba.nezha.compute.deploy

import cn.com.duiba.nezha.compute.biz.app.ml.AdvertCTRLR
import cn.com.duiba.nezha.compute.common.params.Params
import Params.LRCTRParams
import cn.com.duiba.nezha.compute.common.params.Params
import org.apache.log4j.{Level, Logger}
import scopt.OptionParser


/**
 * Created by pc on 2016/12/14.
 */
object AdvertCTRLRApp {

//  Logger.getLogger("org").setLevel(Level.ERROR)


  def main(args: Array[String]) {

    val defaultParams = LRCTRParams()
    // 输入参数设置
    val parser = new OptionParser[LRCTRParams]("AdvertCTRLRApp") {
      head("AdvertCTRLRApp: an app for advert CTR Prediction by LR Model ")
      opt[Boolean]("localRun")
        .text(s"执行模式,本地或集群 , default: ${defaultParams.localRun} (auto)")
        .action((x, c) => c.copy(localRun = x))
      opt[String]("modelKeyId")
        .text(s"模型ID , default: ${defaultParams.modelKeyId} (auto)")
        .action((x, c) => c.copy(modelKeyId = x))
      opt[String]("sep")
        .text(s"输入数据分割符, default: ${defaultParams.sep}")
        .action((x, c) => c.copy(sep = x))
      opt[String]("env")
        .text(s"启动环境类型,eva:模型评估 pred:预测模型 , default: ${defaultParams.env}")
        .action((x, c) => c.copy(env = x))
      opt[Int]("partitionNums")
        .text(s"RDD分区数量, default: ${defaultParams.partitionNums}")
        .action((x, c) => c.copy(partitionNums = x))
      opt[String]("inputTest")
        .text(s"测试数据集路径 , default: ${defaultParams.inputTest}")
        .action((x, c) => c.copy(inputTest = x))
      opt[String]("inputTraining")
        .text(s"训练数据集路径 , default: ${defaultParams.inputTraining}")
        .action((x, c) => c.copy(inputTraining = x))

      note(
        """
          |For example, the following command runs this app on a synthetic dataset:
          |
          | bin/spark-submit --class cn.com.duiba.suanpan.app.ActivityTopicRcmdWithALS \
          |   etl/nezha/alg/spark/suanpan-*.jar \
          |   --modelKeyId  default \
          |   --sep ; \
          |   --env eva \
          |   ... \
          |   data/nezha/input/rating.data \
        """.stripMargin)
    }

    // 启动
    parser.parse(args, defaultParams).map { params =>
      AdvertCTRLR.run(params)
    } getOrElse {
      System.exit(1);
    }
  }


}

package cn.com.duiba.nezha.compute.biz.app.streaming

import cn.com.duiba.nezha.compute.common.params.Params
import Params.LRCTRParams
import cn.com.duiba.nezha.compute.common.params.Params
import org.apache.log4j.{Level, Logger}
import org.slf4j.LoggerFactory
import scopt.OptionParser


/**
  * Created by pc on 2016/12/14.
  */
object AdvertFeatureDirectKafka {

//   Logger.getLogger("org").setLevel(Level.ERROR)

   var logger = LoggerFactory.getLogger(AdvertFeatureDirectKafka.getClass)

   def main(args: Array[String]) {

     val defaultParams = LRCTRParams()
     // 输入参数设置
     val parser = new OptionParser[LRCTRParams]("AdvertFeatureDirectKafka") {
       head("AdvertFeatureDirectKafka: an app for advert feature by DirectKafka ")
       opt[String]("modelKeyId")
         .text(s"模型ID , default: ${defaultParams.modelKeyId} (auto)")
         .action((x, c) => c.copy(modelKeyId = x))
       opt[String]("sep")
         .text(s"输入数据分割符, default: ${defaultParams.sep}")
         .action((x, c) => c.copy(sep = x))
       opt[String]("env")
         .text(s"启动环境类型,eva:模型评估 pred:预测模型 , default: ${defaultParams.env}")
         .action((x, c) => c.copy(env = x))
       arg[String]("<input>")
         .required()
         .text("训练数据集路径 ")
         .action((x, c) => c.copy(input = x))
       note(
         """
           |For example, the following command runs this app on a synthetic dataset:
           |
           | bin/spark-submit --class cn.com.duiba.suanpan.app.ActivityTopicRcmdWithALS \
           |   etl/nezha/alg/spark/suanpan-*.jar \
           |   --featureIdxList  default \
           |   --featureIdxLocMap default \
           |   --sep ; \
           |   --env eva \
           |   ... \
           |   data/nezha/input/rating.data \
         """.stripMargin)
     }

     // 参数解析
     parser.parse(args, defaultParams).map { params =>
       run(params)
     } getOrElse {
       System.exit(1);
     }
   }

   def run(params: LRCTRParams): Unit = {


     }



 }

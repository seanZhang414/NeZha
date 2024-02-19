package cn.com.duiba.nezha.compute.deploy


import cn.com.duiba.nezha.compute.biz.app.streaming.{DirectKafkaTuiaAdvertLog4, DirectKafkaTuiaAdvertLog3, DirectKafkaTuiaAdvertLog}
import cn.com.duiba.nezha.compute.common.params.Params
import Params.AdvertLogParams
import org.apache.log4j.{Level, Logger}
import org.slf4j.LoggerFactory
import scopt.OptionParser


/**
 * Created by pc on 2016/12/14.
 */
object TuiaAdvertLogKafkaApp {
//
//  Logger.getLogger("org").setLevel(Level.ERROR)

  var logger = LoggerFactory.getLogger(AdvertCTRLRApp.getClass)

  def main(args: Array[String]) {

    val defaultParams = AdvertLogParams()

    // 输入参数设置
    val parser = new OptionParser[AdvertLogParams]("AdvertLogKafkaApp") {
      head("AdvertLogKafkaApp: an app for Advert Log Kafka ")
      opt[Boolean]("localRun")
        .text(s"执行模式,本地或集群 , default: ${defaultParams.localRun} (auto)")
        .action((x, c) => c.copy(localRun = x))
      opt[String]("topic")
        .text(s"模型ID , default: ${defaultParams.topic} (auto)")
        .action((x, c) => c.copy(topic = x))
      opt[Int]("interval")
        .text(s"批处理时间间隔 , default: ${defaultParams.interval} (auto)")
        .action((x, c) => c.copy(interval = x))
      opt[Int]("statType")
        .text(s"批处理时间间隔 , default: ${defaultParams.statType} (auto)")
        .action((x, c) => c.copy(statType = x))
      opt[Boolean]("isTest")
        .text(s"批处理时间间隔 , default: ${defaultParams.isTest} (auto)")
        .action((x, c) => c.copy(isTest = x))
      opt[Int]("partitionNums")
        .text(s"分区数 , default: ${defaultParams.partitionNums} (auto)")
        .action((x, c) => c.copy(partitionNums = x))
      note(
        """
          |For example, the following command runs this app on a synthetic dataset:
          |
          | bin/spark-submit --class cn.com.duiba.nezha.compute.deploy.TuiaAdvertLogKafkaApp \
          |   spark/*.jar \
          |   --localRun  true \
          |   --topic launch (launch,charge,landingpage,device_info_b,device_info_region,device_info_sp1,device_info_sp2,landing_page,device_user_link,,,)\
          |   --interval xx  default 5
          |   --statType 0
          |   --isTest false
          |   --partitionNums xx default 10
          |
        """.stripMargin)
    }

    // 启动
    parser.parse(args, defaultParams).map { params =>
      DirectKafkaTuiaAdvertLog4.run(params)
    } getOrElse {
      System.exit(1);
    }

  }


}

package cn.com.duiba.nezha.compute.deploy

import cn.com.duiba.nezha.compute.biz.app.init.LandingPageFeatureInit
import org.apache.log4j.{Level, Logger}
import org.slf4j.LoggerFactory


/**
 * Created by pc on 2016/12/14.
 */
object LogInitApp {

//  Logger.getLogger("org").setLevel(Level.ERROR)

  var logger = LoggerFactory.getLogger(AdvertCTRLRApp.getClass)

  def main(args: Array[String]) {

    val inputPath="hdfs://bigdata01:8020/user/lwj/data/input/ctr/sample/advert/init/f001/"
    LandingPageFeatureInit.run(inputPath)

  }


}

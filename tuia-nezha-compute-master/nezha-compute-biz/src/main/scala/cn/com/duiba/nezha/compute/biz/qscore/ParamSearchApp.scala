package cn.com.duiba.nezha.compute.common

import cn.com.duiba.nezha.compute.common.params.Params.ModelParams
import org.apache.log4j.{Level, Logger}
import scopt.OptionParser

/**
  * Created by jiali on 2017/11/7.
  */


package object ParamSearchApp {


  Logger.getLogger("org").setLevel(Level.ERROR)

  def main(args: Array[String]) {

    val defaultParams = ModelParams()
    // 输入参数设置
    val inputPath="hdfs://bigdata01:8020/user/lwj/data/input/ctr/sample/advert/init/f001/"
    ParamSearch.run(inputPath)

  }

}

package cn.com.duiba.nezha.compute.biz.app.ml

/**
 * Created by pc on 2016/11/21.
 */

import cn.com.duiba.nezha.compute.api.enums.RunEnvEnum
import cn.com.duiba.nezha.compute.biz.evaluate.ALSEvaluater
import cn.com.duiba.nezha.compute.biz.optimizing.ALSRcmdBestParam
import cn.com.duiba.nezha.compute.common.params.Params
import Params.{ALSOptParams, ALSParams}
import cn.com.duiba.nezha.compute.biz.predict.ALSPredicter
import cn.com.duiba.nezha.compute.biz.recommender.ALSRecommender
import cn.com.duiba.nezha.compute.biz.save.ALSSave
import cn.com.duiba.nezha.compute.common.params.Params
import cn.com.duiba.nezha.compute.common.util.{MyFileUtil, TextParser}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory
import scopt.OptionParser


/**
 * moivelens 电影推荐
 *
 */
object AppActivityTopicRcmdWithALS {
//  Logger.getLogger("org").setLevel(Level.ERROR)
  var logger = LoggerFactory.getLogger(ActivityTopicRcmdWithALS.getClass)

  def main(args: Array[String]) {

    val defaultParams = ALSParams()

    // 输入参数设置
    val parser = new OptionParser[ALSParams]("AppActivityTopicRcmdWithALS") {
      head("ActivityTopicRcmdWithALS: an app for ALS with nezha-engine data.")
      opt[Int]("rank")
        .text(s"随机因子,隐藏属性, default: ${defaultParams.rank}")
        .action((x, c) => c.copy(rank = x))
      opt[Int]("numIterations")
        .text(s"最大迭代次数, default: ${defaultParams.numIterations}")
        .action((x, c) => c.copy(numIterations = x))
      opt[Double]("lambda")
        .text(s"正则化参数, default: ${defaultParams.lambda}")
        .action((x, c) => c.copy(lambda = x))
      opt[Int]("numUserBlocks")
        .text(s"number of user blocks, default: ${defaultParams.numUserBlocks} (auto)")
        .action((x, c) => c.copy(numUserBlocks = x))
      opt[Int]("numProductBlocks")
        .text(s"number of product blocks, default: ${defaultParams.numProductBlocks} (auto)")
        .action((x, c) => c.copy(numProductBlocks = x))
      opt[Int]("numRecommender")
        .text(s"每个用户推荐的Top N 个数 , default: ${defaultParams.numRecommender} (auto)")
        .action((x, c) => c.copy(numRecommender = x))
      opt[Unit]("implicitPrefs")
        .text(s"分值敏感参数,是否开启分值阈值,default: ${defaultParams.implicitPrefs} (auto)")
        .action((_, c) => c.copy(implicitPrefs = true))
      opt[Double]("reduceValue")
        .text(s"分值敏感参数,分值阈值, default: ${defaultParams.reduceValue}")
        .action((x, c) => c.copy(reduceValue = x))
      opt[String]("sep")
        .text(s"输入数据分割符, default: ${defaultParams.sep}")
        .action((x, c) => c.copy(sep = x))
      opt[String]("env")
        .text(s"启动环境类型,opt:参数寻优 rcmd:推荐 , default: ${defaultParams.env}")
        .action((x, c) => c.copy(env = x))
      arg[String]("<input>")
        .required()
        .text("input paths, dataset of ratings")
        .action((x, c) => c.copy(input = x))
      arg[String]("<output>")
        .required()
        .text("output paths,dataset of rcmd result")
        .action((x, c) => c.copy(output = x))
      note(
        """
          |For example, the following command runs this app on a synthetic dataset:
          |
          | bin/spark-submit --class cn.com.duiba.suanpan.app.ActivityTopicRcmdWithALS \
          |   etl/nezha/alg/spark/suanpan-*.jar \
          |   --rank  10 \
          |   --numIterations \
          |   --lambda \
          |   ... \
          |   ... \
          |   data/nezha/input/rating.data \
          |   data/nezha/output/rcmd.ata
        """.stripMargin)
    }


    // 参数解析
    parser.parse(args, defaultParams).map { params =>
      run(params)
    } getOrElse {
      System.exit(1);
    }

  }

  def run(params: ALSParams) {

    // 本地运行模式，读取本地的spark主目录
    val conf = new SparkConf()
      .setAppName("AppActivityTopicRcmdWithALS")
      .setMaster("local[2]")
      .set("spark.executor,memory", "1024m")
      .set("", "")
    //      .setSparkHome("D:\\work\\hadoop_lib\\spark-   bin-hadoop  \\spark-   bin-hadoop")

    // 集群运行模式，读取spark集群的环境变量
    //var conf = new SparkConf().setAppName("ALS_Recommendation")

    val sc = new SparkContext(conf)

    // 加载数据
    println("loading data ... ")
    val data = sc.textFile(params.input)

    // 解析数据
    /**
     * *MovieLens ratings are on a scale of
     * Must see
     * Will enjoy
     * It's okay
     * Fairly bad
     * Awful
     */
    println("parse ratings ... ")


    val appRatings = TextParser.appRatingParse(data, params.implicitPrefs, params.sep, params.reduceValue).cache()

    val apps = appRatings.map(_._1).distinct()

    apps.collect().foreach(
      app => {

        val ratings = appRatings.filter(_._1 == app).map(_._2)
        println(s"---- app =$app ---- ")
        println(TextParser.ratingsDescPrint(ratings))

        // env
        if (params.env.equals(RunEnvEnum.ENV_OPT.getDesc)) {
          // 参数寻优
          println("search best params ... ")
          val bestParms = ALSRcmdBestParam.getBestParams(ratings, ALSOptParams())
          // 保存
          sc.parallelize(List(bestParms)).saveAsTextFile(MyFileUtil.getAppFilePath(params.saveType,params.output, "cn/com/duiba/nezha/compute/biz/param", app))

        } else {
          // 训练
          println("traing model ... ")
          val model = ALSRecommender.train(sc, ratings, params)

          // 评估
          println("evaluate model ... ")
          val mse = ALSEvaluater.evaluateMse(ratings, model, params.implicitPrefs)

          println(s"mse = $mse.")

          // 预测
          println("predict ... ")
          val users = ratings.map(_.user).distinct()
          val rcmd = ALSPredicter.predict(model, users, params.numRecommender)
          val ret = sc.parallelize(rcmd).map(r => r match {
            case (user, rcmdString) => (app, user, rcmdString)
          })
          // 保存预测结果
          ALSSave.appRcmdSaveHdfs(ret, MyFileUtil.getAppFilePath(params.saveType,params.output, "rcmd", app))

          // 保存模型
          model.save(sc, MyFileUtil.getAppFilePath(params.saveType,params.output, "model", app))
        }
      }
    )


    //clean up
    sc.stop()

  }


}


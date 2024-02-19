package cn.com.duiba.nezha.compute.biz.app.ml

import cn.com.duiba.nezha.compute.alg.LR
import cn.com.duiba.nezha.compute.api.dict.CategoryFeatureDict
import cn.com.duiba.nezha.compute.api.enums.SerializerEnum
import cn.com.duiba.nezha.compute.biz.util.SampleCategoryFeatureUtil
import cn.com.duiba.nezha.compute.common.util.{LabelUtil, MyStringUtil}

import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.evaluation.{BinaryClassificationMetrics, MulticlassMetrics}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkConf, SparkContext}
import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import org.apache.log4j.{Level, Logger}
;
/**
 * Created by pc on 2016/12/14.
 */
object AdvertLRTest {

  def main(args: Array[String]) {
    //屏蔽不必要的日志显示在终端上
//    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
//    Logger.getLogger("org.apache.eclipse.jetty.server").setLevel(Level.OFF)

    val conf = new SparkConf()
      .setAppName("AdvertLR1")
      .setMaster("local[2]")
    val sc = new SparkContext(conf)

    //    val text = sc.textFile("file:///D:\\usr\\data\\lrdata.txt")
    val text = sc.textFile("file:///D:\\usr\\data\\tmp_advert_alg_lr_origin_feature0103.txt")
    val data = text.sample(false, 0.1, 0).map(_.split(";",-1).toList).map(MyStringUtil.stringListStd).cache()
    print(data.first())



    val lr: LR = new LR()
    //    val dictUtil: CategoryFeatureDictUtil = new CategoryFeatureDictUtil()
    val dict: CategoryFeatureDict = new CategoryFeatureDict();
    //    val modelUtil: LogisticRegressionModelUtil = new LogisticRegressionModelUtil()


    //    val featureIdxList = List("f1", "f2", "f3", "f4", "f5", "f6", "f9", "f10", "f11", "f12", "f13", "f14")
    //    val featuerIdxLocMap = Map(
    //      "f1" -> 2, "f2" -> 3, "f3" -> 4, "f4" -> 5, "f5" -> 9, "f6" -> 10,
    //      "f9" -> 18, "f10" -> 19, "f11" -> 22, "f12" -> 23, "f13" -> 24, "f14" -> 25
    //    )
    val featureIdxList = List("f001", "f003")
    val featuerIdxLocMap = Map(
      "f001" -> 0,  "f003" -> 2)


    featureIdxList.map(featureIdx => {

      val f = SampleCategoryFeatureUtil.getFeature(data, featuerIdxLocMap(featureIdx),2)
      println("featureIdx = " + featureIdx + ",f=" + f)
      dict.setFeature(featureIdx, f.toList.asJava)
    })

    lr.setFeatureDict(dict)



    // 构造训练、测试数据集

    val parsedData = data.map(line => {
      val pbuf = new ListBuffer[String]
      featureIdxList.foreach(featureIdx => {
        pbuf.append(line(featuerIdxLocMap(featureIdx)).toLowerCase)
      })

      val value = lr.getDictUtil.oneHotDoubleEncode(featureIdxList.asJava, pbuf.toList.asJava).toList
      LabelUtil.getLabel(line(4))
      (line, LabeledPoint(LabelUtil.getLabel(line(4)), Vectors.dense(value.toArray)))

    })
    parsedData.take(1).foreach(System.out.println)






    val splits = parsedData.randomSplit(Array(0.8, 0.2), seed = 11L)

    val training = splits(0).map { case (line, sample) => sample }.cache()

    val test = splits(1).distinct().cache()


    // test sample 计数
    val testCounts = test.map {
      case (line, LabeledPoint(label, features)) =>
        (label, 1)
    }.reduceByKey(_ + _)
    testCounts.collect().foreach(println(_))

    // Run training algorithm to build the model
    val model = new LogisticRegressionWithLBFGS()
      .setNumClasses(2)
      .run(training)

    // Prediction with  model
    val predictionAndLabels2 = test.map { case (line, LabeledPoint(label, features)) =>
      val prediction = model.predict(features)
      (prediction, label)
    }.cache()
    // show Metrics
    val metrics2 = new MulticlassMetrics(predictionAndLabels2)
    println("Matrix = " + metrics2.confusionMatrix)
    println("metrics2.recall(0.0) = " + metrics2.recall(0.0))
    println("metrics2.recall(1.0) = " + metrics2.recall(1.0))
    println("metrics2.precision(0.0) = " + metrics2.precision(0.0))
    println("metrics2.precision(1.0) = " + metrics2.precision(1.0))


    // -------------------------------------------------------------------------------------
    model.clearThreshold

    //    modelUtil.setModel(model)
    //    lr.setFeatureDict(dict)
    lr.setModel(model)

    val model_str = lr.getModelUtil.getModelStr(model,SerializerEnum.JAVA_ORIGINAL)
    val model_ret = lr.getModelUtil.getModel(model_str,SerializerEnum.JAVA_ORIGINAL)
    lr.setModel(model_ret)
    lr.setFeatureIdxList(featureIdxList.asJava)


//    data.map(line => {
//      val pbuf = new ListBuffer[String]
//      featureIdxList.foreach(featureIdx => {
//        pbuf.append(line(featuerIdxLocMap(featureIdx)).toLowerCase)
//      })
//      val pre = lr.predict(pbuf.toList.asJava)
//      (LabelUtil.getLabel(line(4)), pre)
//    }).collect().map(x => {
//      println(x._1 + "-->" + x._2)
//    })



    // Compute raw scores on the test set
    val predictionAndLabels3 = test.map { case (line, LabeledPoint(label, features)) =>
      val prediction = lr.getModelUtil.predict(features)
      (line, prediction.doubleValue(), label)
    }.cache()
    predictionAndLabels3.collect().map(x => {
      println(x._1 + ":" + x._2 + "-->" + x._3)
    })

    val predictionAndLabels4 = predictionAndLabels3.map { case (line, pre, label) => (pre, label) }
    //使用了一个BinaryClassificationMetrics来评估
    val metrics = new BinaryClassificationMetrics(predictionAndLabels4)
    // Precision by threshold
    //这里列出了所有阈值的p,r,f值
    val precision = metrics.precisionByThreshold
    // Recall by threshold
    val recall = metrics.recallByThreshold
    val avaluate = precision.join(recall)
    //    avaluate.sortByKey().sample(false, 0.2, 0).foreach {
    //      case (t, (p, r)) => (
    //        println(s"Threshold: $t, Recall: $r, Precision: $p")
    //        )
    //    }


    // F-measure
    //the beta factor in F-Measure computation.
    //    val f1Score = metrics.fMeasureByThreshold
    //    f1Score.foreach { case (t, f) =>
    //      println(s"Threshold: $t, F-score: $f, Beta = 1")
    //    }
    //    val fScore = metrics.fMeasureByThreshold(0.5)
    //    fScore.foreach { case (t, f) =>
    //      println(s"Threshold: $t, F-score: $f, Beta = 0.5")
    //    }

    //     Precision-Recall Curve
    val PRC = metrics.pr
    // AUPRC 精度-召回曲线下的面积
    val auPRC = metrics.areaUnderPR
    println("Area under precision-recall curve = " + auPRC)
    // Compute thresholds used in ROC and PR curves
    val thresholds = precision.map(_._1)

    //     ROC Curve
    val roc = metrics.roc
    val auROC = metrics.areaUnderROC
    println("Area under ROC = " + auROC)

    //
    println("model.weights = " + model.weights)


  }
}

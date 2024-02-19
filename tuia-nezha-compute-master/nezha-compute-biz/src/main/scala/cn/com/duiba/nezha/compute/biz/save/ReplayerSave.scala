package cn.com.duiba.nezha.compute.biz.save

import cn.com.duiba.nezha.compute.api.point.Point.{ModelFeature, ModelReplayWithMap, ModelReplay}
import org.apache.spark.rdd.RDD

/**
 * Created by pc on 2017/8/8.
 */
object ReplayerSave {


  def save(data: RDD[ModelReplay], modelKey: String, dt: String): Unit = {

    val tableName = s"tmp.model_replay_${modelKey}_di"

    val hiveContext = new org.apache.spark.sql.hive.HiveContext(data.sparkContext)

    import hiveContext.implicits._
    data.toDF().registerTempTable("result_table")

    val sql_create_table = s"create table if not exists ${tableName}( " +
      s"order_id bigint ," +
      s"pctr double " +
      s" )partitioned by (dt string comment '') stored as orc "

    val sql_insert = s"insert into ${tableName} partition(dt='${dt}') select order_id,pre from result_table"

    println("sql_create_table="+sql_create_table)
    // 创建表
    hiveContext.sql(sql_create_table)

    println("sql_insert="+sql_insert)
    // 插入数据
    hiveContext.sql(sql_insert)


  }

  def saveMap(data: RDD[ModelReplayWithMap], modelKey: String, dt: String): Unit = {

    val tableName = s"tmp.model_replay_${modelKey}_di"

    val hiveContext = new org.apache.spark.sql.hive.HiveContext(data.sparkContext)

    import hiveContext.implicits._
    data.toDF().registerTempTable("result_table")

    val sql_create_table = s"create table if not exists ${tableName}( " +
      s"order_id bigint ," +
      s"featuremap string ," +
      s"pctr double " +
      s" )partitioned by (dt string comment '') stored as orc "

    val sql_insert = s"insert into ${tableName} partition(dt='${dt}') select order_id,featuremap,pre from result_table"

    println("sql_create_table="+sql_create_table)
    // 创建表
    hiveContext.sql(sql_create_table)

    println("sql_insert="+sql_insert)
    // 插入数据
    hiveContext.sql(sql_insert)

  }

  def saveFeature(data: RDD[ModelFeature], modelKey: String, dt: String): Unit = {

    val tableName = s"tmp.model_feature_${modelKey}_di"

    val hiveContext = new org.apache.spark.sql.hive.HiveContext(data.sparkContext)

    import hiveContext.implicits._
    data.toDF().registerTempTable("result_table")


    val sql_create_table = s"create table if not exists ${tableName}( " +
      s"feature_id string ," +
      s"category string ," +
      s"feature_category_size bigint ," +
      s"index bigint ," +
      s"sub_index bigint ," +
      s"intercept double ," +
      s"weight double ," +
      s"factor string " +
      s" )partitioned by (dt string comment '') stored as orc "

    val sql_insert = s"insert into ${tableName} partition(dt='${dt}') select feature_id,category,feature_category_size,index,sub_index,intercept,weight,factor from result_table"

    println("sql_create_table="+sql_create_table)
    // 创建表
    hiveContext.sql(sql_create_table)

    println("sql_insert="+sql_insert)
    // 插入数据
    hiveContext.sql(sql_insert)


  }
}

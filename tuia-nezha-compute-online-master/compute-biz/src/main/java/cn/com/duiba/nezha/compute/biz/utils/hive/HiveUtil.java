package cn.com.duiba.nezha.compute.biz.utils.hive;


import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.sql.*;

public class HiveUtil {


    public static RDD<Row> selectTableWithDt(String tableName, String dt, SparkSession sc) {
        RDD<Row> ret = null;

        try {
            String sql = "select * from " + tableName + " where dt=" + dt + ";";
            Dataset<Row> resultsDF = sc.sql(sql);
            ret = resultsDF.rdd();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;

    }


}

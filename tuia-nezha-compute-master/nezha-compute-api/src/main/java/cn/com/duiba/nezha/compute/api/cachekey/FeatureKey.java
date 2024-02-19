package cn.com.duiba.nezha.compute.api.cachekey;

/**
 * Created by pc on 2017/2/17.
 */
public class FeatureKey {

    public static final String HBASE_COF_PROFIX = "nz_cof_"; //

    public static final String HBASE_CI_PROFIX = "nz_cf_"; //
    public static final String HBASE_DI_PROFIX = "nz_df_"; //

    public static final String REDIS_PROFIX = "nzh_e_cof_"; //

    public static final String REDIS_CF_PROFIX = "nzh_e_cf_"; //


    public static String getConsumerOrderFeatureRowKey(String consumerId, String activityId) {

        return HBASE_COF_PROFIX + consumerId + "_" + activityId + "_";
    }


    public static String getConsumerInfoMongoDbKey(String consumerId) {

//        return REDIS_CF_PROFIX + consumerId + "_" ;
        return consumerId;
    }



    public static String getConsumerInfoRowKey(String consumerId) {

        return HBASE_CI_PROFIX + consumerId + "_" ;
    }

    public static String getDeviceInfoRowKey(String deviceId) {

        return HBASE_DI_PROFIX + deviceId + "_" ;
    }




    public static String getConsumerOrderFeatureRedisKey(String consumerId, String activityId) {

        return REDIS_PROFIX + consumerId + "_" + activityId + "_";
    }

    public static String getConsumerOrderFeatureRedisDateKey(String consumerId, String activityId, String date) {

        return REDIS_PROFIX + consumerId + "_" + activityId + "_" + date + "_";
    }
    public static String getConsumerOrderFeatureESKey(String consumerId) {

        return REDIS_PROFIX + consumerId+"_";
    }


}

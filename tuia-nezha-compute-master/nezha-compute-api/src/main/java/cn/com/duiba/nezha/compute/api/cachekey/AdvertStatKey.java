package cn.com.duiba.nezha.compute.api.cachekey;


/**
 * Created by pc on 2017/2/17.
 */
public class AdvertStatKey {

    public static final String HBASE_PROFIX = "nz_ad_stat_"; //

    public static final String STAT_PROFIX = "nz_ad_stat_"; //

    public static final String CHEAK_PROFIX = "nz_ad_cheak_"; //

    public static final String CTR_STAT_PROFIX = "nz_ad_ctr_stat_"; //

    public static final String STAT_DIM_PROFIX = "nz_ad_stat_dim_"; //

    public static final String STAT_SUB_DIM_PROFIX = "nz_ad_s_s_d_"; //

//    public static String getAdvertStatRowKey(String advertId,
//                                             String materialId,
//                                             String advertType,
//                                             String advertStatDimType,
//                                             String statDimId) {
//
//
//
//
//        String ret = HBASE_PROFIX + advertId + "_" +
//                advertType + "_" +
//                advertStatDimType + "_" +
//                statDimId + "_";
//
//        if (materialId != null) {
//            ret = HBASE_PROFIX + advertId + "_" +
//                    advertType + "_" +
//                    advertStatDimType + "_" +
//                    statDimId + "_" + materialId;
//        }
//        return ret;
//    }

    public static String getAdvertStatRowKey(String advertId,
                                             String materialId,
                                             Long advertTimes,
                                             String advertStatDimType,
                                             String statDimId) {


        if (advertTimes == null) {
            advertTimes = 1L;
        } else if (advertTimes >= 10) {
            advertTimes = 10L;
        }

        String ret = HBASE_PROFIX + advertId + "_" +
                advertTimes + "_" +
                advertStatDimType + "_" +
                statDimId + "_";

        if (materialId != null) {
            ret = HBASE_PROFIX + advertId + "_" +
                    advertTimes + "_" +
                    advertStatDimType + "_" +
                    statDimId + "_" + materialId;
        }
        return ret;
    }


    public static String getAdvertStatStatusKey() {

        String ret = CHEAK_PROFIX + "_stat_update_time_";
        return ret;
    }


    public static String getAdvertStatESKey(String advertId,
                                            String advertType,
                                            String advertStatDimType,
                                            String statDimId) {

        return STAT_PROFIX + advertId + "_" +
                advertType + "_" +
                advertStatDimType + "_" +
                statDimId + "_";
    }

    public static String getAdvertCtrStatESKey(String advertId,
                                               String advertType,
                                               String advertStatDimType,
                                               String statDimId,
                                               String statIntervalId) {

        return CTR_STAT_PROFIX + advertId + "_" +
                advertType + "_" +
                advertStatDimType + "_" +
                statDimId + "_" +
                statIntervalId + "_";


    }

    public static String getAdvertStatRedisKey(String statDimId) {
        return STAT_DIM_PROFIX + statDimId + "_";
    }


    public static String getAdvertStatMongoDbKey(String advertId, String materialId, String appId, Long times) {

        if (times == null) {
            times = 1L;
        } else if (times >= 10) {
            times = 10L;
        }
        return STAT_DIM_PROFIX + appId + "_" + advertId + "_" + materialId + "_" + times;
    }


    public static String getAdvertStatMongoDbId(String advertId, String materialId, String appId, String timeinterval) {

        return STAT_SUB_DIM_PROFIX + appId + "_" + advertId + "_" + materialId + "_" + timeinterval;
    }


    public static String getAdvertStatMongoDbKey(String advertId, String appId, Long times) {

        if (times == null) {
            times = 1L;
        } else if (times >= 10) {
            times = 10L;
        }

        return getAdvertStatMongoDbKey(advertId, null, appId, times);
    }


    // appId_advertId_day_hour
    // day 20170905  hour 08



    public static String getAdvertSubStatMongoDbKey(String advertId, String appId, String timeinterval) {

        return getAdvertStatMongoDbId(advertId, null, appId, timeinterval);
    }

}

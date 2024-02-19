package cn.com.duiba.nezha.compute.api.constant;

/**
 * Created by pc on 2016/11/21.
 */
public class GlobalConstant {

    public static String KAFKA_BROKERS = "nezha.compute.kafka.brokers";
    public static String KAFKA_TOPICS_TUIA_LAUNCH_LOG = "nezha.compute.kafka.topics.tuia.launch.log";
    public static String KAFKA_TOPICS_TUIA_CHARGE_LOG = "nezha.compute.kafka.topics.tuia.consume.log";

    public static String ES_CLUSTER_NAME = "nezha.compute.es.cluster.name";
    public static String ES_CLUSTER_HOST = "nezha.compute.es.cluster.nodes";
    public static String ES_CLUSTER_POST = "nezha.compute.es.cluster.post";

    //es
    public static String AD_STAT_ES_INDEX = "nezha";
    public static String AD_STAT_ES_TYPE = "ad_stat";
    public static String AD_CTR_STAT_ES_TYPE = "ad_ctr";

    // mongodb
    public static String MATERIAL_STAT_COLLECTION_NAME = "material_stat";
    public static String ADVERT_STAT_COLLECTION_NAME = "ad_ctr";
    public static String ADVERT_INTERVAL_STAT_COLLECTION_NAME = "hourly_data";

    public static String NEZHA_STAT_COLLECTION_NAME = "nezha_stat";

    //es
    public static String CONSUMER_FEATURE_ES_INDEX = "nezha";
    public static String CONSUMER_FEATURE_ES_TYPE = "consumer_feature";

    //es
    public static String LR_MODEL_ES_INDEX = "nezha";
    public static String LR_MODEL_ES_TYPE = "lr_model";

    //es
    public static String NZ_FEATURE_ES_INDEX = "nzhfeature";
    public static String NZ_FEATURE_ES_TYPE = "feature";


    public static String AD_GLOBAL = "global";
    public static String MD_CAT_INDEX = "createdAt";

    public static double DOUBLE_ZERO =0.000000000001;
    public static double EPSILON =0.00000001;
    public static double IE6 =0.000001 ;

}

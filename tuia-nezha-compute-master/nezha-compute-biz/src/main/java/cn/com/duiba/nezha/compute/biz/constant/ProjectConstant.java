package cn.com.duiba.nezha.compute.biz.constant;

/**
 * Created by pc on 2016/11/21.
 */
public class ProjectConstant {
    public static final String CONFIG_PATH = "/application.properties";
    public static String MYBATIS_CONFIG_PATH = "mybatis/mybatis-config.xml";
//    public static final String LOG4J_CONFIG_PATH = "log/properties";


    public static String KAFKA_BROKERS = "nezha.compute.kafka.brokers";
    public static String KAFKA_TOPICS_TUIA_LAUNCH_LOG = "nezha.compute.kafka.topics.tuia.launch.log";
    public static String KAFKA_TOPICS_TUIA_CHARGE_LOG = "nezha.compute.kafka.topics.tuia.consume.log";
    public static String KAFKA_TOPICS_TUIA_LANDING_PAGE_LOG = "nezha.compute.kafka.topics.tuia.landingpage.log";


    public static String ES_CLUSTER_NAME = "nezha.compute.es.cluster.name";
    public static String ES_CLUSTER_HOST = "nezha.compute.es.cluster.nodes";
    public static String ES_CLUSTER_POST = "nezha.compute.es.cluster.post";

    public static String ES_CLUSTER_NAME_2 = "nezha.compute.es.cluster2.name";
    public static String ES_CLUSTER_HOST_2 = "nezha.compute.es.cluster2.nodes";
    public static String ES_CLUSTER_POST_2 = "nezha.compute.es.cluster2.post";



    public static String ZK_LIST = "nezha.compute.zk.list";

    public static String REDIS_IP = "nezha.compute.redis.ip";
    public static String REDIS_PW = "nezha.compute.redis.password";
    public static String REDIS_PORT = "nezha.compute.redis.port";



    // 广告日志类型

    public static Long TUIA_ADVERT_LAUNCH = 1L;
    public static Long TUIA_ADVERT_CHARGE = 2L;
    public static Long TUIA_ADVERT_LANDING_PAGE = 3L;

    // hbase
    public static String HBASE_TN_CONSUMER_ORDER_FEATURE = "consumerOrderFeature";
    public static String CONSUMER_ORDER_FEATURE_FM_INFO = "info";
    public static String CONSUMER_ORDER_FEATURE_FM_RANK = "rank";
    public static String CONSUMER_ORDER_FEATURE_FM_CHARGE = "charge";
    public static String CONSUMER_ORDER_FEATURE_FM_LAST_ORDER = "last_order";
    public static String CONSUMER_ORDER_FEATURE_FM_LAST_ACTIVITY = "last_activity";


    public static int REDIS_PORT_DEAFULT = 6379;
    public static int MAX_IDLE = 20;
    public static int MIN_IDLE = 5;
    public static int MAX_TOTAL = 20;

    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    public static int MAX_WAIT_MILLIS = 5000;

    public static int RETRY_NUM = 3;
    public static int TIME_OUT = 5000;

    public static Boolean TEST_ON_BORROW = false;
    public static Boolean TEST_ON_RETURN = false;


    // redis expire
    public static int YEAR_1_EXPIRE = (60*60*24)*365;
    public static int MONTH_3_EXPIRE = (60*60*24)*30*3;
    public static int MONTH_1_EXPIRE = (60*60*24)*30;
    public static int WEEK_1_EXPIRE = (60*60*24)*7;
    public static int WEEK_2_EXPIRE = (60*60*24)*7*2;
    public static int DAY_2_EXPIRE = (60*60*24)*2;
    public static int DAY_1_EXPIRE = (60*60*24)*1;
    public static int HOUR_1_EXPIRE = (60*60)*1;
    public static int MINUTE_1_EXPIRE = 60*1;
    public static int MINUTE_20_EXPIRE = 60*20;



    // kafka producer conf
    public static String P_BROKER_LIST ="";
    public static String P_ZK_LIST ="";




    // mongodb
    public static String MD_HOSTS ="nezha.compute.mongo.hosts";
    public static String MD_PORT ="nezha.compute.mongo.port";
    public static String MD_PW = "nezha.compute.mongo.password";
    public static String MD_USER_NAME ="nezha.compute.mongo.username";
    public static String MD_DB ="nezha.compute.mongo.db";
    public static String MD_REPL_SET_NAME="nezha.compute.mongo.db.replsetname";
    public static String MD_W_HOST ="nezha.compute.mongo.web.host";
    public static String MD_W_KEY="nezha.compute.mongo.web.key";

}

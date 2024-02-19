package cn.com.duiba.nezha.compute.biz.constant;

public class PsConstant {
    public static final String CONFIG_PATH = "/application.properties";
    public static String KAFKA_BROKERS = "nezha.compute.kafka.brokers";
    public static String ZK_LIST = "nezha.compute.zk.list";


    // Hbase Constant

    public static String TABLE_NAME_SAMPLE = "tuia-loadpage-advert";
    public static String FAMILY_SAMPLE = "cf";


    public static String TABLE_NAME_ORDER_LIST = "odpcadvert-orderlist";
    public static String FAMILY_ORDER_LIST = "cf";

    public static String TABLE_NAME_BASE_INFO = "ps_base_info";
    public static String FAMILY_BASE_INFO = "base_info";

    public static String TABLE_NAME_MODEL = "ps_model";
    public static String FAMILY_MODEL = "model";
    public static String COL_MODEL = "c_model";



    // Model Sava Mongodb
    public static String MODEL_TYPE = "lr_model";
}

package cn.com.duiba.nezha.compute.biz.conf;

import cn.com.duiba.nezha.compute.biz.constant.ProjectConstant;
import cn.com.duiba.nezha.compute.biz.utils.conf.ConfigFactory;

public class KafkaConf {
    public static String brokers;
    public static String zk;

    static {
        try {
            initConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static void initConfig() throws Exception {
        brokers = ConfigFactory.getInstance().getConfigProperties(ProjectConstant.CONFIG_PATH).getProperty(ProjectConstant.KAFKA_BROKERS);
        zk = ConfigFactory.getInstance()
                .getConfigProperties(ProjectConstant.CONFIG_PATH).getProperty(ProjectConstant.ZK_LIST);
    }
}

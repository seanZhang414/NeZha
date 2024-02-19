package cn.com.duiba.nezha.compute.biz.conf;

import cn.com.duiba.nezha.compute.biz.constant.ProjectConstant;
import cn.com.duiba.nezha.compute.biz.utils.conf.ConfigFactory;


/**
 * Created by pc on 2016/11/18.
 */
public class MongoDbConf {

//    public static MongoConfig config;

    public static String whost;
    public static String wkey;


    static {
        try {
            initESConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void initESConfig() throws Exception {
//        config = new MongoConfig();
//
//        String hosts = ConfigFactory.getInstance()
//                .getConfigProperties(ProjectConstant.CONFIG_PATH).getProperty(ProjectConstant.MD_HOSTS);
//
//        int port = ConfigFactory.getInstance()
//                .getConfigProperties(ProjectConstant.CONFIG_PATH).getInt(ProjectConstant.MD_PORT, 3717);
//
//        String db = ConfigFactory.getInstance()
//                .getConfigProperties(ProjectConstant.CONFIG_PATH).getProperty(ProjectConstant.MD_DB);
//
//        String password = ConfigFactory.getInstance()
//                .getConfigProperties(ProjectConstant.CONFIG_PATH).getProperty(ProjectConstant.MD_PW);
//
//        String username = ConfigFactory.getInstance()
//                .getConfigProperties(ProjectConstant.CONFIG_PATH).getProperty(ProjectConstant.MD_USER_NAME);
//
//        String replsetname = ConfigFactory.getInstance()
//                .getConfigProperties(ProjectConstant.CONFIG_PATH).getProperty(ProjectConstant.MD_REPL_SET_NAME);
//
//        config.setIp(hosts);
//        config.setPort(port);
//        config.setDatabaseName(db);
//        config.setPassWord(password);
//        config.setUserName(username);
//        config.setReplSetName(replsetname);

        whost = ConfigFactory.getInstance()
                .getConfigProperties(ProjectConstant.CONFIG_PATH).getProperty(ProjectConstant.MD_W_HOST);

        wkey = ConfigFactory.getInstance()
                .getConfigProperties(ProjectConstant.CONFIG_PATH).getProperty(ProjectConstant.MD_W_KEY);

    }


}

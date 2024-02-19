package cn.com.duiba.nezha.compute.biz.conf;

import cn.com.duiba.nezha.compute.biz.constant.ProjectConstant;
import cn.com.duiba.nezha.compute.biz.utils.jedis.JedisConfig;
import cn.com.duiba.nezha.compute.common.util.conf.ConfigFactory;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by pc on 2016/11/18.
 */
public class JedisPoolConf {


    public static JedisPoolConfig poolConfig;

    public static JedisConfig jedisConfig1;

    static {
        try {
            initJedisPoolConfig();
            initJedisConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void initJedisPoolConfig() {
        poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(ProjectConstant.MAX_IDLE);
        poolConfig.setMinIdle(ProjectConstant.MIN_IDLE);
        poolConfig.setMaxTotal(ProjectConstant.MAX_TOTAL);
        poolConfig.setMaxWaitMillis(ProjectConstant.MAX_WAIT_MILLIS);
        poolConfig.setTestOnBorrow(ProjectConstant.TEST_ON_BORROW);
        poolConfig.setTestOnReturn(ProjectConstant.TEST_ON_RETURN);
    }

    private static void initJedisConfig() throws Exception {

        String ip = ConfigFactory.getInstance().getConfigProperties(ProjectConstant.CONFIG_PATH).getProperty(ProjectConstant.REDIS_IP);
        String passWord = ConfigFactory.getInstance().getConfigProperties(ProjectConstant.CONFIG_PATH).getProperty(ProjectConstant.REDIS_PW);
        int port = ConfigFactory.getInstance().getConfigProperties(ProjectConstant.CONFIG_PATH).getInt(ProjectConstant.REDIS_PORT, ProjectConstant.REDIS_PORT_DEAFULT);

        jedisConfig1 = new JedisConfig();
        jedisConfig1.setIp(ip);
        jedisConfig1.setPassWord(passWord);
        jedisConfig1.setPort(port);

    }


}

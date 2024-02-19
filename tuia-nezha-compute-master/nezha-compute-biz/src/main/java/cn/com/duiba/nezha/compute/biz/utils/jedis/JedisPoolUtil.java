package cn.com.duiba.nezha.compute.biz.utils.jedis;

import cn.com.duiba.nezha.compute.biz.conf.JedisPoolConf;
import cn.com.duiba.nezha.compute.biz.constant.ProjectConstant;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pc on 2016/11/18.
 */
public class JedisPoolUtil
{

    private JedisPoolUtil(){}

    private static class RedisUtilHolder{
        private static final JedisPoolUtil instance = new JedisPoolUtil();
    }

    public static JedisPoolUtil getInstance(){
        return RedisUtilHolder.instance;
    }

    private static Map<String,JedisPool> maps = new HashMap<String,JedisPool>();

    public static JedisPool getPool(JedisConfig jedisConfig){

        JedisPool pool = null;
        int timeOut = ProjectConstant.TIME_OUT;

        String ip = jedisConfig.getIp();
        int port = jedisConfig.getPort();
        String passWord = jedisConfig.getPassWord();

        String key = ip+":"+port;
        if(!maps.containsKey(key))
        {
            JedisPoolConfig config = JedisPoolConf.poolConfig;
            pool = new JedisPool(config,ip,port,timeOut,passWord);
            maps.put(key, pool);
            System.out.println("put new pool");
        }
        else
        {
            pool = maps.get(key);
        }
        return pool;
    }

    public Jedis getJedis(JedisConfig jedisConfig)
    {
        Jedis jedis = null;

        int count = 0;
        do
        {
            try
            {
                jedis = getPool(jedisConfig).getResource();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                getPool(jedisConfig);
            }
        }
        while(jedis == null && count< ProjectConstant.RETRY_NUM);
        return jedis;
    }

}



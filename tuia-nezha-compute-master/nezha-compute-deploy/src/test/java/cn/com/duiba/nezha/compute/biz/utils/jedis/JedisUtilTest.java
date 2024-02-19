package cn.com.duiba.nezha.compute.biz.utils.jedis;

import cn.com.duiba.nezha.compute.biz.conf.JedisPoolConf;
import cn.com.duiba.nezha.compute.common.enums.DateStyle;
import cn.com.duiba.nezha.compute.common.util.DateUtil;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pc on 2017/5/16.
 */
public class JedisUtilTest extends TestCase {
    public static JedisUtil jedisUtil = new JedisUtil(JedisPoolConf.jedisConfig1);

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testGetJedis() throws Exception {

//        JedisPoolConfig poolConfig = new JedisPoolConfig();
//        // 最大连接数
//        poolConfig.setMaxTotal(1);
//        // 最大空闲数
//        poolConfig.setMaxIdle(1);
//        // 最大允许等待时间，如果超过这个时间还未获取到连接，则会报JedisException异常：
//        // Could not get a resource from the pool
//        poolConfig.setMaxWaitMillis(1000);
//        Set<HostAndPort> nodes = new LinkedHashSet<HostAndPort>();
//        nodes.add(new HostAndPort("114.55.59.218", 6300));
//
//        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
//        JedisCluster cluster = new JedisCluster(nodes, poolConfig);
//        String name = cluster.get("name");
//        System.out.println(name);
//        cluster.set("age", "18");
//        System.out.println(cluster.get("age"));


    }

    public void testRemove() throws Exception {

    }

    public void testRemove1() throws Exception {

    }

    public void testExists() throws Exception {

    }

    public void testGetObject() throws Exception {

    }

    public void testGet() throws Exception {
        System.out.println("nz_ad_stat_dim_26744_8447="+jedisUtil.get("nz_ad_stat_dim_26744_8447"));


    }

    public void testSetObject() throws Exception {

    }

    public void testSet() throws Exception {

    }

    public void testSetexObject() throws Exception {

    }

    public void testSetex() throws Exception {

    }

    public void testGetList() throws Exception {

    }

    public void testSetexList() throws Exception {

    }

    public void testMultiSetex() throws Exception {
        Map<String,String> map = new HashMap<>();

        for(int i=0;i<10;i++){
            map.put("test_k_"+i,"test_v_i");

        }
        jedisUtil.multiSetex(map,100);

        for(int i=0;i<10;i++){
            System.out.println("test_k_"+i+"="+jedisUtil.get("nz_ad_ctr_stat_6491_1_2_26744_r7d_"));

        }

        System.out.println("sdsda"+DateUtil.addDay(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS), 7));

    }

    public void testHmSet() throws Exception {

    }

    public void testHSetex() throws Exception {

    }

    public void testHgetAll() throws Exception {
        System.out.println("="+jedisUtil.hgetAll("nz_ad_stat_dim_11955_"));

    }

    public void testExpire() throws Exception {

    }

    public void testHmsetexPipelined() throws Exception {

    }

    public void testHsetexPipelined() throws Exception {

    }

    public void testReturnResource() throws Exception {

    }
}
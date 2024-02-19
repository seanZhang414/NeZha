package cn.com.duiba.nezha.compute.biz.bo;

import junit.framework.TestCase;

/**
 * Created by pc on 2017/2/24.
 */
public class ConsumerOrderFeatureParseTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
        System.out.println(Math.ceil((1000) / (12132)));
    }

    public void tearDown() throws Exception {

    }

    public void testSyncRedisHSet() throws Exception {
        String value = null;
        int seconds = 5;
//
//        JedisUtil jedisCient = new JedisUtil(JedisPoolConf.jedisConfig1);
////
////        System.out.println("setex" + jedisCient.setex("nz-test-010", value, seconds));
////        System.out.println("exists" + jedisCient.exists("nz-test-010"));
//
//        Map<String,String> map = new HashMap<>();
//        map.put("test_01", "val_01");
//        map.put("test_02", "dssd");
////        jedisCient.hmSet("nz-test-012", map);
//        System.out.println("exists" + jedisCient.exists("nz-test-012"));
//        jedisCient.expire("nz-test-012", seconds);
//
//        Thread.sleep(30);
//        System.out.println("exists" + jedisCient.exists("nz-test-012"));



    }
}
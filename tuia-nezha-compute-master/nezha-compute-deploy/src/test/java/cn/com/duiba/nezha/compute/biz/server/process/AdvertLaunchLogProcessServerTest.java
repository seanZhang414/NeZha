package cn.com.duiba.nezha.compute.biz.server.process;

import cn.com.duiba.nezha.compute.biz.log.AdvertOrderLog;
import cn.com.duiba.nezha.compute.biz.vo.MapVo;
import junit.framework.TestCase;

/**
 * Created by pc on 2017/5/15.
 */
public class AdvertLaunchLogProcessServerTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testLogProcess() throws Exception {
        AdvertOrderLog advertOrderLog = new AdvertOrderLog();
        advertOrderLog.setAdvertId("6491");
        advertOrderLog.setAppId("26744");
        advertOrderLog.setActivityId("670");
        advertOrderLog.setConsumerId("1223324545");
        advertOrderLog.setCurrentTime("2017-05-15 10:10:10");
        advertOrderLog.setGmtDate("2017-05-15");
        advertOrderLog.setTime("2017-05-15 10:10:05");
        advertOrderLog.setOrderId("123456789");



        MapVo vo = new MapVo();
        vo.setTimes(1L);
        advertOrderLog.setLogExtMap(vo);
//        OrderLogResultVo retVo = AdvertLaunchLogProcessServer.getInstance().logProcess(advertOrderLog,params);
//        AdvertLaunchLogProcessServer.getInstance().run2(retVo,"test");
//        System.out.println("ret="+ JSONObject.toJSONString(retVo));

    }
}
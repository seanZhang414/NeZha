package cn.com.duiba.nezha.compute.biz.server;

import cn.com.duiba.nezha.compute.biz.constant.ProjectConstant;
import cn.com.duiba.nezha.compute.biz.constant.htable.AdvertStatConstant;
import cn.com.duiba.nezha.compute.biz.log.AdvertOrderLog;
import cn.com.duiba.nezha.compute.biz.server.biz.AdvertStatUpdateServer;
import cn.com.duiba.nezha.compute.biz.vo.AdvertStatVo;
import cn.com.duiba.nezha.compute.biz.vo.MapVo;
import cn.com.duiba.nezha.compute.common.params.Params;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;

/**
 * Created by pc on 2017/3/31.
 */
public class AdvertStatUpdateServerTest extends TestCase {

    AdvertOrderLog advertOrderLog;
    public void setUp() throws Exception {
        super.setUp();
        advertOrderLog = new AdvertOrderLog();
        advertOrderLog.setAdvertId("7012");
        advertOrderLog.setAppId("26926");
        advertOrderLog.setActivityId("670");
        advertOrderLog.setConsumerId("1223324545");
        advertOrderLog.setCurrentTime("2017-08-08 10:10:10");
        advertOrderLog.setGmtDate("2017-08-08");
        advertOrderLog.setTime("2017-08-08 10:10:05");
        advertOrderLog.setOrderId("123456789");



        MapVo vo = new MapVo();
        vo.setTimes(1L);
        advertOrderLog.setLogExtMap(vo);
    }

    public void tearDown() throws Exception {

    }

    public void testSyncES() throws Exception {

    }

    public void testLaunchLogProcess() throws Exception {
        Params.AdvertLogParams params = new Params.AdvertLogParams( false,null, 5,0,false,10);
        for(int i=0;i<2;i++){
            advertOrderLog.setType(ProjectConstant.TUIA_ADVERT_LAUNCH);

            AdvertStatVo advertStatVo = AdvertStatUpdateServer.logProcess(advertOrderLog, AdvertStatConstant.FM_LAUNCH,params);
            AdvertStatUpdateServer.syncES(advertStatVo, params.topic());
            System.out.println("advertStatVo"+ JSONObject.toJSONString(advertStatVo));
        }

    }

    public void testChargeLogProcess() throws Exception {
        for(int i=0;i<2;i++) {
            advertOrderLog.setType(ProjectConstant.TUIA_ADVERT_CHARGE);
//            AdvertStatVo advertStatVo = AdvertStatUpdateServer.logProcess(advertOrderLog, AdvertStatConstant.FM_CHARGE,2);
//            AdvertStatUpdateServer.syncES(advertStatVo);
        }
    }

    public void testLaunchSubProcess() throws Exception {

    }

    public void testChargeSubProcess() throws Exception {

    }

    public void testPrepareSyncDate() throws Exception {

    }

    public void testGetWeightCtr() throws Exception {

    }

    public void testGetAdvertType() throws Exception {

    }

    public void testGetAdvertType1() throws Exception {

    }

    public void testGetLastDateMap() throws Exception {

    }

    public void testAddLongCnt() throws Exception {

    }
}
package cn.com.duiba.nezha.compute.biz.bo;

import cn.com.duiba.nezha.compute.api.vo.AdvertStatStatusVo;
import cn.com.duiba.nezha.compute.biz.constant.htable.AdvertStatConstant;
import cn.com.duiba.nezha.compute.common.enums.DateStyle;
import cn.com.duiba.nezha.compute.common.util.DateUtil;
import junit.framework.TestCase;

/**
 * Created by pc on 2017/7/21.
 */
public class StatCheckBoTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testUpdateTime() throws Exception {
        StatCheckBo.setGmtTime(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS));
        StatCheckBo.updateTime(AdvertStatConstant.FM_LAUNCH);
//        StatCheckBo.updateTime(AdvertStatConstant.FM_CHARGE);
//        StatCheckBo.updateTime(AdvertStatConstant.FM_ACT_CLICK);
//        StatCheckBo.updateTime(AdvertStatConstant.FM_ACT_EXP);

    }

    public void testGetStatStatus() throws Exception {
//        AdvertStatStatusVo advertStatStatusVo = StatCheckBo.getStatTime();
//        boolean ret = StatCheckBo.getStatDelayStatus(advertStatStatusVo);
//        System.out.println("ret = "+ret);
    }

    public void testGetThresholdStatus() throws Exception {
//        boolean ret = StatCheckBo.getGreateThanThreshold("2017-07-21 18:00:00",10);
//        System.out.println("ret = "+ret);
    }

    public void testGetStatTime() throws Exception {

//        AdvertStatStatusVo advertStatStatusVo = StatCheckBo.getStatTime();
    }

    public void testGetDelayMinutes() throws Exception {

        //
        Integer ret = StatCheckBo.getDelayMinutes("2017-07-27 10:23:20", "2017-07-27 10:15:30");

        System.out.println("ret = "+ret);
    }

    public void testGetStatDelayStatus() throws Exception {

    }

    public void testGetWarnStatus() throws Exception {

        System.out.println("StatCheckBo.getWarnStatus(10,10,10,10)="+StatCheckBo.getWarnStatus(5,0,0,4,3));

    }

    public void testGetDataStopStatus() throws Exception {

    }

    public void testGetStatDelayStatusOld() throws Exception {

    }
}
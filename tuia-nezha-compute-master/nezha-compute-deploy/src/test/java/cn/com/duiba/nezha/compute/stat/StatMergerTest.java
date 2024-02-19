package cn.com.duiba.nezha.compute.stat;

import cn.com.duiba.nezha.compute.stat.dto.StatDto;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pc on 2017/12/11.
 */
public class StatMergerTest extends TestCase {


    public void setUp() throws Exception {
        super.setUp();



    }

    public void tearDown() throws Exception {

    }

    public void testDimsMerge() throws Exception {
//
//        StatDto d1 = new StatDto();
//        d1.setLaunchCnt(1000L);
//        d1.setChargeCnt(250L);
//        d1.setActClickCnt(0L);
//        d1.setActExpCnt(250L);
//
//        StatDto d2 = new StatDto();
//        d2.setLaunchCnt(1000L);
//        d2.setChargeCnt(250L);
//        d2.setActClickCnt(0L);
//        d2.setActExpCnt(250L);
//
//
//        StatDto d3 = new StatDto();
//        d3.setLaunchCnt(1000L);
//        d3.setChargeCnt(250L);
//        d3.setActClickCnt(0L);
//        d3.setActExpCnt(250L);
//
//
//
//        StatDto ret3 = StatMerger.dimsMerge(d1,d2,d3,d3);
//
//        System.out.println("testDimsMerge ret3 = " + JSON.toJSONString(ret3));


    }

    public void testDimMerge() throws Exception {
//        StatDto d1 = new StatDto();
//        d1.setLaunchCnt(1000L);
//        d1.setChargeCnt(250L);
//        d1.setActClickCnt(0L);
//        d1.setActExpCnt(250L);
//
//
//        StatDto ret2 = StatMerger.dimMerge(null, d1);
//
//        System.out.println("testDimMerge ret2 = " + JSON.toJSONString(ret2));

    }

    public void testIntervalMerge() throws Exception {


        try {
            Map<Long, StatDto> advertStatDtoHourMap = new HashMap<>();
            Map<Long, StatDto> advertStatDtoDayMap = new HashMap<>();

//            StatDto h1 = new StatDto();
//            h1.setLaunchCnt(10L);
//            h1.setChargeCnt(100L);
//            h1.setActClickCnt(1L);
//            h1.setActExpCnt(250L);
//
//            StatDto h2 = new StatDto();
//            h2.setLaunchCnt(11L);
//            h2.setChargeCnt(100L);
//            h2.setActClickCnt(1L);
//            h2.setActExpCnt(251L);

            StatDto d2 = new StatDto();
            d2.setLaunchCnt(46757L);
            d2.setChargeCnt(23013L);
            d2.setActClickCnt(14912L);
            d2.setActExpCnt(14912L);

            StatDto d1 = new StatDto();
            d1.setLaunchCnt(74069L);
            d1.setChargeCnt(45170L);
            d1.setActClickCnt(29244L);
            d1.setActExpCnt(29244L);

//            StatDto d1 = new StatDto();
//            d1.setLaunchCnt(20L);
//            d1.setChargeCnt(100L);
//            d1.setActClickCnt(1L);
//            d1.setActExpCnt(1251L);

//            advertStatDtoHourMap.put(0L, h1);
//            advertStatDtoHourMap.put(1L, h2);

            advertStatDtoDayMap.put(2L, d1);
            advertStatDtoDayMap.put(1L, d2);
            StatDto ret1 = StatMerger.intervalMerge(advertStatDtoHourMap, advertStatDtoDayMap);
            

            System.out.println("testIntervalMerge  ret1 = " + JSON.toJSONString(ret1));


        } catch (Exception e) {

        }

    }

    public void testMergeWeight() throws Exception {

    }
}
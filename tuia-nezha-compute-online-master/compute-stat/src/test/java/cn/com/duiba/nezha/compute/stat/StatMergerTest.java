package cn.com.duiba.nezha.compute.stat;

import cn.com.duiba.nezha.compute.stat.dto.StatDto;
import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class StatMergerTest {

    @Test
    public void dimsMerge() {
    }

    @Test
    public void dimMerge() {
    }

    @Test
    public void intervalMerge() {

        try {
            Map<Long, StatDto> advertStatDtoHourMap = new HashMap<>();
            Map<Long, StatDto> advertStatDtoDayMap = new HashMap<>();

//            StatDto h1 = new StatDto();
//            h1.setLaunchCnt(1000L);
//            h1.setChargeCnt(250L);
//            h1.setActClickCnt(0L);
//            h1.setActExpCnt(250L);
//
//            StatDto h2 = new StatDto();
//            h2.setLaunchCnt(1100L);
//            h2.setChargeCnt(280L);
//            h2.setActClickCnt(0L);
//            h2.setActExpCnt(251L);

            StatDto d2 = new StatDto();
            d2.setLaunchCnt(91427L);
            d2.setChargeCnt(12578L);
            d2.setActClickCnt(1618L);
            d2.setActExpCnt(15143L);

//            StatDto d1 = new StatDto();
//            d1.setLaunchCnt(12001L);
//            d1.setChargeCnt(1301L);
//            d1.setActClickCnt(0L);
//            d1.setActExpCnt(1251L);
//
//            advertStatDtoHourMap.put(0L, h1);
//            advertStatDtoHourMap.put(1L, h2);

//            advertStatDtoDayMap.put(0L, d1);
            advertStatDtoDayMap.put(1L, d2);
            StatDto ret1 = StatMerger.intervalMerge(advertStatDtoHourMap, advertStatDtoDayMap);

            System.out.println("testIntervalMerge  ret1 = " + JSON.toJSONString(ret1));


        } catch (Exception e) {

        }


    }

    @Test
    public void mergeWeight() {
    }
}
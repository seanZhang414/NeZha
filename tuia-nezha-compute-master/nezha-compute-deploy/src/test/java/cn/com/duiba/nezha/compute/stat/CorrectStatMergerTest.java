package cn.com.duiba.nezha.compute.stat;

import cn.com.duiba.nezha.compute.stat.dto.CorrectStatDto;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pc on 2017/12/11.
 */
public class CorrectStatMergerTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testDimMerge() throws Exception {
        CorrectStatDto d2 = new CorrectStatDto();
        d2.setCtrLaunchCnt(2100L);
        d2.setCvrLaunchCnt(2000L);
        d2.setPreCtrAcc(600.0);
        d2.setPreCvrAcc(60.0);
        d2.setStatCtrAcc(600.0);
        d2.setStatCvrAcc(60.0);
        d2.setPreCtrAvg(0.3);
        d2.setPreCvrAvg(0.03);
        d2.setStatCtrAvg(0.3);
        d2.setStatCvrAvg(0.03);

        CorrectStatDto ret2 = CorrectStatMerger.dimMerge(d2, d2);

        System.out.println("testDimMerge  ret2 = " + JSON.toJSONString(ret2));
    }

    public void testIntervalMerge() throws Exception {
        Map<Long, CorrectStatDto> advertStatDtoHourMap = new HashMap<>();
        Map<Long, CorrectStatDto> advertStatDtoDayMap = new HashMap<>();

        CorrectStatDto h1 = new CorrectStatDto();
        h1.setCtrLaunchCnt(1000L);
        h1.setCvrLaunchCnt(1000L);
        h1.setPreCtrAcc(300.0);
        h1.setPreCvrAcc(30.0);
        h1.setStatCtrAcc(300.0);
        h1.setStatCvrAcc(30.0);


        CorrectStatDto h2 = new CorrectStatDto();
        h2.setCtrLaunchCnt(900L);
        h2.setCvrLaunchCnt(800L);
        h2.setPreCtrAcc(250.0);
        h2.setPreCvrAcc(25.0);
        h2.setStatCtrAcc(260.0);
        h2.setStatCvrAcc(26.0);

        CorrectStatDto d1 = new CorrectStatDto();
        d1.setCtrLaunchCnt(1000L);
        d1.setCvrLaunchCnt(1000L);
        d1.setPreCtrAcc(300.0);
        d1.setPreCvrAcc(30.0);
        d1.setStatCtrAcc(300.0);
        d1.setStatCvrAcc(30.0);

        CorrectStatDto d2 = new CorrectStatDto();
        d2.setCtrLaunchCnt(2000L);
        d2.setCvrLaunchCnt(2000L);
        d2.setPreCtrAcc(600.0);
        d2.setPreCvrAcc(60.0);
        d2.setStatCtrAcc(600.0);
        d2.setStatCvrAcc(60.0);

        advertStatDtoHourMap.put(0L, h1);
        advertStatDtoHourMap.put(1L, h2);

        advertStatDtoDayMap.put(0L, d1);
        advertStatDtoDayMap.put(1L, d2);
        advertStatDtoDayMap.put(5L, d2);
        CorrectStatDto ret1 = CorrectStatMerger.intervalMerge(advertStatDtoHourMap, advertStatDtoDayMap);

        System.out.println("testIntervalMerge  ret1 = " + JSON.toJSONString(ret1));

    }
}
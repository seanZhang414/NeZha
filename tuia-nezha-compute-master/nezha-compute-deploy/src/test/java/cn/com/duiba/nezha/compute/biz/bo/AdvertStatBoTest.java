package cn.com.duiba.nezha.compute.biz.bo;

import cn.com.duiba.nezha.compute.api.dto.AdvertCtrStatDto;
import cn.com.duiba.nezha.compute.api.enums.AdvertStatDimTypeEnum;
import cn.com.duiba.nezha.compute.api.enums.StatIntervalTypeEnum;
import cn.com.duiba.nezha.compute.common.enums.DateStyle;
import cn.com.duiba.nezha.compute.common.util.DateUtil;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.apache.commons.collections.map.HashedMap;

import java.util.Map;

/**
 * Created by pc on 2017/3/13.
 */
public class AdvertStatBoTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testSyncRedisHSet() throws Exception {

//
//
//
//        AdvertStatDto advertStatDto = new AdvertStatDto();
//        advertStatDto.setAdvertId("test_01");
////        advertStatDto.setClickCnt(1001L);
//        advertStatDto.setStatIntervalId(AdvertStatConstant.COL_GLOBAL);
//        advertStatDto.setLaunchCnt(1000L);
//        advertStatDto.setChargeCnt(400L);
//
//        String advertId = advertStatDto.getAdvertId();
//        String statDimId = advertStatDto.getStatDimId();
//        String statIntervalId = advertStatDto.getStatIntervalId();
//
//
//
//        String hashKey = AdvertStatKey.getAdvertStatRedisKey(advertId, statDimId, statIntervalId);
//
//        Map<String, String> map1 = ObjectDynamicCreator.getFieldVlaue(advertStatDto, null);
//        System.out.println("map=" + JSON.toJSONString(map1));
//        AdvertStatDto advertStatDto2 = JSON.parseObject(JSON.toJSONString(map1), AdvertStatDto.class);
//        System.out.println("advertStatDto2=" + JSON.toJSONString(advertStatDto2));
//        AdvertStatBo.syncRedisHSet(Arrays.asList(advertStatDto), true);
//        Map<String, String>  ret = AdvertStatBo.jedisCient.hgetAll(hashKey);
//        System.out.println("ret=" + JSON.toJSONString(ret));
//        AdvertStatDto advertStatDto3 = JSON.parseObject(JSON.toJSONString(ret), AdvertStatDto.class);
//        System.out.println("advertStatDto3=" + JSON.toJSONString(advertStatDto3));
    }

    public void testUpdateCtr() throws Exception {
//        AdvertStatBo.jedisUtil.setex("test_01", "11", 20);
//
//        System.out.println("get=" + AdvertStatBo.jedisUtil.get("test_01"));


//        "nz_ad_stat_9219_1_3_global_";
    }

//    public void testIncrementCount() throws Exception {
//        String tableName = AdvertStatConstant.TABLE_NAME;
//        HbaseUtil hbaseUtil = HbaseUtil.getInstance();
////        AdvertStatBo.incrementCount("t01","all",AdvertStatConstant.FM_CHARGE, "td1");
//
//    }

    public void testGetAdvertStatDto() throws Exception {

        Map<Long, String> dateMap = getLastDateMap("2017-07-19 10:10:20 001", StatIntervalTypeEnum.RECENT_7_DAY.getIndex());
        System.out.println("dateMap = "+JSON.toJSONString(dateMap));


        String advert_id="7012";
        Map<Long, AdvertCtrStatDto> advertCtrStatDtoMap = AdvertStatBo.getAdvertCtrStatDto(advert_id, null,
                1L,
                AdvertStatDimTypeEnum.APP,
                "26926",
                dateMap);

        System.out.println("print = "+JSON.toJSONString(advertCtrStatDtoMap));



        Map<Long, AdvertCtrStatDto> advertCtrStatDtoMap2 = AdvertStatBo.getAdvertCtrStatDto(advert_id,null,
                1L,
                AdvertStatDimTypeEnum.GLOBAL,
                "global",
                dateMap);

        System.out.println("print2 = "+JSON.toJSONString(advertCtrStatDtoMap2));


    }

    public void testGetCtr() throws Exception {

    }

    public static Map<Long, String> getLastDateMap(String currentDate, int interval) {

        Map<Long, String> dateMap = new HashedMap();
        try {
            for (int i = 0; i < interval + 1; i++) {
                String tmpDate = DateUtil.getDaySubTimeString(currentDate, DateStyle.YYYY_MM_DD_HH_MM_SS, DateStyle.YYYY_MM_DD, i * -1);
                if (tmpDate != null && !tmpDate.equals("2017-07-11")) {
                    dateMap.put((long) i, tmpDate);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateMap;
    }
}
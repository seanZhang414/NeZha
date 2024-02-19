package cn.com.duiba.nezha.compute.biz.check;

import cn.com.duiba.nezha.compute.api.cachekey.NezhaStatKey;
import cn.com.duiba.nezha.compute.api.constant.GlobalConstant;
import cn.com.duiba.nezha.compute.api.dto.AdvertCtrStatDto;
import cn.com.duiba.nezha.compute.api.enums.AdvertStatDimTypeEnum;
import cn.com.duiba.nezha.compute.api.enums.StatIntervalTypeEnum;
import cn.com.duiba.nezha.compute.biz.bo.AdvertStatBo;
import cn.com.duiba.nezha.compute.biz.utils.mongodb.MongoUtil;
import cn.com.duiba.nezha.compute.common.enums.DateStyle;
import cn.com.duiba.nezha.compute.common.util.DateUtil;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;

import java.util.Map;

/**
 * Created by pc on 2017/3/13.
 */
public class AdvertStatCheckTest extends TestCase {

    public String advert_id;
    public String app_id;

    public void setUp() throws Exception {
        super.setUp();

        advert_id="12104";
        app_id ="28641";

    }

    public void tearDown() throws Exception {

    }



    /**
     * Hbase存储数据读取，广告每日统计数据
     *
     * @throws Exception
     */
    public void testGetHbaseAdvertStat() throws Exception {
        Map<Long, String> hourMap = DateUtil.getHourSubTimeStringMap(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS), DateStyle.YYYY_MM_DD_HH_MM_SS, DateStyle.YYYY_MM_DD_HH, StatIntervalTypeEnum.RECENT_2_HOUR.getIndex());
        System.out.println("hourMap = "+JSON.toJSONString(hourMap));

        Map<Long, AdvertCtrStatDto> appAdvertCtrStatDtoMap = AdvertStatBo.getAdvertCtrStatDto(advert_id, null,
                1L,
                AdvertStatDimTypeEnum.APP,
                app_id,
                hourMap);

        // 0表示当日,1表示前一日,依此类推
        System.out.println("JSON.toJSONString(appAdvertCtrStatDtoMap)= "+JSON.toJSONString(appAdvertCtrStatDtoMap));



        Map<Long, AdvertCtrStatDto> globalAdvertCtrStatDtoMap = AdvertStatBo.getAdvertCtrStatDto(advert_id,null,
                2L,
                AdvertStatDimTypeEnum.GLOBAL,
                "global",
                hourMap);

        System.out.println("globalAdvertCtrStatDtoMap = "+JSON.toJSONString(globalAdvertCtrStatDtoMap));


    }


//    /**
//     * 数据回流数据，mongodb线上读取
//     *
//     * @throws Exception
//     */
//    public void testGetMongodbAdvertStat() throws Exception {
//
//        String docKey = AdvertStatKey.getAdvertStatMongoDbKey(advert_id,  app_id,null);
//
//        String mongoStatDto = MongoUtil.getMongoDb().findById(GlobalConstant.AD_CTR_STAT_ES_TYPE, docKey);
//        System.out.println("mongoStatDto ,with key=" + docKey + ",value = " + mongoStatDto);
//
//
//    }





    /**
     * Hbase存储数据读取，广告每日统计数据
     *
     * @throws Exception
     */
    public void testGetHbaseHourAdvertStat() throws Exception {
//        Map<Long, String> dateMap = getLastDateMap(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS), StatIntervalTypeEnum.RECENT_7_DAY.getIndex());
//        System.out.println("dateMap = "+JSON.toJSONString(dateMap));
//
//        Map<Long, AdvertCtrStatDto> appAdvertCtrStatDtoMap = AdvertStatBo.getAdvertCtrStatDto(advert_id, null,
//                1L,
//                AdvertStatDimTypeEnum.APP,
//                app_id,
//                dateMap);
//
//        // 0表示当日,1表示前一日,依此类推
//        System.out.println("JSON.toJSONString(appAdvertCtrStatDtoMap)= "+JSON.toJSONString(appAdvertCtrStatDtoMap));
//
//
//
//        Map<Long, AdvertCtrStatDto> globalAdvertCtrStatDtoMap = AdvertStatBo.getAdvertCtrStatDto(advert_id,null,
//                1L,
//                AdvertStatDimTypeEnum.GLOBAL,
//                "global",
//                dateMap);
//
//        System.out.println("globalAdvertCtrStatDtoMap = "+JSON.toJSONString(globalAdvertCtrStatDtoMap));


    }


    /**
     * 数据回流数据，mongodb线上读取
     *
     * @throws Exception
     */
    public void testGetMongodbAdvertHourStat() throws Exception {


//        String docKey = AdvertStatKey.getAdvertSubStatMongoDbKey("7260","22762", "2017091213");
//
//        String mongoStatDto = MongoUtil.getMongoDb().findById(GlobalConstant.ADVERT_INTERVAL_STAT_COLLECTION_NAME, docKey);
//        System.out.println("mongoStatDto ,with key=" + docKey + ",value = " + mongoStatDto);
//

//        System.out.println(DateUtil.getDate(DateUtil.getCurrentTime()));
//
//        String s1="\\n";
//        String s2 ="\n";
//        String ss1 =SampleCategoryFeatureUtil.toLowerCase(s1);
//        String ss2 =SampleCategoryFeatureUtil.toLowerCase(s2);
//        System.out.println( ss1.equals("\\n")+","+ ss2.equals("\n"));

    }

    /**
     * 数据回流数据，mongodb线上读取
     *
     * @throws Exception
     */
    public void testGetMongodbNezhaStat() throws Exception {


        String docKey = NezhaStatKey.getNezhaStatMongoDbKey(206L,12104L, 30837L);

        String mongoStatDto = MongoUtil.getMongoDb().findById( GlobalConstant.NEZHA_STAT_COLLECTION_NAME, docKey);
        System.out.println("mongoStatDto ,with key=" + docKey + ",value = " + mongoStatDto);


    }



}
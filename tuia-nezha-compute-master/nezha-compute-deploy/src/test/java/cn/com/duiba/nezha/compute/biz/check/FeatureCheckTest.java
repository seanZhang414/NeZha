package cn.com.duiba.nezha.compute.biz.check;

import cn.com.duiba.nezha.compute.api.cachekey.FeatureKey;
import cn.com.duiba.nezha.compute.api.constant.GlobalConstant;
import cn.com.duiba.nezha.compute.biz.utils.mongodb.MongoUtil;
import cn.com.duiba.nezha.compute.common.enums.DateStyle;
import cn.com.duiba.nezha.compute.common.util.DateUtil;
import junit.framework.TestCase;

/**
 * Created by pc on 2017/3/13.
 */
public class FeatureCheckTest extends TestCase {

    public String consumerId;
    public String activityDimId;
    public String gmtDate;

    public void setUp() throws Exception {
        super.setUp();

        consumerId="1767558617";
        activityDimId =null;
        gmtDate="2017-08-17";

    }

    public void tearDown() throws Exception {

    }


    /**
     * 数据回流数据，mongodb线上读取
     *
     * @throws Exception
     */
    public void testGetMongodbFeature() throws Exception {



        String mainKey = FeatureKey.getConsumerOrderFeatureRedisKey(
                consumerId,
                activityDimId);

        String dateKey = FeatureKey.getConsumerOrderFeatureRedisDateKey(
                consumerId,
                activityDimId,
                gmtDate);
        String mainKeyDto = MongoUtil.getMongoDb().findById(GlobalConstant.CONSUMER_FEATURE_ES_TYPE, mainKey);
        String dateKeyDto = MongoUtil.getMongoDb().findById(GlobalConstant.CONSUMER_FEATURE_ES_TYPE, dateKey);
        System.out.println("mainKeyDto ,with key=" + mainKey + ",value = " + mainKeyDto);

        System.out.println("dateKeyDto ,with key=" + dateKey + ",value = " + dateKeyDto);
        System.out.println("DateUtil.getHour(DateUtil.getCurrentTime(), DateStyle.YYYY_MM_DD_HH_MM_SS) = " + DateUtil.getHour(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS), DateStyle.YYYY_MM_DD_HH_MM_SS));


    }

}
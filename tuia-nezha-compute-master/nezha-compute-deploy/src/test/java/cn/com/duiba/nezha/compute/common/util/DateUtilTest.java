package cn.com.duiba.nezha.compute.common.util;

import cn.com.duiba.nezha.compute.alg.util.CategoryFeatureDictUtil;
import cn.com.duiba.nezha.compute.api.enums.SerializerEnum;
import cn.com.duiba.nezha.compute.common.enums.DateStyle;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

/**
 * Created by pc on 2017/5/24.
 */
public class DateUtilTest extends TestCase {

    public void testGetIntervalMinutes() throws Exception {
//        System.out.println(""+DateUtil.getIntervalMinutes("2017-05-06 12:30:10 123","2017-05-06 12:10:20 123",true));
        System.out.println(""+DateUtil.getHour("2017-05-06 12:30:10 123", DateStyle.YYYY_MM_DD_HH_MM_SS_SSS));
        System.out.println(""+DateUtil.getWeekNumber("2017-05-06 12:30:10 123", DateStyle.YYYY_MM_DD_HH_MM_SS_SSS));

        CategoryFeatureDictUtil util = new CategoryFeatureDictUtil();
        String featureIdxListStr =  util.featureIdxList2Str(Arrays.asList("f001"), SerializerEnum.JAVA_ORIGINAL);
        List<String> ret = util.getFeatureIdxList(featureIdxListStr,SerializerEnum.JAVA_ORIGINAL);
        System.out.println(""+ JSONObject.toJSONString(ret));
    }
}
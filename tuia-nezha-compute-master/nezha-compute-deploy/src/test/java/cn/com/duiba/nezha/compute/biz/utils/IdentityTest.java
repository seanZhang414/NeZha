package cn.com.duiba.nezha.compute.biz.utils;

import cn.com.duiba.nezha.compute.api.cachekey.FeatureKey;
import cn.com.duiba.nezha.compute.api.dto.AdvertAppStatDto;
import cn.com.duiba.nezha.compute.api.dto.AdvertCtrStatDto;
import cn.com.duiba.nezha.compute.biz.vo.FeatureSyncVo;
import cn.com.duiba.nezha.compute.common.enums.DateStyle;
import cn.com.duiba.nezha.compute.common.util.AssertUtil;
import cn.com.duiba.nezha.compute.common.util.DateUtil;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by pc on 2017/4/18.
 */
public class IdentityTest extends TestCase {

    String idCardId = "152224197306265348";

    public void testGetAge() throws Exception {
//        System.out.println(Identity.getAge(idCardId));

    }

    public void testGetAddressCode() throws Exception {
//        System.out.println(Identity.getRandomIDCard(true));
    }

    public void testGetSex() throws Exception {
//        System.out.println(Identity.getSex(idCardId));
//
//        System.out.println(Identity.getIDOrder(idCardId, true));
//
//        String srt= DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS);

    }

    public void test0919() throws Exception {
        AdvertCtrStatDto sdto = new AdvertCtrStatDto();




        AdvertAppStatDto dto = new AdvertAppStatDto();
        sdto.setAdvertId("a001");
        dto.setAppChStat(sdto);

        AdvertAppStatDto dto2 = new AdvertAppStatDto();
        sdto.setAdvertId("a002");
        dto2.setAppChStat(sdto);

        System.out.println("dto"+JSONObject.toJSONString(dto));
        System.out.println("dto2"+JSONObject.toJSONString(dto2));

    }
}
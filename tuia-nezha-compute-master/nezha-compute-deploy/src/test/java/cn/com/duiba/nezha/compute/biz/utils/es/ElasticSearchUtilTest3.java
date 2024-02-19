package cn.com.duiba.nezha.compute.biz.utils.es;


import cn.com.duiba.nezha.compute.api.cachekey.ModelKey;
import cn.com.duiba.nezha.compute.api.dto.AdvertModelEntity;
import cn.com.duiba.nezha.compute.api.dto.ConsumerDeviceFeatureDto;
import cn.com.duiba.nezha.compute.api.enums.ModelKeyEnum;
import cn.com.duiba.nezha.compute.api.enums.SerializerEnum;
import cn.com.duiba.nezha.compute.biz.conf.ElasticSearchUtilConf;
import cn.com.duiba.nezha.compute.biz.constant.ProjectConstant;
import cn.com.duiba.nezha.compute.common.enums.DateStyle;
import cn.com.duiba.nezha.compute.common.util.DateUtil;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pc on 2017/3/21.
 */
public class ElasticSearchUtilTest3 extends TestCase {

    private ElasticSearchUtil elasticSearchUtil = new ElasticSearchUtil(ElasticSearchUtilConf.esConfig);

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }



    public void testUpdateWithInsert() throws Exception {
        Map<String, ConsumerDeviceFeatureDto> syncMap = new HashMap<>();
        ConsumerDeviceFeatureDto vo = new ConsumerDeviceFeatureDto();
        vo.setAge(15L);
        vo.setConsumerId("674032061");
        vo.setIdentifyId("360122200106281819");
        vo.setSex(1L);
        syncMap.put("nzh_e_cf_674032061_", vo);

        elasticSearchUtil.multiUpsertT("nzhfeature", "feature", syncMap, 200, ProjectConstant.WEEK_1_EXPIRE);
        ConsumerDeviceFeatureDto dayOrderRank = elasticSearchUtil.getValueT("nzhfeature", "feature", "nzh_e_cf_674032061_", ConsumerDeviceFeatureDto.class);
        System.out.println("v=" + JSON.toJSONString(dayOrderRank));


        String lastKey = ModelKey.getLastModelKey(ModelKeyEnum.LR_CTR_MODEL_009.getIndex());

        System.out.println("time1="+ DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS));
        AdvertModelEntity entity = elasticSearchUtil.getValueT("nezha","lr_model", lastKey, AdvertModelEntity.class);
        System.out.println("time2="+ DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS));

        System.out.println("entity"+JSON.toJSONString(entity));
        System.out.println("time3="+ DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS));
////        if (entity != null) {
//            AdvertCtrLogisticRegression lrModel = new AdvertCtrLogisticRegression(entity.getFeatureIdxListStr(),
//                    entity.getFeatureDictStr(),
//                    entity.getModelStr(),SerializerEnum.JAVA_ORIGINAL);
//            //更新时间
////             }
        System.out.println("time4="+ DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS));
    }


}
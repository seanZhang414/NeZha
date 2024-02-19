package cn.com.duiba.nezha.compute.common.model;


import cn.com.duiba.nezha.compute.api.dto.AdvertModelEntity;
import cn.com.duiba.nezha.compute.api.dto.FeatureDto;
import cn.com.duiba.nezha.compute.api.enums.ModelKeyEnum;
import cn.com.duiba.nezha.compute.biz.bo.AdvertCtrLrModelBo;
import cn.com.duiba.nezha.compute.common.support.FeatureParse;
import junit.framework.TestCase;

import java.util.Map;

/**
 * Created by pc on 2017/2/16.
 */
public class AdvertCtrLogisticRegressionPredTest extends TestCase {
    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testReadModel() throws Exception {

    }

    public void testPredict() throws Exception {


//        AdvertModelEntity entity = AdvertCtrLrModelBo.getCTRLastModelByKey(ModelKeyEnum.LR_CTR_MODEL_005.getIndex());
        AdvertModelEntity entity = AdvertCtrLrModelBo.getCTRDtModelByKey(ModelKeyEnum.LR_CTR_MODEL_005.getIndex(), "2017-03-08");
//        AdvertCtrLrModelBo.savaCTRLastModel(entity);
//        AdvertCtrLrModelBo.savaCTRLastModelByKey(ModelKeyEnum.LR_CTR_MODEL_005.getIndex(),entity);
        if (null == entity) {
            System.out.println("the model entity is null.");
            return;
        }



//
//        final LR lrModel = new LR(entity.getFeatureIdxListStr(),
//                entity.getFeatureDictStr(),
//                entity.getModelStr(), SerializerEnum.JAVA_ORIGINAL);



//        map2.put("f101001", "5540");
//                cf.setF101001("3239");

//        map2.put("f106001", "4");
////                cf.setF106001("4");
//
//        map2.put("f108001", null);
//        map2.put("f109001", null);
//
//        map2.put("f201001", "11955");
////                cf.setF201001("427");
//
//        map2.put("f202001", "");
//
//        map2.put("f301001", "14962");
////                cf.setF301001("14962");
//
//        map2.put("f302001", null);
//        map2.put("f303001", null);
//
//        map2.put("f501001", "Android");
////                cf.setF501001("Android");
//
//        map2.put("f502001", "13");
////                cf.setF502001("13");
//
//        map2.put("f502002", "5");
////                cf.setF502002("5");
//
//        map2.put("f503001", "4419");
////                cf.setF503001("4419");
//
//        map2.put("f601001", "2");
////                cf.setF601001("2");
//
//        map2.put("f601002", "8");
////                cf.setF601002("8");
//
//        map2.put("f602001", "2");
////                cf.setF602001("2");
//
//        map2.put("f602002", "2");
////                cf.setF602002("2");
//
//        map2.put("f603001", "1");
////                cf.setF603001("1");
//
//        map2.put("f604001", "1");
////                cf.setF604001("1");
//
//        map2.put("f605001", "1");
////                cf.setF605001("1");
//
//        map2.put("f606001", "1");
////                cf.setF606001("1");
//
//        map2.put("f607001", "1");
////                cf.setF607001("1");

//
        final FeatureDto dto = new FeatureDto();
        dto.setConsumerId(131548344L);
        dto.setAccountId(4L);
        dto.setAppId(11955L);
        dto.setActivityLastChargeNums(0L);
        dto.setActivityLastGmtCreateTime(null);
        dto.setAdvertId(5540L);
        dto.setCityId(11L);
        dto.setCurrentGmtCreateTime("2017-03-01 08:00:00");
        dto.setDayActivityOrderRank(1L);
        dto.setDayOrderRank(1L);
        dto.setActivityOrderRank(1L);
        dto.setOrderRank(1L);
        dto.setSlotId(null);
        dto.setActivityId(50L);
        dto.setActivityType(9L);
        dto.setLastChargeNums(0L);
        dto.setLastGmtCreateTime(null);
        dto.setLastOperatingActivityId(172L);
        dto.setOperatingActivityId(17276L);
        dto.setUa("ios");
        Map<String, String> featureIdxMap = FeatureParse.getFeatureMap(dto);

//        Double ret = lrModel.predict(featureIdxMap);
//        System.out.println("pCtr = "+ret);

    }

}

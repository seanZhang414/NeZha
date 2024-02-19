package cn.com.duiba.nezha.compute.common.model;

import cn.com.duiba.nezha.compute.api.cachekey.ModelKey;
import cn.com.duiba.nezha.compute.api.dto.AdvertModelEntity;
import cn.com.duiba.nezha.compute.api.dto.FeatureDto;
import cn.com.duiba.nezha.compute.api.enums.ModelKeyEnum;
import cn.com.duiba.nezha.compute.biz.conf.ElasticSearchUtilConf;
import cn.com.duiba.nezha.compute.biz.dao.BaseDao;
import cn.com.duiba.nezha.compute.biz.utils.es.ElasticSearchUtil;
import cn.com.duiba.nezha.compute.common.support.FeatureParse;
import junit.framework.TestCase;
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pc on 2017/2/16.
 */
public class AdvertCtrLogisticRegressionTest extends TestCase {
    private ElasticSearchUtil elasticSearchUtil = new ElasticSearchUtil(ElasticSearchUtilConf.esConfig);
    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testPredict() throws Exception {
        SqlSession session = BaseDao.getSessionFactory().openSession();
        try {
            String lastKey = ModelKey.getLastModelKey(ModelKeyEnum.LR_CTR_MODEL_009.getIndex());

            System.out.println("time1=" + System.currentTimeMillis());
            AdvertModelEntity entity = elasticSearchUtil.getValueT("nezha", "lr_model", lastKey, AdvertModelEntity.class);
            System.out.println("time2="+ System.currentTimeMillis());

//                AdvertCtrLogisticRegression lrModel = new AdvertCtrLogisticRegression(entity.getFeatureIdxListStr(),
//                        entity.getFeatureDictStr(),
//                        entity.getModelStr(), SerializerEnum.JAVA_ORIGINAL);
//            System.out.println("time3="+ System.currentTimeMillis());


                FeatureDto cf = new FeatureDto();

            FeatureDto dto = new FeatureDto();
            dto.setConsumerId(131548344L);
            dto.setAccountId(4L);
            dto.setAppId(7983L);
            dto.setActivityLastChargeNums(0L);
            dto.setActivityLastGmtCreateTime(null);
            dto.setAdvertId((long) 100);
            dto.setCityId(null);
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

            Map<String, String> featureIdxMap = new HashMap<>();
            FeatureParse.generateFeatureMapStatic(dto,featureIdxMap);
            FeatureParse.generateFeatureMapDynamic(dto, featureIdxMap);

            System.out.println("time3.5=" + System.currentTimeMillis());
            for(int i=0;i<1000;i++){
//                dto.setAdvertId((long) i );
//                FeatureParse.generateFeatureMapDynamic(dto, featureIdxMap);
//                Double ret = lrModel.predict(featureIdxMap);
            }

            System.out.println("time4="+ System.currentTimeMillis());



        } finally {
            session.close();
        }
    }
}
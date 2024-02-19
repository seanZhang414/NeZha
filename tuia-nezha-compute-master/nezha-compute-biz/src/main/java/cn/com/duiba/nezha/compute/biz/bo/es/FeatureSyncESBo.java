package cn.com.duiba.nezha.compute.biz.bo.es;

import cn.com.duiba.nezha.compute.api.constant.GlobalConstant;
import cn.com.duiba.nezha.compute.biz.bo.HbaseBaseBo;
import cn.com.duiba.nezha.compute.biz.utils.mongodb.MongoUtil;

import java.util.Map;

public class FeatureSyncESBo extends HbaseBaseBo {


//    public static ElasticSearchUtil elasticSearchUtil = new ElasticSearchUtil(ElasticSearchUtilConf.esConfig);



    public static <T> void syncFeature(Map<String,T>  syncMap,String biz) throws Exception {
        // 同步
//        if (AssertUtil.isNotEmpty(syncMap)) {
//            elasticSearchUtil.multiUpsertT(
//                    GlobalConstant.NZ_FEATURE_ES_INDEX,
//                    GlobalConstant.NZ_FEATURE_ES_TYPE,
//                    syncMap,
//                    1000,
//                    ProjectConstant.YEAR_1_EXPIRE);
//            }
        MongoUtil.bulkWriteUpdateT(GlobalConstant.NZ_FEATURE_ES_TYPE,syncMap,biz);

    }

}

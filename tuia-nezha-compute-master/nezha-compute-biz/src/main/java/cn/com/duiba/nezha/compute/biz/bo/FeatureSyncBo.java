package cn.com.duiba.nezha.compute.biz.bo;

import cn.com.duiba.nezha.compute.api.constant.GlobalConstant;
import cn.com.duiba.nezha.compute.biz.conf.MongoDbConf;
import cn.com.duiba.nezha.compute.biz.utils.mongodb.MongoClientUtil;
import cn.com.duiba.nezha.compute.common.util.AssertUtil;

import java.util.Map;

public class FeatureSyncBo extends HbaseBaseBo {


    public static MongoClientUtil mongoClientUtil = new MongoClientUtil(MongoDbConf.config);

    public static <T> void syncFeature(Map<String,T>  syncMap) throws Exception {
        // 同步
        if (AssertUtil.isNotEmpty(syncMap)) {
            mongoClientUtil.bulkWriteUpdateT(
                    MongoDbConf.config.getDatabaseName(),
                    GlobalConstant.NZ_FEATURE_ES_TYPE,
                    syncMap);
            }
    }

}

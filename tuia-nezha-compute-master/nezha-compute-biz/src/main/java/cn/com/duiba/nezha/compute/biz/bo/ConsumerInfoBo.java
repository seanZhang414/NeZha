package cn.com.duiba.nezha.compute.biz.bo;

import cn.com.duiba.nezha.compute.api.cachekey.FeatureKey;
import cn.com.duiba.nezha.compute.biz.constant.htable.ConsumerInfoConstant;

public class ConsumerInfoBo  extends HbaseBaseBo{


    public static<T> void updateByFamily(String consumerId, String family, T log) throws Exception {
        String rowKey = FeatureKey.getConsumerInfoRowKey(consumerId);
        insert(ConsumerInfoConstant.TABLE_NAME, rowKey, family, log);
    }

    public static<T> T getByFamily(String consumerId, String family, Class<T> clazz) throws Exception {
        String rowKey = FeatureKey.getConsumerInfoRowKey(consumerId);
        return get(ConsumerInfoConstant.TABLE_NAME, rowKey, family, clazz);
    }

}

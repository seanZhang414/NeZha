package cn.com.duiba.nezha.compute.biz.bo;

import cn.com.duiba.nezha.compute.api.cachekey.AdvertStatKey;
import cn.com.duiba.nezha.compute.api.cachekey.FeatureKey;
import cn.com.duiba.nezha.compute.api.constant.GlobalConstant;
import cn.com.duiba.nezha.compute.api.dto.AdvertCtrStatDto;
import cn.com.duiba.nezha.compute.biz.conf.ElasticSearchUtilConf;
import cn.com.duiba.nezha.compute.biz.constant.ProjectConstant;
import cn.com.duiba.nezha.compute.biz.constant.htable.DeviceInfoConstant;
import cn.com.duiba.nezha.compute.biz.vo.AdvertCtrStatVo;
import cn.com.duiba.nezha.compute.biz.utils.es.ElasticSearchUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceInfoBo extends HbaseBaseBo{

    private static String index = GlobalConstant.AD_STAT_ES_INDEX;

//    public static ElasticSearchUtil elasticSearchUtil = new ElasticSearchUtil(ElasticSearchUtilConf.esConfig);



    public static<T> void updateByFamily(String deviceId, String family, T log) throws Exception {
        String rowKey = FeatureKey.getConsumerInfoRowKey(deviceId);
        insert(DeviceInfoConstant.TABLE_NAME, rowKey, family, log);
    }

    public static<T> T getByFamily(String deviceId, String family, Class<T> clazz) throws Exception {
        String rowKey = FeatureKey.getConsumerInfoRowKey(deviceId);
        return get(DeviceInfoConstant.TABLE_NAME, rowKey, family, clazz);
    }




}

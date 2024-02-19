package cn.com.duiba.nezha.compute.biz.server.biz;

import cn.com.duiba.nezha.compute.api.cachekey.FeatureKey;
import cn.com.duiba.nezha.compute.api.dto.ConsumerDeviceFeatureDto;
import cn.com.duiba.nezha.compute.biz.bo.ConsumerInfoBo;
import cn.com.duiba.nezha.compute.biz.bo.DeviceInfoBo;
import cn.com.duiba.nezha.compute.biz.bo.es.FeatureSyncESBo;
import cn.com.duiba.nezha.compute.biz.vo.FeatureSyncVo;
import cn.com.duiba.nezha.compute.common.enums.DateStyle;
import cn.com.duiba.nezha.compute.common.util.AssertUtil;
import cn.com.duiba.nezha.compute.common.util.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FeatureUpdateServer {


    public static void syncES(Map<String, ConsumerDeviceFeatureDto> syncMap,String biz) {
        try {

            if (syncMap != null) {
                String startTime2 = DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS);
                FeatureSyncESBo.syncFeature(syncMap,biz);

                String endTime2 = DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS);
                System.out.println("sync ConsumerDeviceFeatureDto,size=" + syncMap.size() + ",startTime=" + startTime2 + ",endTime=" + endTime2);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static <T> void updateConsumerByFamily(String consumerId, String family, T log) throws Exception {
        ConsumerInfoBo.updateByFamily(consumerId, family, log);
    }

    public static <T> T getConsumerByFamily(String consumerId, String family, Class<T> clazz) throws Exception {
        return ConsumerInfoBo.getByFamily(consumerId, family, clazz);
    }


    public static <T> void updateDeviceByFamily(String deviceId, String family, T log) throws Exception {
        DeviceInfoBo.updateByFamily(deviceId, family, log);
    }

    public static <T> T getDeviceByFamily(String deviceId, String family, Class<T> clazz) throws Exception {
        return DeviceInfoBo.getByFamily(deviceId, family, clazz);
    }


    public static List<FeatureSyncVo> getSyncVoList(List<String> consumerIdList, ConsumerDeviceFeatureDto featureVo) {
        List<FeatureSyncVo> retVoList = null;
        try {
            if (AssertUtil.isAllNotEmpty(consumerIdList, featureVo)) {
                retVoList = new ArrayList<>();
                for (String consumerId : consumerIdList) {
                    FeatureSyncVo retVo = new FeatureSyncVo();
                    String key = FeatureKey.getConsumerInfoMongoDbKey(consumerId);
                    retVo.setkey(key);
                    featureVo.setKey(key);
                    retVo.setConsumerDeviceFeatureDto(featureVo);
                    retVoList.add(retVo);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVoList;
    }

    public static List<FeatureSyncVo> getSyncVoList(Set<String> consumerIdSet, ConsumerDeviceFeatureDto featureVo) {
        List<FeatureSyncVo> retVoList = null;
        try {
            if (AssertUtil.isAllNotEmpty(consumerIdSet, featureVo)) {
                retVoList = new ArrayList<>();
                for (String consumerId : consumerIdSet) {
                    FeatureSyncVo retVo = new FeatureSyncVo();
                    String key = FeatureKey.getConsumerInfoMongoDbKey(consumerId);
                    retVo.setkey(key);
                    featureVo.setKey(key);
                    retVo.setConsumerDeviceFeatureDto(featureVo);
                    retVoList.add(retVo);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVoList;
    }



}

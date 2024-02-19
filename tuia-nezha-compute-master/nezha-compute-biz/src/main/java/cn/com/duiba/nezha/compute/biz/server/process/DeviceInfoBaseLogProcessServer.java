package cn.com.duiba.nezha.compute.biz.server.process;


import cn.com.duiba.nezha.compute.api.dto.ConsumerDeviceFeatureDto;
import cn.com.duiba.nezha.compute.biz.constant.htable.ConsumerInfoConstant;
import cn.com.duiba.nezha.compute.biz.constant.htable.DeviceInfoConstant;
import cn.com.duiba.nezha.compute.biz.log.DeviceInfoBaseLog;
import cn.com.duiba.nezha.compute.common.params.Params;
import cn.com.duiba.nezha.compute.biz.server.biz.FeatureUpdateServer;
import cn.com.duiba.nezha.compute.biz.vo.DeviceConsumerLinkVo;
import cn.com.duiba.nezha.compute.biz.vo.FeatureLogResultVo;
import cn.com.duiba.nezha.compute.biz.vo.FeatureSyncVo;
import cn.com.duiba.nezha.compute.common.enums.DateStyle;
import cn.com.duiba.nezha.compute.common.util.AssertUtil;
import cn.com.duiba.nezha.compute.common.util.DateUtil;
import com.alibaba.fastjson.JSONObject;
import scala.collection.Iterator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceInfoBaseLogProcessServer extends BaseProcessServer implements ILogProcessServer<DeviceInfoBaseLog, FeatureLogResultVo> {

    public static DeviceInfoBaseLogProcessServer instance;


    public static DeviceInfoBaseLogProcessServer getInstance() {
        if (instance == null) {
            instance = new DeviceInfoBaseLogProcessServer();
        }
        return instance;
    }

    @Override
    public void run(Iterator<String> partitionOfRecords, Long logType,String topic,Params.AdvertLogParams params) {

        System.out.println("parse start time " + DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS));
        Map<String, ConsumerDeviceFeatureDto> syncMap = new HashMap<>();

        try {
            while (partitionOfRecords.hasNext()) {
                // 1
                String logStr = (String) partitionOfRecords.next();

                // 2

                FeatureLogResultVo retVo = logProcess(logParse(logStr, logType),params);

                // 3
                if (retVo != null) {
                    List<FeatureSyncVo> featureSyncVoSubList = retVo.getFeatureSyncVoList();
                    if (featureSyncVoSubList != null) {
                        for (FeatureSyncVo featureSyncVo : featureSyncVoSubList) {
                            if (featureSyncVo != null) {
                                syncMap.put(featureSyncVo.getkey(), featureSyncVo.getConsumerDeviceFeatureDto());
                            }
                        }

                    }
                }

            }

            // 同步
            FeatureUpdateServer.syncES(syncMap,topic);
            System.out.println("parse end time " + DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 推啊广告发券处理
     *
     * @param logStr
     * @throws Exception
     */
    public DeviceInfoBaseLog logParse(String logStr, Long logType) {
        DeviceInfoBaseLog log = new DeviceInfoBaseLog();
        if (logStr != null) {
            try {
                String logJsonStr = getJsonStr(logStr);
                log = JSONObject.parseObject(logJsonStr, DeviceInfoBaseLog.class);
                //
//                System.out.println("logStr"+logJsonStr);
//                System.out.println("logVo"+JSONObject.toJSONString(log));
//
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return log;
    }


    /**
     * 推啊广告发券处理
     *
     * @param log
     * @throws Exception
     */

    @Override
    public FeatureLogResultVo logProcess(DeviceInfoBaseLog log,Params.AdvertLogParams params) {
        FeatureLogResultVo retVo = new FeatureLogResultVo();
        try {
            if (paramsValid(log)) {

                // 1 计算 设备 基础 特征
                ConsumerDeviceFeatureDto vo = parseFeaure(log);

                // 2 存储 设备 基础 特征
                FeatureUpdateServer.updateDeviceByFamily(
                        log.getDevice_id(),
                        DeviceInfoConstant.FM_FEATURE,
                        vo
                );

                // 3 存储 设备 基础 信息
                FeatureUpdateServer.updateDeviceByFamily(log.getDevice_id(),
                        DeviceInfoConstant.FM_BASE,
                        log);

                // 4 读取 设备 关联用户 信息

                DeviceConsumerLinkVo deviceConsumerLinkVo =
                        FeatureUpdateServer.getDeviceByFamily(log.getDevice_id(),
                                DeviceInfoConstant.FM_USER_LINK, DeviceConsumerLinkVo.class);

                List<String> consumerIdList = parseConsumerList(deviceConsumerLinkVo);

//                if(consumerIdList==null){
//                    consumerIdList = new ArrayList<>();
//                    consumerIdList.add("121311");
//                }


                // 5 计算用户 特征
                List<FeatureSyncVo> featureSyncVoList = FeatureUpdateServer.getSyncVoList(
                        consumerIdList,
                        vo);




                // 6 存储 用户  特征
                if (consumerIdList != null) {
                    for (String consumerId : consumerIdList) {
                        FeatureUpdateServer.updateConsumerByFamily(consumerId,
                                ConsumerInfoConstant.FM_FEATURE,
                                vo);
                    }
                }


                // 7 返回 用户 基础 特征
                retVo.setFeatureSyncVoList(featureSyncVoList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVo;
    }


    public ConsumerDeviceFeatureDto parseFeaure(DeviceInfoBaseLog log) {
        ConsumerDeviceFeatureDto retVo = null;
        try {
            if (AssertUtil.isNotEmpty(log)) {
                retVo = new ConsumerDeviceFeatureDto();
                retVo.setModel(log.getModel());
                retVo.setOsType(log.getOs_type());
                retVo.setVendor(log.getVendor());
                retVo.setDeviceId(log.getDevice_id());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retVo;
    }

    public List<String> parseConsumerList(DeviceConsumerLinkVo deviceConsumerLinkVo) {
        List<String> retList = null;
        try {
            if (AssertUtil.isNotEmpty(deviceConsumerLinkVo) &&
                    AssertUtil.isNotEmpty(deviceConsumerLinkVo.getConsumerLinkListStr())) {

                String[] consumerIdList = deviceConsumerLinkVo.getConsumerLinkListStr().split(",");
                if (consumerIdList != null) {
                    retList = Arrays.asList(consumerIdList);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retList;
    }


    /**
     * 参数合法性检验
     *
     * @param log
     * @return
     */
    @Override
    public Boolean paramsValid(DeviceInfoBaseLog log) {
        Boolean ret = false;
        if (log != null) {
            String deviceId = log.getDevice_id();

            if (AssertUtil.isAllNotEmpty(deviceId)) {
                ret = true;
            }

        }
        return ret;
    }


}

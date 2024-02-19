package cn.com.duiba.nezha.compute.biz.server.process;


import cn.com.duiba.nezha.compute.api.dto.ConsumerDeviceFeatureDto;
import cn.com.duiba.nezha.compute.biz.constant.htable.ConsumerInfoConstant;
import cn.com.duiba.nezha.compute.biz.constant.htable.DeviceInfoConstant;
import cn.com.duiba.nezha.compute.biz.log.UserInfoDeviceLinkLog;
import cn.com.duiba.nezha.compute.common.params.Params;
import cn.com.duiba.nezha.compute.biz.server.biz.FeatureUpdateServer;
import cn.com.duiba.nezha.compute.biz.vo.DeviceConsumerLinkVo;
import cn.com.duiba.nezha.compute.biz.vo.FeatureLogResultVo;
import cn.com.duiba.nezha.compute.biz.vo.FeatureSyncVo;
import cn.com.duiba.nezha.compute.common.enums.DateStyle;
import cn.com.duiba.nezha.compute.common.util.AssertUtil;
import cn.com.duiba.nezha.compute.common.util.DateUtil;
import cn.com.duiba.nezha.compute.common.util.MyStringUtil2;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import scala.collection.Iterator;

import java.util.*;

public class UserInfoDeviceLinkLogProcessServer  extends BaseProcessServer implements ILogProcessServer<UserInfoDeviceLinkLog, FeatureLogResultVo> {

    public static UserInfoDeviceLinkLogProcessServer instance;


    public static UserInfoDeviceLinkLogProcessServer getInstance() {
        if (instance == null) {
            instance = new UserInfoDeviceLinkLogProcessServer();
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
//                            System.out.println("syncMap"+ JSONObject.toJSONString(syncMap));
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
    public UserInfoDeviceLinkLog logParse(String logStr, Long logType) {
        UserInfoDeviceLinkLog log = new UserInfoDeviceLinkLog();
        if (logStr != null) {
            try {
                String logJsonStr = getJsonStr(logStr);
                log = JSONObject.parseObject(logJsonStr, UserInfoDeviceLinkLog.class);
                //
//                System.out.println("logStr"+logJsonStr);
//                System.out.println("log"+JSONObject.toJSONString(log));
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
    public FeatureLogResultVo logProcess(UserInfoDeviceLinkLog log,Params.AdvertLogParams params) {
        FeatureLogResultVo retVo = new FeatureLogResultVo();
        try {
            if (paramsValid(log)) {

                boolean syncStatus = true;

                // 1 读取 设备 关联用户 信息
                DeviceConsumerLinkVo deviceConsumerLinkVo =
                        FeatureUpdateServer.getDeviceByFamily(log.getDeviceId(),
                                DeviceInfoConstant.FM_USER_LINK, DeviceConsumerLinkVo.class);

                // 2 计算最新 设备关联用户信息
                Set<String> consumerIdSet = parseConsumerList(deviceConsumerLinkVo);
                if(consumerIdSet!=null){
                    consumerIdSet.add(log.getCid());
                }else{
                    consumerIdSet = new HashSet<>();
                    consumerIdSet.add(log.getCid());
                }



                // 3 存储 设备 关联用户 信息
                if(deviceConsumerLinkVo!=null){
                    deviceConsumerLinkVo.setConsumerLinkListStr(MyStringUtil2.setToString(consumerIdSet, ","));
                    FeatureUpdateServer.updateDeviceByFamily(log.getDeviceId(),
                            DeviceInfoConstant.FM_USER_LINK,
                            deviceConsumerLinkVo);
                }
//                System.out.println("deviceConsumerLinkVo"+ JSON.toJSONString(deviceConsumerLinkVo));


                // 4 读取 设备 特征

                ConsumerDeviceFeatureDto vo =
                        FeatureUpdateServer.getDeviceByFamily(log.getDeviceId(),
                                DeviceInfoConstant.FM_FEATURE, ConsumerDeviceFeatureDto.class);


                if(AssertUtil.isEmpty(vo)){
                    syncStatus=false;
                    vo = new ConsumerDeviceFeatureDto();
                    vo.setDeviceId(log.getDeviceId());
                }
                // 5 存储 用户  特征
                if (consumerIdSet != null) {
                    for (String consumerId : consumerIdSet) {
                        FeatureUpdateServer.updateConsumerByFamily(consumerId,
                                ConsumerInfoConstant.FM_FEATURE,
                                vo);
                    }
                }
                // 6 合并用户 特征
                List<FeatureSyncVo> featureSyncVoList = FeatureUpdateServer.getSyncVoList(
                        consumerIdSet,
                        vo);


                // 7 返回 用户  特征
                if(syncStatus){
                    retVo.setFeatureSyncVoList(featureSyncVoList);
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVo;
    }

    public Set<String> parseConsumerList(DeviceConsumerLinkVo deviceConsumerLinkVo) {
        Set<String> retList = null;
        try {
            if (AssertUtil.isNotEmpty(deviceConsumerLinkVo) &&
                    AssertUtil.isNotEmpty(deviceConsumerLinkVo.getConsumerLinkListStr())) {

                String[] consumerIdList = deviceConsumerLinkVo.getConsumerLinkListStr().split(",");
                if (consumerIdList != null) {
                    retList = Sets.newHashSet(consumerIdList);
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
    public Boolean paramsValid(UserInfoDeviceLinkLog log) {
        Boolean ret = false;
        if (log != null) {
            String consumerId = log.getCid();

            if (AssertUtil.isAllNotEmpty(consumerId)) {
                ret = true;
            }

        }
        return ret;
    }


}

package cn.com.duiba.nezha.compute.biz.server.process;


import cn.com.duiba.nezha.compute.api.dto.ConsumerDeviceFeatureDto;
import cn.com.duiba.nezha.compute.biz.constant.htable.ConsumerInfoConstant;
import cn.com.duiba.nezha.compute.biz.log.UserInfoLandingPageLog;
import cn.com.duiba.nezha.compute.common.params.Params;
import cn.com.duiba.nezha.compute.biz.server.biz.FeatureUpdateServer;
import cn.com.duiba.nezha.compute.biz.utils.Identity;
import cn.com.duiba.nezha.compute.biz.vo.FeatureLogResultVo;
import cn.com.duiba.nezha.compute.biz.vo.FeatureSyncVo;
import cn.com.duiba.nezha.compute.common.enums.DateStyle;
import cn.com.duiba.nezha.compute.common.util.AssertUtil;
import cn.com.duiba.nezha.compute.common.util.DateUtil;
import scala.collection.Iterator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfoLandingPageInitProcessServer extends BaseProcessServer implements ILogProcessServer<UserInfoLandingPageLog, FeatureLogResultVo> {

    public static UserInfoLandingPageInitProcessServer instance;


    public static UserInfoLandingPageInitProcessServer getInstance() {
        if (instance == null) {
            instance = new UserInfoLandingPageInitProcessServer();
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


//            System.out.println(JSONObject.toJSONString(syncMap));

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
    public UserInfoLandingPageLog logParse(String logStr, Long logType) {
        UserInfoLandingPageLog log = new UserInfoLandingPageLog();
        if (logStr != null) {
            try {
                //
                String[] ret = logStr.split("\001");
                if(ret!=null && ret.length==10){
                    List<String> valueList=Arrays.asList(ret);
                    log.setConsumer_id(valueList.get(1));
                    log.setAdvert_id(valueList.get(2));
                    log.setActivity_id(valueList.get(3));

                    log.setAdvert_plan_id(valueList.get(4));

                    log.setAdvert_media_id(valueList.get(5));

                    log.setUser_name(valueList.get(6));

                    log.setUser_phone(valueList.get(7));

                    log.setIdentification(valueList.get(8));

//                System.out.println("logStr"+logStr);
//                    System.out.println("logVo" + JSONObject.toJSONString(log));
                }
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
    public FeatureLogResultVo logProcess(UserInfoLandingPageLog log,Params.AdvertLogParams params) {
        FeatureLogResultVo retVo = new FeatureLogResultVo();
        try {
            if (paramsValid(log)) {

                // 1 计算 用户 落地页 特征
                ConsumerDeviceFeatureDto vo = parseFeature(log);
                List<FeatureSyncVo> featureSyncVoList = FeatureUpdateServer.getSyncVoList(
                        Arrays.asList(log.getConsumer_id()),
                        vo);


                // 2 存储 用户 落地页 信息
                FeatureUpdateServer.updateConsumerByFamily(log.getConsumer_id(),
                        ConsumerInfoConstant.FM_LANDING_PAGE,
                        log);

                // 3 存储 用户 落地页 特征
                FeatureUpdateServer.updateConsumerByFamily(log.getConsumer_id(),
                        ConsumerInfoConstant.FM_FEATURE,
                        vo);

                // 4 返回 用户 落地页 特征
                retVo.setFeatureSyncVoList(featureSyncVoList);

//                System.out.println("vo"+ JSONObject.toJSONString(vo));


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVo;
    }


    public ConsumerDeviceFeatureDto parseFeature(UserInfoLandingPageLog log) {
        ConsumerDeviceFeatureDto retVo = null;
        try {
            if (AssertUtil.isAllNotEmpty(log)) {
                if (AssertUtil.isNotEmpty(log.getIdentification())) {
                    retVo = new ConsumerDeviceFeatureDto();
                    Long age = Identity.getAge(log.getIdentification());
                    Long sex = Identity.getSex(log.getIdentification());
                    retVo.setAge(age);
                    retVo.setSex(sex);
                    retVo.setIdentifyId(log.getIdentification());
                    retVo.setConsumerId(log.getConsumer_id());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVo;
    }


    /**
     * 参数合法性检验
     *
     * @param log
     * @return
     */
    @Override
    public Boolean paramsValid(UserInfoLandingPageLog log) {
        Boolean ret = false;
        if (log != null) {
            String consumerId = log.getConsumer_id();

            if (AssertUtil.isAllNotEmpty(consumerId)) {
                ret = true;
            }

        }
        return ret;
    }


}

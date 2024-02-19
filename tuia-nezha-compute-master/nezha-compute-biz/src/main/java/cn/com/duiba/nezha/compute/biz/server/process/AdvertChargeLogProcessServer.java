package cn.com.duiba.nezha.compute.biz.server.process;


import cn.com.duiba.nezha.compute.biz.bo.StatCheckBo;
import cn.com.duiba.nezha.compute.biz.constant.htable.AdvertStatConstant;
import cn.com.duiba.nezha.compute.biz.log.AdvertOrderLog;
import cn.com.duiba.nezha.compute.biz.server.biz.AdvertStatUpdateServer;
import cn.com.duiba.nezha.compute.biz.server.biz.ConsumerOrderFeatureUpdateServer;
import cn.com.duiba.nezha.compute.biz.vo.AdvertStatVo;
import cn.com.duiba.nezha.compute.biz.vo.OrderFeatureSyncVo;
import cn.com.duiba.nezha.compute.biz.vo.OrderLogResultVo;
import cn.com.duiba.nezha.compute.common.enums.DateStyle;
import cn.com.duiba.nezha.compute.common.params.Params;
import cn.com.duiba.nezha.compute.common.util.AssertUtil;
import cn.com.duiba.nezha.compute.common.util.DateUtil;
import com.alibaba.fastjson.JSONObject;
import scala.collection.Iterator;

import java.util.ArrayList;
import java.util.List;

public class AdvertChargeLogProcessServer extends BaseProcessServer  implements ILogProcessServer<AdvertOrderLog, OrderLogResultVo> {

    public static AdvertChargeLogProcessServer instance;


    public static AdvertChargeLogProcessServer getInstance() {
        if (instance == null) {
            instance = new AdvertChargeLogProcessServer();
        }
        return instance;
    }

    @Override
    public void run(Iterator<String> partitionOfRecords, Long logType,String topic,Params.AdvertLogParams params) {

        System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + "  batch run start");
        List<OrderFeatureSyncVo> orderFeatureSyncVoList = new ArrayList<>();
        List<AdvertStatVo> advertStatVoList = new ArrayList<>();

        try {
            while (partitionOfRecords.hasNext()) {
                // 1
                String logStr = (String) partitionOfRecords.next();

                // 2
                AdvertOrderLog logVo =logParse(logStr, logType);
//                System.out.println("logVo = "+JSONObject.toJSONString(logVo));

                // 3
                OrderLogResultVo orderLogResultVo = logProcess(logVo,params);
//
                // 3
                if (orderLogResultVo != null && orderLogResultVo.getOrderFeatureSyncVoList() != null) {
                    orderFeatureSyncVoList.addAll(orderLogResultVo.getOrderFeatureSyncVoList());
                }

                if (orderLogResultVo != null && orderLogResultVo.getAdvertStatVo() != null) {
                    advertStatVoList.add(orderLogResultVo.getAdvertStatVo());
                }

            }
//
//
//
//
            StatCheckBo.updateTime(AdvertStatConstant.FM_CHARGE);
            if (!StatCheckBo.getStatDelayStatus()) {

//                // 同步
                System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + "  ConsumerOrderFeatureUpdateServer.syncES(orderFeatureSyncVoList, topic)");
                ConsumerOrderFeatureUpdateServer.syncES(orderFeatureSyncVoList, topic);
//
//
//                System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + "  AdvertStatUpdateServer.syncES(advertStatVoList, topic)");
//                AdvertStatUpdateServer.syncES(advertStatVoList, topic);
//                System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + "  batch run end");

            }else{
                System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + " WARN: batch run delay stop return");
            }

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
    public AdvertOrderLog logParse(String logStr, Long logType) {
        AdvertOrderLog logVo = new AdvertOrderLog();
        if (logStr != null) {
            try {
                String logJsonStr = getJsonStr(logStr);
                logVo = JSONObject.parseObject(logJsonStr, AdvertOrderLog.class);
                logVo.setGmtDate(DateUtil.string2String(logVo.getTime(), DateStyle.YYYY_MM_DD_HH_MM_SS , DateStyle.YYYY_MM_DD));
                logVo.setCurrentTime(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS));
                logVo.setType(logType);

                //
                StatCheckBo.setGmtTime(logVo.getTime());
//                System.out.println(JSONObject.toJSONString(logVo));
//
                Integer dms= DateUtil.getIntervalMinutes(logVo.getTime(), logVo.getCurrentTime(),DateStyle.YYYY_MM_DD_HH_MM_SS);
                if(dms!=null && dms>180){
                    System.out.println("日志时间异常,dms="+dms+","+JSONObject.toJSONString(logVo));
                }



            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return logVo;
    }


    /**
     * 推啊广告发券处理
     *
     * @param advertOrderLog
     * @throws Exception
     */

    public OrderLogResultVo logProcess(AdvertOrderLog advertOrderLog,Params.AdvertLogParams params) {
        OrderLogResultVo retVo = new OrderLogResultVo();
        try {
            if (paramsValid(advertOrderLog)) {
                // 业务1 用户订单特征处理
                List<OrderFeatureSyncVo> orderFeatureSyncVoList = ConsumerOrderFeatureUpdateServer.chargeLogProcess(advertOrderLog);
                retVo.setOrderFeatureSyncVoList(orderFeatureSyncVoList);

                // 业务2 广告统计处理
                AdvertStatVo advertStatVo = AdvertStatUpdateServer.logProcess(advertOrderLog, AdvertStatConstant.FM_CHARGE,params);
                retVo.setAdvertStatVo(advertStatVo);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVo;
    }

    /**
     * 参数合法性检验
     *
     * @param advertOrderLog
     * @return
     */
    public Boolean paramsValid(AdvertOrderLog advertOrderLog) {
        Boolean ret = false;
        if (advertOrderLog != null) {
            String consumerId = advertOrderLog.getConsumerId();
            String activityId = advertOrderLog.getActivityId();
            String orderId = advertOrderLog.getOrderId();
            String gmtDate = advertOrderLog.getGmtDate();
            String gmtTime = advertOrderLog.getTime();
            String currentTime = advertOrderLog.getCurrentTime();
            String referrerType =advertOrderLog.getReferrerType();

            String deliveryType =advertOrderLog.getDeliveryType();
            if(deliveryType==null){
                deliveryType="1";
            }

            if (AssertUtil.isAllNotEmpty(consumerId,
                    activityId,
                    orderId, gmtDate, gmtTime, currentTime)&& deliveryType.equals("1")) {
                ret = true;
            }

        }
        return ret;
    }


}

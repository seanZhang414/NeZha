package cn.com.duiba.nezha.compute.biz.server.process;


import cn.com.duiba.nezha.compute.biz.bo.StatCheckBo;
import cn.com.duiba.nezha.compute.biz.constant.htable.AdvertStatConstant;
import cn.com.duiba.nezha.compute.biz.log.AdvertOrderLog;
import cn.com.duiba.nezha.compute.common.params.Params;
import cn.com.duiba.nezha.compute.biz.server.biz.AdvertStatUpdateServer;
import cn.com.duiba.nezha.compute.biz.server.biz.ConsumerOrderFeatureUpdateServer;
import cn.com.duiba.nezha.compute.biz.vo.AdvertStatVo;
import cn.com.duiba.nezha.compute.biz.vo.OrderFeatureSyncVo;
import cn.com.duiba.nezha.compute.biz.vo.OrderLogResultVo;
import cn.com.duiba.nezha.compute.common.enums.DateStyle;
import cn.com.duiba.nezha.compute.common.util.AssertUtil;
import cn.com.duiba.nezha.compute.common.util.DateUtil;
import com.alibaba.fastjson.JSONObject;
import scala.collection.Iterator;

import java.util.ArrayList;
import java.util.List;

public class LandingPageExpLogProcessServer extends BaseProcessServer implements ILogProcessServer<AdvertOrderLog, OrderLogResultVo> {

    public static LandingPageExpLogProcessServer instance;


    public static LandingPageExpLogProcessServer getInstance() {
        if (instance == null) {
            instance = new LandingPageExpLogProcessServer();
        }
        return instance;
    }

    public void run(Iterator<String> partitionOfRecords, Long logType, String topic, Params.AdvertLogParams params) {

        System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + "  batch run start");
        List<OrderFeatureSyncVo> orderFeatureSyncVoList = new ArrayList<>();
        List<AdvertStatVo> advertStatVoList = new ArrayList<>();

        int i = 0;
        while (partitionOfRecords.hasNext()) {
            i++;
            // 1
            String logStr = (String) partitionOfRecords.next();
//            System.out.println("logStr"+JSONObject.toJSONString(logStr));

            // 2
            OrderLogResultVo orderLogResultVo = logProcess(logParse(logStr, logType), params);

            // 3
            if (orderLogResultVo != null && orderLogResultVo.getOrderFeatureSyncVoList() != null) {
                orderFeatureSyncVoList.addAll(orderLogResultVo.getOrderFeatureSyncVoList());
            }

            if (orderLogResultVo != null && orderLogResultVo.getAdvertStatVo() != null) {
                advertStatVoList.add(orderLogResultVo.getAdvertStatVo());
            }

        }
        // 同步
        ConsumerOrderFeatureUpdateServer.syncES(orderFeatureSyncVoList, topic);

        StatCheckBo.updateTime(AdvertStatConstant.FM_ACT_EXP);
        if (!StatCheckBo.getStatDelayStatus()) {
//            System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + "  AdvertStatUpdateServer.syncES(advertStatVoList, topic)");
//            AdvertStatUpdateServer.syncES(advertStatVoList, topic);
//            System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + "  batch run end");

        } else {
            System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + " WARN: batch run delay stop return");
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
                logVo.setTime(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS));
                logVo.setGmtDate(DateUtil.string2String(logVo.getTime(), DateStyle.YYYY_MM_DD_HH_MM_SS, DateStyle.YYYY_MM_DD));
                logVo.setCurrentTime(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS));
                logVo.setType(logType);
                StatCheckBo.setGmtTime(logVo.getTime());
                //
                Integer dms= DateUtil.getIntervalMinutes(logVo.getTime(), logVo.getCurrentTime(),DateStyle.YYYY_MM_DD_HH_MM_SS);
                if(dms!=null && dms>180){
                    System.out.println("日志时间异常"+JSONObject.toJSONString(logVo));
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
     * @param log
     * @throws Exception
     */

    public OrderLogResultVo logProcess(AdvertOrderLog log, Params.AdvertLogParams params) {
        OrderLogResultVo retVo = new OrderLogResultVo();
        try {
            if (paramsValid(log)) {


                // 业务2 广告统计处理
                AdvertStatVo advertStatVo = AdvertStatUpdateServer.logProcess(log, AdvertStatConstant.FM_ACT_EXP, params);
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
     * @param log
     * @return
     */
    public Boolean paramsValid(AdvertOrderLog log) {
        Boolean ret = false;
        if (log != null) {
            String advertId = log.getAdvertId();
            String gmtDate = log.getGmtDate();
            String gmtTime = log.getTime();
            String currentTime = log.getCurrentTime();
            String chargeType = log.getChargeType();
            String referrerType = log.getReferrerType();
            String deliveryType = log.getDeliveryType();
            if(deliveryType==null){
                deliveryType="1";
            }
            //&& referrerType.equals("1")
            if (AssertUtil.isAllNotEmpty(advertId, gmtDate, gmtTime, currentTime) && deliveryType.equals("1")) {
                ret = true;
            }
        }
        return ret;
    }


}

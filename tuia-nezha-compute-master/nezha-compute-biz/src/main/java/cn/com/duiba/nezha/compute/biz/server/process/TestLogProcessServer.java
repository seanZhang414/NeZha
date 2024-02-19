package cn.com.duiba.nezha.compute.biz.server.process;


import cn.com.duiba.nezha.compute.biz.log.AdvertOrderLog;
import cn.com.duiba.nezha.compute.common.params.Params;
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

public class TestLogProcessServer extends BaseProcessServer implements ILogProcessServer<AdvertOrderLog, OrderLogResultVo> {

    public static TestLogProcessServer instance;


    public static TestLogProcessServer getInstance() {
        if (instance == null) {
            instance = new TestLogProcessServer();
        }
        return instance;
    }

    public void run(Iterator<String> partitionOfRecords, Long logType,String topic,Params.AdvertLogParams params) {

        System.out.println("parse start time " + DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS));
        List<OrderFeatureSyncVo> orderFeatureSyncVoList = new ArrayList<>();
        List<AdvertStatVo> advertStatVoList = new ArrayList<>();

        int i =0;
        while (partitionOfRecords.hasNext()) {
            i++;
        }

        System.out.println("i " + i);
        // 同步
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
                logVo.setGmtDate(DateUtil.string2String(logVo.getTime(), DateStyle.YYYY_MM_DD_HH_MM_SS, DateStyle.YYYY_MM_DD));
                logVo.setCurrentTime(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS));
                logVo.setType(logType);
                //
//                System.out.println(JSONObject.toJSONString(logVo));
//
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

            if (AssertUtil.isAllNotEmpty(consumerId,
                    activityId,
                    orderId, gmtDate, gmtTime, currentTime)) {
                ret = true;
            }

        }
        return ret;
    }


}

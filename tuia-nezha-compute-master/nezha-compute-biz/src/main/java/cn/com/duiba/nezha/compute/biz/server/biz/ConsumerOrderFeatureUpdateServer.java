package cn.com.duiba.nezha.compute.biz.server.biz;


import cn.com.duiba.nezha.compute.api.dto.ConsumerOrderFeatureDto;
import cn.com.duiba.nezha.compute.biz.bo.ConsumerOrderFeatureBo;
import cn.com.duiba.nezha.compute.biz.log.AdvertOrderLog;
import cn.com.duiba.nezha.compute.biz.vo.OrderFeatureSyncVo;
import cn.com.duiba.nezha.compute.common.enums.DateStyle;
import cn.com.duiba.nezha.compute.common.params.Params;
import cn.com.duiba.nezha.compute.common.util.DateUtil;

import java.util.ArrayList;
import java.util.List;


public class ConsumerOrderFeatureUpdateServer {

    public static ConsumerOrderFeatureBo consumerOrderFeatureBo = new ConsumerOrderFeatureBo();


    /**
     * @param orderFeatureSyncVoList
     */
    public static void syncES(List<OrderFeatureSyncVo> orderFeatureSyncVoList,String biz) {
        try {

            String startTime = DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS);
            consumerOrderFeatureBo.syncES(orderFeatureSyncVoList,biz);

            String endTime = DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS);
            System.out.println("sync orderFeatureSyncVoList.size= " + orderFeatureSyncVoList.size()+",startTime="+startTime+",endTime="+endTime);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 推啊广告发券处理
     *
     * @param advertOrderLog
     * @throws Exception
     */
    public static List<OrderFeatureSyncVo> launchLogProcess(AdvertOrderLog advertOrderLog, Params.AdvertLogParams params) throws Exception {

        List<OrderFeatureSyncVo> orderFeatureSyncVoList = new ArrayList<>();

        try {
            OrderFeatureSyncVo dto1 = launchSubProcess(advertOrderLog, advertOrderLog.getActivityId());
            OrderFeatureSyncVo dto2 = launchSubProcess(advertOrderLog, null);
            if (dto1 != null) {
                orderFeatureSyncVoList.add(dto1);
            }
            if (dto2 != null) {
                orderFeatureSyncVoList.add(dto2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return orderFeatureSyncVoList;
    }

    /**
     * 推啊广告计费处理
     *
     * @param advertOrderLog
     * @throws Exception
     */
    public static List<OrderFeatureSyncVo> chargeLogProcess(AdvertOrderLog advertOrderLog) throws Exception {
        List<OrderFeatureSyncVo> orderFeatureSyncVoList = new ArrayList<>();
        try {
            OrderFeatureSyncVo dto1 = chargeSubProcess(advertOrderLog, advertOrderLog.getActivityId());
            OrderFeatureSyncVo dto2 = chargeSubProcess(advertOrderLog, null);
            if (dto1 != null) {
                orderFeatureSyncVoList.add(dto1);
            }
            if (dto2 != null) {
                orderFeatureSyncVoList.add(dto2);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderFeatureSyncVoList;
    }



    /**
     * 推啊广告发券处理
     *
     * @param advertOrderLog
     * @throws Exception
     */
    public static OrderFeatureSyncVo launchSubProcess(AdvertOrderLog advertOrderLog, String activityDimId) throws Exception {
        OrderFeatureSyncVo orderFeatureSyncVo = new OrderFeatureSyncVo();
        String consumerId = advertOrderLog.getConsumerId();

        String orderId = advertOrderLog.getOrderId();
        String gmtDate = advertOrderLog.getGmtDate();
        String gmtTime = advertOrderLog.getTime();
        String currentTime = advertOrderLog.getCurrentTime();


        try {
            // 1 更新 发券排序
            consumerOrderFeatureBo.incrementOrderRank(consumerId, activityDimId, gmtDate);


            // 2 读取 特征数据
            ConsumerOrderFeatureDto cofDto = consumerOrderFeatureBo.getConsumerOrderFeature(consumerId, activityDimId, gmtDate, gmtTime, currentTime);
            // 3 更新首单信息
            if (cofDto.getFirstOrderTime() == null) {
                cofDto.setFirstOrderTime(gmtTime);
                // 插入 首单信息
                consumerOrderFeatureBo.insertFirstOrderInfo(consumerId, activityDimId, gmtTime);
            }
//        // 4 更新最近订单信息
            String lastOrderId = orderId;
            String lastOrderGmtTime = gmtTime;
            // 读取 最近订单计费情况
            String lastOrderChargeNums = consumerOrderFeatureBo.getOrderChargeNums(consumerId, lastOrderId);

            cofDto.setLastOrderId(lastOrderId);
            cofDto.setLastOrderTime(lastOrderGmtTime);
            cofDto.setLastOrderChargeNums(lastOrderChargeNums);

            // 插入  最近订单信息
            consumerOrderFeatureBo.insertLastOrderInfo(consumerId,
                    activityDimId,
                    lastOrderChargeNums,
                    lastOrderId,
                    lastOrderGmtTime);

            // 5 更新最近活动信息
            cofDto.setLastActivityId(activityDimId);
            // 插入 最近活动信息
            consumerOrderFeatureBo.insertLastActivityInfo(consumerId, activityDimId, activityDimId);
//
            // 6 同步

            orderFeatureSyncVo.setactivityDimId(activityDimId);
            orderFeatureSyncVo.setConsumerId(consumerId);
            orderFeatureSyncVo.setConsumerOrderFeatureDto(cofDto);
            orderFeatureSyncVo.setGmtDate(gmtDate);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderFeatureSyncVo;
    }



    /**
     * 推啊广告计费处理
     *
     * @param advertOrderLog
     * @throws Exception
     */
    public static OrderFeatureSyncVo chargeSubProcess(AdvertOrderLog advertOrderLog, String activityDimId) throws Exception {
        OrderFeatureSyncVo orderFeatureSyncVo = new OrderFeatureSyncVo();
        String consumerId = advertOrderLog.getConsumerId();
        String orderId = advertOrderLog.getOrderId();
        String gmtDate = advertOrderLog.getGmtDate();
        String gmtTime = advertOrderLog.getTime();
        String currentTime = advertOrderLog.getCurrentTime();

        try {

            // 1 更新 计费
            consumerOrderFeatureBo.incrementOrderChargeNums(consumerId, orderId);

            // 2 读取 特征数据
            ConsumerOrderFeatureDto cofDto = consumerOrderFeatureBo.getConsumerOrderFeature(consumerId, activityDimId, gmtDate, gmtTime, currentTime);
            // 当前订单为最近一次订单，则更新计费情况
            if (orderId.equals(cofDto.getLastOrderId())) {
                // 读取 最近订单计费情况
                String lastOrderChargeNums = consumerOrderFeatureBo.getOrderChargeNums(consumerId, orderId);
                cofDto.setLastOrderChargeNums(lastOrderChargeNums);
                // 插入  最近订单信息
                consumerOrderFeatureBo.insertLastOrderInfo(consumerId, activityDimId, lastOrderChargeNums, null, null);
            }

            // 6 同步
            orderFeatureSyncVo.setactivityDimId(activityDimId);
            orderFeatureSyncVo.setConsumerId(consumerId);
            orderFeatureSyncVo.setConsumerOrderFeatureDto(cofDto);
            orderFeatureSyncVo.setGmtDate(gmtDate);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderFeatureSyncVo;
    }




}

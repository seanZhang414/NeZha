package cn.com.duiba.nezha.compute.biz.server.distribute;


import cn.com.duiba.nezha.compute.api.enums.LogTopicEnum;
import cn.com.duiba.nezha.compute.biz.server.process.*;
import cn.com.duiba.nezha.compute.common.params.Params;
import scala.collection.Iterator;

public class LogDistributeServer {


    public static void distribute(Iterator<String> partitionOfRecords, LogTopicEnum topicEnum,Params.AdvertLogParams params ) throws Exception {

        try {
            if (topicEnum == null) {
                return;
            }
            // 1 发券日志
            if (topicEnum == LogTopicEnum.TUIA_LAUNCH) {
                AdvertLaunchLogProcessServer.getInstance().run(partitionOfRecords, topicEnum.getId(),topicEnum.getTopic(),params);
            }

            // 2 计费日志
            if (topicEnum == LogTopicEnum.TUIA_CHARGE) {
                AdvertChargeLogProcessServer.getInstance().run(partitionOfRecords, topicEnum.getId(),topicEnum.getTopic(),params);
            }

            // 3 设备信息-基本
            if (topicEnum == LogTopicEnum.DEVICE_INFO_BASE) {
                DeviceInfoBaseLogProcessServer.getInstance().run(partitionOfRecords, topicEnum.getId(),topicEnum.getTopic(),params);
            }

            // 4 设备信息-地域
            if (topicEnum == LogTopicEnum.DEVICE_REGION) {
                DeviceInfoRegionLogProcessServer.getInstance().run(partitionOfRecords, topicEnum.getId(),topicEnum.getTopic(),params);
            }

            // 5 设备信息-SDK-1
            if (topicEnum == LogTopicEnum.DEVICE_INFO_SDK_P1) {
                DeviceInfoSdkPart1LogProcessServer.getInstance().run(partitionOfRecords, topicEnum.getId(),topicEnum.getTopic(),params);
            }

            // 6 设备信息-SDK-2
            if (topicEnum == LogTopicEnum.DEVICE_INFO_SDK_P2) {
                DeviceInfoSdkPart2LogProcessServer.getInstance().run(partitionOfRecords, topicEnum.getId(),topicEnum.getTopic(),params);
            }

            // 7 落地页日志
            if (topicEnum == LogTopicEnum.TUIA_LANDING_PAGE) {
                UserInfoLandingPageLogProcessServer.getInstance().run(partitionOfRecords, topicEnum.getId(),topicEnum.getTopic(),params);
            }

            // 8 设备-用户-关联
            if (topicEnum == LogTopicEnum.DEVICE_USER_LINK) {
                UserInfoDeviceLinkLogProcessServer.getInstance().run(partitionOfRecords, topicEnum.getId(),topicEnum.getTopic(),params);
            }

            // 9 落地页日志-曝光
            if (topicEnum == LogTopicEnum.TUIA_LANDING_PAGE_EXP) {
                LandingPageExpLogProcessServer.getInstance().run(partitionOfRecords, topicEnum.getId(),topicEnum.getTopic(),params);
            }

            // 10 落地页日志-点击
            if (topicEnum == LogTopicEnum.TUIA_LANDING_PAGE_CLICK) {
                LandingPageClickLogProcessServer.getInstance().run(partitionOfRecords, topicEnum.getId(),topicEnum.getTopic(),params);
            }

            // 11 哪吒日志
            if (topicEnum == LogTopicEnum.TUIA_NEZHA) {
                TuiaNezhaLogProcessServer.getInstance().run(partitionOfRecords, topicEnum.getId(),topicEnum.getTopic(),params);
            }

            // 999 test
            if (topicEnum == LogTopicEnum.TEST) {


            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}

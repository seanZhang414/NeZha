package cn.com.duiba.nezha.compute.biz.support;

import cn.com.duiba.nezha.compute.api.enums.LogTopicEnum;

/**
 * Created by pc on 2017/2/27.
 */
public class Topic {

    public static LogTopicEnum getTopic(String topicId) {
        LogTopicEnum ret = null;

        if (topicId != null) {
            switch (topicId) {
                case "launch":
                    ret = LogTopicEnum.TUIA_LAUNCH;
                    break;
                case "charge":
                    ret = LogTopicEnum.TUIA_CHARGE;
                    break;
                case "device_info_b":
                    ret =LogTopicEnum.DEVICE_INFO_BASE;
                    break;
                case "device_info_region":
                    ret =LogTopicEnum.DEVICE_REGION;
                    break;
                case "device_info_sp1":
                    ret =LogTopicEnum.DEVICE_INFO_SDK_P1;
                    break;
                case "device_info_sp2":
                    ret =LogTopicEnum.DEVICE_INFO_SDK_P2;
                    break;
                case "landing_page":
                    ret =LogTopicEnum.TUIA_LANDING_PAGE;
                    break;
                case "device_user_link":
                    ret =LogTopicEnum.DEVICE_USER_LINK;
                    break;
                case "landing_page_exp":
                    ret =LogTopicEnum.TUIA_LANDING_PAGE_EXP;
                    break;
                case "landing_page_click":
                    ret =LogTopicEnum.TUIA_LANDING_PAGE_CLICK;
                    break;

                case "nezha":
                    ret =LogTopicEnum.TUIA_NEZHA;
                    break;

                case "test":
                    ret =LogTopicEnum.TEST;
                    break;
            }
        }
        return ret;

    }

}

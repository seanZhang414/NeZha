/**
 * Project Name:engine-common File Name:CacheKeyConstant.java Package Name:cn.com.duiba.tuia.cache
 * Date:2016年7月19日下午2:31:11 Copyright (c) 2016, duiba.com.cn All Rights Reserved.
 */

package cn.com.duiba.tuia.constant;

import java.util.Date;

import cn.com.duiba.wolf.utils.DateUtils;

/**
 * ClassName:CacheKeyConstant <br/>
 * Date: 2016年7月19日 下午2:31:11 <br/>
 *
 * @author sunjiangrong
 * @see
 * @since JDK 1.6
 */
public class CacheKeyConstant {

    private static String       prefix;

    /**
     * 活动引擎广告位-用户维度的KEY
     */
    private static final String USER                      = "-NU-";

    /**
     * 广告位曝光次数
     */
    private static final String SLOT_FREQUENCY            = "SF";

    /**
     * 活动曝光次数
     */
    private static final String ACTIVITY_FREQUENCY        = "NAF";

    /**
     * 用户活动访问历史
     */
    private static final String VISIT_HISTORY             = "VH";

    /**
     * 新素材曝光数量
     */
    private static final String NEW_MATERIAL_EXPOSE_COUNT = "-NMEC-";

    /**
     * 新活动曝光数量
     */
    private static final String NEW_ACT_EXPOSE_COUNT      = "-NNAEC-";

    /**
     * 设备IP访问记录
     */
    private static final String DEVICE_IP                 = "-IP-";

    private static final String REFRESH_ACTIVITYINFO_TAG   = "REFRESH_ACTIVITYINFO_TAG";

    private CacheKeyConstant() {
    }

    public static String getRefreshActivityinfoTag(){return REFRESH_ACTIVITYINFO_TAG;}

    public static String getPrefix() {
        return prefix;
    }

    public static void setPrefix(String prefixStr) {
        prefix = prefixStr;
    }

    public static String getSlotFrequency() {
        return SLOT_FREQUENCY;
    }

    public static String getActivityFrequency() {
        return ACTIVITY_FREQUENCY;
    }

    /**
     * 获取用户维度的key
     * 
     * @param slotId
     * @param deviceId
     * @return key
     */
    public static String getUserKey(Long slotId, String deviceId) {
        String date = DateUtils.getDayStr(new Date());
        return getPrefix() + USER + slotId + "-" + deviceId + "-" + date;
    }

    public static String getVisitHistory() {
        return VISIT_HISTORY;
    }

    /**
     * 获取新素材曝光量KEY
     * 
     * @param materialId 素材ID
     * @return KEY
     */
    public static String getNewMaterialExposeCount(Long materialId) {
        return getPrefix() + NEW_MATERIAL_EXPOSE_COUNT + materialId;
    }

    /**
     * 获取新活动曝光量KEY
     * 
     * @param activityId 活动ID
     * @param activitySource 活动来源
     * @return KEY
     */
    public static String getNewActExposeCount(Long activityId, Integer activitySource) {
        return getPrefix() + NEW_ACT_EXPOSE_COUNT + activityId + "-" + activitySource;
    }

    /**
     * 获取设备IP前缀
     * 
     * @param ip
     * @param deviceId
     * @return
     */
    public static String getDeviceIp(String deviceId, String ip) {
        return getPrefix() + DEVICE_IP + deviceId + "-" + ip;
    }
}

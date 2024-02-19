package cn.com.duiba.nezha.compute.api.enums;

/**
 * Created by xuezhaoming on 16/8/2.
 */
public enum LogTopicEnum {

    TUIA_LAUNCH("innerlog_group_1_type_20", 20),// 广告发券日志
    TUIA_CHARGE("innerlog_group_1_type_25", 25),// 广告计费日志
    DEVICE_INFO_BASE("innerlog_group_1_type_9", 9),// 设备基本信息日志
    DEVICE_REGION("innerlog_group_1_type_10", 10),// 设备地域信息日志
    DEVICE_INFO_SDK_P1("innerlog_group_1_type_16", 16),// 设备信息-SDK-1日志
    DEVICE_INFO_SDK_P2("innerlog_group_1_type_17", 17),// 设备信息-SDK-2日志
    TUIA_LANDING_PAGE("innerlog_group_1_type_30", 30),// 落地页日志
    DEVICE_USER_LINK("innerlog_group_1_type_31", 31),// 设备用户关联日志
    TUIA_LANDING_PAGE_EXP("innerlog_group_1_type_7", 7),// 广告落地页日志转换曝光日志
    TUIA_LANDING_PAGE_CLICK("innerlog_group_1_type_8", 8),//广告落地页日志转换点击日志
    TUIA_NEZHA("innerlog_group_1_type_38", 38),//哪吒推荐指标监控日志
    TEST("test03", 999),// 设备-用户-关联
    ;

    private String topic;

    private long id;

    LogTopicEnum(String topic, long id) {
        this.topic = topic;
        this.id = id;
    }

    public String  getTopic() {
        return topic;
    }

    public long getId() {
        return id;
    }
}

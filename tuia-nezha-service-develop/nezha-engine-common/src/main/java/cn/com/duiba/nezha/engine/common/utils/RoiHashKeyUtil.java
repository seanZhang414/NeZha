package cn.com.duiba.nezha.engine.common.utils;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: RoiHashKeyUtil.java , v 0.1 2017/7/25 下午4:54 ZhouFeng Exp $
 */
public class RoiHashKeyUtil {
    public static final String DEFAULT = "DEFAULT";

    public static final String APP = "APP";

    public static final String SLOT = "SLOT";

    public static final String ACTIVITY = "ACTIVITY";

    public static String getDefault() {
        return DEFAULT;
    }

    public static String getAppKey(Object appId) {
        return APP + "_" + appId;
    }

    public static String getSlotKey(Object slotId) {
        return SLOT + "_" + slotId;
    }

    public static String getActivityKey(Object appId, String activityId) {
        return ACTIVITY + "_" + appId + "_" + activityId;
    }

    public static String getActivityKey(Object appId, Long activityId, Integer activityUserType) {
        String activityPrefix = activityUserType == 2 ? "1" : "0";
        return ACTIVITY + "_" + appId + "_" + activityPrefix + "_" + activityId;
    }

    private RoiHashKeyUtil(){
        //不允许创建实例
    }


}

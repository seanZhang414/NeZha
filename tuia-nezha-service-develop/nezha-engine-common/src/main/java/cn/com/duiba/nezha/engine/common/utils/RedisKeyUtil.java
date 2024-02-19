package cn.com.duiba.nezha.engine.common.utils;

import cn.com.duiba.nezha.engine.api.enums.RedisKey;
import com.google.common.base.Joiner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static cn.com.duiba.nezha.engine.common.utils.RedisKeyUtil.DataType.*;

/**
 * redis key组装工具类
 *
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: RedisKeyUtil.java , v 0.1 2017/5/19 下午4:56 ZhouFeng Exp $
 */
public class RedisKeyUtil {

    private static final String SEPARATOR = "_";

    private static final String NZ_PREFIX = "NZ";

    private static final String TUIA_PREFIX = "TUIA";

    /**
     * 获取CPA广告点击计数key
     */
    public static String roiClickKey(String advertId, String packageId) {
        return getKey(RedisKey.K37, advertId, packageId, LocalDate.now());
    }

    /**
     * 获取CPC计费累计key
     */
    public static String roiFeeKey(String advertId, String packageId, LocalDate localDate) {
        return getKey(RedisKey.K38, advertId, packageId, localDate);
    }

    /**
     * 获取CPA广告转换次数累计key
     */
    public static String roiCvrKey(String advertId, String packageId, LocalDate localDate) {
        return getKey(RedisKey.K39, advertId, packageId, localDate);
    }

    /**
     * CPA广告点击分布式锁key
     */
    public static String roiClickLockKey(String advertId, String packageId) {
        return getKey(RedisKey.K040, advertId, packageId);
    }

    /**
     * CPA最近点击记录
     */
    public static String recentlyClickKey(String advertId, String packageId) {
        return getKey(RedisKey.K43, advertId, packageId, LocalDate.now());
    }

    /**
     * 广告统计特征
     */
    public static String advertStatFeatureKey(Long appId, Long activityId, Long slotId, Long advertId) {
        String newKey = getNewKey(advertId, appId, null, null, activityId, slotId, null, null);
        return getKey(RedisKey.K44, newKey);
    }

    /**
     * 广告融合统计数据
     */
    public static String advertMergeStatKey(Long appId, Long advertId, Long materialId, Long times) {
        String newKey = getNewKey(advertId, appId, materialId, times, null, null, null, null);
        return getKey(RedisKey.K46, newKey);
    }

    /**
     * 广告分日统计数据
     *
     * @param timestamp 时间戳 yyyyMMdd
     */
    public static String advertDailyStatKey(Long appId, Long advertId, Long packageId, Long materialId, Long tag, String
            timestamp) {
        String newKey = getNewKey(advertId, appId, materialId, null, null, null, packageId, tag);
        return getKey(RedisKey.K47, newKey, timestamp);
    }

    /**
     * 广告分时统计数据
     *
     * @param timestamp 时间戳 yyyyMMddHH
     */
    public static String advertHourlyStatKey(Long appId, Long advertId, Long packageId, Long materialId, String
            timestamp) {
        String newKey = getNewKey(advertId, appId, materialId, null, null, null, packageId, null);
        return getKey(RedisKey.K48, newKey, timestamp);
    }

    /**
     * 广告素材排名
     */
    public static String materialRankList(Long appId, Long advertId) {
        return getKey(RedisKey.K045, appId, advertId);
    }

    /**
     * 广告调价因子key
     */
    public static String factorKey(Long advertId, Long packageId, String suffix) {
        return getKey(RedisKey.K55, advertId, packageId, suffix);
    }

    /**
     * 标签分日统计数据
     *
     * @param timestamp 时间戳 yyyyMMdd
     */
    public static String tagDailyStatKey(Long appId, String tagId, String timestamp) {
        return getKey(RedisKey.K060, appId, tagId, timestamp);
    }

    /**
     * 标签分时统计数据
     *
     * @param timestamp 时间戳 yyyyMMddHH
     */
    public static String tagHourlyStatKey(Long appId, String tagId, String timestamp) {
        return getKey(RedisKey.K061, appId, tagId, timestamp);
    }

    public static String getNezhaStatKey(Long algType, Long advertId, Long appId) {
        return getKey(RedisKey.K66, algType, advertId, appId);
    }

    public static String getNezhaStatKey(RedisKey redisKey, Long algType, Long advertId, Long appId, Long slotId) {
        return getKey(redisKey, algType, advertId, slotId, appId);
    }

    public static String getBlackWhiteKey(Long appId, Long slotId, Long activityId, Long orientationId, Long advertId) {
        return getKey(RedisKey.K73, appId, slotId, activityId, orientationId, advertId);
    }

    public static String getNewBlackWhiteKey(Long advertId, Long orientationId) {
        return getKey(RedisKey.K75, advertId, orientationId);
    }

    /**
     * 自动托管广告位纬度下的广告信息
     */
    public static String getSlotAdvertInfoKey(Long slotId, Long advertId) {
        return getKey(RedisKey.K076, slotId, advertId);
    }

    public static String getSlotPackageDataKey(Long slotId, Long advertId, Long orientationId, Long fee,String timestamp) {
        return getKey(RedisKey.K84, slotId, advertId, orientationId, fee,timestamp);
    }

    public static String getAccountConsumeKey(Long accountId) {
        return getKey(RedisKey.K78, accountId);
    }

    public static String getAdvertOrientationAppConsumeKey(Long advertId, Long orientationId, Long appId) {
        return getKey(RedisKey.K79, advertId, orientationId, appId);
    }

    public static String getAdvertOrientationHourConsumeKey(Long advertId, Long orientationId) {
        return getKey(RedisKey.K80, advertId, orientationId);
    }

    public static String getAdvertOrientationConsumeKey(Long advertId, Long orientationId) {
        return getKey(RedisKey.K81, advertId, orientationId);
    }

    public static String getAdvertConsumeKey(Long advertId) {
        return getKey(RedisKey.K82, advertId);
    }

    public static String getActivityFeatureKey(Long activityId) {
        return getKey(RedisKey.K83, activityId);
    }

    public static String getOrientationPackageData(Long advertId,Long packageId,String timestamp) {
        return getKey(RedisKey.K85, advertId,packageId,timestamp);
    }

    public static String getTrusteeshipParamsKey() {
        return getKey(RedisKey.K86, "trusteeship_params");
    }

    private static String getKey(RedisKey redisKey, Object... objects) {
        return getKey(NZ_PREFIX, redisKey, objects);
    }

    private static String getKey(String prefix, RedisKey redisKey, Object... objects) {


        Joiner joiner = Joiner.on(SEPARATOR).skipNulls();
        List<Object> items = new ArrayList<>();
        items.add(prefix);
        items.add(redisKey);
        items.addAll(Arrays.stream(objects).map(value -> value == null ? "null" : value).collect(Collectors.toList()));
        return joiner.join(items);

    }

    private static String getNewKey(Long advertId, Long appId, Long materialId, Long advertTimes, Long activityId, Long slotId, Long orientationId, Long tag) {
        StringBuilder sb = new StringBuilder();
        String split = "_";
        if (advertId != null) {
            sb.append(ADVERT.getIndex()).append(split).append(advertId).append(split);
        }
        if (appId != null) {
            sb.append(APP.getIndex()).append(split).append(appId).append(split);
        }
        if (materialId != null) {
            sb.append(MATERIAL.getIndex()).append(split).append(materialId).append(split);
        }
        if (advertTimes != null) {
            sb.append(ADVERT_TIMES.getIndex()).append(split).append(advertTimes).append(split);
        }
        if (activityId != null) {
            sb.append(ACTIVITY.getIndex()).append(split).append(activityId).append(split);
        }
        if (slotId != null) {
            sb.append(SLOT.getIndex()).append(split).append(slotId).append(split);
        }
        if (orientationId != null) {
            sb.append(ORIENTATION.getIndex()).append(split).append(orientationId).append(split);
        }
        if (tag != null) {
            sb.append(FLOW_TAG.getIndex()).append(split).append(tag).append(split);
        }

        String s = sb.toString();
        return s.substring(0, s.length() - split.length());

    }


    private RedisKeyUtil() {
        // 不允许创建实例
    }


    enum DataType {
        //广告
        ADVERT("advert", "1"),

        //媒体
        APP("app", "2"),

        //素材
        MATERIAL("material", "3"),

        //发券次数
        ADVERT_TIMES("advert_times", "4"),

        //活动
        ACTIVITY("activity", "5"),

        //广告位
        SLOT("slot", "6"),

        //广告配置包
        ORIENTATION("orientation", "7"),

        //流量类型
        FLOW_TAG("orientation", "8");

        //维度名称
        private String name;
        //维度对应的编号
        private String index;

        DataType(String name, String index) {
            this.name = name;
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public String getIndex() {
            return index;
        }
    }
}

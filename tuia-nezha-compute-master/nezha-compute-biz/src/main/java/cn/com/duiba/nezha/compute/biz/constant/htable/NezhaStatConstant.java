package cn.com.duiba.nezha.compute.biz.constant.htable;

import cn.com.duiba.nezha.compute.api.enums.StatIntervalTypeEnum;
import org.apache.commons.collections.map.HashedMap;

import java.util.Map;

/**
 * Created by pc on 2017/2/21.
 */
public class NezhaStatConstant {

    // hbase
    public static String TABLE_NAME = "nezhaStat";

    public static String FM_CTR_LAUNCH = "ctr_launch";
    public static String FM_CVR_LAUNCH = "cvr_launch";

    public static String FM_PRE_CTR_ACC = "pre_ctr_acc";
    public static String FM_PRE_CVR_ACC = "pre_cvr_acc";

    public static String FM_STAT_CTR_ACC = "stat_ctr_acc";
    public static String FM_STAT_CVR_ACC = "stat_cvr_acc";

    //
    public static Long DOUBLE_ACC_FACTOR = 100000L;

    public static Long confidenceThreshold =150L;
    //
    public static Map<StatIntervalTypeEnum,Double> statIntervalWeightMap = null;


    static {

        statIntervalWeightMap = new HashedMap();
        statIntervalWeightMap.put(StatIntervalTypeEnum.CURRENT_HOUR,0.3);
        statIntervalWeightMap.put(StatIntervalTypeEnum.RECENT_1_HOUR,0.3);
        statIntervalWeightMap.put(StatIntervalTypeEnum.CURRENT_DAY,0.2);
        statIntervalWeightMap.put(StatIntervalTypeEnum.RECENT_1_DAY,0.1);
        statIntervalWeightMap.put(StatIntervalTypeEnum.RECENT_2_DAY,0.1);

    }

}

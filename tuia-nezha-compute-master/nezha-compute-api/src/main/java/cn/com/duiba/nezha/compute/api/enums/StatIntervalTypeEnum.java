package cn.com.duiba.nezha.compute.api.enums;

/**
 * Created by xuezhaoming on 16/8/2.
 */
public enum StatIntervalTypeEnum {


    CURRENT_DAY(0, "cd"), //"当日"
    RECENT_1_DAY(1, "r1d"), //"1天"
    RECENT_2_DAY(2, "r2d"), //"2日"
    RECENT_3_DAY(3, "r3d"), //"3日"
    RECENT_4_DAY(4, "r4d"), //"4日"
    RECENT_5_DAY(5, "r5d"), //"5日"
    RECENT_6_DAY(6, "r6d"), //"6日"
    RECENT_7_DAY(7, "r7d"), //"1周"

    CURRENT_HOUR(0, "ch"), //"当小时"
    RECENT_1_HOUR(1, "r1h"), //"近1小时"
    RECENT_2_HOUR(2, "r2h"), //"近2小时"
    RECENT_3_HOUR(3, "r3h"), //"近3小时"
    RECENT_4_HOUR(4, "r4h"), //"近4小时"
    RECENT_5_HOUR(5, "r5h"), //"近5小时"
    RECENT_6_HOUR(6, "r6h"), //"近6小时"

    ;

    private int index;

    private String desc;

    StatIntervalTypeEnum(int index, String desc) {
        this.index = index;
        this.desc = desc;
    }

    public int getIndex() {
        return index;
    }

    public String getDesc() {
        return desc;
    }
}

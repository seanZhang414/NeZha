package cn.com.duiba.nezha.engine.api.enums;

/**
 * 统计数据范围类型
 * Created by lwj on 16/8/2.
 */
public enum StatTypeEnum {

    STAT_TYPE_RECENT_14(0, "数据统计,近14日数据"),
    STAT_TYPE_RECENT_180(1, "数据统计,近180日数据"),
    ;

    private long index;

    private String desc;

    StatTypeEnum(long index, String desc) {
        this.index = index;
        this.desc = desc;
    }

    public long getIndex() {
        return index;
    }

    public String getDesc() {
        return desc;
    }
}

package cn.com.duiba.nezha.compute.api.enums;

/**
 * Created by xuezhaoming on 16/8/2.
 */
public enum StatAppTypeEnum {

    MODEL(1, "model"), //"模型统计"
    CORRECT(1, "correct"), //"纠偏"
    FEATURE(2, "feature"), //"模型特征"
    ;
    private int index;
    private String desc;
    StatAppTypeEnum(int index, String desc) {
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

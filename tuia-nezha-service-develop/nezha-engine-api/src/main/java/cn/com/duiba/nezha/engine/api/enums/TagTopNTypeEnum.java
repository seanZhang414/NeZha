package cn.com.duiba.nezha.engine.api.enums;

/**
 * 干预规则
 * Created by lwj on 16/8/2.
 */
public enum TagTopNTypeEnum {

    CTR_TYPE(1, "根据标签CTR值排序"),

    QUALITY_TYPE(2, "根据标签质量分排序"),

    FINAL_TYPE(3, "根据综合得分排序");


    private  int type;

    private String desc;

    TagTopNTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }



    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}

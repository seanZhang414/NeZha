package cn.com.duiba.nezha.compute.api.enums;

/**
 * Created by xuezhaoming on 16/8/2.
 */
public enum StatTypeEnum {


    ADVERT_AND_MATERIAL(0, ""),//"广告 && 素材统计"
    ADVERT(1, ""),//"广告统计"
    MATERIAL(2, "2"), //"素材统计"
    ;

    private int index;

    private String desc;

    StatTypeEnum(int index, String desc) {
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

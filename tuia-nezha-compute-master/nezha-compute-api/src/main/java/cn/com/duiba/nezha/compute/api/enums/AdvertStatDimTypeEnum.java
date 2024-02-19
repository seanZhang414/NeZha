package cn.com.duiba.nezha.compute.api.enums;

/**
 * Created by xuezhaoming on 16/8/2.
 */
public enum AdvertStatDimTypeEnum {

    GLOBAL(1, "1"),//"全局"
    APP(2, "2"), //"媒体"
    ACTIVITY(3, "3"), //"活动"
    ;

    private int index;

    private String desc;

    AdvertStatDimTypeEnum(int index, String desc) {
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

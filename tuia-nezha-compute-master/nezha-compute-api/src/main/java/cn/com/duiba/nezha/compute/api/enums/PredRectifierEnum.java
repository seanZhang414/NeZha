package cn.com.duiba.nezha.compute.api.enums;

/**
 * Created by xuezhaoming on 16/8/2.
 */
public enum PredRectifierEnum {


    COR(0, "Correction"),
    REC(1, "Reconstruction"),
    ;

    private int index;

    private String desc;

    PredRectifierEnum(int index, String desc) {
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

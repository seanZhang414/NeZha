package cn.com.duiba.nezha.compute.feature.enums;

/**
 * Created by pc on 2017/1/10.
 */
public enum FeatureCodeEnum {

    C001(0,"c001"),
    C002(1,"c002");


    private int index;
    private String name;

    FeatureCodeEnum(int index, String value) {
        this.index =index;
        this.name = name;
    }

    public int getIndex() {
        return this.index;
    }
    public String getName() {
        return this.name;
    }
}
package cn.com.duiba.nezha.engine.api.enums;

/**
 * 统计维度类型
 * Created by lwj on 16/8/2.
 */
public enum AdvertMaterialGroupTypeEnum {

    WITH_WEIGHT_MATERIAL_TYPE(1, "含权重标签组"),
    WITHOUT_WEIGHT_MATERIAL_TYPE(0, "无权重标签组")
    ;

    private  long index;
    private String desc;

    AdvertMaterialGroupTypeEnum(long index, String desc) {
        this.index = index;
        this.desc = desc;
    }

    public  long getIndex() {
        return index;
    }

    public String getDesc() {
        return desc;
    }
}

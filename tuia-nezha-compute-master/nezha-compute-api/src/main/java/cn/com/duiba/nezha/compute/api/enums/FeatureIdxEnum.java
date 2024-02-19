package cn.com.duiba.nezha.compute.api.enums;

/**
 * Created by xuezhaoming on 16/8/2.
 */
public enum FeatureIdxEnum {

    FEATURE_IDX_001("001", "opt"),// 参数优化
    FEATURE_IDX_MAP_001("001", "opt"),// 参数优化
    ;

    private String index;

    private String desc;

    FeatureIdxEnum(String index, String desc) {
        this.index = index;
        this.desc = desc;
    }

    public String  getIndex() {
        return index;
    }

    public String getDesc() {
        return desc;
    }
}

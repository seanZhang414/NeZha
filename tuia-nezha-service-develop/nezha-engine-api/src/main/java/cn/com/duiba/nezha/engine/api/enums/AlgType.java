package cn.com.duiba.nezha.engine.api.enums;

/**
 * 算法类型
 * Created by lwj on 16/8/2.
 */
public enum AlgType {

    SC(1, "统计算法"),
    PC(3, "预估算法"),;

    private Integer type;

    private String desc;


    AlgType(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }


    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}

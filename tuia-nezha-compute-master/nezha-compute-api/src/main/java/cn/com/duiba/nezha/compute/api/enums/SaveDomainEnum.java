package cn.com.duiba.nezha.compute.api.enums;

/**
 * Created by xuezhaoming on 16/8/2.
 */
public enum SaveDomainEnum {

    MODEL(1, "model"),// 模型
    RCMD(2, "rcmd"), // 推荐
    PARAMS(3, "params"), // 参数
    ;

    private int index;

    private String desc;

    SaveDomainEnum(int index, String desc) {
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

package cn.com.duiba.nezha.engine.api.enums;

public enum RecommendType {
    SHOW("show"),

    INTERACT("interact"),
    ;
    private String desc;

    RecommendType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}

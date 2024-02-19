package cn.com.duiba.nezha.engine.api.enums;

public enum ModelType {

    FM("FM"), LR("LR"), DEEP("深度学习");

    private String desc;

    ModelType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}

package cn.com.duiba.nezha.engine.api.enums;

public enum FlowTag {

    // 0=普通流量 1=劣质流量  2=优质流量
    NORMAL(0L), BAD(1L), GOOD(2L);

    private Long tag;

    FlowTag(Long tag) {
        this.tag = tag;
    }

    public Long getTag() {
        return tag;
    }
}

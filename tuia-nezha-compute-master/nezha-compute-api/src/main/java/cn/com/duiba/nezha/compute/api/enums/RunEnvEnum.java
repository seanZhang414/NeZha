package cn.com.duiba.nezha.compute.api.enums;

/**
 * Created by xuezhaoming on 16/8/2.
 */
public enum RunEnvEnum {

    ENV_OPT(1, "opt"),// 参数优化
    ENV_RCMD(2, "rcmd"), // 推荐
    ENV_EVA(3, "eva"), // 模型评估
    ENV_PRED(4, "pred"), // 模型预测
    ENV_REPLAY(5, "replay"), // 模型评估
    ;

    private int index;

    private String desc;

    RunEnvEnum(int index, String desc) {
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

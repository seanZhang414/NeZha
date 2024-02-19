package cn.com.duiba.nezha.engine.api.enums;

/**
 * 错误码
 * Created by liwenjin on 16/8/2.
 */
public enum ResultCodeEnum {

    NZ_0202009("NZ_0202009", "广告推荐－推荐候选－业务级－未知异常"),
    NZ_0202006("NZ_0202006", "广告推荐－推荐候选－业务级－输入参数非法"),
    NZ_0302007("NZ_0302007", "广告推荐－推荐候选融合－业务级－集合合并失败"),
    NZ_0302008("NZ_0302008", "广告推荐－推荐候选融合－业务级－集合去重失败"),
    NZ_0502008("NZ_0502008", "广告推荐－重排序－业务级－未知异常"),
    NZ_0202005("NZ_0202005", "广告推荐－推荐候选－业务级－entity集合转换成Vo集合"),
    PARAMS_INVALID("params invalid","params invalid"),
    STRATEGY_ID_NOT_EXIST("NZ_0502009","策略ID不存在"),
    ;

    private String code;

    private  String desc;

    ResultCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

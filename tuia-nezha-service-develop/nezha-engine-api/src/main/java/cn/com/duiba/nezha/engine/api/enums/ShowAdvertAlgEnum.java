package cn.com.duiba.nezha.engine.api.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public enum ShowAdvertAlgEnum implements AdvertAlgEnum {

    SHOW_AND_PC_1(301, "PC,展示广告", AlgType.PC);

    private Integer type;

    private String desc;

    private AlgType algType;


    ShowAdvertAlgEnum(Integer type, String desc, AlgType algType) {
        this.type = type;
        this.desc = desc;
        this.algType = algType;
    }

    private static final Map<String, ShowAdvertAlgEnum> CACHE = Arrays.stream(ShowAdvertAlgEnum.values())
            .collect(toMap(showAdvertAlgEnum -> showAdvertAlgEnum.getType().toString(), Function.identity()));

    public static AdvertAlgEnum get(String strategyId) {
        return CACHE.get(strategyId);
    }

    public String getDesc() {
        return this.desc;
    }

    @Override
    public Integer getType() {
        return this.type;
    }

    @Override
    public AlgType getAlgType() {
        return this.algType;
    }
}

package cn.com.duiba.nezha.engine.api.enums;

import java.util.Arrays;

/**
 * 广告计费类型
 *
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: ChargeTypeEnum.java , v 0.1 2017/4/27 下午2:49 ZhouFeng Exp $
 */
public enum ChargeTypeEnum {

    CPC(1),

    CPA(2),;


    private Integer value;

    ChargeTypeEnum(int value) {
        this.value = value;
    }


    public Integer getValue() {
        return value;
    }


    /**
     * 通过value创建一个枚举
     *
     * @param value
     * @return
     */
    public static ChargeTypeEnum create(Integer value) {
        if (value == null) {
            return null;
        }

        return Arrays.stream(values()).filter(type -> type.getValue().equals(value)).findFirst().orElse(null);
    }
}

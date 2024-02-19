package cn.com.duiba.nezha.engine.biz.constant;

import com.google.common.base.Splitter;

import java.util.function.BinaryOperator;

/**
 * Created by pc on 2016/11/21.
 */
public class GlobalConstant {

    public static String LR_MODEL_ES_TYPE = "lr_model";

    // 新广告发券判断阈值.全局小于1000为新广告
    public static final long NEW_ADD_AD_THRESHOLD = 1000L;

    // 默认点击率
    public static final double INTERACT_DEFAULT_CTR = 0.2;
    public static final double SHOW_DEFAULT_CTR = 0.01;

    // 默认转化率
    public static final double INTERACT_DEFAULT_CVR = 0.02;
    public static final double SHOW_DEFAULT_CVR = 0.1;

    // 默认广告权重
    public static final double DEFAULT_ADVERT_WEIGHT = 1D;

    public static final Splitter SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();

    /**
     * copy {@link java.util.stream.Collectors}
     * method {@link java.util.stream.Collectors#throwingMerger
     */
    public static <T> BinaryOperator<T> throwingMerger() {
        return (u,v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); };
    }

    private GlobalConstant() {
        //不可实例化类
    }

}

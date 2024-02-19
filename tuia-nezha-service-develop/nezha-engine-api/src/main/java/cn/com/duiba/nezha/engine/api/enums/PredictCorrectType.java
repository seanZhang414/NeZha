package cn.com.duiba.nezha.engine.api.enums;

/**
 * 纠偏类型
 *
 * @author ElinZhou
 * @version $Id: PredictCorrectType.java , v 0.1 2017/9/14 下午3:04 ElinZhou Exp $
 */
public enum PredictCorrectType {

    /**
     * 纠偏(旧)
     */
    CORRECT,

    /**
     * 纠偏(新-方式1)
     */
    CORRECT_NEW1,

    /**
     * 纠偏(新-方式2)
     */
    CORRECT_NEW2,

    /**
     * 纠偏&分布重构
     */
    CORRECT_REFACTOR,


    /**
     * 不纠偏
     */
    NONE


}

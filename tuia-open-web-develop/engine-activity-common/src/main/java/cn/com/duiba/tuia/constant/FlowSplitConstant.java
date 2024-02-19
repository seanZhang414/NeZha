package cn.com.duiba.tuia.constant;

/**
 * @author xuyenan
 * @createTime 2017/1/24
 */
public class FlowSplitConstant {

    /** 引擎投放 */
    public static final int ACT_ENGINE_OUTPUT   = 1;

    /** 人工投放 */
    public static final int ACT_MANUAL_OUTPUT   = 2;

    /** 试投 */
    public static final int ACT_NEW_OUTPUT      = 3;

    /** 定制列表投放 */
    public static final int ACT_CUSTOM_OUTPUT   = 4;

    /** 重投上一个活动 */
    public static final int ACT_LAST_OUTPUT     = 5;
    
    /** 引擎策略2投放 */
    public static final int ACT_ENGINE2_OUTPUT  = 6;
    
    /** RPM引擎策略投放 */
    public static final int ACT_RPM_ENGINE_OUTPUT = 7;
    
    /** RPM2算法引擎策略投放 */
    public static final int ACT_RPM2_ENGINE_OUTPUT = 8;

    /** RPM3主会场算法引擎策略投放 */
    public static final int ACT_RPM3_ENGINE_OUTPUT = 9;

    /** RPM2限定活动算法引擎策略投放 */
    public static final int ACT_RPM2_ENGINE_LIMIT_OUTPUT = 10;

    /** RPM2新算法引擎策略投放(手投) */
    public static final int ACT_RPM2_ENGINE_OUTPUT_NEW_MANUAL = 11;

    /** RPM2新算法引擎策略投放 */
    public static final int ACT_RPM2_ENGINE_OUTPUT_NEW = 12;
    
    /** 手投无素材引擎策略投放 */
    public static final int MANUAL_ACT_ENGINE_OUTPUT = 1;
    
    /** 手投算法引擎策略投放 */
    public static final int MANUAL_ACT_ENGINE2_OUTPUT = 2;
    
    /** 引擎/人工列表投放 */
    public static final int ENGINE_MANUAL_OUTPUT = 1;
    
    /** 全引擎算法投放 */
    public static final int ENGINE_ALL_OUTPUT = 2;

    /** 新素材投放 */
    public static final int NEW_MATERIAL_OUTPUT = 1;

    /** 老素材投放 */
    public static final int OLD_MATERIAL_OUTPUT = 2;

    /** 人工列表投放 */
    public static final int MANUAL_OUTPUT = 1;

    /** 全引擎算法投放 */
    public static final int ENGINE_OUTPUT = 2;

    /** 全引擎限定活动算法投放 */
    public static final int ENGINE_LIMIT_OUTPUT = 3;

    private FlowSplitConstant() {
    }
}

package cn.com.duiba.nezha.engine.api.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

/**
 * 策略类型
 */
public enum InteractAdvertAlgEnum implements AdvertAlgEnum {

    BTM_AND_SC_6(106, "SC,小时维度", AlgType.SC),
    BTM_AND_SC_7(107, "SC,小时维度,优先选择素材", AlgType.SC),

    BTM_AND_PC_1(201, "PC,LR模型", AlgType.PC),
    BTM_AND_PC_2(202, "PC,FM模型", AlgType.PC),
    BTM_AND_PC_6(206, "PC,FM模型,预估纠偏", AlgType.PC),
    BTM_AND_PC_7(207, "PC,FM模型,预估纠偏,分布重构", AlgType.PC),
    BTM_AND_PC_8(208, "PC,FM模型 无广告和素材标签", AlgType.PC),
    BTM_AND_PC_9(209, "PC,FM模型 精简特征", AlgType.PC),
    BTM_AND_PC_10(210, "PC,小时维度,优先选择素材,FM 模型", AlgType.PC),
    BTM_AND_PC_11(211, "PC,LR V7模型", AlgType.PC),
    BTM_AND_PC_12(212, "PC,LR V8模型", AlgType.PC),
    BTM_AND_PC_13(213, "PC,FM 多维度质量分", AlgType.PC),
    BTM_AND_PC_14(214, "PC,FM 2017Q4-特征扩充", AlgType.PC),
    BTM_AND_PC_15(215, "PC,FM app安装列表及行业标签", AlgType.PC),
    BTM_AND_PC_16(216, "PC,FM app安装列表及行业标签,基础精简特征", AlgType.PC),
    BTM_AND_PC_17(217, "PC,FM模型+智能策略", AlgType.PC),
    BTM_AND_PC_18(218, "PC,FM_CVR_MODEL_v610", AlgType.PC),
    BTM_AND_PC_20(220, "PC,用户行为偏好", AlgType.PC),

    BTM_AND_PC_21(221, "PC,在线学习模型V001", AlgType.PC),
    BTM_AND_PC_22(222, "PC,用户社会属性", AlgType.PC),
    BTM_AND_PC_23(223, "PC,在线学习模型V002", AlgType.PC),
    BTM_AND_PC_24(224, "PC,在线学习模型V003", AlgType.PC),
    BTM_AND_PC_25(225, "PC,在线学习-用户行为特征", AlgType.PC),
    BTM_AND_PC_26(226, "PC,在线学习-预估值权重优化", AlgType.PC),
    BTM_AND_PC_27(227, "PC,app安装列表优化模型", AlgType.PC),
    BTM_AND_PC_28(228, "PC,用户行为-计数特征", AlgType.PC),
    BTM_AND_PC_29(229, "PC,FM模型后端优化", AlgType.PC),
    BTM_AND_PC_30(230, "PC,用户行为统计特征", AlgType.PC),
    BTM_AND_PC_31(231, "PC,在线学习批量训练", AlgType.PC),
    BTM_AND_PC_32(232, "PC,深度学习fnn1", AlgType.PC),
    BTM_AND_PC_33(233, "PC,深度学习deepFm1", AlgType.PC),
    BTM_AND_PC_34(234, "PC,新素材推荐", AlgType.PC),
    BTM_AND_PC_35(235, "PC,纠偏2", AlgType.PC),
    BTM_AND_PC_36(236, "PC,纠偏3", AlgType.PC),
    BTM_AND_PC_37(237, "PC,广告位媒体标签", AlgType.PC),
    BTM_AND_PC_38(238, "PC,素材图片特征", AlgType.PC),
    BTM_AND_PC_39(239, "PC,深度学习opnn", AlgType.PC),
    BTM_AND_PC_40(240, "PC,活动特征", AlgType.PC),
    BTM_AND_PC_41(241, "PC,深度学习fnn2", AlgType.PC),
    BTM_AND_PC_42(242, "PC,深度学习deepFm2", AlgType.PC),
    BTM_AND_PC_43(243, "PC,深度学习dcn1", AlgType.PC),
    BTM_AND_PC_44(244, "PC,深度学习dcn2", AlgType.PC),
    BTM_AND_PC_45(245, "PC,深度学习dcn3", AlgType.PC),
    BTM_AND_PC_46(246, "PC,深度学习xDeepFm2", AlgType.PC),
    BTM_AND_PC_47(247, "PC,深度学习xDeepFm3", AlgType.PC),
    ;


    private Integer type;

    private String desc;

    private AlgType algType;


    private static final Map<String, InteractAdvertAlgEnum> CACHE = Arrays.stream(InteractAdvertAlgEnum.values())
            .collect(toMap(AdvertAlgTypeEnum -> AdvertAlgTypeEnum.getType().toString(), Function.identity()));

    InteractAdvertAlgEnum(Integer type, String desc, AlgType algType) {
        this.type = type;
        this.desc = desc;
        this.algType = algType;
    }


    public static AdvertAlgEnum get(String strategyId) {
        return CACHE.get(strategyId);
    }


    public Integer getType() {

        return type;
    }

    public String getDesc() {
        return desc;
    }

    public AlgType getAlgType() {
        return algType;
    }

}

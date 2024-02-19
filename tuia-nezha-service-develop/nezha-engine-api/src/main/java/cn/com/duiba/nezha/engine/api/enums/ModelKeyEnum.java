package cn.com.duiba.nezha.engine.api.enums;

public enum ModelKeyEnum {

    //LR_CTR
    LR_CTR_MODEL_v004("mid_lr_ctr_v004", ModelType.LR, false),
    LR_CTR_MODEL_v007("mid_lr_ctr_v007", ModelType.LR, false),
    LR_CTR_MODEL_v008("mid_lr_ctr_v008", ModelType.LR, false),

    //LR_CVR
    LR_CVR_MODEL_v004("mid_lr_cvr_v004", ModelType.LR, false),
    LR_CVR_MODEL_v007("mid_lr_cvr_v007", ModelType.LR, false),
    LR_CVR_MODEL_v008("mid_lr_cvr_v008", ModelType.LR, false),

    //FM_CTR
    FM_CTR_MODEL_v003("mid_fm_ctr_v003", ModelType.FM, false),
    FM_CTR_MODEL_v004("mid_fm_ctr_v004", ModelType.FM, false),
    FM_CTR_MODEL_v005("mid_fm_ctr_v005", ModelType.FM, false),
    FM_CTR_MODEL_v006("mid_fm_ctr_v006", ModelType.FM, false),
    FM_CTR_MODEL_v007("mid_fm_ctr_v007", ModelType.FM, false),
    FM_CTR_MODEL_v600("mid_fm_ctr_v600", ModelType.FM, false),
    FM_CTR_MODEL_v601("mid_fm_ctr_v601", ModelType.FM, false),
    FM_CTR_MODEL_v602("mid_fm_ctr_v602", ModelType.FM, false),
    FM_CTR_MODEL_v603("mid_fm_ctr_v603", ModelType.FM, false),

    FM_CTR_MODEL_v610("mid_fm_ctr_v610", ModelType.FM, false),
    FM_CTR_MODEL_v611("mid_fm_ctr_v611", ModelType.FM, false),
    FM_CTR_MODEL_v612("mid_fm_ctr_v612", ModelType.FM, false),
    FM_CTR_MODEL_v613("mid_fm_ctr_v613", ModelType.FM, false),
    FM_CTR_MODEL_v614("mid_fm_ctr_v614", ModelType.FM, false),

    FM_CTR_MODEL_v501("mid_fm_ctr_v501", ModelType.FM, false),
    FM_CTR_MODEL_v502("mid_fm_ctr_v502", ModelType.FM, false),

    //FM_CVR
    FM_CVR_MODEL_v003("mid_fm_cvr_v003", ModelType.FM, false),
    FM_CVR_MODEL_v004("mid_fm_cvr_v004", ModelType.FM, false),
    FM_CVR_MODEL_v005("mid_fm_cvr_v005", ModelType.FM, false),
    FM_CVR_MODEL_v006("mid_fm_cvr_v006", ModelType.FM, false),
    FM_CVR_MODEL_v007("mid_fm_cvr_v007", ModelType.FM, false),
    FM_CVR_MODEL_v600("mid_fm_cvr_v600", ModelType.FM, false),
    FM_CVR_MODEL_v601("mid_fm_cvr_v601", ModelType.FM, false),
    FM_CVR_MODEL_v602("mid_fm_cvr_v602", ModelType.FM, false),
    FM_CVR_MODEL_v603("mid_fm_cvr_v603", ModelType.FM, false),

    FM_CVR_MODEL_v501("mid_fm_cvr_v501", ModelType.FM, false),
    FM_CVR_MODEL_v502("mid_fm_cvr_v502", ModelType.FM, false),

    FM_CVR_MODEL_v610("mid_fm_cvr_v610", ModelType.FM, false),
    FM_CVR_MODEL_v611("mid_fm_cvr_v611", ModelType.FM, false),
    FM_CVR_MODEL_v612("mid_fm_cvr_v612", ModelType.FM, false),
    FM_CVR_MODEL_v613("mid_fm_cvr_v613", ModelType.FM, false),
    FM_CVR_MODEL_v614("mid_fm_cvr_v614", ModelType.FM, false),

    //
    FM_BE_CVR_MODEL_v001("mid_fm_be_cvr_v001", ModelType.FM, false),


    //FM_CTR
    FTRL_FM_CTR_MODEL_v001("mid_ftrl_fm_ctr_v001", ModelType.FM, true),
    FTRL_FM_CTR_MODEL_v002("mid_ftrl_fm_ctr_v002", ModelType.FM, true),
    FTRL_FM_CTR_MODEL_v003("mid_ftrl_fm_ctr_v003", ModelType.FM, true),
    FTRL_FM_CTR_MODEL_v004("mid_ftrl_fm_ctr_v004", ModelType.FM, true),
    FTRL_FM_CTR_MODEL_v005("mid_ftrl_fm_ctr_v005", ModelType.FM, true),
    FTRL_FM_CTR_MODEL_v006("mid_ftrl_fm_ctr_v006", ModelType.FM, true),
    FTRL_FM_CTR_MODEL_v007("mid_ftrl_fm_ctr_v007", ModelType.FM, true),
    FTRL_FM_CTR_MODEL_v008("mid_ftrl_fm_ctr_v008", ModelType.FM, true),
    FTRL_FM_CTR_MODEL_v009("mid_ftrl_fm_ctr_v009", ModelType.FM, true),

    //FM_CVR
    FTRL_FM_CVR_MODEL_v001("mid_ftrl_fm_cvr_v001", ModelType.FM, true),
    FTRL_FM_CVR_MODEL_v002("mid_ftrl_fm_cvr_v002", ModelType.FM, true),
    FTRL_FM_CVR_MODEL_v003("mid_ftrl_fm_cvr_v003", ModelType.FM, true),
    FTRL_FM_CVR_MODEL_v004("mid_ftrl_fm_cvr_v004", ModelType.FM, true),
    FTRL_FM_CVR_MODEL_v005("mid_ftrl_fm_cvr_v005", ModelType.FM, true),
    FTRL_FM_CVR_MODEL_v006("mid_ftrl_fm_cvr_v006", ModelType.FM, true),
    FTRL_FM_CVR_MODEL_v007("mid_ftrl_fm_cvr_v007", ModelType.FM, true),
    FTRL_FM_CVR_MODEL_v008("mid_ftrl_fm_cvr_v008", ModelType.FM, true),
    FTRL_FM_CVR_MODEL_v009("mid_ftrl_fm_cvr_v009", ModelType.FM, true),
    ;

    private String index;

    private ModelType modelType;

    private Boolean online;

    public String getIndex() {
        return index;
    }

    public ModelType getModelType() {
        return modelType;
    }

    public Boolean getOnline() {
        return online;
    }

    ModelKeyEnum(String index, ModelType modelType, Boolean online) {
        this.index = index;
        this.modelType = modelType;
        this.online = online;
    }
}

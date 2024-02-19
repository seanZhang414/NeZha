package cn.com.duiba.nezha.compute.feature.enums;

/**
 * Created by xuezhaoming on 16/8/2.
 */
public enum ModelKeyEnum {


    //FM_CTR
    FTRL_FM_CTR_MODEL_v001("mid_ftrl_fm_ctr_v001", "fm_ctr_v001_0517", true, "20180206"),
    FTRL_FM_CTR_MODEL_v002("mid_ftrl_fm_ctr_v002", "fm_ctr_v002_0626", true, "20180626"),
    FTRL_FM_CTR_MODEL_v003("mid_ftrl_fm_ctr_v003", "fm_ctr_v003_0626", true, "20180626"),
    FTRL_FM_CTR_MODEL_v004("mid_ftrl_fm_ctr_v004", "fm_ctr_v004"     , true, "20180321"),
    FTRL_FM_CTR_MODEL_v005("mid_ftrl_fm_ctr_v005", "fm_ctr_v005_0626", true, "20180626"),
    //FM_CVR
    FTRL_FM_CVR_MODEL_v001("mid_ftrl_fm_cvr_v001", "fm_cvr_v001_0517", false, "20180206"),
    FTRL_FM_CVR_MODEL_v002("mid_ftrl_fm_cvr_v002", "fm_cvr_v002_0626", false, "20180626"),
    FTRL_FM_CVR_MODEL_v003("mid_ftrl_fm_cvr_v003", "fm_cvr_v003_0626", false, "20180626"),
    FTRL_FM_CVR_MODEL_v004("mid_ftrl_fm_cvr_v004", "fm_cvr_v004"     , false, "20180321"),
    FTRL_FM_CVR_MODEL_v005("mid_ftrl_fm_cvr_v005", "fm_cvr_v005_0626", false, "20180626"),

    FTRL_FM_CVR_MODEL_test("mid_ftrl_fm_cvr_test", "fm_cvr_test_0009", false, "20180321"),

    ;
    private String index;

    private String featureIndex;

    private boolean isCtr;
    private String desc;

    ModelKeyEnum(String index, String featureIndex, boolean isCtr, String desc) {
        this.index = index;
        this.featureIndex = featureIndex;
        this.isCtr = isCtr;
        this.desc = desc;
    }

    public String getIndex() {
        return index;
    }

    public String getFeatureIndex() {
        return featureIndex;
    }

    public boolean getIsCtr() {
        return isCtr;
    }


    public String getDesc() {
        return desc;
    }
}

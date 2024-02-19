package cn.com.duiba.nezha.compute.api.dto;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class CorrectionInfo implements Serializable {
    private static final long serialVersionUID = -316102112618444133L;


    private Long advertId; //广告ID
    private Long type; //1:CTR 2:CVR
    private Double currentPreValue ; //模型预估值
    private NezhaStatDto nezhaStatDto ; //预估值纠偏对象
    private Double correctionFactor; //默认值 1.0
    private Double reconstructionFactor; //默认值 1.0



    public Long getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Long advertId) {
        this.advertId = advertId;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }


    public Double getCurrentPreValue() {
        return currentPreValue;
    }

    public void setCurrentPreValue(Double currentPreValue) {
        this.currentPreValue = currentPreValue;
    }


    public NezhaStatDto getNezhaStatDto() {
        return nezhaStatDto;
    }

    public void setNezhaStatDto(NezhaStatDto nezhaStatDto) {
        this.nezhaStatDto = nezhaStatDto;
    }


    public Double getCorrectionFactor() {
        return correctionFactor;
    }

    public void setCorrectionFactor(Double correctionFactor) {
        this.correctionFactor = correctionFactor;
    }


    public Double getReconstructionFactor() {
        return reconstructionFactor;
    }

    public void setReconstructionFactor(Double reconstructionFactor) {
        this.reconstructionFactor = reconstructionFactor;
    }


}

package cn.com.duiba.nezha.compute.api.dto;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class ModelPredStatDto implements Serializable {
    private static final long serialVersionUID = -316102112618444133L;


    private String algType;
    private Long cpcCnt;
    private Long ocpcCnt;
    private Double preCtrs;
    private Double preCvrs;
    private Double avgPreCtr;
    private Double avgPreCvr;

    private String time;


    public String getAlgType() {
        return algType;
    }

    public void setAlgType(String algType) {
        this.algType = algType;
    }


    public Long getCpcCnt() {
        return cpcCnt;
    }

    public void setCpcCnt(Long cpcCnt) {
        this.cpcCnt = cpcCnt;
    }

    public Long getOcpcCnt() {
        return ocpcCnt;
    }

    public void setOcpcCnt(Long ocpcCnt) {
        this.ocpcCnt = ocpcCnt;
    }


    public Double getPreCtrs() {
        return preCtrs;
    }

    public void setPreCtrs(Double preCtrs) {
        this.preCtrs = preCtrs;
    }


    public Double getPreCvrs() {
        return preCvrs;
    }

    public void setPreCvrs(Double preCvrs) {
        this.preCvrs = preCvrs;
    }


    public Double getAvgPreCtr() {
        return avgPreCtr;
    }

    public void setAvgPreCtr(Double avgPreCtr) {
        this.avgPreCtr = avgPreCtr;
    }


    public Double getAvgPreCvr() {
        return avgPreCvr;
    }

    public void setAvgPreCvr(Double avgPreCvr) {
        this.avgPreCvr = avgPreCvr;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}

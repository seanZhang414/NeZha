package cn.com.duiba.nezha.compute.api.dto;

import cn.com.duiba.nezha.compute.api.enums.StatIntervalTypeEnum;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class NezhaStatMergeDto implements Serializable {

    private static final long serialVersionUID = -316102112618444133L;

    private String key;
    private Long algType;
    private Long appId;
    private Long advertId;

    private Double mergePreCtr;
    private Double mergePreCvr;

    private Double mergeStatCtr;
    private Double mergeStatCvr;


    private NezhaStatDto chNezhaStatDto;

    private NezhaStatDto r1hNezhaStatDto;

    private NezhaStatDto cdNezhaStatDto;

    private NezhaStatDto r1dNezhaStatDto;

    private NezhaStatDto r2dNezhaStatDto;



    public String getKey() {return key;}

    public void setKey(String key) {this.key = key;}



    public Long getAlgType() {
        return algType;
    }

    public void setAlgType(Long algType) {
        this.algType = algType;
    }


    public Long getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Long advertId) {
        this.advertId = advertId;
    }




    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }




    public NezhaStatDto getChNezhaStatDto() {
        return chNezhaStatDto;
    }

    public void setChNezhaStatDto(NezhaStatDto chNezhaStatDto) {
        this.chNezhaStatDto = chNezhaStatDto;
    }

    public NezhaStatDto getR1hNezhaStatDto() {
        return r1hNezhaStatDto;
    }

    public void setR1hNezhaStatDto(NezhaStatDto r1hNezhaStatDto) {
        this.r1hNezhaStatDto = r1hNezhaStatDto;
    }



    public NezhaStatDto getR1dNezhaStatDto() {
        return r1dNezhaStatDto;
    }

    public void setR1dNezhaStatDto(NezhaStatDto r1dNezhaStatDto) {
        this.r1dNezhaStatDto = r1dNezhaStatDto;
    }




    public NezhaStatDto getCdNezhaStatDto() {
        return cdNezhaStatDto;
    }

    public void setCdNezhaStatDto(NezhaStatDto cdNezhaStatDto) {
        this.cdNezhaStatDto = cdNezhaStatDto;
    }


    public NezhaStatDto getR2dNezhaStatDto() {
        return r2dNezhaStatDto;
    }

    public void setR2dNezhaStatDto(NezhaStatDto r2dNezhaStatDto) {
        this.r2dNezhaStatDto = r2dNezhaStatDto;
    }


    public Double getMergePreCtr() {
        return mergePreCtr;
    }

    public void setMergePreCtr(Double mergePreCtr) {
        this.mergePreCtr = mergePreCtr;
    }

    public Double getMergePreCvr() {
        return mergePreCvr;
    }
    public void setMergePreCvr(Double mergePreCvr) {
        this.mergePreCvr = mergePreCvr;
    }


    public Double getMergeStatCtr() {
        return mergeStatCtr;
    }

    public void setMergeStatCtr(Double mergeStatCtr) {
        this.mergeStatCtr = mergeStatCtr;
    }

    public Double getMergeStatCvr() {
        return mergeStatCvr;
    }
    public void setMergeStatCvr(Double mergeStatCvr) {
        this.mergeStatCvr = mergeStatCvr;
    }





}

package cn.com.duiba.nezha.compute.api.dto;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class AdvertMergeStatDto implements Serializable {
    private static final long serialVersionUID = -316102112618444139L;


    private String id;
    private Long advertId;
    private Long materialId;
    private Long launchCnt;
    private Long chargeCnt;
    private Long actExpCnt;
    private Long actClickCnt;
    private Double mergeCtr;
    private Double mergeCvr;
    private String time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Long getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Long advertId) {
        this.advertId = advertId;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }


    public Long getLaunchCnt() {
        return launchCnt;
    }

    public void setLaunchCnt(Long launchCnt) {
        this.launchCnt = launchCnt;
    }


    public Long getChargeCnt() {
        return chargeCnt;
    }

    public void setChargeCnt(Long chargeCnt) {
        this.chargeCnt = chargeCnt;
    }


    public Long getActClickCnt() {
        return actClickCnt;
    }

    public void setActClickCnt(Long actClickCnt) {
        this.actClickCnt = actClickCnt;
    }

    public Long getActExpCnt() {
        return actExpCnt;
    }

    public void setActExpCnt(Long actExpCnt) {
        this.actExpCnt = actExpCnt;
    }


    public Double getMergeCtr() {
        return mergeCtr;
    }

    public void setMergeCtr(Double mergeCtr) {
        this.mergeCtr = mergeCtr;
    }


    public Double getMergeCvr() {
        return mergeCvr;
    }

    public void setMergeCvr(Double mergeCvr) {
        this.mergeCvr = mergeCvr;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}

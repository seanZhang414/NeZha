package cn.com.duiba.nezha.compute.api.dto;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class AdvertRStatDto implements Serializable {
    private static final long serialVersionUID = -316102112618444133L;
    private String advertId;

    private Boolean isNewAdvert;


    private Long launchCnt;

    private Long exposureCnt;

    private Long clickCnt;

    private Long chargeCnt;

    private Long actExpCnt;

    private Long actClickCnt;

    private Double cvr;

    private Double ctr;


    public String getAdvertId() {
        return advertId;
    }

    public void setAdvertId(String advertId) {
        this.advertId = advertId;
    }


    public Boolean getIsNewAdvert() {
        return isNewAdvert;
    }

    public void setIsNewAdvert(Boolean isNewAdvert) {
        this.isNewAdvert = isNewAdvert;
    }


    public Long getLaunchCnt() {
        return launchCnt;
    }

    public void setLaunchCnt(Long launchCnt) {
        this.launchCnt = launchCnt;
    }

    public Long getExposureCnt() {
        return exposureCnt;
    }

    public void setExposureCnt(Long exposureCnt) {
        this.exposureCnt = exposureCnt;
    }


    public Long getClickCnt() {
        return clickCnt;
    }

    public void setClickCnt(Long clickCnt) {
        this.clickCnt = clickCnt;
    }


    public Long getChargeCnt() {
        return chargeCnt;
    }

    public void setChargeCnt(Long chargeCnt) {
        this.chargeCnt = chargeCnt;
    }

    public Double getCtr() {
        return ctr;
    }

    public void setCtr(Double ctr) {
        this.ctr = ctr;
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



    public Double getCvr() {
        return cvr;
    }

    public void setCvr(Double cvr) {
        this.cvr = cvr;
    }

}

package cn.com.duiba.nezha.compute.api.dto;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class AdvertCtrStatDto implements Serializable {
    private static final long serialVersionUID = -316102112618444133L;


    private String id;
    private String advertId;
    private String materialId;
    private Long launchCnt;
    private Long chargeCnt;
    private Long actExpCnt;
    private Long actClickCnt;
    private Double ctr;
    private Double cvr;
    private Long chargeFees;

    private String time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getAdvertId() {
        return advertId;
    }

    public void setAdvertId(String advertId) {
        this.advertId = advertId;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
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



    public Double getCtr() {
        return ctr;
    }

    public void setCtr(Double ctr) {
        this.ctr = ctr;
    }


    public Double getCvr() {
        return cvr;
    }

    public void setCvr(Double cvr) {
        this.cvr = cvr;
    }




    public Long getChargeFees() {
        return chargeFees;
    }

    public void setChargeFees(Long chargeFees) {
        this.chargeFees = chargeFees;
    }



    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}

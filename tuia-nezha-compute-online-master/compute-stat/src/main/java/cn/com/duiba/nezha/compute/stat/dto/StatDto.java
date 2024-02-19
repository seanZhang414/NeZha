package cn.com.duiba.nezha.compute.stat.dto;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class StatDto implements Serializable {
    private static final long serialVersionUID = -316102112618444133L;
    private String id;
    private Long launchCnt;
    private Long chargeCnt;
    private Long actExpCnt;
    private Long actClickCnt;

    private Long chargeFees;
    private Double mergeCtr;
    private Double mergeCvr;

    private String time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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



    public Long getChargeFees() {
        return chargeFees;
    }

    public void setChargeFees(Long chargeFees) {
        this.chargeFees = chargeFees;
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

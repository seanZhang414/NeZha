package cn.com.duiba.nezha.compute.alg.vo;

import java.util.Map;

public class StatDo {

    private Long advertId;
    private Long launchCnt;
    private Long chargeCnt;
    private Long actExpCnt;
    private Long actClickCnt;

    private Long chargeFees;
    private Double mergeCtr;
    private Double mergeCvr;

    private Map<String, Long> backendCntMap; // 后端转化数据 Map集合   Key:1安装APP,2启动APP,3注册账户,4激活账户,5登录账户,6用户付费,7用户进件,8用户完件；Value：Cnt


    public Long getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Long advertId) {
        this.advertId = advertId;
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



    public Map<String, Long> getBackendCntMap() {
        return backendCntMap;
    }

    public void setBackendCntMap(Map<String, Long> backendCntMap) {
        this.backendCntMap = backendCntMap;
    }



}

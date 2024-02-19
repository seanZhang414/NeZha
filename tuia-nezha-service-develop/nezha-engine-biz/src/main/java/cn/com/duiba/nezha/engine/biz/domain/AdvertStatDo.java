package cn.com.duiba.nezha.engine.biz.domain;

import java.util.Optional;

public class AdvertStatDo extends AdvertBaseStatDo{
    private Long appId;

    private Long advertId;

    private Long materialId;

    /**
     * 发券次数
     */
    private Long launchCnt;

    /**
     * 计费次数(点击数)
     */
    private Long chargeCnt;

    /**
     * 转化页曝光次数
     */
    private Long actExpCnt;

    /**
     * 转化数
     */
    private Long actClickCnt;

    /**
     * 累计计费金额
     */
    private Long chargeFees;


    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
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
        return Optional.ofNullable(launchCnt).orElse(0L);
    }

    public void setLaunchCnt(Long launchCnt) {
        this.launchCnt = launchCnt;
    }

    public Long getChargeCnt() {
        return Optional.ofNullable(chargeCnt).orElse(0L);
    }

    public void setChargeCnt(Long chargeCnt) {
        this.chargeCnt = chargeCnt;
    }

    public Long getActExpCnt() {
        return Optional.ofNullable(actExpCnt).orElse(0L);
    }

    public void setActExpCnt(Long actExpCnt) {
        this.actExpCnt = actExpCnt;
    }

    public Long getActClickCnt() {
        return Optional.ofNullable(actClickCnt).orElse(0L);
    }

    public void setActClickCnt(Long actClickCnt) {
        this.actClickCnt = actClickCnt;
    }

    public Long getChargeFees() {
        return Optional.ofNullable(chargeFees).orElse(0L);
    }

    public void setChargeFees(Long chargeFees) {
        this.chargeFees = chargeFees;
    }
}

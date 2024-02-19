package cn.com.duiba.nezha.compute.api.dto;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class AdvertAppStatDto implements Serializable {

    private static final long serialVersionUID = -316102112618444133L;

    private String key;
    private Long appId;
    private Long advertId;
    private Long materialId;

    private AdvertCtrStatDto appCdStat;// 广告媒体维度,首次投放,当日 CTR

    private AdvertCtrStatDto appR2dStat;// 广告媒体维度,首次投放,近2日 CTR

    private AdvertCtrStatDto appR7dStat;// 广告媒体维度,首次投放,近7日CTR

    private AdvertCtrStatDto appChStat;// 广告媒体维度,首次投放,当小时 CTR

    private AdvertCtrStatDto appR2hStat;// 广告媒体维度,首次投放,近2小时CTR



    public String getKey() {return key;}

    public void setKey(String key) {this.key = key;}


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



    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }




    public AdvertCtrStatDto getAppCdStat() {
        return appCdStat;
    }

    public void setAppCdStat(AdvertCtrStatDto appCdStat) {
        this.appCdStat = appCdStat;
    }

    public AdvertCtrStatDto getAppR2dStat() {
        return appR2dStat;
    }

    public void setAppR2dStat(AdvertCtrStatDto appR2dStat) {
        this.appR2dStat = appR2dStat;
    }


    public AdvertCtrStatDto getAppR7dStat() {
        return appR7dStat;
    }

    public void setAppR7dStat(AdvertCtrStatDto appR7dStat) {
        this.appR7dStat = appR7dStat;
    }




    public AdvertCtrStatDto getAppChStat() {
        return appChStat;
    }

    public void setAppChStat(AdvertCtrStatDto appChStat) {
        this.appChStat = appChStat;
    }



    public AdvertCtrStatDto getAppR2hStat() {
        return appR2hStat;
    }

    public void setAppR2hStat(AdvertCtrStatDto appR2hStat) {
        this.appR2hStat = appR2hStat;
    }







}

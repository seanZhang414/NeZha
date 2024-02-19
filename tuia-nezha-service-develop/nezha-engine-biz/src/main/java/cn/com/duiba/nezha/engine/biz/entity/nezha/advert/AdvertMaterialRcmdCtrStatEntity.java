package cn.com.duiba.nezha.engine.biz.entity.nezha.advert;

import java.io.Serializable;

/**
 * Created by xuezhaoming on 16/8/2.
 */
public class AdvertMaterialRcmdCtrStatEntity implements Serializable {

    private static final long serialVersionUID = -316102112618444933L;

    private long    id;     // 主键
    private long appId;   // 媒体ID
    private long advertId;    // 广告计划 ID
    private long materialId;  // 广告素材 ID
    private long exposePv;    // 曝光PV
    private long clickPv;    // 点击PV
    private double ctr;    // 7日历史数据CTR


    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public long getAppId() { return appId; }

    public void setAppId(long appId) { this.appId = appId; }

    public long getMaterialId() { return materialId; }

    public void setMaterialId(long materialId) { this.materialId = materialId; }

    public long getAdvertId() { return advertId; }

    public void setAdvertId(long advertId) { this.advertId = advertId; }

    public long getExposePv() { return exposePv; }

    public void setExposePv(long exposePv) { this.exposePv = exposePv; }

    public long getClickPv() { return clickPv;}

    public void setClickPv(long clickPv) {this.clickPv = clickPv;}

    public double getCtr() { return ctr; }

    public void setCtr(double ctr) { this.ctr = ctr; }



}

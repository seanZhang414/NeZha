package cn.com.duiba.nezha.engine.biz.domain;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: AdvertStatFeatureDo.java , v 0.1 2017/11/30 下午4:22 ZhouFeng Exp $
 */
public class AdvertStatFeatureDo {

    /**
     * 广告 维度统计CTR特征
     */
    private Double advertCtr;
    /**
     * 广告 维度统计CVR特征
     */
    private Double advertCvr;
    /**
     * 广告&媒体 维度统计CTR特征
     */
    private Double advertAppCtr;
    /**
     * 广告&媒体 维度统计CVR特征
     */
    private Double advertAppCvr;
    /**
     * 广告&广告位 维度统计CTR特征
     */
    private Double advertSlotCtr;
    /**
     * 广告&广告位 维度统计CVR特征
     */
    private Double advertSlotCvr;
    /**
     * 广告&活动 维度统计CTR特征
     */
    private Double advertActivityCtr;
    /**
     * 广告&活动 维度统计CVR特征
     */
    private Double advertActivityCvr;

    public AdvertStatFeatureDo() {
    }

    public AdvertStatFeatureDo(AdvertBaseStatDo advert, AdvertBaseStatDo advertApp, AdvertBaseStatDo advertSlot,
                               AdvertBaseStatDo advertActivity) {
        if (advert != null) {
            advertCtr = advert.getCtr();
            advertCvr = advert.getCvr();
        }
        if (advertApp != null) {
            advertAppCtr = advertApp.getCtr();
            advertAppCvr = advertApp.getCvr();
        }
        if (advertSlot != null) {
            advertSlotCtr = advertSlot.getCtr();
            advertSlotCvr = advertSlot.getCvr();
        }
        if (advertActivity != null) {
            advertActivityCtr = advertActivity.getCtr();
            advertActivityCvr = advertActivity.getCvr();
        }

    }


    public Double getAdvertCtr() {
        return advertCtr;
    }

    public void setAdvertCtr(Double advertCtr) {
        this.advertCtr = advertCtr;
    }

    public Double getAdvertCvr() {
        return advertCvr;
    }

    public void setAdvertCvr(Double advertCvr) {
        this.advertCvr = advertCvr;
    }

    public Double getAdvertAppCtr() {
        return advertAppCtr;
    }

    public void setAdvertAppCtr(Double advertAppCtr) {
        this.advertAppCtr = advertAppCtr;
    }

    public Double getAdvertAppCvr() {
        return advertAppCvr;
    }

    public void setAdvertAppCvr(Double advertAppCvr) {
        this.advertAppCvr = advertAppCvr;
    }

    public Double getAdvertSlotCtr() {
        return advertSlotCtr;
    }

    public void setAdvertSlotCtr(Double advertSlotCtr) {
        this.advertSlotCtr = advertSlotCtr;
    }

    public Double getAdvertSlotCvr() {
        return advertSlotCvr;
    }

    public void setAdvertSlotCvr(Double advertSlotCvr) {
        this.advertSlotCvr = advertSlotCvr;
    }

    public Double getAdvertActivityCtr() {
        return advertActivityCtr;
    }

    public void setAdvertActivityCtr(Double advertActivityCtr) {
        this.advertActivityCtr = advertActivityCtr;
    }

    public Double getAdvertActivityCvr() {
        return advertActivityCvr;
    }

    public void setAdvertActivityCvr(Double advertActivityCvr) {
        this.advertActivityCvr = advertActivityCvr;
    }
}

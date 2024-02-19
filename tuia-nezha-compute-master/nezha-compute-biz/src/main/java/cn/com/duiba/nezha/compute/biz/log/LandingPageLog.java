package cn.com.duiba.nezha.compute.biz.log;



import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class LandingPageLog implements Serializable {
    private String tuiaId; //是中间页传入的活动子订单id（兑吧）和活动订单id（推啊）
    private String cId; //用户id
    private String url; //当前页面的url
    private String referrer; //当前页面的referrer参数
    private String referrerType; //referrer域名是否是当前系统域名，0：不是，1：是,2:没传referrer
    private String result; //
    private String advertId; //广告id
    private String slotId; //广告位id
    private String materialId; //素材id
    private String appId; //应用id
    private String activityId; //活动id
    private String adSpecId; //定向配置包ID
    private String chargeType; //计费方式，1：CPC，2：CPA
    private String fee; //
    private String activitySource; //0：兑吧；1：推啊

    private String gmtDate;
    private String currentTime;


    public String getTuiaId() {return tuiaId;}

    public void setTuiaId(String tuiaId) {this.tuiaId = tuiaId;}

    public String getCId() {return cId;}

    public void setCId(String cId) {this.cId = cId;}

    public String getUrl() {return url;}

    public void setUrl(String url) {this.url = url;}

    public String getReferrer() {return referrer;}

    public void setReferrer(String referrer) {this.referrer = referrer;}

    public String getReferrerType() {return referrerType;}

    public void setReferrerType(String referrerType) {this.referrerType = referrerType;}

    public String getResult() {return result;}

    public void setResult(String result) {this.result = result;}

    public String getAdvertId() {return advertId;}

    public void setAdvertId(String advertId) {this.advertId = advertId;}


    public String getSlotId() {return  slotId;}

    public void setSlotId(String  slotId) {this.slotId =  slotId;}

    public String getAppId() {return appId;}

    public void setAppId(String appId) {this.appId = appId;}

    public String getMaterialId() {return materialId;}

    public void setMaterialId(String materialId) {this.materialId = materialId;}

    public String getActivityId() {return activityId;}

    public void setActivityId(String activityId) {this.activityId = activityId;}


    public String getAdSpecId() {return adSpecId;}

    public void setAdSpecId(String adSpecId) {this.adSpecId = adSpecId;}



    public String getChargeType() {return chargeType;}

    public void setChargeType(String chargeType) {this.chargeType = chargeType;}


    public String getFee() {return fee;}

    public void setFee(String fee) {this.fee = fee;}

    public String getActivitySource() {return activitySource;}

    public void setActivitySource(String activitySource) {this.activitySource = activitySource;}


    public String getGmtDate() {return gmtDate;}

    public void setGmtDate(String gmtDate) {this.gmtDate = gmtDate;}

    public String getCurrentTime() {return currentTime;}

    public void setCurrentTime(String currentTime) {this.currentTime = currentTime;}

}

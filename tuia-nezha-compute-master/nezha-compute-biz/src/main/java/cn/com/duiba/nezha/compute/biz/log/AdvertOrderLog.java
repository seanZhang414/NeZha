package cn.com.duiba.nezha.compute.biz.log;

import cn.com.duiba.nezha.compute.biz.vo.MapVo;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class AdvertOrderLog implements Serializable {

    private String consumerId;
    private String activityId;
    private String advertId;
    private String materialId;
    private String appId;
    private String orderId;
    private String orientationId;
    private Long fee;
    private String time;
    private String gmtDate;
    private String currentTime;
    private MapVo logExtMap;
    private String chargeType; //计费方式，1：CPC，2：CPA
    private Long type; // 1 计费 2 发券

    private String referrerType; //referrer域名是否是当前系统域名，0：不是，1：是,2:没传referrer
    private String deliveryType; // 直投类型：1 是


    public String getConsumerId() {return consumerId;}

    public void setConsumerId(String consumerId) {this.consumerId = consumerId;}

    public String getActivityId() {return activityId;}

    public void setActivityId(String activityId) {this.activityId = activityId;}

    public String getAdvertId() {return advertId;}

    public void setAdvertId(String advertId) {this.advertId = advertId;}

    public String getMaterialId() {return materialId;}

    public void setMaterialId(String materialId) {this.materialId = materialId;}


    public String getAppId() {return appId;}

    public void setAppId(String appId) {this.appId = appId;}

    public String getOrderId() {return orderId;}

    public void setOrderId(String orderId) {this.orderId = orderId;}

    public String getOrientationId() {return orientationId;}

    public void setOrientationId(String orientationId) {this.orientationId = orientationId;}

    public Long getFee() {return fee;}

    public void setFee(Long fee) {this.fee = fee;}

    public String getTime() {return time;}

    public void setTime(String time) {this.time = time;}

    public String getGmtDate() {return gmtDate;}

    public void setGmtDate(String gmtDate) {this.gmtDate = gmtDate;}

    public String getCurrentTime() {return currentTime;}

    public void setCurrentTime(String currentTime) {this.currentTime = currentTime;}

    public MapVo getLogExtMap() {return logExtMap;}

    public void setLogExtMap(MapVo logExtMap) {this.logExtMap = logExtMap;}

    public Long getType() {return type;}

    public void setType(Long type) {this.type = type;}

    public String getChargeType() {return chargeType;}

    public void setChargeType(String chargeType) {this.chargeType = chargeType;}

    public String getReferrerType() {return referrerType;}

    public void setReferrerType(String referrerType) {this.referrerType = referrerType;}

    public String getDeliveryType() {return deliveryType;}

    public void setDeliveryType(String deliveryType) {this.deliveryType = deliveryType;}

}

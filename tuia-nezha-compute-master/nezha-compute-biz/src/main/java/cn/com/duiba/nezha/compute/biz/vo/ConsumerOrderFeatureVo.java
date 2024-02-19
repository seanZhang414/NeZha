package cn.com.duiba.nezha.compute.biz.vo;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class ConsumerOrderFeatureVo implements Serializable {

    private static final long serialVersionUID = -316102112618444133L;
    private String consumerId; // rowkey p
    private String activityId; // rowkey s
    private String orderId;  // col_value
    private String gmtTime;
    private String gmtDate;
    private String currentTime;

    private String orderRank;
    private String dayOrderRank;

    private String firstOrderTime;

    private String lastOrderTime;
    private String lastOrderId;

    private String lastActivityId;

    private String lastOrderChargeNums;

    public String getConsumerId() {return consumerId;}

    public void setConsumerId(String consumerId) {this.consumerId = consumerId;}


    public String getActivityId() {return activityId;}

    public void setActivityId(String activityId) {this.activityId = activityId;}


    public String getOrderId() {return orderId;}

    public void setOrderId(String orderId) {this.orderId = orderId;}


    public String getGmtTime() {return gmtTime;}

    public void setGmtTime(String gmtTime) {this.gmtTime = gmtTime;}

    public String getGmtDate() {return gmtDate;}

    public void setGmtDate(String gmtDate) {this.gmtDate = gmtDate;}

    public String getCurrentTime() {return currentTime;}

    public void setCurrentTime(String currentTime) {this.currentTime = currentTime;}




    public String getOrderRank() {return orderRank;}

    public void setOrderRank(String orderRank) {this.orderRank = orderRank;}

    public String getDayOrderRank() {return dayOrderRank;}

    public void setDayOrderRank(String dayOrderRank) {this.dayOrderRank = dayOrderRank;}



    public String getFirstOrderTime() {return firstOrderTime;}

    public void setFirstOrderTime(String firstOrderTime) {this.firstOrderTime = firstOrderTime;}



    public String getLastOrderTime() {return lastOrderTime;}

    public void setLastOrderTime(String lastOrderTime) {this.lastOrderTime = lastOrderTime;}


    public String getLastOrderId() {return lastOrderId;}

    public void setLastOrderId(String lastOrderId) {this.lastOrderId = lastOrderId;}


    public String getLastOrderChargeNums() {return lastOrderChargeNums;}

    public void setLastOrderChargeNums(String lastOrderChargeNums) {this.lastOrderChargeNums = lastOrderChargeNums;}

    public String getLastActivityId() {return lastActivityId;}

    public void setLastActivityId(String lastActivityId) {this.lastActivityId = lastActivityId;}

}

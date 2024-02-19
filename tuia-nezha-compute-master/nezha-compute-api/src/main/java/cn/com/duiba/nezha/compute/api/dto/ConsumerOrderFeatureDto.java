package cn.com.duiba.nezha.compute.api.dto;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class ConsumerOrderFeatureDto implements Serializable {

    private static final long serialVersionUID = -316102112618444133L;
    private String key;

    private String consumerId; // rowkey p

    private String orderRank;
    private String dayOrderRank;

    private String firstOrderTime;

    private String lastOrderTime;
    private String lastOrderId;
    private String lastOrderChargeNums;

    private String lastActivityId;

    public String getKey() {return key;}

    public void setKey(String key) {this.key = key;}



    public String getConsumerId() {return consumerId;}

    public void setConsumerId(String consumerId) {this.consumerId = consumerId;}


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

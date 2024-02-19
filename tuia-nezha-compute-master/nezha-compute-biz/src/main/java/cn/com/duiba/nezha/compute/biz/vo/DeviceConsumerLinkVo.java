package cn.com.duiba.nezha.compute.biz.vo;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class DeviceConsumerLinkVo implements Serializable {

    private String deviceId; // rowkey p
    private String  consumerLinkListStr;


    public String getDeviceId() {return deviceId;}

    public void setDeviceId(String deviceId) {this.deviceId = deviceId;}


    public String getConsumerLinkListStr() {return consumerLinkListStr;}

    public void setConsumerLinkListStr(String consumerLinkListStr) {this.consumerLinkListStr = consumerLinkListStr;}
}

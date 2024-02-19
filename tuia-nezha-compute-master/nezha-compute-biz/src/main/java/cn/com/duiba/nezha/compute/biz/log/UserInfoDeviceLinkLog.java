package cn.com.duiba.nezha.compute.biz.log;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class UserInfoDeviceLinkLog implements Serializable {
    private String cid; //用户ID
    private String appId; // 媒体AppId
    private String deviceId;// 设备ID uid




    public String getCid() {return cid;}

    public void setCid(String cid) {this.cid = cid;}

    public String getAppId() {return appId;}

    public void setAppId(String appId) {this.appId = appId;}

    public String getDeviceId() {return deviceId;}

    public void setDeviceId(String deviceId) {this.deviceId = deviceId;}



}

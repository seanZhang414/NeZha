package cn.com.duiba.tuia.engine.activity.model.req;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * SpmTerminalReq
 */
public class SpmTerminalReq {

    private String device_id; // NOSONAR

    private String app_list;  // NOSONAR

    private String phone;

    private String ip;

    private String serverTime;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getApp_list() {// NOSONAR
        return app_list;
    }

    public void setApp_list(String app_list) {// NOSONAR
        this.app_list = app_list;
    }

    public String getDevice_id() {// NOSONAR
        return device_id;
    }

    public void setDevice_id(String device_id) {// NOSONAR
        this.device_id = device_id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

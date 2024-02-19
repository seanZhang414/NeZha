package cn.com.duiba.nezha.compute.api.vo;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class AdvertStatStatusVo implements Serializable {
    private static final long serialVersionUID = -316102112618444133L;

    private String currentTime;
    private String lastWarnTime;
    private String launchUpdateTime;
    private String chargeUpdateTime;
    private String actExpUpdateTime;
    private String actClickUpdateTime;
    private String nezhaUpdateTime;

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getLastWarnTime() {
        return lastWarnTime;
    }

    public void setLastWarnTime(String lastWarnTime) {
        this.lastWarnTime = lastWarnTime;
    }


    public String getLaunchUpdateTime() {
        return launchUpdateTime;
    }

    public void setLaunchUpdateTime(String launchUpdateTime) {
        this.launchUpdateTime = launchUpdateTime;
    }


    public String getChargeUpdateTime() {
        return chargeUpdateTime;
    }

    public void setChargeUpdateTime(String chargeUpdateTime) {
        this.chargeUpdateTime = chargeUpdateTime;
    }

    public String getActExpUpdateTime() {
        return actExpUpdateTime;
    }

    public void setActExpUpdateTime(String actExpUpdateTime) {
        this.actExpUpdateTime = actExpUpdateTime;
    }

    public String getActClickUpdateTime() {
        return actClickUpdateTime;
    }

    public void setActClickUpdateTime(String actClickUpdateTime) {
        this.actClickUpdateTime = actClickUpdateTime;
    }

    public String getNezhaUpdateTime() {
        return nezhaUpdateTime;
    }

    public void setNezhaUpdateTime(String nezhaUpdateTime) {
        this.nezhaUpdateTime = nezhaUpdateTime;
    }


}

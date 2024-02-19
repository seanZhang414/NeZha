/**
 * Project Name:engine-activity-biz File Name:UserInfoLogReq.java Package
 * Name:cn.com.duiba.tuia.engine.activity.model.req Date:2017年9月28日下午12:49:33 Copyright (c) 2017, duiba.com.cn All
 * Rights Reserved.
 */

package cn.com.duiba.tuia.engine.activity.model.req;

/**
 * ClassName:用户信息日志类<br/>
 * Date: 2017年9月28日 下午12:49:33 <br/>
 * 
 * @author Administrator
 * @version
 * @since JDK 1.6
 * @see
 */
public class UserInfoLogReq {

    public static final int LOG_TYPE_AYNC = 2;

    public static final int LOG_TYPE_SYNC = 1;

    /**
     * appId
     */
    private Long            appId;

    /**
     * deviceId:设备id
     */
    private String          deviceId;

    /**
     * md:设备信息
     */
    private String          md;

    /**
     * ui:用户信息
     */
    private String          ui;

    /**
     * ec:环境信息
     */
    private String          ec;

    /**
     * logType:日志类型：1:同步日志；2：异步回调日志
     */
    private int             logType;

    /**
     * 广告位ID
     */
    private Long            slotId;

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMd() {
        return md;
    }

    public void setMd(String md) {
        this.md = md;
    }

    public String getUi() {
        return ui;
    }

    public void setUi(String ui) {
        this.ui = ui;
    }

    public String getEc() {
        return ec;
    }

    public void setEc(String ec) {
        this.ec = ec;
    }

    public int getLogType() {
        return logType;
    }

    public void setLogType(int logType) {
        this.logType = logType;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }
}

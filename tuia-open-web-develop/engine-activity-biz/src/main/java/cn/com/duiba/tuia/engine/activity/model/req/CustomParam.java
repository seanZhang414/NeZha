/**
 * Project Name:engine-activity-deploy File Name:CustomParam.java Package
 * Name:cn.com.duiba.tuia.engine.activity.web.controller.params Date:2017年9月28日上午9:44:50 Copyright (c) 2017,
 * duiba.com.cn All Rights Reserved.
 */

package cn.com.duiba.tuia.engine.activity.model.req;

import java.io.Serializable;

/**
 * ClassName:CustomParam <br/>
 * Function: 客户端和设备信息 <br/>
 * Reason: 用于记录设备信息和安全信息校验 <br/>
 * Date: 2017年9月28日 上午9:44:50 <br/>
 * 
 * @author Administrator
 * @version
 * @since JDK 1.6
 * @see
 */
public class CustomParam implements Serializable {

    /**
     * BEFORE_THRESHOLD:时间戳校验阀值《前》
     */
    public static final Long  BEFORE_THRESHOLD = 5l * 60l * 1000l;
    /**
     * AFTER_THRESHOLD:时间戳校验阀值《后》
     */
    public static final Long  AFTER_THRESHOLD  = 5l * 60l * 1000l;
    private static final long serialVersionUID = -6139652367786546871L;
    /**
     * md:设备信息gzip压缩后base64编码串
     */
    private String            md;

    /**
     * timestamp:时间戳
     */
    private Long              timestamp;

    /**
     * nonce:随机数
     */
    private Long              nonce;

    /**
     * appSecret
     */
    private String            appSecret;

    /**
     * ui:用户信息gzip压缩后base64编码串
     */
    private String            ui;

    /**
     * ec:环境信息gzip压缩后base64编码串
     */
    private String            ec;

    public String getMd() {
        return md;
    }

    public void setMd(String md) {
        this.md = md;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
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

}

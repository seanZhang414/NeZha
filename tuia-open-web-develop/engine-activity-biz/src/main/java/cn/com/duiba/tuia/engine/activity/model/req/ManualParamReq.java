package cn.com.duiba.tuia.engine.activity.model.req;

import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

public class ManualParamReq implements Serializable {

    private static final long serialVersionUID = -6139652367786546871L;

    /**
     *
     */
    private String            appKey;

    /**
     * 广告位ID
     */
    private Long              adslotId;

    /**
     * 活动Id
     */
    private Long              activityId;

    /**
     * 签名
     */
    private String            signature;

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
     * ui:用户信息gzip压缩后base64编码串
     */
    @Length(max = 2048, message = "ui最长为2048字节")
    private String            ui;

    /**
     * ec:环境信息gzip压缩后base64编码串
     */
    @Length(max = 2048, message = "ec最长为2048字节")
    private String            ec;

    /** 用户ID */
    private String            userId;

    private String            imei;
    private String            idfa;
    private String            device_id;
    /**
     * 媒体用户ID
     */
    private String muId;
    /**
     * 媒体账户余额
     */
    private String credits;
    /**
     * 签名
     */
    private String sign;

    public String getMuId() {
        return muId;
    }

    public void setMuId(String muId) {
        this.muId = muId;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public Long getAdslotId() {
        return adslotId;
    }

    public void setAdslotId(Long adslotId) {
        this.adslotId = adslotId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

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

    public String getEc() {
        return ec;
    }

    public void setEc(String ec) {
        this.ec = ec;
    }

    public String getUi() {
        return ui;
    }

    public void setUi(String ui) {
        this.ui = ui;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getIdfa() {
        return idfa;
    }

    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
}

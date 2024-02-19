package cn.com.duiba.tuia.engine.activity.model.req;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * SDK V1.5新增字段, http://cf.dui88.com/pages/viewpage.action?pageId=4500879 新增请求失败日志上传接口，请求广告/曝光/点击
 * 每种类型的失败次数在上传失败时缓存在本地，下次曝光成功时顺带调用此接口上传。
 */
public class SpmErrorReq {

    // 请求广告失败次数
    private String req_fail;      // NOSONAR
    // 曝光上报请求失败次数
    private String exposure_fail; // NOSONAR
    // 点击上报请求失败次数
    private String click_fail;    // NOSONAR
    // IP
    private String ip;

    public String getReq_fail() {// NOSONAR
        return req_fail;
    }

    public void setReq_fail(String req_fail) {// NOSONAR
        this.req_fail = req_fail;
    }

    public String getExposure_fail() {// NOSONAR
        return exposure_fail;
    }

    public void setExposure_fail(String exposure_fail) {// NOSONAR
        this.exposure_fail = exposure_fail;
    }

    public String getClick_fail() {// NOSONAR
        return click_fail;
    }

    public void setClick_fail(String click_fail) {// NOSONAR
        this.click_fail = click_fail;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

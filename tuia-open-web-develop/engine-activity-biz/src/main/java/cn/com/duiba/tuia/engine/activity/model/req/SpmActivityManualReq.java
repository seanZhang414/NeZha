package cn.com.duiba.tuia.engine.activity.model.req;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 人工设置广告位日志统计信息。
 * 详见：http://cf.dui88.com/pages/viewpage.action?pageId=4370982
 */
public class SpmActivityManualReq {

    private Long slot_id;     // NOSONAR
    // 广告位ID

    private String app_key;     // NOSONAR
    // 应用key

    private String device_id;   // NOSONAR
    // 设备id，与普通广告位的device_id生成方法一致。

    private Long activity_id; // NOSONAR
    // 活动ID

    private String ua;
    // 客户端UA

    private String ip;
    // 客户端IP

    private String os_type;     // NOSONAR
    // 描述操作系统的可选值集合（Android，iOS，windows，OS）

    private Long app_id;      // NOSONAR
    // app_id

    private String day;
    // 事件发生时间

    private int source;

    // 活动来源 1-兑吧  2-推啊   3-流量引导页
    private String rid;

    private String referer;

    //投放方式：0轮询，1定时，2轮询组，3引擎
    private Integer outPutSource;

    /** 测试广告位子投放方式: 仅投1 优投人工2 优投算法3 纯算法4 其他0 */
    private Integer subActivityWay;

    //当前引擎域名
    private String host;

    private String imei;
    private String idfa;

    private String clickUrl;
    // http://cf.dui88.com/pages/viewpage.action?pageId=9766391
    // 设备机型 根据user_agent解析出设备机型
    private String model;
    // 网络类型（2G,3G,WIFI等）
    private String connect_type;// NOSONAR

    public Long getSlot_id() {// NOSONAR
        return slot_id;
    }

    public void setSlot_id(Long slot_id) {// NOSONAR
        this.slot_id = slot_id;
    }

    public String getApp_key() {// NOSONAR
        return app_key;
    }

    public void setApp_key(String app_key) {// NOSONAR
        this.app_key = app_key;
    }

    public String getDevice_id() {// NOSONAR
        return device_id;
    }

    public void setDevice_id(String device_id) {// NOSONAR
        this.device_id = device_id;
    }

    public Long getActivity_id() {// NOSONAR
        return activity_id;
    }

    public void setActivity_id(Long activity_id) {// NOSONAR
        this.activity_id = activity_id;
    }

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getOs_type() {// NOSONAR
        return os_type;
    }

    public void setOs_type(String os_type) {// NOSONAR
        this.os_type = os_type;
    }

    public Long getApp_id() {// NOSONAR
        return app_id;
    }

    public void setApp_id(Long app_id) {// NOSONAR
        this.app_id = app_id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    @Override public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public Integer getOutPutSource() {
        return outPutSource;
    }

    public void setOutPutSource(Integer outPutSource) {
        this.outPutSource = outPutSource;
    }

    public Integer getSubActivityWay() {
        return subActivityWay;
    }

    public void setSubActivityWay(Integer subActivityWay) {
        this.subActivityWay = subActivityWay;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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

    public String getClickUrl() {
        return clickUrl;
    }

    public void setClickUrl(String clickUrl) {
        this.clickUrl = clickUrl;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getConnect_type() {
        return connect_type;
    }

    public void setConnect_type(String connect_type) {
        this.connect_type = connect_type;
    }
}

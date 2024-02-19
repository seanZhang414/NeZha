package cn.com.duiba.tuia.engine.activity.model.req;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * SDK V1.5新增字段, http://cf.dui88.com/pages/viewpage.action?pageId=4500879
 * SDK V2.1.0新增字段 http://cf.dui88.com/pages/viewpage.action?pageId=5243485
 */
public class SpmSDataReq {

    // 设备参数
    // string 是 设备唯一标识码，一旦生成，后续该设备所有与服务器交互共用该标识码
    private String device_id;  // NOSONAR
    // string 否 设备类型（手机端，平板，PC）
    private String device_type;// NOSONAR
    // string 否 操作系统（iOS，Android）
    private String os_type;    // NOSONAR
    // string 否 操作系统版本
    private String os_version; // NOSONAR
    // string 否 设备厂商
    private String vendor;
    // string 否 设备型号
    private String model;
    // string 否 设备屏幕尺寸
    private String screen_size;// NOSONAR
    // string 否（only Android） Build.SERIAL
    private String serial;
    // string 是（only Android） 设备IMEI号，15位数字
    private String imei;
    // string 是（only Android） 设备IMSI号，插入SIM卡时必填
    private String imsi;
    // string 是（only Android） Android系统标识
    private String android_id; // NOSONAR
    // string 是（only iOS） 设备idfa编号
    private String idfa;
    // string 是（only iOS） 设备idfv编号
    private String idfv;
    // WIFI网络参数（WiFiAp）
    // string 选填（only Android） 本机mac地址（ssid）
    private String mac;
    // 其他
    // string 否（only Android） 手机号码
    private String phone;
    //string 是 此次请求、曝光、点击的应用包名
    private String app_package;     // NOSONAR
    //string 是 此次请求、曝光、点击的应用版本
    private String app_version;     // NOSONAR
    //string 是 此次请求、曝光、点击的应用名称
    private String app_name;     // NOSONAR

    //广告位Id
    private Long slotId;

    //appId
    private Long appId;

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getDevice_id() {// NOSONAR
        return device_id;
    }

    public void setDevice_id(String device_id) {// NOSONAR
        this.device_id = device_id;
    }

    public String getDevice_type() {// NOSONAR
        return device_type;
    }

    public void setDevice_type(String device_type) {// NOSONAR
        this.device_type = device_type;
    }

    public String getOs_type() {// NOSONAR
        return os_type;
    }

    public void setOs_type(String os_type) {// NOSONAR
        this.os_type = os_type;
    }

    public String getOs_version() {// NOSONAR
        return os_version;
    }

    public void setOs_version(String os_version) {// NOSONAR
        this.os_version = os_version;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getScreen_size() {// NOSONAR
        return screen_size;
    }

    public void setScreen_size(String screen_size) {// NOSONAR
        this.screen_size = screen_size;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getAndroid_id() {// NOSONAR
        return android_id;
    }

    public void setAndroid_id(String android_id) {// NOSONAR
        this.android_id = android_id;
    }

    public String getIdfa() {
        return idfa;
    }

    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }

    public String getIdfv() {
        return idfv;
    }

    public void setIdfv(String idfv) {
        this.idfv = idfv;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getApp_package() {     // NOSONAR
        return app_package;
    }

    public void setApp_package(String app_package) {     // NOSONAR
        this.app_package = app_package;
    }

    public String getApp_version() {     // NOSONAR
        return app_version;
    }

    public void setApp_version(String app_version) {     // NOSONAR
        this.app_version = app_version;
    }

    public String getApp_name() {      // NOSONAR
        return app_name;
    }

    public void setApp_name(String app_name) {     // NOSONAR
        this.app_name = app_name;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

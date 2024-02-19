package cn.com.duiba.tuia.engine.activity.model.req;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;

/**
 * GetActivityReq
 */
public class GetActivityReq implements Serializable {

    private static final long serialVersionUID = 1L;

    // 基础参数
    @NotBlank(message = "广告请求ID不可为空")
    private String            request_id;           // NOSONAR
    // 必填 广告请求ID
    // 由接入方自定义，在发起请求时填写，需确保request_id的唯一性。推荐使用app_id+adslot_id+若干位随机key构成

    private String            api_version;          // NOSONAR
    // 必填 API接口版本号
    // 按照当前接入所参照的API文档版本赋值，影响所有后续逻辑。当前版本1.0.0，填写错误将导致请求错误

    // 媒体参数
    private Long              app_id;               // NOSONAR
    // 应用Id，服务端通过appKey获取

    @NotBlank(message = "应用key不可为空")
    private String            app_key;              // NOSONAR
    // 必填 应用key
    // 代码位在推啊媒体平台（以下简称推啊SSP）提交媒体信息，可获得应用ID。示例：XXXX

    private String            app_version;          // NOSONAR
    // 必填 应用版本
    // 格式参照api_version。请正确填写，影响优选策略
    private String            app_package;          // NOSONAR
    // 选填 应用包名
    // 媒体方填写的应用包名，确保与提交的应用相对应

    // 广告位参数（adslot）：广告位创建和设置在推啊SSP完成
    //@NotNull(message = "广告位id不可为空")
    private Long              adslot_id;            // NOSONAR

    //广告位ID，由于浏览器屏蔽，替代adslot_id
    private Long              slotId;
    // 必填 广告位id
    // 广告位在推啊SSP进行设置，平台提供多种属性选择，设置实时生效，请谨慎操作。

    private String            adslot_size;          // NOSONAR

    //替代adslot_size
    private String            slotSize;
    // 广告位长宽比

    // 设备参数（device）：获取并回传正确的设备信息，有帮变现效果
    private String            device_type;          // NOSONAR
    // 必填 设备类型 描述设备分类的可选值集合。

    private String            os_type;              // NOSONAR
    // 必填 操作系统
    // 描述操作系统的可选值集合。包括iOS
    // 和Android

    private String            os_info;              // NOSONAR
    // 必填 操作系统版本
    // 格式参照api_version，注意至少需要填写主要版本号major和副版本号minor

    private String            vendor;
    // 必填 设备厂商
    // 设备厂商名称，中文需要UTF-8编码。样例：MEIXU

    private String            model;
    // 必填 设备型号
    // 设备型号，中文需要UTF-8编码。样例：MX5

    private String            screen_size;          // NOSONAR
    // 必填 设备屏幕尺寸 广告检索需要字段

    // 设备唯一识别码（UDID）：请优先使用明文，务必填入真实信息，否则无法保证效果
    @NotBlank(message = "设备ID不可为空")
    private String            device_id;            // NOSONAR
    // 必填 设备唯一标识码 广告定向依赖ID

    // 移动网络参数（Network）：用于广告系统针对性匹配广告和选择交互方式
    private String            ipv4;
    // 必填 IPv4地址
    // 公网IPv4地址，服务器对接必填。确保填写的内容为用户设备的公网出IP地址

    private String            connection_type;      // NOSONAR
    // 必填 网络类型
    // 移动设备网络连接方式的可选值集合。

    private String            operator_type;        // NOSONAR
    // 必填 运营商ID
    // 当前系统可以接受的移动运营商分类可选值集合。

    private String            cellular_id;          // NOSONAR
    // 选填 基站ID
    // 当前连接的运营商基站ID，用于辅助用户定位

    // WIFI网络参数（WiFiAp）：建议填写，可填写多个，包括当前连接热点和其他周边热点
    private String            ap_mac;               // NOSONAR
    // 选填 热点mac地址 重要参数，建议填写

    private String            rssi;
    // 选填 热点信号强度 重要参数，建议填写

    private String            ap_name;              // NOSONAR
    // 选填 热点名称
    // 可用于识别用户所处场所，以精准定向

    private String            wifi_type;            // NOSONAR
    // 必填 wifi类型

    private String            hotspot_type;         // NOSONAR
    // 热点类型

    // GPS参数（GPS）：建议填写，描述当前用户设备设备所处实时位置，用于出发LBS广告
    private String            coordinate_type;      // NOSONAR
    // 选填 GPS坐标类型

    private String            longitude;
    // 选填 GPS坐标经度

    private String            latitude;
    // 选填 GPS坐标维度

    private String            timestamp;
    // 选填 GPS时间戳信息

    private String            serverTime;
    // 服务端时间，日志统计需要

    private String             rid;

    //当前引擎域名
    private String            host;

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }
    //全链路唯一请求id

    public String getRequest_id() {// NOSONAR
        return request_id;
    }

    public void setRequest_id(String request_id) {// NOSONAR
        this.request_id = request_id;
    }

    public String getApi_version() {// NOSONAR
        return api_version;
    }

    public void setApi_version(String api_version) {// NOSONAR
        this.api_version = api_version;
    }

    public Long getApp_id() {// NOSONAR
        return app_id;
    }

    public void setApp_id(Long app_id) {// NOSONAR
        this.app_id = app_id;
    }

    public String getApp_key() {// NOSONAR
        return app_key;
    }

    public void setApp_key(String app_key) {// NOSONAR
        this.app_key = app_key;
    }

    public String getApp_version() {// NOSONAR
        return app_version;
    }

    public void setApp_version(String app_version) {// NOSONAR
        this.app_version = app_version;
    }

    public String getApp_package() {// NOSONAR
        return app_package;
    }

    public void setApp_package(String app_package) {// NOSONAR
        this.app_package = app_package;
    }

    public Long getAdslot_id() {// NOSONAR
        return adslot_id;
    }

    public void setAdslot_id(Long adslot_id) {// NOSONAR
        this.adslot_id = adslot_id;
    }

    public String getAdslot_size() {// NOSONAR
        return adslot_size;
    }

    public void setAdslot_size(String adslot_size) {// NOSONAR
        this.adslot_size = adslot_size;
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

    public String getOs_info() {// NOSONAR
        return os_info;
    }

    public void setOs_info(String os_info) {// NOSONAR
        this.os_info = os_info;
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

    public String getDevice_id() {// NOSONAR
        return device_id;
    }

    public void setDevice_id(String device_id) {// NOSONAR
        this.device_id = device_id;
    }

    public String getIpv4() {
        return ipv4;
    }

    public void setIpv4(String ipv4) {
        this.ipv4 = ipv4;
    }

    public String getConnection_type() {// NOSONAR
        return connection_type;
    }

    public void setConnection_type(String connection_type) {// NOSONAR
        this.connection_type = connection_type;
    }

    public String getOperator_type() {// NOSONAR
        return operator_type;
    }

    public void setOperator_type(String operator_type) {// NOSONAR
        this.operator_type = operator_type;
    }

    public String getCellular_id() {// NOSONAR
        return cellular_id;
    }

    public void setCellular_id(String cellular_id) {// NOSONAR
        this.cellular_id = cellular_id;
    }

    public String getAp_mac() {// NOSONAR
        return ap_mac;
    }

    public void setAp_mac(String ap_mac) {// NOSONAR
        this.ap_mac = ap_mac;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getAp_name() {// NOSONAR
        return ap_name;
    }

    public void setAp_name(String ap_name) {// NOSONAR
        this.ap_name = ap_name;
    }

    public String getWifi_type() {// NOSONAR
        return wifi_type;
    }

    public void setWifi_type(String wifi_type) {// NOSONAR
        this.wifi_type = wifi_type;
    }

    public String getHotspot_type() {// NOSONAR
        return hotspot_type;
    }

    public void setHotspot_type(String hotspot_type) {// NOSONAR
        this.hotspot_type = hotspot_type;
    }

    public String getCoordinate_type() {// NOSONAR
        return coordinate_type;
    }

    public void setCoordinate_type(String coordinate_type) {// NOSONAR
        this.coordinate_type = coordinate_type;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public String getSlotSize() {
        return slotSize;
    }

    public void setSlotSize(String slotSize) {
        this.slotSize = slotSize;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

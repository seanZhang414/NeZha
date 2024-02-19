package cn.com.duiba.tuia.engine.activity.model.req;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * SDK V1.5新增字段, http://cf.dui88.com/pages/viewpage.action?pageId=4500879
 */
public class SpmNSDataReq {

    // 设备参数
    // string 是 设备唯一标识码，一旦生成，后续该设备所有与服务器交互共用该标识码
    private String  device_id;        // NOSONAR
    // 移动网络参数（Network）
    // string 否 IPv4地址
    private String  ipv4;
    // string 否 网络类型（2G，3G，4G）
    private String  connection_type;  // NOSONAR
    // string 否 运营商（中国联通，中国移动，中国电信）
    private String  operator_type;    // NOSONAR
    // int 否 基站ID
    private String  cellular_id;      // NOSONAR
    // WIFI网络参数（WiFiAp）
    // string 选填 热点mac地址
    private String  ap_mac;           // NOSONAR
    // string 选填 热点信号强度
    private String  rssi;
    // string 选填 热点名称
    private String  ap_name;          // NOSONAR
    // string 选填 wifi类型
    private String  wifi_type;        // NOSONAR
    // String 选填 wifi类型
    private String wifi_list;         // NOSONAR
    // string 选填 热点类型
    private String  hotspot_type;     // NOSONAR
    // GPS参数（GPS）
    // string 选填 GPS坐标类型,不显式让用户添加权限,有才拿
    private String  coordinate_type;  // NOSONAR
    // string 选填 GPS坐标经度
    private String  longitude;
    // string 选填 GPS坐标纬度
    private String  latitude;
    // 设备本地应用列表
    // string 否（only Android）
    private String  app_list;         // NOSONAR
    // 本地已安装应用列表包名，多个用逗号分开（由于这个数据量较大，而且相对稳定，需要只在应用列表有更改的时候才重新提交一份完整的，需要把app_list信息进行排序后md5放到my.prop中）
    // string 否（only Android） 本地应用列表使用情况，包括应用包名、应用使用时长，启动次数。
    private String  app_use_list;     // NOSONAR
    // 硬件参数
    // int 是 内存大小，单位MB
    private Integer mem_size;         // NOSONAR
    // int 是 剩余内存大小，单位MB
    private Integer mem_free_size;    // NOSONAR
    // int 是 外部存储总容量，单位MB
    private Integer storage_size;     // NOSONAR
    // int 是 外部存储剩余容量，单位MB
    private Integer storage_free_size;// NOSONAR
    // int 是 cpu核心数
    private Integer cpu_cores;        // NOSONAR
    // int 是 cpu主频,单位MHz
    private Integer cpu_frequency;    // NOSONAR


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

    public String getApp_list() {// NOSONAR
        return app_list;
    }

    public void setApp_list(String app_list) {// NOSONAR
        this.app_list = app_list;
    }

    public String getApp_use_list() {// NOSONAR
        return app_use_list;
    }

    public void setApp_use_list(String app_use_list) {// NOSONAR
        this.app_use_list = app_use_list;
    }

    public Integer getMem_size() {// NOSONAR
        return mem_size;
    }

    public void setMem_size(Integer mem_size) {// NOSONAR
        this.mem_size = mem_size;
    }

    public Integer getMem_free_size() {// NOSONAR
        return mem_free_size;
    }

    public void setMem_free_size(Integer mem_free_size) {// NOSONAR
        this.mem_free_size = mem_free_size;
    }

    public Integer getStorage_size() {// NOSONAR
        return storage_size;
    }

    public void setStorage_size(Integer storage_size) {// NOSONAR
        this.storage_size = storage_size;
    }

    public Integer getStorage_free_size() {// NOSONAR
        return storage_free_size;
    }

    public void setStorage_free_size(Integer storage_free_size) {// NOSONAR
        this.storage_free_size = storage_free_size;
    }

    public Integer getCpu_cores() {// NOSONAR
        return cpu_cores;
    }

    public void setCpu_cores(Integer cpu_cores) {// NOSONAR
        this.cpu_cores = cpu_cores;
    }

    public Integer getCpu_frequency() {// NOSONAR
        return cpu_frequency;
    }

    public void setCpu_frequency(Integer cpu_frequency) {// NOSONAR
        this.cpu_frequency = cpu_frequency;
    }

    public String getWifi_list() {
        return wifi_list;
    }

    public void setWifi_list(String wifi_list) {
        this.wifi_list = wifi_list;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

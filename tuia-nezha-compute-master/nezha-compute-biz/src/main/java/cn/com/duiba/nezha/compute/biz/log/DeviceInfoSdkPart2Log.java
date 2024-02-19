package cn.com.duiba.nezha.compute.biz.log;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class DeviceInfoSdkPart2Log implements Serializable {
    private String device_id;//	设备唯一标识码，一旦生成，后续该设备所有与服务器交互共用该标识码
    private String ipv4;//	IPv4地址
    private String connection_type;//	网络类型（2G，3G，4G）
    private String operator_type;//	运营商（中国联通，中国移动，中国电信）
    private String cellular_id;//	基站ID
    private String ap_mac;//	热点mac地址
    private String rssi;//	热点信号强度
    private String ap_name;//	热点名称
    private String wifi_type;//	wifi类型
    private String hotspot_type;//	热点类型
    private String coordinate_type;//	GPS坐标类型,不显式让用户添加权限,有才拿
    private String longitude;//	GPS坐标经度
    private String latitude;//	GPS坐标纬度
    private String app_list;//	本地已安装应用列表包名，多个用逗号分开（由于这个数据量较大，而且相对稳定，需要只在应用列表有更改的时候才重新提交一份完整的，需要把app_list信息进行排序后md5放到my.prop中）
    private String app_use_list;//本地应用列表使用情况，包括应用包名、应用使用时长，启动次数。
    private String mem_size;//	内存大小，单位MB
    private String mem_free_size;//	剩余内存大小，单位MB
    private String storage_size;//	外部存储总容量，单位MB(对于安卓手机算法如下：手机内置存储容量+SD卡容量)
    private String storage_free_size;//	外部存储剩余容量，单位MB(对于安卓手机算法如下：手机内置存储容量+SD卡容量)
    private String cpu_cores;//	cpu核心数
    private String cpu_frequency;//	cpu主频,单位MHz




    public String getDevice_id() {return device_id;}

    public void setDevice_id(String device_id) {this.device_id = device_id;}


    public String getIpv4() {return ipv4;}

    public void setIpv4(String ipv4) {this.ipv4 = ipv4;}


    public String getConnection_type() {return connection_type;}

    public void setConnection_type(String connection_type) {this.connection_type = connection_type;}

    public String getOperator_type() {return operator_type;}

    public void setOperator_type(String operator_type) {this.operator_type = operator_type;}

    public String getCellular_id() {return cellular_id;}

    public void setCellular_id(String cellular_id) {this.cellular_id = cellular_id;}


        public String getAp_mac() {return ap_mac;}

    public void setAp_mac(String ap_mac) {this.ap_mac = ap_mac;}

    public String getRssi() {return rssi;}

    public void setRssi(String rssi) {this.rssi = rssi;}

    public String getAp_name() {return ap_name;}

    public void setAp_name(String ap_name) {this.ap_name = ap_name;}

    public String getWifi_type() {return wifi_type;}

    public void setWifi_type(String wifi_type) {this.wifi_type = wifi_type;}

    public String getHotspot_type() {return hotspot_type;}

    public void setHotspot_type(String hotspot_type) {this.hotspot_type = hotspot_type;}

    public String getCoordinate_type() {return coordinate_type;}

    public void setCoordinate_type(String coordinate_type) {this.coordinate_type = coordinate_type;}

    public String getLongitude() {return longitude;}

    public void setLongitude(String longitude) {this.longitude = longitude;}

    public String getLatitude() {return latitude;}

    public void setLatitude(String latitude) {this.latitude = latitude;}

    public String getApp_list() {return app_list;}

    public void setApp_list(String app_list) {this.app_list = app_list;}

    public String getApp_use_list() {return app_use_list;}

    public void setApp_use_list(String app_use_list) {this.app_use_list = app_use_list;}

    public String getMem_size() {return mem_size;}

    public void setMem_size(String mem_size) {this.mem_size = mem_size;}


    public String getMem_free_size() {return mem_free_size;}

    public void setMem_free_size(String mem_free_size) {this.mem_free_size = mem_free_size;}

    public String getStorage_size() {return storage_size;}

    public void setStorage_size(String storage_size) {this.storage_size = storage_size;}

    public String getStorage_free_size() {return storage_free_size;}

    public void setStorage_free_size(String storage_free_size) {this.storage_free_size = storage_free_size;}

    public String getCpu_cores() {return cpu_cores;}

    public void setCpu_cores(String cpu_cores) {this.cpu_cores = cpu_cores;}


    public String getCpu_frequency() {return cpu_frequency;}

    public void setCpu_frequency(String cpu_frequency) {this.cpu_frequency = cpu_frequency;}
}

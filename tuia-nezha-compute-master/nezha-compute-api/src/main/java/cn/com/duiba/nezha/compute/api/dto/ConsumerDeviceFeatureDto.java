package cn.com.duiba.nezha.compute.api.dto;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class ConsumerDeviceFeatureDto implements Serializable {
//    private static final long serialVersionUID = -316102112618444133L;

    private String key;

    private String deviceId;//	设备ID
    private String consumerId;//	用户id
    private Long age;//	年龄
    private Long sex;//	性别
    private String identifyId;//	用户id
    private String osType;//	操作系统
    private String osVersion;//	操作系统版本
    private String vendor;//	设备厂商
    private String model;//	设备型号
    private String deviceType;//	设备类型（手机端，平板，PC）
    private String screenSize;//	设备屏幕尺寸
    private String serial;//	Build.SERIAL
    private String imei;//	设备IMEI号，15位数字
    private String imsi;//	设备IMSI号，插入SIM卡时必填
    private String androidId;//	Android系统标识
    private String idfa;//	设备idfa编号
    private String idfv;//	设备idfv编号
    private String mac;//	本机mac地址（ssid）
    private String phone;//	手机号码
    private String ipv4;//	IPv4地址
    private String connectionType;//	网络类型（2G，3G，4G）
    private String operatorType;//	运营商（中国联通，中国移动，中国电信）
    private String cellularId;//	基站ID
    private String apMac;//	热点mac地址
    private String rssi;//	热点信号强度
    private String apName;//	热点名称
    private String wifiType;//	wifi类型
    private String hotspotType;//	热点类型
    private String country;//	国家
    private String province;//	省
    private String city;//	市
    private String district;//	区
    private String township;//	街道


    public String getKey() {return key;}

    public void setKey(String key) {this.key = key;}


    public String getDeviceId() {return deviceId;}

    public void setDeviceId(String deviceId) {this.deviceId = deviceId;}

    public String getConsumerId() {return consumerId;}

    public void setConsumerId(String consumerId) {this.consumerId = consumerId;}

    public Long getAge() {return age;}

    public void setAge(Long age) {this.age = age;}

    public Long getSex() {return sex;}

    public void setSex(Long sex) {this.sex = sex;}

    public String getIdentifyId() {return identifyId;}

    public void setIdentifyId(String identifyId) {this.identifyId = identifyId;}

    public String getOsType() {return osType;}

    public void setOsType(String osType) {this.osType = osType;}

    public String getOsVersion() {return osVersion;}

    public void setOsVersion(String osVersion) {this.osVersion = osVersion;}

    public String getVendor() {return vendor;}

    public void setVendor(String vendor) {this.vendor = vendor;}

    public String getModel() {return model;}

    public void setModel(String model) {this.model = model;}

    public String getDeviceType() {return deviceType;}

    public void setDeviceType(String deviceType) {this.deviceType = deviceType;}

    public String getScreenSize() {return screenSize;}

    public void setScreenSize(String screenSize) {this.screenSize = screenSize;}

    public String getSerial() {return serial;}

    public void setSerial(String serial) {this.serial = serial;}
    public String getImei() {return imei;}

    public void setImei(String imei) {this.imei = imei;}

    public String getImsi() {return imsi;}

    public void setImsi(String imsi) {this.imsi = imsi;}

    public String getAndroidId() {return androidId;}

    public void setAndroidId(String androidId) {this.androidId = androidId;}

    public String getIdfa() {return idfa;}

    public void setIdfa(String idfa) {this.idfa = idfa;}


    public String getIdfv() {return idfv;}

    public void setIdfv(String idfv) {this.idfv = idfv;}

    public String getMac() {return mac;}

    public void setMac(String mac) {this.mac = mac;}

    public String getPhone() {return phone;}

    public void setPhone(String phone) {this.phone = phone;}

    public String getIpv4() {return ipv4;}

    public void setIpv4(String ipv4) {this.ipv4 = ipv4;}


    public String getConnectionType() {return connectionType;}

    public void setConnectionType(String connectionType) {this.connectionType = connectionType;}

    public String getOperatorType() {return operatorType;}

    public void setOperatorType(String operatorType) {this.operatorType = operatorType;}

    public String getCellularId() {return cellularId;}

    public void setCellularId(String cellularId) {this.cellularId = cellularId;}


    public String getApMac() {return apMac;}

    public void setApMac(String apMac) {this.apMac = apMac;}

    public String getRssi() {return rssi;}

    public void setRssi(String rssi) {this.rssi = rssi;}

    public String getApName() {return apName;}

    public void setApName(String apName) {this.apName = apName;}

    public String getWifiType() {return wifiType;}

    public void setWifiType(String wifiType) {this.wifiType = wifiType;}

    public String getHotspotType() {return hotspotType;}

    public void setHotspotType(String hotspotType) {this.hotspotType = hotspotType;}


    public String getCountry() {return country;}

    public void setCountry(String country) {this.country = country;}

    public String getProvince() {return province;}

    public void setProvince(String province) {this.province = province;}

    public String getCity() {return city;}

    public void setCity(String city) {this.city = city;}

    public String getDistrict() {return district;}

    public void setDistrict(String district) {this.district = district;}

    public String getTownship() {return township;}

    public void setTownship(String township) {this.township = township;}

}

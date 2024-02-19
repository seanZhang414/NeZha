package cn.com.duiba.nezha.compute.biz.log;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class DeviceInfoSdkPart1Log implements Serializable {
    private String device_id;//	设备唯一标识码，一旦生成，后续该设备所有与服务器交互共用该标识码
    private String device_type;//	设备类型（手机端，平板，PC）
    private String os_type;//	操作系统（iOS，Android）
    private String os_version;//	操作系统版本
    private String vendor;//	设备厂商
    private String model;//	设备型号
    private String screen_size;//	设备屏幕尺寸
    private String serial;//	Build.SERIAL
    private String imei;//	设备IMEI号，15位数字
    private String imsi;//	设备IMSI号，插入SIM卡时必填
    private String android_id;//	Android系统标识
    private String idfa;//	设备idfa编号
    private String idfv;//	设备idfv编号
    private String mac;//	本机mac地址（ssid）
    private String phone;//	手机号码
    private String app_package;
    private String app_version;
    private String app_name;




    public String getDevice_id() {return device_id;}

    public void setDevice_id(String device_id) {this.device_id = device_id;}

    public String getDevice_type() {return device_type;}

    public void setDevice_type(String device_type) {this.device_type = device_type;}

    public String getOs_type() {return os_type;}

    public void setOs_type(String os_type) {this.os_type = os_type;}


    public String getOs_version() {return os_version;}

    public void setos_version(String os_version) {this.os_version = os_version;}


    public String getVendor() {return vendor;}

    public void setVendor(String vendor) {this.vendor = vendor;}

    public String getModel() {return model;}

    public void setModel(String model) {this.model = model;}

    public String getScreen_size() {return screen_size;}

    public void setScreen_size(String screen_size) {this.screen_size = screen_size;}

    public String getSerial() {return serial;}

    public void setSerial(String serial) {this.serial = serial;}

    public String getImei() {return imei;}

    public void setImei(String imei) {this.imei = imei;}

    public String getImsi() {return imsi;}

    public void setImsi(String imsi) {this.imsi = imsi;}

    public String getAndroid_id() {return android_id;}

    public void setAndroid_id(String android_id) {this.android_id = android_id;}

    public String getIdfa() {return idfa;}

    public void setIdfa(String idfa) {this.idfa = idfa;}


    public String getIdfv() {return idfv;}

    public void setIdfv(String idfv) {this.idfv = idfv;}

    public String getMac() {return mac;}

    public void setMac(String mac) {this.mac = mac;}

    public String getPhone() {return phone;}

    public void setPhone(String phone) {this.phone = phone;}



    public String getApp_package() {return app_package;}

    public void setApp_package(String app_package) {this.app_package = app_package;}

    public String getApp_version() {return app_version;}

    public void setApp_version(String app_version) {this.app_version = app_version;}

    public String getApp_name() {return app_name;}

    public void setApp_name(String app_name) {this.app_name = app_name;}



}

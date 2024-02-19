package cn.com.duiba.nezha.compute.biz.log;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class DeviceInfoRegionLog implements Serializable {
    private String device_id;//	设备ID
    private String country;//	国家
    private String province;//	省
    private String city;//	市
    private String district;//	区
    private String township;//	街道
    private String time;//	服务器时间




    public String getDevice_id() {return device_id;}

    public void setDevice_id(String device_id) {this.device_id = device_id;}

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

    public String getTime() {return time;}

    public void setTime(String time) {this.time = time;}

}

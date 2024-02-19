package cn.com.duiba.tuia.engine.activity.log;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * GeoAddrLog
 */
public class GeoAddrLog {

    private String device_id;// NOSONAR

    private String country;  // 国家
    private String province; // 省
    private String city;     // 市
    private String district; // 区
    private String township; // 街道
    private String json;     // json数组 整个返回json全部保存
    private String time;     // 服务器时间

    public String getDevice_id() {// NOSONAR
        return device_id;
    }

    public void setDevice_id(String device_id) {// NOSONAR
        this.device_id = device_id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTownship() {
        return township;
    }

    public void setTownship(String township) {
        this.township = township;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

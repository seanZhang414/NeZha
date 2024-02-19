package cn.com.duiba.tuia.utils;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * GeoData
 */
public class GeoData {

    private String country; // 国家
    private String province; // 省
    private String city;    // 市
    private String district; // 区
    private String township; // 街道

    private String json;    // json数组 整个返回json全部保存

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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

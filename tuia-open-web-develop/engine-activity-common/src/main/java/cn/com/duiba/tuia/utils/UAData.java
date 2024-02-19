package cn.com.duiba.tuia.utils;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * UAData
 */
public class UAData {

    private String osType; // 操作系统
    private String model; // 设备型号
    private String vendor; // 设备厂商

    private String ua;

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

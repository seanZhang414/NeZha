package cn.com.duiba.tuia.engine.activity.log;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * DeviceInfoLog
 */
public class DeviceInfoLog {

    // 设备ID
    private String device_id;// NOSONAR
    // 操作系统
    private String os_type;  // NOSONAR
    // 设备厂商
    private String vendor;
    // 设备型号
    private String model;

    public String getDevice_id() {// NOSONAR
        return device_id;
    }

    public void setDevice_id(String device_id) {// NOSONAR
        this.device_id = device_id;
    }

    public String getOs_type() {// NOSONAR
        return os_type;
    }

    public void setOs_type(String os_type) {// NOSONAR
        this.os_type = os_type;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

package cn.com.duiba.nezha.compute.biz.log;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class DeviceInfoBaseLog implements Serializable {
    private String device_id;//	设备ID
    private String os_type;//	操作系统
    private String vendor;//	设备厂商
    private String model;//	设备型号

    public String getDevice_id() {return device_id;}

    public void setDevice_id(String device_id) {this.device_id = device_id;}

    public String getOs_type() {return os_type;}

    public void setOs_type(String os_type) {this.os_type = os_type;}

    public String getVendor() {return vendor;}

    public void setVendor(String vendor) {this.vendor = vendor;}

    public String getModel() {return model;}

    public void setModel(String model) {this.model = model;}



}

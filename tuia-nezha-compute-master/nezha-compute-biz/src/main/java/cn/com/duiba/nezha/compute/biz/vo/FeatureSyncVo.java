package cn.com.duiba.nezha.compute.biz.vo;

import cn.com.duiba.nezha.compute.api.dto.ConsumerDeviceFeatureDto;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class FeatureSyncVo implements Serializable {

    private static final long serialVersionUID = -316102112618444133L;

    private String key;

    private ConsumerDeviceFeatureDto consumerDeviceFeatureDto;


    public String getkey() {
        return key;
    }

    public void setkey(String key) {
        this.key = key;
    }


    public ConsumerDeviceFeatureDto getConsumerDeviceFeatureDto() {
        return consumerDeviceFeatureDto;
    }

    public void setConsumerDeviceFeatureDto(ConsumerDeviceFeatureDto consumerDeviceFeatureDto) {
        this.consumerDeviceFeatureDto = consumerDeviceFeatureDto;
    }


}

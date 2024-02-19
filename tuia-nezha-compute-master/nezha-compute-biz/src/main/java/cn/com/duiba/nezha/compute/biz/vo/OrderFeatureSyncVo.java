package cn.com.duiba.nezha.compute.biz.vo;

import cn.com.duiba.nezha.compute.api.dto.ConsumerOrderFeatureDto;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class OrderFeatureSyncVo implements Serializable {

    private static final long serialVersionUID = -316102112618444133L;

    private String consumerId;
    private String activityDimId;
    private String gmtDate;
    private ConsumerOrderFeatureDto consumerOrderFeatureDto;


    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getactivityDimId() {
        return activityDimId;
    }

    public void setactivityDimId(String activityDimId) {
        this.activityDimId = activityDimId;
    }


    public ConsumerOrderFeatureDto getConsumerOrderFeatureDto() {
        return consumerOrderFeatureDto;
    }

    public void setConsumerOrderFeatureDto(ConsumerOrderFeatureDto consumerOrderFeatureDto) {
        this.consumerOrderFeatureDto = consumerOrderFeatureDto;
    }

    public String getGmtDate() {
        return gmtDate;
    }

    public void setGmtDate(String gmtDate) {
        this.gmtDate = gmtDate;
    }


}

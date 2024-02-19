package cn.com.duiba.nezha.compute.biz.dto;

import java.util.Map;

public class PsModelSample {

    private String orderId;
    private String feature;
    private Map<String, String> featureMap;

    private Long isClick;
    private Long isActClick;
    private Long ocpc;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public Map<String, String> getFeatureMap() {
        return featureMap;
    }

    public void setFeatureMap(Map<String, String> featureMap) {
        this.featureMap = featureMap;
    }


    public Long getIsClick() {
        return isClick;
    }

    public void setIsClick(Long isClick) {
        this.isClick = isClick;
    }


    public Long getIsActClick() {
        return isActClick;
    }

    public void setIsActClick(Long isActClick) {
        this.isActClick = isActClick;
    }

    public Long getOcpc() {
        return ocpc;
    }

    public void setOcpc(Long ocpc) {
        this.ocpc = ocpc;
    }


}

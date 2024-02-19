package cn.com.duiba.nezha.engine.api.dto;

import java.io.Serializable;

public class RecommendAppDto implements Serializable {
    // 广告id
    private Long advertId;
    // 配置包id 默认配置为0
    private Long packageId;
    // 媒体id
    private Long appId;
    // 偏差
    private Double bias;
    // 推荐类型 1-定向 2-屏蔽
    private Integer type;

    public Long getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Long advertId) {
        this.advertId = advertId;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Double getBias() {
        return bias;
    }

    public void setBias(Double bias) {
        this.bias = bias;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}

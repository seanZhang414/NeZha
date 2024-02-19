package cn.com.duiba.nezha.engine.api.dto;

import java.io.Serializable;

public class FusingOrientationPackageDto implements Serializable {
    private Long id;
    private Long advertId;
    private Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Long advertId) {
        this.advertId = advertId;
    }
}

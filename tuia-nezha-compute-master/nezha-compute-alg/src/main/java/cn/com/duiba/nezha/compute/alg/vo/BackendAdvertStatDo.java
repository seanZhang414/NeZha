package cn.com.duiba.nezha.compute.alg.vo;

public class BackendAdvertStatDo {

    private Long advertId;
    private String backendType;
    private Double avgBackendCvr;
    private Double cBackendCvr;

    public Long getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Long advertId) {
        this.advertId = advertId;
    }


    public String getBackendType() {
        return backendType;
    }

    public void setBackendType(String backendType) {
        this.backendType = backendType;
    }

    public Double getAvgBackendCvr() {
        return avgBackendCvr;
    }

    public void setAvgBackendCvr(Double avgBackendCvr) { this.avgBackendCvr = avgBackendCvr; }

    public Double getCBackendCvr() {
        return cBackendCvr;
    }

    public void setCBackendCvr(Double cBackendCvr) { this.cBackendCvr = cBackendCvr; }

}

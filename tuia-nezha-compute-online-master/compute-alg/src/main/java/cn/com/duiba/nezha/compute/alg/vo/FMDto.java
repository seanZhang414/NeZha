package cn.com.duiba.nezha.compute.alg.vo;

import java.io.Serializable;
import java.util.Map;

public class FMDto implements Serializable {
//    private static final long serialVersionUID = -316102112618444133L;
    private Long dim;
    private Long factorNum;

    private Double weight0;
    private Map<Long, Double> weight;
    private Map<Long, Map<Long, Double>> vector;

    public void setDim(Long dim) {
        this.dim = dim;
    }

    public Long getDim() {
        return this.dim;
    }


    public void setFactorNum(Long factorNum) {
        this.factorNum = factorNum;
    }

    public Long getFactorNum() {
        return this.factorNum;
    }

    public void setWeight0(Double weight0) {
        this.weight0 = weight0;
    }

    public Double getWeight0() {
        return this.weight0;
    }

    public void setWeight(Map<Long, Double> weight) {
        this.weight = weight;
    }

    public Map<Long, Double> getWeight() {
        return this.weight;
    }

    public void setVector(Map<Long, Map<Long, Double>> vector) {
        this.vector = vector;
    }

    public Map<Long, Map<Long, Double>> getVector() {
        return this.vector;
    }

}

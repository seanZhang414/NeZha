package cn.com.duiba.nezha.compute.api.vo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by pc on 2016/11/16.
 */
public class AlgFeatureVo implements Serializable {
    private static final long serialVersionUID = -316102112618444133L;
    private Long id;
    private String featureId;
    private Long featureCategorySize;
    private Long index;
    private Long subIndex;

    private String category;

    private Double intercept;
    private Double weight;

    private String factor;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getFeatureId() {
        return featureId;
    }

    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }

    public Long getFeatureCategorySize() {
        return featureCategorySize;
    }

    public void setFeatureCategorySize(Long featureCategorySize) {
        this.featureCategorySize = featureCategorySize;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public Long getSubIndex() {
        return subIndex;
    }

    public void setSubIndex(Long subIndex) {
        this.subIndex = subIndex;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }



    public Double getIntercept() {
        return intercept;
    }

    public void setIntercept(Double intercept) {
        this.intercept = intercept;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getFactor() {
        return factor;
    }

    public void setFactor(String factor) {
        this.factor = factor;
    }


}

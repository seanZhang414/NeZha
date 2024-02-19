package cn.com.duiba.nezha.compute.api;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class PredResultVo implements Serializable {

    private static final long serialVersionUID = -316102112618444113L;

    private Double predValue;

    private int newFeatureNums;
    private int totalFeatureNums;

    public Double getPredValue() {
        return predValue;
    }

    public void setPredValue(Double predValue) {
        this.predValue = predValue;
    }


    public int getNewFeatureNums() {
        return newFeatureNums;
    }

    public void setNewFeatureNums(int newFeatureNums) {
        this.newFeatureNums = newFeatureNums;
    }

    public int getTotalFeatureNums() {
        return totalFeatureNums;
    }

    public void setTotalFeatureNums(int totalFeatureNums) {
        this.totalFeatureNums = totalFeatureNums;
    }


}

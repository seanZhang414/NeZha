package cn.com.duiba.nezha.compute.alg.vo;


import org.apache.spark.mllib.linalg.SparseVector;

import java.io.Serializable;


/**
 * Created by pc on 2016/11/16.
 */
public class VectorResult implements Serializable {

    private static final long serialVersionUID = -316102112618444133L;

    private SparseVector vector;
    private Double predValue;
    private int newFeatureNums;
    private int totalFeatureNums;

    public SparseVector getVector() {
        return vector;
    }

    public void setVector(SparseVector vector) {
        this.vector = vector;
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


    public Double getPredValue() {
        return predValue;
    }

    public void setPredValue(Double predValue) {
        this.predValue = predValue;
    }
}

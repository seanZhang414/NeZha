package cn.com.duiba.nezha.compute.biz.entity.model;

import java.io.Serializable;

/**
 * Created by xuezhaoming on 16/8/2.
 */
public class AdvertCtrLrModelEvaluateEntity implements Serializable {

    private static final long serialVersionUID = -316102112618444133L;

    private int    id;     // 主键
    private String modelKey;    // 模型key
    private String dt;    // 日期 yyyyMMdd
    private long traingNums;    // 训练样本数
    private long testNums;    // 测试样本数
    private long featureSize;    // 测试样本数

    private double testAuprc;    // 测试 auprc
    private double testAuroc;    // 测试 auroc

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getModelKey() { return modelKey; }

    public void setModelKey(String modelKey) { this.modelKey = modelKey; }

    public String getDt() { return dt; }

    public void setDt(String dt) { this.dt = dt; }

    public long getTraingNums() { return traingNums; }

    public void setTraingNums(long traingNums) { this.traingNums = traingNums; }

    public long getTestNums() { return testNums; }

    public void setTestNums(long testNums) { this.testNums = testNums; }

    public long getFeatureSize() { return featureSize; }

    public void setFeatureSize(long featureSize) { this.featureSize = featureSize; }


    public double getTestAuprc() { return testAuprc; }

    public void setTestAuprc(double testAuprc) { this.testAuprc = testAuprc; }

    public double getTestAuroc() { return testAuroc; }

    public void setTestAuroc(double testAuroc) { this.testAuroc = testAuroc; }


}

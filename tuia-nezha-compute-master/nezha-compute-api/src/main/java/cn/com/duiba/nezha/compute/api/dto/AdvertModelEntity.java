package cn.com.duiba.nezha.compute.api.dto;

import java.io.Serializable;

/**
 * Created by xuezhaoming on 16/8/2.
 */
public class AdvertModelEntity implements Serializable {

    private static final long serialVersionUID = -316102112618444133L;

    private int serializerId;

    private String modelKey;    // 模型key
    private String dt;    // 日期 yyyyMMdd
    private String featureIdxListStr;    // 特征索引列表序列化字符串
    private String featureCollectListStr;    // 特征索引列表序列化字符串
    private String featureDictStr;    // 特征索引字典序列化字符串
    private String modelStr;    // 模型序列化字符串

    public int getSerializerId() { return serializerId; }

    public void setSerializerId(int serializerId) { this.serializerId = serializerId; }


    public String getModelKey() { return modelKey; }

    public void setModelKey(String modelKey) { this.modelKey = modelKey; }

    public String getDt() { return dt; }

    public void setDt(String dt) { this.dt = dt; }

    public String getFeatureIdxListStr() { return featureIdxListStr; }

    public void setFeatureIdxListStr(String featureIdxListStr) { this.featureIdxListStr = featureIdxListStr; }

    public String getFeatureCollectListStr() { return featureCollectListStr; }

    public void setFeatureCollectListStr(String featureCollectListStr) { this.featureCollectListStr = featureCollectListStr; }


    public String getFeatureDictStr() { return featureDictStr; }

    public void setFeatureDictStr(String featureDictStr) { this.featureDictStr = featureDictStr; }

    public String getModelStr() { return modelStr; }

    public void setModelStr(String modelStr) { this.modelStr = modelStr; }



}

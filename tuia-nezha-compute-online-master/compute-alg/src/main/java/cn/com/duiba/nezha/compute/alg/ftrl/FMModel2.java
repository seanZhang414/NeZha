package cn.com.duiba.nezha.compute.alg.ftrl;

import cn.com.duiba.nezha.compute.alg.vo.FMDto;
import cn.com.duiba.nezha.compute.core.util.DataUtil;
import cn.com.duiba.nezha.compute.core.util.MathUtil;
import cn.com.duiba.nezha.compute.feature.FeatureCoder;
import cn.com.duiba.nezha.compute.feature.vo.Feature;
import cn.com.duiba.nezha.compute.feature.vo.FeatureBaseCode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FMModel2 implements Serializable {

    private FMDto fMDto;

    private String modelId;

    private String updateTime;

    private List<FeatureBaseCode> featureBaseCode;

    public void setFeatureBaseCode(List<FeatureBaseCode> featureBaseCode) {
        this.featureBaseCode = featureBaseCode;
    }

    public List<FeatureBaseCode> getFeatureBaseCode() {
        return this.featureBaseCode;
    }


    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getModelId() {
        return this.modelId;
    }


    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }


    public void setFMDto(FMDto fMDto) {
        this.fMDto = fMDto;
    }

    public FMDto getFMDto() {
        return this.fMDto;
    }




    public Double predict(Feature feature) throws Exception {


        try {
            double ret = 0.0;
            double retw0 = 0.0;
            double retw = 0.0;
            double retv = 0.0;
            long factorNum = fMDto.getFactorNum();
            if (feature != null) {

                retw0 += fMDto.getWeight0();
                for (int i = 0; i < feature.indices.length; i++) {
                    Long fId = 0L + feature.indices[i];
                    Double value = feature.values[i];
                    retw += fMDto.getWeight().getOrDefault(fId, 0.0) * value;
                }

                for (int j = 0; j < factorNum; j++) {
                    Map<Long, Double> parMap = fMDto.getVector().getOrDefault(0L + j, new HashMap<>());
                    double retvp1 = 0.0;
                    double retvp2 = 0.0;
                    for (int i = 0; i < feature.indices.length; i++) {
                        Long fId = 0L + feature.indices[i];
                        Double value = feature.values[i];
                        Double vif = parMap.getOrDefault(fId, 0.0);
                        retvp1 += value * vif;
                        retvp2 += Math.pow(value, 2) * Math.pow(vif, 2);
                    }
                    retv += 0.5 * (Math.pow(retvp1, 2) - retvp2);
                }

                ret = retw0 + retw + retv;
            }

            double pValue = MathUtil.sigmoid(ret);
            return DataUtil.formatdouble(pValue, 5);
        } catch (Exception e) {
            System.out.println("FMModel.predict error");
            throw new Exception(e);
        }

    }

}

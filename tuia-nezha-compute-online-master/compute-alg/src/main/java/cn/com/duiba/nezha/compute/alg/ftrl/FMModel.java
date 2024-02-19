package cn.com.duiba.nezha.compute.alg.ftrl;

import cn.com.duiba.nezha.compute.core.util.DataUtil;
import cn.com.duiba.nezha.compute.core.util.MathUtil;
import cn.com.duiba.nezha.compute.alg.vo.FMDto;
import cn.com.duiba.nezha.compute.feature.FeatureCoder;
import cn.com.duiba.nezha.compute.feature.vo.Feature;
import cn.com.duiba.nezha.compute.feature.vo.FeatureBaseCode;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.ArrayUtils;


import java.io.Serializable;
import java.util.*;

public class FMModel implements Serializable {

    private FMDto fMDto;

    private String modelId;

    private String updateTime;

    private List<FeatureBaseCode> featureBaseCode = new ArrayList<FeatureBaseCode>();
    ;

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

    /**
     * @param featureMap
     * @return
     * @throws Exception
     */
    public Double predict(Map<String, String> featureMap) throws Exception {
        Double ret = null;
        Feature feature = FeatureCoder.featureCode(featureMap, getFeatureBaseCode());

        ret = predict(feature);

        return ret;
    }

    /**
     * @param featureMap
     * @return
     * @throws Exception
     */
    public double[] getModelParams(Map<String, String> featureMap) throws Exception {
        double[] ret = null;
        List<Set<Long>> codeSet = FeatureCoder.featureCodeSet(featureMap, getFeatureBaseCode());

        ret = getModelParams(codeSet);

        return ret;
    }

    /**
     * @param feature
     * @return
     * @throws Exception
     */
    public Double predict(Feature feature) throws Exception {


        try {
            double ret = 0.0;
            double retw0 = 0.0;
            double retw = 0.0;
            double retv = 0.0;
            long factorNum = fMDto.getFactorNum();
            if (feature != null ) {

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

//                System.out.println("JFM retw0="+retw0+",retw="+retw+",retv="+retv);

                ret = retw0 + retw + retv;

            }

            double pValue = MathUtil.sigmoid(ret);
            return DataUtil.formatdouble(pValue, 5);
        } catch (Exception e) {
            System.out.println("FMModel.predict error");
            throw e;
        }

    }

    /**
     * @param codeSetList
     * @return
     * @throws Exception
     */
    public double[] getModelParams(List<Set<Long>> codeSetList) throws Exception {


        try {
            boolean status = true;

            double[] ret = null;


            if (codeSetList != null) {

                int factorNum = fMDto.getFactorNum().intValue();
                int fSize = codeSetList.size();

                double retw0 = 0.0;
                double[] retw = new double[fSize];
                double[] retv = new double[fSize * factorNum];
                ret = new double[1 + fSize + fSize * factorNum];

                //1
                retw0 = fMDto.getWeight0();

                for (int i = 0; i < fSize; i++) {

                    Set<Long> fSet = codeSetList.get(i);
                    if (fSet != null) {
                        double subw = 0.0;
                        int setSize = fSet.size();
                        for (Long f : fSet) {
                            subw += fMDto.getWeight().getOrDefault(f, 0.0) / setSize;
                        }

                        retw[i] = subw;

                    } else {
                        status = false;
                    }

                }
                for (int j = 0; j < factorNum; j++) {
                    Map<Long, Double> parMap = fMDto.getVector().getOrDefault(0L + j, new HashMap<>());

                    for (int i = 0; i < fSize; i++) {

                        Set<Long> fSet = codeSetList.get(i);
                        if (fSet != null) {
                            double subv = 0.0;
                            int setSize = fSet.size();
                            for (Long f : fSet) {
                                subv += parMap.getOrDefault(f, 0.0) / setSize;

                            }
                            retv[i * factorNum + j] = subv;

                        }

                    }

                }

//                System.out.println("JFM retw0="+retw0+",retw="+retw+",retv="+retv);
                ret[0]=retw0;
                System.arraycopy(retw, 0, ret, 1, fSize);
                System.arraycopy(retv, 0, ret, 1 + fSize, fSize * factorNum);

//                System.out.println("retw0="+ JSON.toJSONString(retw0));
//                System.out.println("retw="+JSON.toJSONString(retw));
//                System.out.println("retv="+JSON.toJSONString(retv));
//                System.out.println("ret="+JSON.toJSONString(ret));
            }
            return ret;
        } catch (Exception e) {
            System.out.println("FMModel.predict error");
            throw new Exception(e);
        }

    }
    public static void main(String[] args){
        int factorNum=3;
        int fSize =10;
        for (int j = 0; j < factorNum; j++) {

            for (int i = 0; i < fSize; i++) {

                System.out.println(i * factorNum + j);

            }

        }

    }
}

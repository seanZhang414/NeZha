package cn.com.duiba.nezha.compute.alg.ftrl;

import cn.com.duiba.nezha.compute.alg.vo.FMDto;
import cn.com.duiba.nezha.compute.core.util.DataUtil;
import cn.com.duiba.nezha.compute.core.util.MathUtil;
import cn.com.duiba.nezha.compute.feature.FeatureCoder;
import cn.com.duiba.nezha.compute.feature.vo.Feature;
import cn.com.duiba.nezha.compute.feature.vo.FeatureBaseCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FMModelBase  {

    public static Double getSparseRatio(FMDto fmDto) {

        long allSize = 1 + fmDto.getDim() * (1 + fmDto.getFactorNum());
        long weightSize = fmDto.getWeight().size();
        long vectorSize = 0;

        for (Map<Long, Double> sMap : fmDto.getVector().values()) {
            vectorSize += sMap.size();
        }

        return (weightSize + vectorSize + 1.0) / allSize;
    }
}

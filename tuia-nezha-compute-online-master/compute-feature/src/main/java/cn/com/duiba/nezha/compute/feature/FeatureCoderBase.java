package cn.com.duiba.nezha.compute.feature;

import cn.com.duiba.nezha.compute.core.util.AssertUtil;
import cn.com.duiba.nezha.compute.feature.constant.FeatureMapConstant;
import cn.com.duiba.nezha.compute.feature.vo.Feature;
import com.alibaba.fastjson.JSON;

import java.util.*;

public class FeatureCoderBase {


    public static int getVectorSize(List<Long> pLenList) {
        int ret = 0;
        if (pLenList != null && pLenList.size() > 0) {
            Long tmp = pLenList.get(pLenList.size() - 1);
            if (tmp != null) {
                ret = tmp.intValue();
            }
        }
        return ret;
    }

    public static Set<Long> getId(int fSize, int fId, List<Long> pLenList, List<Integer> indices, List<Double> values) throws Exception {
        Set<Long> ret = new HashSet<>();
        int pLen = getVectorSize(pLenList);
        indices.add(pLen + fId);
        ret.add(pLen + fId + 0L);
        values.add(1.0);
        pLenList.add(0L + pLen + fSize);

        return ret;

    }


    public static Set<Long> getIds(int fSize, int pFNums, int[] sFIds, List<Long> pLenList, List<Integer> indices, List<Double> values) throws Exception {
        Set<Long> ret = new HashSet<>();
        int pLen = getVectorSize(pLenList);
        if (sFIds != null && sFIds.length > 0) {
            for (int i = 0; i < sFIds.length; i++) {
                int fId = sFIds[i];
                indices.add(pLen + fId);
                ret.add(pLen + fId + 0L);
                values.add(1.0 / sFIds.length);
            }
        }
        pLenList.add(0L + pLen + pFNums * fSize);
        return ret;
    }

    public static Set<Long> getSubId(String feature, String fStr, int fSize, List<Long> pLenList, List<Integer> indices, List<Double> values) throws Exception {


        int fId = FeatureUtil.getSubFId(feature, fStr, fSize);

        return getId(fSize, fId, pLenList, indices, values);

    }

    public static Set<Long> getSubIds(String feature, String fStr, String seq, int fSize, List<Long> pLenList, List<Integer> indices, List<Double> values) throws Exception {

        String[] fStrs = FeatureUtil.toFeatures(fStr, seq);
        int[] sFIds = FeatureUtil.getSubFIds(feature, fStrs, fSize);

        return getIds(fSize, 1, sFIds, pLenList, indices, values);
    }


    public static Set<Long> getHashSubId(String feature, String fStr, int fSize, int pNums, List<Long> pLenList, List<Integer> indices, List<Double> values) throws Exception {
        Set<Long> ret = new HashSet<>();
        int pLen = getVectorSize(pLenList);
        int[] sFIds = FeatureUtil.getHashSubFId(feature, fStr, fSize, pNums);
        return getIds(fSize, pNums, sFIds, pLenList, indices, values);
    }

    public static Set<Long> getHashSubIds(String feature, String fStr, String seq, int fSize, int pNums, List<Long> pLenList, List<Integer> indices, List<Double> values) throws Exception {
        Set<Long> ret = new HashSet<>();
        int pLen = getVectorSize(pLenList);
        String[] fStrs = FeatureUtil.toFeatures(fStr, seq);
//        System.out.println("featureId="+feature+",str="+ JSON.toJSONString(fStr)+",fStrs="+JSON.toJSONString(fStrs));
        int[] sFIds = FeatureUtil.getHashSubFIds(feature, fStrs, fSize, pNums);
//         System.out.println("pFNums="+pFNums+"，sFIds="+JSON.toJSONString(sFIds));
        return getIds(fSize, pNums, sFIds, pLenList, indices, values);
    }


    public static Set<Long> getDictSubId(String feature, String fStr, int fSize, List<Long> pLenList, List<Integer> indices, List<Double> values) throws Exception {

        Map<String, Long> fMap = FeatureMapConstant.getFeatureIdxMap(feature);
        int fId = FeatureUtil.getDictSubFId(feature, fStr, fSize, fMap);

        return getId(fSize, fId, pLenList, indices, values);
    }

    public static Set<Long> getDictSubIds(String feature, String fStr, String seq, int fSize, List<Long> pLenList, List<Integer> indices, List<Double> values) throws Exception {

        String[] fStrs = FeatureUtil.toFeatures(fStr, seq);
        Map<String, Long> fMap = FeatureMapConstant.getFeatureIdxMap(feature);
        int[] sFIds = FeatureUtil.getDictSubFIds(feature, fStrs, fSize, fMap);

        return getIds(fSize, 1, sFIds, pLenList, indices, values);
    }


    public static Feature toFeature(List<Integer> indices, List<Double> values, List<Long> pLenList) throws Exception {

        if (AssertUtil.isAnyEmpty(indices, values, pLenList)) {
            throw new Exception("AssertUtil.isAnyEmpty(indices,values,pLenList), input invalid");
        }

        if (values.size() != indices.size()) {
            throw new Exception("values.size()!=pLenList.size(), input invalid");
        }


        int[] indices2 = new int[indices.size()];
        double[] values2 = new double[values.size()];


        for (int i = 0; i < indices.size(); i++) {
            indices2[i] = indices.get(i);
            values2[i] = values.get(i);
        }


        int pLen = getVectorSize(pLenList);

        return new Feature(pLen, indices2, values2);


    }
}
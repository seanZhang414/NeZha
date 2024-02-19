package cn.com.duiba.nezha.compute.feature;

import cn.com.duiba.nezha.compute.feature.constant.FeatureListConstant;
import cn.com.duiba.nezha.compute.feature.enums.FeatureEnumC1;
import cn.com.duiba.nezha.compute.feature.enums.ModelKeyEnum;
import cn.com.duiba.nezha.compute.feature.vo.Feature;
import cn.com.duiba.nezha.compute.feature.vo.FeatureBaseCode;
import cn.com.duiba.nezha.compute.feature.vo.FeatureCodeInfo;
import com.alibaba.fastjson.JSON;

import java.util.*;

public class FeatureCoder extends FeatureCoderBase {


    public static Map<String, List<FeatureBaseCode>> cache = new HashMap<>();


    public static List<Set<Long>> featureCodeSet(Map<String, String> featureMap, List<FeatureBaseCode> codeList) {
        List<Set<Long>> ret = null;
        try {
            if (codeList != null) {
                List<FeatureCodeInfo> list = new ArrayList<>();

                for (FeatureBaseCode f : codeList) {
                    String value = featureMap.getOrDefault(f.getName(), null);
                    FeatureCodeInfo info = new FeatureCodeInfo(f.getName(), f.getCodeType(), f.getSubLen(), f.getHashNums(), value);
                    list.add(info);
                }
                ret = codeSet(list);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static Feature featureCode(Map<String, String> featureMap, List<FeatureBaseCode> codeList) {
        Feature feature = null;
        try {
            if (codeList != null) {
                List<FeatureCodeInfo> list = new ArrayList<>();

                for (FeatureBaseCode f : codeList) {
                    String value = featureMap.getOrDefault(f.getName(), null);
                    FeatureCodeInfo info = new FeatureCodeInfo(f.getName(), f.getCodeType(), f.getSubLen(), f.getHashNums(), value);
                    list.add(info);
                }
                feature = code(list);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return feature;
    }


    public static List<FeatureBaseCode> getFeatureCode(String modelId) {

        List<FeatureBaseCode> list = new ArrayList<>();
        try {
            if (cache.containsKey(modelId)) {
                list = cache.get(modelId);
            } else {
                List<FeatureEnumC1> featureEnumC1List = FeatureListConstant.FLC1_BASE;


                if (modelId.equals(ModelKeyEnum.FTRL_FM_CVR_MODEL_test.getFeatureIndex())) {
                    featureEnumC1List = FeatureListConstant.FLC1_BASE_ALL;
                }

                if (modelId.equals(ModelKeyEnum.FTRL_FM_CVR_MODEL_test.getFeatureIndex())
                        ) {
                    featureEnumC1List = FeatureListConstant.FLC1_BASE_CVR;
                }



                // learn  衰减，
                if (modelId.equals(ModelKeyEnum.FTRL_FM_CTR_MODEL_v001.getFeatureIndex())) {
                    featureEnumC1List = FeatureListConstant.FLC1_BASE_ALL_U_I_P;
                }
                if (modelId.equals(ModelKeyEnum.FTRL_FM_CVR_MODEL_v001.getFeatureIndex())
                        ) {
                    featureEnumC1List = FeatureListConstant.FLC1_BASE_ALL_U_I_P;
                }



                //stat  0626+衰减
                if (modelId.equals(ModelKeyEnum.FTRL_FM_CTR_MODEL_v002.getFeatureIndex())) {
                    featureEnumC1List = FeatureListConstant.FLC1_BASE_ALL_U_I_P_2;
                }
                if (modelId.equals(ModelKeyEnum.FTRL_FM_CVR_MODEL_v002.getFeatureIndex())) {

                    featureEnumC1List = FeatureListConstant.FLC1_BASE_ALL_U_I_P_2;
                }


                // base  0626+衰减
                if (modelId.equals(ModelKeyEnum.FTRL_FM_CTR_MODEL_v003.getFeatureIndex())) {
                    featureEnumC1List = FeatureListConstant.FLC1_BASE_U_I_P;
                }
                if (modelId.equals(ModelKeyEnum.FTRL_FM_CVR_MODEL_v003.getFeatureIndex())) {
                    featureEnumC1List = FeatureListConstant.FLC1_BASE_U_I_P;
                }

                // act
                if (modelId.equals(ModelKeyEnum.FTRL_FM_CTR_MODEL_v004.getFeatureIndex())) {
                    featureEnumC1List = FeatureListConstant.FLC1_BASE_U_I_P;
                }
                if (modelId.equals(ModelKeyEnum.FTRL_FM_CVR_MODEL_v004.getFeatureIndex())) {
                    featureEnumC1List = FeatureListConstant.FLC1_BASE_U_I_P;
                }

                // weight 0626+衰减+分布
                if (modelId.equals(ModelKeyEnum.FTRL_FM_CTR_MODEL_v005.getFeatureIndex())) {
                    featureEnumC1List = FeatureListConstant.FLC1_BASE_U_I_P;
                }
                if (modelId.equals(ModelKeyEnum.FTRL_FM_CVR_MODEL_v005.getFeatureIndex())) {
                    featureEnumC1List = FeatureListConstant.FLC1_BASE_U_I_P;
                }

                list = getFeatureCode1List(featureEnumC1List);
                System.out.println("featureCodeList=" + JSON.toJSONString(list));
                cache.put(modelId, list);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    public static List<FeatureBaseCode> getFeatureCode1List(List<FeatureEnumC1> featureEnumC1List) {

        List<FeatureBaseCode> list = new ArrayList<>();
        try {


            for (FeatureEnumC1 f : featureEnumC1List) {

                FeatureBaseCode info = new FeatureBaseCode();

                info.setName(f.getName());
                info.setCodeType(f.getCodeType());
                info.setSubLen(f.getSubLen());
                info.setHashNums(f.getHashNums());

                list.add(info);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

//    public static Map<String,FeatureBaseCode> getFeatureCodeMap(String modelId) {
//
//        Map<String,FeatureBaseCode> map = new HashMap<>();
//        try {
//
//            if (1 == 1 ||
//                    modelId.equals(ModelKeyEnum.FTRL_FM_CTR_MODEL_v001.getIndex()) ||
//                    modelId.equals(ModelKeyEnum.FTRL_FM_CVR_MODEL_v001.getIndex()) ||
//                    modelId.equals(ModelKeyEnum.FTRL_FM_CTR_MODEL_v002.getIndex()) ||
//                    modelId.equals(ModelKeyEnum.FTRL_FM_CVR_MODEL_v002.getIndex())
//                    ) {
//
//                for (FeatureEnumC1 f : FeatureListConstant.getFLC1001()) {
//
//                    FeatureBaseCode info = new FeatureBaseCode();
//                    info.setName(f.getName());
//                    info.setCodeType(f.getCodeType());
//                    info.setSubLen(f.getSubLen());
//                    info.setHashNums(f.getHashNums());
//
//                    map.put(f.getName(),info);
//                }
//
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return map;
//    }

    public static Feature featureCode(Map<String, String> featureMap, String modelId) {
        Feature feature = null;
        try {

            List<FeatureBaseCode> list = getFeatureCode(modelId);
            if (list != null) {
                feature = featureCode(featureMap, list);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return feature;
    }


    public static Feature code(List<FeatureCodeInfo> list) throws Exception {

        List<Integer> indices = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        List<Long> pLenList = new ArrayList<>();

        for (FeatureCodeInfo info : list) {

            if (info.codeType == 10) {
                FeatureCoderBase.getSubId(info.name, info.value, info.subLen, pLenList, indices, values);
            }
            if (info.codeType == 11) {
                FeatureCoderBase.getSubIds(info.name, info.value, ",", info.subLen, pLenList, indices, values);
            }

            if (info.codeType == 20) {
                FeatureCoderBase.getHashSubId(info.name, info.value, info.subLen, info.hashNums, pLenList, indices, values);
            }
            if (info.codeType == 21) {
                FeatureCoderBase.getHashSubIds(info.name, info.value, ",", info.subLen, info.hashNums, pLenList, indices, values);
            }

            if (info.codeType == 30) {
                FeatureCoderBase.getDictSubId(info.name, info.value, info.subLen, pLenList, indices, values);
            }
            if (info.codeType == 31) {
                FeatureCoderBase.getDictSubIds(info.name, info.value, ",", info.subLen, pLenList, indices, values);
            }
//
//            System.out.println("info="+JSON.toJSONString(info));
//            System.out.println("indices="+JSON.toJSONString(indices));
//            System.out.println("values="+JSON.toJSONString(values));
//            System.out.println("pLenList="+JSON.toJSONString(pLenList));
        }

        return FeatureCoderBase.toFeature(indices, values, pLenList);
    }


    public static List<Set<Long>> codeSet(List<FeatureCodeInfo> list) throws Exception {

        List<Integer> indices = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        List<Long> pLenList = new ArrayList<>();

        List<Set<Long>> ret = new ArrayList<>();

        for (FeatureCodeInfo info : list) {

            if (info.codeType == 10) {
                Set<Long> ret10 = FeatureCoderBase.getSubId(info.name, info.value, info.subLen, pLenList, indices, values);
                ret.add(ret10);
            }
            if (info.codeType == 11) {
                Set<Long> ret11 = FeatureCoderBase.getSubIds(info.name, info.value, ",", info.subLen, pLenList, indices, values);
                ret.add(ret11);
            }

            if (info.codeType == 20) {
                Set<Long> ret20 = FeatureCoderBase.getHashSubId(info.name, info.value, info.subLen, info.hashNums, pLenList, indices, values);
                ret.add(ret20);
            }
            if (info.codeType == 21) {
                Set<Long> ret21 = FeatureCoderBase.getHashSubIds(info.name, info.value, ",", info.subLen, info.hashNums, pLenList, indices, values);
                ret.add(ret21);
            }

            if (info.codeType == 30) {
                Set<Long> ret30 = FeatureCoderBase.getDictSubId(info.name, info.value, info.subLen, pLenList, indices, values);
                ret.add(ret30);
            }
            if (info.codeType == 31) {
                Set<Long> ret31 = FeatureCoderBase.getDictSubIds(info.name, info.value, ",", info.subLen, pLenList, indices, values);
                ret.add(ret31);
            }
//
//            System.out.println("info="+JSON.toJSONString(info));
//            System.out.println("indices="+JSON.toJSONString(indices));
//            System.out.println("values="+JSON.toJSONString(values));
//            System.out.println("pLenList="+JSON.toJSONString(pLenList));
        }

        return ret;
    }

    public static void main(String[] args) {
        Map<String, String> featureMap = new HashMap<>();
        featureMap.put("f101001", "3");
        featureMap.put("f102001", "d3,3");
        featureMap.put("f305001", "8");
        featureMap.put("f501001", "UNKONWN");

        List<FeatureEnumC1> list = new ArrayList<>();
        list.add(FeatureEnumC1.F102001);
        list.add(FeatureEnumC1.F101001);
        list.add(FeatureEnumC1.F305001);
        list.add(FeatureEnumC1.F501001);


        List<FeatureBaseCode> listc = getFeatureCode1List(list);

        Feature feature = featureCode(featureMap, listc);
        List<Set<Long>> featureSet = featureCodeSet(featureMap, listc);

        System.out.println("listc=" + JSON.toJSONString(listc));

        System.out.println("feature=" + JSON.toJSONString(feature));

        System.out.println("featureSet=" + JSON.toJSONString(featureSet));

        
    }

}

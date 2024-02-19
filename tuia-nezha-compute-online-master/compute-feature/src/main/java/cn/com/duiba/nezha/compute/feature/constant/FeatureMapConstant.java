package cn.com.duiba.nezha.compute.feature.constant;

import java.util.HashMap;
import java.util.Map;

public class FeatureMapConstant {

    public static String[] F501001 = new String[]{"android", "ios","unknow"};
    public static String[] F505001 = new String[]{"0-599", "600-1099","1100-1699","1700-2699","2700-4499","4500+"};



    public static Map<String, Map<String, Long>> cacheMap = new HashMap<>();


    public static Map<String, Long> getFeatureIdxMap(String feature) throws Exception {

        Map<String, Long> fMap = getFeatureCacheMap(feature);
        if (fMap == null) {
            setFeatureCacheMap(feature);
        }
        return getFeatureCacheMap(feature);
    }

    public static int getIdx(String feature, String str) throws Exception {

        int ret = 0;
        Map<String, Long> fMap = getFeatureIdxMap(feature);

        Long tmp = fMap.getOrDefault(str, 0L);
        if (tmp != null) {
            ret = tmp.intValue();
        }
        return ret;
    }

    public static void setFeatureCacheMap(String feature) throws Exception {

        switch (feature) {
            case "f501001":
                setFeatureCacheMap(feature, F501001);
                break;
            case "f505001":
                setFeatureCacheMap(feature, F505001);
                break;
            default:
                throw new Exception("setFeatureCacheMap default,invalid");
        }
    }


    public static void setFeatureCacheMap(String feature, String[] fList) {
        Map<String, Long> map = new HashMap<>();
        for (int i = 0; i < fList.length; i++) {
            map.put(fList[i], i + 1L);
        }
        cacheMap.put(feature, map);
    }

    public static Map<String, Long> getFeatureCacheMap(String feature) {
        return cacheMap.get(feature);
    }


}

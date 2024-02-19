package cn.com.duiba.nezha.engine.biz.support;


import cn.com.duiba.nezha.engine.common.utils.AssertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by pc on 2016/10/9.
 */
public class Standardize {

    private static final Logger logger = LoggerFactory.getLogger(Standardize.class);

    private Standardize() {
        throw new IllegalAccessError("Standardize class");
    }

    /**
     * map value 指定区间标准化
     *
     * @param map  map
     * @param sMin value标准化后，区间最大值
     * @param sMax value标准化后，区间最小值
     * @param <T>
     * @return
     */
    public static <T> Map<T, Double> mapValueIntervalStd(Map<T, Double> map, Double sMin, Double sMax) {
        List<Double> mapValueList = new ArrayList<>();
        Map<T, Double> retMap = new HashMap<>();
        // 参数检验
        if (AssertUtil.isEmpty(map) || sMin == null || sMax == null || sMin >= sMax) {
            logger.warn("mapValueIntervalStd param is invalid", "params invalid");
            return null;
        }

        for (Map.Entry<T, Double> entry : map.entrySet()) {
            mapValueList.add(entry.getValue());
        }
        Double mapMaxValue = Collections.max(mapValueList);
        Double mapMinValue = Collections.min(mapValueList);
        for (Map.Entry<T, Double> entry : map.entrySet()) {
            retMap.put(entry.getKey(), dataStd(entry.getValue(), mapMinValue, mapMaxValue, sMin, sMax));
        }

        return retMap;
    }


    /**
     * @param map
     * @param <T>
     * @return
     */
    public static <T> Map<T, Double> mapValueNormalized(Map<T, Double> map) {
        Map<T, Double> retMap = new HashMap<>();
        // 参数检验
        if (map == null || map.size() == 0) {
            logger.warn("Standardize.dataStd() param check invalid");
            return null;
        }
        Double mapValueSum = 0.0;
        for (Map.Entry<T, Double> entry : map.entrySet()) {
            mapValueSum += Math.max(entry.getValue(), 0);
        }

        if (mapValueSum > 0.0) {
            for (Map.Entry<T, Double> entry : map.entrySet()) {
                retMap.put(entry.getKey(), Math.max(entry.getValue(), 0) / mapValueSum);
            }
        }

        return retMap;
    }

    /**
     * @param map
     * @param <T>
     * @return
     */
    public static <T> Map<T, Double> mapValueLong2Double(Map<T, Long> map) {
        Map<T, Double> retMap = new HashMap<>();
        // 参数检验
        if (map == null || map.size() == 0) {
            logger.warn("Standardize.dataStd() param check invalid");
            return null;
        }

        for (Map.Entry<T, Long> entry : map.entrySet()) {
            Long value = entry.getValue();
            if (value != null) {
                retMap.put(entry.getKey(), entry.getValue() + 0.0);
            }

        }

        return retMap;
    }

    /**
     * @param x
     * @param xMin
     * @param xMax
     * @param sMin
     * @param sMax
     * @return
     */
    public static Double dataStd(Double x, Double xMin, Double xMax, Double sMin, Double sMax) {

        Double ret = null;

        try {
            if (Math.abs(xMax - xMin) < 0.000001) {
                ret = sMax;
            } else {
                ret = (x - xMin) * (sMax - sMin) / (xMax - xMin) + sMin;
            }
        } catch (Exception e) {
            logger.error("dataStd happened error:{}", e);
        }
        return ret;
    }
}

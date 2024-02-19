package cn.com.duiba.nezha.engine.biz.support;

import cn.com.duiba.nezha.engine.common.utils.AssertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by pc on 2016/10/10.
 */
public class Roulette {

    private static final Logger logger = LoggerFactory.getLogger(Roulette.class);
    private static Random rand = new Random();


    private Roulette() {
        throw new IllegalAccessError("Roulette class");
    }

    /**
     * 轮盘赌
     *
     * @param map map
     * @param <T>
     * @return
     */
    public static <T> T doubleMap(Map<T, Double> map) {

        // 参数检验
        if (AssertUtil.isEmpty(map)) {
            logger.warn("mapSample param is invalid", "params invalid");
            return null;
        }
        Double mapValueSum = 0.0;

        for (Map.Entry<T, Double> entry : map.entrySet()) {
            mapValueSum = mapValueSum + Math.max(entry.getValue(), 0);
        }
        Map<T, Double> mapStd = Standardize.mapValueNormalized(map);
        double r = rand.nextDouble();
        double w = 0.0;
        try {
            for (Map.Entry<T, Double> entry : mapStd.entrySet()) {
                w += entry.getValue();
                if (r <= w) {
                    return entry.getKey();
                }
            }
        } catch (Exception e) {
            logger.error("mapSample happened error:{}", e);

        }
        return null;
    }

    /**
     * 轮盘赌
     *
     * @param map map
     * @param <T>
     * @return
     */
    public static <T> T longMap(Map<T, Long> map) {
        T ret = null;
        // 参数检验
        if (AssertUtil.isEmpty(map)) {
            logger.warn("mapSample param is invalid", "params invalid");
            return ret;
        }
        try {
            Map<T, Double> mapCopy = Standardize.mapValueLong2Double(map);
            ret = doubleMap(mapCopy);


        } catch (Exception e) {
            logger.error("mapSample happened error:{}", e);

        }
        return ret;
    }

}


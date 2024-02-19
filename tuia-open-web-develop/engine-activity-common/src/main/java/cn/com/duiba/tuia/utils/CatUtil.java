package cn.com.duiba.tuia.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.cat.Cat;

/**
 * Created by sunjiangrong . 16/11/15 .
 */
public class CatUtil {

    private static final Logger logger = LoggerFactory.getLogger(CatUtil.class);

    private CatUtil() {

    }

    /**
     * 计数方法
     * 
     * @param serviceName 计数点名称
     */
    public static void log(String serviceName) {
        try {
            Cat.logMetricForCount(serviceName);
        } catch (Exception e) {
            logger.error("CatUtil log error", e);
        }
    }
}

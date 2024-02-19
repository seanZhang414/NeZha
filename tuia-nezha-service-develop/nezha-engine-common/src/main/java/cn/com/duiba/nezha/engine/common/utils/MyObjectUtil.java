package cn.com.duiba.nezha.engine.common.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Created by pc on 2016/9/2.
 */
public class MyObjectUtil {
    private static final Logger logger = LoggerFactory.getLogger(MyObjectUtil.class);

    private MyObjectUtil() {
        throw new IllegalAccessError("MyObjectUtil class");
    }

    public static Long string2long(String str) {

        try {
            return Optional.ofNullable(str).map(Long::parseLong).orElse(null);
        } catch (Exception e) {
            logger.warn("long2String happened error:{}", e);
            return null;
        }
    }
}

package cn.com.duiba.nezha.engine.common.utils;

import java.math.BigDecimal;

/**
 * Created by pc on 2016/9/2.
 */
public class FormatUtil {
    private FormatUtil() {
        throw new IllegalAccessError("FormatUtil class");
    }

    public static Double doubleFormat(Double d, int bsize) {
        Double ret = null;
        if (d != null) {
            BigDecimal bg = new BigDecimal(d);
            ret = bg.setScale(Math.max(bsize, 0), BigDecimal.ROUND_HALF_UP).doubleValue();
        }

        return ret;
    }

}

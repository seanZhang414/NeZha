package cn.com.duiba.nezha.compute.stat.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by pc on 2017/9/12.
 */
public class MathUtil {



    public static Double add(Double v1, Double v2) {
        Double ret = 0.0;
        if (v1 != null) {
            ret += v1;
        }
        if (v2 != null) {
            ret += v2;
        }
        return ret;
    }


    public static Long add(Long v1, Long v2) {
        Long ret = 0L;
        if (v1 != null) {
            ret += v1;
        }
        if (v2 != null) {
            ret += v2;
        }
        return ret;
    }

    public static Long dot(Long v1, Long v2) {
        Long ret = 0L;
        if (v1 != null && v2 != null) {
            ret = v1 * v2;
        }
        return ret;
    }

    public static Double dot(Double v1, Long v2) {
        Double ret = 0.0;
        if (v1 != null && v2 != null) {
            ret = v1 * v2;
        }
        return ret;
    }


    public static Double dot(Double v1, Double v2) {
        Double ret = 0.0;
        if (v1 != null && v2 != null) {
            ret = v1 * v2;
        }
        return ret;
    }




    public static Double division(Long v1, Long v2, int newScala) {
        Double ret = null;
        if (v1 != null && v2 != null && v2 >= 1) {
            ret = division(toDouble(v1), toDouble(v2), newScala);
        }
        return ret;
    }

    public static Double division(Double v1, Double v2, int newScala) {
        Double ret = null;
        if (v1 != null && v2 != null && v2 >= 1) {
            ret = v1 / v2;
            if (ret != null) {
                ret = formatDouble(ret.doubleValue(), newScala);
            }
        }
        return ret;
    }


    public static Double division(Long v1, Double v2, int newScala) {
        Double ret = null;
        if (v1 != null && v2 != null && v2 >= 1) {
            ret = v1 / v2;
            if (ret != null) {
                ret = formatDouble(ret.doubleValue(), newScala);
            }
        }
        return ret;
    }

    public static Double division(Double v1, Long v2, int newScala) {
        Double ret = null;
        if (v1 != null && v2 != null && v2 >= 1) {
            ret = division(v1, toDouble(v2), newScala);
        }
        return ret;
    }


    public static Long division(Long v1, Long v2) {
        Long ret = null;
        if (v1 != null && v2 != null && v2 >= 1) {
            Double d = toDouble(v1) / toDouble(v2);
            if (d != null) {
                ret = Math.round(d);
            }
        }
        return ret;
    }


    public static Double division(Double v1, Long v2) {
        Double ret = null;
        if (v1 != null && v2 != null && v2 >= 1) {
            Double d = v1 / toDouble(v2);
            if (d != null) {
                ret = d;
            }
        }
        return ret;
    }


    /**
     * 数据类型转换
     *
     * @param v1
     * @return
     */
    public static Double toDouble(Long v1) {
        Double ret = 0.0;
        if (v1 != null) {
            ret = v1 + 0.0;
        }
        return ret;
    }
    /**
     * 数据类型转换
     *
     * @param v1
     * @return
     */
    public static double toddouble(Double v1) {
        double ret = 0.0;
        if (v1 != null) {
            ret = v1 + 0.0;
        }
        return ret;
    }



    /**
     * 数据类型转换
     *
     * @param v1
     * @return
     */
    public static long tolong(Long v1) {
        long ret = 0;
        if (v1 != null) {
            ret = v1.longValue();
        }
        return ret;
    }


    /**
     * DecimalFormat is a concrete subclass of NumberFormat that formats decimal numbers.
     *
     * @param d
     * @return
     */
    public static double formatDouble(double d, int newScala) {
        BigDecimal bg = new BigDecimal(d).setScale(newScala, RoundingMode.UP);

        return bg.doubleValue();
    }

}

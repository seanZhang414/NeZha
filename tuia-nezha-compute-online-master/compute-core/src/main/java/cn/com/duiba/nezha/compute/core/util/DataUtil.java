package cn.com.duiba.nezha.compute.core.util;




import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by pc on 2017/9/7.
 */
public class DataUtil {





    /**
     * @param value
     * @return
     */
    public static Double toDouble(Long value) {
        return long2Double(value, 1L, 0);
    }


    public static double todouble(Double value) {
        double ret = 0;
        if (value != null) {
            ret = value;
        }
        return ret;
    }

    public static int toInt(Long value) {
        int ret = 0;
        if (value != null) {
            ret = value.intValue();
        }
        return ret;
    }

    /**
     * @param value
     * @param amplification
     * @return
     */
    public static Double long2Double(Long value, Long amplification, int newScala) {
        Double ret = null;
        if (value != null) {
            ret = (value + 0.0) / amplification;

            if (ret != null) {
                ret = formatDouble(ret.doubleValue(), newScala);
            }

        }
        return ret;
    }




    /**
     * @param value
     * @param amplification
     * @return
     */
    public static double long2double(long value, long amplification, int newScala) {
        double ret = formatdouble((value + 0.0) / amplification, newScala);
        return ret;
    }


    /**
     * 位数截取
     *
     * @param d
     * @return
     */
    public static Double formatDouble(Double d, int newScala) {
        Double ret = null;
        if (d != null) {
            BigDecimal bg = new BigDecimal(d).setScale(newScala, RoundingMode.UP);
            ret = bg.doubleValue();
        }
        return ret;
    }


    /**
     * 位数截取
     *
     * @param d
     * @return
     */
    public static double formatdouble(double d, int newScala) {

        BigDecimal bg = new BigDecimal(d).setScale(newScala, RoundingMode.UP);
        double ret = bg.doubleValue();
        return ret;
    }



    /**
     * @param value
     * @return
     */
    public static Long double2Long(Double value) {
        return double2Long(value, 1L);
    }

    /**
     * @param value
     * @param amplification
     * @return
     */
    public static Long double22Long(Double value, Long amplification) {
        Long ret = null;
        if (amplification == null) {
            amplification = 1L;
        }

        if (value != null) {
            ret = Math.round(value * amplification);
        }
        return ret;
    }

    /**
     * @param value
     * @param amplification
     * @return
     */
    public static long double2long(double value, long amplification) {
        return Math.round(value * amplification);
    }

    /**
     * @param value
     * @param amplification
     * @return
     */
    public static Long double2Long(double value, Long amplification) {
        return Math.round(value * amplification);
    }


    /**
     * 判断广告对用户的发券类型   首次发  重复发
     *
     * @return
     */
    public static Long addLong(Long v1, Long v2) {
        Long ret = 0L;
        try {
            if (v1 != null) {
                ret = ret + v1;
            }
            if (v2 != null) {
                ret = ret + v2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }


    /**
     * 判断广告对用户的发券类型   首次发  重复发
     *
     * @return
     */
    public static Double addDouble(Double v1, Double v2, int newScala) {

        return formatDouble(addDouble(v1, v2), newScala);
    }

    /**
     * 判断广告对用户的发券类型   首次发  重复发
     *
     * @return
     */
    public static Double addDouble(Double v1, Double v2) {
        Double ret = 0.0;
        try {
            if (v1 != null) {
                ret = ret + v1;
            }
            if (v2 != null) {
                ret = ret + v2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * 判断广告对用户的发券类型   首次发  重复发
     *
     * @return
     */
    public static Double multiplyDouble(Double v1, Double v2) {
        Double ret = null;
        try {
            if (v1 != null && v2 != null) {
                ret = v1 * v2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }


    /**
     * 判断广告对用户的发券类型   首次发  重复发
     *
     * @return
     */
    public static Double multiply(Double v1, Long v2) {
        Double ret = null;
        try {
            if (v1 != null && v2 != null) {
                ret = v1 * v2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }


    /**
     * 判断广告对用户的发券类型   首次发  重复发
     *
     * @return
     */
    public static Long multiplyLong(Long v1, Long v2) {
        Long ret = null;
        try {
            if (v1 != null && v2 != null) {
                ret = v1 * v2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }


    public static Double division(Double v1, Long v2, int newScala) {
        return formatDouble(division(v1, v2), newScala);
    }

    public static Double division(Double v1, Long v2) {
        return division(v1, toDouble(v2));
    }

    public static Double division(Long v1, Double v2, int newScala) {
        return formatDouble(division(v1, v2), newScala);
    }


    public static Double division(Long v1, Double v2) {
        return division(toDouble(v1), v2);
    }


    public static Double division(Long v1, Long v2, int newScala) {

        return formatDouble(division(v1, v2), newScala);
    }

    public static Double division(Long v1, Long v2) {

        return division(toDouble(v1), toDouble(v2));
    }


    public static Double division(Double v1, Double v2, int newScala) {
        return formatDouble(division(v1, v2), newScala);
    }

    public static Double division(Double v1, Double v2) {
        Double ret = null;
        try {
            if (v1 != null && v2 != null) {
                ret = v1 / (v2 + 0.0000000001);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }


}

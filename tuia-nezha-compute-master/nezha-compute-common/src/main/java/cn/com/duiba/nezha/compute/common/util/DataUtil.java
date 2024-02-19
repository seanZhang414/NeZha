package cn.com.duiba.nezha.compute.common.util;

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
    public static Long str2Long(String value, Long defaultVal) {
        Long ret = defaultVal;
        if (value != null && value != "" && value != "null") {
            try {
                ret = Long.valueOf(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ret;
    }


    /**
     * @param value
     * @return
     */
    public static Double long2Double(Long value) {
        return long2Double(value, 1L, 0);
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
    public static Long double2Long(Double value, Long amplification) {
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
        return division(v1, long2Double(v2));
    }

    public static Double division(Long v1, Double v2, int newScala) {
        return formatDouble(division(v1, v2), newScala);
    }


    public static Double division(Long v1, Double v2) {
        return division(long2Double(v1), v2);
    }


    public static Double division(Long v1, Long v2, int newScala) {

        return formatDouble(division(v1, v2), newScala);
    }

    public static Double division(Long v1, Long v2) {

        return division(long2Double(v1), long2Double(v2));
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

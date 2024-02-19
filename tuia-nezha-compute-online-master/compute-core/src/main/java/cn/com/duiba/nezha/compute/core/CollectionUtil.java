package cn.com.duiba.nezha.compute.core;

import cn.com.duiba.nezha.compute.core.util.DataUtil;
import scala.collection.Iterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionUtil {


    public static String toString(double[] value) throws Exception {
        String ret = null;
        if (value != null) {
            StringBuilder s = new StringBuilder();
            for (double v : value) {
                s.append("," + v);
            }

            ret = s.toString();
        }
        return ret;
    }

    public static List<String> toList(Iterator<String> partitionOfRecords) throws Exception {

        List<String> orderIdList = new ArrayList<>();
        while (partitionOfRecords.hasNext()) {
            orderIdList.add(partitionOfRecords.next());

        }
        return orderIdList;
    }

    public static int[] toArray(List<Long> value) {
        if (value == null) {
            value = new ArrayList<>();
        }
        int[] ret = new int[value.size()];

        for (int i = 0; i < value.size(); i++) {
            if (value.get(i) != null) {
                ret[i] = value.get(i).intValue();
            } else {
                ret[i] = 0;
            }

        }
        return ret;
    }


    /**
     * double数组，放大一定倍数，转long数组，
     *
     * @param value
     * @param multiples
     * @return
     */
    public static long[] double2long(double[] value, long multiples) {
        long[] ret = new long[value.length];

        for (int i = 0; i < value.length; i++) {
            ret[i] = DataUtil.double2long(value[i], multiples);
        }
        return ret;
    }


    /**
     * double数组，放大一定倍数，转long数组，
     *
     * @param value
     * @param multiples
     * @return
     */
    public static Long[] double2Long(double[] value, Long multiples) {
        Long[] ret = new Long[value.length];

        for (int i = 0; i < value.length; i++) {
            ret[i] = DataUtil.double2long(value[i], multiples);
        }
        return ret;
    }


    /**
     * long数组，缩小一定倍数，转double数组，
     *
     * @param value
     * @param multiples
     * @param newScala  保留小数位数
     * @return
     */
    public static double[] long2double(long[] value, long multiples, int newScala) {
        double[] ret = new double[value.length];

        for (int i = 0; i < value.length; i++) {
            ret[i] = DataUtil.long2double(value[i], multiples, newScala);
        }
        return ret;
    }


    /**
     * Long数组，缩小一定倍数，转double数组，
     *
     * @param value
     * @param multiples
     * @param newScala  保留小数位数
     * @return
     */
    public static double[] long22double(Long[] value, long multiples, int newScala) {
        double[] ret = new double[value.length];

        for (int i = 0; i < value.length; i++) {
            Long v = value[i];
            if (v == null) {
                v = 0L;
            }
            ret[i] = DataUtil.long2double(v, multiples, newScala);

        }
        return ret;
    }

    /**
     * Long数组，缩小一定倍数，转double数组，
     *
     * @param value
     * @param multiples
     * @param newScala  保留小数位数
     * @return
     */
    public static double[] long22double(List<Long> value, long multiples, int newScala) {
        double[] ret = new double[value.size()];

        for (int i = 0; i < value.size(); i++) {
            Long v = value.get(i);
            if (v == null) {
                v = 0L;
            }
            ret[i] = DataUtil.long2double(v, multiples, newScala);

        }
        return ret;
    }

    /**
     * int数组，转String数组，
     *
     * @param value
     * @return
     */
    public static String[] int2String(int[] value) {
        String[] ret = new String[value.length];

        for (int i = 0; i < value.length; i++) {
            ret[i] = value[i] + "";
        }
        return ret;
    }

    /**
     * long数组，转String数组，
     *
     * @param value
     * @return
     */
    public static Map<String, String> toMap(String[] key, String[] value) {
        Map<String, String> ret = new HashMap<>();

        if (key.length != value.length) {
            return null;
        }
        for (int i = 0; i < value.length; i++) {
            ret.put(String.valueOf(key[i]), String.valueOf(value[i]));
        }
        return ret;
    }


    /**
     * long数组，转String数组，
     *
     * @param value
     * @return
     */
    public static String[] long2String(long[] value) {
        String[] ret = new String[value.length];

        for (int i = 0; i < value.length; i++) {
            ret[i] = String.valueOf(value[i]);
        }
        return ret;
    }


}

package cn.com.duiba.nezha.compute.common.util;

import org.apache.hadoop.hbase.util.Bytes;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by pc on 2017/2/17.
 */
public class MyStringUtil2 {

    public static String stringStd(String src) {
        if (src == "") {
            return null;
        } else {
            return src;
        }
    }

    public static String Long2String(Long src) {
        String ret = null;
        if (src != null) {
            ret = Long.toString(src);
        }
        return ret;

    }

    public static Long string2Long(String src) {
        Long ret = null;
        if (src != null) {
            ret = Long.valueOf(src);
        }
        return ret;

    }



    public static String Integer2String(Integer src) {
        String ret = null;
        if (src != null) {
            ret = Integer.toString(src);
        }
        return ret;

    }

    public static String double2String(Double src) {
        String ret = null;
        if (src != null) {
            ret = Double.toString(src);
        }
        return ret;

    }


    public static String bytesToLongString(byte[] bytes) {
        String ret = null;
        if (bytes != null) {
            Long retLong = Bytes.toLong(bytes);
            if (retLong != null) {
                ret = retLong.toString();
            }
        }


        return ret;
    }

    public static Long bytesToLong(byte[] bytes) {
        Long ret = null;
        if (bytes != null) {
            ret = Bytes.toLong(bytes);
            if (ret == null) {
                ret = 0L;
            }
        }

        return ret;
    }


    public static List<String> stringToList(String src, String sep) {
        List<String> ret = null;
        if (src != null && sep != null) {
            String[] tmp = src.split(sep);
            ret = Arrays.asList(tmp);
        }
        return ret;
    }

    public static String setToString(Set<String> set, String sep) {
        String ret = null;
        if (set != null && sep != null) {
            ret = org.apache.commons.lang.StringUtils.join(set.toArray(), sep);
        }
        return ret;
    }


    public static String listToString(List<String> list, String sep) {
        String ret = null;
        if (list != null && sep != null) {
            ret = org.apache.commons.lang.StringUtils.join(list.toArray(), sep);
        }
        return ret;
    }


}

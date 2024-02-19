package cn.com.duiba.nezha.compute.core.util;

import com.alibaba.fastjson.JSON;

import java.util.Map;

public class JSONUtil {

    public static <T> Map<String, String> getObjectMap(T t) {
        Map<String, String> ret = null;
        String jString = JSON.toJSONString(t);

        ret = (Map) JSON.parse(jString);

        return ret;

    }
}

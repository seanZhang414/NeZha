package cn.com.duiba.tuia.engine.activity.api;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.binary.Base64;

import cn.com.duiba.tuia.utils.GzipUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 算法：把表格中的所有字段拼接成a=b&c=d的形式，用gzip压缩，最后用base64编码，把结果记为$A 上传接口的实际提交字段为(n)sdata=$A&time=**&token=**
 * (注意sdata、nsdata需要放在post的body中提交)，token生成只需要用到(n)sdata和time，不需要用到原始数据。 (由于用的是https接口，实际上https会为我们做加密)
 */
public class SdkCipherUtils {

    private SdkCipherUtils() {
    }

    /**
     * toQueryString
     * 
     * @param bean
     * @return String
     */
    public static String toQueryString(Object bean) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        StringBuilder builder = new StringBuilder();
        Map<String, String> map = BeanUtils.describe(bean);
        Map<String, String> sortedMap = new TreeMap<>(map);

        for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
            if (!"class".equals(entry.getKey()) && !"time".equals(entry.getKey()) && !"token".equals(entry.getKey())) {
                builder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }

    /**
     * toPropertyMap
     * 
     * @param queryString
     * @return Map
     */
    public static Map<String, Object> toPropertyMap(String queryString) {
        if (StringUtils.isBlank(queryString)) {
            return Collections.emptyMap();
        }

        Map<String, Object> map = new HashMap<>();
        String[] params = queryString.split("&");
        for (String param : params) {
            String[] kv = param.split("=");
            map.put(kv[0], kv.length > 1 ? kv[1] : null);
        }
        return map;
    }

    /**
     * toPropertyBean
     * 
     * @param bean
     * @param queryString
     * @return bean
     */
    public static <T> T toPropertyBean(T bean, String queryString) throws InvocationTargetException, IllegalAccessException {
        Map<String, Object> properties = toPropertyMap(queryString);
        BeanUtils.populate(bean, properties);
        return bean;
    }

    /**
     * encodeData
     * 
     * @param data
     * @return String
     */
    public static String encodeData(String data) throws IOException {
        if (StringUtils.isBlank(data)) {
            return null;
        }
        byte[] zipedData = GzipUtils.zip(data.getBytes());
        return Base64.encodeBase64String(zipedData);
    }

    /**
     * decodeData
     * 
     * @param data
     * @return String
     */
    public static String decodeData(String data) throws IOException {
        if (StringUtils.isBlank(data)) {
            return null;
        }
        byte[] zipedData = Base64.decodeBase64(data);
        return new String(GzipUtils.unzip(zipedData));
    }
}

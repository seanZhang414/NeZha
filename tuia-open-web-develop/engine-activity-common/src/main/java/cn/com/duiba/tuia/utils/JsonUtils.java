package cn.com.duiba.tuia.utils;
/**
 * 文件名： JsonUtils.java 此类描述的是： 作者: leiliang 创建时间: 2016年3月23日 上午10:48:33
 */

import cn.com.duiba.tuia.exception.TuiaRuntimeException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * json相关 <功能详细描述>
 * 
 * @author: leiliang
 * @version:
 */
public class JsonUtils {

    public static final ObjectMapper mapper = new ObjectMapper();

    private JsonUtils() {

    }

    /**
     * 对象转json
     * 
     * @param obj
     * @return String
     */
    public static String objectToString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new TuiaRuntimeException(e);
        }
    }

    /**
     * json转对象
     * 
     * @param clazz
     * @param json
     * @return object
     */
    @SuppressWarnings("unchecked")
    public static <T> T jsonToObject(Class<?> clazz, String json) {
        try {
            return (T) mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new TuiaRuntimeException(e);
        }
    }
}

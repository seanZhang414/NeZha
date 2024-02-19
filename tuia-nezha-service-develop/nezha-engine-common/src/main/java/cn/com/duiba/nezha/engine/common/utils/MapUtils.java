package cn.com.duiba.nezha.engine.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MapUtils {

    private MapUtils() {
        //private
    }

    public static <Q, K, V> Map<Q, V> translate(Map<Q, K> queryKeyMap, Map<K, V> keyValueMap) {
        // 参数校验
        Objects.requireNonNull(queryKeyMap, "argument can not be null");
        Objects.requireNonNull(keyValueMap, "argument can not be null");

        // new出来一个新的返回对象
        Map<Q, V> map = new HashMap<>(queryKeyMap.size());

        // 根据K进行关系对应.完成Q,V的转换
        queryKeyMap.forEach((query, key) -> map.put(query, keyValueMap.get(key)));

        // 返回对象
        return map;
    }
}

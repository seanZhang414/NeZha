package cn.com.duiba.nezha.engine.common.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: StringRedisHelper.java , v 0.1 2017/12/4 上午11:11 ZhouFeng Exp $
 */
public class StringRedisHelper {

    private StringRedisTemplate stringRedisTemplate;

    private StringRedisHelper(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public static StringRedisHelper of(StringRedisTemplate stringRedisTemplate) {
        return new StringRedisHelper(stringRedisTemplate);
    }

    public <T> Map<String, T> valueMultiGet(Iterable<? extends String> ks, Class<T> clazz, Supplier<T> emptySupplier) {

        Map<String, T> map = MapUtils.EMPTY_MAP;


        List<String> keys = Lists.newArrayList(ks);

        List<String> record = stringRedisTemplate.opsForValue().multiGet(keys);

        if (CollectionUtils.isEmpty(record)) {
            return map;
        }

        map = new HashMap<>(record.size());

        for (int index = 0; index < record.size(); index++) {

            String key = keys.get(index);
            String value = record.get(index);

            if (StringUtils.isNotBlank(value)) {
                map.put(key, JSON.parseObject(value, clazz));
            } else {
                map.put(key, emptySupplier.get());
            }

        }

        return map;

    }


}

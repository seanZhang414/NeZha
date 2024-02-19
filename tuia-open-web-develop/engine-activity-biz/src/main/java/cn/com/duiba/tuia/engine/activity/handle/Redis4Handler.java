package cn.com.duiba.tuia.engine.activity.handle;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class Redis4Handler<K, HK, V> {

    @Resource
    private RedisTemplate<K, V> redisTemplate;

    /**
     * 散列域赋值
     *
     * @param key     散列key
     * @param hashKey 域key
     * @param value
     */
    public void hSet(K key, HK hashKey, V value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 散列删除域值
     *
     * @param key     散列key
     * @param hashKey 域key
     */
    public void hDel(K key, Object... hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    public V get(K key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void set(K key, V o, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, o, timeout, unit);
    }

    /**
     * 在不确定数据量的情况下不要使用
     */
    @SuppressWarnings("unchecked")
    public Map<K, V> entries(K key) {
        return (Map<K, V>) redisTemplate.opsForHash().entries(key);
    }

    /**
     * 删除值
     *
     * @param key key
     */
    public void del(K key) {
        redisTemplate.delete(key);
    }
}

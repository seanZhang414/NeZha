package cn.com.duiba.tuia.engine.activity.handle;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class Redis3Handler {

    @Resource(name = "redisTemplate03")
    private StringRedisTemplate stringRedisTemplate02;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 删除
     *
     * @param key key
     */
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 自增自定义数值
     *
     * @param key   key
     * @param delta 增量
     * @return 自增后的值
     */
    public Long increment(String key, long delta) {
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 设置过期时间--一般配合redis其他操作使用，存在并发风险，慎用！！
     *
     * @param key     key
     * @param timeout 时间
     * @param unit    单位
     * @return 是否成功
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return stringRedisTemplate.expire(key, timeout, unit);
    }

    /**
     * 散列获取域值
     *
     * @param key     散列key
     * @param hashKey 域key
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public <V> V hGet(String key, Object hashKey) {
        return (V) stringRedisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 散列域赋值
     *
     * @param key     散列key
     * @param hashKey 域key
     * @param value   值
     */
    public void hSet(String key, Object hashKey, Object value) {
        stringRedisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 散列删除域值
     *
     * @param key     散列key
     * @param hashKey 域key
     */
    public void hDel(String key, Object... hashKey) {
        stringRedisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * 散列域值递增固定值
     *
     * @param key     散列key
     * @param hashKey 域key
     * @param delta   递增数目
     * @return 递增后的值
     */
    public Long hIncr(String key, Object hashKey, Long delta) {
        return stringRedisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    // redis02 跟tuia-activity-web数据交互用
    @SuppressWarnings("unchecked")
    public <V> V get(String key) {
        return (V) stringRedisTemplate02.opsForValue().get(key);
    }

    @SuppressWarnings("unchecked")
    public <K> Set<K> hGetKeys(String key) {
        return (Set<K>) stringRedisTemplate02.opsForHash().keys(key);
    }

    public void set(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate02.opsForValue().set(key, value, timeout, unit);
    }

    public void set(String key, String value, long offset) {
        stringRedisTemplate02.opsForValue().set(key, value,offset);
    }
    // end redis02

    public void flushAll() {
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.flushAll();
            return null;
        });
    }

    public Object execute(String command, byte[]... args) {
        return stringRedisTemplate.execute((RedisCallback<Object>) connection -> connection.execute(command, args));
    }
}

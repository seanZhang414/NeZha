package cn.com.duiba.nezha.engine.common.cache;

/**
 * Created by pc on 2016/8/26.
 */

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * redis cache 工具类
 */
public class RedisUtil {
    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);
    private RedisTemplate<Serializable, Object> redisTemplate;


    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate
                    .opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            logger.warn("set object  happened error:{}", e);
        }
        return result;
    }


    /**
     * @param key
     * @param list
     * @param expireTime
     * @return
     */
    public boolean setList(final String key, Object list, Long expireTime) {
        boolean result = false;
        try {
            String value = JSON.toJSONString(list);

            if (key != null) {
                result = set(key, value, expireTime);
            }
        } catch (Exception e) {
            logger.warn("setList happened error:{}", e);
        }
        return result;
    }



    /**
     * 获取list
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> getList(final String key, Class clazz) {
        List<T> result = null;
        try {
            Object value = get(key);
            result = JSON.parseArray((String) value, clazz);
        } catch (Exception e) {
            logger.warn("getList happened error:{},key:{}", e, key);
        }
        return result;
    }

    public void setRedisTemplate(
            RedisTemplate<Serializable, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public Object get(final String key) {

        ValueOperations<Serializable, Object> operations = redisTemplate
                .opsForValue();
        return operations.get(key);
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public  <T> T  get(final String key, Class clazz) {
        T result = null;
        Object value = get(key);
        if(value!=null){
            result = (T)JSON.parseObject((String)value, clazz);
        }

        return result;
    }


    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate
                    .opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            logger.error("set happened error:{}", e);
        }
        return result;
    }




}
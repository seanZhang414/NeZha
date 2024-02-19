package cn.com.duiba.nezha.compute.biz.utils.jedis;


import cn.com.duiba.nezha.compute.common.util.AssertUtil;
import com.alibaba.fastjson.JSON;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pc on 2016/11/18.
 */
public class JedisUtil {

    private JedisConfig jedisConfig;

    public JedisUtil(JedisConfig jedisConfig) {
        this.jedisConfig = jedisConfig;
    }

    public Jedis getJedis() {
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getInstance().getJedis(jedisConfig);
        } catch (Exception e) {
            System.out.println("redis error " + e);
        }
        return jedis;
    }


    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    public void remove(final String... keys) {
        try {
            for (String key : keys) {
                remove(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除对应的value
     *
     * @param key
     */
    public void remove(final String key) {
        Jedis jedis = getJedis();
        boolean isBroken = false;
        try {
            if (exists(key)) {

                jedis.del(key);
            }
        } catch (Exception e) {
            isBroken = true;
            System.out.println("redis error " + e);
        } finally {
            returnResource(jedis, isBroken);
        }

    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        boolean ret = false;

        Jedis jedis = getJedis();
        boolean isBroken = false;
        try {
            ret = jedis.exists(key);
        } catch (Exception e) {
            isBroken = true;
            System.out.println("redis error " + e);
        } finally {

            returnResource(jedis, isBroken);
        }
        return ret;
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public Object getObject(final String key) {
        Object result = null;
        boolean isBroken = false;
        Jedis jedis = getJedis();
        try {
            result = (Object) jedis.get(key);

        } catch (Exception e) {
            isBroken = true;
            System.out.println("redis error " + e);
        } finally {
            returnResource(jedis, isBroken);
        }
        return result;
    }


    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public String get(final String key) {
        String result = null;
        boolean isBroken = false;
        Jedis jedis = getJedis();
        try {
            result = jedis.get(key);
        } catch (Exception e) {
            isBroken = true;
            System.out.println("redis error " + e);
        } finally {
            returnResource(jedis, isBroken);
        }
        return result;
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public <T> T get(final String key, Class clazz) {
        T result = null;
        boolean isBroken = false;
        Jedis jedis = getJedis();
        try {
            Object value = getObject(key);
            result=(T)JSON.parseObject((String) value, clazz);
        } catch (Exception e) {
            isBroken = true;
            System.out.println("redis error " + e);
        } finally {
            returnResource(jedis, isBroken);
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
    public boolean setObject(final String key, Object value) {
        boolean result = false;
        boolean isBroken = false;
        Jedis jedis = getJedis();
        try {

            jedis.set(key, JSON.toJSONString(value));
            result = true;
        } catch (Exception e) {
            isBroken = true;
            System.out.println("redis error " + e);
        } finally {
            returnResource(jedis, isBroken);
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
    public boolean set(final String key, String value) {
        boolean result = false;
        boolean isBroken = false;
        Jedis jedis = getJedis();
        try {
            jedis.set(key, value);
            result = true;
        } catch (Exception e) {
            isBroken = true;
            System.out.println("redis error " + e);
        } finally {
            returnResource(jedis, isBroken);
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
    public boolean setexObject(final String key, Object value, int seconds) {
        boolean result = false;
        boolean isBroken = false;
        Jedis jedis = getJedis();
        try {
            jedis.setex(key, seconds, JSON.toJSONString(value));
            result = true;
        } catch (Exception e) {
            isBroken = true;
            System.out.println("redis error " + e);
        } finally {
            returnResource(jedis, isBroken);
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
    public boolean setex(final String key, String value, int seconds) {
        boolean result = false;
        boolean isBroken = false;
        Jedis jedis = getJedis();
        try {
            jedis.setex(key, seconds, value);
            result = true;
        } catch (Exception e) {
            isBroken = true;
            System.out.println("redis error " + e);
        } finally {
            returnResource(jedis, isBroken);
        }
        return result;
    }

    /**
     * 获取list
     *
     * @param key
     * @return list
     */
    public <T> List<T> getList(final String key, Class clazz) {
        List<T> result = null;
        try {
            Object value = getObject(key);
            result = JSON.parseArray((String) value, clazz);
        } catch (Exception e) {
            System.out.println("redis error " + e);
        }
        return result;
    }

    /**
     * 获取list
     *
     * @param key
     * @param list
     * @return
     */
    public boolean setexList(final String key, Object list, int seconds) {
        boolean result = false;
        boolean isBroken = false;
        Jedis jedis = getJedis();
        try {
            String value = JSON.toJSONString(list);
            jedis.setex(key, seconds, value);
            result = true;
        } catch (Exception e) {
            isBroken = true;
            System.out.println("redis error " + e);
        } finally {
            returnResource(jedis, isBroken);
        }
        return result;
    }



    /**
     * 获取list
     *
     * @param kvMap
     * @return
     */
    public <T> boolean multiSetexT(final Map<String,  T> kvMap, int seconds) {
        boolean result = false;
        boolean isBroken = false;
        Jedis jedis = getJedis();
        Pipeline p = jedis.pipelined();
        try {
            if (AssertUtil.isNotEmpty(kvMap)) {
                int index = 0;
                for (Map.Entry<String, T> entry : kvMap.entrySet()) {
                    String key = entry.getKey();
                    T value = entry.getValue();
                    String jsonValue = JSON.toJSONString(value);
                    if (AssertUtil.isAllNotEmpty(key, jsonValue)) {
                        p.set(key, jsonValue);
                        p.expire(key, seconds);

                        if (++index % 1000 == 0) {
                            p.sync();
                        }
                    }
                }
                p.sync();
                result = true;
            }
        } catch (Exception e) {
            isBroken = true;
            System.out.println("redis error " + e);
        } finally {
            returnResource(jedis, isBroken);
        }
        return result;
    }

    /**
     * 获取list
     *
     * @param kvMap
     * @return
     */
    public boolean multiSetex(final Map<String, String> kvMap, int seconds) {
        boolean result = false;
        boolean isBroken = false;
        Jedis jedis = getJedis();
        Pipeline p = jedis.pipelined();
        try {
            if (AssertUtil.isNotEmpty(kvMap)) {
                int index = 0;
                for (Map.Entry<String, String> entry : kvMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (AssertUtil.isAllNotEmpty(key, value)) {
                        p.set(key, value);
                        p.expire(key, seconds);

                        if (++index % 1000 == 0) {
                            p.sync();
                        }
                    }

                }
                p.sync();
                result = true;
            }
        } catch (Exception e) {
            isBroken = true;
            System.out.println("redis error " + e);
        } finally {
            returnResource(jedis, isBroken);
        }
        return result;
    }


    /**
     * 获取list
     *
     * @param key
     * @param map
     * @return
     */
    public boolean hmSet(final String key, Map<String, String> map) {
        boolean result = false;
        boolean isBroken = false;
        Jedis jedis = getJedis();
        try {
            if (map != null) {
                jedis.hmset(key, map);
                result = true;
            }

        } catch (Exception e) {
            isBroken = true;
            System.out.println("redis error " + e);
        } finally {
            returnResource(jedis, isBroken);
        }
        return result;
    }


    /**
     * 获取list
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    public boolean hSetex(final String key, String field, String value) {
        boolean result = false;
        boolean isBroken = false;
        Jedis jedis = getJedis();
        try {
            jedis.hset(key, field, value);
            result = true;

        } catch (Exception e) {
            isBroken = true;
            System.out.println("redis error " + e);
        } finally {
            returnResource(jedis, isBroken);
        }
        return result;
    }


    /**
     * 获取list
     *
     * @param key
     * @return
     */
    public Map<String, String> hgetAll(final String key) {
        Map<String, String> ret = new HashMap<>();
        boolean isBroken = false;
        Jedis jedis = getJedis();
        try {
            ret = jedis.hgetAll(key);

        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jedis, isBroken);
        }
        return ret;
    }


    /**
     * 获取list
     *
     * @param key
     * @param seconds
     * @return
     */
    public boolean expire(final String key, int seconds) {
        boolean result = false;
        boolean isBroken = false;
        Jedis jedis = getJedis();
        try {
            jedis.expire(key, seconds);
            result = true;

        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jedis, isBroken);
        }
        return result;
    }


    /**
     * 获取list
     *
     * @param keyMapList
     * @return
     */
    public void hmsetexPipelined(final Map<String, Map<String, String>> keyMapList, int seconds) {
        Jedis jedis = getJedis();
        boolean isBroken = false;
        try {
            Pipeline p = jedis.pipelined();
            if (keyMapList != null) {
                int index = 0;
                for (Map.Entry<String, Map<String, String>> entry : keyMapList.entrySet()) {
                    String key = entry.getKey();
                    Map<String, String> value = entry.getValue();
                    if (value != null) {
                        p.hmset(key, value);
                        p.expire(key, seconds);
                        if (++index % 1000 == 0) {
                            p.sync();
                        }
                    }
                }
                p.sync();
            }


        } catch (Exception e) {
            isBroken = true;
            System.out.println("redis error " + e);
        } finally {
            returnResource(jedis, isBroken);
        }
    }


    /**
     * 获取list
     *
     * @param keyMapList
     * @return
     */
    public void hsetexPipelined(final Map<String, String> keyMapList, int seconds) {
        Jedis jedis = getJedis();
        boolean isBroken = false;
        try {
            Pipeline p = jedis.pipelined();
            if (keyMapList != null) {
                for (Map.Entry<String, String> entry : keyMapList.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (value != null) {
                        p.setex(key, seconds, value);
                    }
                }
            }
            p.sync();
        } catch (Exception e) {
            isBroken = true;
            System.out.println("redis error "+e);
        } finally {
            returnResource(jedis, isBroken);

        }
    }

    /**
     * @param jedis
     */
    public void returnResource(Jedis jedis, boolean isBroken) {
        try {

            if (null == jedis) {
                return;
            }
            jedis.close();

        } catch (Exception e) {
            System.out.println("redis error " + e);
        }

    }
}



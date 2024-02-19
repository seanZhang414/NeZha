package cn.com.duiba.tuia.engine.activity.web.config;

import cn.com.duibaboot.ext.autoconfigure.redis.Hessian2SerializationRedisSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class Redis03Config {

    @Value("${tuia.open.web.redis03.host}")
    private String hostName;
    @Value("${tuia.open.web..redis03.password}")
    private String password;
    @Value("${tuia.open.web.redis03.port}")
    private int port;

    @Bean(name = "jedisConnectionFactory03", destroyMethod = "destroy")
    public JedisConnectionFactory jedisConnectionFactory03() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName(hostName);
        jedisConnectionFactory.setPassword(password);
        jedisConnectionFactory.setPort(port);
        jedisConnectionFactory.setPoolConfig(jedisPoolConfig03());
        return jedisConnectionFactory;
    }

    @Bean(name = "jedisPoolConfig03")
    public JedisPoolConfig jedisPoolConfig03() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(100);// 最大连接数
        jedisPoolConfig.setMaxIdle(50);// 最大空闲连接数
        jedisPoolConfig.setMinIdle(30);// 最小空闲连接数
        jedisPoolConfig.setMaxWaitMillis(500);// 获取连接时最大等待毫秒数
        return jedisPoolConfig;
    }

    @Bean(name = "redisTemplate03")
    public StringRedisTemplate redisTemplate03() {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(jedisConnectionFactory03());
        stringRedisTemplate.setDefaultSerializer(new Hessian2SerializationRedisSerializer());
        return stringRedisTemplate;
    }

    @Bean(name = "redisTemplate04")
    public RedisTemplate<Object, Object> redisTemplate04() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory03());
        Hessian2SerializationRedisSerializer serializer = new Hessian2SerializationRedisSerializer();
        serializer.setCompressionThreshold(0);
        redisTemplate.setDefaultSerializer(serializer);
        return redisTemplate;
    }
}

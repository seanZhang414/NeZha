package cn.com.duiba.tuia.engine.activity.web.config;

import cn.com.duiba.dayu.api.client.DayuClient;
import cn.com.duiba.tuia.constant.CacheKeyConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolsConfig {

    @Value("${tuia.engine.activity.cache.key.prefix}")
    private String prefix;

    @Bean
    public CacheKeyConstant cachedKeyUtils() {
        CacheKeyConstant.setPrefix(prefix);
        return null;
    }

    @Bean
    public DayuClient dayuClient() {
        DayuClient dayuClient = new DayuClient();
        // 请求数达到指定次数后刷新配置
        // dayuClient.setCount(20L); //NOSONAR
        // 本地配置缓存的时间,单位为ms
        dayuClient.setExpireTime(60000L);
        return dayuClient;
    }
}

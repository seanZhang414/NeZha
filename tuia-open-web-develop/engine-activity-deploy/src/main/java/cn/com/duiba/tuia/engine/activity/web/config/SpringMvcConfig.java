package cn.com.duiba.tuia.engine.activity.web.config;

import cn.com.duiba.tuia.engine.activity.web.filter.AccessHostFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;

@Configuration
public class SpringMvcConfig extends WebMvcConfigurerAdapter {//WebMvcConfigurationSupport


    @Resource
    private AccessHostFilter accessHostFilter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessHostFilter).addPathPatterns("/**");

    }


}

package cn.com.duiba.nezha.engine.deploy.interceptor;

import cn.com.duiba.wolf.perf.timeprofile.DBTimeProfile;
import cn.com.duibaboot.ext.autoconfigure.core.Constants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dianping.cat.Cat;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: RecommendInterceptor.java , v 0.1 2017/11/2 上午11:00 ZhouFeng Exp $
 */
@java.lang.SuppressWarnings({"squid:S3776","squid:S1186"})
public class RecommendInterceptor implements HandlerInterceptor, ApplicationContextAware {

    private static final Logger            RECOMMEND_LOGGER     = LoggerFactory.getLogger(RecommendInterceptor.class);

    private static final Logger            SYS_LOGGER           = LoggerFactory.getLogger(HandlerInterceptor.class);

    private static final String            timePattern          = "yyyyMMddHHmmss";

    private static final DateTimeFormatter dateTimeFormatter    = DateTimeFormatter.ofPattern(timePattern);

    private static final Set<String>       ALLOW_LOG_ENVIROMENT = Sets.newHashSet("dev", "prod");

    private boolean                        limit                = false;

    private static final RateLimiter       RATE_LIMITER         = RateLimiter.create(0.02);

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                             Object o) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
                           ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {
        try {
            DBTimeProfile.enter("afterCompletion");

            String servletPath = httpServletRequest.getServletPath();

            if (limit && RATE_LIMITER.tryAcquire() && StringUtils.isNotBlank(servletPath)) {


                Object attribute = httpServletRequest.getAttribute(Constants.HTTP_REQUEST_ATTRIBUTE_RPC_ARGS);
                if (!(attribute instanceof List)) {
                    return;
                }

                List<Object> params = (List<Object>) attribute;

                // 暂只支持两个参数的服务
                if (params.size() == 2) {

                    Splitter splitter = Splitter.on("/");
                    Iterable<String> split = splitter.omitEmptyStrings().trimResults().split(servletPath);

                    List<String> strings = new ArrayList<>();
                    for (String s : split) {
                        strings.add(s);
                    }

                    // 请求路径分成两部分,例如： /serviceName/methodName
                    if (strings.size() == 2) {
                        String strategyId = (String) params.get(1);

                        String service = strings.get(0);
                        String method = strings.get(1);
                        JSONObject json = (JSONObject) JSON.toJSON(params.get(0));

                        json.put("strategyId", strategyId);
                        json.put("method", method);
                        json.put("service", service);
                        json.put("time", LocalDateTime.now().format(dateTimeFormatter));

                        String record = json.toString();

                        // 记录Cat曲线图
                        Cat.logMetricForCount("RecommendInterceptor");

                        RECOMMEND_LOGGER.info(record);
                    }

                }

            }

        } catch (Exception ee) {
            SYS_LOGGER.info("recommend interceptor error ", ee);
        } finally {
            DBTimeProfile.release();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        String active = applicationContext.getEnvironment().getProperty("spring.profiles.active");
        // 判断当前环境时是否允许打印日志
        if (ALLOW_LOG_ENVIROMENT.contains(active)) {
            limit = true;
        }
    }
}

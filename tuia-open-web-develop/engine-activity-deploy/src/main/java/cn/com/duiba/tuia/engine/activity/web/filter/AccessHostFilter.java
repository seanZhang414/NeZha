package cn.com.duiba.tuia.engine.activity.web.filter;

import cn.com.duibaboot.ext.autoconfigure.accesslog.AccessLogFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


/**
 * 统一访问日志host设置filter
 *
 * @author songjiaxing
 *
 */
@Component
public class AccessHostFilter implements HandlerInterceptor , InitializingBean {

	private static Logger log = LoggerFactory.getLogger(AccessHostFilter.class);

	/** 跳转链接. */
	protected static List<String> notCheckList = new ArrayList<>();

	@Override
	public void afterPropertiesSet() throws Exception {
		notCheckList.add("engine.tuia.cn");
	}

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse response,
							 Object handler) throws Exception {
		try {
			String host = req.getHeader("host");
			// 域名切换后，修改统一访问日志中的host为原host
			if (!notCheckList.contains(host)) {
				AccessLogFilter.putOverWritePair(AccessLogFilter.OW_HOST,
						"engine.tuia.cn");
			}
			AccessLogFilter.putExPair("use_host",host);
			AccessLogFilter.putExPair("agreement",req.getHeader("X-Forwarded-Proto")==null?"http":req.getHeader("X-Forwarded-Proto"));
		} catch (Exception e) {
			log.error("addCookieIfNeed error");
		}
		return true;
	}

	@Override public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
									 ModelAndView modelAndView) {//NOSONAR

	}

	@Override public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
										  Exception ex) {//NOSONAR

	}

}

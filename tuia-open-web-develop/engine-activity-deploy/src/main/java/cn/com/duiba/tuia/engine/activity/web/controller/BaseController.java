package cn.com.duiba.tuia.engine.activity.web.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.duiba.tuia.utils.UAData;
import cn.com.duiba.tuia.utils.UAUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.duiba.tuia.utils.UrlUtil;
import cn.com.duiba.wolf.perf.timeprofile.RequestTool;

/**
 * BaseController
 */
public class BaseController {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected String getIP(HttpServletRequest request) {
        return RequestTool.getIpAddr(request);
    }

    protected String getOS(HttpServletRequest request) {
        return RequestTool.getOS(request);
    }

    protected String getUA(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    protected String getModel(HttpServletRequest request) {
        String ua = getUA(request);
        if (StringUtils.isNotBlank(ua)) {
            UAData data = UAUtils.parseUA(ua);
            return data.getModel();
        }
        return null;
    }

    protected String getCookie(String name, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    protected Map<String, String> getCookies(String namePrefix, HttpServletRequest request) {
        Map<String, String> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                if (cookie.getName() != null && cookie.getName().startsWith(namePrefix)) {
                    cookieMap.put(cookie.getName(), cookie.getValue());
                }
            }
        }
        return cookieMap;
    }

    protected void setCookie(String name, String value, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value); // NOSONAR
        cookie.setPath("/");// NOSONAR
        response.addCookie(cookie);// NOSONAR
    }

    protected static String getQuerySimple(String name, String url) {
        String query = url.substring(url.indexOf('?') + 1);
        String[] params = query.split("&");
        for (String param : params) {
            String[] kv = param.split("=");
            if (name.equals(kv[0])) {
               return kv[1]; 
            }
        }
        return null;
    }

    protected String sendRedirect(String url, boolean isHttp) {
        String httpsUrl;
        if (isHttp) {
            httpsUrl = UrlUtil.httpsToHttp(url);
        } else {
            httpsUrl = UrlUtil.http2Https(url);
        }
        return httpsUrl;
    }
    
    protected void sendRedirect(String url, HttpServletResponse response) {
    	try {
    		String httpsUrl = UrlUtil.http2Https(url);
    		response.sendRedirect(httpsUrl);
    	} catch (IOException e) {
    		logger.error("BaseController sendRedirect error", e);
    	}
    }

    /**
     * 不修改url，直接重定向
     * @param url
     * @param response
     */
    protected void defSendRedirect(String url, HttpServletResponse response) {
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            logger.error("BaseController sendRedirect error", e);
        }
    }
}

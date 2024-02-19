/**
 * 文件名： AjaxUtils.java 此类描述的是： 作者: leiliang 创建时间: 2016年3月23日 上午10:46:40
 */
package cn.com.duiba.tuia.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletResponse;

import cn.com.duiba.tuia.exception.TuiaRuntimeException;
import org.apache.commons.lang.StringUtils;

import cn.com.duiba.tuia.constant.CommonConstants;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <一句话功能描述> <功能详细描述>
 * 
 * @author: leiliang
 * @version:
 */
public class AjaxUtils {

    // -- header 常量定义 --//
    private static final String      HEADER_ENCODING  = "encoding";
    private static final String      HEADER_NOCACHE   = "no-cache";
    private static final String      DEFAULT_ENCODING = "UTF-8";
    private static final boolean     DEFAULT_NOCACHE  = true;

    private static final ObjectMapper mapper           = JsonUtils.mapper;

    static {
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    private AjaxUtils() {

    }

    /**
     * 直接输出内容的简便函数. render("text/plain", "hello", "encoding:GBK"); render("text/plain", "hello", "no-cache:false");
     * render("text/plain", "hello", "encoding:GBK", "no-cache:false");
     * 
     * @param headers 可变的header数组，目前接受的值为"encoding:"或"no-cache:",默认值分别为UTF-8和true.
     * @param response
     * @param contentType
     * @param content
     */
    private static void render(HttpServletResponse response, final String contentType, final String content,
                               final String... headers) {
        initResponseHeader(response, contentType, headers);
        PrintWriter write = null;
        try {
            write = response.getWriter();
            write.write(content);
            write.flush();
        } catch (IOException e) {
            throw new TuiaRuntimeException(e);
        } finally {
            if (write != null) {
                write.close();
            }
        }
    }


    /**
     * 直接输出JSON,使用Jackson转换Java对象.
     * 
     * @param data 可以是List<POJO>, POJO[], POJO, 也可以Map名值对.
     * @param response
     * @param headers
     * @see #render(HttpServletResponse, String, String, String...)
     */
    public static void renderJson(HttpServletResponse response, final Object data, final String... headers) {
        initResponseHeader(response, CommonConstants.CONTENT_TYPE.JSON_TYPE, headers);
        try {
            mapper.writeValue(response.getWriter(), data);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 直接输出支持跨域Mashup的JSONP.
     * 
     * @param callbackName callback函数名.
     * @param response
     * @param headers
     * @param object Java对象,可以是List<POJO>, POJO[], POJO ,也可以Map名值对, 将被转化为json字符串.
     */
    public static void renderJsonp(HttpServletResponse response, final String callbackName, final Object object,
                                   final String... headers) {
        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        String result = new StringBuilder().append(callbackName).append("(").append(jsonString).append(");").toString();

        render(response, CommonConstants.CONTENT_TYPE.JS_TYPE, result, headers);
    }

    /**
     * 分析并设置contentType与headers.
     * 
     * @param response
     * @param contentType
     * @param headers
     */
    private static void initResponseHeader(HttpServletResponse response, final String contentType,
                                           final String... headers) {
        // 分析headers参数
        String encoding = DEFAULT_ENCODING;
        boolean noCache = DEFAULT_NOCACHE;
        for (String header : headers) {
            String headerName = StringUtils.substringBefore(header, ":");
            String headerValue = StringUtils.substringAfter(header, ":");

            if (StringUtils.equalsIgnoreCase(headerName, HEADER_ENCODING)) {
                encoding = headerValue;
            } else if (StringUtils.equalsIgnoreCase(headerName, HEADER_NOCACHE)) {
                noCache = Boolean.parseBoolean(headerValue);
            } else {
                throw new IllegalArgumentException(headerName + "不是一个合法的header类型");
            }
        }

        // 设置headers参数
        String fullContentType = contentType + ";charset=" + encoding;
        response.setContentType(fullContentType);
        if (noCache) {
            setDisableCacheHeader(response);
        }
    }

    /**
     * 设置禁止客户端缓存的Header.
     * 
     * @param response
     */
    public static void setDisableCacheHeader(HttpServletResponse response) {
        // Http 1.0 header
        response.setDateHeader("Expires", 1L);
        response.addHeader("Pragma", "no-cache");
        // Http 1.1 header
        response.setHeader("Cache-Control", "no-cache, no-store, max-age=0");
    }
}

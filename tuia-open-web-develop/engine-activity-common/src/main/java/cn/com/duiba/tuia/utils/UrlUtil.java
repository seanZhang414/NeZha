package cn.com.duiba.tuia.utils;

import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtil {

    private static final String HTTP_PREFIX = "http://";

    private static final String HTTPS_PREFIX = "https://";

    private UrlUtil(){
    }

    public static String http2Https(String oldUrl){

        if (StringUtils.startsWith(oldUrl, "//")) {
            return "https:" + oldUrl;
        }

        if (!StringUtils.startsWith(oldUrl, HTTP_PREFIX) && !StringUtils.startsWith(oldUrl, HTTPS_PREFIX)) {
            return HTTPS_PREFIX + oldUrl;
        }
        String protocol ;
        try {
            URL url = new URL(oldUrl);
            protocol = url.getProtocol();
        } catch (MalformedURLException e) {
            return oldUrl;
        }

        if ("https".equals(protocol)) {
            return oldUrl;
        }

        if ("http".equals(protocol)) {
            return StringUtils.replace(oldUrl, HTTP_PREFIX, HTTPS_PREFIX);
        }
        return oldUrl;
    }

    public static String httpsToHttp(String oldUrl){

        if (StringUtils.startsWith(oldUrl, "//")) {
            return "http:" + oldUrl;
        }

        if (!StringUtils.startsWith(oldUrl, HTTP_PREFIX) && !StringUtils.startsWith(oldUrl, HTTPS_PREFIX)) {
            return HTTP_PREFIX + oldUrl;
        }
        String protocol ;
        try {
            URL url = new URL(oldUrl);
            protocol = url.getProtocol();
        } catch (MalformedURLException e) {
            return oldUrl;
        }

        if ("http".equals(protocol)) {
            return oldUrl;
        }

        if ("https".equals(protocol)) {
            return StringUtils.replace(oldUrl,HTTPS_PREFIX ,HTTP_PREFIX);
        }
        return oldUrl;
    }
}

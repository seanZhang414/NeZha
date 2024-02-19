package cn.com.duiba.tuia.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GeoUtils
 */
public class GeoUtils {

    private static final Logger logger     = LoggerFactory.getLogger(GeoUtils.class);

    private static final String AMAP_KEY   = "7cd1e4c85ea7a90bfd927ba857024f33";

    private static final String IPIP_TOKEN = "5a56b378cef2ffc4c35e4bf0596aeba8868b6e2a";

    private GeoUtils(){
    }

    /**
     * get
     * 
     * @param url
     * @param head
     * @return String
     */
    public static String get(String url, Map<String, String> head) throws IOException {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        try {
            HttpGet httpGet = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(1000).setConnectionRequestTimeout(1000).setSocketTimeout(1000).build();
            httpGet.setConfig(requestConfig);
            if (head != null) {
                for (Map.Entry<String, String> entry : head.entrySet()) {
                    httpGet.setHeader(entry.getKey(), entry.getValue());
                }
            }
            HttpResponse httpResponse = closeableHttpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity);
            }
        } catch (Exception e) {
            logger.warn("url->[{}] request failed, because of [{}]", url, e);
        } finally {
            closeableHttpClient.close();
        }
        return null;
    }

    /**
     * getGeo
     * 
     * @param longitude
     * @param latitude
     * @return GeoData
     */
    public static GeoData getGeo(String longitude, String latitude) {
        if (StringUtils.isBlank(longitude) || StringUtils.isBlank(latitude)) {
            return null;
        }

        try {
            String url = "http://restapi.amap.com/v3/geocode/regeo?output=json&location=" + longitude + "," + latitude + "&key=" + AMAP_KEY + "&radius=1000";
            String resultJson = get(url, null);
            if (StringUtils.isBlank(resultJson)) {
                return null;
            }

            Map<?, ?> resultMap = JsonUtils.jsonToObject(Map.class, resultJson);
            Map<?, ?> regeoMap = (Map<?, ?>) resultMap.get("regeocode");
            if (regeoMap != null) {
                Map<?, ?> address = (Map<?, ?>) regeoMap.get("addressComponent");
                if (address != null) {
                    GeoData data = new GeoData();
                    data.setCountry(String.valueOf(address.get("country")));
                    data.setProvince(String.valueOf(address.get("province")));
                    data.setCity(String.valueOf(address.get("city")));
                    data.setDistrict(String.valueOf(address.get("district")));
                    data.setTownship(String.valueOf(address.get("township")));
                    data.setJson(resultJson);
                    return data;
                }
            }
        } catch (Exception e) {
            logger.warn("getGeo failed, longitude=" + longitude + ", latitude=" + latitude, e);
        }
        return null;
    }
    
    /**
     * getGeo
     * 
     * @param ipAddr
     * @return GeoData
     */
    public static GeoData getGeo(String ipAddr) {
        if (StringUtils.isBlank(ipAddr)) {
            return null;
        }

        try {
            String url = "http://ipapi.ipip.net/find?addr=" + ipAddr;
            Map<String, String> head = new HashMap<>();
            head.put("Token", IPIP_TOKEN);
            String resultJson = get(url, head);
            if (StringUtils.isBlank(resultJson)) {
                return null;
            }

            Map<?, ?> resultMap = JsonUtils.jsonToObject(Map.class, resultJson);
            String ret = (String) resultMap.get("ret");
            if ("ok".equals(ret)) {
                List<?> address = (List<?>) resultMap.get("data");
                if (address != null && address.size() >= 3) {
                    GeoData data = new GeoData();
                    data.setCountry(String.valueOf(address.get(0)));
                    data.setProvince(String.valueOf(address.get(1)));
                    data.setCity(String.valueOf(address.get(2)));
                    data.setJson(resultJson.replace("\n", ""));
                    return data;
                }
            }
        } catch (Exception e) {
            logger.warn("getGeo failed, ipAddr=" + ipAddr, e);
        }
        return null;
    }

    public static GeoData getGeoLocal(String ipAddr) {
        String[] address = IPIPUtils.findIp(ipAddr);
        if (address != null && address.length >= 3) {
            GeoData data = new GeoData();
            data.setCountry(address[0]);
            data.setProvince(address[1]);
            data.setCity(address[2]);
            data.setJson(Arrays.toString(address));
            return data;
        }
        return null;
    }
}

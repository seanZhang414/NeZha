package cn.com.duiba.tuia.engine.activity.tongdun;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


import cn.com.duiba.tuia.engine.activity.model.req.SpmActivityReq;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FraudApiInvoker
 */
public class FraudApiInvoker {
    private static final Logger log = LoggerFactory.getLogger(FraudApiInvoker.class);
    private static final String API_URL      = "https://api.tongdun.cn/riskService";// NOSONAR
    private static final String PARTNER_CODE = "duiba";                             // NOSONAR

    /**
     * invoke
     * 
     * @param params
     * @return FraudApiResponse
     */
    public FraudApiResponse invoke(Map<String, Object> params) {
        HttpURLConnection conn;
        try {
            URL url = new URL(API_URL);
            // 组织请求参数
            StringBuilder postBody = new StringBuilder();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                postBody.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue().toString(),
                                                                                     "utf-8")).append("&");
            }

            if (!params.isEmpty()) {
                postBody.deleteCharAt(postBody.length() - 1);
            }

            conn = (HttpURLConnection) url.openConnection();
            // 设置长链接
            conn.setRequestProperty("Connection", "Keep-Alive");
            // 设置连接超时
            conn.setConnectTimeout(100);
            // 设置读取超时
            conn.setReadTimeout(100);
            // 提交参数
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.getOutputStream().write(postBody.toString().getBytes());
            conn.getOutputStream().flush();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                log.warn("[FraudApiInvoker] invoke failed, response status:{}" ,responseCode);
                return null;
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line).append("\n");
            }
            return JSON.parseObject(result.toString().trim(), FraudApiResponse.class);
        } catch (Exception e) {
            log.warn("[FraudApiInvoker] invoke throw exception, details: " + e);
        }
        return null;
    }

    private static FraudApiResponse invoke(String secretKey, String eventId, String tokenId, String blackBox,
                                           SpmActivityReq req) {
        Map<String, Object> params = new HashMap<>();
        params.put("partner_code", PARTNER_CODE);// 此处值填写您的合作方标识
        params.put("secret_key", secretKey);// 此处填写对应app密钥
        params.put("event_id", eventId);// 此处填写策略集上的事件标识
        if (tokenId != null) {
            params.put("token_id", tokenId);// 此处填写设备指纹服务的会话标识，和部署设备脚本的token一致
        }
        if (blackBox != null) {
            params.put("black_box", blackBox);// 此处填写设备指纹服务的会话标识，和部署设备脚本的token一致
        }
        params.put("ext_ad_consumer_id", req.getDevice_id());// 以下填写其他要传的参数，比如系统字段，扩展字段
        params.put("ip_address", req.getIp());
        params.put("ext_app_id", req.getApp_id());
        params.put("ext_adslot_id", req.getAdslot_id());
        params.put("ext_activity_id", req.getActivity_id());
        params.put("ext_adaction", req.getType());
        params.put("resp_detail_type", "device");
        return new FraudApiInvoker().invoke(params);
    }

    /**
     * invoke4web
     * 
     * @param req
     * @return FraudApiResponse
     */
    public static FraudApiResponse invoke4web(SpmActivityReq req) {
        String secretKey = "f516f6e2d7514f66adcd107634000ff5";
        String eventId = "marketing_professional_h5";
        req.setEvent_id(eventId);
        return invoke(secretKey, eventId, req.getToken_id(), null, req);
    }

    /**
     * invoke4ios
     *
     * @param req
     * @return FraudApiResponse
     */
    public static FraudApiResponse invoke4ios(SpmActivityReq req) {
        String secretKey = "b8a8f4e3877b452699dfd0bcbcb07c7a";
        String eventId = "marketing_professional_ios";
        req.setEvent_id(eventId);
        return invoke(secretKey, eventId, null, req.getToken_id(), req);
    }

    /**
     * invoke4android
     *
     * @param req
     * @return FraudApiResponse
     */
    public static FraudApiResponse invoke4android(SpmActivityReq req) {
        String secretKey = "499b39efe0db46f6b9525ef340f70f28";
        String eventId = "marketing_professional_android";
        req.setEvent_id(eventId);
        return invoke(secretKey, eventId, null, req.getToken_id(), req);
    }
}

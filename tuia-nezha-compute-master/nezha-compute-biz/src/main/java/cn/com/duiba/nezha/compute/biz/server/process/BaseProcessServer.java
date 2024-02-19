package cn.com.duiba.nezha.compute.biz.server.process;


import cn.com.duiba.nezha.compute.biz.log.InnerLog;
import com.alibaba.fastjson.JSONObject;

public class BaseProcessServer {


    /**
     * 内部日志解析
     *
     * @param logStr
     */
    public static InnerLog innerLogParse(String logStr) {
        InnerLog innerLog = null;
        if (logStr != null) {
            innerLog = JSONObject.parseObject(logStr, InnerLog.class);
        }
        return innerLog;
    }

    /**
     * 内部日志解析
     *
     * @param logStr
     */
    public static String getJsonStr(String logStr) {
        return getJsonStr2(logStr);
    }

    /**
     * 内部日志解析
     *
     * @param logStr
     */
    public static String getTimeStr(String logStr) {
        String timeStr = null;
        InnerLog innerLog = innerLogParse(logStr);
        if (innerLog != null) {
            timeStr = innerLog.getTime();
        }
        return timeStr;
    }

    /**
     * 内部日志解析
     *
     * @param logStr
     */
    public static String getJsonStr2(String logStr) {
        String jsonStr = null;
        InnerLog innerLog = innerLogParse(logStr);
        if (innerLog != null) {
            jsonStr = innerLog.getJson();
        }
        return jsonStr;
    }


}

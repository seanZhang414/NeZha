package cn.com.duiba.nezha.compute.biz.support;

import cn.com.duiba.nezha.compute.biz.dto.InnerLog;
import cn.com.duiba.nezha.compute.biz.dto.LaunchLog;
import cn.com.duiba.nezha.compute.core.enums.DateStyle;
import cn.com.duiba.nezha.compute.core.util.DateUtil;
import com.alibaba.fastjson.JSONObject;

public class LogParse {
    /**
     * 推啊广告发券处理
     *
     * @param logStr
     * @throws Exception
     */
    public static LaunchLog logParse(String logStr) {
        LaunchLog log = new LaunchLog();
        if (logStr != null) {
            try {
//                InnerLog innerLog = getInnerLog(logStr);
//                System.out.println("log"+logStr);
                if (logStr != null) {

                    log = JSONObject.parseObject(logStr, LaunchLog.class);
                    log.setGmtDate(DateUtil.string2String(log.getLogTime(), DateStyle.YYYY_MM_DD_HH_MM_SS, DateStyle.YYYY_MM_DD));
                    log.setCurrentTime(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS));

                    Integer dms = DateUtil.getIntervalSeconds(log.getLogTime(), log.getCurrentTime(), DateStyle.YYYY_MM_DD_HH_MM_SS);
                    log.setDelaySeconds(dms);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return log;
//        return null;
    }


    public static InnerLog getInnerLog(String logStr) {
        InnerLog innerLog = null;
        if (logStr != null) {
            innerLog = JSONObject.parseObject(logStr, InnerLog.class);

        }
        return innerLog;
    }
}

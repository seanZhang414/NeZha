package cn.com.duiba.tuia.engine.activity.log;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.duiba.tuia.engine.activity.model.req.SpmTerminalReq;
import cn.com.duiba.tuia.utils.CatUtil;
import cn.com.duiba.tuia.utils.JsonUtils;

/**
 * StatActTerminalLog
 */
public final class StatActTerminalLog {

    private static Logger log = LoggerFactory.getLogger(StatActTerminalLog.class);

    private StatActTerminalLog() {
    }

    /**
     * log
     * 
     * @param req
     */
    public static void log(SpmTerminalReq req) {
        req.setServerTime((new DateTime()).toString("yyyy-MM-dd HH:mm:ss"));
        String logInfo = format(req);
        log.info(logInfo);
        CatUtil.log("statActTerminalLog");
    }

    private static String format(SpmTerminalReq req) {
        return JsonUtils.objectToString(req);
    }
}

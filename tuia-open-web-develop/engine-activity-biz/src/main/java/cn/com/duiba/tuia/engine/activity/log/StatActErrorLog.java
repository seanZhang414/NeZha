package cn.com.duiba.tuia.engine.activity.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.duiba.tuia.engine.activity.model.req.SpmErrorReq;
import cn.com.duiba.tuia.utils.CatUtil;
import cn.com.duiba.tuia.utils.JsonUtils;

/**
 * StatActErrorLog
 */
public final class StatActErrorLog {

    private static Logger log = LoggerFactory.getLogger(StatActErrorLog.class);

    private StatActErrorLog() {
    }

    /**
     * log
     * 
     * @param req
     */
    public static void log(SpmErrorReq req) {
        String logInfo = format(req);
        log.info(logInfo);
        CatUtil.log("statActErrorLog");
    }

    private static String format(SpmErrorReq req) {
        return JsonUtils.objectToString(req);
    }
}

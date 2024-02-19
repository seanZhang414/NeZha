package cn.com.duiba.tuia.engine.activity.log;

import cn.com.duiba.tuia.utils.CatUtil;
/**
 * StatActRequestLog
 */
public final class StatActRequestLog {

    private StatActRequestLog() {
    }

    /**
     * log
     */
    public static void log() {
        CatUtil.log("statActRequestLog");
    }
}

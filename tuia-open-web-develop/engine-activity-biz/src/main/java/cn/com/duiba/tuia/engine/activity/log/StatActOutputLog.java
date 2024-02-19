package cn.com.duiba.tuia.engine.activity.log;

import cn.com.duiba.tuia.engine.activity.api.ApiCode;
import cn.com.duiba.tuia.utils.CatUtil;

import cn.com.duiba.tuia.engine.activity.model.rsp.GetActivityRsp;

/**
 * StatActOutputLog
 */
public final class StatActOutputLog {

    private StatActOutputLog() {

    }

    /**
     * log
     * 
     * @param req
     */
    public static void log(GetActivityRsp req) {
        if (ApiCode.SUCCESS.equals(req.getError_code())) {
            CatUtil.log("statActOutputLog");
        }
    }
}

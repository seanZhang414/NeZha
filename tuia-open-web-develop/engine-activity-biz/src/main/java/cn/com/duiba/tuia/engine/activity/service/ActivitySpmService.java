package cn.com.duiba.tuia.engine.activity.service;

import cn.com.duiba.tuia.engine.activity.model.req.SpmActivityManualReq;
import cn.com.duiba.tuia.engine.activity.model.req.SpmActivityReq;
import cn.com.duiba.tuia.engine.activity.model.req.SpmErrorReq;
import cn.com.duiba.tuia.engine.activity.model.req.SpmTerminalReq;

/**
 * ActivitySpmService
 */
public interface ActivitySpmService {

    /**
     * 增加活动曝光度/点击数
     * 
     * @param req
     */
    void spmActivity4native(SpmActivityReq req);

    /**
     * 增加活动曝光度/点击数 V2
     * 
     * @param req
     * @param sdata
     * @param nsdata
     */
    void spmActivity4native(SpmActivityReq req, String sdata, String nsdata);

    /**
     * 增加活动曝光度/点击数
     * 
     * @param req
     */
    void spmActivity4web(SpmActivityReq req);

    /**
     * 增加活动曝光度
     * 
     * @param req
     */
    void spmActivity4manual(SpmActivityManualReq req);

    /**
     * 上报终端信息
     * 
     * @param req
     */
    void spmTerminal4native(SpmTerminalReq req);

    /**
     * 上报错误信息
     * 
     * @param req
     */
    void spmError4native(SpmErrorReq req);
}

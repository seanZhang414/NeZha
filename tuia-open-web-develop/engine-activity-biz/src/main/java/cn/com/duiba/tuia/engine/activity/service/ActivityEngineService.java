package cn.com.duiba.tuia.engine.activity.service;

import cn.com.duiba.tuia.engine.activity.model.req.GetActivityReq;
import cn.com.duiba.tuia.engine.activity.model.rsp.GetActivityRsp;
import cn.com.duiba.tuia.ssp.center.api.dto.ActivityManualPlanDto;


/**
 * 兑吧活动推荐引擎服务接口。
 */
public interface ActivityEngineService {

    /**
     * 获取推荐活动。
     * 
     * @param req
     * @return
     */
    GetActivityRsp getActivity(GetActivityReq req);

    /**
     * 获取推荐活动。
     *
     * @param req
     * @return
     */
    GetActivityRsp getActivity4native(GetActivityReq req) throws Exception;



    /**检查是否需要域名屏蔽*/
    GetActivityRsp domainShield(GetActivityReq req, String referer);

    /**
     * 获取手动投放活动
     * @param appKey
     * @param adslotId
     * @param operatorActivityId
     * @return
     */
    ActivityManualPlanDto getActivityManualPlan(String appKey, Long adslotId, Long operatorActivityId, String deviceId, String rid);


}

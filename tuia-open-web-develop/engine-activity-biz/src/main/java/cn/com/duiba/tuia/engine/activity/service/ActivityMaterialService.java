package cn.com.duiba.tuia.engine.activity.service;

import cn.com.duiba.tuia.activity.center.api.dto.GuidePageDto;
import cn.com.duiba.tuia.activity.center.api.dto.TuiaActivityDto;
import cn.com.duiba.tuia.engine.activity.model.req.GetActivityReq;
import cn.com.duiba.tuia.engine.activity.model.rsp.GetActivityRsp;
import cn.com.duiba.tuia.ssp.center.api.dto.SlotCacheDto;

/**
 * ActivityMaterialService
 */
public interface ActivityMaterialService {

    /**
     * @param advertTitle
     * @param activitySource
     * @param activityTuia
     * @param adslot
     * @param activityOutputWay
     * @return
     */
    GetActivityRsp toActivityResponse(GetActivityReq req, String advertTitle,Integer activitySource, TuiaActivityDto activityTuia, SlotCacheDto adslot, Integer activityOutputWay,String desc);

    /**
     * @param advertTitle
     * @param guidePageDto
     * @param adslot
     * @param activityOutputWay
     * @return
     */
    GetActivityRsp toActivityResponse(GetActivityReq req, String advertTitle, Integer activitySource, GuidePageDto guidePageDto, SlotCacheDto adslot, Integer activityOutputWay,String desc);
}

package cn.com.duiba.tuia.engine.activity.service;

import cn.com.duiba.tuia.ssp.center.api.dto.RspActivityDto;
import cn.com.duiba.tuia.ssp.center.api.dto.SlotCacheDto;

import java.util.Set;

/**
 * 活动推荐策略引擎
 * Created by shenjunlin on 2017/8/14.
 */
public interface StrategyEngine {

    RspActivityDto getActivity(SlotCacheDto slot, String deviceId, Set<String> visitHistory);
}

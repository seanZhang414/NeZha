package cn.com.duiba.tuia.engine.activity.service.impl.strategy.engine;

import cn.com.duiba.tuia.engine.activity.handle.Redis3Handler;
import cn.com.duiba.tuia.engine.activity.service.LocalCacheService;
import cn.com.duiba.tuia.engine.activity.service.StrategyEngine;
import cn.com.duiba.tuia.ssp.center.api.dto.RspActivityDto;
import cn.com.duiba.tuia.ssp.center.api.dto.SlotCacheDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * Created by shenjunlin on 2017/8/14.
 */
public abstract class AbstractStrategyEngine implements StrategyEngine {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected LocalCacheService localCacheService;

    @Autowired
    protected Redis3Handler redis3Handler;

    /**
     * 引擎策略
     * @param deviceId 设备id
     * @param slot 广告位详情
     * @param list 经过过滤后的活动列表
     * @param visitedHistory 用户访问历史
     */
    abstract RspActivityDto executeStrategy(String deviceId, SlotCacheDto slot, List<RspActivityDto> list, Set<String> visitedHistory);


    @Override
    public RspActivityDto getActivity(SlotCacheDto slot, String deviceId, Set<String> visitHistory) {

        //获取活动引擎推荐的活动列表 + 新活动列表
        List<RspActivityDto> activityDtos = localCacheService.getEngineActivityList(slot.getId());
        activityDtos.addAll(localCacheService.getNewActivityList(slot.getId()));

        if (CollectionUtils.isEmpty(activityDtos)) {
            logger.warn("getActivity error, because of empty engineActivityList");
            return null;
        }

        return executeStrategy(deviceId, slot ,activityDtos,visitHistory);
    }
}

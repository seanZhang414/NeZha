package cn.com.duiba.tuia.engine.activity.service.impl.strategy.engine;

import cn.com.duiba.tuia.ssp.center.api.dto.ActivityRpmDto;
import cn.com.duiba.tuia.ssp.center.api.dto.RspActivityDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 新算法活动引擎（手投包含新活动）
 */
@Component
public class ActivitySelector4ManualStrategyEngine extends ActivitySelectorStrategyEngine {

    List<RspActivityDto> loadData(Long slotId) {
        //获取活动引擎推荐的活动列表 + 新活动列表
        List<RspActivityDto> activityDtoList = localCacheService.getEngineActivityList(slotId);
        activityDtoList.addAll(localCacheService.getNewActivityList(slotId));
        return activityDtoList;
    }

    @Override
    Map<String, ActivityRpmDto> loadRpmBySlotAndType(Long slotId, Integer statType) {
        return localCacheService.getRpmBySlotInWeek4manual(slotId, statType);
    }
}

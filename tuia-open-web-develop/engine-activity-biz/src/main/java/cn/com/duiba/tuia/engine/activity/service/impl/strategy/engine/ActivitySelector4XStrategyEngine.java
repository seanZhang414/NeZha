package cn.com.duiba.tuia.engine.activity.service.impl.strategy.engine;

import cn.com.duiba.tuia.ssp.center.api.dto.ActivityRpmDto;
import cn.com.duiba.tuia.ssp.center.api.dto.RspActivityDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 新算法活动引擎
 */
@Component
public class ActivitySelector4XStrategyEngine extends ActivitySelectorStrategyEngine {

    List<RspActivityDto> loadData(Long slotId) {
        //获取活动引擎推荐的活动列表
        return localCacheService.getEngineActivityList(slotId);
    }

    @Override
    Map<String, ActivityRpmDto> loadRpmBySlotAndType(Long slotId, Integer statType) {
        return localCacheService.getRpmBySlotInWeek(slotId, statType);
    }
}

package cn.com.duiba.tuia.engine.activity.service.impl.strategy.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import cn.com.duiba.tuia.constant.ActivityConstant;
import cn.com.duiba.tuia.constant.RedisKeyConstant;
import cn.com.duiba.tuia.engine.activity.remoteservice.UserService;
import cn.com.duiba.tuia.ssp.center.api.constant.SplitConstant;
import cn.com.duiba.tuia.ssp.center.api.dto.ActivitySpmDto;
import cn.com.duiba.tuia.ssp.center.api.dto.RspActivityDto;
import cn.com.duiba.tuia.ssp.center.api.dto.SlotCacheDto;

/**
 * 无素材活动引擎
 * Created by shenjunlin on 2017/8/14.
 */
@Component
public class NOMaterialStrategyEngine extends AbstractStrategyEngine {

    @Autowired
    private UserService userService;

    @Value("${tuia.engine.activity.defaultActivityId}")
    private Long defaultActivityId;

    @Value("${tuia.engine.activity.activityRedisPrefix}")
    private String activityPrefix;


    @Override
    public RspActivityDto getActivity(SlotCacheDto slot, String deviceId, Set<String> visitHistory) {

        if (isResetVisitedHistory(deviceId,slot.getId()) && !CollectionUtils.isEmpty(visitHistory) && visitHistory.contains(String.valueOf(defaultActivityId))) {
        	return getDefaultActivity();
        }
        return super.getActivity(slot, deviceId, visitHistory);
    }

    @Override
    RspActivityDto executeStrategy(String deviceId, SlotCacheDto slot, List<RspActivityDto> list, Set<String> visitedHistory) {

        List<RspActivityDto> activityList = filterActivity(list, visitedHistory);
        // 过滤用户已经参与过的活动 原因是：用户活动访问历史重置后，参与过的没重置
        List<RspActivityDto> afterFilterJoined = null;
        if (isResetVisitedHistory(deviceId, slot.getId())) {
            //获取用户活动参与历史
            Set<String> joinedActivitySet = getJoinedHistory(slot, deviceId);
            //过滤活动参与历史
            afterFilterJoined = this.filterActivity(activityList, joinedActivitySet);
        } else {
            afterFilterJoined = activityList;
        }
        return getFromFilterActivityList(afterFilterJoined, deviceId, visitedHistory, slot);
    }

    private RspActivityDto getFromFilterActivityList(List<RspActivityDto> list, String deviceId, Set<String> visitedHistory, SlotCacheDto slot) {
        if (CollectionUtils.isEmpty(list)) { //过滤后没有活动的
            return invokeStrategyWhenNoActivity(deviceId, visitedHistory, slot);
        } else {
            //从缓存中 获取活动页面曝光pv > 5000的活动列表list1
            List<ActivitySpmDto> activitySpmList = localCacheService.getExposeCountMoreThan5000();
            if (CollectionUtils.isEmpty(activitySpmList)) {
                return avgLaunch(list);
            }
            //引擎推荐活动集合
            Set<String> engineActivityIds = list.stream().map(o -> o.getId() + SplitConstant.SPLIT_HYPHEN + o.getSource()).collect(Collectors.toSet());
            //过滤后的活动列表list中活动曝光数大于5000并且每UV发券值大于1的活动列表
            List<ActivitySpmDto> filterActivityList = activitySpmList.stream().filter(o -> isContains(o, engineActivityIds)).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(filterActivityList)) {
                return avgLaunch(list);
            }
            //曝光数大于5000并且每UV发券值大于1的活动
            ActivitySpmDto activitySpmDto = filterActivityList.get(0);
            return localCacheService.getActivityDetail(activitySpmDto.getActivityId(), activitySpmDto.getSource());
        }
    }

    /**
     * 判断一个活动是否在 set集合中
     * @param activitySpmDto
     * @param activityIds
     * @return
     */
    private boolean isContains(ActivitySpmDto activitySpmDto, Set<String> activityIds){
        return activityIds.contains(activitySpmDto.getActivityId() + SplitConstant.SPLIT_HYPHEN + activitySpmDto.getSource());
    }

    private List<RspActivityDto> filterActivity(List<RspActivityDto> activityList, Set<String> filterActivityId) {
        if (CollectionUtils.isEmpty(filterActivityId)) {
            return activityList;
        }
        List<RspActivityDto> result = new ArrayList<>();
        for (RspActivityDto activity : activityList) {
            if (activity.getSource() == ActivityConstant.Source.TUI_A.getValue() && !filterActivityId.contains(activity.getId().toString())) {
                result.add(activity);
            }
        }
        return result;
    }

    /**
     * 经过过滤后没有用户访问
     *
     * @param deviceId
     * @param visitedHistory
     * @param slot
     * @return
     */
    private RspActivityDto invokeStrategyWhenNoActivity(String deviceId, Set<String> visitedHistory, SlotCacheDto slot) {
        if (CollectionUtils.isEmpty(visitedHistory)) {
            return getDefaultActivity();
        }
        if (isResetVisitedHistory(deviceId, slot.getId())) {
            return getDefaultActivity();
        }
        Set<String> newHistorySet =  this.resetVisitedHistory(deviceId, slot.getId(), visitedHistory);
        //重新进入无素材活动引擎
        return this.getActivity(slot, deviceId, newHistorySet);
    }

    /**
     * 投放默认的活动中心
     *
     * @return
     */
    private RspActivityDto getDefaultActivity() {
        RspActivityDto activityDto = localCacheService.getActivityDetail(defaultActivityId, 2);
        logger.info("NoMaterial defaultAct:{}", defaultActivityId);
        return activityDto;
    }
    
    public Long getDefaultActivityId() {
        return defaultActivityId;
    }

    /**
     * 用户访问历史是否重置过
     *
     * @param deviceId 设备Id
     * @param slotId   广告位ID
     * @return
     */
    private boolean isResetVisitedHistory(String deviceId, Long slotId) {
        String key = RedisKeyConstant.getUserKey(slotId, deviceId);
        Object object = redis3Handler.hGet(key, RedisKeyConstant.getActivityReset());
        return object != null;
    }

    /**
     * 重置用户活动访问历史
     *
     * @param deviceId
     * @param slotId
     */
    private Set<String> resetVisitedHistory(String deviceId, Long slotId, Set<String> visitedHistory) {
        String key = RedisKeyConstant.getUserKey(slotId, deviceId);
        //redis3Handler.hDel(key, RedisKeyConstant.getVisitHistory());  //不能全部删除 定制列表的活动不删除
        StringBuilder vh = new StringBuilder();
        List<RspActivityDto> slotActivityList = localCacheService.getSlotActivityList(slotId);//广告位定制活动列表
        Set<String> newHistorySet = Sets.newHashSet();
        for (RspActivityDto rspActivityDto : slotActivityList) {
            if (visitedHistory.contains(rspActivityDto.getId().toString())) {
                vh.append(rspActivityDto.getId() + SplitConstant.SPLIT_HYPHEN + rspActivityDto.getSource()).append(SplitConstant.SPLIT_COMMA);
                newHistorySet.add(rspActivityDto.getId().toString());
            }
        }


        String vhStr = StringUtils.substringBeforeLast(vh.toString(), SplitConstant.SPLIT_COMMA);
        redis3Handler.hSet(key, RedisKeyConstant.getVisitHistory() , vhStr);
        redis3Handler.hIncr(key, RedisKeyConstant.getActivityReset(), 1L);

        logger.info("NoMaterial resetVisit devId:{}, slotId:{}", deviceId, slotId);
        return newHistorySet;
    }

    /**
     * 活动列表均分投放
     *
     * @param list 经过过滤后的活动列表
     * @return
     */
    private RspActivityDto avgLaunch(List<RspActivityDto> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        int index = RandomUtils.nextInt(0, list.size());
        return list.get(index);
    }

    /**
     * 获取consumerId，这个值是活动那边保存的，根据这个值获取用户的活动参与记录
     *
     * @param slot
     * @param deviceId
     * @return
     */
    private Long getConsumerId(SlotCacheDto slot, String deviceId) {
        String consumerIdKey = "-01-" + slot.getAppId() + "-" + deviceId;
        Long consumerId = redis3Handler.get(consumerIdKey);
        if (consumerId != null) {
            return consumerId;
        }
        return userService.getConsumerId(slot.getAppId(), deviceId);  // 后续考虑加本地缓存
    }

    private Set<String> getJoinedHistory(SlotCacheDto slot, String deviceId) {
        Long consumerId = getConsumerId(slot, deviceId);
        if (consumerId == null) {
            return Collections.emptySet();
        }
        String key = activityPrefix + "-19-" + consumerId + "-" + new DateTime().getDayOfMonth();
        return redis3Handler.hGetKeys(key);
    }
}

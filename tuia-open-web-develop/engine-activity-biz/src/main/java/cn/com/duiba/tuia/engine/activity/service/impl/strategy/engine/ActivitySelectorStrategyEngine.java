package cn.com.duiba.tuia.engine.activity.service.impl.strategy.engine;

import cn.com.duiba.nezha.compute.common.model.activityselectchange.ActivityInfo;
import cn.com.duiba.nezha.compute.common.model.activityselectchange.ActivitySelector;
import cn.com.duiba.tuia.constant.RedisKeyConstant;
import cn.com.duiba.tuia.engine.activity.handle.Redis4Handler;
import cn.com.duiba.tuia.engine.activity.log.InnerLog;
import cn.com.duiba.tuia.engine.activity.service.LocalCacheService;
import cn.com.duiba.tuia.engine.activity.service.StrategyEngine;
import cn.com.duiba.tuia.ssp.center.api.constant.MaterialSpecificationConstant;
import cn.com.duiba.tuia.ssp.center.api.constant.SplitConstant;
import cn.com.duiba.tuia.ssp.center.api.dto.ActivityRpmDto;
import cn.com.duiba.tuia.ssp.center.api.dto.RspActivityDto;
import cn.com.duiba.tuia.ssp.center.api.dto.SlotCacheDto;
import cn.com.duiba.tuia.utils.CatUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 新算法活动引擎
 */
public abstract class ActivitySelectorStrategyEngine implements StrategyEngine {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected LocalCacheService localCacheService;

    @Autowired
    private Redis4Handler<String, String, ActivityInfo> redis4Handler;

    /**
     * 加载待推荐活动列表
     *
     * @param slotId 广告位id
     * @return 推荐活动列表
     */
    abstract List<RspActivityDto> loadData(Long slotId);

    /**
     * 加载spm数据
     *
     * @param slotId   广告位id
     * @param statType 维度类型
     * @return spm数据
     */
    abstract Map<String, ActivityRpmDto> loadRpmBySlotAndType(Long slotId, Integer statType);

    @Override
    public RspActivityDto getActivity(SlotCacheDto slot, String deviceId, Set<String> visitHistory) {
        Long slotId = slot.getId();
        //获取活动引擎推荐的活动列表
        List<RspActivityDto> activityDtoList = loadData(slot.getId());

        if (CollectionUtils.isEmpty(activityDtoList)) {
            logger.warn("getActivity error, because of empty engineActivityList");
            return null;
        }
        // 获取rpm数据
        Map<String, ActivityRpmDto> slotMap = loadRpmBySlotAndType(slotId, MaterialSpecificationConstant.STAT_TYPE_SLOT);
        Map<String, ActivityRpmDto> appMap = loadRpmBySlotAndType(slotId, MaterialSpecificationConstant.STAT_TYPE_APP);
        // 如果没有媒体维度的数据，则继续查询全局维度的数据
        Map<String, ActivityRpmDto> allMap = loadRpmBySlotAndType(slotId, MaterialSpecificationConstant.STAT_TYPE_OVERALL);
        return getByRpm2SortAndFlowSplit(slotId, activityDtoList, visitHistory, slotMap, appMap, allMap);
    }

    protected RspActivityDto getByRpm2SortAndFlowSplit(Long slotId, List<RspActivityDto> activityList, Set<String> visitHistory,// NOSONAR
                                                       Map<String, ActivityRpmDto> slotMap,
                                                       Map<String, ActivityRpmDto> appMap,
                                                       Map<String, ActivityRpmDto> allMap) {

        long timeout = 24 * 60 * 60 * 5000L;
        long now = new Date().getTime();

        List<RspActivityDto> filteredActList = filterVisitedActivity(activityList, visitHistory);
        List<ActivityRpmDto> rpmList = Lists.newArrayList();
        buildRpm2List(filteredActList, rpmList, slotMap, appMap, allMap);
        //如果过滤以后没有活动，则重置历史
        if (CollectionUtils.isEmpty(rpmList) && filteredActList.size() != activityList.size()) {
            buildRpm2List(activityList, rpmList, slotMap, appMap, allMap);
        }
        Set<String> actKeys = Sets.newHashSet();
        rpmList.forEach(rpm -> actKeys.add(rpm.getActivityId() + SplitConstant.SPLIT_HYPHEN + rpm.getSource()));
        //构建算法模型对象
        Map<String, ActivityInfo> actInfoMap = Maps.newHashMap();
        //redis 读写性能问题？
        Map<String, ActivityInfo> actInfoInRedis = redis4Handler.entries(RedisKeyConstant.getSlotActInfoNew(slotId));
        List<String> delActs = Lists.newArrayList();
        actInfoInRedis.forEach((k, v) -> {
            String actKey = v.getActivityId() + SplitConstant.SPLIT_HYPHEN + v.getSource();
            if (!actKeys.contains(actKey)) {
                v.setValid(false);
            } else {
                v.setValid(true);
            }
            if (now - v.getUpdateTime() >= timeout) {
                delActs.add(k);
            } else {
                actInfoMap.put(k, v);
            }
        });
        if (!CollectionUtils.isEmpty(delActs)) {
            redis4Handler.hDel(RedisKeyConstant.getSlotActInfoNew(slotId), delActs.toArray());
        }
        Map<Long, Date> activityCreateTimeList = activityList.stream().collect(Collectors.toMap(RspActivityDto::getId, RspActivityDto::getGmtCreate, (oldValue, newValue) -> newValue));
        rpmList.forEach(rpm -> {
            String key = rpm.getActivityId() + SplitConstant.SPLIT_HYPHEN + rpm.getSource();
            ActivityInfo actInfo = actInfoMap.get(key);
            if (actInfo == null) {
                actInfo = new ActivityInfo();
                actInfo.setActivityId(rpm.getActivityId());
                actInfo.setSource(rpm.getSource());
                actInfo.setSlotId(slotId);
                actInfo.setActivityType(rpm.getActivityType());
                actInfo.setRequest(actInfo.new Val());
                actInfo.setSend(actInfo.new Val());
                actInfo.setClick(actInfo.new Val());
                actInfo.setHisClick(actInfo.new Val());
                actInfo.setHisRequest(actInfo.new Val());
                actInfo.setHisSend(actInfo.new Val());
                actInfo.setLastClick(actInfo.new Val());
                actInfo.setLastRequest(actInfo.new Val());
                actInfo.setLastSend(actInfo.new Val());
                actInfo.setDirectRequest(actInfo.new Val());
                actInfo.setDirectSend(actInfo.new Val());

                actInfo.setCreateTime(activityCreateTimeList.get(rpm.getActivityId()).getTime());
                actInfo.setHisCost(actInfo.new Val());
                actInfo.setLastCost(actInfo.new Val());
                actInfo.setCost(actInfo.new Val());
                actInfo.setDirectClick(actInfo.new Val());
                actInfo.setDirectCost(actInfo.new Val());
                actInfoMap.put(key, actInfo);
            }
            // end
            buildActInfo(rpm, actInfo);
        });
        //调用算法获取最优对象
        List<ActivityInfo> activityInfoList = Lists.newArrayList(actInfoMap.values());
        if (CollectionUtils.isEmpty(activityInfoList)) {
            logger.info("activityInfoList is empty , slotId={}", slotId);
            return null;
        }
        // 获取算法参数
        String sGama = localCacheService.getSystemConfigValue("activity-selector-gama");
        double gama = 0D;
        try {
            gama = Double.parseDouble(sGama);
        } catch (Exception ignored) {
            //
        }
        ActivityInfo bestAct = ActivitySelector.select(activityInfoList, gama);
        // InnerLog.log("81", activityInfoList);// 记录日志 数据那边处理性能有问题
        if (null != bestAct) {
            String bestKey = bestAct.getActivityId() + SplitConstant.SPLIT_HYPHEN + bestAct.getSource();
            if (bestAct.getIsUpdate()) {
                bestAct.setUpdateTime(now);
                // 插入redis
                redis4Handler.hSet(RedisKeyConstant.getSlotActInfoNew(slotId), bestKey, bestAct);
            }
        } else {
            logger.info("rpm2 error slotId={}", slotId);
            CatUtil.log("activitySelectorError");
            // 随机选一个
            int index = new Random().nextInt(activityInfoList.size());
            bestAct = activityInfoList.get(index);
        }
        return localCacheService.getActivityDetail(bestAct.getActivityId(), (int) bestAct.getSource());
    }

    /**
     * 过滤已经投放过的活动
     */
    private List<RspActivityDto> filterVisitedActivity(List<RspActivityDto> activityList, Set<String> vhSet) {
        if (CollectionUtils.isEmpty(vhSet)) {
            return activityList;
        }
        // 过滤已经投放的活动
        List<RspActivityDto> result = new ArrayList<>();
        for (RspActivityDto activity : activityList) {
            String idAndType = activity.getId() + SplitConstant.SPLIT_HYPHEN + activity.getSource();
            if (activity.getActGroupId() != null) {
                idAndType = activity.getActGroupId().toString();
            }
            if (!vhSet.contains(idAndType)) {
                result.add(activity);
            }
        }
        // 如果全部投放过了，则返回所有活动
        if (CollectionUtils.isEmpty(result)) {
            return activityList;
        }
        return result;
    }

    private void buildRpm2List(List<RspActivityDto> filteredActList, List<ActivityRpmDto> rpmList,
                               Map<String, ActivityRpmDto> slotMap, Map<String, ActivityRpmDto> appMap,
                               Map<String, ActivityRpmDto> allMap) {
        for (RspActivityDto activity : filteredActList) {
            ActivityRpmDto slotRpmDto = slotMap.get(activity.getId() + SplitConstant.SPLIT_HYPHEN + activity.getSource());
            if (null != slotRpmDto) {
                rpmList.add(slotRpmDto);
            }
            ActivityRpmDto appRpmDto = appMap.get(activity.getId() + SplitConstant.SPLIT_HYPHEN + activity.getSource());
            if (null != appRpmDto) {
                rpmList.add(appRpmDto);
            }
            ActivityRpmDto allRpmDto = allMap.get(activity.getId() + SplitConstant.SPLIT_HYPHEN + activity.getSource());
            if (null != allRpmDto) {
                rpmList.add(allRpmDto);
            }
        }
    }

    /**
     * buildActInfo:构建活动算法模型数据. <br/>
     *
     * @param rpm     rpm对象
     * @param actInfo 活动算法模型对象
     * @author Administrator
     * @since JDK 1.6
     */
    private void buildActInfo(ActivityRpmDto rpm, ActivityInfo actInfo) {
        if (MaterialSpecificationConstant.STAT_TYPE_SLOT == rpm.getStatType()) {
            actInfo.getRequest().setSlotVal(rpm.getActRequest());
            actInfo.getSend().setSlotVal(rpm.getSendCount());

            actInfo.getClick().setSlotVal(rpm.getCouponClick());
            actInfo.getCost().setSlotVal(rpm.getCouponConsume());
            actInfo.getDirectClick().setSlotVal(rpm.getDirectCouponClick());
            actInfo.getDirectCost().setSlotVal(rpm.getDirectCouponConsume());

            actInfo.getDirectRequest().setSlotVal(rpm.getDirectActReqUv());
            actInfo.getDirectSend().setSlotVal(rpm.getDirectLaunchCount());
        } else if (MaterialSpecificationConstant.STAT_TYPE_APP == rpm.getStatType()) {
            actInfo.getRequest().setAppVal(rpm.getActRequest());
            actInfo.getSend().setAppVal(rpm.getSendCount());

            actInfo.getClick().setAppVal(rpm.getCouponClick());
            actInfo.getCost().setAppVal(rpm.getCouponConsume());
            actInfo.getDirectClick().setAppVal(rpm.getDirectCouponClick());
            actInfo.getDirectCost().setAppVal(rpm.getDirectCouponConsume());

            actInfo.getDirectRequest().setAppVal(rpm.getDirectActReqUv());
            actInfo.getDirectSend().setAppVal(rpm.getDirectLaunchCount());
        } else if (MaterialSpecificationConstant.STAT_TYPE_OVERALL == rpm.getStatType()) {
            actInfo.getRequest().setGlobalVal(rpm.getActRequest());
            actInfo.getSend().setGlobalVal(rpm.getSendCount());

            actInfo.getClick().setGlobalVal(rpm.getCouponClick());
            actInfo.getCost().setGlobalVal(rpm.getCouponConsume());
            actInfo.getDirectClick().setGlobalVal(rpm.getDirectCouponClick());
            actInfo.getDirectCost().setGlobalVal(rpm.getDirectCouponConsume());

            actInfo.getDirectRequest().setGlobalVal(rpm.getDirectActReqUv());
            actInfo.getDirectSend().setGlobalVal(rpm.getDirectLaunchCount());
        }
    }
}

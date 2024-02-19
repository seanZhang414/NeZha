package cn.com.duiba.tuia.engine.activity.service.impl;

import cn.com.duiba.nezha.compute.common.model.activityselect.ActivityInfo;
import cn.com.duiba.nezha.compute.common.model.activityselect.ActivitySelector;
import cn.com.duiba.nezha.compute.common.model.mainmeetselect.MainMeet;
import cn.com.duiba.tuia.constant.CacheKeyConstant;
import cn.com.duiba.tuia.constant.FlowSplitConstant;
import cn.com.duiba.tuia.constant.RedisKeyConstant;
import cn.com.duiba.tuia.engine.activity.handle.Redis4Handler;
import cn.com.duiba.tuia.engine.activity.message.RefreshCacheMqProducer;
import cn.com.duiba.tuia.engine.activity.service.ActivityFilterService;
import cn.com.duiba.tuia.engine.activity.service.LocalCacheService;
import cn.com.duiba.tuia.engine.activity.temp.TempFunction;
import cn.com.duiba.tuia.ssp.center.api.constant.MaterialSpecificationConstant;
import cn.com.duiba.tuia.ssp.center.api.constant.SplitConstant;
import cn.com.duiba.tuia.ssp.center.api.dto.*;
import cn.com.duiba.tuia.utils.CatUtil;
import cn.com.duiba.tuia.utils.FlowSplit;
import cn.com.duiba.tuia.utils.FlowSplitUtils;
import cn.com.duiba.wolf.perf.timeprofile.RequestTool;
import cn.com.duiba.wolf.utils.BeanUtils;
import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 活动投放历史过滤类
 *
 * @author xuyenan
 * @since 2017/1/24
 */
@Service("activityFilterService")
public class ActivityFilterServiceImpl implements ActivityFilterService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityFilterServiceImpl.class);
    @Autowired
    private LocalCacheService localCacheService;


    @Resource
    private ExecutorService executorService;

    @Resource
    private RefreshCacheMqProducer refreshCacheMqProducer;

    @Autowired
    private Redis4Handler<String, String, ActivityInfo> redis4Handler;

    private final LoadingCache<Long, Map<String, ActivityInfo>> ACTIVITYINFO_CACHE = CacheBuilder.newBuilder().initialCapacity(1000)
            .refreshAfterWrite(1, TimeUnit.HOURS).build(new CacheLoader<Long, Map<String, ActivityInfo>>() {
        @Override
        public Map<String, ActivityInfo> load(Long slotId)  {
            Map<String, ActivityInfo> actInfoInRedis = redis4Handler.entries(RedisKeyConstant.getSlotActInfo(slotId));
            return actInfoInRedis == null ? Maps.newHashMap() : actInfoInRedis;
        }

        @Override
        public ListenableFuture<Map<String, ActivityInfo>> reload(final Long key, Map<String, ActivityInfo> oldValue)  {
            ListenableFutureTask<Map<String, ActivityInfo>> task = ListenableFutureTask.create(() -> load(key));
            executorService.submit(task);
            return task;
        }
    });

     @Override
    public void refreshActivityInfo(ActivityInfo activityInfo){
         if(activityInfo==null||activityInfo.getSlotId()==0)return;
         Long slotId=activityInfo.getSlotId();

         if (ACTIVITYINFO_CACHE.asMap().containsKey(slotId)) {//如果本地缓存存在对应的广告位信息则直接更新
             String key = activityInfo.getActivityId() + SplitConstant.SPLIT_HYPHEN + activityInfo.getSource();
             ACTIVITYINFO_CACHE.asMap().get(slotId).put(key, activityInfo);
         } else {//如果本地没有缓存过从redis加载最新的数据
             ACTIVITYINFO_CACHE.refresh(slotId);
         }
    }

    @Override
    public Boolean compareActivityInfo(Long slotId) {
        Map<String,ActivityInfo> activityInfoMap= Maps.newTreeMap();

       for (Map.Entry<String,ActivityInfo> activityInfoEntry:ACTIVITYINFO_CACHE.asMap().get(slotId).entrySet()){
           activityInfoMap.put(activityInfoEntry.getKey(),activityInfoEntry.getValue());
       }
         Map<String, ActivityInfo> actInfoInRedis = Maps.newTreeMap();
        for (Map.Entry<String,ActivityInfo> activityInfoEntry: redis4Handler.entries(RedisKeyConstant.getSlotActInfo(slotId)).entrySet()){
            actInfoInRedis.put(activityInfoEntry.getKey(),activityInfoEntry.getValue());
        }
        Boolean val=JSON.toJSONString(activityInfoMap).equals(JSON.toJSONString(actInfoInRedis));
        return val;
    }

    @Override
    public RspActivityDto getCurrentActivity(String visitHistory) {
        String lastAct = visitHistory.substring(visitHistory.lastIndexOf(SplitConstant.SPLIT_COMMA) + 1, visitHistory.length());
        Long activityId = Long.parseLong(lastAct.split(SplitConstant.SPLIT_HYPHEN)[0]);
        Integer source = Integer.parseInt(lastAct.split(SplitConstant.SPLIT_HYPHEN)[1]);
        return localCacheService.getActivityDetail(activityId, source);
    }

    @Override
    public RspActivityDto getNextActivity(List<RspActivityDto> activityList, String visitHistory) {
        if (CollectionUtils.isEmpty(activityList)) {
            logger.warn("getNextActivity error, because of empty manualActivityList");
            return null;
        }
        // 先过滤出未访问的活动 然后取第一个
        RspActivityDto rspActivityDto = filterVisitedActivity(activityList, visitHistory).get(0);
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        //临时需求 http://cf.dui88.com/pages/viewpage.action?pageId=8534595
        rspActivityDto = getRspActivityDtoOne(rspActivityDto, request);
        rspActivityDto = getRspActivityDtoTwo(rspActivityDto, request);
        return rspActivityDto;
    }

    private RspActivityDto getRspActivityDtoOne(RspActivityDto rspActivityDto, HttpServletRequest request) {
        if (Objects.equals(rspActivityDto.getSource(), 2) && Objects.equals(rspActivityDto.getId(), TempFunction.QS_DIRECT_ID_ONE)) {
            if (Objects.equals(TempFunction.getCityLevel(localCacheService, RequestTool.getIpAddr(request)), 1) || !Objects.equals(RequestTool.getOSNew(request), "0")) {
                rspActivityDto = localCacheService.getActivityDetail(TempFunction.QS_ACTIVITY_ID_DOWNGRADE, 1);
            } else {
                Integer random = ThreadLocalRandom.current().nextInt(4);
                // 如果random=0，直接走默认的TempFunction.QS_DIRECT_ID_ONE即可
                if (Objects.equals(random, 1)) {
                    rspActivityDto = localCacheService.getActivityDetail(TempFunction.QS_DIRECT_ID_TWO, 2);
                } else if (Objects.equals(random, 2)) {
                    rspActivityDto = localCacheService.getActivityDetail(TempFunction.QS_DIRECT_ID_THREE, 2);
                } else if (Objects.equals(random, 3)) {
                    rspActivityDto = localCacheService.getActivityDetail(TempFunction.QS_DIRECT_ID_FOUR, 2);
                }
            }
        }
        return rspActivityDto;
    }

    private RspActivityDto getRspActivityDtoTwo(RspActivityDto rspActivityDto, HttpServletRequest request) {
        if (Objects.equals(rspActivityDto.getSource(), 2) && Objects.equals(rspActivityDto.getId(), TempFunction.QS2_DIRECT_ID_ONE)) {
            if (Objects.equals(TempFunction.getCityLevel(localCacheService, RequestTool.getIpAddr(request)), 1) || !Objects.equals(RequestTool.getOSNew(request), "0")) {
                rspActivityDto = localCacheService.getActivityDetail(TempFunction.QS_ACTIVITY_ID_DOWNGRADE, 1);
            } else {
                Integer random = ThreadLocalRandom.current().nextInt(4);
                // 如果random=0，直接走默认的TempFunction.QS_DIRECT_ID_ONE即可
                if (Objects.equals(random, 1)) {
                    rspActivityDto = localCacheService.getActivityDetail(TempFunction.QS2_DIRECT_ID_TWO, 2);
                } else if (Objects.equals(random, 2)) {
                    rspActivityDto = localCacheService.getActivityDetail(TempFunction.QS2_DIRECT_ID_THREE, 2);
                } else if (Objects.equals(random, 3)) {
                    rspActivityDto = localCacheService.getActivityDetail(TempFunction.QS2_DIRECT_ID_FOUR, 2);
                }
            }
        }
        return rspActivityDto;
    }

    @Override
    public RspActivityDto getNextActivity(Long slotId, String visitHistory, Integer strType) {
        List<RspActivityDto> activityList = localCacheService.getEngineActivityList(slotId);
        if (CollectionUtils.isEmpty(activityList)) {
            logger.warn("getNextActivity error, because of empty engineActivityList");
            return null;
        }

        if (FlowSplitConstant.ACT_ENGINE_OUTPUT == strType) {
            return getBySpmSortAndFlowSplit(slotId, activityList, visitHistory);
        } else if (FlowSplitConstant.ACT_ENGINE2_OUTPUT == strType) {
            return getBySpmSortAndFlowSplit2(slotId, activityList, visitHistory);
        } else if (FlowSplitConstant.ACT_RPM_ENGINE_OUTPUT == strType) { //新增rpm 引擎投放策略
            return getByRpmSortAndFlowSplit(slotId, activityList, visitHistory);
        } else if (FlowSplitConstant.ACT_RPM2_ENGINE_OUTPUT == strType) {
            return getByRpm2SortAndFlowSplit(slotId, activityList, visitHistory);
        } else if (FlowSplitConstant.ACT_RPM3_ENGINE_OUTPUT == strType) {
            return getByRpm3SortAndFlowSplit(slotId, activityList, visitHistory);
        } else {
            return null;
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
        checkActivityInfoNull(actInfo);
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

    private RspActivityDto getByRpm2SortAndFlowSplit(Long slotId, List<RspActivityDto> activityList, String visitHistory,
                                                     Map<String, ActivityRpmDto> slotMap,
                                                     Map<String, ActivityRpmDto> appMap,
                                                     Map<String, ActivityRpmDto> allMap) {

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
        long now = new Date().getTime();
        //redis 读写性能问题？
        Map<String, ActivityInfo> actInfoInRedis =Maps.newHashMap();
        BeanUtils.copy(ACTIVITYINFO_CACHE.getUnchecked(slotId),actInfoInRedis);//因为下面有修改取出的数据，这儿不能直接使用内存缓存返回值
        List<String> delActs = Lists.newArrayList();
        setValidAndDirect(actKeys, actInfoMap, now, actInfoInRedis, delActs);
        if (!CollectionUtils.isEmpty(delActs)) {
            redis4Handler.hDel(RedisKeyConstant.getSlotActInfo(slotId), delActs.toArray());
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
            // 修复老数据 FIXME 未来可删
            actInfo.setCreateTime(activityCreateTimeList.get(rpm.getActivityId()).getTime());
            // end
            buildActInfo(rpm, actInfo);
        });
        //调用算法获取最优对象
        List<ActivityInfo> activityInfoList = Lists.newArrayList(actInfoMap.values());
        if (CollectionUtils.isEmpty(activityInfoList)) {
            logger.info("activityInfoList is empty , slotId={}", slotId);
            return null;
        }
        ActivityInfo bestAct = ActivitySelector.select(activityInfoList);
        if (null != bestAct) {
            String bestKey = bestAct.getActivityId() + SplitConstant.SPLIT_HYPHEN + bestAct.getSource();
            if (bestAct.getIsUpdate()) {
                bestAct.setUpdateTime(now);
                // 插入redis
                redis4Handler.hSet(RedisKeyConstant.getSlotActInfo(slotId), bestKey, bestAct);
                CatUtil.log("updateActivityInfo");
                //发送更新消息
                refreshCacheMqProducer.sendMsg(CacheKeyConstant.getRefreshActivityinfoTag(), JSON.toJSONString(bestAct));
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
     * rpm引擎策略-按RPM值分配流量
     */
    @Override
    public RspActivityDto getByRpm2SortAndFlowSplit(Long slotId, List<RspActivityDto> activityList, String visitHistory) {
        // 获取rpm数据
        Map<String, ActivityRpmDto> slotMap = localCacheService.getRpmBySlotInWeek(slotId, MaterialSpecificationConstant.STAT_TYPE_SLOT);
        Map<String, ActivityRpmDto> appMap = localCacheService.getRpmBySlotInWeek(slotId, MaterialSpecificationConstant.STAT_TYPE_APP);
        // 如果没有媒体维度的数据，则继续查询全局维度的数据
        Map<String, ActivityRpmDto> allMap = localCacheService.getRpmBySlotInWeek(slotId, MaterialSpecificationConstant.STAT_TYPE_OVERALL);
        return getByRpm2SortAndFlowSplit(slotId, activityList, visitHistory, slotMap, appMap, allMap);
    }

    private void setValidAndDirect(Set<String> actKeys, Map<String, ActivityInfo> actInfoMap, long now,
                                   Map<String, ActivityInfo> actInfoInRedis, List<String> delActs) {
        actInfoInRedis.forEach((k, v) -> {
            String actKey = v.getActivityId() + SplitConstant.SPLIT_HYPHEN + v.getSource();
            if (!actKeys.contains(actKey)) {
                v.setValid(false);
            } else {
                v.setValid(true);
            }
            if (now - v.getUpdateTime() >= 24 * 60 * 60 * 5000) {
                delActs.add(k);
            } else {
                checkActivityInfoNull(v);
                actInfoMap.put(k, v);
            }
        });
    }

    private void checkActivityInfoNull(ActivityInfo v) {
        if (v.getDirectRequest() == null) {
            v.setDirectRequest(v.new Val());
        }
        if (v.getDirectSend() == null) {
            v.setDirectSend(v.new Val());
        }
        if (v.getHisCost() == null) {
            v.setHisCost(v.new Val());
        }
        if (v.getLastCost() == null) {
            v.setLastCost(v.new Val());
        }
        if (v.getClick() == null) {
            v.setClick(v.new Val());
        }
        if (v.getCost() == null) {
            v.setCost(v.new Val());
        }
        if (v.getDirectClick() == null) {
            v.setDirectClick(v.new Val());
        }
        if (v.getDirectCost() == null) {
            v.setDirectCost(v.new Val());
        }
    }

    /**
     * rpm引擎策略-按RPM值分配流量for手投
     */
    @Override
    public RspActivityDto getByRpm2SortAndFlowSplit4manual(Long slotId, List<RspActivityDto> activityList, String visitHistory) {
        // 获取rpm数据
        Map<String, ActivityRpmDto> slotMap = localCacheService.getRpmBySlotInWeek4manual(slotId, MaterialSpecificationConstant.STAT_TYPE_SLOT);
        Map<String, ActivityRpmDto> appMap = localCacheService.getRpmBySlotInWeek4manual(slotId, MaterialSpecificationConstant.STAT_TYPE_APP);
        // 如果没有媒体维度的数据，则继续查询全局维度的数据
        Map<String, ActivityRpmDto> allMap = localCacheService.getRpmBySlotInWeek4manual(slotId, MaterialSpecificationConstant.STAT_TYPE_OVERALL);
        return getByRpm2SortAndFlowSplit(slotId, activityList, visitHistory, slotMap, appMap, allMap);
    }

    /**
     * 引擎策略1-按SPM值分配流量
     */
    private RspActivityDto getBySpmSortAndFlowSplit(Long slotId, List<RspActivityDto> activityList, String visitHistory) {
        List<RspActivityDto> filteredActList = filterVisitedActivity(activityList, visitHistory);
        List<ActivitySpmDto> spmList = getSpmList(slotId, filteredActList);
        // 最后几个活动可能存在没有SPM数据的情况，按顺序投放
        if (CollectionUtils.isEmpty(spmList)) {
            return getNextActivity(filteredActList, visitHistory);
        }
        this.sortBySPM(spmList);
        // SPM排序取前20按比例分配流量
        if (spmList.size() > 20) {
            spmList = spmList.subList(0, 20);
        }
        // 按SPM切分流量
        List<FlowSplit> flowSplits = new ArrayList<>();
        for (int i = 0; i < spmList.size(); i++) {
            FlowSplit flowSplit = new FlowSplit(i, (double) spmList.get(i).getSpm());
            flowSplits.add(flowSplit);
        }
        int index = FlowSplitUtils.split(flowSplits);
        ActivitySpmDto activitySpmDto = spmList.get(index);
        return localCacheService.getActivityDetail(activitySpmDto.getActivityId(), activitySpmDto.getSource());
    }

    /**
     * 引擎策略2-按SPM值分配流量
     */
    private RspActivityDto getBySpmSortAndFlowSplit2(Long slotId, List<RspActivityDto> activityList, String visitHistory) {
        List<RspActivityDto> filteredActList = filterVisitedActivity(activityList, visitHistory);
        List<ActivitySpmDto> spmList = getSpmList(slotId, filteredActList);

        // 此种策略下如果找不到有spm值的列表，则将visitHistory重置；
        if (CollectionUtils.isEmpty(spmList)) {
            spmList = getSpmList(slotId, activityList);
        }
        this.sortBySPM(spmList);
        ActivitySpmDto activitySpmDto = spmList.get(0);
        return localCacheService.getActivityDetail(activitySpmDto.getActivityId(), activitySpmDto.getSource());
    }

    /**
     * rpm引擎策略-按RPM值分配流量
     */
    private RspActivityDto getByRpmSortAndFlowSplit(Long slotId, List<RspActivityDto> activityList, String visitHistory) {
        List<RspActivityDto> filteredActList = filterVisitedActivity(activityList, visitHistory);
        List<ActivityRpmDto> rpmList = getRpmList(slotId, filteredActList);

        // 此种策略下如果找不到有rpm值的列表，则将visitHistory重置；
        if (CollectionUtils.isEmpty(rpmList)) {
            rpmList = getRpmList(slotId, activityList);
        }
        this.sortByRPM(rpmList);
        if (CollectionUtils.isEmpty(rpmList)) {
            logger.info("rpm error slotId={}", slotId);
        }
        ActivityRpmDto activityRpmDto = rpmList.get(0);
        return localCacheService.getActivityDetail(activityRpmDto.getActivityId(), activityRpmDto.getSource());
    }

    /**
     * 查询SPM列表
     */
    private List<ActivitySpmDto> getSpmList(Long slotId, List<RspActivityDto> activityList) {
        List<ActivitySpmDto> spmList = new ArrayList<>();
        // 先查询广告位维度的数据
        Map<String, ActivitySpmDto> map = localCacheService.getSpmBySlot(slotId, MaterialSpecificationConstant.STAT_TYPE_SLOT);
        for (RspActivityDto activity : activityList) {
            ActivitySpmDto activitySpmDto = map.get(activity.getId() + SplitConstant.SPLIT_HYPHEN + activity.getSource());
            if (activitySpmDto != null && activitySpmDto.getSpm() >= 20) {
                spmList.add(activitySpmDto);
            }
        }
        if (!CollectionUtils.isEmpty(spmList)) {
            return spmList;
        }
        // 如果没有广告位维度的数据，则继续查询媒体维度的数据
        map = localCacheService.getSpmBySlot(slotId, MaterialSpecificationConstant.STAT_TYPE_APP);
        for (RspActivityDto activity : activityList) {
            ActivitySpmDto activitySpmDto = map.get(activity.getId() + SplitConstant.SPLIT_HYPHEN + activity.getSource());
            if (activitySpmDto != null && activitySpmDto.getSpm() >= 20) {
                spmList.add(activitySpmDto);
            }
        }
        if (!CollectionUtils.isEmpty(spmList)) {
            return spmList;
        }
        // 如果没有媒体维度的数据，则继续查询全局维度的数据
        map = localCacheService.getSpmBySlot(slotId, MaterialSpecificationConstant.STAT_TYPE_OVERALL);
        for (RspActivityDto activity : activityList) {
            ActivitySpmDto activitySpmDto = map.get(activity.getId() + SplitConstant.SPLIT_HYPHEN + activity.getSource());
            if (activitySpmDto != null && activitySpmDto.getSpm() >= 20) {
                spmList.add(activitySpmDto);
            }
        }
        return spmList;
    }

    /**
     * 过滤已经投放过的活动
     */
    private List<RspActivityDto> filterVisitedActivity(List<RspActivityDto> activityList, String visitHistory) {
        if (StringUtils.isEmpty(visitHistory)) {
            return activityList;
        }
        // 过滤已经投放的活动
        List<String> visitedActs = Lists.newArrayList(visitHistory.split(SplitConstant.SPLIT_COMMA));
        Set<String> vhSet = new HashSet<>();
        for (String vh : visitedActs) {
            String[] vhArray = vh.split(SplitConstant.SPLIT_HYPHEN);
            if (vhArray.length == 3) {
                vhSet.add(vhArray[2]);
            } else {
                vhSet.add(vh);
            }
        }
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

    /**
     * 根据SPM值大小从高到低排序
     */
    private void sortBySPM(List<ActivitySpmDto> spmList) {
        spmList.sort((ActivitySpmDto o1, ActivitySpmDto o2) -> {
            if (o1.getSpm() == null && o2.getSpm() == null) return 0;
            if (o1.getSpm() == null) return 1;
            if (o2.getSpm() == null) return -1;
            return o2.getSpm().compareTo(o1.getSpm());
        });
    }

    /**
     * 查询rpm list
     *
     * @author guyan
     * @since JDK 1.6
     */
    private List<ActivityRpmDto> getRpmList(Long slotId, List<RspActivityDto> activityList) {
        List<ActivityRpmDto> rpmList = new ArrayList<>();
        // 先查询广告位维度的数据
        Map<String, ActivityRpmDto> map = localCacheService.getRpmBySlot(slotId, MaterialSpecificationConstant.STAT_TYPE_SLOT);
        for (RspActivityDto activity : activityList) {
            ActivityRpmDto activityRpmDto = map.get(activity.getId() + SplitConstant.SPLIT_HYPHEN + activity.getSource());
            if (activityRpmDto != null && activityRpmDto.getRpm() >= 20) {
                rpmList.add(activityRpmDto);
            }
        }
        if (!CollectionUtils.isEmpty(rpmList)) {
            return rpmList;
        }
        // 如果没有广告位维度的数据，则继续查询媒体维度的数据
        map = localCacheService.getRpmBySlot(slotId, MaterialSpecificationConstant.STAT_TYPE_APP);
        for (RspActivityDto activity : activityList) {
            ActivityRpmDto activityRpmDto = map.get(activity.getId() + SplitConstant.SPLIT_HYPHEN + activity.getSource());
            if (activityRpmDto != null && activityRpmDto.getRpm() >= 20) {
                rpmList.add(activityRpmDto);
            }
        }
        if (!CollectionUtils.isEmpty(rpmList)) {
            return rpmList;
        }
        // 如果没有媒体维度的数据，则继续查询全局维度的数据
        map = localCacheService.getRpmBySlot(slotId, MaterialSpecificationConstant.STAT_TYPE_OVERALL);
        for (RspActivityDto activity : activityList) {
            ActivityRpmDto activityRpmDto = map.get(activity.getId() + SplitConstant.SPLIT_HYPHEN + activity.getSource());
            if (activityRpmDto != null && activityRpmDto.getRpm() >= 20) {
                rpmList.add(activityRpmDto);
            }
        }
        return rpmList;
    }


    /**
     * 根据RPM值大小从高到低排序
     */
    private void sortByRPM(List<ActivityRpmDto> rpmList) {
        rpmList.sort((ActivityRpmDto o1, ActivityRpmDto o2) -> {
            if (o1.getRpm() == null && o2.getRpm() == null) return 0;
            if (o1.getRpm() == null) return 1;
            if (o2.getRpm() == null) return -1;
            return o2.getRpm().compareTo(o1.getRpm());
        });
    }


    @Autowired
    private Redis4Handler<String, String, cn.com.duiba.nezha.compute.common.model.mainmeetselect.ActivityInfo> redis4Handler1;
    @Autowired
    private Redis4Handler<String, String, List<List<Long>>> redis4Handler2;

    /**
     * rpm引擎策略-按RPM值分配流量
     */
    @Override
    public RspActivityDto getByRpm3SortAndFlowSplit(Long slotId, List<RspActivityDto> activityList, String visitHistory) {
        // 获取rpm数据
        Map<String, ActivityRpmDto> slotMap = localCacheService.getRpmBySlotInWeek(slotId, MaterialSpecificationConstant.STAT_TYPE_SLOT);
        Map<String, ActivityRpmDto> appMap = localCacheService.getRpmBySlotInWeek(slotId, MaterialSpecificationConstant.STAT_TYPE_APP);
        // 如果没有媒体维度的数据，则继续查询全局维度的数据
        Map<String, ActivityRpmDto> allMap = localCacheService.getRpmBySlotInWeek(slotId, MaterialSpecificationConstant.STAT_TYPE_OVERALL);
        // 获取主会场下活动rpm数据
        Map<String, List<ActivityRpmWithMainMeetDto>> slotMap1 = localCacheService.getRpmWithMainMeetBySlotInWeek(slotId, MaterialSpecificationConstant.STAT_TYPE_SLOT);
        Map<String, List<ActivityRpmWithMainMeetDto>> appMap1 = localCacheService.getRpmWithMainMeetBySlotInWeek(slotId, MaterialSpecificationConstant.STAT_TYPE_APP);
        Map<String, List<ActivityRpmWithMainMeetDto>> allMap1 = localCacheService.getRpmWithMainMeetBySlotInWeek(slotId, MaterialSpecificationConstant.STAT_TYPE_OVERALL);
        return getByRpm3SortAndFlowSplit(slotId, activityList, visitHistory, slotMap, appMap, allMap, slotMap1, appMap1, allMap1);
    }

    private RspActivityDto getByRpm3SortAndFlowSplit(Long slotId, List<RspActivityDto> activityList, String visitHistory,// NOSONAR
                                                     Map<String, ActivityRpmDto> slotMap,
                                                     Map<String, ActivityRpmDto> appMap,
                                                     Map<String, ActivityRpmDto> allMap,
                                                     Map<String, List<ActivityRpmWithMainMeetDto>> slotMap1,
                                                     Map<String, List<ActivityRpmWithMainMeetDto>> appMap1,
                                                     Map<String, List<ActivityRpmWithMainMeetDto>> allMap1) {

        long timeout = 24 * 60 * 60 * 5000L;

        List<RspActivityDto> filteredActList = filterVisitedActivity(activityList, visitHistory);
        List<ActivityRpmDto> rpmList = Lists.newArrayList();
        buildRpm2List(filteredActList, rpmList, slotMap, appMap, allMap);
        //如果过滤以后没有活动，则重置历史
        if (CollectionUtils.isEmpty(rpmList) && filteredActList.size() != activityList.size()) {
            buildRpm2List(activityList, rpmList, slotMap, appMap, allMap);
        }
        List<ActivityRpmWithMainMeetDto> rpm3List = Lists.newArrayList();
        buildRpm3List(filteredActList, rpm3List, slotMap1, appMap1, allMap1);
        //如果过滤以后没有活动，则重置历史
        if (CollectionUtils.isEmpty(rpm3List) && filteredActList.size() != activityList.size()) {
            buildRpm3List(activityList, rpm3List, slotMap1, appMap1, allMap1);
        }
        rpmList.addAll(rpm3List);
        Set<String> actKeys = Sets.newHashSet();
        rpmList.forEach(rpm -> {
            long mainMeetId = -1;
            if (rpm instanceof ActivityRpmWithMainMeetDto) {
                mainMeetId = ((ActivityRpmWithMainMeetDto) rpm).getMainmeetId();
            }
            actKeys.add(rpm.getActivityId() + SplitConstant.SPLIT_HYPHEN + mainMeetId);
        });
        //构建算法模型对象
        Map<String, cn.com.duiba.nezha.compute.common.model.mainmeetselect.ActivityInfo> actInfoMap = Maps.newHashMap();
        long now = new Date().getTime();
        //redis 读写性能问题？
        Map<String, cn.com.duiba.nezha.compute.common.model.mainmeetselect.ActivityInfo> actInfoInRedis = redis4Handler1.entries(RedisKeyConstant.getSlotActInfoMain(slotId));
        List<String> delActs = Lists.newArrayList();
        // setValidAndDirect(actKeys, actInfoMap, now, actInfoInRedis, delActs);
        actInfoInRedis.forEach((k, v) -> {
            String actKey = v.getActivityId() + SplitConstant.SPLIT_HYPHEN + v.getMainMeetId();
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
            redis4Handler1.hDel(RedisKeyConstant.getSlotActInfoMain(slotId), delActs.toArray());
        }
        Map<Long, Date> activityCreateTimeList = activityList.stream().collect(Collectors.toMap(RspActivityDto::getId, RspActivityDto::getGmtCreate, (oldValue, newValue) -> newValue));
        Map<String, ActivityRpmWithMainMeetDto> missRequestBySlotInWeek = localCacheService.getMissRequestBySlotInWeek(4);
        rpmList.forEach(rpm -> {
            long mainMeetId=-1;
            if (rpm instanceof ActivityRpmWithMainMeetDto) {
                mainMeetId=((ActivityRpmWithMainMeetDto) rpm).getMainmeetId();
            }
            String key = rpm.getActivityId() + SplitConstant.SPLIT_HYPHEN + mainMeetId;
            cn.com.duiba.nezha.compute.common.model.mainmeetselect.ActivityInfo actInfo = actInfoMap.get(key);
            if (actInfo == null) {
                actInfo = new cn.com.duiba.nezha.compute.common.model.mainmeetselect.ActivityInfo();
                actInfo.setActivityId(rpm.getActivityId());
                actInfo.setSource(rpm.getSource());
                actInfo.setMainMeetId(mainMeetId);
                actInfo.setSlotId(slotId);
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

                actInfo.setCreateTime(activityCreateTimeList.get(mainMeetId!=-1?mainMeetId:rpm.getActivityId()).getTime());
                actInfo.setHisCost(actInfo.new Val());
                actInfo.setLastCost(actInfo.new Val());
                actInfo.setCost(actInfo.new Val());
                actInfo.setDirectClick(actInfo.new Val());
                actInfo.setDirectCost(actInfo.new Val());
                actInfo.setMissRequest(actInfo.new Val());
                actInfoMap.put(key, actInfo);
            }
            // 流失率
            ActivityRpmWithMainMeetDto activityRpmWithMainMeetDto = missRequestBySlotInWeek.get(mainMeetId + SplitConstant.SPLIT_HYPHEN + rpm.getAppId() + SplitConstant.SPLIT_HYPHEN + rpm.getSlotId());
            long missRequest = Optional.ofNullable(activityRpmWithMainMeetDto).map(ActivityRpmWithMainMeetDto::getMainRequestPv).orElse(0L);

            if (MaterialSpecificationConstant.STAT_TYPE_SLOT == rpm.getStatType()) {
                actInfo.getRequest().setSlotVal(rpm.getActRequest());
                actInfo.getSend().setSlotVal(rpm.getSendCount());

                actInfo.getClick().setSlotVal(rpm.getCouponClick());
                actInfo.getCost().setSlotVal(rpm.getCouponConsume());
                actInfo.getDirectClick().setSlotVal(rpm.getDirectCouponClick());
                actInfo.getDirectCost().setSlotVal(rpm.getDirectCouponConsume());

                actInfo.getDirectRequest().setSlotVal(rpm.getDirectActReqUv());
                actInfo.getDirectSend().setSlotVal(rpm.getDirectLaunchCount());
                actInfo.getMissRequest().setSlotVal(missRequest);
            } else if (MaterialSpecificationConstant.STAT_TYPE_APP == rpm.getStatType()) {
                actInfo.getRequest().setAppVal(rpm.getActRequest());
                actInfo.getSend().setAppVal(rpm.getSendCount());

                actInfo.getClick().setAppVal(rpm.getCouponClick());
                actInfo.getCost().setAppVal(rpm.getCouponConsume());
                actInfo.getDirectClick().setAppVal(rpm.getDirectCouponClick());
                actInfo.getDirectCost().setAppVal(rpm.getDirectCouponConsume());

                actInfo.getDirectRequest().setAppVal(rpm.getDirectActReqUv());
                actInfo.getDirectSend().setAppVal(rpm.getDirectLaunchCount());
                actInfo.getMissRequest().setAppVal(missRequest);
            } else if (MaterialSpecificationConstant.STAT_TYPE_OVERALL == rpm.getStatType()) {
                actInfo.getRequest().setGlobalVal(rpm.getActRequest());
                actInfo.getSend().setGlobalVal(rpm.getSendCount());

                actInfo.getClick().setGlobalVal(rpm.getCouponClick());
                actInfo.getCost().setGlobalVal(rpm.getCouponConsume());
                actInfo.getDirectClick().setGlobalVal(rpm.getDirectCouponClick());
                actInfo.getDirectCost().setGlobalVal(rpm.getDirectCouponConsume());

                actInfo.getDirectRequest().setGlobalVal(rpm.getDirectActReqUv());
                actInfo.getDirectSend().setGlobalVal(rpm.getDirectLaunchCount());
                actInfo.getMissRequest().setGlobalVal(missRequest);
            }
        });
        //调用算法获取最优对象
        List<cn.com.duiba.nezha.compute.common.model.mainmeetselect.ActivityInfo> activityInfoList = Lists.newArrayList(actInfoMap.values());
        if (CollectionUtils.isEmpty(activityInfoList)) {
            logger.info("activityInfoList is empty , slotId={}", slotId);
            return null;
        }

        List<List<Long>> mainMeetActivitysList = redis4Handler2.get(RedisKeyConstant.getSlotActInfoMain(slotId) + "_list");
        MainMeet mainMeet = new MainMeet();
        mainMeet.setActivityInfo(activityInfoList);
        mainMeet.setMainMeetActivitysList(mainMeetActivitysList);
        MainMeet bestActs = cn.com.duiba.nezha.compute.common.model.mainmeetselect.ActivitySelector.select(mainMeet);
        cn.com.duiba.nezha.compute.common.model.mainmeetselect.ActivityInfo bestAct = bestActs.getActivityInfo().get(0);
        mainMeetActivitysList = bestActs.getMainMeetActivitysList();
        for(cn.com.duiba.nezha.compute.common.model.mainmeetselect.ActivityInfo ss:bestActs.getActivityInfo()) {
            String bestKey = ss.getActivityId() + SplitConstant.SPLIT_HYPHEN + ss.getMainMeetId();
            if (ss.getIsUpdate()) {
                ss.setUpdateTime(now);
                // 插入redis
                redis4Handler1.hSet(RedisKeyConstant.getSlotActInfoMain(slotId), bestKey, ss);
            }
        }
        redis4Handler2.set(RedisKeyConstant.getSlotActInfoMain(slotId) + "_list", mainMeetActivitysList, timeout, TimeUnit.MILLISECONDS);
        if (null != bestAct) {
            long activityId = bestAct.getActivityId();
            int source = (int) bestAct.getSource();
            if (bestAct.getMainMeetId()!=-1) {
                activityId=bestAct.getMainMeetId();
                source = ReqIdAndType.REQ_ACT_SOURCE_FLOW;
            }
            return localCacheService.getActivityDetail(activityId, source);
        } else {
            logger.info("rpm3 error slotId={}", slotId);
            return null;
        }
    }

    private void buildRpm3List(List<RspActivityDto> filteredActList, List<ActivityRpmWithMainMeetDto> rpmList,
                               Map<String, List<ActivityRpmWithMainMeetDto>> slotMap, Map<String, List<ActivityRpmWithMainMeetDto>> appMap,
                               Map<String, List<ActivityRpmWithMainMeetDto>> allMap) {
        for (RspActivityDto activity : filteredActList) {
            List<ActivityRpmWithMainMeetDto> slotRpmDto = slotMap.get(activity.getId() + SplitConstant.SPLIT_HYPHEN + activity.getSource());
            if (null != slotRpmDto&&!slotRpmDto.isEmpty()) {
                rpmList.addAll(slotRpmDto);
            }
            List<ActivityRpmWithMainMeetDto> appRpmDto = appMap.get(activity.getId() + SplitConstant.SPLIT_HYPHEN + activity.getSource());
            if (null != appRpmDto&&!appRpmDto.isEmpty()) {
                rpmList.addAll(appRpmDto);
            }
            List<ActivityRpmWithMainMeetDto> allRpmDto = allMap.get(activity.getId() + SplitConstant.SPLIT_HYPHEN + activity.getSource());
            if (null != allRpmDto&&!allRpmDto.isEmpty()) {
                rpmList.addAll(allRpmDto);
            }
        }
    }
}

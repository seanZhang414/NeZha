package cn.com.duiba.tuia.engine.activity.service.impl;

import cn.com.duiba.dayu.api.client.DayuClient;
import cn.com.duiba.dayu.api.enums.ArgumentType;
import cn.com.duiba.dayu.api.result.DayuResult;
import cn.com.duiba.tuia.activity.center.api.dto.GuidePageDto;
import cn.com.duiba.tuia.activity.center.api.dto.TuiaActivityDto;
import cn.com.duiba.tuia.constant.FlowSplitConstant;
import cn.com.duiba.tuia.constant.RedisKeyConstant;
import cn.com.duiba.tuia.engine.activity.api.ApiCode;
import cn.com.duiba.tuia.engine.activity.api.Frequency4RedisSupport;
import cn.com.duiba.tuia.engine.activity.handle.Redis3Handler;
import cn.com.duiba.tuia.engine.activity.handle.SlotHandle;
import cn.com.duiba.tuia.engine.activity.log.InnerLog;
import cn.com.duiba.tuia.engine.activity.log.StatActOutputLog;
import cn.com.duiba.tuia.engine.activity.log.StatActRequestLog;
import cn.com.duiba.tuia.engine.activity.model.AdType;
import cn.com.duiba.tuia.engine.activity.model.req.GetActivityReq;
import cn.com.duiba.tuia.engine.activity.model.req.SpmActivityReq;
import cn.com.duiba.tuia.engine.activity.model.rsp.ActivityRsp;
import cn.com.duiba.tuia.engine.activity.model.rsp.GetActivityRsp;
import cn.com.duiba.tuia.engine.activity.remoteservice.ActivityService;
import cn.com.duiba.tuia.engine.activity.service.*;
import cn.com.duiba.tuia.engine.activity.service.impl.strategy.engine.StrategyEngineFactory;
import cn.com.duiba.tuia.engine.activity.temp.ActivityAccessTemp;
import cn.com.duiba.tuia.engine.activity.temp.TempFunction;
import cn.com.duiba.tuia.ssp.center.api.constant.ActivityConstant;
import cn.com.duiba.tuia.ssp.center.api.constant.SplitConstant;
import cn.com.duiba.tuia.ssp.center.api.dto.*;
import cn.com.duiba.tuia.utils.CatUtil;
import cn.com.duiba.tuia.utils.RandomUtil;
import cn.com.duiba.tuia.utils.UniqRequestIdGen;
import cn.com.duiba.tuia.utils.UrlUtil;
import cn.com.duiba.wolf.utils.DateUtils;
import cn.com.duiba.wolf.utils.UrlUtils;
import cn.com.duibaboot.ext.autoconfigure.accesslog.AccessLogFilter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.net.InternetDomainName;
import freemarker.template.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * 兑吧商业广告活动的推荐引擎。
 * <p>
 * 媒体APP通过此接口获取活动广告后，将活动广告投放到媒体APP上。 媒体可以通过媒体投放平台创造媒体广告位，设置对应的投放屏蔽策略。 同时APP客户端用户，在展现广告时，会做疲劳度控制，防止统一广告短时间内过多重复展现。
 */
@Repository("activityEngineService")
public class ActivityEngineServiceImpl implements ActivityEngineService {

    private static final Logger logger               = LoggerFactory.getLogger(ActivityEngineService.class);
    private static final Logger actOutputErrorLogger = LoggerFactory.getLogger("ActOutputErrorLog");

    private static final int ONE_YEAR = 365;
    
    private static final int ONE_MINUTE = 1;

    @Autowired
    private Configuration configuration;

    @Autowired
    private Frequency4RedisSupport frequency4RedisSupport;

    @Autowired
    private LocalCacheService localCacheService;

    @Autowired
    private FlowSplitService flowSplitService;

    @Autowired
    private ActivityFilterService activityFilterService;

    @Autowired
    private Redis3Handler redis3Handler;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityMaterialService activityMaterialService;

    @Autowired
    private StrategyEngineFactory strategyEngineFactory;
    
    @Resource
    private DayuClient dayuClient;

    /**
     * 活动曝光量+1，到达2000后变为老活动
     */
    private void addNewActExposeCount(Long activityId, Integer activitySource) {
    	String key = RedisKeyConstant.getNewActExposeCount(activityId, activitySource);
        long count = redis3Handler.increment(key, 1L);
        if(count < 2){
        	redis3Handler.expire(key, ONE_YEAR, TimeUnit.DAYS);
        }
        if (count >= flowSplitService.getActNewToOldBound() && activityService.removeFromNewActList(activityId, activitySource)) {
            // 改变活动标记位
            redis3Handler.expire(key, ONE_MINUTE, TimeUnit.MINUTES);
            // 本地所有试投列表缓存更新
            localCacheService.refreshNewActivityList();
        }
    }

    private ActivityRsp getActivityRsp4testSlots(SlotCacheDto slot, String deviceId, List<RspActivityDto> slotActivityList, String visitHistory, boolean isNew) {
        String key = RedisKeyConstant.getUserKey(slot.getId(), deviceId);
        // 获取当前投放的序号
        String sIndex = redis3Handler.hGet(key, RedisKeyConstant.getVisitIndex());
        int index = Integer.parseInt(sIndex == null ? "0" : sIndex);

        if (!CollectionUtils.isEmpty(slotActivityList)) {
            redis3Handler.hIncr(key, RedisKeyConstant.getVisitIndex(), 1L);// 生命周期跟visitHistory相同
            if (index == 0) {
                // 设置历史过期时间
                expireKey(key);
            }
            RspActivityDto rspActivityDto = slotActivityList.get(index % slotActivityList.size());
            // 判断当前当前活动是否是普通活动
            if (rspActivityDto.getSource() == ActivityConstant.REQ_ACT_SOURCE_FLOW_GUIDE_PAGE || rspActivityDto.getSource() == ReqIdAndType.REQ_GAME_SOURCE || rspActivityDto.getSource() == ReqIdAndType.REQ_GAME_HALL ) {
                // 流量引导页直接返回不走算法
                ActivityRsp rsp = new ActivityRsp();
                rsp.setActivityDto(rspActivityDto);
                rsp.setOutputWay(AdType.ADSENSE_TYPE_MANUAL);
                return rsp;
            }
        }
        if (isNew) {
            if (slot.getSlotType() != SlotDto.ADSENSE_TYPE_MANUAL) {
                return getActivityByOutputWay(slot, deviceId, FlowSplitConstant.ACT_RPM2_ENGINE_OUTPUT_NEW, null, visitHistory);
            } else {
                return getActivityByOutputWay(slot, deviceId, FlowSplitConstant.ACT_RPM2_ENGINE_OUTPUT_NEW_MANUAL, null, visitHistory);
            }
        } else {
            if (slot.getSlotType() != SlotDto.ADSENSE_TYPE_MANUAL) {
                return getActivityByOutputWay(slot, deviceId, FlowSplitConstant.ACT_RPM2_ENGINE_OUTPUT, null, visitHistory);
            } else {
                List<RspActivityDto> activityDtoList = localCacheService.getEngineActivityList(slot.getId());
                activityDtoList.addAll(localCacheService.getNewActivityList(slot.getId()));
                ActivityRsp rsp = getActivityByOutputWay(slot, deviceId, FlowSplitConstant.ACT_RPM2_ENGINE_LIMIT_OUTPUT, activityDtoList, visitHistory);
                rsp.setOutputWay(AdType.ADSENSE_TYPE_MANUAL);
                rsp.setOutputSource(ActivityRsp.ENGINE2_OUTPUT);
                return rsp;
            }
        }
    }

    private ActivityRsp getActivityRsp4testSlotsMain(SlotCacheDto slot, String deviceId, String visitHistory) {
        ActivityRsp activityByOutputWay = getActivityByOutputWay(slot, deviceId, FlowSplitConstant.ACT_RPM3_ENGINE_OUTPUT, null, visitHistory);
        if (slot.getSlotType() == SlotDto.ADSENSE_TYPE_MANUAL) {
            activityByOutputWay.setOutputSource(ActivityRsp.ENGINE2_OUTPUT);
        }
        return activityByOutputWay;
    }

    private ActivityRsp getNextActivity(SlotCacheDto slot, String deviceId) { // NOSONAR
        // 检查是否需要投放下一个活动
        String visitHistory = null;
        try {
            visitHistory = redis3Handler.hGet(RedisKeyConstant.getUserKey(slot.getId(), deviceId), RedisKeyConstant.getVisitHistory());
        } catch (Exception e) {
            CatUtil.log("getVisitHistoryError");
        }

        if (isNext(slot, deviceId, visitHistory)) {
            return getActivityByOutputWay(slot, deviceId, FlowSplitConstant.ACT_LAST_OUTPUT, null, visitHistory);
        }
        List<RspActivityDto> slotActivityList;
        if (SlotHandle.isRatioPutWay(slot.getActivityPutWay())) {
            slotActivityList = getActRatioList(slot.getId(), deviceId);
        } else {
            slotActivityList = localCacheService.getSlotActivityList(slot.getId());//广告位定制活动列表
        }
        int tag = 0;
        // 验证广告位是否在指定测试广告位列表内，如果是，则切分流量，一半走原逻辑，一半直接走主会场算法
        if (isDirectMainAlgorithm(slot)) {
            // 纯算法类型投放标记4
            tag = getTag(slot.getId(), 4);
            ActivityRsp activityRsp4testSlotsMain = getActivityRsp4testSlotsMain(slot, deviceId, visitHistory);
            activityRsp4testSlotsMain.setSubOutputWay(tag);
            return activityRsp4testSlotsMain;
        }
        //验证广告位是否在指定测试广告位列表内，如果是，则切分流量，一半走原逻辑，一半直接走算法
        if (isDirectAlgorithm(slot)) {
            // 纯算法类型投放标记4
            tag = getTag(slot.getId(), 4);
            ActivityRsp activityRsp4testSlots = getActivityRsp4testSlots(slot, deviceId, slotActivityList, visitHistory, true);
            activityRsp4testSlots.setSubOutputWay(tag);
            return activityRsp4testSlots;
        }
        //验证广告位是否在指定测试广告位列表内，如果是，则切分流量，1/3走原逻辑、1/3直接走算法、1/3走限定活动的算法
        if (isTestSlotNew(slot.getId())) {
            ActivityRsp activityRsp4testSlots;
            int i = flowSplitService.getEngineOutputTest();
            if (i == FlowSplitConstant.ENGINE_OUTPUT) {
                // 纯算法类型投放标记4
                tag = getTag(slot.getId(), 4);
                activityRsp4testSlots = getActivityRsp4testSlots(slot, deviceId, slotActivityList, visitHistory, false);
                activityRsp4testSlots.setSubOutputWay(tag);
                return activityRsp4testSlots;
            } else if (i == FlowSplitConstant.ENGINE_LIMIT_OUTPUT) {
                // 限定活动算法类型投放标记5
                tag = getTag(slot.getId(), 5);

                List<RspActivityDto> engineLimitActivityList = localCacheService.getEngineLimitActivityList(slot.getId());
                activityRsp4testSlots = getActivityByOutputWay(slot, deviceId, FlowSplitConstant.ACT_RPM2_ENGINE_LIMIT_OUTPUT, engineLimitActivityList, visitHistory);
                activityRsp4testSlots.setSubOutputWay(tag);
                return activityRsp4testSlots;
            } else {
                // 继续往下执行老逻辑
            }
        }
        List<RspActivityDto> afterShield = Collections.emptyList();
        // 有定制列表直接投放定制列表的活动
        if (!CollectionUtils.isEmpty(slotActivityList)) {
            afterShield = localCacheService.afterShield(slot, slotActivityList);
            RspActivitySlotDto activitySlotDto = localCacheService.getActivitySlotById(slot.getId());
            if (activitySlotDto != null && activitySlotDto.getDirectMode() == ActivityConstant.DIRECT_ADVERT_MODE_ONLY) {// 仅投放定制列表
                //如果屏蔽后无活动，则失效
                if (!afterShield.isEmpty()) {
                    slotActivityList = afterShield;
                }
                // 仅投投放方式投放的流量打上标签1
                tag = getTag(slot.getId(), 1);
            } else
                // 优先投放定制列表；定制列表投放完毕，则走分流器
                // 定制列表和SDK活动都投放完毕，重新开始优先投放定制列表
                if (!actListIsAllVisited(afterShield, visitHistory) && !afterShield.isEmpty()) {
                    slotActivityList = afterShield;
                    // 优投投放方式，其中投放的人工配置的标记2
                    tag = getTag(slot.getId(), 2);
                } else {
                    slotActivityList = null;
                    // 优投投放方式，人工配置列表遍历完之后进入算法投放的标记3
                    tag = getTag(slot.getId(), 3);
                }
        }
        if (!CollectionUtils.isEmpty(slotActivityList)) {
            ActivityRsp activityByOutputWay = getActivityByOutputWay(slot, deviceId, FlowSplitConstant.ACT_CUSTOM_OUTPUT, slotActivityList, visitHistory);
            activityByOutputWay.setSubOutputWay(tag);
            return activityByOutputWay;
        }
        ActivityRsp activityRsp = engineProcess(slot, deviceId, visitHistory);
        activityRsp.setSubOutputWay(tag);
        // 只有优投和仅投未配置活动才能走到这逻辑
        // http://cf.dui88.com/pages/viewpage.action?pageId=9788787
        if (slot.getSlotType() != SlotDto.ADSENSE_TYPE_MANUAL && activityRsp.getActivityDto() != null && !CollectionUtils.isEmpty(afterShield)) {
            ActivityRsp activityByOutputWay = getActivityByOutputWay(slot, deviceId, FlowSplitConstant.ACT_CUSTOM_OUTPUT, afterShield, null);
            // 优投投放方式，其中投放的人工配置的标记2
            tag = getTag(slot.getId(), 2);
            activityByOutputWay.setSubOutputWay(tag);
            return activityByOutputWay;
        }
        return activityRsp;
    }

    /**
     * 需求 http://cf.dui88.com/pages/viewpage.action?pageId=8540782
     *
     * @param slotId 广告位
     * @param tag 标签值
     * @return 标签值
     */
    public int getTag(Long slotId, int tag) {
        if (isTestSlot(slotId) || isTestSlotNew(slotId) || isTestSlotMain(slotId)) {
            return tag;
        } else {
            return 0;
        }
    }
    
    private boolean isNext(SlotCacheDto slot, String deviceId, String visitHistory){
    	return StringUtils.isNotEmpty(visitHistory) && !frequency4RedisSupport.needGetNextActivity(slot, deviceId);
    }

    private boolean isTestSlot(Long slotId) {
        String testSlotStr = localCacheService.getSystemConfigValue("test-slot-ids");
        try {
            if (StringUtils.isNotBlank(testSlotStr)) {
                List<String> testSlotIds = Lists.newArrayList(StringUtils.split(testSlotStr, ","));
                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(testSlotIds)) {
                    return testSlotIds.contains(slotId.toString());
                }
            }
        } catch (Exception e) {
            logger.warn("isTestSlot is error,e:", e);
        }
        return false;
    }

    private boolean isTestSlotNew(Long slotId) {
        String testSlotStr = localCacheService.getSystemConfigValue("test-slot-new-ids");
        try {
            if (StringUtils.isNotBlank(testSlotStr)) {
                List<String> testSlotIds = Lists.newArrayList(StringUtils.split(testSlotStr, ","));
                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(testSlotIds)) {
                    return testSlotIds.contains(slotId.toString());
                }
            }
        } catch (Exception e) {
            logger.warn("isTestSlotNew is error,e:", e);
        }
        return false;
    }

    private boolean isTestSlotMain(Long slotId) {
        String testSlotStr = localCacheService.getSystemConfigValue("test-slot-main-ids");
        try {
            if (StringUtils.isNotBlank(testSlotStr)) {
                List<String> testSlotIds = Lists.newArrayList(StringUtils.split(testSlotStr, ","));
                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(testSlotIds)) {
                    return testSlotIds.contains(slotId.toString());
                }
            }
        } catch (Exception e) {
            logger.warn("isTestSlotMain is error,e:", e);
        }
        return false;
    }

    private boolean isDirectAlgorithm(SlotCacheDto slot){
        return isTestSlot(slot.getId()) && flowSplitService.getEngineOutput() == FlowSplitConstant.ENGINE_ALL_OUTPUT;
    }

    private boolean isDirectMainAlgorithm(SlotCacheDto slot){
        return isTestSlotMain(slot.getId()) && flowSplitService.getEngineOutput() == FlowSplitConstant.ENGINE_ALL_OUTPUT;
    }
    
    private ActivityRsp engineProcess(SlotCacheDto slot, String deviceId, String visitHistory){
    	if (SlotHandle.isManualPolling(slot.getSlotType(), slot.getActivityPutWay())) {
            return getManualActDto(slot, deviceId, visitHistory);
        }else{
        	// 没有定制列表就走分流器
        	int actOutputWay = flowSplitService.getActOutputWay();
        	// 引擎投放
        	ActivityRsp rsp = getActivityRspByFlowSplit(slot, deviceId, visitHistory, actOutputWay);
        	rsp.setOutputSource(ActivityRsp.ENGINE_OUTPUT);
        	return rsp;
        }
    }
    
    private List<RspActivityDto> getActRatioList(Long slotId, String deviceId){
    	GroupRatioDayuDto groupRatioDayu = localCacheService.getactRatioList(slotId);
    	if(groupRatioDayu == null || CollectionUtils.isEmpty(groupRatioDayu.getGroupRatios())){
    		return Collections.emptyList();
    	}
    	String sceneCode ="活动组比例" + SplitConstant.SPLIT_COLON + groupRatioDayu.getSceneId();
    	DayuResult dayuResult;
		try {
			dayuResult = dayuClient.handleRequest(sceneCode, deviceId, ArgumentType.DEVICE_ID);
		} catch (Exception e) {
            CatUtil.log("dayuClientError");
			return Collections.emptyList();
		}
    	Map<String, String> paramMap = dayuResult.getArguments();
    	return groupRatioDayu.getGroupRatios().stream()
    		.filter(o->o.getActIdSources().contains(paramMap.get(o.getGroupId().toString())))
    		.map(o->{
    			RspActivityDto dto = new RspActivityDto();
    			dto.setActGroupId(o.getGroupId());
    			String idSource = paramMap.get(o.getGroupId().toString());
    			dto.setId(Long.valueOf(idSource.split(SplitConstant.SPLIT_COLON)[0]));
    			dto.setSource(Integer.valueOf(idSource.split(SplitConstant.SPLIT_COLON)[1]));
    			return dto;
    		})
    		.collect(Collectors.toList());
    }
    
    /**
     * getManualActDto:手投根据引擎获取活动. <br/>
     *
     * @param slot
     * @param deviceId
     * @param visitHistory
     * @return
     * @since JDK 1.6
     */
    private ActivityRsp getManualActDto(SlotCacheDto slot,String deviceId,String visitHistory){
        //走分流器，确定是无素材引擎还是算法
        int manualActOutputWay = flowSplitService.getManualActOutput();
        // 算法逻辑
        if (FlowSplitConstant.MANUAL_ACT_ENGINE2_OUTPUT == manualActOutputWay) {
            List<RspActivityDto> activityDtos = localCacheService.getEngineActivityList(slot.getId());
            activityDtos.addAll(localCacheService.getNewActivityList(slot.getId()));
            RspActivityDto rspActivityDto = activityFilterService.getByRpm2SortAndFlowSplit4manual(slot.getId(),
                                                                                            activityDtos,
                                                                                            visitHistory);
            ActivityRsp rsp = new ActivityRsp();
            rsp.setActivityDto(rspActivityDto);
            rsp.setOutputWay(AdType.ADSENSE_TYPE_MANUAL);
            rsp.setOutputSource(ActivityRsp.ENGINE2_OUTPUT);
            updateHistory4NoMaterial(slot, deviceId, rspActivityDto, visitHistory);
            return rsp;
        } else {
            // 无素材引擎逻辑
            Set<String> vhSet = getVHSet(visitHistory);
            StrategyEngine noMaterialStrategyEngine = strategyEngineFactory.getNoMaterialStrategyEngine();
            RspActivityDto rspActivityDto = noMaterialStrategyEngine.getActivity(slot, deviceId, vhSet);
            ActivityRsp rsp = new ActivityRsp();
            rsp.setActivityDto(rspActivityDto);
            rsp.setOutputWay(AdType.ADSENSE_TYPE_MANUAL);
            rsp.setOutputSource(ActivityRsp.ENGINE_OUTPUT);
            updateHistory4NoMaterial(slot, deviceId, rspActivityDto, visitHistory);
            return rsp;
        }
    }
    
    /**
     * 分流器投放广告
     * @param slot
     * @param deviceId
     * @param visitHistory
     * @param actOutputWay
     * @return
     */
    private ActivityRsp getActivityRspByFlowSplit(SlotCacheDto slot, String deviceId, String visitHistory, int actOutputWay) {
        if (FlowSplitConstant.ACT_ENGINE_OUTPUT == actOutputWay) {
            return getActivityByOutputWay(slot, deviceId, actOutputWay, null, visitHistory);
        } else if (FlowSplitConstant.ACT_ENGINE2_OUTPUT == actOutputWay) {
            // 引擎策略2投放
            return getActivityByOutputWay(slot, deviceId, actOutputWay, null, visitHistory);
        } else if (FlowSplitConstant.ACT_NEW_OUTPUT == actOutputWay) {
            // 试投
            // 如果没有试投活动或试投活动全部投放完，则走人工投放
            List<RspActivityDto> newActivityList = localCacheService.getNewActivityList(slot.getId());
            if (!CollectionUtils.isEmpty(newActivityList) && !actListIsAllVisited(newActivityList, visitHistory)) {
                return getActivityByOutputWay(slot, deviceId, actOutputWay, newActivityList, visitHistory);
            }
        }else if(FlowSplitConstant.ACT_RPM2_ENGINE_OUTPUT == actOutputWay){
            //RPM2算法引擎策略投放
            return getActivityByOutputWay(slot, deviceId, actOutputWay, null, visitHistory);
        }
        
        // rpm引擎策略投放
        return getActivityByOutputWay(slot, deviceId, FlowSplitConstant.ACT_RPM_ENGINE_OUTPUT, null, visitHistory);
    }

    private Set<String> getVHSet(String visitHistory) {
        Set<String> vhSet;
        if (StringUtils.isEmpty(visitHistory)) {
            vhSet = Sets.newHashSet();
        } else {
            List<String> vhList = Lists.newArrayList(visitHistory.split(SplitConstant.SPLIT_COMMA));
            vhSet = vhList.stream().map(o-> StringUtils.substringBefore(o,SplitConstant.SPLIT_HYPHEN)).collect(Collectors.toSet());
        }
        return vhSet;
    }

    /**
     * @param visitHistory 用户维度活动投放历史
     */
    private ActivityRsp getActivityByOutputWay(SlotCacheDto slot, String deviceId, Integer outputWay, List<RspActivityDto> activityList, String visitHistory) {
        Long slotId = slot.getId();
        ActivityRsp activityRsp = new ActivityRsp();
        switch (outputWay) {
            case FlowSplitConstant.ACT_LAST_OUTPUT:
                activityRsp.setActivityDto(activityFilterService.getCurrentActivity(visitHistory));
                break;
            case FlowSplitConstant.ACT_CUSTOM_OUTPUT:
            case FlowSplitConstant.ACT_MANUAL_OUTPUT:
                activityRsp.setActivityDto(activityFilterService.getNextActivity(activityList, visitHistory));
                break;
            case FlowSplitConstant.ACT_ENGINE_OUTPUT:
            case FlowSplitConstant.ACT_ENGINE2_OUTPUT:
            case FlowSplitConstant.ACT_RPM_ENGINE_OUTPUT:
            case FlowSplitConstant.ACT_RPM2_ENGINE_OUTPUT:
            case FlowSplitConstant.ACT_RPM3_ENGINE_OUTPUT:
                activityRsp.setActivityDto(activityFilterService.getNextActivity(slotId, visitHistory, outputWay));
                break;
            case FlowSplitConstant.ACT_RPM2_ENGINE_LIMIT_OUTPUT:
                activityRsp.setActivityDto(activityFilterService.getByRpm2SortAndFlowSplit4manual(slotId, activityList, visitHistory));
                break;
            case FlowSplitConstant.ACT_NEW_OUTPUT:
                RspActivityDto activityDto = activityFilterService.getNextActivity(activityList, visitHistory);
                activityRsp.setActivityDto(activityDto);
                addNewActExposeCount(activityDto.getId(), activityDto.getSource());
                break;
            case FlowSplitConstant.ACT_RPM2_ENGINE_OUTPUT_NEW_MANUAL:
                activityRsp.setActivityDto(strategyEngineFactory.getActivitySelector4ManualStrategyEngine().getActivity(slot, deviceId, getHistorySet(visitHistory)));
                break;
            case FlowSplitConstant.ACT_RPM2_ENGINE_OUTPUT_NEW:
                activityRsp.setActivityDto(strategyEngineFactory.getActivitySelector4XStrategyEngine().getActivity(slot, deviceId, getHistorySet(visitHistory)));
                break;
            default:
                break;
        }
        activityRsp.setOutputWay(outputWay);
        if (activityRsp.getActivityDto() == null) {
            actOutputErrorLogger.info("Get Activity Error:SlotId=[{}],DeviceId=[{}],OutputWay=[{}]", slotId, deviceId, outputWay);
        } else {
            // 重投上一个活动不用更新缓存
            if (FlowSplitConstant.ACT_LAST_OUTPUT != outputWay) {
                updateHistory(slotId, deviceId, activityRsp.getActivityDto(), visitHistory);
            }
        }
        return activityRsp;
    }

    private Set<String> getHistorySet(String visitHistory) {
        Set<String> vhSet = new HashSet<>();
        if (StringUtils.isNotEmpty(visitHistory)) {
            List<String> visitedActs = Lists.newArrayList(visitHistory.split(SplitConstant.SPLIT_COMMA));
            for (String vh : visitedActs) {
                String[] vhArray = vh.split(SplitConstant.SPLIT_HYPHEN);
                if (vhArray.length == 3) {
                    vhSet.add(vhArray[2]);
                } else {
                    vhSet.add(vh);
                }
            }
        }
        return vhSet;
    }

    /**
     * 更新活动投放历史
     */
    private void updateHistory(Long slotId, String deviceId, RspActivityDto activity, String visitHistory) {
        if (activity == null) {
            return;
        }
        String newValue = activity.getId() + SplitConstant.SPLIT_HYPHEN + activity.getSource();
        if(activity.getActGroupId() != null){
        	newValue = newValue + SplitConstant.SPLIT_HYPHEN + activity.getActGroupId();//活动组投放，存放活动组id
        }
        String key = RedisKeyConstant.getUserKey(slotId, deviceId);
        // 拼装value
        if (StringUtils.isNotEmpty(visitHistory) && !visitHistory.contains(newValue)) {
            newValue = visitHistory + SplitConstant.SPLIT_COMMA + newValue;
        }
        redis3Handler.hSet(key, RedisKeyConstant.getVisitHistory(), newValue);
        if(visitHistory == null){
            expireKey(key);
        }
    }
    
    /**
     * 更新活动投放历史
     */
    private void updateHistory4NoMaterial(SlotCacheDto slot, String deviceId, RspActivityDto activity, String visitHistory) {
        if (activity == null) {
            return;
        }
        Long slotId = slot.getId();
        String newValue = activity.getId() + SplitConstant.SPLIT_HYPHEN + activity.getSource();
        if(activity.getActGroupId() != null){
        	newValue = newValue + SplitConstant.SPLIT_HYPHEN + activity.getActGroupId();//活动组投放，存放活动组id
        }
        String key = RedisKeyConstant.getUserKey(slotId, deviceId);
        // 拼装value
        if (StringUtils.isNotEmpty(visitHistory) && !visitHistory.contains(newValue)) {
            newValue = visitHistory + SplitConstant.SPLIT_COMMA + newValue;
        }
        if (visitHistory != null && visitHistory.contains(newValue)) {
            if(strategyEngineFactory.getNoMaterialStrategyEngine().getDefaultActivityId().equals(activity.getId())){
                return;
            }
            List<RspActivityDto> list = localCacheService.getSlotActivityList(slot.getId());
            List<RspActivityDto> afterShield = localCacheService.afterShield(slot, list);
            StringBuilder afterShieldIds = new StringBuilder();
            for(RspActivityDto activityDto : afterShield){
            	String vh = activityDto.getId() + SplitConstant.SPLIT_HYPHEN + activityDto.getSource();
            	if(activityDto.getActGroupId() != null){
            		vh = vh + SplitConstant.SPLIT_HYPHEN + activityDto.getActGroupId();
            	}
            	vh = vh +  SplitConstant.SPLIT_COMMA;
                afterShieldIds.append(vh);
            }
            newValue = afterShieldIds.toString() + newValue;

        }
        redis3Handler.hSet(key, RedisKeyConstant.getVisitHistory(), newValue);
        if(visitHistory == null){
        	expireKey(key);
        }
    }

    private void expireKey(String key){
        // 防止凌晨00:00:00时设置过期时间出错
        int seconds = DateUtils.getToTomorrowSeconds();
        seconds += new Random().nextInt(3600 * 4);
        int time = seconds == 0 ? 24 * 3600 : seconds;
        redis3Handler.expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * 判断列表中的所有活动是否已经全部投放过
     */
    private boolean actListIsAllVisited(List<RspActivityDto> activityList, String visitHistory) {

        if (StringUtils.isEmpty(visitHistory)) {
            return false;
        }
        String[] vhArray = visitHistory.split(SplitConstant.SPLIT_COMMA);
        Set<String> vhSet = new HashSet<>();
        for (String aVhArray : vhArray) {
            String[] vh = aVhArray.split(SplitConstant.SPLIT_HYPHEN);
            if (vh.length == 3) {
                vhSet.add(vh[2]);
            } else {
                vhSet.add(aVhArray);
            }
        }
        
        for (RspActivityDto rspActivityDto : activityList) {
            String value = rspActivityDto.getId() + SplitConstant.SPLIT_HYPHEN + rspActivityDto.getSource();
            if(rspActivityDto.getActGroupId() != null){
            	value = rspActivityDto.getActGroupId().toString();
            }
            if (!vhSet.contains(value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取活动
     *
     * @param req
     * @param adslot
     * @return 活动
     */
    public GetActivityRsp getActivity(GetActivityReq req, SlotCacheDto adslot) {
        ActivityAccessTemp temp = userBehaviorTestLog(req,adslot);
        ActivityRsp    activityRsp = null;
        RspActivityDto activity    = null;
        RspActivityDto activityDetail = getBehaviorRedirectActivity(adslot, temp);
        if(null == activityDetail){
            activityRsp = getNextActivity(adslot, req.getDevice_id());
            activity    = activityRsp.getActivityDto();
            if (activity == null) {
                logger.info("not find activity, appId=[{}], deviceId=[{}]", adslot.getAppId(), req.getDevice_id());
                CatUtil.log("getActivityError");
                return null;
            }
        }else{
            activityRsp = new ActivityRsp();
            activity = activityDetail;
            activityRsp.setOutputWay(adslot.getSlotType());
            activityRsp.setActivityDto(activityDetail);
            activityRsp.setOutputSource(0);
            activityRsp.setSubOutputWay(0);
        }
        activityDetail = localCacheService.getActivityDetail(activity.getId(), activity.getSource());
        GetActivityRsp actRsp = getGetActivityRsp(req, adslot, activityRsp.getOutputWay(), activityDetail);
        if(actRsp != null){
            actRsp.setOutputSource(activityRsp.getOutputSource());
            actRsp.setSub_activity_way(activityRsp.getSubOutputWay());
        }
        return actRsp;
    }

    /**
     * 用户维度行为喜好测试
     * 通过设备ID进行测试
     * 设备ID作为KEY  用户行为数据为VALUE 存放在REDIS中
     * 第一次访问初始化数据进redis
     * 根据上一次访问的结果 判断用户的访问路径
     * http://cf.dui88.com/pages/viewpage.action?pageId=8534220
     * @param req
     * @param adslot
     */
    private ActivityAccessTemp userBehaviorTestLog(GetActivityReq req, SlotCacheDto adslot) {
        ActivityAccessTemp temp = null;
        ActivityAccessTemp thePreTime = null;
        if(TempFunction.USER_BEHAVIOR_ADVERT_TEST.contains(adslot.getId())){
            //对设备id进行分流处理
            boolean half = deviceHalf(req);
            String value = redis3Handler.get(TempFunction.USER_ACTIVITY_PRE_TEST_REDIS_FLAG + req.getDevice_id());
            if(StringUtils.isNotBlank(value)){
                //thePreTime 对象 accessNum为当前是第几次,onceJoinFlag 上次是否参与
                thePreTime = new ActivityAccessTemp();
                temp = JSONObject.parseObject(value, ActivityAccessTemp.class);
                temp.setAccessNum(temp.getAccessNum()+1);
                thePreTime.setJoinFlag(temp.getJoinFlag());
                thePreTime.setAccessNum(temp.getAccessNum());
                thePreTime.setOnceJoinFlag(temp.getOnceJoinFlag());
                thePreTime.setHit(half);
                temp.setJoinFlag(0);
            }else {
                temp = new ActivityAccessTemp();
                temp.setJoinFlag(0);
                temp.setAccessNum(1);
                temp.setOnceJoinFlag(0);
            }

            if(temp.getAccessNum()<TempFunction.ACCESS_SEVEN){
                AccessLogFilter.putExPair("accessNum",temp.getAccessNum());
                AccessLogFilter.putExPair("joinFlag",temp.getJoinFlag());
                AccessLogFilter.putExPair("deviceId",req.getDevice_id());
                AccessLogFilter.putExPair("isHit",half);
                if(temp.getAccessNum()==1){
                    redis3Handler.set(TempFunction.USER_ACTIVITY_PRE_TEST_REDIS_FLAG + req.getDevice_id()
                        , JSON.toJSONString(temp), 7, TimeUnit.DAYS);
                }else{
                    redis3Handler.set(
                        TempFunction.USER_ACTIVITY_PRE_TEST_REDIS_FLAG + req.getDevice_id(), JSON.toJSONString(temp),0);
                }
            }
        }
        return thePreTime;
    }

    /**
     * @param adslot
     * @param temp
     * @return
     */
    private RspActivityDto getBehaviorRedirectActivity(SlotCacheDto adslot, ActivityAccessTemp temp) {
        RspActivityDto activityDetail = null;

        if(TempFunction.USER_BEHAVIOR_ADVERT_3_TEST.contains(adslot.getId())&&temp!=null
            &&TempFunction.FAIL_PART_IN.equals(temp.getOnceJoinFlag())
            &&!TempFunction.ACCESS_SEVEN.equals(temp.getAccessNum())&&temp.getHit()){
            if(TempFunction.ACCESS_SECOND.equals(temp.getAccessNum())){
                activityDetail = localCacheService.getActivityDetail(TempFunction.ACCESS_SECOND_ACTIVITY, 2);
            }
            if(TempFunction.ACCESS_THREE.equals(temp.getAccessNum())){
                activityDetail = localCacheService.getActivityDetail(TempFunction.ACCESS_THREE_ACTIVITY, 2);
            }
            if(TempFunction.ACCESS_FIVE.equals(temp.getAccessNum())){
                activityDetail = localCacheService.getActivityDetail(TempFunction.ACCESS_FIVE_ACTIVITY, 2);
            }
            if(TempFunction.ACCESS_SIX.equals(temp.getAccessNum())){
                activityDetail = localCacheService.getActivityDetail(TempFunction.ACCESS_SIX_ACTIVITY, 2);
            }
        }
        return activityDetail;
    }

    /**
     * 描述：根据设备ID进行分流
     * @param
     * @return
     * @author weny.cai
     * @date 2018/8/7 11:07
     */
    private boolean deviceHalf(GetActivityReq req) {
        String hitValue = redis3Handler.get(TempFunction.USER_ACTIVITY_HIT_TEST_REDIS_FLAG + req.getDevice_id());
        //如果是一个未分流的设备 进行分流 分流数据设置至redis 否则去redis里进行判断
        if(StringUtils.isBlank(hitValue)){
            boolean half = RandomUtil.getHalf();
            if(half){
                redis3Handler.set(TempFunction.USER_ACTIVITY_HIT_TEST_REDIS_FLAG + req.getDevice_id(),"1", 7, TimeUnit.DAYS);
            }else{
                redis3Handler.set(TempFunction.USER_ACTIVITY_HIT_TEST_REDIS_FLAG + req.getDevice_id(),"0", 7, TimeUnit.DAYS);
            }
            return half;
        }else{
            if("1".equals(hitValue)){
                return true;
            }else{
                return false;
            }
        }
    }


    private GetActivityRsp getGetActivityRsp(GetActivityReq req, SlotCacheDto slot, Integer outputWay, RspActivityDto activityDetail) {
        GetActivityRsp rsp;
        if (activityDetail.getSource() == null || activityDetail.getSource() == 0) { // 0-兑吧活动
           logger.warn("still has duiba activity output");
           rsp = null;
        } else if (activityDetail.getSource() == 1) { // 1-推啊活动
            TuiaActivityDto activityTuia = localCacheService.getTuiaActivity(activityDetail.getId());
            rsp = activityMaterialService.toActivityResponse(req, activityTuia.getTitle(), activityDetail.getSource(), activityTuia, slot, outputWay,activityDetail.getDescription());
        }else if(activityDetail.getSource() == 20 ||activityDetail.getSource() == 21){
            //游戏和游戏厅特殊处理
            GetActivityRsp getActivityRsp = new GetActivityRsp();
            getActivityRsp.setSource(activityDetail.getSource());
            getActivityRsp.setAd_type(slot.getSlotType());
            getActivityRsp.setError_code(ApiCode.SUCCESS);
            getActivityRsp.setActivity_id(activityDetail.getId().toString());
            rsp = getActivityRsp;
        } else {//2-流量引导页活动
            GuidePageDto guidePageDto = activityService.getGuidePageDetail(activityDetail.getId());
            rsp = activityMaterialService.toActivityResponse(req, guidePageDto.getTitle(), activityDetail.getSource(), guidePageDto, slot, outputWay,activityDetail.getDescription());
        }
        return rsp;
    }

    private GetActivityRsp doGetActivity(GetActivityReq req, SlotCacheDto slot) {
        // 判断该用户在该广告位下连续曝光次数是否大于设定值而未被点击，此时需屏蔽广告
        if (frequency4RedisSupport.checkIsReachLimit(req.getAdslot_id(), req.getDevice_id())) {
            GetActivityRsp activityRsp = new GetActivityRsp();
            activityRsp.setError_code(ApiCode.SLOT_EXPOSE_REACH_LIMIT);
            return activityRsp;
        }

        GetActivityRsp activityRsp = getActivity(req, slot);
        if (activityRsp != null) {
            activityRsp.setDcm("401." + req.getAdslot_id() + ".0." + (activityRsp.getMaterial_id() != null ? activityRsp.getMaterial_id() : 0));
            Map<String, String> params = new HashMap<>();
            params.put("tck_rid_6c8", req.getRid());
            params.put("dcm", activityRsp.getDcm());
            params.put("tenter","SOW");

            String activityId = decryptedActivityId(activityRsp.getActivity_id());

            handleClickParam(req, activityRsp, params, activityId);
            if(StringUtils.isNotBlank(activityRsp.getClick_url())){
                activityRsp.setClick_url(UrlUtils.appendParams(activityRsp.getClick_url(), params)+"&");
            }
        }

        if (activityRsp == null) {
            activityRsp = new GetActivityRsp();
            activityRsp.setError_code(ApiCode.EMPTY_RESULT);
        }

        return activityRsp;
    }

    private void handleClickParam(GetActivityReq req, GetActivityRsp activityRsp, Map<String, String> params, String activityId) {
        String tckLocC5d;
        if (activityRsp.getSource() == 0) {  //兑吧参数处理
            if (StringUtils.contains(activityRsp.getClick_url(), "slotId")) {
                activityRsp.setClick_url(StringUtils.replace(activityRsp.getClick_url(), "slotId", "adslotId"));
            } else {
                params.put("adslotId", String.valueOf(req.getAdslot_id()));
            }
            tckLocC5d = "newtools-" + activityId;
        } else if (activityRsp.getSource() == 1) { //推啊参数处理
            if (!StringUtils.contains(activityRsp.getClick_url(), "slotId")) {
                params.put("slotId", String.valueOf(req.getAdslot_id()));
            }
            tckLocC5d = "tactivity-" + activityId;
        } else {  //流量引导页参数处理
            if (!StringUtils.contains(activityRsp.getClick_url(), "slotId")) {
                params.put("slotId", String.valueOf(req.getAdslot_id()));
            }
            if (StringUtils.contains(activityRsp.getClick_url(), "mainMeet")) {
                tckLocC5d = "tmainmeet-" + activityId;
            } else if (StringUtils.contains(activityRsp.getClick_url(), "actCenter")) {
                tckLocC5d = "tactivitycenter-" + activityId;
            } else {
                tckLocC5d = null;
            }
        }
        params.put("tck_loc_c5d", tckLocC5d);
    }

    @Override
    public GetActivityRsp getActivity(GetActivityReq req) {
        MediaAppDataDto app = localCacheService.getMediaApp(req.getApp_key());
        req.setApp_id(app != null ? app.getAppId() : 0L);
       
        //生成rid
        String rid = UniqRequestIdGen.resolveReqId();
        req.setRid(rid);

        // 记录请求广告的请求日志
        StatActRequestLog.log();
        InnerLog.log("34", req);

        SlotCacheDto   slot        = localCacheService.getSlotDetail(req.getAdslot_id());
        GetActivityRsp activityRsp = doGetActivity(req, slot);

        activityRsp.setRequest_id(req.getRequest_id());
        activityRsp.setAdslot_id(req.getAdslot_id());
        activityRsp.setExpire(0L);
        activityRsp.setRid(rid);
        activityRsp.setApp_id(req.getApp_id());
        activityRsp.setData1(rid);

        configHttps(activityRsp);

        // 记录广告返回日志
        GetActivityRsp activityRspLog = new GetActivityRsp();
        BeanUtils.copyProperties(activityRsp, activityRspLog);
        if (!StringUtils.isEmpty(activityRspLog.getActivity_id())) {
            // 输出日志的活动ID字段提取活动ID
            String data = activityRspLog.getActivity_id().split(SplitConstant.SPLIT_COMMA)[0];
            if (data.length() == SpmActivityReq.DATA_SOURCE_BIT) {
                activityRspLog.setActivity_id(StringUtils.stripStart(data.substring(1), "0"));
            } else {
                activityRspLog.setActivity_id(data);
            }
        }
        activityRspLog.setServer_time((new DateTime()).toString("yyyy-MM-dd HH:mm:ss"));
        activityRspLog.setDevice_id(req.getDevice_id());
        activityRspLog.setHost(req.getHost());
        StatActOutputLog.log(activityRspLog);  //NOSONAR
        InnerLog.log("1", activityRspLog); // 1 (广告位请求响应日志)

        return activityRsp;
    }

    @Override
    public GetActivityRsp getActivity4native(GetActivityReq req) throws Exception {
        GetActivityRsp activityRsp = getActivity(req);

        if (!ApiCode.SUCCESS.equals(activityRsp.getError_code())) {
            return activityRsp;
        }

        // 信息流类型预定义模板选择
        if (AdType.ADSENSE_TYPE_INFORMATION_STREAM == activityRsp.getAd_type()) {
            Map<String, Object> data = new HashMap<>();
            data.put("title", activityRsp.getDescription());
            data.put("image", activityRsp.getImg_url());
            data.put("adIconVisible", activityRsp.isAd_icon_visible());
            if (activityRsp.getImg_width() == 700 && activityRsp.getImg_height() == 280) { // 主图尺寸宽度700的信息流模板
                String text = FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate("activity/1.ftl", "UTF-8"), data);
                activityRsp.setAd_content(text);
            } else if (activityRsp.getImg_width() == 225 && activityRsp.getImg_height() == 140) { // 主图尺寸宽度225的信息流模板
                String text = FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate("activity/2.ftl", "UTF-8"), data);
                activityRsp.setAd_content(text);
            } else {
                logger.error("Not find ad content, adslotId=[{}],width=[{}],height=[{}]", req.getAdslot_id(), activityRsp.getImg_width(), activityRsp.getImg_height());
            }
        }
        return activityRsp;
    }

    @Override 
    public GetActivityRsp domainShield(GetActivityReq req, String referer)  {
        try {
            Set<String> shieldDomainSet = localCacheService.getShieldDomain(req.getAdslot_id());
            URL url = new URL(referer);
            String topDomain = InternetDomainName.from(url.getHost()).topPrivateDomain().toString();
            if(CollectionUtils.isEmpty(shieldDomainSet) || !shieldDomainSet.contains(topDomain)) {
                return null;
            }
            // 记录请求广告的请求日志
            StatActRequestLog.log();
            InnerLog.log("34", req);

            GetActivityRsp rsp = new GetActivityRsp();
            rsp.setError_code(ApiCode.EMPTY_RESULT);

            //输出日志
            GetActivityRsp activityRspLog = new GetActivityRsp();
            activityRspLog.setServer_time((new DateTime()).toString("yyyy-MM-dd HH:mm:ss"));
            BeanUtils.copyProperties(rsp, activityRspLog);
            StatActOutputLog.log(activityRspLog);  //NOSONAR

            InnerLog.log("1", activityRspLog); // 1 (广告位请求响应日志)
            return rsp;
        }catch (Exception e){
            return null;
        }
    }

    private void configHttps(GetActivityRsp activityRsp) {
        if (StringUtils.isNotBlank(activityRsp.getImg_url())) {
            activityRsp.setImg_url(UrlUtil.http2Https(activityRsp.getImg_url()));
        }

        if (StringUtils.isNotBlank(activityRsp.getClick_url())) {
            String clickUrl=activityRsp.getClick_url();
            String httpsUrl = UrlUtil.http2Https(clickUrl);
            if(!StringUtils.endsWith(clickUrl, "&")){
                httpsUrl = httpsUrl + "&" ;
            }
            activityRsp.setClick_url(httpsUrl);
        }
    }

    @Override
    public ActivityManualPlanDto getActivityManualPlan(String appKey, Long adslotId, Long operatorActivityId, String deviceId,String rid) {
        SlotCacheDto adslot = localCacheService.getSlotDetail(adslotId);
        if (adslot == null || !adslot.isValid() || adslot.getSlotType() != SlotDto.ADSENSE_TYPE_MANUAL) {
            logger.info("Adslot is invalid, appKey=[{}], adslotId = [{}]", appKey, adslotId);
            return null;
        }
        if(SlotHandle.isTimePutWay(adslot.getSlotType(),adslot.getActivityPutWay())){
        	// 兼容处理，先从手动列表获取,再从广告位上获取活动地址
        	ActivityManualPlanDto activityManual = localCacheService.getActivityManual(adslotId);
        	if (activityManual != null && activityManual.getActivityUrl() != null) {
        		activityManual.setLaunchType(adslot.getActivityPutWay());
                activityManual.setActivityUrl(activityManual.getActivityUrl() + "&dsm=1." + adslotId + ".0.0");
                return activityManual;
        	}
        	
        	if (StringUtils.isNotBlank(adslot.getActivityTargetUrl())) {
                ActivityManualPlanDto activityManualPlan = new ActivityManualPlanDto();
                activityManualPlan.setLaunchType(adslot.getActivityPutWay());
                activityManualPlan.setActivityUrl(adslot.getActivityTargetUrl() + "&dsm=1." + adslotId + ".0.0");
                activityManualPlan.setOperatorActivityId(operatorActivityId);
        		return activityManualPlan;
        	}
        }
        if(SlotHandle.isManualPolling(adslot.getSlotType(), adslot.getActivityPutWay()) || SlotHandle.isTimePutWay(adslot.getSlotType(), adslot.getActivityPutWay())){
        	//SDK逻辑相同，但没有素材规格的逻辑
        	GetActivityReq req = new GetActivityReq();
        	req.setAdslot_id(adslotId);
        	req.setApp_key(appKey);
        	req.setDevice_id(deviceId);
            req.setRid(rid);
            MediaAppDataDto app = localCacheService.getMediaApp(appKey);
            req.setApp_id(app.getAppId());
            
        	GetActivityRsp activityRsp = doGetActivity(req, adslot);
        	
        	configHttps(activityRsp);
        	String url = activityRsp.getClick_url();
        	//如果是游戏（20）或者游戏厅（21）则url可以为空
            if (StringUtils.isBlank(url) && activityRsp.getSource() != 20 && activityRsp.getSource() != 21) {
                return null;
            }
        	ActivityManualPlanDto activityManualPlan = new ActivityManualPlanDto();
    		activityManualPlan.setActivityUrl(url);
    		activityManualPlan.setOperatorActivityId(operatorActivityId);
    		activityManualPlan.setLaunchType(getActLaunchType(activityRsp, adslot));
            activityManualPlan.setActSource(activityRsp.getSource());
            activityManualPlan.setSubActivityWay(activityRsp.getSub_activity_way());
            try {
                String activityId = decryptedActivityId(activityRsp.getActivity_id());
                activityManualPlan.setActivityId(Long.valueOf(activityId));
            }catch (Exception e){
                //不作处理
            }
    		return activityManualPlan;
        }
        return null;
    }
    
    private int getActLaunchType(GetActivityRsp activityRsp, SlotCacheDto adslot){
    	if(ActivityRsp.ENGINE_OUTPUT == activityRsp.getOutputSource()){
			return 3;//来源于引擎
		}else if(ActivityRsp.ENGINE2_OUTPUT == activityRsp.getOutputSource()){
		    return 4;//来源于算法
		}
    	return adslot.getActivityPutWay();
    }

    private String decryptedActivityId(String activityId){
        String result = "";
        if (!StringUtils.isEmpty(activityId)) {
            // 输出日志的活动ID字段提取活动ID
            String data = activityId.split(SplitConstant.SPLIT_COMMA)[0];
            if (data.length() == SpmActivityReq.DATA_SOURCE_BIT) {
                result = StringUtils.stripStart(data.substring(1), "0");
            } else {
                result = data;
            }
        }
        return result;
    }
}

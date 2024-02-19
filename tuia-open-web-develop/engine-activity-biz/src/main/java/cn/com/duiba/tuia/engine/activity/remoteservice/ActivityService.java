package cn.com.duiba.tuia.engine.activity.remoteservice;

import cn.com.duiba.tuia.activity.center.api.dto.ActivityType;
import cn.com.duiba.tuia.activity.center.api.dto.GuidePageDto;
import cn.com.duiba.tuia.activity.center.api.dto.TuiaActivityDto;
import cn.com.duiba.tuia.activity.center.api.remoteservice.RemoteActivityService;
import cn.com.duiba.tuia.activity.center.api.remoteservice.RemoteGuidePageService;
import cn.com.duiba.tuia.ssp.center.api.constant.SplitConstant;
import cn.com.duiba.tuia.ssp.center.api.dto.*;
import cn.com.duiba.tuia.ssp.center.api.remote.RemoteActivityManualService;
import cn.com.duiba.tuia.ssp.center.api.remote.RemoteActivitySlotService;
import cn.com.duiba.tuia.ssp.center.api.remote.RemoteActivitySortService;
import cn.com.duiba.tuia.ssp.center.api.remote.RemoteTuiaActivityService;
import cn.com.duiba.wolf.dubbo.DubboResult;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @author xuyenan
 * @createTime 2016/11/25
 */

@Service("activityService")
public class ActivityService {

    private static final Logger              logger = LoggerFactory.getLogger(ActivityService.class);

    @Autowired
    private RemoteActivitySortService        remoteActivitySortService;

    @Autowired
    private RemoteTuiaActivityService        remoteTuiaActivityService;

    @Autowired
    private RemoteActivityManualService      remoteActivityManualService;

    @Autowired
    private RemoteActivityService            remoteActivityService;

    @Autowired
    private RemoteActivitySlotService        remoteActivitySlotService;

    @Autowired
    private RemoteGuidePageService           remoteGuidePageService;

    /**
     * 获取活动
     * 
     * @param activityId
     * @return
     */
    public TuiaActivityDto getTuiaActivity(Long activityId) {
        DubboResult<TuiaActivityDto> result = remoteActivityService.getActivity(activityId);
        if (!result.isSuccess()) {
            logger.error("getActivity error,actId=[{}],msg=[{}]", activityId, result.getMsg());
            return null;
        }
        return result.getResult();
    }

    /**
     * 获取手动投放活动
     * 
     * @param slotId 广告位ID
     * @return 手动投放
     */
    public ActivityManualPlanDto findActivityManualPlan(Long slotId) {
        DubboResult<ActivityManualPlanDto> result = this.remoteActivityManualService.findActivityManualPlan(slotId);
        if (!result.isSuccess()) {
            logger.error("findActivityManualPlan error,slotId=[{}],msg=[{}],", slotId, result.getMsg());
            return null;
        }
        return result.getResult();
    }

    /**
     * 获取广告位定制活动
     * 
     * @param slotId 广告位ID
     * @return 活动列表
     */
    public List<RspActivityDto> getActivityBySlot(Long slotId) {
        DubboResult<List<RspActivityDto>> result = remoteActivitySortService.getActivityBySlotNew(slotId, getActTypeMap());
        if (!result.isSuccess()) {
            logger.error("getActivityBySlot error, slotId=[{}]", slotId);
            return Collections.emptyList();
        }
        return result.getResult();
    }

    /**
     * 获取引擎投放列表
     * 
     * @return 活动列表
     */
    public List<RspActivityDto> getEngineActivityList() {
    	Map<Integer, String> actTypeMap = getActTypeMap();
        DubboResult<List<RspActivityDto>> result = remoteActivitySortService.getEngineActivityList(actTypeMap);
        if (!result.isSuccess()) {
            String log = JSONObject.toJSONString(actTypeMap);
            logger.error("getEngineActivityList error, actTypeMap={}, msg={}", log ,result.getMsg());
            return Collections.emptyList();
        }
        return result.getResult();
    }

    /**
     * 获取试投列表
     * 
     * @return 活动列表
     */
    public List<RspActivityDto> getNewActivityList() {
    	Map<Integer, String> actTypeMap = getActTypeMap();
        DubboResult<List<RspActivityDto>> result = remoteActivitySortService.getNewActivityList(actTypeMap);
        if (!result.isSuccess()) {
            String log = JSONObject.toJSONString(actTypeMap);
            logger.error("getNewActivityList error, actTypeMap={}, msg={}", log ,result.getMsg());
            return Collections.emptyList();
        }
        return result.getResult();
    }

    /**
     * 获取手动投放列表
     * 
     * @return 活动列表
     */
    public List<RspActivityDto> getDefaultActivityList() {
        DubboResult<List<RspActivityDto>> result = remoteActivitySortService.getDefaultActivityList();
        if (!result.isSuccess()) {
            logger.error("getDefaultActivityList error, msg={}", result.getMsg());
            return Collections.emptyList();
        }
        return result.getResult();
    }

    /**
     * 获取活动定向媒体
     * 
     * @param activityId 活动ID
     * @param activitySource 活动来源
     * @return 媒体ID列表
     */
    public List<Long> getActivityDirectedMediaApp(Long activityId, Integer activitySource) {
        DubboResult<List<MediaAppDto>> result = remoteTuiaActivityService.getActivityDirectedMediaApp(activityId, activitySource);
        if (!result.isSuccess()) {
            logger.error("getActivityDirectedMediaApp error,activityId={},activitySource={},msg={}",activityId,activitySource,result.getMsg());
            return Collections.emptyList();
        }
        List<MediaAppDto> appList = result.getResult();
        List<Long> appIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(appList)) {
            for (MediaAppDto appDto : appList) {
                appIds.add(appDto.getAppId());
            }
        }
        return appIds;
    }

    /**
     * 获取活动详情
     * 
     * @param activityId 活动ID
     * @param activitySource 活动来源
     * @return 活动详情
     */
    public RspActivityDto getActivityDetail(Long activityId, Integer activitySource) {
        DubboResult<RspActivityDto> result = remoteTuiaActivityService.getActivityPlanDetail(activityId, activitySource, getActTypeMap());
        if (!result.isSuccess()) {
            logger.error("getActivityPlanDetail error,activityId={},activitySource={},msg={}",activityId,activitySource,result.getMsg());
            return null;
        }
        return result.getResult();
    }

    /**
     * 获取某个活动在某个广告位下的SPM
     * 
     * @param slotId 广告位ID
     * @param statType 统计维度
     * @return 活动SPM
     */
    public Map<String, ActivitySpmDto> getSlotSpm(Long slotId, Integer statType) {
        DubboResult<List<ActivitySpmDto>> result = remoteTuiaActivityService.getSlotSpm(slotId, statType);
        if (!result.isSuccess()) {
            logger.error("getSlotSpm error,slotId={},statType={},msg={}",slotId,statType,result.getMsg());
            return Collections.emptyMap();
        }
        Map<String, ActivitySpmDto> map = new HashMap<>();
        for (ActivitySpmDto activitySpmDto : result.getResult()) {
            map.put(activitySpmDto.getActivityId() + SplitConstant.SPLIT_HYPHEN + activitySpmDto.getSource(), activitySpmDto);
        }
        return map;
    }

    /**
     * 将新活动移出试投列表
     * 
     * @param activityId 活动ID
     * @param activitySource 活动来源
     * @return Boolean
     */
    public Boolean removeFromNewActList(Long activityId, Integer activitySource) {
        DubboResult<Boolean> result = remoteTuiaActivityService.removeFromNewActList(activityId, activitySource);
        if (!result.isSuccess()) {
            logger.error("removeFromNewActList error,activityId={},activitySource={},msg={}",activityId,activitySource,result.getMsg());
            return false;
        }
        return result.getResult();
    }

    /**
     * 获取引擎投放列表
     *
     * @param ids 活动ID列表
     * @return 活动列表
     */
    public List<RspActivityDto> getActivityPlanDetailList(List<Long> ids) {
        DubboResult<List<RspActivityDto>> result = remoteActivitySortService.getActivityPlanDetailList(ids, getActTypeMap());
        if (!result.isSuccess()) {
            logger.error("getActivityPlanDetailList error,ids={},msg={}",ids,result.getMsg());
            return Collections.emptyList();
        }
        return result.getResult();
    }

    /**
     * 查询定制广告位
     * 
     * @param slotId 广告位ID
     * @return 定制广告位
     */
    public RspActivitySlotDto getActivitySlotById(Long slotId) {
        DubboResult<RspActivitySlotDto> result = remoteActivitySlotService.getById(slotId);
        if (!result.isSuccess()) {
            logger.error("getById error,slotId={},msg={}", slotId, result.getMsg());
            return null;
        }
        return result.getResult();
    }

    /**
     * 查询流量引导页
     * 
     * @param id
     * @return 流量引导页
     */
    public GuidePageDto getGuidePageDetail(Long id) {
        DubboResult<GuidePageDto> result = remoteGuidePageService.getGuidePageDetail(id);
        if (!result.isSuccess()) {
            logger.error("getGuidePageDetail error,id={},msg={}", id, result.getMsg());
            return null;
        }
        return result.getResult();
    }
    
    /**
     * getActTypeMap:(获取活动类型). <br/>
     *
     * @return
     * @throws TuiaMediaException 
     * @since JDK 1.6
     */
   private Map<Integer, String> getActTypeMap()  {
        // 批量查询推啊活动类型
        DubboResult<List<ActivityType>> selectResult = remoteActivityService.selectAllActivityType();
        // 组装活动类型map
        HashMap<Integer, String> actTypeMap = new HashMap<>();
        List<ActivityType> actTypeList = selectResult.getResult();
        for (ActivityType actType : actTypeList) {
            actTypeMap.put(actType.getId(), actType.getName());
        }
        return actTypeMap;
    }
   
   /**
    * 获取某个活动在某个广告位下的RPM
    * @param slotId 广告位ID
    * @param statType 统计维度
    * @return 活动SPM
    */
   public Map<String, ActivityRpmDto> getSlotRpm(Long slotId, Integer statType) {
       DubboResult<List<ActivityRpmDto>> result = remoteTuiaActivityService.getSlotRpm(slotId, statType);
       if (!result.isSuccess()) {
           logger.error("getSlotRpm error,slotId={},statType={},msg={}",slotId,statType,result.getMsg());
           return Collections.emptyMap();
       }
       Map<String, ActivityRpmDto> map = new HashMap<>();
       for (ActivityRpmDto activityRpmDto : result.getResult()) {
           map.put(activityRpmDto.getActivityId() + SplitConstant.SPLIT_HYPHEN + activityRpmDto.getSource(), activityRpmDto);
       }
       return map;
   }
   
    /**
     * getSlotRpmInWeek:获取广告位一周内活动rpm数据. <br/>
     * 
     * @author Administrator
     * @param slotId 广告位id
     * @param statType 纬度
     * @return
     * @since JDK 1.6
     */
    public Map<String, ActivityRpmDto> getSlotRpmInWeek(Long slotId, Integer statType) {
        DubboResult<List<ActivityRpmDto>> result = remoteTuiaActivityService.getSlotRpmInWeek(slotId, statType);
        if (!result.isSuccess()) {
            logger.error("getSlotRpm error,slotId={},statType={},msg={}", slotId, statType, result.getMsg());
            return Collections.emptyMap();
        }
        return addActivityType(result.getResult());
    }

    /**
     * 补上活动类型
     */
    private Map<String, ActivityRpmDto> addActivityType(List<ActivityRpmDto> activityRpmDtoList) {
        Set<Long> ids = activityRpmDtoList.stream().map(ActivityRpmDto::getActivityId).collect(Collectors.toSet());
        DubboResult<List<TuiaActivityDto>> result = remoteActivityService.getActivityList(new ArrayList<>(ids));
        Map<Long, Integer> activityTypeMap;
        if (!result.isSuccess()) {
            logger.error("remoteActivityService.getActivityList error,slotId={},msg={}", ids, result.getMsg());
            activityTypeMap = Collections.emptyMap();
        } else {
            activityTypeMap = result.getResult().stream().collect(Collectors.toMap(TuiaActivityDto::getId, TuiaActivityDto::getActivityType));
        }
        Map<String, ActivityRpmDto> map = new HashMap<>();
        for (ActivityRpmDto activityRpmDto : activityRpmDtoList) {
            Integer activityType = activityTypeMap.get(activityRpmDto.getActivityId());
            activityRpmDto.setActivityType(activityType);
            map.put(activityRpmDto.getActivityId() + SplitConstant.SPLIT_HYPHEN + activityRpmDto.getSource(),
                    activityRpmDto);
        }
//        Map<String, ActivityRpmDto> map = activityRpmDtoList.stream().map(activityRpmDto -> {
//            Integer activityType = activityTypeMap.get(activityRpmDto.getActivityId());
//            activityRpmDto.setActivityType(activityType);
//            return activityRpmDto;
//        }).collect(Collectors.toMap(activityRpmDto -> activityRpmDto.getActivityId() + SplitConstant.SPLIT_HYPHEN + activityRpmDto.getSource(), Function.identity()));
        return map;
    }

    /**
     * 获取广告位一周内活动更新rpm数据.
     *
     * @param slotId   广告位id
     * @param statType 纬度
     * @return
     */
    public Map<String, List<ActivityRpmWithMainMeetDto>> getSlotRpmWithMainMeetInWeek(Long slotId, Integer statType) {
        List<ActivityRpmWithMainMeetDto> result = remoteTuiaActivityService.getSlotRpmWithMainMeetInWeek(slotId, statType);
        Map<String, List<ActivityRpmWithMainMeetDto>> map = new HashMap<>();
        for (ActivityRpmWithMainMeetDto activityRpmDto : result) {
            List<ActivityRpmWithMainMeetDto> activityRpmWithMainMeetDtos = new ArrayList<>();
            activityRpmWithMainMeetDtos.add(activityRpmDto);
            map.merge(activityRpmDto.getMainmeetId() + SplitConstant.SPLIT_HYPHEN + ReqIdAndType.REQ_ACT_SOURCE_FLOW,
                    activityRpmWithMainMeetDtos, (oldValue, newValue) -> {
                        oldValue.add(activityRpmDto);
                        return oldValue;
                    });
        }
        return map;
    }

    public Map<String, ActivityRpmWithMainMeetDto> getMissRequestBySlotInWeek() {
        List<ActivityRpmWithMainMeetDto> result = remoteTuiaActivityService.getSlotRpmWithMainMeetInWeek(null, 4);
        Map<String, ActivityRpmWithMainMeetDto> map = new HashMap<>();
        for (ActivityRpmWithMainMeetDto activityRpmDto : result) {
            map.merge(activityRpmDto.getMainmeetId() + SplitConstant.SPLIT_HYPHEN + activityRpmDto.getAppId() + SplitConstant.SPLIT_HYPHEN + activityRpmDto.getSlotId(),
                    activityRpmDto, (oldValue, newValue) -> {
                        oldValue.setMainRequestPv(oldValue.getMainRequestPv() + newValue.getMainRequestPv());
                        oldValue.setMainRequestUv(oldValue.getMainRequestUv() + newValue.getMainRequestUv());
                        return oldValue;
                    });
        }
        return map;
    }
    
    public Map<String, ActivityRpmDto> getSlotRpmInWeek4manual(Long slotId, Integer statType) {
        DubboResult<List<ActivityRpmDto>> result = remoteTuiaActivityService.getSlotRpmInWeek4manual(slotId, statType);
        if (!result.isSuccess()) {
            logger.error("getSlotRpm error,slotId={},statType={},msg={}", slotId, statType, result.getMsg());
            return Collections.emptyMap();
        }
        return addActivityType(result.getResult());
    }
}

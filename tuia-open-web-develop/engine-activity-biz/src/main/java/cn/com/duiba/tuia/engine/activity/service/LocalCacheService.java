package cn.com.duiba.tuia.engine.activity.service;

import cn.com.duiba.tuia.activity.center.api.dto.TuiaActivityDto;
import cn.com.duiba.tuia.ssp.center.api.dto.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author xuyenan
 * @createTime 2017/2/4
 */
public interface LocalCacheService {

    /**
     * IP查询地理信息
     * @param ipLong
     * @return
     */
    IpAreaLibraryDto findByIpLong(Long ipLong);

	/**
	 * 获取广告位活动定制素材列表
	 * @param slotId
	 * @param actId
	 * @param actSource
	 * @return
	 */
	List<SlotActMaterial4Engine> getSlotActMaterialList(Long slotId,Long actId,Integer actSource);

    /**
     * 获取广告位定制活动列表
     * 
     * @param slotId 广告位ID
     * @return 活动列表
     */
    List<RspActivityDto> getSlotActivityList(Long slotId);
    
    /**
     * 活动组比例活动
     * @param slotId
     * @return
     */
    GroupRatioDayuDto getactRatioList(Long slotId);

    /**
     * 获取引擎投放活动列表
     * 
     * @param slotId 广告位ID
     * @return 活动列表
     */
    List<RspActivityDto> getEngineActivityList(Long slotId);

    /**
     * 获取人工投放活动列表
     *
     * @param slotId 广告位ID
     * @return 活动列表
     */
    List<RspActivityDto> getManualActivityList(Long slotId);

    /**
     * 获取试投活动列表
     *
     * @param slotId 广告位ID
     * @return 活动列表
     */
    List<RspActivityDto> getNewActivityList(Long slotId);

    /**
     * 获取限定活动算法活动列表
     *
     * @param slotId 广告位ID
     * @return 活动列表
     */
    List<RspActivityDto> getEngineLimitActivityList(Long slotId);

    /**
     * 更新广告位试投
     */
    void refreshNewActivityList();

    /**
     * 获取活动详情
     * 
     * @param activityId 活动ID
     * @param activitySource 活动来源
     * @return 活动详情
     */
    RspActivityDto getActivityDetail(Long activityId, Integer activitySource);

    /**
     * 清除活动缓存
     * 
     * @param activityId 活动ID
     * @param activitySource 活动来源
     */
    void refreshActivityDetail(Long activityId, Integer activitySource);

    /**
     * 获取广告位详情
     * 
     * @param slotId 广告位ID
     * @return 广告位
     */
    SlotCacheDto getSlotDetail(Long slotId);

    /**
     * 获取媒体详情
     * 
     * @param appKey APP秘钥
     * @return APP详情
     */
    MediaAppDataDto getMediaApp(String appKey);

    /**
     * 获取手动投放计划
     * 
     * @param slotId 广告位ID
     * @return 手动投放计划
     */
    ActivityManualPlanDto getActivityManual(Long slotId);

    /**
     * 获取活动SPM
     * 
     * @param slotId 广告位ID
     * @param statType 统计维度
     * @return 活动SPM
     */
    Map<String, ActivitySpmDto> getSpmBySlot(Long slotId, Integer statType);

    /**
     * 获取素材CTR
     * 
     * @param slotId 广告位ID
     * @return 素材CTR
     */
    List<MaterialCtrDto> getMaterialBySlotActivity(Long slotId);

    /**
     * 获取推啊活动
     * 
     * @param activityId
     * @return 推啊活动
     */
    TuiaActivityDto getTuiaActivity(Long activityId);

    /**
     * 查询定制广告位
     * 
     * @param slotId 广告位ID
     * @return 定制广告位
     */
    RspActivitySlotDto getActivitySlotById(Long slotId);

    /**
     * 过滤广告位屏蔽的活动
     * @param slotCacheDto
     * @param result
     * @return
     */
    List<RspActivityDto> afterShield(SlotCacheDto slotCacheDto,
                                     List<RspActivityDto> result) ;
    /**
     * 获取系统配置项的值
     * 
     * @param key 配置项key
     * @return 配置项的值
     */
    String getSystemConfigValue(Long key);

    DomainInfoDto getDomainInfoDto(Long slotId);

    /**
     * 获取活动RPM
     * 
     * @param slotId 广告位ID
     * @param statType 统计维度
     * @return 活动SPM
     */
    Map<String, ActivityRpmDto> getRpmBySlot(Long slotId, Integer statType);


    /**
     * 活动曝光大于5000并且没UV发券值大于1的活动列表
     * @return
     */
    List<ActivitySpmDto> getExposeCountMoreThan5000();

    /**
     * 查询被屏蔽的域名
     * @param slotId 广告位ID
     * @return
     */
    Set<String> getShieldDomain(Long slotId);

    /**
     * 获取全局域名
     */
    SystemConfigDto getGlobalDomain();
    
    
    /**
     * getRpmBySlotInWeek:获取一周内RPM数据. <br/>
     *
     * @author Administrator
     * @param slotId 广告位id
     * @param statType 纬度
     * @return
     * @since JDK 1.6
     */
    Map<String, ActivityRpmDto> getRpmBySlotInWeek(Long slotId, Integer statType);


    /**
     * 获取一周内更新RPM数据（包含主会场）.
     *
     * @param slotId   广告位id
     * @param statType 纬度
     * @return Map<String, ActivityRpmDto>
     */
    Map<String, List<ActivityRpmWithMainMeetDto>> getRpmWithMainMeetBySlotInWeek(Long slotId, Integer statType);

    /**
     * 
     * validateAppKey:(验证appKey). <br/>
     *
     * @author guyan
     * @param appKey
     * @return
     */
    Boolean validateAppKey(String appKey);
    
    /**
     * 
     * validateSlotId:(验证广告位Id). <br/>
     *
     * @author guyan
     * @param slotId
     * @return
     */
    Boolean validateSlotId(Long slotId);
    
    /**
     * getSckBySlot:根据广告位id获取素材信息 <br/>
     *
     * @author Administrator
     * @param slotId 广告位id
     * @return
     * @since JDK 1.6
     */
    List<SlotSck> getSckBySlot(Long slotId);

    Map<String, ActivityRpmWithMainMeetDto> getMissRequestBySlotInWeek(Integer statType);

    Map<String, ActivityRpmDto> getRpmBySlotInWeek4manual(Long slotId, Integer statType);

    String getSystemConfigValue(String key);

    /**
     * 通过媒体ID找到对应的域名策略
     *
     * @param appId
     * @return
     */
    DomainConfigDto getDomainConfigByAppId(Long appId);

    /**
     * 查询广告位定制游戏
     * @param slotId
     * @return
     */
    List<SlotGameDto> getSlotGameList(Long slotId);

    List<ActivityDto> getEnableGameList();
}

package cn.com.duiba.tuia.engine.activity.service;

import cn.com.duiba.nezha.compute.common.model.activityselect.ActivityInfo;
import cn.com.duiba.tuia.ssp.center.api.dto.RspActivityDto;

import java.util.List;

/**
 * @author xuyenan
 * @since 2017/2/4
 */
public interface ActivityFilterService {

    /**
     * 获取当前投放的活动
     *
     * @param visitHistory 用户活动投放历史
     * @return 活动
     */
    RspActivityDto getCurrentActivity(String visitHistory);

    /**
     * 获取活动列表中下一个未投放过的活动
     *
     * @param activityList 活动列表
     * @param visitHistory 用户活动投放历史
     * @return 活动
     */
    RspActivityDto getNextActivity(List<RspActivityDto> activityList, String visitHistory);

    /**
     * 引擎投放获取下一个活动
     * 
     * @param slotId 广告位ID
     * @param visitHistory 用户活动投放历史
     * @param strType 引擎策略类型
     * @return 活动
     */
    RspActivityDto getNextActivity(Long slotId, String visitHistory, Integer strType);
    
    /**
     * getByRpm2SortAndFlowSplit:算法投放. <br/>
     *
     * @param slotId 广告位id
     * @param activityList 活动列表
     * @param visitHistory 访问历史
     * @since JDK 1.6
     */
    RspActivityDto getByRpm2SortAndFlowSplit(Long slotId, List<RspActivityDto> activityList, String visitHistory) ;
    
    RspActivityDto getByRpm2SortAndFlowSplit4manual(Long slotId, List<RspActivityDto> activityList, String visitHistory);

    void refreshActivityInfo(ActivityInfo activityInfo);

    Boolean compareActivityInfo(Long slotId);

    RspActivityDto getByRpm3SortAndFlowSplit(Long slotId, List<RspActivityDto> activityList, String visitHistory);
}

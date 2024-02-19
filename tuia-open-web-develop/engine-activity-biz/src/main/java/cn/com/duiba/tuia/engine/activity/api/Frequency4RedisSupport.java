package cn.com.duiba.tuia.engine.activity.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import cn.com.duiba.tuia.constant.RedisKeyConstant;
import cn.com.duiba.tuia.engine.activity.handle.Redis3Handler;
import cn.com.duiba.tuia.engine.activity.service.LocalCacheService;
import cn.com.duiba.tuia.ssp.center.api.dto.SlotCacheDto;

/**
 * 用户访问频次控制
 */
@Component
public class Frequency4RedisSupport {

    @Autowired
    private Redis3Handler redis3Handler;
    
    @Autowired
    private LocalCacheService localCacheService;

    /**
     * 频次增加
     * 
     * @param slotId 广告位ID
     * @param deviceId 用户唯一标示
     * @param slotLimit 广告位曝光上限
     * @param activityLimit 活动曝光上限
     */
    public void increaseFrequency(Long slotId, String deviceId, Integer slotLimit, Integer activityLimit) {
        if (slotLimit != null && slotLimit != 0) {
            redis3Handler.hIncr(RedisKeyConstant.getUserKey(slotId, deviceId), RedisKeyConstant.getSlotFrequency(), 1L);
        }
        if (activityLimit != null && activityLimit != 0) {
            redis3Handler.hIncr(RedisKeyConstant.getUserKey(slotId, deviceId), RedisKeyConstant.getActivityFrequency(), 1L);
        }
    }

    /**
     * 重置频次
     *
     * @param slotId 广告位ID
     * @param deviceId 用户唯一标示
     * @param slotLimit 广告位曝光上限
     * @param activityLimit 活动曝光上限
     */
    public void resetFrequency(Long slotId, String deviceId, Integer slotLimit, Integer activityLimit) {
        if (slotLimit != null && slotLimit != 0) {
            redis3Handler.hDel(RedisKeyConstant.getUserKey(slotId, deviceId), RedisKeyConstant.getSlotFrequency());
        }
        if (activityLimit != null && activityLimit != 0) {
            redis3Handler.hDel(RedisKeyConstant.getUserKey(slotId, deviceId), RedisKeyConstant.getActivityFrequency());
        }
    }

    /**
     * 检查当前活动连续曝光次数是否大于等于设定值而未被点击，此时需要投放下一个活动
     *
     * @param adslot 广告位ID
     * @param deviceId 用户唯一标示
     * @return boolean
     */
    public boolean needGetNextActivity(SlotCacheDto adslot, String deviceId) {
        if (adslot == null || adslot.getActivityExposeLimit() == null || adslot.getActivityExposeLimit() == 0) {
            return true;
        }
        Object activityFrequency = redis3Handler.hGet(RedisKeyConstant.getUserKey(adslot.getId(), deviceId), RedisKeyConstant.getActivityFrequency());
        // 如果value为空，则表示要获取下一个活动
        if (StringUtils.isEmpty(activityFrequency)) {
            return true;
        }

        if (Integer.parseInt(activityFrequency.toString()) >= adslot.getActivityExposeLimit()) {
            redis3Handler.hDel(RedisKeyConstant.getUserKey(adslot.getId(), deviceId), RedisKeyConstant.getActivityFrequency());
            return true;
        }
        return false;
    }

    /**
     * 判断该用户在该广告位下连续曝光次数是否大于设定值而未被点击，此时需屏蔽广告
     *
     * @param slotId 广告位ID
     * @param deviceId 用户唯一标示
     * @return boolean
     */
    public boolean checkIsReachLimit(Long slotId, String deviceId) {
        SlotCacheDto slotCacheDto = localCacheService.getSlotDetail(slotId);
        if (slotCacheDto == null || slotCacheDto.getSlotExposeLimit() == null || slotCacheDto.getSlotExposeLimit() == 0) {
            return false;
        }
       
        Object slotFrequency = redis3Handler.hGet(RedisKeyConstant.getUserKey(slotId, deviceId), RedisKeyConstant.getSlotFrequency());
        return slotFrequency != null && Integer.parseInt(slotFrequency.toString()) >= slotCacheDto.getSlotExposeLimit();
    }
}

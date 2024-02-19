/**
 * Project Name:engine-activity-biz File Name:MaterialService.java Package
 * Name:cn.com.duiba.tuia.engine.activity.service.impl Date:2017年11月6日上午11:04:59 Copyright (c) 2017, duiba.com.cn All
 * Rights Reserved.
 */

package cn.com.duiba.tuia.engine.activity.service.impl;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.duiba.tuia.constant.RedisKeyConstant;
import cn.com.duiba.tuia.engine.activity.handle.Redis3Handler;
import cn.com.duiba.tuia.engine.activity.service.LocalCacheService;
import cn.com.duiba.tuia.engine.activity.service.MaterialService;
import cn.com.duiba.tuia.ssp.center.api.dto.SlotSck;
import cn.com.duiba.wolf.utils.DateUtils;

@Service
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private Redis3Handler     redis3Handler;

    @Autowired
    private LocalCacheService localCacheService;

    @Override
    public String getMaterialUrl(String scheme, Long slotId, String deviceId) {
        String materialUrl = "";
        List<SlotSck> slotScks = localCacheService.getSckBySlot(slotId);
        // 如果没有配置素材，则记录日志并返回
        if (CollectionUtils.isEmpty(slotScks)) {
            return materialUrl;
        }
        String key = RedisKeyConstant.getUserKey(slotId, deviceId);
        Object materialVisitHistory = redis3Handler.hGet(key, RedisKeyConstant.getManualMaterialVisitHistory());
        if (materialVisitHistory == null) {
            materialUrl = slotScks.get(0).getScUrl();
            // 添加手动历史
            redis3Handler.hSet(key, RedisKeyConstant.getManualMaterialVisitHistory(),
                    slotScks.get(0).getSckId().toString());
            // 设置历史过期时间
            expireKey(key);
        } else {
            String mmvh = materialVisitHistory.toString();
            String mmvhStr = "," + mmvh + ",";
            StringBuilder mmvhNew = new StringBuilder(mmvh).append(",");
            for (SlotSck sck : slotScks) {
                String sckId = "," + sck.getSckId() + ",";
                if (StringUtils.indexOf(mmvhStr, sckId) == -1) {
                    // 添加手动历史
                    materialUrl = sck.getScUrl();
                    mmvhNew.append(sck.getSckId());
                    redis3Handler.hSet(key, RedisKeyConstant.getManualMaterialVisitHistory(), mmvhNew.toString());
                    break;
                }
            }
            // 如果过滤历史后没有素材，则重置历史
            if (StringUtils.isBlank(materialUrl)) {
                materialUrl = slotScks.get(0).getScUrl();
                // 添加手动历史
                redis3Handler.hSet(key, RedisKeyConstant.getManualMaterialVisitHistory(),
                        slotScks.get(0).getSckId().toString());
            }
        }
        if (StringUtils.isNotBlank(materialUrl) && StringUtils.startsWith(materialUrl, "//")) {
            materialUrl = scheme + ":" + materialUrl;
        }
        return materialUrl;
    }

    private void expireKey(String key) {
        // 防止凌晨00:00:00时设置过期时间出错
        int seconds = DateUtils.getToTomorrowSeconds();
        seconds += new Random().nextInt(3600 * 4);
        int time = seconds == 0 ? 24 * 3600 : seconds;
        redis3Handler.expire(key, time, TimeUnit.SECONDS);
    }

}

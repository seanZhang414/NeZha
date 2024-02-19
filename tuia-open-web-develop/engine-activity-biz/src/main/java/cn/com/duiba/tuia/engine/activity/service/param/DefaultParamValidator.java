/**
 * Project Name:engine-activity-biz
 * File Name:DefaultParamValidator.java
 * Package Name:cn.com.duiba.tuia.engine.activity.service.param.impl
 * Date:2017年12月12日下午1:44:26
 * Copyright (c) 2017, duiba.com.cn All Rights Reserved.
 *
*/

package cn.com.duiba.tuia.engine.activity.service.param;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.duiba.tuia.engine.activity.model.req.GetActivityReq;
import cn.com.duiba.tuia.engine.activity.model.req.ManualParamReq;
import cn.com.duiba.tuia.engine.activity.service.LocalCacheService;
import cn.com.duiba.tuia.ssp.center.api.dto.MediaAppDataDto;
import cn.com.duiba.tuia.ssp.center.api.dto.SlotCacheDto;
import cn.com.duiba.tuia.ssp.center.api.dto.SlotDto;

/**
 * ClassName:DefaultParamValidator <br/>
 * Function: 默认校验器 <br/>
 * Date:     2017年12月12日 下午1:44:26 <br/>
 * @author   Administrator
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
@Service("defaultParamValidator")
public class DefaultParamValidator {
    private static final Logger logger               = LoggerFactory.getLogger(DefaultParamValidator.class);
    @Autowired
    private  LocalCacheService localCacheService;
    
    public boolean reqParamValidate(GetActivityReq param) {
        if (param.getRequest_id() == null || !(localCacheService.validateAppKey(param.getApp_key())) || !(localCacheService.validateSlotId(param.getAdslot_id())) || param.getDevice_id() == null) {
            return false;
        }
        MediaAppDataDto app = localCacheService.getMediaApp(param.getApp_key());
        SlotCacheDto   slot        = localCacheService.getSlotDetail(param.getAdslot_id());
        if (app == null || slot == null || (slot.getAppId() != null && !slot.getAppId().equals(app.getAppId()))) {
            // 做兼容处理，广告位存在appId信息时，必须与app一致
            return false;
        }
        if (app.getAppId() <= 0 || !app.isValid()) {
            logger.info("Media App is not valid. appKey=[{}]", param.getApp_key());
            return false;
        }
        if (!slot.isValid()) {
            logger.info("Adslot is not valid. slotId=[{}], slotType=[{}]", param.getAdslot_id(), slot.getSlotType());
            return false;
        }
        return true;
    }
    
    public boolean reqParamValidate(ManualParamReq param){
        if (!(localCacheService.validateAppKey(param.getAppKey())) || !(localCacheService.validateSlotId(param.getAdslotId()))) {
            logger.info("app is invalid, appKey=[{}], adslotId = [{}]", param.getAppKey(), param.getAdslotId());
            return false;
        }
        
        MediaAppDataDto app = localCacheService.getMediaApp(param.getAppKey());
        if (app == null || !app.isValid() || app.getAppId() <= 0) {
            logger.info("app is invalid, appKey=[{}], adslotId = [{}]", param.getAppKey(), param.getAdslotId());
            return false;
        }
        
        SlotCacheDto adslot = localCacheService.getSlotDetail(param.getAdslotId());
        if (!adslot.isValid() || adslot.getSlotType() != SlotDto.ADSENSE_TYPE_MANUAL || !adslot.getAppId().equals(app.getAppId())) {
            logger.info("Adslot is invalid, appKey=[{}], adslotId = [{}]", param.getAppKey(), param.getAdslotId());
            return false;
        }
        return true;
    }

    public boolean reqParamValidate(String appKey, Long slotId) {
        if (!(localCacheService.validateAppKey(appKey)) || !(localCacheService.validateSlotId(slotId))) {
            logger.info("app is invalid, appKey=[{}], adslotId = [{}]", appKey, slotId);
            return false;
        }

        MediaAppDataDto app = localCacheService.getMediaApp(appKey);
        if (app == null || !app.isValid() || app.getAppId() <= 0) {
            logger.info("app is invalid, appKey=[{}], adslotId = [{}]", appKey, slotId);
            return false;
        }

        SlotCacheDto adslot = localCacheService.getSlotDetail(slotId);
        if (!adslot.isValid() || adslot.getSlotType() != SlotDto.ADSENSE_TYPE_MANUAL || !adslot.getAppId().equals(app.getAppId())) {
            logger.info("Adslot is invalid, appKey=[{}], adslotId = [{}]", appKey, slotId);
            return false;
        }
        return true;
    }
}


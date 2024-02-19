package cn.com.duiba.tuia.engine.activity.remoteservice;

import java.util.Collections;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.duiba.tuia.ssp.center.api.dto.ManagerShieldStrategyDto;
import cn.com.duiba.tuia.ssp.center.api.dto.MediaAppDataDto;
import cn.com.duiba.tuia.ssp.center.api.dto.SlotCacheDto;
import cn.com.duiba.tuia.ssp.center.api.remote.RemoteManagerShieldStrategyService;
import cn.com.duiba.tuia.ssp.center.api.remote.RemoteMediaService;
import cn.com.duiba.wolf.dubbo.DubboResult;

/**
 * @author xuyenan
 * @createTime 2017/2/7
 */
@Service("mediaService")
public class MediaService {

    private static final Logger logger = LoggerFactory.getLogger(MediaService.class);

    @Autowired
    private RemoteMediaService  remoteMediaService;

    @Autowired
    private RemoteManagerShieldStrategyService remoteManagerShieldStrategyService;

    /**
     * 查询广告位详情
     * 
     * @param slotId 广告位ID
     * @return 广告位详情
     */
    public SlotCacheDto getSlot(Long slotId) {
        DubboResult<SlotCacheDto> result = remoteMediaService.getSlot(slotId);
        if (!result.isSuccess()) {
            logger.error("getSlot error,slotId=[{}],msg=[{}],", slotId, result.getMsg());
            return null;
        }
        return result.getResult();
    }

    /**
     * 查询广告位屏蔽策略
     *
     * @param id 屏蔽策略id
     * @return 广告位屏蔽策略id
     */
    public ManagerShieldStrategyDto getShieldStrategy(Long id) {
        DubboResult<ManagerShieldStrategyDto> result = remoteManagerShieldStrategyService.selectById(id);
        if (!result.isSuccess()) {
            logger.error("remoteStrategyService selectShieldStrategyById error,id=[{}],msg=[{}],", id, result.getMsg());
            return null;
        }
        return result.getResult();
    }

    /**
     * 查询APP详情
     * 
     * @param appKey AppKey
     * @return AppKey
     */
    public MediaAppDataDto getMediaAppByKey(String appKey) {
        DubboResult<MediaAppDataDto> appResult = remoteMediaService.getMediaAppByKey(appKey);
        if (!appResult.isSuccess()) {
            logger.error("getMediaAppByKey error, appKey=[{}], msg=[{}]", appKey, appResult.getMsg());
            return null;
        }
        return appResult.getResult();
    }
    
    /**
     * 
     * getAllAppKeys:(查询所有的媒体ids). <br/>
     *
     * @author guyan
     * @return
     */
    public Set<String> getAllAppKeys(){
        DubboResult<Set<String>> appResult = remoteMediaService.getAllAppKeys();
        if (!appResult.isSuccess()) {
            logger.error("getAllAppKeys error, msg=[{}]",appResult.getMsg());
            return Collections.emptySet();
        }
        return appResult.getResult();
    }
    
    /**
     * 
     * getAllSlotIds:(查询所有的广告位ids). <br/>
     *
     * @author guyan
     * @return
     */
    public Set<Long > getAllSlotIds(){
        DubboResult<Set<Long>> slotResult = remoteMediaService.getAllSlotIds();
        if (!slotResult.isSuccess()) {
            logger.error("getAllSlotIds error, msg=[{}]",slotResult.getMsg());
            return Collections.emptySet();
        }
        return slotResult.getResult();
        
    }
}

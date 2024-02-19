package cn.com.duiba.nezha.engine.biz.service.advert.ctr;

import cn.com.duiba.nezha.engine.biz.domain.AdvertStatFeatureDo;
import cn.com.duiba.nezha.engine.biz.domain.advert.Advert;

import java.util.Collection;
import java.util.Map;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: AdvertStatFeatureService.java , v 0.1 2017/11/30 下午4:21 ZhouFeng Exp $
 */
public interface AdvertStatFeatureService {

    /**
     * 查询广告统计特征
     *
     * @param appId
     * @param slotId
     * @param activityId
     * @param adverts
     */
    Map<Long, AdvertStatFeatureDo> get(Long appId, Long slotId, Long activityId, Collection<Advert> adverts);

}

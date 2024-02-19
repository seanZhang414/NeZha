package cn.com.duiba.nezha.engine.biz.service.advert.ctr;

import cn.com.duiba.nezha.engine.biz.entity.nezha.advert.AdvertStatisticMergeEntity;
import cn.com.duiba.nezha.engine.biz.service.CacheService;

import java.util.Map;
import java.util.Set;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: TagStatAssociationService.java , v 0.1 2018/2/5 下午7:35 ZhouFeng Exp $
 */
public interface TagStatAssociationService {

    /**
     * 查询app、全局维度下标签多个时间的统计
     *
     * @param appId
     * @param tagIds
     * @return <标签ID，分时维度融合数据>
     */
    Map<String, AdvertStatisticMergeEntity> getTagStat(Long appId, Set<String> tagIds);


    Map<String, CacheService.CacheInfo> getCacheInfo();

}

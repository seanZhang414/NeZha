package cn.com.duiba.nezha.engine.biz.bo.advert;

import cn.com.duiba.nezha.engine.api.enums.StatDataTypeEnum;
import cn.com.duiba.nezha.engine.biz.domain.AdvertStatDo;
import cn.com.duiba.nezha.engine.biz.domain.advert.OrientationPackage;
import cn.com.duiba.nezha.engine.biz.entity.nezha.advert.AdvertStatisticMergeEntity;
import cn.com.duiba.nezha.engine.biz.service.advert.ctr.AdvertStatService;
import com.google.common.collect.Table;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: AdvertStatAssociationBo.java , v 0.1 2017/12/14 下午4:33 ZhouFeng Exp $
 */
public interface AdvertStatAssociationBo {

    /**
     * 查询广告在不同app下七天统计
     *
     * @return <广告ID，7日融合统计数据>
     */
    Map<Long, AdvertStatDo> get7DayStat(Set<Long> appIds, Long advertId, Map<Long, Long> advertId2PkgId);


    /**
     * 获取智能匹配的当天统计数据
     *
     * @param advertList 广告列表
     * @return 当天智能匹配广告数据
     */
    Map<OrientationPackage, AdvertStatDo> getTodayAutoMatchStat(Collection<OrientationPackage> advertList);


    /**
     * 查询广告app、全局下不同广告多个维度（时间、类型）的统计
     *
     * @return <广告ID，数据类型，分时维度融合数据>
     */
    <R> Table<R, StatDataTypeEnum, AdvertStatisticMergeEntity> getMultiDimStat(Long appId,
                                                                               Function<OrientationPackage, R> typeMapper,
                                                                               Collection<OrientationPackage> orientationPackages,
                                                                               Function<OrientationPackage, AdvertStatService.Query> globalMapper);


    /**
     * 查询今日每小时统计数据
     * @return <广告ID，数据类型，每小时统计值>
     */
    Table<Long, StatDataTypeEnum, List<Double>> getTodayHourlyStat(Long appId, Collection<Long> advertIds, Map<Long, Long>
            advertId2PkgId);
}

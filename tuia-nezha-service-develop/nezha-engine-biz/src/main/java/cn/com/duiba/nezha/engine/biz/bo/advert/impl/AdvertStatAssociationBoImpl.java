package cn.com.duiba.nezha.engine.biz.bo.advert.impl;

import cn.com.duiba.nezha.engine.api.enums.FlowTag;
import cn.com.duiba.nezha.engine.api.enums.StatDataTypeEnum;
import cn.com.duiba.nezha.engine.api.support.RecommendEngineException;
import cn.com.duiba.nezha.engine.biz.bo.advert.AdvertStatAssociationBo;
import cn.com.duiba.nezha.engine.biz.domain.AdvertStatDo;
import cn.com.duiba.nezha.engine.biz.domain.advert.OrientationPackage;
import cn.com.duiba.nezha.engine.biz.entity.nezha.advert.AdvertStatisticMergeEntity;
import cn.com.duiba.nezha.engine.biz.service.advert.ctr.AdvertStatService;
import cn.com.duiba.nezha.engine.common.utils.MapUtils;
import cn.com.duiba.nezha.engine.common.utils.Pair;
import cn.com.duiba.wolf.perf.timeprofile.DBTimeProfile;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

import static cn.com.duiba.nezha.engine.api.enums.StatDataTypeEnum.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: AdvertStatAssociationBoImpl.java , v 0.1 2017/12/14 下午5:18 ZhouFeng Exp $
 */
@Service
public class AdvertStatAssociationBoImpl implements AdvertStatAssociationBo {

    private static final ImmutableMap<StatDataTypeEnum, Function<AdvertStatDo, Double>> DATA_TYPE_FUNCTION_MAP =
            ImmutableMap.of(
                    CTR, (AdvertStatDo stat) -> Optional.ofNullable(stat.getCtr()).orElse(0D),
                    CVR, (AdvertStatDo stat) -> Optional.ofNullable(stat.getCvr()).orElse(0D),
                    CLICK, (AdvertStatDo stat) -> stat.getChargeCnt().doubleValue(),
                    EXPOSURE, (AdvertStatDo stat) -> stat.getLaunchCnt().doubleValue(),
                    FEE, (AdvertStatDo stat) -> stat.getChargeFees().doubleValue()
            );


    @Autowired
    private AdvertStatService advertStatService;


    @Override
    public Map<Long, AdvertStatDo> get7DayStat(Set<Long> appIds, Long advertId, Map<Long, Long> advertId2PkgId) {

        if (advertId2PkgId == null) {
            advertId2PkgId = Collections.emptyMap();
        }

        Set<AdvertStatService.Query> queries = new HashSet<>(appIds.size());
        Map<Long, AdvertStatService.Query> appId2QueryMap = new HashMap<>(appIds.size());
        for (Long appId : appIds) {
            AdvertStatService.Query query = new AdvertStatService.Query.Builder()
                    .appId(appId)
                    .advertId(advertId)
                    .packageId(advertId2PkgId.get(advertId))
                    .build();
            queries.add(query);
            appId2QueryMap.put(appId, query);
        }


        Map<AdvertStatService.Query, AdvertStatDo> r7DayStat = advertStatService.get7DayStat(queries);

        return MapUtils.translate(appId2QueryMap, r7DayStat);

    }

    @Override
    public Map<OrientationPackage, AdvertStatDo> getTodayAutoMatchStat(Collection<OrientationPackage> orientPackageList) {
        try {
            DBTimeProfile.enter("advertStatAssociationBo.getTodayAutoMatchStat");
            Map<OrientationPackage, AdvertStatService.Query> orientPackage2QueryMap = orientPackageList.stream()
                    .collect(toMap(Function.identity(), orientPackage -> new AdvertStatService.Query.Builder()
                            .advertId(orientPackage.getAdvertId())
                            .packageId(orientPackage.getId())
                            .tag(FlowTag.GOOD.getTag())
                            .build()));

            Map<AdvertStatService.Query, AdvertStatDo> currentDayStat = advertStatService.getCurrentDayStat(orientPackage2QueryMap.values());

            return MapUtils.translate(orientPackage2QueryMap, currentDayStat);
        } finally {
            DBTimeProfile.release();
        }
    }


    @Override
    public <R> Table<R, StatDataTypeEnum, AdvertStatisticMergeEntity> getMultiDimStat(Long appId,
                                                                                      Function<OrientationPackage, R> typeMapper,
                                                                                      Collection<OrientationPackage> orientPackageList,
                                                                                      Function<OrientationPackage, AdvertStatService.Query> globalMapper) {
        try {
            // 判断是否是请求定向配置包数据
            boolean isOrientPackage = typeMapper.apply(new OrientationPackage()) instanceof OrientationPackage;
            DBTimeProfile.enter("advertStatAssociationBo.getMultiDim" + (isOrientPackage ? "OrientPackage" : "Advert") + "Stat");

            Set<AdvertStatService.Query> queries = new HashSet<>(orientPackageList.size() * 2);
            Map<R, Pair<AdvertStatService.Query>> advert2QueryPairMap = new HashMap<>(orientPackageList.size());

            for (OrientationPackage orientationPackage : orientPackageList) {
                AdvertStatService.Query appQuery = globalMapper.apply(orientationPackage).setAppId(appId);
                AdvertStatService.Query globalQuery = globalMapper.apply(orientationPackage);
                queries.add(appQuery);
                queries.add(globalQuery);
                advert2QueryPairMap.put(typeMapper.apply(orientationPackage), Pair.of(appQuery, globalQuery));
            }

            //当前小时
            Map<AdvertStatService.Query, AdvertStatDo> currentHourStatMap = advertStatService.getCurrentHourStat(queries);

            //当天
            Map<AdvertStatService.Query, AdvertStatDo> currentDayStatMap = advertStatService.getCurrentDayStat(queries);

            //7天
            Map<AdvertStatService.Query, AdvertStatDo> r7DayStatMap = advertStatService.get7DayStat(queries);

            //合并
            Table<R, StatDataTypeEnum, AdvertStatisticMergeEntity> record = HashBasedTable.create(orientPackageList.size(), StatDataTypeEnum.values().length);

            for (OrientationPackage orientationPackage : orientPackageList) {

                R type = typeMapper.apply(orientationPackage);
                Pair<AdvertStatService.Query> queryPair = advert2QueryPairMap.get(type);

                for (Map.Entry<StatDataTypeEnum, Function<AdvertStatDo, Double>> entry : DATA_TYPE_FUNCTION_MAP.entrySet()) {

                    StatDataTypeEnum dataType = entry.getKey();
                    //获取当前数据类型的get方法
                    Function<AdvertStatDo, Double> getDataFunction = entry.getValue();

                    //当前小时
                    Double appCurrentlyHour = this.leftValue(queryPair, currentHourStatMap, getDataFunction);
                    Double globalCurrentlyHour = this.rightValue(queryPair, currentHourStatMap, getDataFunction);

                    //当天
                    Double appCurrentlyDay = this.leftValue(queryPair, currentDayStatMap, getDataFunction);
                    Double globalCurrentlyDay = this.rightValue(queryPair, currentDayStatMap, getDataFunction);

                    //七天
                    Double appRecently7Day = this.leftValue(queryPair, r7DayStatMap, getDataFunction);
                    Double globalRecently7Day = this.rightValue(queryPair, r7DayStatMap, getDataFunction);


                    AdvertStatisticMergeEntity entity = new AdvertStatisticMergeEntity.Builder()
                            .appCurrentlyHour(appCurrentlyHour)
                            .globalCurrentlyHour(globalCurrentlyHour)
                            .appCurrentlyDay(appCurrentlyDay)
                            .globalCurrentlyDay(globalCurrentlyDay)
                            .appRecently7Day(appRecently7Day)
                            .globalRecently7Day(globalRecently7Day)
                            .build();

                    record.put(type, dataType, entity);
                }
            }
            return record;
        } catch (Exception e) {
            throw new RecommendEngineException("getMultiDimStat error", e);
        } finally {
            DBTimeProfile.release();
        }


    }

    private Double leftValue(Pair<AdvertStatService.Query> queryPair, Map<AdvertStatService.Query, AdvertStatDo> statMap, Function<AdvertStatDo, Double> getDataFunction) {
        return queryPair.getLeft().map(statMap::get).map(getDataFunction).orElse(0D);
    }

    private Double rightValue(Pair<AdvertStatService.Query> queryPair, Map<AdvertStatService.Query, AdvertStatDo> statMap, Function<AdvertStatDo, Double> getDataFunction) {
        return queryPair.getRight().map(statMap::get).map(getDataFunction).orElse(0D);
    }

    @Override
    public Table<Long, StatDataTypeEnum, List<Double>> getTodayHourlyStat(Long appId, Collection<Long> advertIds, Map<Long,
            Long> advertId2PkgId) {
        try {
            // todo 可能有遗留bug
            DBTimeProfile.enter("advertStatAssociationBo.getTodayHourlyStat");
            Set<AdvertStatService.Query> queries = new HashSet<>(advertIds.size());
            for (Long advertId : advertIds) {
                AdvertStatService.Query query = new AdvertStatService.Query.Builder().appId(appId).advertId
                        (advertId).packageId(advertId2PkgId.get(advertId)).build();
                queries.add(query);
            }


            Map<AdvertStatService.Query, List<AdvertStatDo>> todayHourlyStatMap = advertStatService.getTodayHourlyStat
                    (queries);


            Table<Long, StatDataTypeEnum, List<Double>> todayHourlyStat = HashBasedTable.create(advertIds.size(),
                    StatDataTypeEnum.values().length);

            //按广告聚合数据
            for (AdvertStatService.Query query : queries) {

                List<AdvertStatDo> advertStatDos = todayHourlyStatMap.get(query);

                if (CollectionUtils.isEmpty(advertStatDos)) {
                    continue;
                }

                //按数据类型聚合数据
                for (StatDataTypeEnum dataType : StatDataTypeEnum.values()) {
                    Function<AdvertStatDo, Double> function = DATA_TYPE_FUNCTION_MAP.get(dataType);
                    if (function == null) {
                        continue;
                    }
                    List<Double> collect = advertStatDos.stream().filter(Objects::nonNull).map(function).collect(toList());
                    todayHourlyStat.put(query.getAdvertId(), dataType, collect);
                }

            }
            return todayHourlyStat;
        } catch (Exception e) {
            throw new RecommendEngineException("getTodayHourlyStat error", e);
        } finally {
            DBTimeProfile.release();
        }
    }
}




package cn.com.duiba.nezha.engine.biz.service.advert.merge;

import cn.com.duiba.nezha.alg.alg.alg.BackendCostOptAlg;
import cn.com.duiba.nezha.alg.alg.vo.BackendAdvertStatDo;
import cn.com.duiba.nezha.alg.alg.vo.BackendFactor;
import cn.com.duiba.nezha.alg.alg.vo.BudgetSmoothDo;
import cn.com.duiba.nezha.alg.alg.vo.StatDo;
import cn.com.duiba.nezha.compute.common.model.pacing.*;
import cn.com.duiba.nezha.compute.common.model.qscore.Qscore;
import cn.com.duiba.nezha.compute.common.model.qscore.QualityInfo;
import cn.com.duiba.nezha.engine.api.dto.FusingOrientationPackageDto;
import cn.com.duiba.nezha.engine.api.dto.RecommendAppDto;
import cn.com.duiba.nezha.engine.api.enums.RecommendType;
import cn.com.duiba.nezha.engine.api.enums.StatDataTypeEnum;
import cn.com.duiba.nezha.engine.biz.bo.advert.AdvertBackendBo;
import cn.com.duiba.nezha.engine.biz.bo.advert.AdvertStatAssociationBo;
import cn.com.duiba.nezha.engine.biz.constant.GlobalConstant;
import cn.com.duiba.nezha.engine.biz.domain.AdvertStatDo;
import cn.com.duiba.nezha.engine.biz.domain.FeatureIndex;
import cn.com.duiba.nezha.engine.biz.domain.advert.Advert;
import cn.com.duiba.nezha.engine.biz.domain.advert.Material;
import cn.com.duiba.nezha.engine.biz.domain.advert.OrientationPackage;
import cn.com.duiba.nezha.engine.biz.entity.nezha.advert.AdvertStatisticMergeEntity;
import cn.com.duiba.nezha.engine.biz.entity.nezha.advert.SlotAdvertInfo;
import cn.com.duiba.nezha.engine.biz.service.CacheService;
import cn.com.duiba.nezha.engine.biz.service.advert.ctr.AdvertStatService;
import cn.com.duiba.nezha.engine.biz.service.advert.ctr.TagStatAssociationService;
import cn.com.duiba.nezha.engine.biz.vo.advert.*;
import cn.com.duiba.nezha.engine.common.utils.FormatUtil;
import cn.com.duiba.nezha.engine.common.utils.RedisKeyUtil;
import cn.com.duiba.nezha.engine.common.utils.RoiHashKeyUtil;
import cn.com.duiba.wolf.perf.timeprofile.DBTimeProfile;
import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Table;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static cn.com.duiba.nezha.engine.api.enums.ChargeTypeEnum.CPA;
import static cn.com.duiba.nezha.engine.api.enums.StatDataTypeEnum.*;
import static cn.com.duiba.nezha.engine.biz.entity.nezha.advert.AdvertStatisticMergeEntity.DEFAULT;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

/**
 * Created by pc on 2016/10/18.
 */
@Service
public class AdvertDataMergeService extends CacheService {
    private static final Logger logger = LoggerFactory.getLogger(AdvertDataMergeService.class);

    @Autowired
    private AdvertStatAssociationBo advertStatAssociationBo;

    @Autowired
    private TagStatAssociationService tagStatAssociationService;

    @Autowired
    private AdvertStatService advertStatService;

    @Autowired
    private AdvertBackendBo advertBackendBo;

    @Resource
    private StringRedisTemplate nezhaStringRedisTemplate;

    private LoadingCache<String, Double> factorCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build(new CacheLoader<String, Double>() {
                @Override
                public Double load(String key) {
                    throw new UnsupportedOperationException("not support single query");
                }

                @Override
                public Map<String, Double> loadAll(Iterable<? extends String> keys) {

                    List<String> collect = StreamSupport.stream(keys.spliterator(), false).collect(toList());

                    Map<String, Double> factorMap = new HashMap<>(collect.size());

                    DBTimeProfile.enter("load factor " + collect.size());

                    List<String> values = nezhaStringRedisTemplate.opsForValue().multiGet(collect);

                    DBTimeProfile.release();

                    for (int index = 0; index < collect.size(); index++) {
                        String key = collect.get(index);
                        String value = values.get(index);

                        double factor = 1D;
                        if (StringUtils.isNotEmpty(value)) {
                            factor = Double.parseDouble(value);
                        }

                        factorMap.put(key, factor);
                    }

                    return factorMap;

                }
            });

    private LoadingCache<String, Integer> blackWhiteCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(new CacheLoader<String, Integer>() {
                @Override
                public Integer load(String key) {
                    throw new UnsupportedOperationException("not support single query");
                }

                @Override
                public Map<String, Integer> loadAll(Iterable<? extends String> keys) {

                    List<String> keyList = StreamSupport.stream(keys.spliterator(), false).collect(toList());

                    Map<String, Integer> blackWhiteMap = new HashMap<>(keyList.size());

                    DBTimeProfile.enter("load blackWhiteCache " + keyList.size());

                    List<String> values = nezhaStringRedisTemplate.opsForValue().multiGet(keyList);

                    DBTimeProfile.release();

                    convert(keyList, blackWhiteMap, values);

                    return blackWhiteMap;

                }
            });

    private LoadingCache<String, Integer> newBlackWhiteCache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit
            .MINUTES).build(new CacheLoader<String, Integer>() {
        @Override
        public Integer load(String key) {
            throw new UnsupportedOperationException("not support single query");
        }

        @Override
        public Map<String, Integer> loadAll(Iterable<? extends String> keys) {

            List<String> collect = StreamSupport.stream(keys.spliterator(), false).collect(toList());

            Map<String, Integer> blackWhiteMap = new HashMap<>(collect.size());

            DBTimeProfile.enter("load newBlackWhiteCache " + collect.size());

            List<String> values = nezhaStringRedisTemplate.opsForValue().multiGet(collect);

            DBTimeProfile.release();

            convert(collect, blackWhiteMap, values);

            return blackWhiteMap;

        }
    });

    private LoadingCache<String, TrusteeshipParams> trusteeshipParamsCache = CacheBuilder.newBuilder().refreshAfterWrite(10, TimeUnit.MINUTES)
            .build(new CacheLoader<String, TrusteeshipParams>() {
                @Override
                public TrusteeshipParams load(String key) throws Exception {
                    return Optional.ofNullable(nezhaStringRedisTemplate.opsForValue().get(RedisKeyUtil.getTrusteeshipParamsKey()))
                            .map(json -> JSON.parseObject(json, TrusteeshipParams.class))
                            .orElseGet(TrusteeshipParams::new);
                }

                @Override
                public ListenableFuture<TrusteeshipParams> reload(String key, TrusteeshipParams oldValue) throws Exception {
                    ListenableFutureTask<TrusteeshipParams> task = ListenableFutureTask.create(() -> load(key));
                    executorService.submit(task);
                    return task;
                }
            });

    private void convert(List<String> collect, Map<String, Integer> blackWhiteMap, List<String> values) {
        for (int index = 0; index < collect.size(); index++) {
            String key = collect.get(index);
            String value = values.get(index);

            Integer qualityLevel = Optional.ofNullable(value).map(v -> {
                switch (v) {
                    case "b1":
                        return 1;
                    case "b2":
                        return 2;
                    case "b3":
                        return 3;
                    case "w":
                        return 8;
                    default:
                        return 16;
                }
            }).orElse(0);
            blackWhiteMap.put(key, qualityLevel);
        }
    }

    private Map<StatDataTypeEnum, Map<Long, Double>> getMergeStatMap(AdvertRecommendRequestVo advertRecommendRequestVo,
                                                                     Map<Long, StatDo> advertAppStatMap,
                                                                     Map<Long, StatDo> advertGlobalStatMap) {


        Set<Long> advertIds = advertRecommendRequestVo.getAdvertMap().keySet();
        Long appId = advertRecommendRequestVo.getAppDo().getId();
        Map<Long, Long> timesMap = advertRecommendRequestVo.getTimesMap();


        Map<StatDataTypeEnum, Map<Long, Double>> map = new HashMap<>();
        Map<Long, String> statRedisKeyMap = new HashMap<>();

        Map<Long, Double> ctrMap = new HashMap<>();
        Map<Long, Double> cvrMap = new HashMap<>();

        RecommendType recommendType = advertRecommendRequestVo.getRecommendType();
        Double defaultCtr;
        Double defaultCvr;
        if (recommendType.equals(RecommendType.SHOW)) {
            defaultCtr = GlobalConstant.SHOW_DEFAULT_CTR;
            defaultCvr = GlobalConstant.SHOW_DEFAULT_CVR;
        } else {
            defaultCtr = GlobalConstant.INTERACT_DEFAULT_CTR;
            defaultCvr = GlobalConstant.INTERACT_DEFAULT_CVR;
        }

        advertIds.forEach(advertId -> {

            Optional<StatDo> appStat = Optional.ofNullable(advertAppStatMap.get(advertId));
            Optional<StatDo> globalStat = Optional.ofNullable(advertGlobalStatMap.get(advertId));

            // 默认全局
            ctrMap.put(advertId, globalStat.map(StatDo::getCtr).orElse(defaultCtr));
            cvrMap.put(advertId, globalStat.map(StatDo::getCvr).orElse(defaultCvr));

            final Long[] finalAppId = {null};

            // 如果媒体纬度存在.则以媒体纬度为准
            appStat.map(StatDo::getCtr).ifPresent(ctr -> {
                finalAppId[0] = appId;
                ctrMap.put(advertId, ctr);
                cvrMap.put(advertId, appStat.map(StatDo::getCvr).orElse(defaultCvr));
            });
            appStat.map(StatDo::getCvr).ifPresent(cvr -> {
                finalAppId[0] = appId;
                cvrMap.put(advertId, cvr);
                ctrMap.put(advertId, appStat.map(StatDo::getCtr).orElse(defaultCtr));
            });


            map.put(CTR, ctrMap);
            map.put(StatDataTypeEnum.CVR, cvrMap);

            statRedisKeyMap.put(advertId, RedisKeyUtil.advertMergeStatKey(finalAppId[0], advertId, null, timesMap.get(advertId)));
        });

        advertRecommendRequestVo.setStatRedisKeyMap(statRedisKeyMap);
        return map;

    }

    public List<AdvertResortVo> dataMerge(AdvertRecommendRequestVo advertRecommendRequestVo) {

        try {

            // 广告数据
            Map<Long, Advert> advertMap = advertRecommendRequestVo.getAdvertMap();
            Set<Long> advertIds = advertMap.keySet();
            Collection<Advert> adverts = advertMap.values();

            // 配置包数据
            Set<OrientationPackage> orientationPackages = advertRecommendRequestVo.getAdvertOrientationPackages();

            DBTimeProfile.enter("dataMerge orientationPackageSize:" + orientationPackages.size());

            // 统计数据权重对象
            AdvertStatDimWeightVo advertStatDimWeightVo = advertRecommendRequestVo.getAdvertStatDimWeightVo();
            double statCtrWeight = advertStatDimWeightVo.getStatCtrWeight();
            double preCtrWeight = advertStatDimWeightVo.getPreCtrWeight();
            double statCvrWeight = advertStatDimWeightVo.getStatCvrWeight();
            double preCvrWeight = advertStatDimWeightVo.getPreCvrWeight();

            // 基本请求参数
            Long appId = advertRecommendRequestVo.getAppDo().getId();
            Long slotId = advertRecommendRequestVo.getAppDo().getSlotId();
            Long activityId = advertRecommendRequestVo.getActivityDo().getOperatingId();

            List<StatDo> advertAppMergeStatDoList = advertRecommendRequestVo.getAdvertAppMergeStatDoList();
            List<StatDo> advertGlobalMergeStatDoList = advertRecommendRequestVo.getAdvertGlobalMergeStatDoList();
            Map<Long, StatDo> advertAppStatMap = advertAppMergeStatDoList.stream().collect(toMap(StatDo::getAdvertId, Function.identity()));
            Map<Long, StatDo> advertGlobalStatMap = advertGlobalMergeStatDoList.stream().collect(toMap(StatDo::getAdvertId, Function.identity()));

            // 获取统计ctr和cvr
            Map<StatDataTypeEnum, Map<Long, Double>> mergeStatMap = this.getMergeStatMap(advertRecommendRequestVo, advertAppStatMap, advertGlobalStatMap);
            Map<Long, Double> ctrStat = mergeStatMap.getOrDefault(CTR, new HashMap<>());
            Map<Long, Double> cvrStat = mergeStatMap.getOrDefault(CVR, new HashMap<>());

            // 获取调价因子
            Set<OrientationPackage> cpaOrientationPackages = orientationPackages.stream().filter(OrientationPackage::isCpa).collect(toSet());
            Map<OrientationPackage, Double> factorMap = this.loadFactors(cpaOrientationPackages, appId, slotId);

            Map<Long, Set<String>> advertMatchTags = adverts.stream().filter(advert -> StringUtils.isNotBlank(advert.getMatchTags()))
                    .collect(toMap(Advert::getId, advert -> Stream.of(advert.getMatchTags().split(",")).collect(toSet())));
            Set<String> allTags = advertMatchTags.values().stream().flatMap(Collection::stream).collect(toSet());

            // 获取每小时数据
            Table<Long, StatDataTypeEnum, List<Double>> todayHourlyStat = advertStatAssociationBo
                    .getTodayHourlyStat(appId, advertIds, new HashMap<>());

            //获取广告多维度分时分日数据
            Table<Long, StatDataTypeEnum, AdvertStatisticMergeEntity> multiDimAdvertStat = advertStatAssociationBo
                    .getMultiDimStat(appId,
                            OrientationPackage::getAdvertId,
                            orientationPackages,
                            orientationPackage -> new AdvertStatService.Query.Builder().advertId(orientationPackage.getAdvertId()).build());

            //获取配置多维度分时分日数据
            Table<OrientationPackage, StatDataTypeEnum, AdvertStatisticMergeEntity> multiDimPkgStat = advertStatAssociationBo
                    .getMultiDimStat(appId,
                            Function.identity(),
                            orientationPackages,
                            orientationPackage -> new AdvertStatService.Query.Builder().advertId(orientationPackage.getAdvertId()).packageId(orientationPackage.getId()).build());

            //获取标签多维度分时分日数据
            Map<String, AdvertStatisticMergeEntity> tagStat = tagStatAssociationService.getTagStat(appId, allTags);

            // 获取配置当日智能匹配数据
            Map<OrientationPackage, AdvertStatDo> todayAutoMatchStat = advertStatAssociationBo.getTodayAutoMatchStat(orientationPackages);

            // 根据是否强弱定向来分组
            Map<Boolean, List<OrientationPackage>> partByTargetType = orientationPackages.stream().collect(partitioningBy(OrientationPackage::getStrongTarget));
            List<OrientationPackage> strongTargetPackages = partByTargetType.getOrDefault(true, new ArrayList<>());
            List<OrientationPackage> weakTargetPackages = partByTargetType.getOrDefault(false, new ArrayList<>());


            // 获取自动托管推荐的广告.没推荐出来的走智能匹配
            Map<ResultType, Collection<AdvertOrientInfo>> trusteeshipRecommendResult = this.getTrusteeshipRecommendResult(weakTargetPackages, slotId, multiDimPkgStat);
            Map<AdvertOrientInfo, OrientationPackage> packageMap = weakTargetPackages
                    .stream()
                    .collect(toMap(orientationPackage -> new AdvertOrientInfo(orientationPackage.getAdvertId(), orientationPackage.getId()), Function.identity()));


            List<OrientationPackage> trusteeshipOnTargetPackages = trusteeshipRecommendResult
                    .getOrDefault(ResultType.ONTARGET, new ArrayList<>())
                    .stream()
                    .map(packageMap::get)
                    .collect(toList());

            List<OrientationPackage> trusteeshipGiveUpPackages = trusteeshipRecommendResult
                    .getOrDefault(ResultType.GIVEUP, new ArrayList<>())
                    .stream()
                    .map(packageMap::get)
                    .collect(toList());


            this.handlerTrusteeshipResult(appId, advertRecommendRequestVo, trusteeshipRecommendResult);

            Map<OrientationPackage, Integer> blackWhiteMap = this.loadBlackWhite(orientationPackages, appId, slotId, activityId);
            Map<Long, Integer> userAdvertBehaviorMap = advertRecommendRequestVo.getUserAdvertBehaviorMap();
            Map<Long, Double> ctrReconstructionFactorMap = advertRecommendRequestVo.getCtrReconstructionFactorMap();
            Map<Long, Double> cvrReconstructionFactorMap = advertRecommendRequestVo.getCvrReconstructionFactorMap();
            Map<FeatureIndex, Double> predictCtr = advertRecommendRequestVo.getPredictCtr();
            Map<FeatureIndex, Double> predictCvr = advertRecommendRequestVo.getPredictCvr();

            Collection<OrientationPackage> allPackage = new ArrayList<>(strongTargetPackages);
            allPackage.addAll(trusteeshipOnTargetPackages);
            allPackage.addAll(trusteeshipGiveUpPackages);

            DBTimeProfile.enter("pacing");
            Set<AdvertResortVo> advertResortVos = allPackage
                    .stream()
                    .map(orientationPackage -> {
                        Long advertId = orientationPackage.getAdvertId();
                        Advert advert = advertMap.get(advertId);
                        Long packageId = orientationPackage.getId();

                        Optional<Material> materialOptional = orientationPackage.getMaterials().stream().findAny();
                        FeatureIndex featureIndex = materialOptional
                                .map(material -> new FeatureIndex(advertId, material.getId()))
                                .orElse(new FeatureIndex(advertId));

                        Boolean smartShop = trusteeshipGiveUpPackages.contains(orientationPackage);

                        Boolean newAddAdStatus = this.isNewAdvert(advertId, advertGlobalStatMap);
                        Double statDimMatchWeight = 1.0;

                        Double statCtr = ctrStat.getOrDefault(advertId, 0.0);
                        Double preCtr = predictCtr.getOrDefault(featureIndex, 0.0);
                        Double ctr = this.getAdvertCtr(statCtr, statDimMatchWeight, statCtrWeight, preCtr, preCtrWeight, newAddAdStatus);

                        Double statCvr = cvrStat.getOrDefault(advertId, 0.0);
                        Double preCvr = predictCvr.getOrDefault(featureIndex, 0.0);
                        Double cvr = this.getAdvertCvr(statCvr, statDimMatchWeight, statCvrWeight, preCvr, preCvrWeight, newAddAdStatus);

                        Double factor = factorMap.getOrDefault(orientationPackage, 1D);
                        Long targetFee = orientationPackage.getFee();
                        Long finalFee = this.calculateFinalFee(orientationPackage.isCpa(), factor, cvr, targetFee);

                        Map<StatDataTypeEnum, AdvertStatisticMergeEntity> multiDimAdvertStatMap = multiDimAdvertStat.rowMap().getOrDefault(advertId, new HashMap<>());
                        Map<StatDataTypeEnum, AdvertStatisticMergeEntity> multiDimPkgStatMap = multiDimPkgStat.rowMap().getOrDefault(orientationPackage, new HashMap<>());
                        Map<StatDataTypeEnum, List<Double>> hourlyStatMap = todayHourlyStat.rowMap().getOrDefault(advertId, new HashMap<>());

                        AdvertStatisticMergeEntity tagMerge = this.mergeTagStat(tagStat, advertMatchTags.get(advertId));
                        Integer userBehavior = userAdvertBehaviorMap.getOrDefault(advertId, 0);
                        Integer qualityLevel = blackWhiteMap.get(orientationPackage);
                        AdvertStatDo advertAutoMatchStatDo = todayAutoMatchStat.get(orientationPackage);
                        Double tagWeight = Optional.ofNullable(advert.getWeight()).orElse(GlobalConstant.DEFAULT_ADVERT_WEIGHT);
                        double ctrReconstructionFactor = ctrReconstructionFactorMap.getOrDefault(advertId, 1D);
                        double cvrReconstructionFactor = cvrReconstructionFactorMap.getOrDefault(advertId, 1D);
                        PacingVo pacingVo = PacingVo.newBuilder()
                                .advertId(advertId)
                                .packageId(packageId)
                                .smartShop(smartShop)
                                .orientationPackage(orientationPackage)
                                .appId(appId)
                                .ctr(ctr)
                                .cvr(cvr)
                                .factor(factor)
                                .finalFee(finalFee)
                                .targetFee(targetFee)
                                .hourlyStatDataMap(hourlyStatMap)
                                .advertStatDataMap(multiDimAdvertStatMap)
                                .orientPackageStatDataMap(multiDimPkgStatMap)
                                .tagStatData(tagMerge)
                                .userBehavior(userBehavior)
                                .qualityLevel(qualityLevel)
                                .autoMatchStatDo(advertAutoMatchStatDo)
                                .build();

                        PacingResult pacingResult = this.pacing(pacingVo);

                        Boolean giveUp = pacingResult.getGiveUp();
                        Long tag = pacingResult.getTag();
                        AdvertResortVo advertResortVo = new AdvertResortVo();
                        advertResortVo.setAccountId(advert.getAccountId());
                        advertResortVo.setAdvertId(advertId);
                        advertResortVo.setPackageId(packageId);
                        advertResortVo.setChargeType(orientationPackage.getChargeType());
                        advertResortVo.setCtr(ctr);
                        advertResortVo.setQualityLevel(qualityLevel);
                        advertResortVo.setNewStatus(newAddAdStatus);
                        advertResortVo.setStatCtr(statCtr);
                        advertResortVo.setPreCtr(preCtr);
                        advertResortVo.setWeight(advert.getWeight());
                        advertResortVo.setCvr(cvr);
                        advertResortVo.setStatCvr(statCvr);
                        advertResortVo.setPreCvr(preCvr);
                        advertResortVo.setFinalFee(finalFee);
                        advertResortVo.setOriginalFee(targetFee);
                        advertResortVo.setGiveUp(giveUp);
                        advertResortVo.setTag(tag);
                        advertResortVo.setTagWeight(tagWeight);
                        advertResortVo.setDiscountRate(advert.getDiscountRate());
                        advertResortVo.setCtrReconstructionFactor(ctrReconstructionFactor);
                        advertResortVo.setCvrReconstructionFactor(cvrReconstructionFactor);
                        materialOptional.map(Material::getId).ifPresent(advertResortVo::setMaterialId);
                        advertResortVo.setFactor(factor);
                        advertResortVo.setLaunchCountToUser(advert.getLaunchCountToUser());
                        advertResortVo.setBackupAdvertIds(advert.getBackupAdvertIds());
                        return advertResortVo;
                    }).collect(toSet());

            DBTimeProfile.release();

            // 获取后端消耗速率
            Map<Long, Double> predictedBackendCvr = advertRecommendRequestVo.getPredictBackendCvr();
            Map<OrientationPackage, BackendFactor> backendFactorMap = new HashMap<>();
            Map<OrientationPackage, BackendAdvertStatDo> orientationPackageBackendStatInfo = new HashMap<>();
            Map<Long, BackendAdvertStatDo> backendAdvertStatInfo = advertRecommendRequestVo.getBackendAdvertStatMap();
            if (!predictCvr.isEmpty()) {
                Map<OrientationPackage, Double> orientationPackagePredictedBackendCvr = new HashMap<>();
                Map<OrientationPackage, BudgetSmoothDo> advertBudgetInfo = advertBackendBo.getBudgetSmooth(appId, advertResortVos);

                orientationPackages.forEach(orientationPackage -> {
                    Long advertId = orientationPackage.getAdvertId();
                    Double backendCvrValue = predictedBackendCvr.getOrDefault(advertId, 0D);
                    orientationPackagePredictedBackendCvr.put(orientationPackage, backendCvrValue);

                    Optional.ofNullable(backendAdvertStatInfo.get(advertId))
                            .ifPresent(backendAdvertStatDo ->
                                    orientationPackageBackendStatInfo.put(orientationPackage, backendAdvertStatDo));
                });
                // 后端转化因子
                backendFactorMap = BackendCostOptAlg
                        .getBackendAdvertInfo(orientationPackagePredictedBackendCvr,
                                orientationPackageBackendStatInfo,
                                advertBudgetInfo);
            }

            Boolean advertMultiDimScoreEffective = advertRecommendRequestVo.getAdvertMultiDimScoreEffective();

            // 计算rankScore
            DBTimeProfile.enter("calculate rankScore");
            Map<OrientationPackage, BackendFactor> finalBackendFactorMap = backendFactorMap;
            return advertResortVos
                    .stream()
                    .peek(advertResortVo -> {
                        Long advertId = advertResortVo.getAdvertId();
                        double ctr = advertResortVo.getCtr();
                        double cvr = advertResortVo.getCvr();
                        long finalFee = advertResortVo.getFinalFee();
                        Double tagWeight = advertResortVo.getTagWeight();
                        Integer chargeType = advertResortVo.getChargeType();
                        Double discountRate = advertResortVo.getDiscountRate();
                        Double ctrReconstructionFactor = advertResortVo.getCtrReconstructionFactor();
                        Double cvrReconstructionFactor = advertResortVo.getCvrReconstructionFactor();
                        double qScore = this.getScore(finalFee, chargeType, ctr, cvr, advertMultiDimScoreEffective);

                        double rankScore = this.getRankScore(ctrReconstructionFactor,
                                cvrReconstructionFactor,
                                finalFee,
                                tagWeight,
                                qScore,
                                discountRate);

                        OrientationPackage orientationPackage = new OrientationPackage();
                        orientationPackage.setAdvertId(advertResortVo.getAdvertId());
                        orientationPackage.setId(advertResortVo.getPackageId());

                        Optional<BackendFactor> optionalBackendFactor = Optional.ofNullable(finalBackendFactorMap.get(orientationPackage));
                        Double backendFactor = optionalBackendFactor.map(BackendFactor::getBackendFactor).orElse(1D);
                        if (chargeType.equals(CPA.getValue())) {
                            rankScore *= backendFactor;
                        }
                        Optional.ofNullable(backendAdvertStatInfo.get(advertId)).map(BackendAdvertStatDo::getBackendType)
                                .ifPresent(advertResortVo::setBackendType);
                        advertResortVo.setBackendFactor(backendFactor);
                        optionalBackendFactor.map(BackendFactor::getBudgetType).ifPresent(advertResortVo::setBudgetType);
                        optionalBackendFactor.map(BackendFactor::getBudgetRatio).ifPresent(advertResortVo::setBudgetRatio);
                        advertResortVo.setqScore(qScore);
                        advertResortVo.setPreBackendCvr(predictedBackendCvr.getOrDefault(advertId, 0D));
                        advertResortVo.setRankScore(rankScore);
                    })
                    .collect(groupingBy(AdvertResortVo::getAdvertId, maxBy(comparing(AdvertResortVo::getRankScore))))
                    .values()
                    .stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(toList());

        } finally {
            DBTimeProfile.release();
            DBTimeProfile.release();
        }
    }

    private void handlerTrusteeshipResult(Long appId, AdvertRecommendRequestVo advertRecommendRequestVo, Map<ResultType, Collection<AdvertOrientInfo>> trusteeshipRecommendResult) {
        Collection<AdvertOrientInfo> fuse1Package = trusteeshipRecommendResult.getOrDefault(ResultType.FUSE1, new ArrayList<>());
        Collection<AdvertOrientInfo> fuse2Package = trusteeshipRecommendResult.getOrDefault(ResultType.FUSE2, new ArrayList<>());

        Set<FusingOrientationPackageDto> fuse1PackageDto = fuse1Package.stream().map(advertOrientInfo -> {
            FusingOrientationPackageDto fusingOrientationPackageDto = new FusingOrientationPackageDto();
            fusingOrientationPackageDto.setType(2);
            fusingOrientationPackageDto.setId(advertOrientInfo.getOrientId());
            fusingOrientationPackageDto.setAdvertId(advertOrientInfo.getAdvertId());
            return fusingOrientationPackageDto;
        }).collect(toSet());
        Set<FusingOrientationPackageDto> fuse2PackageDto = fuse2Package.stream().map(advertOrientInfo -> {
            FusingOrientationPackageDto fusingOrientationPackageDto = new FusingOrientationPackageDto();
            fusingOrientationPackageDto.setType(1);
            fusingOrientationPackageDto.setId(advertOrientInfo.getOrientId());
            fusingOrientationPackageDto.setAdvertId(advertOrientInfo.getAdvertId());
            return fusingOrientationPackageDto;
        }).collect(toSet());

        fuse1PackageDto.addAll(fuse2PackageDto);
        advertRecommendRequestVo.setFusingOrientationPackages(fuse1PackageDto);


        Collection<AdvertOrientInfo> recommendTargetApp = trusteeshipRecommendResult.getOrDefault(ResultType.ORIENT, new ArrayList<>());
        Collection<AdvertOrientInfo> recommendLimitApp = trusteeshipRecommendResult.getOrDefault(ResultType.SHIELD, new ArrayList<>());

        Set<RecommendAppDto> recommendTargetAppDto = recommendTargetApp.stream().map(advertOrientInfo -> {
            RecommendAppDto recommendAppDto = new RecommendAppDto();
            recommendAppDto.setAdvertId(advertOrientInfo.getAdvertId());
            recommendAppDto.setAppId(appId);
            recommendAppDto.setPackageId(advertOrientInfo.getOrientId());
            recommendAppDto.setBias(advertOrientInfo.getCostConvertBias());
            recommendAppDto.setType(1);
            return recommendAppDto;
        }).collect(toSet());
        Set<RecommendAppDto> recommendLimitAppDto = recommendLimitApp.stream().map(advertOrientInfo -> {
            RecommendAppDto recommendAppDto = new RecommendAppDto();
            recommendAppDto.setAdvertId(advertOrientInfo.getAdvertId());
            recommendAppDto.setAppId(appId);
            recommendAppDto.setPackageId(advertOrientInfo.getOrientId());
            recommendAppDto.setBias(advertOrientInfo.getCostConvertBias());
            recommendAppDto.setType(2);
            return recommendAppDto;
        }).collect(toSet());
        recommendTargetAppDto.addAll(recommendLimitAppDto);

        advertRecommendRequestVo.setRecommendApps(recommendTargetAppDto);
    }

    private Long calculateFinalFee(Boolean cpa, Double factor, double cvr, Long fee) {
        if (!cpa) {
            return fee;
        }

        Long finalFee = Math.round(fee * cvr * factor);
        // 设定保底值1分
        if (finalFee.equals(0L)) {
            finalFee = 1L;
        }
        return finalFee;
    }

    private Map<ResultType, Collection<AdvertOrientInfo>> getTrusteeshipRecommendResult(Collection<OrientationPackage> weakTargetOrientationPackages,
                                                                                        Long slotId,
                                                                                        Table<OrientationPackage, StatDataTypeEnum, AdvertStatisticMergeEntity> multiDimPkgStat) {

        try {
            DBTimeProfile.enter("handleTrusteeshipAdvert");

            Set<Long> trusteeshipAdvertIds = weakTargetOrientationPackages.stream().map(OrientationPackage::getAdvertId).collect(toSet());

            // 获取自动托管参数
            TrusteeshipParams trusteeshipParams = trusteeshipParamsCache.getUnchecked(RedisKeyUtil.getTrusteeshipParamsKey());

            // 获取弱定向配置包的配置数据(熔断之后会重置)
            Map<OrientationPackage, AdvertStatDo> packageAdvertStatDoMap = advertStatService.getPackageData(weakTargetOrientationPackages);

            // 获取候选集
            Map<Long, SlotAdvertInfo> advertSlotInfoMap = advertStatService.getSlotAdvertInfo(trusteeshipAdvertIds, slotId);

            // 自动托管配置在广告位上的数据
            Map<OrientationPackage, AdvertStatDo> orientationSlotDataMap = advertStatService.getSlotPackageData(weakTargetOrientationPackages, slotId);

            List<OrientInfo> trusteeshipOrientInfoList = weakTargetOrientationPackages.stream().map(orientationPackage -> {
                Long advertId = orientationPackage.getAdvertId();
                Long packageId = orientationPackage.getId();

                Map<StatDataTypeEnum, AdvertStatisticMergeEntity> packageData = multiDimPkgStat.rowMap()
                        .getOrDefault(orientationPackage, new HashMap<>());
                AdvertStatisticMergeEntity cvrData = packageData.getOrDefault(CVR, DEFAULT);
                AdvertStatisticMergeEntity costData = packageData.getOrDefault(FEE, DEFAULT);

                SlotAdvertInfo slotAdvertInfo = advertSlotInfoMap.get(advertId);
                List<Double> biasSet = slotAdvertInfo.getBiasSet();
                List<Double> confidenceSet = slotAdvertInfo.getConfidenceSet();
                List<Double> cvrSet = slotAdvertInfo.getCvrSet();
                List<Double> priceSection = slotAdvertInfo.getPriceSection();
                AdvertStatDo packageStatDo = packageAdvertStatDoMap.get(orientationPackage);

                OrientInfo orientInfo = new OrientInfo();
                orientInfo.setAdvertId(advertId);
                orientInfo.setOrientId(packageId);
                orientInfo.setPriceSection(priceSection);
                orientInfo.setCvrSet(cvrSet);
                orientInfo.setConfidenceSet(confidenceSet);
                orientInfo.setBiasSet(biasSet);
                orientInfo.setManagered(true);
                orientInfo.setManageType(orientationPackage.getTargetAppLimit());
                orientInfo.setChargeType(orientationPackage.getChargeType());
                orientInfo.setTarget(orientationPackage.getConvertCost());
                orientInfo.setFee(orientationPackage.getFee());

                orientInfo.setOrientCostG1d(packageStatDo.getChargeFees().doubleValue());
                orientInfo.setOrientConvertG1d(packageStatDo.getActClickCnt().doubleValue());

                Optional.ofNullable(costData).map(AdvertStatisticMergeEntity::getGlobalRecently7Day).ifPresent(orientInfo::setOrientCostG7d);
                Optional.ofNullable(cvrData).map(AdvertStatisticMergeEntity::getAppCurrentlyDay).ifPresent(orientInfo::setAppOrientCvrDay);

                // 设置配置包在广告位上的消耗信息
                Optional.ofNullable(orientationSlotDataMap.get(orientationPackage)).ifPresent(slotPackageData -> {
                    orientInfo.setSlotOrientationConvert(slotPackageData.getActClickCnt().doubleValue());
                    orientInfo.setSlotOrientationCost(slotPackageData.getChargeFees().doubleValue());
                });

                return orientInfo;
            }).collect(toList());

            return SlotRecommender.recommend(trusteeshipOrientInfoList, slotId, trusteeshipParams);
        } finally {
            DBTimeProfile.release();
        }
    }

    private double getRankScore(double ctrReconstructionFactor,
                                double cvrReconstructionFactor,
                                Long fee,
                                double tagWeight,
                                double qScore,
                                double discountRate) {

        return fee * qScore * tagWeight * discountRate * ctrReconstructionFactor * cvrReconstructionFactor;
    }


    /**
     * 判断是否需要放弃流量
     */
    private PacingResult pacing(PacingVo pacingVo) {

        try {
            Long advertId = pacingVo.getAdvertId();
            Long packageId = pacingVo.getPackageId();
            OrientationPackage orientationPackage = pacingVo.getOrientationPackage();
            Integer chargeType = orientationPackage.getChargeType();

            Map<StatDataTypeEnum, AdvertStatisticMergeEntity> multiDimAdvertStat = pacingVo.getAdvertStatDataMap();
            Map<StatDataTypeEnum, AdvertStatisticMergeEntity> multiDimPkgStat = pacingVo.getOrientPackageStatDataMap();
            Map<StatDataTypeEnum, List<Double>> hourlyStatMap = pacingVo.getHourlyStatDataMap();
            AdvertStatisticMergeEntity tagStat = pacingVo.getTagStatData();

            AdvertStatisticMergeEntity adCtrMergeEntity = multiDimAdvertStat.getOrDefault(CTR, DEFAULT);
            AdvertStatisticMergeEntity adCvrMergeEntity = multiDimAdvertStat.getOrDefault(CVR, DEFAULT);
            AdvertStatisticMergeEntity adClickMergeEntity = multiDimAdvertStat.getOrDefault(CLICK, DEFAULT);

            AdvertStatisticMergeEntity pgkCtrMergeEntity = multiDimPkgStat.getOrDefault(CTR, DEFAULT);
            AdvertStatisticMergeEntity pgkCvrMergeEntity = multiDimPkgStat.getOrDefault(CVR, DEFAULT);
            AdvertStatisticMergeEntity pkgClickMergeEntity = multiDimPkgStat.getOrDefault(CLICK, DEFAULT);
            AdvertStatisticMergeEntity pkgCostMergeEntity = multiDimPkgStat.getOrDefault(FEE, DEFAULT);

            // 每小时
            TimeInfo timeInfo = new TimeInfo();
            timeInfo.setHourBudgetFee(orientationPackage.getHourlyBudgetFees());
            timeInfo.setHourBudgetExp(orientationPackage.getHourlyBudgetCounts());
            timeInfo.setPackageBudget(Optional.ofNullable(orientationPackage.getBudget()).map(Long::doubleValue).orElse(-1D));
            timeInfo.setHourCtr(hourlyStatMap.getOrDefault(CTR, Collections.emptyList()));
            timeInfo.setHourCvr(hourlyStatMap.getOrDefault(CVR, Collections.emptyList()));
            timeInfo.setHourClk(hourlyStatMap.getOrDefault(CLICK, Collections.emptyList()));
            timeInfo.setHourExp(hourlyStatMap.getOrDefault(EXPOSURE, Collections.emptyList()));
            timeInfo.setHourFee(hourlyStatMap.getOrDefault(FEE, Collections.emptyList()));

            // CTR
            CtrInfo ctrInfo = new CtrInfo();
            ctrInfo.setCtr(pacingVo.getCtr());

            // CVR
            CvrInfo cvrInfo = new CvrInfo();
            cvrInfo.setCvr(pacingVo.getCvr());

            //广告维度数据
            StatInfo adCtrStatInfo = this.getStatInfo(adCtrMergeEntity);
            ctrInfo.setAdCtrInfo(adCtrStatInfo);

            //广告维度cvr
            StatInfo adCvrStatInfo = this.getStatInfo(adCvrMergeEntity);
            cvrInfo.setAdCvrInfo(adCvrStatInfo);

            //配置包维度数据
            StatInfo pkgCtrStatInfo = this.getStatInfo(pgkCtrMergeEntity);
            ctrInfo.setOrientCtrInfo(pkgCtrStatInfo);

            //配置包维度cvr
            StatInfo pkgCvrStatInfo = this.getStatInfo(pgkCvrMergeEntity);
            cvrInfo.setOrientCvrInfo(pkgCvrStatInfo);

            //同行业标签下cvr
            StatInfo competerCvrInfo = this.getStatInfo(tagStat);
            cvrInfo.setCompeterCvrInfo(competerCvrInfo);

            //广告维度点击
            StatInfo adClickStatInfo = this.getStatInfo(adClickMergeEntity);

            //配置包维度点击
            StatInfo pkgClickStatInfo = this.getStatInfo(pkgClickMergeEntity);

            OrientInfo orientInfo = new OrientInfo();
            orientInfo.setAdvertId(advertId);
            orientInfo.setOrientId(packageId);
            orientInfo.setManagered(false); // todo
            orientInfo.setChargeType(chargeType);
            orientInfo.setTarget(orientationPackage.getConvertCost()); // todo
            orientInfo.setFee(pacingVo.getFinalFee());
            orientInfo.setOrientCostG1d(0.0D);
            orientInfo.setAppId(pacingVo.getAppId());
            orientInfo.setFactor(pacingVo.getFactor());
            orientInfo.setQuailityLevel(pacingVo.getQualityLevel());

            OrientInfo.Type type = orientInfo.new Type();
            type.setChargeType(chargeType);
            int pid = 0;
            if (pacingVo.getSmartShop()) {
                pid |= 0b100;
            }
            type.setPid(pid);
            type.setPackageType(pacingVo.getUserBehavior());
            orientInfo.setType(type);


            AutoMatchInfo autoMatchInfo = new AutoMatchInfo();
            Optional<AdvertStatDo> optionalStatDo = Optional.ofNullable(pacingVo.getAutoMatchStatDo());
            Long convert = optionalStatDo.map(AdvertStatDo::getActClickCnt).orElse(0L);
            Long cost = optionalStatDo.map(AdvertStatDo::getChargeFees).orElse(0L);
            Long launch = optionalStatDo.map(AdvertStatDo::getLaunchCnt).orElse(0L);
            Long click = optionalStatDo.map(AdvertStatDo::getChargeCnt).orElse(0L);
            autoMatchInfo.setConvertCost(orientationPackage.getConvertCost());
            autoMatchInfo.setConvert(convert);
            autoMatchInfo.setClick(click);
            autoMatchInfo.setCost(cost);
            autoMatchInfo.setLaunch(launch);
            orientInfo.setAutoMatchInfo(autoMatchInfo);

            // 设置广告配置在全局的消耗
            Optional.ofNullable(pkgCostMergeEntity.getGlobalCurrentlyDay()).ifPresent(orientInfo::setOrientCostG1d);

            boolean needGiveUp = new Pacing().pacing(orientInfo, cvrInfo, ctrInfo, adClickStatInfo, pkgClickStatInfo, timeInfo);
            PacingResult pacingResult = PacingResult.newBuilder().giveUp(needGiveUp).tag(0L).build();

            Optional.ofNullable(orientInfo.getAutoMatchInfo()).map(AutoMatchInfo::getEffectLog)
                    .map(AutoMatchInfo.EffectLog::getTag)
                    .ifPresent(pacingResult::setTag);

            return pacingResult;
        } catch (Exception e) {
            logger.warn("pacing error:{}", e);
            return PacingResult.DEFAULT;
        }

    }

    /**
     * 融合CTR
     * 综合CTR = （统计CTR*统计CTR权重 + 预估CTR*预估CTR权重）,注释：权重和为1
     * 统计CTR = 统计维度系数*统计维度CTR
     *
     * @param statCtr            统计维度CTR
     * @param statDImMatchWeight 统计维度系数
     * @param statCtrWeight      统计CTR权重
     * @param preCtr             预估CTR
     * @param preCtrWeight       预估CTR权重
     */
    private Double getAdvertCtr(double statCtr,
                                double statDImMatchWeight,
                                double statCtrWeight,
                                double preCtr,
                                double preCtrWeight,
                                boolean newAddAdStatus) {
        double ret;
        if (preCtr < 0.0001) {
            ret = statCtr * statDImMatchWeight;
        } else {
            if (newAddAdStatus) {
                ret = 0.5 * preCtr + 0.5 * statCtr;
            } else {
                ret = (statCtrWeight * (statCtr * statDImMatchWeight) +
                        preCtrWeight * preCtr) / (statCtrWeight + preCtrWeight);
            }
        }
        return FormatUtil.doubleFormat(ret, 6);
    }


    /**
     * 融合CVR
     * 综合CVR = （统计CVR*统计CVR权重 + 预估CVR*预估CVR权重）,注释：权重和为1
     * 统计CVR = 统计维度系数*统计维度CVR
     *
     * @param statCvr            统计维度CVR
     * @param statDImMatchWeight 统计维度系数
     * @param statCvrWeight      统计CVR权重
     * @param preCvr             预估CVR
     * @param preCvrWeight       预估CVR权重
     */
    private Double getAdvertCvr(double statCvr,
                                double statDImMatchWeight,
                                double statCvrWeight,
                                double preCvr,
                                double preCvrWeight,
                                boolean newAddAdStatus) {
        double ret;
        if (preCvr < 0.0001) {
            ret = statCvr * statDImMatchWeight;
        } else {
            if (newAddAdStatus) {
                ret = 0.5 * preCvr + 0.5 * statCvr;
            } else {
                ret = (statCvrWeight * (statCvr * statDImMatchWeight) +
                        preCvrWeight * preCvr) / (statCvrWeight + preCvrWeight);
                ret = Math.min(ret, statCvr * 1.5);
            }
        }
        return FormatUtil.doubleFormat(ret, 6);
    }

    /**
     * 加载缓存中不存在的调价因子
     *
     * @param orientationPackages CPA广告列表
     */
    private Map<OrientationPackage, Double> loadFactors(Set<OrientationPackage> orientationPackages, Long appId, Long slotId) {

        try {

            DBTimeProfile.enter("loadFactors");

            //加载所有需要的调价因子

            List<String> keys = new ArrayList<>(orientationPackages.size() * 3);

            Map<OrientationPackage, List<String>> advert2dimKeysMap = new HashMap<>(orientationPackages.size());

            for (OrientationPackage orientationPackage : orientationPackages) {
                Long advertId = orientationPackage.getAdvertId();
                Long packageId = orientationPackage.getId();

                String slotKey = RedisKeyUtil.factorKey(advertId, packageId, RoiHashKeyUtil.getSlotKey(slotId));
                String appKey = RedisKeyUtil.factorKey(advertId, packageId, RoiHashKeyUtil.getAppKey(appId));
                String defaultKey = RedisKeyUtil.factorKey(advertId, packageId, RoiHashKeyUtil.getDefault());

                List<String> dimKeys = Arrays.asList(slotKey, appKey, defaultKey);

                keys.addAll(dimKeys);
                advert2dimKeysMap.put(orientationPackage, dimKeys);
            }
            Map<String, Double> factorMap = new HashMap<>(0);
            try {
                factorMap = factorCache.getAll(keys);
            } catch (Exception e) {
                logger.error("load factor error :{}", e);
            }

            Map<OrientationPackage, Double> advert2FactorMap = new HashMap<>(orientationPackages.size());

            //根据广告提取存在且最细粒度的调价因子
            for (OrientationPackage orientationPackage : orientationPackages) {

                //先设置默认值，在没有合适调价因子的情况下使用默认值
                advert2FactorMap.put(orientationPackage, 1D);

                List<String> dimKeys = advert2dimKeysMap.get(orientationPackage);
                for (String dimKey : dimKeys) {
                    //dimkey 粒度依次增大，故排在前面的维度优先使用
                    Double factor = factorMap.get(dimKey);
                    if (checkFactor(factor)) {
                        //找到合适的调价因子后，直接结束查找
                        advert2FactorMap.put(orientationPackage, factor);
                        break;
                    }
                }

            }

            return advert2FactorMap;

        } finally {
            DBTimeProfile.release();
        }


    }

    /**
     * 加载缓存中不存在的调价因子
     *
     * @param adverts CPA广告列表
     */
    private Map<OrientationPackage, Integer> loadBlackWhite(Set<OrientationPackage> adverts, Long appId, Long slotId, Long activityId) {

        try {

            DBTimeProfile.enter("loadBlackWhite");

            // 最大纬度的key
            Map<OrientationPackage, String> advert2KeysMap = adverts.stream().collect(toMap(Function.identity(),
                    advert -> RedisKeyUtil.getBlackWhiteKey(appId, slotId, activityId,
                            advert.getId(), advert.getAdvertId())));

            // 广告配置包的key
            Map<OrientationPackage, String> advert2NewKeysMap = adverts.stream().collect(toMap(Function.identity(),
                    advert -> RedisKeyUtil.getNewBlackWhiteKey(advert.getAdvertId(), advert.getId())));

            //加载所有需要的调价因子
            Collection<String> keys = advert2KeysMap.values();
            Collection<String> newKeys = advert2NewKeysMap.values();

            Map<String, Integer> blackWhiteMap = new HashMap<>(keys.size());
            Map<String, Integer> newBlackWhiteMap = new HashMap<>(keys.size());
            try {
                blackWhiteMap = blackWhiteCache.getAll(keys);
                newBlackWhiteMap = newBlackWhiteCache.getAll(newKeys);
            } catch (Exception e) {
                logger.error("load blackWhite error :{}", e);
            }


            Map<String, Integer> finalBlackWhiteMap = blackWhiteMap;
            Map<String, Integer> finalNewBlackWhiteMap = newBlackWhiteMap;

            Map<OrientationPackage, Integer> advertQualityLevelMap = advert2KeysMap.entrySet().stream()
                    .collect(toMap(Map.Entry::getKey, entry -> finalBlackWhiteMap.getOrDefault(entry.getValue(), 0)));

            Map<OrientationPackage, Integer> newAdvertQualityLevelMap = advert2NewKeysMap.entrySet().stream()
                    .collect(toMap(Map.Entry::getKey, entry -> finalNewBlackWhiteMap.get(entry.getValue())));

            // 如果新的黑白名单有值,则替换老的map中的值
            advertQualityLevelMap.forEach((advertId, qualityLevel) ->
                    Optional.ofNullable(newAdvertQualityLevelMap.get(advertId))
                            .ifPresent(newQualityLevel -> advertQualityLevelMap.put(advertId, newQualityLevel)));

            return advertQualityLevelMap;
        } finally {
            DBTimeProfile.release();
        }


    }

    private boolean checkFactor(Double value) {
        return value != null && !value.isNaN() && !value.equals(1D);
    }

    private double getScore(Long finalFee, Integer chargeType, Double ctr, Double cvr, Boolean advertMultiDimScoreEffective) {

        double score = ctr;

        // 如果不为true（包括false和null）,则返回ctr
        if (!BooleanUtils.isTrue(advertMultiDimScoreEffective)) {
            return score;
        }

        try {
            Qscore qscore = new Qscore();
            QualityInfo qualityInfo = new QualityInfo();
            qualityInfo.setType(chargeType);
            qualityInfo.setCtr(ctr);
            qualityInfo.setCvr(cvr);
            qualityInfo.setTarget(finalFee.doubleValue());

            score = qscore.getQscore(qualityInfo);
        } catch (Exception e) {
            logger.warn("calculate qScore error :{}", e);
        }

        return score;

    }

    /**
     * 融合不同维度的标签统计
     */
    private AdvertStatisticMergeEntity mergeTagStat(Map<String, AdvertStatisticMergeEntity> tagStat, Collection<String>
            tags) {

        if (MapUtils.isEmpty(tagStat)) {
            return AdvertStatisticMergeEntity.DEFAULT;
        }

        List<AdvertStatisticMergeEntity> entities = tags.stream().distinct().map(tagStat::get).collect(toList());

        double appCh = entities.stream().map(AdvertStatisticMergeEntity::getAppCurrentlyHour)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0D);

        double appCd = entities.stream().map(AdvertStatisticMergeEntity::getAppCurrentlyDay)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0D);

        double app7d = entities.stream().map(AdvertStatisticMergeEntity::getAppRecently7Day)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0D);

        double globalCh = entities.stream().map(AdvertStatisticMergeEntity::getGlobalCurrentlyHour)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0D);

        double globalCd = entities.stream()
                .map(AdvertStatisticMergeEntity::getGlobalCurrentlyDay)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0D);

        double global7D = entities.stream().map(AdvertStatisticMergeEntity::getGlobalRecently7Day)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0D);

        return new AdvertStatisticMergeEntity.Builder()
                .appCurrentlyHour(appCh)
                .appCurrentlyDay(appCd)
                .appRecently7Day(app7d)
                .globalCurrentlyHour(globalCh)
                .globalCurrentlyDay(globalCd)
                .globalRecently7Day(global7D)
                .build();

    }

    private Boolean isNewAdvert(Long advertId, Map<Long, StatDo> statDoMap) {
        StatDo statDo = statDoMap.get(advertId);
        return statDo == null || statDo.getLaunchCnt() == null || statDo.getLaunchCnt() < GlobalConstant.NEW_ADD_AD_THRESHOLD;
    }

    private StatInfo getStatInfo(AdvertStatisticMergeEntity advertStatisticMergeEntity) {
        StatInfo statInfo = new StatInfo();
        statInfo.setApp1h(advertStatisticMergeEntity.getAppCurrentlyHour());
        statInfo.setApp1d(advertStatisticMergeEntity.getAppCurrentlyDay());
        statInfo.setApp7d(advertStatisticMergeEntity.getAppRecently7Day());
        statInfo.setG1h(advertStatisticMergeEntity.getGlobalCurrentlyHour());
        statInfo.setG1d(advertStatisticMergeEntity.getGlobalCurrentlyDay());
        statInfo.setG7d(advertStatisticMergeEntity.getGlobalRecently7Day());
        return statInfo;
    }
}

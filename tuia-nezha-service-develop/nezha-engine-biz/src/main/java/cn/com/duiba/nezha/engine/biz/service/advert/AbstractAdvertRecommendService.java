package cn.com.duiba.nezha.engine.biz.service.advert;

import cn.com.duiba.nezha.alg.alg.alg.BackendCostOptAlg;
import cn.com.duiba.nezha.alg.alg.vo.BackendAdvertStatDo;
import cn.com.duiba.nezha.alg.alg.vo.StatDo;
import cn.com.duiba.nezha.engine.api.enums.DeepTfServer;
import cn.com.duiba.nezha.engine.api.enums.ModelKeyEnum;
import cn.com.duiba.nezha.engine.api.enums.PredictCorrectType;
import cn.com.duiba.nezha.engine.api.enums.StatDataTypeEnum;
import cn.com.duiba.nezha.engine.biz.constant.GlobalConstant;
import cn.com.duiba.nezha.engine.biz.domain.ConsumerDo;
import cn.com.duiba.nezha.engine.biz.domain.CorrectResult;
import cn.com.duiba.nezha.engine.biz.domain.FeatureIndex;
import cn.com.duiba.nezha.engine.biz.domain.PredictParameter;
import cn.com.duiba.nezha.engine.biz.domain.advert.Advert;
import cn.com.duiba.nezha.engine.biz.domain.advert.Material;
import cn.com.duiba.nezha.engine.biz.domain.advert.OrientationPackage;
import cn.com.duiba.nezha.engine.biz.enums.RecommendMaterialType;
import cn.com.duiba.nezha.engine.biz.service.advert.ctr.AdvertMergeStatService;
import cn.com.duiba.nezha.engine.biz.service.advert.ctr.AdvertPredictCorrectService;
import cn.com.duiba.nezha.engine.biz.service.advert.ctr.AdvertPredictService;
import cn.com.duiba.nezha.engine.biz.service.advert.material.AdvertMaterialService;
import cn.com.duiba.nezha.engine.biz.service.advert.merge.AdvertDataMergeService;
import cn.com.duiba.nezha.engine.biz.service.advert.rerank.AdvertReRankService;
import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertRecommendRequestVo;
import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertResortVo;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;

import static cn.com.duiba.nezha.engine.api.enums.StatDataTypeEnum.*;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

public abstract class AbstractAdvertRecommendService {

    @Resource
    private AdvertReRankService advertReRankService;

    @Autowired
    private AdvertMergeStatService advertMergeStatService;

    @Resource
    private AdvertDataMergeService advertDataMergeService;

    @Autowired
    private AdvertMaterialService advertMaterialService;

    @Autowired
    private AdvertPredictCorrectService advertPredictCorrectService;

    @Autowired
    private AdvertPredictService advertPredictService;

    /**
     * 准备策略参数
     */
    public abstract void prepareStrategyParameter(AdvertRecommendRequestVo advertRecommendRequestVo);


    public List<AdvertResortVo> recommend(AdvertRecommendRequestVo advertRecommendRequestVo) {

        Map<Long, Advert> advertMap = advertRecommendRequestVo.getAdvertMap();
        Set<Long> advertIds = advertMap.keySet();
        Collection<Advert> adverts = advertMap.values();
        Map<Long, Long> timesMap = advertRecommendRequestVo.getTimesMap();

        Long appId = advertRecommendRequestVo.getAppDo().getId();
        List<StatDo> advertAppMergeStatDoList = advertMergeStatService.get(advertIds, appId, timesMap);
        List<StatDo> advertGlobalMergeStatDoList = advertMergeStatService.get(advertIds, null, timesMap);
        advertRecommendRequestVo.setAdvertAppMergeStatDoList(advertAppMergeStatDoList);
        advertRecommendRequestVo.setAdvertGlobalMergeStatDoList(advertGlobalMergeStatDoList);

        // 如果需要预估
        Map<StatDataTypeEnum, Map<FeatureIndex, Double>> predictValueMap = new HashMap<>();
        if (advertRecommendRequestVo.getNeedPredict()) {

            // 需要预估cvr的广告
            Set<Advert> needPredictCvrAdverts = this.getNeedPredictCvrAdverts(advertGlobalMergeStatDoList, advertMap);

            Map<Long, StatDo> appStatDoList = advertAppMergeStatDoList.stream().collect(toMap(StatDo::getAdvertId, Function.identity()));
            Map<Long, StatDo> globalStatDoList = advertGlobalMergeStatDoList.stream().collect(toMap(StatDo::getAdvertId, Function.identity()));

            // 获取后端数据
            Map<Long, BackendAdvertStatDo> backendAdvertStatInfo = BackendCostOptAlg.getBackendAdvertStatInfo(appStatDoList, globalStatDoList);
            advertRecommendRequestVo.setBackendAdvertStatMap(backendAdvertStatInfo);
            Set<Long> hasBackendCvrDataAdvertIds = backendAdvertStatInfo.keySet();

            // 需要进行后端cvr预估的配置
            Set<Advert> needPredictBackendCvrAdverts = this.getNeedPredictBackendCvrAdverts(adverts, hasBackendCvrDataAdvertIds);

            // 预估类型对应的广告列表
            Map<StatDataTypeEnum, Collection<Advert>> needPredictAdvertMap = new HashMap<>();
            needPredictAdvertMap.put(CTR, adverts);
            needPredictAdvertMap.put(CVR, needPredictCvrAdverts);
            needPredictAdvertMap.put(BACKEND_CVR, needPredictBackendCvrAdverts);

            // 预估类型对应的模型key
            Map<StatDataTypeEnum, ModelKeyEnum> modelKeyMap = new HashMap<>();
            modelKeyMap.put(CTR, advertRecommendRequestVo.getCtrModelKey());
            modelKeyMap.put(CVR, advertRecommendRequestVo.getCvrModelKey());
            modelKeyMap.put(BACKEND_CVR, advertRecommendRequestVo.getBackendCtrModelKey());

            // 深度学习模型
            Map<StatDataTypeEnum, DeepTfServer> deepModelKeyMap = new HashMap<>();
            deepModelKeyMap.put(CTR, advertRecommendRequestVo.getDeepCtrModelKey());
            deepModelKeyMap.put(CVR, advertRecommendRequestVo.getDeepCvrModelKey());

            // 预估参数准备
            PredictParameter predictParameter = PredictParameter.newBuilder()
                    .modelKeyMap(modelKeyMap)
                    .deepModelKeyMap(deepModelKeyMap)
                    .needPredictAdvertMap(needPredictAdvertMap)
                    .build();

            // 进行预估
            predictValueMap = advertPredictService.predict(advertRecommendRequestVo, predictParameter);

            // 获取预估ctr
            Map<FeatureIndex, Double> predictCtrMap = predictValueMap.get(CTR);
            Map<FeatureIndex, Double> predictCvrMap = predictValueMap.get(CVR);
            Map<FeatureIndex, Double> predictBackendCvrMap = predictValueMap.getOrDefault(BACKEND_CVR, new HashMap<>());
            advertRecommendRequestVo.setPredictCtr(predictCtrMap);
            advertRecommendRequestVo.setPredictCvr(predictCvrMap);

            // 因为后端预估没有素材参与,所以修正为 广告id->预估值
            Map<Long, Double> advertPredictBackendCvrMap = new HashMap<>();
            predictBackendCvrMap.forEach((featureIndex, predictValue) -> advertPredictBackendCvrMap.put(featureIndex.getAdvertId(), predictValue));

            advertRecommendRequestVo.setPredictBackendCvr(advertPredictBackendCvrMap);
        }

        // 进行素材选择(只选择一个)
        RecommendMaterialType recommendMaterialType = advertRecommendRequestVo.getRecommendMaterialType();
        Map<FeatureIndex, Double> predictCtrMap = predictValueMap.getOrDefault(CTR, new HashMap<>());
        Map<FeatureIndex, Double> predictCvrMap = predictValueMap.getOrDefault(CVR, new HashMap<>());
        this.handleTestMaterial(adverts, appId, recommendMaterialType, predictCtrMap, predictCvrMap);

        // 进行纠偏和重构
        PredictCorrectType predictCorrectType = advertRecommendRequestVo.getPredictCorrectType();
        if (!predictCorrectType.equals(PredictCorrectType.NONE)
                && recommendMaterialType.equals(RecommendMaterialType.NONE)) {
            // 纠偏
            CorrectResult correctResult = advertPredictCorrectService.correct(advertRecommendRequestVo, predictValueMap);

            Map<StatDataTypeEnum, Map<Long, Double>> reconstructionFactorMap = correctResult.getReconstructionFactorMap();
            Map<StatDataTypeEnum, Map<Long, Double>> correctionFactorMap = correctResult.getCorrectionFactorMap();

            // CTR 重构系数
            Map<Long, Double> ctrReconstructionFactorMap = reconstructionFactorMap.getOrDefault(CTR, new HashMap<>());
            Map<Long, Double> ctrCorrectionFactorMap = correctionFactorMap.getOrDefault(CTR, new HashMap<>());

            // CVR 重构系数
            Map<Long, Double> cvrReconstructionFactorMap = reconstructionFactorMap.getOrDefault(CVR, new HashMap<>());
            Map<Long, Double> cvrCorrectionFactorMap = correctionFactorMap.getOrDefault(CVR, new HashMap<>());

            advertRecommendRequestVo.setCtrReconstructionFactorMap(ctrReconstructionFactorMap);
            advertRecommendRequestVo.setCvrReconstructionFactorMap(cvrReconstructionFactorMap);
            advertRecommendRequestVo.setCtrCorrectionFactorMap(ctrCorrectionFactorMap);
            advertRecommendRequestVo.setCvrCorrectionFactorMap(cvrCorrectionFactorMap);
        }


        // 设置用户行为
        ConsumerDo consumerDo = advertRecommendRequestVo.getConsumerDo();
        Map<Long, Integer> userAdvertBehaviorMap = this.userAdvertBehavior(adverts, consumerDo);
        advertRecommendRequestVo.setUserAdvertBehaviorMap(userAdvertBehaviorMap);

        // 数据融合
        List<AdvertResortVo> advertResortVos = advertDataMergeService.dataMerge(advertRecommendRequestVo);

        // 排序
        return advertReRankService.reRank(advertResortVos);

    }


    /**
     * 处理试投素材
     * 对于配置包进行素材筛选
     * 如果广告命中了试投素材.
     * 则投放试投素材
     *
     * @param adverts               广告列表
     * @param appId                 媒体id
     * @param recommendMaterialType 素材推荐类型
     * @param predictCtrMap         ctr预估
     * @param predictCvrMap         cvr预估
     */
    public void handleTestMaterial(Collection<Advert> adverts,
                                   Long appId,
                                   RecommendMaterialType recommendMaterialType,
                                   Map<FeatureIndex, Double> predictCtrMap,
                                   Map<FeatureIndex, Double> predictCvrMap) {

        // 如果不进行素材推荐.则直接返回
        if (recommendMaterialType.equals(RecommendMaterialType.NONE)) {
            return;
        }

        Set<Long> advertIds = adverts.stream().map(Advert::getId).collect(toSet());
        Map<Long, List<Long>> materialRankList = new HashMap<>();
        if (recommendMaterialType.equals(RecommendMaterialType.STATIC)) {
            materialRankList = advertMaterialService.getMaterialRankList(appId, advertIds);
        }

        Map<Long, List<Long>> finalMaterialRankList = materialRankList;
        adverts.stream()
                .map(Advert::getOrientationPackages)
                .flatMap(Collection::stream)
                .forEach(orientationPackage -> {
                    Long advertId = orientationPackage.getAdvertId();

                    // 如果是素材优选,则只选一个素材
                    if (recommendMaterialType.equals(RecommendMaterialType.STATIC)) {
                        orientationPackage.setMaterials(this.selectMaterial(orientationPackage, finalMaterialRankList));
                    } else {
                        // 如果是素材改造,则根据ctr,和cvr来选取一个素材
                        Boolean cpa = orientationPackage.isCpa();
                        Set<Material> bestMaterial = orientationPackage.getMaterials()
                                .stream()
                                .max(Comparator.comparing(material -> {
                                    Long materialId = material.getId();
                                    FeatureIndex featureIndex = new FeatureIndex(advertId, materialId);
                                    Double ctrValue = predictCtrMap.getOrDefault(featureIndex, 0D);
                                    Double cvrValue = predictCvrMap.getOrDefault(featureIndex, 0D);
                                    if (cpa) {
                                        return ctrValue * cvrValue;
                                    } else {
                                        return ctrValue;
                                    }
                                }))
                                .map(Collections::singleton)
                                .orElseGet(HashSet::new);
                        orientationPackage.setMaterials(bestMaterial);
                    }
                });
    }

    /**
     * 为配置包选取一个素材
     *
     * @param orientationPackage 配置包
     * @param materialRankList   广告对应的素材列表
     * @return 单个素材的集合(可能为kong)
     */
    private Set<Material> selectMaterial(OrientationPackage orientationPackage, Map<Long, List<Long>> materialRankList) {
        Set<Material> materials = orientationPackage.getMaterials();

        if (materials.isEmpty()) {
            return new HashSet<>();
        }

        List<Long> materialIds = materialRankList.get(orientationPackage.getAdvertId());
        // 从推荐的素材列表中获取一个素材.如果没有.则从请求列表中随机选取一个

        Map<Long, Material> collect = materials.stream().collect(toMap(Material::getId, Function.identity()));
        Set<Long> requestMaterials = collect.keySet();
        Material finalMaterial = materialIds
                .stream()
                .filter(requestMaterials::contains)
                .findAny()
                .map(collect::get)
                .orElse(new ArrayList<>(materials).get(new Random().nextInt(materials.size())));
        return Collections.singleton(finalMaterial);
    }

    private Set<Advert> getNeedPredictBackendCvrAdverts(Collection<Advert> adverts, Set<Long> hasBackendCvrDataAdvertIds) {
        return adverts.stream()
                .filter(advert -> hasBackendCvrDataAdvertIds.contains(advert.getId()))
                .collect(toSet());
    }

    private Set<Advert> getNeedPredictCvrAdverts(List<StatDo> advertGlobalMergeStatDoList, Map<Long, Advert> advertMap) {

        // 获取有转换数据的广告
        Set<Advert> advertsWithCvrData = advertGlobalMergeStatDoList.
                stream()
                .filter(this::hasCvrData)
                .map(StatDo::getAdvertId)
                .map(advertMap::get)
                .collect(toSet());

        // cpa 广告
        Set<Advert> cpaAdverts = advertMap.values().stream().filter(Advert::hasCpaOrientationPackage).collect(toSet());

        // 需要预估cvr的广告
        Set<Advert> needPredictCvrAdverts = new HashSet<>();
        needPredictCvrAdverts.addAll(cpaAdverts);
        needPredictCvrAdverts.addAll(advertsWithCvrData);
        return needPredictCvrAdverts;
    }

    /**
     * 判断该广告是否有转换数据
     *
     * @param statDo 统计数据
     * @return true-有 false-没有
     */
    private Boolean hasCvrData(StatDo statDo) {
        // 有曝光数据
        return statDo.getActExpCnt() != null && statDo.getActExpCnt() > 0;
    }

    private Map<Long, Integer> userAdvertBehavior(Collection<Advert> adverts, ConsumerDo consumerDo) {

        Map<Long, Integer> advertBehaviorMap = new HashMap<>(adverts.size());

        String clickInterestedTags = consumerDo.getClickInterestedTags();
        String clickUninterestedTags = consumerDo.getClickUninterestedTags();
        String convertInterestedTags = consumerDo.getConvertInterestedTags();
        String convertUninterestedTags = consumerDo.getConvertUninterestedTags();

        Splitter splitter = GlobalConstant.SPLITTER;

        Set<String> clickInterestedTagSet = StringUtils.isEmpty(clickInterestedTags) ? Collections.emptySet() :
                new HashSet<>(splitter.splitToList(clickInterestedTags));
        Set<String> clickUninterestedTagSet = StringUtils.isEmpty(clickUninterestedTags) ? Collections.emptySet() :
                new HashSet<>(splitter.splitToList(clickUninterestedTags));
        Set<String> convertInterestedTagSet = StringUtils.isEmpty(convertInterestedTags) ? Collections.emptySet() :
                new HashSet<>(splitter.splitToList(convertInterestedTags));
        Set<String> convertUninterestedTagSet = StringUtils.isEmpty(convertUninterestedTags) ? Collections.emptySet() :
                new HashSet<>(splitter.splitToList(convertUninterestedTags));


        for (Advert advert : adverts) {

            String matchTags = advert.getMatchTags();
            Set<String> matchTagNumSet = StringUtils.isEmpty(matchTags) ? Collections.emptySet() :
                    new HashSet<>(splitter.splitToList(matchTags));

            int type = getType(clickInterestedTagSet, clickUninterestedTagSet, convertInterestedTagSet,
                    convertUninterestedTagSet, matchTagNumSet);

            advertBehaviorMap.put(advert.getId(), type);

        }


        return advertBehaviorMap;
    }

    private int getType(Set<String> clickInterestedTagSet, Set<String> clickUninterestedTagSet, Set<String>
            convertInterestedTagSet, Set<String> convertUninterestedTagSet, Set<String> matchTagNumSet) {
        int type = 0;

        if (CollectionUtils.isNotEmpty(Sets.intersection(clickInterestedTagSet, matchTagNumSet))) {
            type |= 0b1000;
        }

        if (CollectionUtils.isNotEmpty(Sets.intersection(clickUninterestedTagSet, matchTagNumSet))) {
            type |= 0b100;
        }

        if (CollectionUtils.isNotEmpty(Sets.intersection(convertInterestedTagSet, matchTagNumSet))) {
            type |= 0b10;
        }

        if (CollectionUtils.isNotEmpty(Sets.intersection(convertUninterestedTagSet, matchTagNumSet))) {
            type |= 0b1;
        }
        return type;
    }
}

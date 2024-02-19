package cn.com.duiba.nezha.engine.biz.service.advert.ctr.impl;

import cn.com.duiba.nezha.alg.alg.vo.BackendAdvertStatDo;
import cn.com.duiba.nezha.alg.feature.parse.FeatureParse;
import cn.com.duiba.nezha.alg.feature.vo.FeatureDo;
import cn.com.duiba.nezha.alg.model.FM;
import cn.com.duiba.nezha.alg.model.tf.TFServingClient;
import cn.com.duiba.nezha.engine.api.enums.DeepTfServer;
import cn.com.duiba.nezha.engine.api.enums.ModelKey;
import cn.com.duiba.nezha.engine.api.enums.ModelKeyEnum;
import cn.com.duiba.nezha.engine.api.enums.StatDataTypeEnum;
import cn.com.duiba.nezha.engine.api.support.RecommendEngineException;
import cn.com.duiba.nezha.engine.biz.bo.hbase.ConsumerFeatureBo;
import cn.com.duiba.nezha.engine.biz.constant.GlobalConstant;
import cn.com.duiba.nezha.engine.biz.domain.*;
import cn.com.duiba.nezha.engine.biz.domain.advert.Advert;
import cn.com.duiba.nezha.engine.biz.domain.advert.Material;
import cn.com.duiba.nezha.engine.biz.domain.advert.OrientationPackage;
import cn.com.duiba.nezha.engine.biz.service.CacheService;
import cn.com.duiba.nezha.engine.biz.service.advert.ctr.AdvertPredictService;
import cn.com.duiba.nezha.engine.biz.service.advert.ctr.AdvertStatFeatureService;
import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertRecommendRequestVo;
import cn.com.duiba.wolf.perf.timeprofile.DBTimeProfile;
import cn.com.duibaboot.ext.autoconfigure.core.utils.CatUtils;
import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.com.duiba.nezha.engine.biz.constant.GlobalConstant.throwingMerger;
import static java.util.stream.Collectors.*;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: AdvertPredictServiceImpl.java , v 0.1 2017/5/20 下午11:09 ZhouFeng Exp $
 */
@Service
public class AdvertPredictServiceImpl extends CacheService implements AdvertPredictService, InitializingBean {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ConsumerFeatureBo consumerFeatureBo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AdvertStatFeatureService advertStatFeatureService;


    @Value("${spring.profiles.active}")
    private String active;

    /**
     * 离线预估模型缓存
     */
    private final LoadingCache<ModelKeyEnum, FM> offlineModelCache = CacheBuilder.newBuilder()
            .refreshAfterWrite(30, TimeUnit.MINUTES)
            .build(new CacheLoader<ModelKeyEnum, FM>() {

                @Override
                public FM load(ModelKeyEnum modelKey) {
                    //数据第一次加载或失效时调用，如果获取不到值则返回null
                    return loadModel(modelKey).orElse(null);
                }

                @Override
                public ListenableFuture<FM> reload(ModelKeyEnum modelKey, FM oldValue) {
                    //数据刷新时调用，如果获取不到，将继续使用老值
                    ListenableFutureTask<FM> task = ListenableFutureTask.create(() -> loadModel(modelKey).orElse(oldValue));
                    executorService.submit(task);
                    return task;
                }
            });
    /**
     * 在线预估模型缓存
     */
    private final LoadingCache<ModelKeyEnum, FM> onlineModelCache = CacheBuilder.newBuilder()
            .refreshAfterWrite(60, TimeUnit.SECONDS)
            .expireAfterWrite(90, TimeUnit.SECONDS)
            .build(new CacheLoader<ModelKeyEnum, FM>() {

                @Override
                public FM load(ModelKeyEnum modelKey) {
                    //数据第一次加载或失效时调用，如果获取不到值则返回null
                    return loadModel(modelKey).orElse(null);
                }

                @Override
                public ListenableFuture<FM> reload(ModelKeyEnum modelKey, FM oldValue) {
                    //数据刷新时调用，如果获取不到，将继续使用老值
                    ListenableFutureTask<FM> task = ListenableFutureTask.create(() -> loadModel(modelKey).orElse(oldValue));
                    executorService.submit(task);
                    return task;
                }
            });


    @Override
    public Map<StatDataTypeEnum, Map<FeatureIndex, Double>> predict(AdvertRecommendRequestVo advertRecommendRequestVo, PredictParameter predictParameter) {


        try {
            DBTimeProfile.enter("predict");
            Collection<Advert> adverts = advertRecommendRequestVo.getAdvertMap().values();

            AppDo appDo = advertRecommendRequestVo.getAppDo();
            RequestDo requestDo = advertRecommendRequestVo.getRequestDo();
            ConsumerDo consumerDo = advertRecommendRequestVo.getConsumerDo();
            ActivityDo activityDo = advertRecommendRequestVo.getActivityDo();
            Map<Long, Long> launchTimesMap = advertRecommendRequestVo.getTimesMap();
            Map<Long, BackendAdvertStatDo> advertBackendStatMap = advertRecommendRequestVo.getBackendAdvertStatMap();
            Map<StatDataTypeEnum, Collection<Advert>> needPredictAdvertMap = predictParameter.getNeedPredictAdvertMap();

            Long appId = appDo.getId();
            Long slotId = appDo.getSlotId();
            Long activityId = activityDo.getOperatingId();


            Map<Long, AdvertStatFeatureDo> advertStatFeatureMap = advertStatFeatureService.get(appId, slotId, activityId, adverts);
            advertRecommendRequestVo.setAdvertStatFeatureMap(advertStatFeatureMap);

            // 深度学习模型
            Map<StatDataTypeEnum, DeepTfServer> deepModelKeyMap = predictParameter.getDeepModelKeyMap();

            // 获取特征
            Map<FeatureIndex, Map<String, String>> featureMap = this.getFeatureMap(adverts,
                    consumerDo, appDo, activityDo, requestDo,
                    launchTimesMap,
                    advertStatFeatureMap,
                    advertBackendStatMap);

            advertRecommendRequestVo.setFeatureMap(featureMap);

            Map<StatDataTypeEnum, ModelKeyEnum> modelKeyMap = predictParameter.getModelKeyMap();

            Map<ModelKeyEnum, FM> keyModelMap = modelKeyMap.values().stream().filter(Objects::nonNull).collect(toMap(Function.identity(), this::getModel, throwingMerger(), Hashtable::new));

            Map<StatDataTypeEnum, Map<FeatureIndex, Double>> dataPredictValueMap = new HashMap<>();

            modelKeyMap.forEach((type, modelKey) -> {

                if (modelKey == null) {
                    return;
                }

                // 获取模型
                FM model = keyModelMap.get(modelKey);
                if (model == null) {
                    logger.warn("{} model is null,with modelKey {}", type.toString(), modelKey);
                    return;
                }

                // 获取深度学习服务对象
                DeepTfServer deepTfServer = deepModelKeyMap.get(type);

                Collection<Advert> needPredictAdverts = needPredictAdvertMap.get(type);
                Map<FeatureIndex, Map<String, String>> featureIndexMap = needPredictAdverts.stream()
                        .map(this::convertToFeatureIndex)
                        .flatMap(Set::stream)
                        .collect(toMap(Function.identity(), featureMap::get));

                // 获取广告的预估值
                try {
                    DBTimeProfile.enter(type.toString() + " predict");
                    boolean normalPredict = deepTfServer == null;
                    Map<FeatureIndex, Double> predictValueMap = CatUtils.executeInCatTransaction(
                                    () -> this.getPredictResultVo(model, featureIndexMap, deepTfServer),
                                    normalPredict ? "Predict" : "deepPredict",
                                    normalPredict ? modelKey.toString() : deepTfServer.toString());

                    dataPredictValueMap.put(type, predictValueMap);
                } catch (Throwable e) {
                    logger.error("CatUtils.executeInCatTransaction happened error:{},the model key :{}", e.getMessage(), modelKey);
                } finally {
                    DBTimeProfile.release();
                }

            });
            return dataPredictValueMap;
        } catch (Exception e) {
            logger.warn("predict happened error:{}", e);
            return new HashMap<>();
        } finally {
            DBTimeProfile.release();
        }
    }

    private Set<FeatureIndex> convertToFeatureIndex(Advert advert) {
        Long advertId = advert.getId();
        Set<OrientationPackage> orientationPackages = advert.getOrientationPackages();

        return orientationPackages.stream().map(orientationPackage -> {
            Set<Material> materials = orientationPackage.getMaterials();
            if (materials.isEmpty()) {
                return Collections.singleton(new FeatureIndex(advertId));
            } else {
                return materials.stream().map(material -> new FeatureIndex(advertId, material.getId())).collect(toSet());
            }
        }).flatMap(Set::stream).collect(toSet());
    }

    public Map<FeatureIndex, Map<String, String>> getFeatureMap(Collection<Advert> adverts,
                                                                ConsumerDo consumerDo,
                                                                AppDo appDo,
                                                                ActivityDo activityDo,
                                                                RequestDo requestDo,
                                                                Map<Long, Long> launchTimesMap,
                                                                Map<Long, AdvertStatFeatureDo> advertStatFeatureMap,
                                                                Map<Long, BackendAdvertStatDo> advertBackendStatMap) {

        // 获取本次请求特征(静态特征)
        FeatureDo staticFeatureDo = this.getFeatureDo(consumerDo, appDo, activityDo, requestDo);
        Map<String, String> staticFeatureMap = FeatureParse.generateFeatureMapStatic(staticFeatureDo);

        // 算法需要的特征map , FeatureDo -> Map<String,String>
        Map<FeatureIndex, Map<String, String>> finalFeatureMap = new HashMap<>();

        adverts.forEach(advert -> {

            Long advertId = advert.getId();

            // 广告粒度的特征
            FeatureDo advertFeatureDo = new FeatureDo();
            advertFeatureDo.setAdvertId(advertId);
            advertFeatureDo.setAccountId(advert.getAccountId());
            advertFeatureDo.setMatchTagNums(advert.getMatchTags());
            advertFeatureDo.setTradeId2(advert.getIndustryTag());
            advertFeatureDo.setAdvertTags(advert.getSpreadTags().stream().filter(Objects::nonNull).collect(Collectors.joining(",")));
            advertFeatureDo.setTimes(launchTimesMap.get(advertId));

            // 设置后端类型
            Optional.ofNullable(advertBackendStatMap.get(advertId)).map(BackendAdvertStatDo::getBackendType).ifPresent(advertFeatureDo::setBankEndType);

            // 设置特征统计值
            Optional<AdvertStatFeatureDo> statFeatureDo = Optional.ofNullable(advertStatFeatureMap.get(advertId));
            statFeatureDo.map(AdvertStatFeatureDo::getAdvertCtr).ifPresent(advertFeatureDo::setAdvertCtr);
            statFeatureDo.map(AdvertStatFeatureDo::getAdvertCvr).ifPresent(advertFeatureDo::setAdvertCvr);
            statFeatureDo.map(AdvertStatFeatureDo::getAdvertAppCtr).ifPresent(advertFeatureDo::setAdvertAppCtr);
            statFeatureDo.map(AdvertStatFeatureDo::getAdvertAppCvr).ifPresent(advertFeatureDo::setAdvertAppCvr);
            statFeatureDo.map(AdvertStatFeatureDo::getAdvertSlotCtr).ifPresent(advertFeatureDo::setAdvertSlotCtr);
            statFeatureDo.map(AdvertStatFeatureDo::getAdvertSlotCvr).ifPresent(advertFeatureDo::setAdvertSlotCvr);
            statFeatureDo.map(AdvertStatFeatureDo::getAdvertActivityCtr).ifPresent(advertFeatureDo::setAdvertActivityCtr);
            statFeatureDo.map(AdvertStatFeatureDo::getAdvertActivityCvr).ifPresent(advertFeatureDo::setAdvertActivityCvr);


            // 如果使用素材作为预估特征.由于素材是跟着配置的.但是配置没有额外特征.所以配置的特征沿用广告
            advert.getOrientationPackages().forEach(orientationPackage -> {

                // 获取配置包对应的素材
                Set<Material> materials = orientationPackage.getMaterials();

                // 如果没有素材,则直接使用广告的特征
                if (materials.isEmpty()) {
                    FeatureIndex advertFeatureIndex = FeatureIndex.newBuilder().advertId(advertId).build();
                    Map<String, String> dynamicMapMap = this.getDynamicMapMap(staticFeatureDo, advertFeatureDo, staticFeatureMap);
                    finalFeatureMap.put(advertFeatureIndex, dynamicMapMap);
                    return;
                }

                materials.forEach(materialDto -> {

                    Long materialId = materialDto.getId();

                    FeatureIndex advertMaterialFeatureIndex = FeatureIndex.newBuilder().advertId(advertId).materialId(materialId).build();

                    // 从临时容器中获取广告+素材纬度的特征.如果不存在.则生成广告+素材纬度的特征.并放入临时容器.下次直接取出来即可
                    Optional.ofNullable(finalFeatureMap.get(advertMaterialFeatureIndex)).orElseGet(() -> {
                        FeatureDo materialFeatureDo = this.copyAdvertFeature(advertFeatureDo);
                        materialFeatureDo.setMaterialId(materialId.toString());
                        materialFeatureDo.setAtmosphere(materialDto.getAtmosphere());
                        materialFeatureDo.setBackgroundColour(materialDto.getBackgroundColour());
                        materialFeatureDo.setIfPrevalent(String.valueOf(materialDto.getPrevalent()));
                        materialFeatureDo.setDescribeKeywords(materialDto.getInterception());
                        materialFeatureDo.setDynamicEffect(materialDto.getCarton());
                        materialFeatureDo.setBodyElement(materialDto.getBodyElement());
                        materialFeatureDo.setMaterialTags(materialDto.getTags().stream().filter(Objects::nonNull).collect(joining(",")));
                        Map<String, String> dynamicMap = this.getDynamicMapMap(staticFeatureDo, materialFeatureDo, staticFeatureMap);
                        dynamicMap.putAll(staticFeatureMap);
                        finalFeatureMap.put(advertMaterialFeatureIndex, dynamicMap);
                        return dynamicMap;
                    });
                });
            });
        });

        return finalFeatureMap;

    }

    private Map<FeatureIndex, Double> getPredictResultVo(FM model, Map<FeatureIndex, Map<String, String>> featureMap, DeepTfServer deepTfServer) {

        try {
            TFServingClient tfServingClient = Optional.ofNullable(deepTfServer)
                    .map(server -> new TFServingClient(server.getHost(), server.getPort(), server.getModelKey(), null))
                    .orElse(null);

            return model.predictWithTF(featureMap, tfServingClient);
        } catch (Exception e) {
            if (deepTfServer == null) {
                logger.error("模型计算错误:{}", e);
                return new HashMap<>();
            } else {
                try {
                    logger.error("深度模型计算错误:{}", e);
                    return model.predicts(featureMap);
                } catch (Exception e1) {
                    logger.error("深度模型计算错误后,降级模型计算错误:{}", e);
                    return new HashMap<>();
                }
            }
        }
    }

    /**
     * 获取用户请求特征
     */
    private FeatureDo getFeatureDo(ConsumerDo consumerDo, AppDo appDo, ActivityDo activityDo, RequestDo requestDo) {
        try {
            DBTimeProfile.enter("consumerFeatureBo.getFeatureDo");
            return consumerFeatureBo.getFeatureDo(consumerDo, appDo, activityDo, requestDo);
        } catch (Exception e) {
            throw new RecommendEngineException("consumerFeatureBo.getFeatureDo happened error", e);
        } finally {
            DBTimeProfile.release();
        }
    }

    /**
     * 加载模型
     *
     * @param modelKey 模型的key
     */
    private FM getModel(ModelKeyEnum modelKey) {

        try {
            if (modelKey.getOnline()) {
                return onlineModelCache.get(modelKey);
            } else {
                return offlineModelCache.get(modelKey);
            }
        } catch (Exception e) {
            logger.warn("get model happened error ,the model key is {} ,{}", modelKey, e);
            return null;
        }
    }


    /**
     * 加载在线预估模型
     */
    private Optional<FM> loadModel(ModelKeyEnum modelKeyEnum) {
        try {
            DBTimeProfile.enter("loadModel");
            return Optional.ofNullable(modelKeyEnum)
                    .map(modelKey -> mongoTemplate.findById(
                            ModelKey.getLastModelNewKey(modelKey.getIndex()),
                            String.class,
                            GlobalConstant.LR_MODEL_ES_TYPE))
                    .map(json -> JSON.parseObject(json, FM.class));
        } catch (Exception e) {
            logger.error("load loadModel error,modelKey:{}", modelKeyEnum);
            throw new RecommendEngineException("load model exception", e);
        } finally {
            DBTimeProfile.release();
        }

    }


    /**
     * 拷贝对象
     */
    private FeatureDo copyAdvertFeature(FeatureDo advertFeatureDo) {
        FeatureDo featureDo = new FeatureDo();
        featureDo.setAdvertId(advertFeatureDo.getAdvertId());
        featureDo.setAccountId(advertFeatureDo.getAccountId());
        featureDo.setMatchTagNums(advertFeatureDo.getMatchTagNums());
        featureDo.setAdvertTags(advertFeatureDo.getAdvertTags());
        featureDo.setTradeId(advertFeatureDo.getTradeId());
        featureDo.setTradeId2(advertFeatureDo.getTradeId2());
        featureDo.setTimes(advertFeatureDo.getTimes());

        featureDo.setAdvertCtr(advertFeatureDo.getAdvertCtr());
        featureDo.setAdvertCvr(advertFeatureDo.getAdvertCvr());
        featureDo.setAdvertSlotCtr(advertFeatureDo.getAdvertSlotCtr());
        featureDo.setAdvertSlotCvr(advertFeatureDo.getAdvertSlotCvr());
        featureDo.setAdvertAppCtr(advertFeatureDo.getAdvertAppCtr());
        featureDo.setAdvertAppCvr(advertFeatureDo.getAdvertAppCvr());
        featureDo.setAdvertActivityCtr(advertFeatureDo.getAdvertActivityCtr());
        featureDo.setAdvertActivityCvr(advertFeatureDo.getAdvertActivityCvr());

        return featureDo;
    }

    /**
     * 获取动态map
     */
    private Map<String, String> getDynamicMapMap(FeatureDo staticFeatureDo, FeatureDo advertFeatureDo, Map<String, String> staticMap) {
        Map<String, String> dynamicMap = FeatureParse.generateFeatureMapDynamic(advertFeatureDo, staticFeatureDo);
        dynamicMap.putAll(staticMap);
        return dynamicMap;
    }


    @Override
    public void afterPropertiesSet() throws Exception {

        if ("dev".equals(active)) {
            return;
        }
        //初始化模型
        logger.info("start init predict model");
        long start = System.currentTimeMillis();

        Map<Boolean, List<ModelKeyEnum>> modelKeyMap = Arrays.stream(ModelKeyEnum.values())
                .collect(partitioningBy(ModelKeyEnum::getOnline));


        // 预加载离线模型
        for (ModelKeyEnum modelKey : modelKeyMap.get(false)) {
            try {
                offlineModelCache.get(modelKey);
            } catch (Exception e) {
               logger.info("{}",modelKey);
            }
        }

        // 预加载在线模型
        for (ModelKeyEnum modelKey : modelKeyMap.get(true)) {
            try {
                onlineModelCache.get(modelKey);
            } catch (Exception e) {
                logger.info("{}",modelKey);
            }
        }

        logger.info("init predict model finish,spend {} ms", System.currentTimeMillis() - start);

    }
}

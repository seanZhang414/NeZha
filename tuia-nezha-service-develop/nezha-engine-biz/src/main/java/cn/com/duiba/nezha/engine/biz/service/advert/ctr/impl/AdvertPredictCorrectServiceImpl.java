package cn.com.duiba.nezha.engine.biz.service.advert.ctr.impl;

import cn.com.duiba.nezha.alg.alg.correct.ModelPredCorrect;
import cn.com.duiba.nezha.alg.alg.correct.ModelPredRectifier;
import cn.com.duiba.nezha.alg.alg.vo.CorrectionInfo;
import cn.com.duiba.nezha.alg.alg.vo.NezhaStatDto;
import cn.com.duiba.nezha.engine.api.enums.AdvertAlgEnum;
import cn.com.duiba.nezha.engine.api.enums.PredictCorrectType;
import cn.com.duiba.nezha.engine.api.enums.RedisKey;
import cn.com.duiba.nezha.engine.api.enums.StatDataTypeEnum;
import cn.com.duiba.nezha.engine.biz.domain.CorrectResult;
import cn.com.duiba.nezha.engine.biz.domain.FeatureIndex;
import cn.com.duiba.nezha.engine.biz.domain.advert.Advert;
import cn.com.duiba.nezha.engine.biz.service.advert.ctr.AdvertPredictCorrectService;
import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertRecommendRequestVo;
import cn.com.duiba.nezha.engine.common.utils.MapUtils;
import cn.com.duiba.nezha.engine.common.utils.RedisKeyUtil;
import cn.com.duiba.nezha.engine.common.utils.StringRedisHelper;
import cn.com.duiba.wolf.perf.timeprofile.DBTimeProfile;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.com.duiba.nezha.engine.api.enums.PredictCorrectType.*;


@Service
public class AdvertPredictCorrectServiceImpl implements AdvertPredictCorrectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvertPredictCorrectServiceImpl.class);

    @Resource
    private StringRedisTemplate nezhaStringRedisTemplate;

    private LoadingCache<String, NezhaStatDto> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build(new CacheLoader<String, NezhaStatDto>() {
                @Override
                public NezhaStatDto load(String key) throws Exception {
                    throw new IllegalAccessException("not suppose single query");
                }

                @Override
                public Map<String, NezhaStatDto> loadAll(Iterable<? extends String> keys) {
                    return StringRedisHelper.of(nezhaStringRedisTemplate).valueMultiGet
                            (keys, NezhaStatDto.class, NezhaStatDto::new);
                }
            });

    @Override
    public CorrectResult correct(AdvertRecommendRequestVo advertRecommendRequestVo,
                                 Map<StatDataTypeEnum, Map<FeatureIndex, Double>> advertPredictValueMap) {

        PredictCorrectType correctType = advertRecommendRequestVo.getPredictCorrectType();
        AdvertAlgEnum advertAlgEnum = advertRecommendRequestVo.getAdvertAlgEnum();
        Collection<Advert> adverts = advertRecommendRequestVo.getAdvertMap().values();
        Long appId = advertRecommendRequestVo.getAppDo().getId();
        Long slotId = advertRecommendRequestVo.getAppDo().getSlotId();
        Map<Long, NezhaStatDto> nezhaStatDtoMap;
        if (correctType.equals(CORRECT) || correctType.equals(CORRECT_REFACTOR)) {
            nezhaStatDtoMap = this.loadNezhaStatDto(advertAlgEnum, adverts, appId);
        } else {
            nezhaStatDtoMap = this.loadNezhaStatDto(correctType, advertAlgEnum, adverts, appId, slotId);
        }
        advertRecommendRequestVo.setNezhaStatDtoMap(nezhaStatDtoMap);

        Map<StatDataTypeEnum, Map<Long, Double>> typeReconstructionFactorMap = new HashMap<>();
        Map<StatDataTypeEnum, Map<Long, Double>> typeCorrectionFactorMap = new HashMap<>();

        advertPredictValueMap.forEach((typeEnum, predictValueMap) -> {
            try {
                DBTimeProfile.enter("start " + typeEnum.toString() + " Correct");
                if (typeEnum.equals(StatDataTypeEnum.BACKEND_CVR)) {
                    return;
                }
                List<CorrectionInfo> correctionInfoList = new ArrayList<>();

                predictValueMap.keySet().forEach(featureIndex -> {
                    Long advertId = featureIndex.getAdvertId();
                    Optional.ofNullable(nezhaStatDtoMap.get(advertId)).ifPresent(nezhaStatDto -> {
                        Double predictValue = predictValueMap.get(featureIndex);
                        CorrectionInfo correctionInfo = new CorrectionInfo();
                        correctionInfo.setAdvertId(advertId);
                        correctionInfo.setType(typeEnum == StatDataTypeEnum.CTR ? 1L : 2L);
                        correctionInfo.setCurrentPreValue(predictValue);
                        correctionInfo.setNezhaStatDto(nezhaStatDto);
                        correctionInfoList.add(correctionInfo);
                    });
                });

                // 调用算法进行纠偏(分布重构)
                if (correctType == CORRECT) {
                    ModelPredRectifier.getCorrectionFactor(correctionInfoList);
                } else if (correctType == CORRECT_REFACTOR) {
                    ModelPredRectifier.getCorrectionReconstructionFactor(correctionInfoList);
                } else {
                    ModelPredCorrect.getCorrectionFactor(correctionInfoList);
                }

                Map<Long, Double> reconstructionFactorMap = new HashMap<>();
                Map<Long, Double> correctionFactorMap = new HashMap<>();

                correctionInfoList.forEach(correctionInfo -> {
                    Long advertId = correctionInfo.getAdvertId();
                    FeatureIndex featureIndex = new FeatureIndex(advertId);

                    // 获取纠偏参数,重构参数
                    Double correctionFactor = correctionInfo.getCorrectionFactor();
                    Double reconstructionFactor = correctionInfo.getReconstructionFactor();

                    // 纠偏预估值然后重新放入
                    double correctValue = predictValueMap.getOrDefault(featureIndex, 0D) * correctionFactor;
                    predictValueMap.put(featureIndex, correctValue);

                    // 返回
                    reconstructionFactorMap.put(advertId, reconstructionFactor);
                    correctionFactorMap.put(advertId, correctionFactor);
                });
                typeReconstructionFactorMap.put(typeEnum, reconstructionFactorMap);
                typeCorrectionFactorMap.put(typeEnum, correctionFactorMap);
            } catch (Exception e) {
                LOGGER.warn("correct error :{}", e);
            } finally {
                DBTimeProfile.release();
            }
        });

        return CorrectResult.newBuilder()
                .nezhaStatDtoMap(nezhaStatDtoMap)
                .correctionFactorMap(typeCorrectionFactorMap)
                .reconstructionFactorMap(typeReconstructionFactorMap)
                .build();
    }

    private Map<Long, NezhaStatDto> loadNezhaStatDto(PredictCorrectType correctType, AdvertAlgEnum advertAlgEnum, Collection<Advert> advertIds, Long appId, Long slotId) {
        RedisKey redisKey;
        if (correctType.equals(CORRECT_NEW1)) {
            redisKey = RedisKey.K67;
        } else {
            redisKey = RedisKey.K68;
        }
        try {
            DBTimeProfile.enter("loadNezhaStatDto");
            Map<Long, String> advertIdKeyMap = advertIds.stream().collect(Collectors.toMap(Advert::getId,
                    advert -> RedisKeyUtil.getNezhaStatKey(redisKey, advertAlgEnum.getType().longValue(), advert.getId(), appId, slotId)));
            return MapUtils.translate(advertIdKeyMap, cache.getAll(advertIdKeyMap.values()));
        } catch (Exception e) {
            LOGGER.warn("loadNezhaStatDto happened error:{}",e);
            return new HashMap<>();
        } finally {
            DBTimeProfile.release();
        }
    }

    @Override
    public Map<Long, NezhaStatDto> loadNezhaStatDto(AdvertAlgEnum advertAlgEnum, Collection<Advert> adverts, Long appId) {
        try {
            DBTimeProfile.enter("loadNezhaStatDto");
            Map<Long, String> advertIdKeyMap = adverts.stream().collect(Collectors.toMap(Advert::getId,
                    advert -> RedisKeyUtil.getNezhaStatKey(advertAlgEnum.getType().longValue(), advert.getId(), appId)));
            return MapUtils.translate(advertIdKeyMap, cache.getAll(advertIdKeyMap.values()));
        } catch (Exception e) {
            LOGGER.warn("loadNezhaStatDto happened error:{}",e);
            return new HashMap<>();
        } finally {
            DBTimeProfile.release();
        }
    }
}

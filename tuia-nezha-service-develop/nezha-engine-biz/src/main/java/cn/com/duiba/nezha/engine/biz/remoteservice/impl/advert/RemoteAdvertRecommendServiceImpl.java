package cn.com.duiba.nezha.engine.biz.remoteservice.impl.advert;

import cn.com.duiba.boot.perftest.PerfTestContext;
import cn.com.duiba.nezha.alg.alg.vo.NezhaStatDto;
import cn.com.duiba.nezha.engine.api.dto.*;
import cn.com.duiba.nezha.engine.api.enums.*;
import cn.com.duiba.nezha.engine.api.remoteservice.advert.RemoteAdvertRecommendService;
import cn.com.duiba.nezha.engine.api.support.RecommendEngineException;
import cn.com.duiba.nezha.engine.biz.domain.*;
import cn.com.duiba.nezha.engine.biz.domain.advert.Advert;
import cn.com.duiba.nezha.engine.biz.domain.advert.Material;
import cn.com.duiba.nezha.engine.biz.domain.advert.OrientationPackage;
import cn.com.duiba.nezha.engine.biz.entity.nezha.advert.BizLogEntity;
import cn.com.duiba.nezha.engine.biz.enums.RecommendMaterialType;
import cn.com.duiba.nezha.engine.biz.log.BaseInnerLog;
import cn.com.duiba.nezha.engine.biz.service.advert.AbstractAdvertRecommendService;
import cn.com.duiba.nezha.engine.biz.service.advert.InteractAdvertRecommendService;
import cn.com.duiba.nezha.engine.biz.service.advert.ShowAdvertRecommendService;
import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertRecommendRequestVo;
import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertResortVo;
import cn.com.duiba.nezha.engine.common.utils.AssertUtil;
import cn.com.duiba.wolf.dubbo.DubboResult;
import cn.com.duiba.wolf.perf.timeprofile.DBTimeProfile;
import com.alibaba.fastjson.JSON;
import com.dianping.cat.Cat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;


@RestController
public class RemoteAdvertRecommendServiceImpl implements RemoteAdvertRecommendService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteAdvertRecommendServiceImpl.class);

    @Autowired
    private ShowAdvertRecommendService showAdvertRecommendService;

    @Autowired
    private InteractAdvertRecommendService interactAdvertRecommendService;

    @Override
    public DubboResult<RcmdAdvertDto> recommend(ReqAdvertNewDto reqAdvertNewDto, String strategyId) {
        // 获取算法类型
        AdvertAlgEnum interactAdvertAlgEnum = Objects.requireNonNull(InteractAdvertAlgEnum.get(strategyId),
                "STRATEGY_ID_NOT_EXIST:" + strategyId);

        List<RcmdAdvertDto> rcmdAdvertDtos = this.doRecommend(reqAdvertNewDto, interactAdvertAlgEnum);

        if (!rcmdAdvertDtos.isEmpty()) {
            return DubboResult.successResult(rcmdAdvertDtos.get(0));
        }

        return DubboResult.successResult(null);
    }

    @Override
    public List<RcmdAdvertDto> batchRecommend(ReqAdvertNewDto reqAdvertNewDto, String strategyId) {
        AdvertAlgEnum advertAlgEnum = Optional.ofNullable(InteractAdvertAlgEnum.get(strategyId))
                .orElse(ShowAdvertAlgEnum.get(strategyId));
        Objects.requireNonNull(advertAlgEnum, "STRATEGY_ID_NOT_EXIST:" + strategyId);

        return this.doRecommend(reqAdvertNewDto, advertAlgEnum);
    }

    private List<RcmdAdvertDto> doRecommend(ReqAdvertNewDto reqAdvertNewDto, AdvertAlgEnum advertAlgEnum) {
        try {

            // 检查参数
            this.checkParam(reqAdvertNewDto);

            RecommendType recommendType;

            AbstractAdvertRecommendService abstractAdvertRecommendService;
            // 展示广告
            if (advertAlgEnum instanceof ShowAdvertAlgEnum) {
                recommendType = RecommendType.SHOW;
                abstractAdvertRecommendService = showAdvertRecommendService;
            } else {// 互动广告
                recommendType = RecommendType.INTERACT;
                abstractAdvertRecommendService = interactAdvertRecommendService;
            }

            DBTimeProfile.enter(recommendType.getDesc() + "Recommend:" + String.valueOf(advertAlgEnum.getType()));

            // 记录Cat曲线图
            Cat.logMetricForCount(advertAlgEnum.toString());

            // 准备参数
            AdvertRecommendRequestVo advertRecommendRequestVo = new AdvertRecommendRequestVo();

            // 设置算法策略信息
            advertRecommendRequestVo.setAdvertAlgEnum(advertAlgEnum);
            advertRecommendRequestVo.setRecommendType(recommendType);

            // 基础参数准备.不同的策略参数不同,可查看子类方法实现
            abstractAdvertRecommendService.prepareStrategyParameter(advertRecommendRequestVo);

            List<AdvertNewDto> advertList = reqAdvertNewDto.getAdvertList();
            Boolean invokeWeakFilter = advertRecommendRequestVo.getInvokeWeakFilter();
            RecommendMaterialType recommendMaterialType = advertRecommendRequestVo.getRecommendMaterialType();

            Map<Long, Advert> advertMap = new HashMap<>(advertList.size());
            Set<OrientationPackage> orientationPackages = new HashSet<>();
            Map<Long, Long> timesMap = new HashMap<>(advertList.size());

            // 处理广告数据,比较复杂
            this.handleData(advertList,
                    invokeWeakFilter,
                    recommendMaterialType,
                    advertMap,
                    timesMap,
                    orientationPackages);

            Collection<Advert> adverts = advertMap.values();

            // 如果广告为空,则直接返回
            if (adverts.isEmpty()) {
                return new ArrayList<>();
            }
            // 设置广告相关信息
            advertRecommendRequestVo.setAdvertMap(advertMap);
            advertRecommendRequestVo.setTimesMap(timesMap);
            advertRecommendRequestVo.setAdvertOrientationPackages(orientationPackages);


            // 设置请求相关信息
            AppDo appDo = AppDo.convert(reqAdvertNewDto.getAppDto());
            ConsumerDo consumerDo = ConsumerDo.convert(reqAdvertNewDto.getConsumerDto());
            RequestDo requestDo = RequestDo.convert(reqAdvertNewDto.getRequestDto());
            ActivityDo activityDo = ActivityDo.convert(reqAdvertNewDto.getAdvertActivityDto());

            // 设置媒体信息
            advertRecommendRequestVo.setAppDo(appDo);
            // 设置用户信息
            advertRecommendRequestVo.setConsumerDo(consumerDo);
            // 设置本次请求信息
            advertRecommendRequestVo.setRequestDo(requestDo);
            // 设置活动信息
            advertRecommendRequestVo.setActivityDo(activityDo);

            List<AdvertResortVo> advertResortVos = abstractAdvertRecommendService.recommend(advertRecommendRequestVo);

            return this.returnHandle(advertResortVos, advertRecommendRequestVo);
        } catch (Exception e) {
            LOGGER.error("advert recommend happened error :{}", e);
            return new ArrayList<>();
        } finally {
            DBTimeProfile.release();
        }
    }

    /**
     * @param advertList 配置(附带广告信息,历史遗留)
     */
    public void handleData(Collection<AdvertNewDto> advertList,
                           Boolean invokeWeakFilter,
                           RecommendMaterialType recommendMaterialType,
                           Map<Long, Advert> advertMap,
                           Map<Long, Long> timesMap,
                           Set<OrientationPackage> advertOrientationPackages) {

        advertList.forEach((AdvertNewDto advertDto) -> {

            Long advertId = advertDto.getAdvertId();
            // 如果该配置为免费的.则排除
            if (advertDto.getFee() <= 0L) {
                return;
            }
            // 提取配置包基础数据
            OrientationPackage orientationPackage = OrientationPackage.convert(advertDto);
            // 如果执行若过滤,
            if (invokeWeakFilter && orientationPackage.isWeakTarget()) {
                return;
            }
            // 提取广告数据
            Advert advert = Optional.ofNullable(advertMap.get(advertId)).orElse(Advert.convert(advertDto));

            // 提取素材数据
            Set<Material> materials;
            // 如果不推荐素材.则置空素材
            if (recommendMaterialType.equals(RecommendMaterialType.NONE)) {
                materials = new HashSet<>();
            } else {
                materials = Material.convert(advertDto);
            }

            // 将构建的素材数据添加到配置包中
            orientationPackage.setMaterials(materials);

            // 将获取到的配置包数据添加到广告里
            Set<OrientationPackage> orientationPackages = advert.getOrientationPackages();
            orientationPackages.add(orientationPackage);
            advert.setOrientationPackages(orientationPackages);

            advertOrientationPackages.add(orientationPackage);
            // 广告集合中添加广告
            advertMap.put(advertId, advert);

            // 次数集合添加广告对应的次数
            timesMap.put(advertId, advert.getCurrentLaunchCountToUser());
        });
    }


    private void checkParam(ReqAdvertNewDto req) {
        boolean pass = true;
        try {
            // 媒体信息
            AppDto appDto = req.getAppDto();
            // 用户信息
            ConsumerDto consumerDto = req.getConsumerDto();
            // 活动信息
            AdvertActivityDto advertActivityDto = req.getAdvertActivityDto();

            RequestDto requestDto = req.getRequestDto();
            if (AssertUtil.isAnyEmpty(appDto, consumerDto, advertActivityDto, requestDto)) {
                LOGGER.warn(" paramCheck 0 error, req = [{}], please check the req ", req);
                pass = false;
            }

            Long appId = appDto.getAppId();
            Long consumerId = consumerDto.getConsumerId();
            Long operatingActivityId = advertActivityDto.getOperatingActivityId();


            if (AssertUtil.isAnyEmpty(consumerId, appId, operatingActivityId)) {
                LOGGER.warn(" paramCheck 1 error, req = [{}], please check the req ", req);
                pass = false;
            }

            String ua = requestDto.getUa();
            String ip = requestDto.getIp();
            List<String> orderIds = requestDto.getOrderIds();

            if (AssertUtil.isAnyEmpty(ua, ip, orderIds)) {
                LOGGER.warn("paramCheck 2 error, req = [{}], please check the req ", req);
                pass = false;
            }
        } catch (Exception e) {
            LOGGER.error("paramCheck happened error :{}", e);
            pass = false;
        }

        if (!pass) {
            LOGGER.warn("req is invalid", ResultCodeEnum.PARAMS_INVALID.getDesc());
            throw new RecommendEngineException("req is invalid");
        }
    }


    private List<RcmdAdvertDto> returnHandle(List<AdvertResortVo> advertResortVoList,
                                             AdvertRecommendRequestVo advertRecommendRequestVo) {

        Boolean interactAdvert = advertRecommendRequestVo.getAdvertAlgEnum() instanceof InteractAdvertAlgEnum;
        RequestDo requestDo = advertRecommendRequestVo.getRequestDo();
        List<String> orderIds = requestDo.getOrderIds();
        Long needCount = requestDo.getNeedCount();
        Long startCount = requestDo.getStartCount();
        if (interactAdvert) {
            needCount += 1;
        }

        // 根据该流量是否放弃分组
        Map<Boolean, List<AdvertResortVo>> advertResortVoMap = advertResortVoList.stream().collect(partitioningBy(AdvertResortVo::getGiveUp));

        // 获取被放弃的广告
        List<AdvertResortVo> giveUpAdvertList = advertResortVoMap.getOrDefault(true, new ArrayList<>());

        // 获取没有放弃的广告
        List<AdvertResortVo> onTargetAdvertList = advertResortVoMap.getOrDefault(false, new ArrayList<>());
        if (onTargetAdvertList.isEmpty()) {
            return new ArrayList<>();
        }

        // 根据rankScore分组.选取分数最大中随机的一个配置进行投放
        List<AdvertResortVo> selectAdvert = onTargetAdvertList.stream()
                // 根据rankScore分组
                .collect(groupingBy(AdvertResortVo::getRankScore,
                        // 随机选取其中一个广告
                        collectingAndThen(toList(), this::getRandomOne)))
                .entrySet()
                .stream()
                // 根据rankScore从打到小排序
                .sorted(comparing(Map.Entry<Double, AdvertResortVo>::getKey).reversed())
                // 获取所需要的广告个数
                .limit(needCount)
                .map(Map.Entry::getValue)
                .collect(toList());

        // 获取排名第一广告的 rankScore
        Double topOneRankScore = selectAdvert.get(0).getRankScore();

        // 获取放弃的流量中,rankScore大于命中广告 topOne 的第一条广告
        Optional<AdvertResortVo> giveUpTopOneAdvert = giveUpAdvertList.stream()
                .filter(advertResortVo -> advertResortVo.getTag().equals(1L))
                .filter(advertResortVo -> advertResortVo.getRankScore() >= topOneRankScore)
                .max(comparing(AdvertResortVo::getRankScore));

        // 初始化需要打印日志的广告列表(未放弃的广告的topOne & 放弃广告中排名大于命中广告的 topOne)
        List<AdvertResortVo> needLogAdvertList = new ArrayList<>(selectAdvert.size() + 1);
        giveUpTopOneAdvert.ifPresent(needLogAdvertList::add);

        Map<FeatureIndex, Map<String, String>> featureMap = advertRecommendRequestVo.getFeatureMap();

        List<RcmdAdvertDto> adverts = new ArrayList<>();


        int advertSize = selectAdvert.size();
        if (interactAdvert && advertSize >= 2) {
            advertSize -= 1;
        }

        for (int i = 0; i < advertSize; i++) {
            AdvertResortVo resortVo = selectAdvert.get(i);
            RcmdAdvertDto rcmdAdvertDto = new RcmdAdvertDto();
            rcmdAdvertDto.setOriginalAdvertId(resortVo.getAdvertId());

            // 如果备用广告列表不为空
            if (!resortVo.getBackupAdvertIds().isEmpty()) {
                rcmdAdvertDto.setNeedReplace(true);// 本次广告需要替换

                // 获取该广告的备用广告(只传一个过来)
                Long backupAdvertId = new ArrayList<>(resortVo.getBackupAdvertIds()).get(0);

                // 从所有命中广告获取到该广告,替换
                resortVo = advertResortVoList.stream()
                        .filter(advert -> advert.getAdvertId().equals(backupAdvertId))
                        .findAny()
                        .orElse(resortVo);

            }
            // 设置订单id
            String orderId = orderIds.get(i);
            resortVo.setOrderId(orderId);
            needLogAdvertList.add(resortVo);
            Long advertId = resortVo.getAdvertId();
            Long materialId = resortVo.getMaterialId();
            FeatureIndex featureIndex = new FeatureIndex(advertId, materialId);
            rcmdAdvertDto.setOrderId(orderId);
            rcmdAdvertDto.setAdvertId(advertId);
            rcmdAdvertDto.setPackageId(resortVo.getPackageId());
            rcmdAdvertDto.setMaterialId(materialId);
            rcmdAdvertDto.setCtr(resortVo.getCtr());
            rcmdAdvertDto.setStatCtr(resortVo.getStatCtr());
            rcmdAdvertDto.setPreCtr(resortVo.getPreCtr());
            rcmdAdvertDto.setCvr(resortVo.getCvr());
            rcmdAdvertDto.setStatCvr(resortVo.getStatCvr());
            rcmdAdvertDto.setPreCvr(resortVo.getPreCvr());
            rcmdAdvertDto.setFee(resortVo.getFinalFee());
            rcmdAdvertDto.setTag(resortVo.getTag());
            rcmdAdvertDto.setRecommendApps(advertRecommendRequestVo.getRecommendApps());
            rcmdAdvertDto.setFusingOrientationPackages(advertRecommendRequestVo.getFusingOrientationPackages());

            adverts.add(rcmdAdvertDto);
            // 如果是预推荐或者压测模式.则不打印日志
            if (PerfTestContext.isCurrentInPerfTestMode()) {
                continue;
            }
            long finalStartCount = startCount + i;
            Optional.ofNullable(featureMap.get(featureIndex)).ifPresent(feature -> {
                feature.put("time", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
                feature.put("orderId", orderId);
                feature.put("advertId", String.valueOf(advertId));
                feature.put("f8807", Long.toString(finalStartCount));
                BaseInnerLog.log(feature);
            });
        }

        // todo 优化
        if (advertRecommendRequestVo.getAdvertAlgEnum() instanceof InteractAdvertAlgEnum && selectAdvert.size() >= 2) {
            AdvertResortVo advertResortVo = selectAdvert.get(1);
            RcmdAdvertDto otherAdvert = new RcmdAdvertDto();
            otherAdvert.setAdvertId(advertResortVo.getAdvertId());
            otherAdvert.setPackageId(advertResortVo.getPackageId());
            otherAdvert.setCtr(advertResortVo.getCtr());
            otherAdvert.setCvr(advertResortVo.getCvr());
            adverts.add(otherAdvert);
        }

        Map<Long, NezhaStatDto> nezhaStatDtoMap = advertRecommendRequestVo.getNezhaStatDtoMap();
        Map<Long, Double> ctrReconstructionFactorMap = advertRecommendRequestVo.getCtrReconstructionFactorMap();
        Map<Long, Double> cvrReconstructionFactorMap = advertRecommendRequestVo.getCvrReconstructionFactorMap();
        Map<Long, Double> ctrCorrectionFactorMap = advertRecommendRequestVo.getCtrCorrectionFactorMap();
        Map<Long, Double> cvrCorrectionFactorMap = advertRecommendRequestVo.getCvrCorrectionFactorMap();
        Map<Long, AdvertStatFeatureDo> advertStatFeatureMap = advertRecommendRequestVo.getAdvertStatFeatureMap();
        Map<Long, String> statRedisKeyMap = advertRecommendRequestVo.getStatRedisKeyMap();
        // 需要打印的广告列表中筛选出大于第一名的广告进行打印
        needLogAdvertList.forEach(advertResortVo -> {
            BizLogEntity bizLogEntity = new BizLogEntity();

            Long advertId = advertResortVo.getAdvertId();
            Long packageId = advertResortVo.getPackageId();
            Long materialId = advertResortVo.getMaterialId();
            FeatureIndex featureIndex = new FeatureIndex(advertId, materialId);
            Map<String, String> feature = featureMap.get(featureIndex);
            Double ctr = advertResortVo.getCtr();
            Long finalFee = advertResortVo.getFinalFee();

            // 请求信息
            bizLogEntity.setTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            String orderId = Optional.ofNullable(advertResortVo.getOrderId()).orElse(requestDo.getOrderId());
            bizLogEntity.setOrderId(orderId);
            bizLogEntity.setAlgType(advertRecommendRequestVo.getAdvertAlgEnum().getType());

            // 广告基础信息
            bizLogEntity.setAdvertId(advertId);
            bizLogEntity.setPackageId(packageId);
            bizLogEntity.setMaterialId(materialId);
            bizLogEntity.setFee(finalFee);
            bizLogEntity.setOriginalFee(advertResortVo.getOriginalFee());
            bizLogEntity.setNew(advertResortVo.getNewStatus());
            bizLogEntity.setCount(advertResortVo.getLaunchCountToUser());
            bizLogEntity.setChargeType(advertResortVo.getChargeType());
            bizLogEntity.setDiscountRate(advertResortVo.getDiscountRate());
            bizLogEntity.setAdvertWeight(advertResortVo.getWeight());

            // 媒体信息
            AppDo appDo = advertRecommendRequestVo.getAppDo();
            bizLogEntity.setAppId(appDo.getId());
            bizLogEntity.setSlotIndustryTagId(appDo.getSlotIndustryTagId());
            bizLogEntity.setSlotIndustryTagPid(appDo.getSlotIndustryTagPid());
            bizLogEntity.setAppIndustryTagId(appDo.getIndustryTagId());
            bizLogEntity.setAppIndustryTagPid(appDo.getIndustryTagPid());
            bizLogEntity.setTrafficTagId(appDo.getTrafficTagId());
            bizLogEntity.setTrafficTagPid(appDo.getTrafficTagPid());

            // 统计信息
            bizLogEntity.setFactor(advertResortVo.getFactor());
            bizLogEntity.setStatCtr(advertResortVo.getStatCtr());
            bizLogEntity.setStatCvr(advertResortVo.getStatCvr());
            bizLogEntity.setPreCtr(advertResortVo.getPreCtr());
            bizLogEntity.setPreCvr(advertResortVo.getPreCvr());
            bizLogEntity.setPreBackendCvr(advertResortVo.getPreBackendCvr());
            bizLogEntity.setBackendType(advertResortVo.getBackendType());
            bizLogEntity.setBackendFactor(advertResortVo.getBackendFactor());
            bizLogEntity.setStatRedisKey(statRedisKeyMap.get(advertId));

            bizLogEntity.setCtr(ctr);
            bizLogEntity.setCvr(advertResortVo.getCvr());

            bizLogEntity.setBudgetType(advertResortVo.getBudgetType());
            bizLogEntity.setBudgetRatio(advertResortVo.getBudgetRatio());
            bizLogEntity.setArpu(BigDecimal.valueOf(finalFee * ctr).setScale(2, RoundingMode.HALF_UP).doubleValue());
            bizLogEntity.setNotFreeAdvertNum(advertRecommendRequestVo.getAdvertMap().size());
            bizLogEntity.setBiddingAdvertNum(advertRecommendRequestVo.getAdvertMap().size());
            bizLogEntity.setTagWeight(advertResortVo.getTagWeight());

            // 流量信息
            bizLogEntity.setTag(advertResortVo.getTag());
            bizLogEntity.setQualityLevel(advertResortVo.getQualityLevel());

            // 排序信息
            bizLogEntity.setRankScore(advertResortVo.getRankScore());
            bizLogEntity.setqScore(advertResortVo.getqScore());
            bizLogEntity.setRank(advertResortVo.getRank());

            // 特征信息
            bizLogEntity.setCvrFeatureMap(JSON.toJSONString(feature));

            // 活动信息
            bizLogEntity.setActivityId(advertRecommendRequestVo.getActivityDo().getOperatingId());
            Integer activityUseType = advertRecommendRequestVo.getActivityDo().getUseType();
            Long slotId = appDo.getSlotId();
            if (activityUseType.equals(0) || activityUseType.equals(1)) {
                bizLogEntity.setDuibaSlotId(slotId);
            } else {
                bizLogEntity.setSlotId(slotId);
            }
            bizLogEntity.setCtrCorrectionFactor(ctrCorrectionFactorMap.get(advertId));
            bizLogEntity.setCvrCorrectionFactor(cvrCorrectionFactorMap.get(advertId));
            bizLogEntity.setCtrReconstructionFactor(ctrReconstructionFactorMap.get(advertId));
            bizLogEntity.setCvrReconstructionFactor(cvrReconstructionFactorMap.get(advertId));

            Optional.ofNullable(nezhaStatDtoMap.get(advertId)).ifPresent(nezhaStatDto -> {
                bizLogEntity.setPreCtrAvg(nezhaStatDto.getPreCtrAvg());
                bizLogEntity.setPreCvrAvg(nezhaStatDto.getPreCvrAvg());
                bizLogEntity.setStatCtrAvg(nezhaStatDto.getStatCtrAvg());
                bizLogEntity.setStatCvrAvg(nezhaStatDto.getStatCvrAvg());
            });

            Optional.ofNullable(advertStatFeatureMap.get(advertId)).ifPresent(statFeatureDo -> {
                bizLogEntity.setAdvertCtr(statFeatureDo.getAdvertCtr());
                bizLogEntity.setAdvertCvr(statFeatureDo.getAdvertCvr());
                bizLogEntity.setAdvertAppCtr(statFeatureDo.getAdvertAppCtr());
                bizLogEntity.setAdvertAppCvr(statFeatureDo.getAdvertAppCvr());
                bizLogEntity.setAdvertSlotCtr(statFeatureDo.getAdvertSlotCtr());
                bizLogEntity.setAdvertSlotCvr(statFeatureDo.getAdvertSlotCvr());
                bizLogEntity.setAdvertActivityCtr(statFeatureDo.getAdvertActivityCtr());
                bizLogEntity.setAdvertActivityCvr(statFeatureDo.getAdvertActivityCvr());
            });
            if (PerfTestContext.isCurrentInPerfTestMode()) {
                return;
            }
            BaseInnerLog.log(bizLogEntity);
        });
        return adverts;

    }

    private <T> T getRandomOne(List<T> list) {
        if (list.size() == 1) {
            return list.get(0);
        }
        return list.get(new Random().nextInt(list.size()));
    }
}

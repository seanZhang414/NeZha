package cn.com.duiba.nezha.engine.biz.bo.hbase;

import cn.com.duiba.nezha.alg.feature.vo.FeatureDo;
import cn.com.duiba.nezha.engine.api.dto.TagStat;
import cn.com.duiba.nezha.engine.biz.domain.*;
import cn.com.duiba.nezha.engine.biz.entity.nezha.advert.AppInstallFeature;
import cn.com.duiba.nezha.engine.biz.service.advert.feature.ActivityFeatureService;
import cn.com.duiba.nezha.engine.biz.service.advert.feature.ConsumerAppInstallService;
import cn.com.duiba.nezha.engine.biz.service.advert.feature.ConsumerFeatureService;
import cn.com.duiba.nezha.engine.common.utils.MyObjectUtil;
import com.google.common.base.Joiner;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by pc on 2017/2/27.
 */
@Service
public class ConsumerFeatureBo {

    @Autowired
    private ConsumerFeatureService consumerFeatureService;

    @Autowired
    private ConsumerAppInstallService consumerAppInstallService;

    @Autowired
    private ActivityFeatureService activityFeatureService;

    public FeatureDo getFeatureDo(ConsumerDo consumerDo, AppDo appDo, ActivityDo activityDo, RequestDo requestDo) {
        FeatureDo featureDo = new FeatureDo();

        //用户参数
        featureDo.setConsumerId(consumerDo.getId());
        featureDo.setMemberId(consumerDo.getMemberId() == null ? null : String.valueOf(consumerDo.getMemberId()));
        featureDo.setShipArea(consumerDo.getShipArea());
        featureDo.setUserLastlogbigintime(consumerDo.getLastLoginTime());
        featureDo.setUserRegtime(consumerDo.getRegisterTime());
        featureDo.setMobile(consumerDo.getPhoneNumber());


        //app参数
        featureDo.setAppId(appDo.getId());
        featureDo.setAppCategory(appDo.getCategory());
        featureDo.setSlotId(appDo.getSlotId());
        featureDo.setSlotType(appDo.getSlotType());
        featureDo.setSlotWidth(appDo.getSlotWidth());
        featureDo.setSlotHeight(appDo.getSlotHeight());

        featureDo.setSlotIndustryTagId(appDo.getSlotIndustryTagId());
        featureDo.setSlotIndustryTagPid(appDo.getSlotIndustryTagPid());
        featureDo.setAppIndustryTagId(appDo.getIndustryTagId());
        featureDo.setAppIndustryTagPid(appDo.getIndustryTagPid());
        featureDo.setTrafficTagId(appDo.getTrafficTagId());
        featureDo.setTrafficTagPid(appDo.getTrafficTagPid());

        //活动参数
        Long operatingActivityId = activityDo.getOperatingId();
        ActivityFeatureDo activityFeatureDo = activityFeatureService.get(operatingActivityId);
        featureDo.setOperatingActivityId(operatingActivityId);
        featureDo.setActivityUseType(activityDo.getUseType() == null ? null : String.valueOf
                (activityDo.getUseType()));
        featureDo.setActivityId(activityDo.getId());
        featureDo.setActivityType(activityDo.getType());
        featureDo.setPerformance(activityFeatureDo.getPerformance());
        featureDo.setLaunchInfo(activityFeatureDo.getLaunchInfo());
        featureDo.setBasicInfo(activityFeatureDo.getBasicInfo());
        featureDo.setTotalInfo(activityFeatureDo.getTotalInfo());

        //请求参数
        featureDo.setUa(requestDo.getUa());
        featureDo.setCityId(requestDo.getCityId());
        featureDo.setPriceSection(requestDo.getPriceSection());
        featureDo.setPutIndex(requestDo.getPutIndex());
        featureDo.setModel(requestDo.getModel());
        featureDo.setConnectionType(requestDo.getConnectionType());
        featureDo.setOperatorType(requestDo.getOperatorType());
        featureDo.setPhoneBrand(requestDo.getPhoneBrand());
        featureDo.setPhoneModelNum(requestDo.getPhoneModel());
        featureDo.setAlgStartPosition(requestDo.getStartCount());

        List<String> keys = new ArrayList<>();
        Long consumerId = featureDo.getConsumerId();
        keys.add(getConsumerOrderFeatureKey(consumerId, null));
        keys.add(getConsumerOrderFeatureKey(consumerId, operatingActivityId));

        Map<String, ConsumerFeatureDO> featureVos = consumerFeatureService.getFeatures(keys);


        // 2 实时计算特征-用户全局粒度
        ConsumerFeatureDO globalFeatureDO = this.getConsumerOrderFeatureDo(consumerId, null, featureVos);
        featureDo.setDayOrderRank(getRealRank(globalFeatureDO.getCurrentDayOrderCount()));
        featureDo.setOrderRank(getRealRank(globalFeatureDO.getOrderCount()));
        featureDo.setLastGmtCreateTime(globalFeatureDO.getLastOrderTime());
        featureDo.setLastChargeNums(MyObjectUtil.string2long(globalFeatureDO.getIsBilling()));
        featureDo.setLastOperatingActivityId(MyObjectUtil.string2long(globalFeatureDO.getLastActivityId()));

        // 2 实时计算特征-用户活动粒度
        ConsumerFeatureDO activityFeatureDO = this.getConsumerOrderFeatureDo(consumerId, operatingActivityId, featureVos);
        featureDo.setDayActivityOrderRank(getRealRank(activityFeatureDO.getCurrentDayOrderCount()));
        featureDo.setActivityOrderRank(getRealRank(activityFeatureDO.getOrderCount()));
        featureDo.setActivityLastGmtCreateTime(activityFeatureDO.getLastOrderTime());
        featureDo.setActivityLastChargeNums(MyObjectUtil.string2long(activityFeatureDO.getIsBilling()));

        Joiner joiner = Joiner.on(",");

        //app安装情况
        List<String> installApps = consumerDo.getInstallApps();
        if (CollectionUtils.isNotEmpty(installApps)) {
            AppInstallFeature installFeature = consumerAppInstallService.getFeature(installApps);

            featureDo.setAppList2(joiner.join(installApps));
            featureDo.setCategoryIdList1(joiner.join(installFeature.getFirstCategory()));
            featureDo.setCategoryIdList2(joiner.join(installFeature.getSecondCategory()));
            featureDo.setCategory1idCntList(installFeature.getFirstCategoryCount());
            featureDo.setCategory2idCntList(installFeature.getSecondCategoryCount());
            featureDo.setIsGame(installFeature.getHasGame() ? "1" : "0");
            featureDo.setClusterId(joiner.join(installFeature.getClusterIdList()));
            featureDo.setImportantApp(joiner.join(installFeature.getImportantAppList()));
        }


        //用户行为特征
        List<TagStat> tagStats = consumerDo.getTagStats();
        if (CollectionUtils.isNotEmpty(tagStats)) {
            List<String> tagIds = new ArrayList<>(tagStats.size());
            List<Double> scores = new ArrayList<>(tagStats.size());
            List<Long> launchs = new ArrayList<>(tagStats.size());
            List<Long> clicks = new ArrayList<>(tagStats.size());
            List<Long> converts = new ArrayList<>(tagStats.size());

            for (TagStat tagStat : tagStats) {
                tagIds.add(tagStat.getTagId());
                scores.add(tagStat.getScore());
                launchs.add(tagStat.getLaunch());
                clicks.add(tagStat.getClick());
                converts.add(tagStat.getConvert());
            }

            featureDo.setUIIds(joiner.join(tagIds));
            featureDo.setUIScore(joiner.join(scores));
            featureDo.setUILaunchPV(joiner.join(launchs));
            featureDo.setUIClickPv(joiner.join(clicks));
            featureDo.setUIEffectPv(joiner.join(converts));
        }

        featureDo.setUICtr(consumerDo.getClickInterestedTags());
        featureDo.setUUnICtr(consumerDo.getClickUninterestedTags());
        featureDo.setUICvr(consumerDo.getConvertInterestedTags());
        featureDo.setUUnICvr(consumerDo.getConvertUninterestedTags());


        featureDo.setSex(consumerDo.getSex());
        featureDo.setAge(consumerDo.getAge());
        featureDo.setWorkStatus(consumerDo.getWorkStatus());
        featureDo.setStudentStatus(consumerDo.getStudentStatus());
        featureDo.setMarriageStatus(consumerDo.getMarriageStatus());
        featureDo.setBear(consumerDo.getBear());
        if (CollectionUtils.isNotEmpty(consumerDo.getInterestedList())) {
            featureDo.setInterestList(joiner.join(consumerDo.getInterestedList()));
        }

        return featureDo;

    }


    private ConsumerFeatureDO getConsumerOrderFeatureDo(Long consumerId,
                                                        Long activityId,
                                                        Map<String, ConsumerFeatureDO> featureDtoMap) {

        String mainKey = getConsumerOrderFeatureKey(consumerId, activityId);
        return featureDtoMap.getOrDefault(mainKey, new ConsumerFeatureDO());
    }

    private Long getRealRank(Long longVal) {
       return Optional.ofNullable(longVal).map(v -> v + 1).orElse(1L);
    }

    public String getConsumerOrderFeatureKey(Long consumerId, Long activityId) {
        //将consumerId反转，解决查询热点问题
        StringBuilder sb = new StringBuilder().append(consumerId).reverse();

        if (activityId != null) {
            sb.append("-").append(activityId);
        }

        return sb.toString();

    }


}

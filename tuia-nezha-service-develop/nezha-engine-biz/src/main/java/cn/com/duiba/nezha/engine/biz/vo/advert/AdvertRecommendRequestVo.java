package cn.com.duiba.nezha.engine.biz.vo.advert;

import cn.com.duiba.nezha.alg.alg.vo.BackendAdvertStatDo;
import cn.com.duiba.nezha.alg.alg.vo.NezhaStatDto;
import cn.com.duiba.nezha.alg.alg.vo.StatDo;
import cn.com.duiba.nezha.engine.api.dto.FusingOrientationPackageDto;
import cn.com.duiba.nezha.engine.api.dto.RecommendAppDto;
import cn.com.duiba.nezha.engine.api.enums.*;
import cn.com.duiba.nezha.engine.biz.domain.*;
import cn.com.duiba.nezha.engine.biz.domain.advert.Advert;
import cn.com.duiba.nezha.engine.biz.domain.advert.OrientationPackage;
import cn.com.duiba.nezha.engine.biz.enums.RecommendMaterialType;

import java.util.*;

public class AdvertRecommendRequestVo {

    // 用户信息
    private ConsumerDo consumerDo;

    // engine的请求信息
    private RequestDo requestDo;

    // 请求的媒体信息
    private AppDo appDo;

    // 请求的活动信息
    private ActivityDo activityDo;

    // 算法类型
    private AdvertAlgEnum advertAlgEnum;

    // 推荐类型 展示广告或者互动广告
    private RecommendType recommendType;

    // 权重对象
    private AdvertStatDimWeightVo advertStatDimWeightVo;

    // CVR LR模型KEY
    private ModelKeyEnum cvrModelKey;

    // CTR LR模型KEY
    private ModelKeyEnum ctrModelKey;

    private DeepTfServer deepCtrModelKey;

    private DeepTfServer deepCvrModelKey;

    // 后端 CVR LR 模型 Key
    private ModelKeyEnum backendCtrModelKey;

    // 素材推荐方式
    private RecommendMaterialType recommendMaterialType;

    // 预估纠偏类型
    private PredictCorrectType predictCorrectType;

    // 广告多维度质量分是否启用
    private Boolean advertMultiDimScoreEffective;

    //是否启动弱条件过滤
    private Boolean invokeWeakFilter;

    // 广告媒体融合数据
    private List<StatDo> advertAppMergeStatDoList;

    // 广告全局融合数据
    private List<StatDo> advertGlobalMergeStatDoList;

    // 广告对应的发券次数
    private Map<Long, Long> timesMap;

    private Set<Long> advertIds;

    private Map<Long, Advert> advertMap;

    private Set<OrientationPackage> advertOrientationPackages;

    private Map<Long, Integer> userAdvertBehaviorMap;

    private Map<Long, Double> ctrReconstructionFactorMap;

    private Map<Long, Double> cvrReconstructionFactorMap;

    private Map<FeatureIndex, Double> predictCtr;

    private Map<FeatureIndex, Double> predictCvr;

    private Map<Long, Double> predictBackendCvr;

    private Map<Long, BackendAdvertStatDo> backendAdvertStatMap;

    private Map<Long, Double> ctrCorrectionFactorMap;

    private Map<Long, Double> cvrCorrectionFactorMap;

    private Map<FeatureIndex, Map<String, String>> featureMap;

    private Map<Long, NezhaStatDto> nezhaStatDtoMap;

    private Map<Long, AdvertStatFeatureDo> advertStatFeatureMap;

    private Map<Long, String> statRedisKeyMap;

    private Boolean needPredict;

    // 推荐(定向/屏蔽)的媒体
    private Set<RecommendAppDto> recommendApps;

    // 需要熔断的配置包
    private Set<FusingOrientationPackageDto> fusingOrientationPackages;

    public Set<RecommendAppDto> getRecommendApps() {
        return recommendApps;
    }

    public void setRecommendApps(Set<RecommendAppDto> recommendApps) {
        this.recommendApps = recommendApps;
    }

    public Set<FusingOrientationPackageDto> getFusingOrientationPackages() {
        return fusingOrientationPackages;
    }

    public void setFusingOrientationPackages(Set<FusingOrientationPackageDto> fusingOrientationPackages) {
        this.fusingOrientationPackages = fusingOrientationPackages;
    }

    public Map<Long, String> getStatRedisKeyMap() {
        return statRedisKeyMap;
    }

    public RecommendType getRecommendType() {
        return recommendType;
    }

    public void setRecommendType(RecommendType recommendType) {
        this.recommendType = recommendType;
    }

    public Boolean getNeedPredict() {
        return needPredict;
    }

    public void setNeedPredict(Boolean needPredict) {
        this.needPredict = needPredict;
    }

    public void setStatRedisKeyMap(Map<Long, String> statRedisKeyMap) {
        this.statRedisKeyMap = statRedisKeyMap;
    }

    public Map<Long, AdvertStatFeatureDo> getAdvertStatFeatureMap() {
        return Optional.ofNullable(advertStatFeatureMap).orElseGet(HashMap::new);
    }

    public void setAdvertStatFeatureMap(Map<Long, AdvertStatFeatureDo> advertStatFeatureMap) {
        this.advertStatFeatureMap = advertStatFeatureMap;
    }

    public Map<Long, NezhaStatDto> getNezhaStatDtoMap() {
        return Optional.ofNullable(nezhaStatDtoMap).orElseGet(HashMap::new);
    }

    public void setNezhaStatDtoMap(Map<Long, NezhaStatDto> nezhaStatDtoMap) {
        this.nezhaStatDtoMap = nezhaStatDtoMap;
    }

    public Map<FeatureIndex, Map<String, String>> getFeatureMap() {
        return Optional.ofNullable(featureMap).orElseGet(HashMap::new);
    }

    public void setFeatureMap(Map<FeatureIndex, Map<String, String>> featureMap) {
        this.featureMap = featureMap;
    }

    public Map<Long, Double> getCvrCorrectionFactorMap() {
        return Optional.ofNullable(cvrCorrectionFactorMap).orElseGet(HashMap::new);
    }

    public void setCvrCorrectionFactorMap(Map<Long, Double> cvrCorrectionFactorMap) {
        this.cvrCorrectionFactorMap = cvrCorrectionFactorMap;
    }

    public Map<Long, Double> getCtrCorrectionFactorMap() {
        return Optional.ofNullable(ctrCorrectionFactorMap).orElseGet(HashMap::new);
    }

    public void setCtrCorrectionFactorMap(Map<Long, Double> ctrCorrectionFactorMap) {
        this.ctrCorrectionFactorMap = ctrCorrectionFactorMap;
    }

    public ConsumerDo getConsumerDo() {
        return consumerDo;
    }

    public void setConsumerDo(ConsumerDo consumerDo) {
        this.consumerDo = consumerDo;
    }

    public RequestDo getRequestDo() {
        return requestDo;
    }

    public void setRequestDo(RequestDo requestDo) {
        this.requestDo = requestDo;
    }

    public AppDo getAppDo() {
        return appDo;
    }

    public void setAppDo(AppDo appDo) {
        this.appDo = appDo;
    }

    public ActivityDo getActivityDo() {
        return activityDo;
    }

    public void setActivityDo(ActivityDo activityDo) {
        this.activityDo = activityDo;
    }

    public AdvertAlgEnum getAdvertAlgEnum() {
        return advertAlgEnum;
    }

    public void setAdvertAlgEnum(AdvertAlgEnum advertAlgEnum) {
        this.advertAlgEnum = advertAlgEnum;
    }

    public AdvertStatDimWeightVo getAdvertStatDimWeightVo() {
        return advertStatDimWeightVo;
    }

    public void setAdvertStatDimWeightVo(AdvertStatDimWeightVo advertStatDimWeightVo) {
        this.advertStatDimWeightVo = advertStatDimWeightVo;
    }

    public ModelKeyEnum getCvrModelKey() {
        return cvrModelKey;
    }

    public void setCvrModelKey(ModelKeyEnum cvrModelKey) {
        this.cvrModelKey = cvrModelKey;
    }

    public ModelKeyEnum getCtrModelKey() {
        return ctrModelKey;
    }

    public void setCtrModelKey(ModelKeyEnum ctrModelKey) {
        this.ctrModelKey = ctrModelKey;
    }

    public DeepTfServer getDeepCtrModelKey() {
        return deepCtrModelKey;
    }

    public void setDeepCtrModelKey(DeepTfServer deepCtrModelKey) {
        this.deepCtrModelKey = deepCtrModelKey;
    }

    public DeepTfServer getDeepCvrModelKey() {
        return deepCvrModelKey;
    }

    public void setDeepCvrModelKey(DeepTfServer deepCvrModelKey) {
        this.deepCvrModelKey = deepCvrModelKey;
    }

    public ModelKeyEnum getBackendCtrModelKey() {
        return backendCtrModelKey;
    }

    public void setBackendCtrModelKey(ModelKeyEnum backendCtrModelKey) {
        this.backendCtrModelKey = backendCtrModelKey;
    }

    public RecommendMaterialType getRecommendMaterialType() {
        return recommendMaterialType;
    }

    public void setRecommendMaterialType(RecommendMaterialType recommendMaterialType) {
        this.recommendMaterialType = recommendMaterialType;
    }

    public PredictCorrectType getPredictCorrectType() {
        return predictCorrectType;
    }

    public void setPredictCorrectType(PredictCorrectType predictCorrectType) {
        this.predictCorrectType = predictCorrectType;
    }

    public Boolean getAdvertMultiDimScoreEffective() {
        return advertMultiDimScoreEffective;
    }

    public void setAdvertMultiDimScoreEffective(Boolean advertMultiDimScoreEffective) {
        this.advertMultiDimScoreEffective = advertMultiDimScoreEffective;
    }

    public Boolean getInvokeWeakFilter() {
        return invokeWeakFilter;
    }

    public void setInvokeWeakFilter(Boolean invokeWeakFilter) {
        this.invokeWeakFilter = invokeWeakFilter;
    }

    public List<StatDo> getAdvertAppMergeStatDoList() {
        return advertAppMergeStatDoList;
    }

    public void setAdvertAppMergeStatDoList(List<StatDo> advertAppMergeStatDoList) {
        this.advertAppMergeStatDoList = advertAppMergeStatDoList;
    }

    public List<StatDo> getAdvertGlobalMergeStatDoList() {
        return advertGlobalMergeStatDoList;
    }

    public void setAdvertGlobalMergeStatDoList(List<StatDo> advertGlobalMergeStatDoList) {
        this.advertGlobalMergeStatDoList = advertGlobalMergeStatDoList;
    }

    public Map<Long, Long> getTimesMap() {
        return timesMap;
    }

    public void setTimesMap(Map<Long, Long> timesMap) {
        this.timesMap = timesMap;
    }

    public Set<Long> getAdvertIds() {
        return advertIds;
    }

    public void setAdvertIds(Set<Long> advertIds) {
        this.advertIds = advertIds;
    }

    public Map<Long, Advert> getAdvertMap() {
        return advertMap;
    }

    public void setAdvertMap(Map<Long, Advert> advertMap) {
        this.advertMap = advertMap;
    }

    public Set<OrientationPackage> getAdvertOrientationPackages() {
        return advertOrientationPackages;
    }

    public void setAdvertOrientationPackages(Set<OrientationPackage> advertOrientationPackages) {
        this.advertOrientationPackages = advertOrientationPackages;
    }

    public Map<Long, Integer> getUserAdvertBehaviorMap() {
        return Optional.ofNullable(userAdvertBehaviorMap).orElseGet(HashMap::new);
    }

    public void setUserAdvertBehaviorMap(Map<Long, Integer> userAdvertBehaviorMap) {
        this.userAdvertBehaviorMap = userAdvertBehaviorMap;
    }

    public Map<Long, Double> getCtrReconstructionFactorMap() {
        return Optional.ofNullable(ctrReconstructionFactorMap).orElseGet(HashMap::new);
    }

    public void setCtrReconstructionFactorMap(Map<Long, Double> ctrReconstructionFactorMap) {
        this.ctrReconstructionFactorMap = ctrReconstructionFactorMap;
    }

    public Map<Long, Double> getCvrReconstructionFactorMap() {
        return Optional.ofNullable(cvrReconstructionFactorMap).orElseGet(HashMap::new);
    }

    public void setCvrReconstructionFactorMap(Map<Long, Double> cvrReconstructionFactorMap) {
        this.cvrReconstructionFactorMap = cvrReconstructionFactorMap;
    }

    public Map<FeatureIndex, Double> getPredictCtr() {
        return Optional.ofNullable(predictCtr).orElseGet(HashMap::new);
    }

    public void setPredictCtr(Map<FeatureIndex, Double> predictCtr) {
        this.predictCtr = predictCtr;
    }

    public Map<FeatureIndex, Double> getPredictCvr() {
        return Optional.ofNullable(predictCvr).orElseGet(HashMap::new);
    }

    public void setPredictCvr(Map<FeatureIndex, Double> predictCvr) {
        this.predictCvr = predictCvr;
    }

    public Map<Long, Double> getPredictBackendCvr() {
        return Optional.ofNullable(predictBackendCvr).orElseGet(HashMap::new);
    }

    public void setPredictBackendCvr(Map<Long, Double> predictBackendCvr) {
        this.predictBackendCvr = predictBackendCvr;
    }

    public Map<Long, BackendAdvertStatDo> getBackendAdvertStatMap() {
        return Optional.ofNullable(backendAdvertStatMap).orElseGet(HashMap::new);
    }

    public void setBackendAdvertStatMap(Map<Long, BackendAdvertStatDo> backendAdvertStatMap) {
        this.backendAdvertStatMap = backendAdvertStatMap;
    }
}

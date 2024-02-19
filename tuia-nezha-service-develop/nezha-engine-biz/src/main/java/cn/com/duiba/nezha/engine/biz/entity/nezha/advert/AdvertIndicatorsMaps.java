package cn.com.duiba.nezha.engine.biz.entity.nezha.advert;

import cn.com.duiba.nezha.alg.alg.vo.BackendAdvertStatDo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 广告指标映射集合
 *
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: AdvertIndicatorsMaps.java , v 0.1 2017/12/15 下午3:03 ZhouFeng Exp $
 */
public class AdvertIndicatorsMaps {

    /**
     * 预估CTR
     * key:advertId
     */
    private Map<Long, Double> predictedCtr;

    /**
     * 预估CVR
     * key:advertId
     */
    private Map<Long, Double> predictedCvr;

    // 后端预估cvr
    private Map<Long, Double> predictedBackendCvr;

    // 后端数据
    private Map<Long, BackendAdvertStatDo> backendAdvertStatInfo;

    /**
     * ctr 分布重构系数
     */
    private Map<Long, Double> ctrReconstructionFactorMap;


    /**
     * cvr 分布重构系数
     */
    private Map<Long, Double> cvrReconstructionFactorMap;

    /**
     * 用户对广告的偏好
     * key为广告ID，value为用户的行为偏好
     */
    private Map<Long, Integer> userAdvertBehaviorMap;

    private AdvertIndicatorsMaps(Builder builder) {
        predictedCtr = builder.predictedCtr;
        predictedCvr = builder.predictedCvr;
        predictedBackendCvr = builder.predictedBackendCvr;
        backendAdvertStatInfo = builder.backendAdvertStatInfo;
        ctrReconstructionFactorMap = builder.ctrReconstructionFactorMap;
        cvrReconstructionFactorMap = builder.cvrReconstructionFactorMap;
        userAdvertBehaviorMap = builder.userAdvertBehaviorMap;
    }

    public Map<Long, Double> getPredictedBackendCvr() {
        return Optional.ofNullable(predictedBackendCvr).orElseGet(HashMap::new);
    }

    public Map<Long, BackendAdvertStatDo> getBackendAdvertStatInfo() {
        return Optional.ofNullable(backendAdvertStatInfo).orElseGet(HashMap::new);
    }

    public Map<Long, Double> getPredictedCtr() {
        return Optional.ofNullable(predictedCtr).orElse(new HashMap<>());
    }

    public Map<Long, Double> getPredictedCvr() {
        return Optional.ofNullable(predictedCvr).orElse(new HashMap<>());
    }

    public Map<Long, Double> getCtrReconstructionFactorMap() {
        return Optional.ofNullable(ctrReconstructionFactorMap).orElse(new HashMap<>());
    }

    public Map<Long, Double> getCvrReconstructionFactorMap() {
        return Optional.ofNullable(cvrReconstructionFactorMap).orElse(new HashMap<>());
    }

    public Map<Long, Integer> getUserAdvertBehaviorMap() {
        return Optional.ofNullable(userAdvertBehaviorMap).orElse(new HashMap<>());
    }

    public static final class Builder {
        private Map<Long, Double> predictedCtr;
        private Map<Long, Double> predictedCvr;
        private Map<Long, Double> predictedBackendCvr;
        private Map<Long, BackendAdvertStatDo> backendAdvertStatInfo;
        private Map<Long, Double> ctrReconstructionFactorMap;
        private Map<Long, Double> cvrReconstructionFactorMap;
        private Map<Long, Integer> userAdvertBehaviorMap;

        public Builder() {
            // builder
        }


        public Builder predictedCtr(Map<Long, Double> val) {
            predictedCtr = val;
            return this;
        }

        public Builder predictedCvr(Map<Long, Double> val) {
            predictedCvr = val;
            return this;
        }

        public Builder predictedBackendCvr(Map<Long, Double> val) {
            predictedBackendCvr = val;
            return this;
        }

        public Builder backendAdvertStatInfo(Map<Long, BackendAdvertStatDo> val) {
            backendAdvertStatInfo = val;
            return this;
        }

        public Builder ctrReconstructionFactorMap(Map<Long, Double> val) {
            ctrReconstructionFactorMap = val;
            return this;
        }

        public Builder cvrReconstructionFactorMap(Map<Long, Double> val) {
            cvrReconstructionFactorMap = val;
            return this;
        }

        public Builder userAdvertBehaviorMap(Map<Long, Integer> val) {
            userAdvertBehaviorMap = val;
            return this;
        }

        public AdvertIndicatorsMaps build() {
            return new AdvertIndicatorsMaps(this);
        }
    }
}

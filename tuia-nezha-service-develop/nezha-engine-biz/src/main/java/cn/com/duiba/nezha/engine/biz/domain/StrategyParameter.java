package cn.com.duiba.nezha.engine.biz.domain;

import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertStatDimWeightVo;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: StrategyParameter.java , v 0.1 2017/11/8 下午4:28 ZhouFeng Exp $
 */
public class StrategyParameter {

    /**
     * 广告统计维度权重
     */
    private AdvertStatDimWeightVo advertStatDimWeight;

    /**
     * 广告权重是否生效
     */
    private Boolean advertWeightEffective;

    /**
     * 广告多维度质量分是否启用
     */
    private Boolean advertMultiDimScoreEffective;

    private StrategyParameter(Builder builder) {
        advertStatDimWeight = builder.advertStatDimWeightVo;
        advertWeightEffective = builder.advertWeightEffective;
        advertMultiDimScoreEffective = builder.advertMultiDimScoreEffective;
    }

    public AdvertStatDimWeightVo getAdvertStatDimWeight() {
        return advertStatDimWeight;
    }

    public Boolean getAdvertWeightEffective() {
        return advertWeightEffective;
    }

    public Boolean getAdvertMultiDimScoreEffective() {
        return advertMultiDimScoreEffective;
    }

    public static final class Builder {

        private AdvertStatDimWeightVo advertStatDimWeightVo;
        private Boolean advertWeightEffective;
        private Boolean advertMultiDimScoreEffective;

        public Builder() {
            // builder
        }

        public Builder advertStatDimWeightVo(AdvertStatDimWeightVo val) {
            advertStatDimWeightVo = val;
            return this;
        }

        public Builder advertWeightEffective(Boolean val) {
            advertWeightEffective = val;
            return this;
        }

        public Builder advertMultiDimScoreEffective(Boolean val) {
            advertMultiDimScoreEffective = val;
            return this;
        }

        public StrategyParameter build() {
            return new StrategyParameter(this);
        }
    }
}

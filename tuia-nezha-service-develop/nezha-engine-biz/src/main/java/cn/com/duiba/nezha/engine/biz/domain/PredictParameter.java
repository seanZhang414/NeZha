package cn.com.duiba.nezha.engine.biz.domain;

import cn.com.duiba.nezha.engine.api.enums.DeepTfServer;
import cn.com.duiba.nezha.engine.api.enums.ModelKeyEnum;
import cn.com.duiba.nezha.engine.api.enums.StatDataTypeEnum;
import cn.com.duiba.nezha.engine.biz.domain.advert.Advert;

import java.util.Collection;
import java.util.Map;

/**
 * 预估参数
 */
public class PredictParameter {
    private Map<StatDataTypeEnum, ModelKeyEnum> modelKeyMap;
    private Map<StatDataTypeEnum, DeepTfServer> deepModelKeyMap;
    private Map<StatDataTypeEnum, Collection<Advert>> needPredictAdvertMap;

    private PredictParameter(Builder builder) {
        setModelKeyMap(builder.modelKeyMap);
        setDeepModelKeyMap(builder.deepModelKeyMap);
        setNeedPredictAdvertMap(builder.needPredictAdvertMap);
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public Map<StatDataTypeEnum, ModelKeyEnum> getModelKeyMap() {
        return modelKeyMap;
    }

    public void setModelKeyMap(Map<StatDataTypeEnum, ModelKeyEnum> modelKeyMap) {
        this.modelKeyMap = modelKeyMap;
    }

    public Map<StatDataTypeEnum, DeepTfServer> getDeepModelKeyMap() {
        return deepModelKeyMap;
    }

    public void setDeepModelKeyMap(Map<StatDataTypeEnum, DeepTfServer> deepModelKeyMap) {
        this.deepModelKeyMap = deepModelKeyMap;
    }

    public Map<StatDataTypeEnum, Collection<Advert>> getNeedPredictAdvertMap() {
        return needPredictAdvertMap;
    }

    public void setNeedPredictAdvertMap(Map<StatDataTypeEnum, Collection<Advert>> needPredictAdvertMap) {
        this.needPredictAdvertMap = needPredictAdvertMap;
    }

    public static final class Builder {
        private Map<StatDataTypeEnum, ModelKeyEnum> modelKeyMap;
        private Map<StatDataTypeEnum, DeepTfServer> deepModelKeyMap;
        private Map<StatDataTypeEnum, Collection<Advert>> needPredictAdvertMap;

        private Builder() {
        }

        public Builder modelKeyMap(Map<StatDataTypeEnum, ModelKeyEnum> val) {
            modelKeyMap = val;
            return this;
        }

        public Builder deepModelKeyMap(Map<StatDataTypeEnum, DeepTfServer> val) {
            deepModelKeyMap = val;
            return this;
        }

        public Builder needPredictAdvertMap(Map<StatDataTypeEnum, Collection<Advert>> val) {
            needPredictAdvertMap = val;
            return this;
        }

        public PredictParameter build() {
            return new PredictParameter(this);
        }
    }
}

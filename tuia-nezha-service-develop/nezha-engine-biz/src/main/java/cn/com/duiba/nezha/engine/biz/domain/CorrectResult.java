package cn.com.duiba.nezha.engine.biz.domain;

import cn.com.duiba.nezha.alg.alg.vo.NezhaStatDto;
import cn.com.duiba.nezha.engine.api.enums.StatDataTypeEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CorrectResult {

    public static final CorrectResult DEFAULT = CorrectResult.newBuilder().build();

    private Map<StatDataTypeEnum, Map<Long, Double>> correctionFactorMap;
    private Map<StatDataTypeEnum, Map<Long, Double>> reconstructionFactorMap;
    private Map<Long, NezhaStatDto> nezhaStatDtoMap;


    private CorrectResult(Builder builder) {
        setCorrectionFactorMap(builder.correctionFactorMap);
        setReconstructionFactorMap(builder.reconstructionFactorMap);
        setNezhaStatDtoMap(builder.nezhaStatDtoMap);
    }


    public static Builder newBuilder() {
        return new Builder();
    }

    public static CorrectResult getDEFAULT() {
        return DEFAULT;
    }

    public Map<Long, NezhaStatDto> getNezhaStatDtoMap() {
        return Optional.ofNullable(nezhaStatDtoMap).orElseGet(HashMap::new);
    }

    public void setNezhaStatDtoMap(Map<Long, NezhaStatDto> nezhaStatDtoMap) {
        this.nezhaStatDtoMap = nezhaStatDtoMap;
    }

    public Map<StatDataTypeEnum, Map<Long, Double>> getCorrectionFactorMap() {
        return Optional.ofNullable(correctionFactorMap).orElseGet(HashMap::new);
    }

    public void setCorrectionFactorMap(Map<StatDataTypeEnum, Map<Long, Double>> correctionFactorMap) {
        this.correctionFactorMap = correctionFactorMap;
    }

    public Map<StatDataTypeEnum, Map<Long, Double>> getReconstructionFactorMap() {
        return Optional.ofNullable(reconstructionFactorMap).orElseGet(HashMap::new);
    }

    public void setReconstructionFactorMap(Map<StatDataTypeEnum, Map<Long, Double>> reconstructionFactorMap) {
        this.reconstructionFactorMap = reconstructionFactorMap;
    }


    public static final class Builder {
        private Map<StatDataTypeEnum, Map<Long, Double>> correctionFactorMap;
        private Map<StatDataTypeEnum, Map<Long, Double>> reconstructionFactorMap;
        private Map<Long, NezhaStatDto> nezhaStatDtoMap;

        private Builder() {
        }

        public Builder correctionFactorMap(Map<StatDataTypeEnum, Map<Long, Double>> val) {
            correctionFactorMap = val;
            return this;
        }

        public Builder reconstructionFactorMap(Map<StatDataTypeEnum, Map<Long, Double>> val) {
            reconstructionFactorMap = val;
            return this;
        }

        public Builder nezhaStatDtoMap(Map<Long, NezhaStatDto> val) {
            nezhaStatDtoMap = val;
            return this;
        }

        public CorrectResult build() {
            return new CorrectResult(this);
        }
    }
}

package cn.com.duiba.nezha.engine.biz.vo.advert;

import cn.com.duiba.nezha.engine.api.enums.StatDataTypeEnum;
import cn.com.duiba.nezha.engine.biz.domain.AdvertStatDo;
import cn.com.duiba.nezha.engine.biz.domain.advert.OrientationPackage;
import cn.com.duiba.nezha.engine.biz.entity.nezha.advert.AdvertStatisticMergeEntity;

import java.util.List;
import java.util.Map;

public class PacingVo {
    private Long appId;
    private Long advertId;
    private Long packageId;
    private Long targetFee;
    private Long finalFee;
    private OrientationPackage orientationPackage;
    private Double ctr;
    private Double cvr;
    private Double factor;
    private Map<StatDataTypeEnum, List<Double>> hourlyStatDataMap;
    private Map<StatDataTypeEnum, AdvertStatisticMergeEntity> advertStatDataMap;
    private Map<StatDataTypeEnum, AdvertStatisticMergeEntity> orientPackageStatDataMap;
    private AdvertStatisticMergeEntity tagStatData;
    private Integer userBehavior;
    private Integer qualityLevel;
    private AdvertStatDo autoMatchStatDo;
    private Boolean smartShop;

    private PacingVo(Builder builder) {
        appId = builder.appId;
        advertId = builder.advertId;
        packageId = builder.packageId;
        targetFee = builder.targetFee;
        finalFee = builder.finalFee;
        orientationPackage = builder.orientationPackage;
        ctr = builder.ctr;
        cvr = builder.cvr;
        factor = builder.factor;
        hourlyStatDataMap = builder.hourlyStatDataMap;
        advertStatDataMap = builder.advertStatDataMap;
        orientPackageStatDataMap = builder.orientPackageStatDataMap;
        tagStatData = builder.tagStatData;
        userBehavior = builder.userBehavior;
        qualityLevel = builder.qualityLevel;
        autoMatchStatDo = builder.autoMatchStatDo;
        smartShop = builder.smartShop;
    }

    public Boolean getSmartShop() {
        return smartShop;
    }

    public OrientationPackage getOrientationPackage() {
        return orientationPackage;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getAppId() {
        return appId;
    }

    public Long getAdvertId() {
        return advertId;
    }

    public Long getPackageId() {
        return packageId;
    }

    public Long getTargetFee() {
        return targetFee;
    }

    public Long getFinalFee() {
        return finalFee;
    }


    public Double getCtr() {
        return ctr;
    }

    public Double getCvr() {
        return cvr;
    }

    public Double getFactor() {
        return factor;
    }

    public Map<StatDataTypeEnum, List<Double>> getHourlyStatDataMap() {
        return hourlyStatDataMap;
    }

    public Map<StatDataTypeEnum, AdvertStatisticMergeEntity> getAdvertStatDataMap() {
        return advertStatDataMap;
    }

    public Map<StatDataTypeEnum, AdvertStatisticMergeEntity> getOrientPackageStatDataMap() {
        return orientPackageStatDataMap;
    }

    public AdvertStatisticMergeEntity getTagStatData() {
        return tagStatData;
    }

    public Integer getUserBehavior() {
        return userBehavior;
    }

    public Integer getQualityLevel() {
        return qualityLevel;
    }

    public AdvertStatDo getAutoMatchStatDo() {
        return autoMatchStatDo;
    }

    public static final class Builder {
        private Long appId;
        private Long advertId;
        private Long packageId;
        private Long targetFee;
        private Long finalFee;
        private OrientationPackage orientationPackage;
        private Double ctr;
        private Double cvr;
        private Double factor;
        private Map<StatDataTypeEnum, List<Double>> hourlyStatDataMap;
        private Map<StatDataTypeEnum, AdvertStatisticMergeEntity> advertStatDataMap;
        private Map<StatDataTypeEnum, AdvertStatisticMergeEntity> orientPackageStatDataMap;
        private AdvertStatisticMergeEntity tagStatData;
        private Integer userBehavior;
        private Integer qualityLevel;
        private AdvertStatDo autoMatchStatDo;
        private Boolean smartShop;

        private Builder() {
        }

        public Builder appId(Long val) {
            appId = val;
            return this;
        }

        public Builder advertId(Long val) {
            advertId = val;
            return this;
        }

        public Builder packageId(Long val) {
            packageId = val;
            return this;
        }

        public Builder targetFee(Long val) {
            targetFee = val;
            return this;
        }

        public Builder finalFee(Long val) {
            finalFee = val;
            return this;
        }

        public Builder orientationPackage(OrientationPackage val) {
            orientationPackage = val;
            return this;
        }


        public Builder ctr(Double val) {
            ctr = val;
            return this;
        }

        public Builder cvr(Double val) {
            cvr = val;
            return this;
        }

        public Builder factor(Double val) {
            factor = val;
            return this;
        }

        public Builder hourlyStatDataMap(Map<StatDataTypeEnum, List<Double>> val) {
            hourlyStatDataMap = val;
            return this;
        }

        public Builder advertStatDataMap(Map<StatDataTypeEnum, AdvertStatisticMergeEntity> val) {
            advertStatDataMap = val;
            return this;
        }

        public Builder orientPackageStatDataMap(Map<StatDataTypeEnum, AdvertStatisticMergeEntity> val) {
            orientPackageStatDataMap = val;
            return this;
        }

        public Builder tagStatData(AdvertStatisticMergeEntity val) {
            tagStatData = val;
            return this;
        }

        public Builder userBehavior(Integer val) {
            userBehavior = val;
            return this;
        }

        public Builder qualityLevel(Integer val) {
            qualityLevel = val;
            return this;
        }

        public Builder autoMatchStatDo(AdvertStatDo val) {
            autoMatchStatDo = val;
            return this;
        }

        public Builder smartShop(Boolean val) {
            smartShop = val;
            return this;
        }

        public PacingVo build() {
            return new PacingVo(this);
        }
    }
}

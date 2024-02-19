package cn.com.duiba.nezha.engine.biz.service.advert.ctr;

import cn.com.duiba.nezha.engine.biz.domain.AdvertStatDo;
import cn.com.duiba.nezha.engine.biz.domain.advert.OrientationPackage;
import cn.com.duiba.nezha.engine.biz.entity.nezha.advert.SlotAdvertInfo;
import cn.com.duiba.nezha.engine.biz.service.CacheService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author ElinZhou
 * @version $Id: AdvertStatService.java , v 0.1 2017/12/18 下午5:23 ElinZhou Exp $
 */
public interface AdvertStatService {


    /**
     * 获取广告位下广告的信息
     */
    Map<Long, SlotAdvertInfo> getSlotAdvertInfo(Collection<Long> advertIds, Long slotId);

    /**
     * 查询广告在不同app下七天统计
     *
     * @return <广告ID，7日融合统计数据>
     */
    Map<Query, AdvertStatDo> get7DayStat(Collection<Query> queries);


    /**
     * 查询广告当天统计
     *
     * @return <广告ID，融合统计数据>
     */
    Map<Query, AdvertStatDo> getCurrentDayStat(Collection<Query> queries);


    /**
     * 查询广告当前小时统计
     *
     * @return <广告ID，融合统计数据>
     */
    Map<Query, AdvertStatDo> getCurrentHourStat(Collection<Query> queries);


    /**
     * 查询今天每个小时的统计(不含当前小时)
     *
     * @return <广告ID，每小时融合统计数据>
     */
    Map<Query, List<AdvertStatDo>> getTodayHourlyStat(Collection<Query> queries);

    Map<String, CacheService.CacheInfo> getCacheInfo();

    Map<OrientationPackage, AdvertStatDo> getSlotPackageData(Collection<OrientationPackage> trusteeshipOrientationPackages, Long slotId);

    Map<OrientationPackage, AdvertStatDo> getPackageData(Collection<OrientationPackage> weakTargetOrientationPackages);

    class Query {

        private Long appId;

        private Long advertId;

        private Long packageId;

        private Long materialId;

        private Long tag;// 流量类型 0-普通 1-劣质 2-优质

        private String timestamp;

        private Query(Builder builder) {
            appId = builder.appId;
            advertId = builder.advertId;
            packageId = builder.packageId;
            materialId = builder.materialId;
            tag = builder.tag;
            timestamp = builder.timestamp;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Long getAppId() {
            return appId;
        }

        public Query setAppId(Long appId) {
            this.appId = appId;
            return this;
        }

        public Long getAdvertId() {
            return advertId;
        }

        public Long getPackageId() {
            return packageId;
        }

        public Long getMaterialId() {
            return materialId;
        }

        public Long getTag() {
            return tag;
        }

        @Override
        public int hashCode() {
            return getKey().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Query && getKey().equals(((Query) obj).getKey());
        }

        public String getKey() {
            return appId + "_" + advertId + "_" + materialId + "_" + packageId + "_" + tag + "_" + timestamp;
        }

        public static final class Builder {
            private Long appId;
            private Long advertId;
            private Long packageId;
            private Long materialId;
            private Long tag;
            private String timestamp;

            public Builder() {
                //builder
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

            public Builder materialId(Long val) {
                materialId = val;
                return this;
            }

            public Builder tag(Long val) {
                tag = val;
                return this;
            }

            public Builder timestamp(String val) {
                timestamp = val;
                return this;
            }

            public Query build() {
                return new Query(this);
            }
        }
    }
}

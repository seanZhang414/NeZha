package cn.com.duiba.nezha.engine.biz.service.advert.ctr;

import cn.com.duiba.nezha.alg.alg.vo.StatDo;
import cn.com.duiba.nezha.engine.biz.service.CacheService;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by pc on 2017/2/27.
 */
public interface AdvertMergeStatService {

    List<StatDo> get(Collection<Long> advertIdList, Long appId, Map<Long, Long> timesMap);

    List<StatDo> get(Map<Long, Long> materialInfoEntityMap, Long appId, Map<Long, Long> timesMap);

    List<StatDo> get(Long advertId, Set<Long> appIds);

    List<StatDo> get(Long advertId, Long appId, List<Long> materialIds);

    Map<String, CacheService.CacheInfo> getCacheInfo();

    class AdvertStatQuery {

        private Long advertId;
        private Long materialId;
        private Long appId;
        private Long times;

        @Override
        public int hashCode() {
            return getId().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !getClass().equals(obj.getClass())) {
                return false;
            }

            return getId().equals(((AdvertStatQuery) obj).getId());

        }

        private String getId() {
            Joiner joiner = Joiner.on("-");
            // map的目的是因为join不支持入参为null，故map后防止存在null
            return joiner.join(Lists.newArrayList(advertId, materialId, appId, times).stream().map(String::valueOf).collect
                    (Collectors.toList()));
        }

        private AdvertStatQuery(Builder builder) {
            advertId = builder.advertId;
            materialId = builder.materialId;
            appId = builder.appId;
            times = builder.times;
        }

        public static final class Builder {

            private Long advertId;
            private Long materialId;
            private Long appId;
            private Long times;

            public Builder() {
                // 构造器
            }

            public Builder advertId(Long val) {
                advertId = val;
                return this;
            }

            public Builder materialId(Long val) {
                materialId = val;
                return this;
            }

            public Builder appId(Long val) {
                appId = val;
                return this;
            }

            public Builder times(Long val) {
                times = val;
                return this;
            }

            public AdvertStatQuery build() {
                return new AdvertStatQuery(this);
            }
        }

        public Long getAdvertId() {
            return advertId;
        }

        public Long getMaterialId() {
            return materialId;
        }

        public Long getAppId() {
            return appId;
        }

        public Long getTimes() {
            return times;
        }
    }
}

package cn.com.duiba.nezha.engine.biz.bo.advert;

import cn.com.duiba.nezha.alg.alg.alg.BudgetSmoothAlg;
import cn.com.duiba.nezha.alg.alg.vo.BudgetDo;
import cn.com.duiba.nezha.alg.alg.vo.BudgetInfo;
import cn.com.duiba.nezha.alg.alg.vo.BudgetSmoothDo;
import cn.com.duiba.nezha.engine.biz.domain.advert.OrientationPackage;
import cn.com.duiba.nezha.engine.biz.service.CacheService;
import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertResortVo;
import cn.com.duiba.nezha.engine.common.utils.MapUtils;
import cn.com.duiba.nezha.engine.common.utils.RedisKeyUtil;
import cn.com.duiba.nezha.engine.common.utils.StringRedisHelper;
import cn.com.duiba.wolf.perf.timeprofile.DBTimeProfile;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Service
public class AdvertBackendBo extends CacheService {

    @Resource
    private StringRedisTemplate nezhaStringRedisTemplate;

    private LoadingCache<String, BudgetDo> consumeCache = CacheBuilder.newBuilder()
            .expireAfterWrite(60L, TimeUnit.SECONDS)
            .recordStats()
            .build(new CacheLoader<String, BudgetDo>() {
                @Override
                public BudgetDo load(String key) {
                    throw new UnsupportedOperationException("not support single query");
                }

                @Override
                public Map<String, BudgetDo> loadAll(Iterable<? extends String> keys) {

                    return StringRedisHelper.of(nezhaStringRedisTemplate).valueMultiGet
                            (keys, BudgetDo.class, BudgetDo::new);
                }
            });

    private LoadingCache<BudgetQuery, BudgetSmoothDo> budgetCache = CacheBuilder.newBuilder()
            .expireAfterWrite(60L, TimeUnit.SECONDS)
            .recordStats()
            .build(new CacheLoader<BudgetQuery, BudgetSmoothDo>() {
                @Override
                public BudgetSmoothDo load(BudgetQuery key) throws Exception {
                    throw new UnsupportedOperationException("not support single query");
                }

                @Override
                public Map<BudgetQuery, BudgetSmoothDo> loadAll(Iterable<? extends BudgetQuery> keys) throws Exception {
                    List<BudgetQuery> budgetQueries = Lists.newArrayList(keys);
                    return getConsumeRatio(budgetQueries);
                }
            });

    public Map<OrientationPackage, BudgetSmoothDo> getBudgetSmooth(Long appId, Set<AdvertResortVo> advertResortVoList) {

        Map<OrientationPackage, BudgetQuery> packageQueryMap = advertResortVoList
                .stream()
                .collect(toMap(advertResortVo -> {
                            OrientationPackage orientationPackage = new OrientationPackage();
                            orientationPackage.setId(advertResortVo.getPackageId());
                            orientationPackage.setAdvertId(advertResortVo.getAdvertId());
                            return orientationPackage;
                        },
                        advertResortVo -> {
                            BudgetQuery budgetQuery = new BudgetQuery();
                            budgetQuery.setAccountId(advertResortVo.getAccountId());
                            budgetQuery.setAdvertId(advertResortVo.getAdvertId());
                            budgetQuery.setAppId(appId);
                            budgetQuery.setPackageId(advertResortVo.getPackageId());
                            return budgetQuery;
                        }));


        try {
            DBTimeProfile.enter("getBudgetSmooth");
            Collection<BudgetQuery> values = packageQueryMap.values();
            return MapUtils.translate(packageQueryMap, budgetCache.getAll(values));
        } catch (Exception e) {
            logger.error("getBudgetSmooth happened error:{}", e);
            return new HashMap<>();
        } finally {
            DBTimeProfile.release();
        }

    }

    private Map<BudgetQuery, BudgetSmoothDo> getConsumeRatio(List<BudgetQuery> queries) {
        try {

            DBTimeProfile.enter("getConsumeRatio");
            Map<ConsumeQuery, String> queryKeyMap = new HashMap<>();
            queries.forEach(query -> {
                Long advertId = query.getAdvertId();
                ConsumeQuery advertAccountQuery = new ConsumeQuery(advertId, 1);
                ConsumeQuery advertPackageAppQuery = new ConsumeQuery(advertId, 2);
                ConsumeQuery advertPackageHourQuery = new ConsumeQuery(advertId, 3);
                ConsumeQuery advertPackageQuery = new ConsumeQuery(advertId, 4);
                ConsumeQuery advertQuery = new ConsumeQuery(advertId, 5);
                Long packageId = query.getPackageId();
                queryKeyMap.put(advertAccountQuery, RedisKeyUtil.getAccountConsumeKey(query.getAccountId()));
                queryKeyMap.put(advertPackageAppQuery, RedisKeyUtil.getAdvertOrientationAppConsumeKey(advertId, packageId, query.getAppId()));
                queryKeyMap.put(advertPackageHourQuery, RedisKeyUtil.getAdvertOrientationHourConsumeKey(advertId, packageId));
                queryKeyMap.put(advertPackageQuery, RedisKeyUtil.getAdvertOrientationConsumeKey(advertId, packageId));
                queryKeyMap.put(advertQuery, RedisKeyUtil.getAdvertConsumeKey(advertId));
            });

            Map<ConsumeQuery, BudgetDo> consumeQueryBudgetDoMap = MapUtils.translate(queryKeyMap, consumeCache.getAll(queryKeyMap.values()));
            Map<BudgetQuery, BudgetInfo> advertBudgetInfoMap = queries.stream().collect(toMap(Function.identity(), query -> {
                Long advertId = query.getAdvertId();
                ConsumeQuery advertAccountQuery = new ConsumeQuery(advertId, 1);
                ConsumeQuery advertPackageAppQuery = new ConsumeQuery(advertId, 2);
                ConsumeQuery advertPackageHourQuery = new ConsumeQuery(advertId, 3);
                ConsumeQuery advertPackageQuery = new ConsumeQuery(advertId, 4);
                ConsumeQuery advertQuery = new ConsumeQuery(advertId, 5);

                BudgetInfo budgetInfo = new BudgetInfo();
                budgetInfo.setAccountBudgetInfo(consumeQueryBudgetDoMap.get(advertAccountQuery));
                budgetInfo.setAdvertBudgetInfo(consumeQueryBudgetDoMap.get(advertQuery));
                budgetInfo.setOrientationAndAppBudgetInfo(consumeQueryBudgetDoMap.get(advertPackageAppQuery));
                budgetInfo.setOrientationAndTimeBudgetInfo(consumeQueryBudgetDoMap.get(advertPackageHourQuery));
                budgetInfo.setOrientationBudgetInfo(consumeQueryBudgetDoMap.get(advertPackageQuery));
                return budgetInfo;
            }));
            DBTimeProfile.enter("BudgetSmoothAlg.getBudgetRatio");
            Map<BudgetQuery, BudgetSmoothDo> budgetRatio = BudgetSmoothAlg.getBudgetRatio(advertBudgetInfoMap);
            DBTimeProfile.release();
            return budgetRatio;

        } catch (Exception e) {
            logger.error("getConsumeRatio happened error:{}", e);
            return new HashMap<>();
        } finally {
            DBTimeProfile.release();
        }

    }

    class ConsumeQuery {
        private Long advertId;
        private Integer type;

        public Long getAdvertId() {
            return advertId;
        }

        public void setAdvertId(Long advertId) {
            this.advertId = advertId;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public ConsumeQuery(Long advertId, Integer type) {
            this.advertId = advertId;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConsumeQuery that = (ConsumeQuery) o;
            return Objects.equals(advertId, that.advertId) &&
                    Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(advertId, type);
        }
    }

    class BudgetQuery {
        private Long advertId;
        private Long packageId;
        private Long accountId;
        private Long appId;

        public Long getAdvertId() {
            return advertId;
        }

        public void setAdvertId(Long advertId) {
            this.advertId = advertId;
        }

        public Long getPackageId() {
            return packageId;
        }

        public void setPackageId(Long packageId) {
            this.packageId = packageId;
        }

        public Long getAccountId() {
            return accountId;
        }

        public void setAccountId(Long accountId) {
            this.accountId = accountId;
        }

        public Long getAppId() {
            return appId;
        }

        public void setAppId(Long appId) {
            this.appId = appId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BudgetQuery that = (BudgetQuery) o;
            return Objects.equals(advertId, that.advertId) &&
                    Objects.equals(packageId, that.packageId) &&
                    Objects.equals(accountId, that.accountId) &&
                    Objects.equals(appId, that.appId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(advertId, packageId, accountId, appId);
        }
    }

}

package cn.com.duiba.nezha.engine.biz.service.advert.ctr.impl;

import cn.com.duiba.nezha.alg.alg.vo.StatDo;
import cn.com.duiba.nezha.engine.api.support.RecommendEngineException;
import cn.com.duiba.nezha.engine.biz.service.CacheService;
import cn.com.duiba.nezha.engine.biz.service.advert.ctr.AdvertMergeStatService;
import cn.com.duiba.nezha.engine.common.utils.AssertUtil;
import cn.com.duiba.nezha.engine.common.utils.RedisKeyUtil;
import cn.com.duiba.nezha.engine.common.utils.StringRedisHelper;
import cn.com.duiba.wolf.perf.timeprofile.DBTimeProfile;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: AdvertMergeStatServiceImpl.java , v 0.1 2017/6/21 下午3:14 ZhouFeng Exp $
 */

@Service
public class AdvertMergeStatServiceImpl extends CacheService implements AdvertMergeStatService {
    private static final Logger logger = LoggerFactory.getLogger(AdvertMergeStatService.class);

    /**
     * 发券次数无关统计数据查询时发券次数
     */
    private static final Long TIMES_GLOBAL = 1L;

    private LoadingCache<AdvertStatQuery, StatDo> advertMergeStatCache = CacheBuilder.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .recordStats()
            .build(new CacheLoader<AdvertStatQuery, StatDo>() {
                @Override
                public StatDo load(AdvertStatQuery key) throws Exception {
                    throw new IllegalAccessException("not suppose single query");
                }

                @Override
                public Map<AdvertStatQuery, StatDo> loadAll(Iterable<? extends AdvertStatQuery> keys) throws Exception {
                    if (!(keys instanceof Collection)) {
                        throw new IllegalArgumentException("type error");
                    }
                    return loadAllRecord((Collection) keys);
                }
            });

    /**
     * 获取广告统计数据
     *
     * @param advertIdList
     * @param appId
     * @return
     */
    @Override
    public List<StatDo> get(Collection<Long> advertIdList, Long appId, Map<Long, Long> timesMap) {
        try {
            DBTimeProfile.enter("getAdvertMergeStat");

            if (advertIdList.isEmpty()) {
                return new ArrayList<>();
            }

            List<AdvertStatQuery> queries = advertIdList
                    .stream()
                    .map(advertId -> new AdvertStatQuery.Builder()
                            .advertId(advertId)
                            .appId(appId)
                            .times(timesMap.get(advertId)).build())

                    .collect(toList());

            return Lists.newArrayList(advertMergeStatCache.getAll(queries).values());

        } catch (Exception e) {
            throw new RecommendEngineException("get happen error", e);
        } finally {
            DBTimeProfile.release();
        }
    }

    @Override
    public List<StatDo> get(Map<Long, Long> advertMaterialMap, Long appId, Map<Long, Long> timesMap) {

        try {
            DBTimeProfile.enter("getAdvertMergeStatWithMaterial");

            if (advertMaterialMap.isEmpty()) {
                return new ArrayList<>();
            }

            List<AdvertStatQuery> queries = advertMaterialMap.entrySet()
                    .stream()
                    .map(entry -> new AdvertStatQuery.Builder()
                            .advertId(entry.getKey())
                            .times(timesMap.get(entry.getKey()))
                            .materialId(entry.getValue())
                            .appId(appId).build())

                    .collect(toList());

            return Lists.newArrayList(advertMergeStatCache.getAll(queries).values());

        } catch (Exception e) {
            throw new RecommendEngineException("get happen error", e);
        } finally {
            DBTimeProfile.release();
        }
    }


    @Override
    public List<StatDo> get(Long advertId, Set<Long> appIds) {
        List<StatDo> advertStatEntities = new ArrayList<>();
        if (AssertUtil.isEmpty(appIds)) {
            return advertStatEntities;
        }

        try {
            DBTimeProfile.enter("get");

            List<AdvertStatQuery> queries = new ArrayList<>(appIds.size());

            for (Long appId : appIds) {
                AdvertStatQuery query = new AdvertStatQuery.Builder().advertId(advertId).appId(appId).build();

                queries.add(query);
            }

            return Lists.newArrayList(advertMergeStatCache.getAll(queries).values());

        } catch (Exception e) {
            throw new RecommendEngineException("get happen error", e);
        } finally {
            DBTimeProfile.release();
        }
    }

    @Override
    public List<StatDo> get(Long advertId, Long appId, List<Long> materialIds) {
        try {
            DBTimeProfile.enter("get");
            if (AssertUtil.isEmpty(materialIds)) {
                return new ArrayList<>();
            }

            List<AdvertStatQuery> queries = materialIds
                    .stream()
                    .map(materialId -> new AdvertStatQuery.Builder().advertId(advertId).materialId(materialId).appId(appId).times(TIMES_GLOBAL).build())
                    .collect(toList());

            return Lists.newArrayList(advertMergeStatCache.getAll(queries).values());
        } catch (Exception e) {
            throw new RecommendEngineException("get happen error", e);
        } finally {
            DBTimeProfile.release();
        }
    }

    @Override
    public Map<String, CacheInfo> getCacheInfo() {
        Map<String, CacheInfo> cacheInfoMap = new HashMap<>();
        cacheInfoMap.put("advertMergeStatCache", CacheInfo.generate(advertMergeStatCache));
        return cacheInfoMap;
    }


    private Map<AdvertStatQuery, StatDo> loadAllRecord(Collection<? extends AdvertStatQuery> keys) {

        try {
            DBTimeProfile.enter("loadAdvertMergeStat");
            Map<String, AdvertStatQuery> redisKey2QueryMap = keys.stream().collect(toMap(key -> RedisKeyUtil.advertMergeStatKey(key.getAppId(), key.getAdvertId(), key.getMaterialId(), key.getTimes()), Function.identity()));
            Map<String, StatDo> advertStatDoMap = StringRedisHelper.of(nezhaStringRedisTemplate).valueMultiGet(redisKey2QueryMap.keySet(), StatDo.class, StatDo::new);
            return advertStatDoMap.entrySet().stream().collect(toMap(entry -> redisKey2QueryMap.get(entry.getKey()), entry -> {
                AdvertStatQuery query = redisKey2QueryMap.get(entry.getKey());
                StatDo mergeStatDo = entry.getValue();
                mergeStatDo.setAdvertId(query.getAdvertId());
                mergeStatDo.setMaterialId(query.getMaterialId());
                mergeStatDo.setAppId(query.getAppId());
                mergeStatDo.setTimes(query.getTimes());
                return mergeStatDo;
            }));
        } finally {
            DBTimeProfile.release();
        }
    }


}
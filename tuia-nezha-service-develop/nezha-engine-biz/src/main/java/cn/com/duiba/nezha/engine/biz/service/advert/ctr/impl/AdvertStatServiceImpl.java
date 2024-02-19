package cn.com.duiba.nezha.engine.biz.service.advert.ctr.impl;

import cn.com.duiba.nezha.engine.api.support.RecommendEngineException;
import cn.com.duiba.nezha.engine.biz.domain.AdvertStatDo;
import cn.com.duiba.nezha.engine.biz.domain.advert.OrientationPackage;
import cn.com.duiba.nezha.engine.biz.entity.nezha.advert.SlotAdvertInfo;
import cn.com.duiba.nezha.engine.biz.service.CacheService;
import cn.com.duiba.nezha.engine.biz.service.advert.ctr.AdvertStatService;
import cn.com.duiba.nezha.engine.common.utils.MapUtils;
import cn.com.duiba.nezha.engine.common.utils.RedisKeyUtil;
import cn.com.duiba.nezha.engine.common.utils.StringRedisHelper;
import cn.com.duiba.wolf.perf.timeprofile.DBTimeProfile;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: AdvertStatServiceImpl.java , v 0.1 2017/12/18 下午5:23 ZhouFeng Exp $
 */
@Service
public class AdvertStatServiceImpl extends CacheService implements AdvertStatService {

    /**
     * 每小时统计缓存时间
     */
    private static final CacheDuration HOURLY_STAT_CACHE_DURATION = new CacheDuration(12L, TimeUnit.HOURS);
    /**
     * 每天统计缓存时间
     */
    private static final CacheDuration DAILY_STAT_CACHE_DURATION = new CacheDuration(1L, TimeUnit.DAYS);
    /**
     * 当天统计缓存时间
     */
    private static final CacheDuration CURRENT_DAY_STAT_CACHE_DURATION = new CacheDuration(1L, TimeUnit.MINUTES);
    /**
     * 当前小时
     */
    private static final CacheDuration CURRENT_HOUR_STAT_CACHE_DURATION = new CacheDuration(90L, TimeUnit.SECONDS);

    /**
     * 每小时统计缓存
     * key:各维度ke+yyyyyMMddHH
     */
    private LoadingCache<String, AdvertStatDo> hourlyStatCache = CacheBuilder.newBuilder()
            .expireAfterWrite(HOURLY_STAT_CACHE_DURATION.getDuration(), HOURLY_STAT_CACHE_DURATION.getTimeUnit())
            .recordStats()
            .build(new CacheLoader<String, AdvertStatDo>() {
                @Override
                public AdvertStatDo load(String key) throws Exception {
                    throw new IllegalAccessException("not suppose single query");
                }

                @Override
                public Map<String, AdvertStatDo> loadAll(Iterable<? extends String> keys) throws Exception {

                    DBTimeProfile.enter("loadAdvertHourlyStat");
                    Map<String, AdvertStatDo> key2Stat = StringRedisHelper.of(nezhaStringRedisTemplate).valueMultiGet
                            (keys, AdvertStatDo.class, AdvertStatDo::new);
                    DBTimeProfile.release();

                    return key2Stat;
                }
            });

    /**
     * 每天统计缓存
     * key:各维度ke+yyyyyMMdd
     */
    private LoadingCache<String, AdvertStatDo> dailyStatCache = CacheBuilder.newBuilder()
            .expireAfterWrite(DAILY_STAT_CACHE_DURATION.getDuration(), DAILY_STAT_CACHE_DURATION.getTimeUnit())
            .recordStats()
            .build(new CacheLoader<String, AdvertStatDo>() {
                @Override
                public AdvertStatDo load(String key) throws Exception {
                    throw new IllegalAccessException("not suppose single query");
                }

                @Override
                public Map<String, AdvertStatDo> loadAll(Iterable<? extends String> keys) throws Exception {

                    return StringRedisHelper.of(nezhaStringRedisTemplate).valueMultiGet(keys, AdvertStatDo.class, AdvertStatDo::new);
                }
            });


    /**
     * 当天统计缓存
     * key:各维度ke+yyyyyMMdd
     */
    private LoadingCache<String, AdvertStatDo> currentDayStatCache = CacheBuilder.newBuilder()
            .expireAfterWrite(CURRENT_DAY_STAT_CACHE_DURATION.getDuration(), CURRENT_DAY_STAT_CACHE_DURATION.getTimeUnit())
            .recordStats()
            .build(new CacheLoader<String, AdvertStatDo>() {
                @Override
                public AdvertStatDo load(String key) throws Exception {
                    throw new IllegalAccessException("not suppose single query");
                }

                @Override
                public Map<String, AdvertStatDo> loadAll(Iterable<? extends String> keys) throws Exception {
                    return StringRedisHelper.of(nezhaStringRedisTemplate).valueMultiGet(keys, AdvertStatDo.class, AdvertStatDo::new);
                }
            });


    /**
     * 当前小时统计缓存
     * key:各维度ke+yyyyyMMddHH
     */
    private LoadingCache<String, AdvertStatDo> currentHourStatCache = CacheBuilder.newBuilder()
            .expireAfterWrite(CURRENT_HOUR_STAT_CACHE_DURATION.getDuration(), CURRENT_HOUR_STAT_CACHE_DURATION.getTimeUnit())
            .recordStats()
            .build(new CacheLoader<String, AdvertStatDo>() {
                @Override
                public AdvertStatDo load(String key) throws Exception {
                    throw new IllegalAccessException("not suppose single query");
                }

                @Override
                public Map<String, AdvertStatDo> loadAll(Iterable<? extends String> keys) throws Exception {
                    return StringRedisHelper.of(nezhaStringRedisTemplate).valueMultiGet(keys, AdvertStatDo.class, AdvertStatDo::new);
                }
            });

    private LoadingCache<Query, AdvertStatDo> daily6StatCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1L, TimeUnit.HOURS)
            .recordStats()
            .build(new CacheLoader<Query, AdvertStatDo>() {
                @Override
                public AdvertStatDo load(Query key) throws Exception {
                    throw new IllegalAccessException("not suppose single query");
                }

                @Override
                public Map<Query, AdvertStatDo> loadAll(Iterable<? extends Query> keys) throws Exception {
                    //当天
                    String dayTimestamp = LocalDateTime.now().format(DAY_FORMATTER);
                    Map<Query, List<String>> query2TimestampListMap = Lists.newArrayList(keys)
                            .stream()
                            .collect(toMap(Function.identity(), query -> {
                                List<String> last6DayTimestamps = last6DayTimestampCache.getUnchecked(dayTimestamp);
                                return last6DayTimestamps.stream().map(time -> RedisKeyUtil.advertDailyStatKey(
                                        query.getAppId(),
                                        query.getAdvertId(),
                                        query.getPackageId(),
                                        query.getMaterialId(),
                                        query.getTag(),
                                        time)).collect(toList());
                            }));


                    List<String> allKeys = query2TimestampListMap.values().stream().flatMap(Collection::stream).collect(toList());

                    ImmutableMap<String, AdvertStatDo> timestamp2Stat = dailyStatCache.getAll(allKeys);

                    return query2TimestampListMap.entrySet().stream().collect(toMap(Map.Entry::getKey,
                            entry -> sum(entry.getValue().stream().map(timestamp2Stat::get).collect(toList()))));
                }
            });

    /**
     * 广告位的广告信息.离线数据(昨日)
     */
    private LoadingCache<String, SlotAdvertInfo> slotAdvertCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10L, TimeUnit.MINUTES)
            .recordStats()
            .build(new CacheLoader<String, SlotAdvertInfo>() {
                @Override
                public SlotAdvertInfo load(String key) throws Exception {
                    throw new IllegalAccessException("not suppose single query");
                }

                @Override
                public Map<String, SlotAdvertInfo> loadAll(Iterable<? extends String> keys) throws Exception {
                    return StringRedisHelper.of(nezhaStringRedisTemplate).valueMultiGet(keys, SlotAdvertInfo.class, SlotAdvertInfo::new);
                }
            });


    private LoadingCache<String, AdvertStatDo> slotPackageDataCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1L, TimeUnit.MINUTES)
            .build(new CacheLoader<String, AdvertStatDo>() {
                @Override
                public AdvertStatDo load(String key) throws Exception {
                    throw new IllegalAccessException("not suppose single query");
                }

                @Override
                public Map<String, AdvertStatDo> loadAll(Iterable<? extends String> keys) throws Exception {
                    return StringRedisHelper.of(nezhaStringRedisTemplate).valueMultiGet(keys, AdvertStatDo.class, AdvertStatDo::new);
                }
            });

    private LoadingCache<String, AdvertStatDo> packageDataCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build(new CacheLoader<String, AdvertStatDo>() {
                @Override
                public AdvertStatDo load(String s) throws Exception {
                    throw new IllegalAccessException("not suppose single query");
                }

                @Override
                public Map<String, AdvertStatDo> loadAll(Iterable<? extends String> keys) throws Exception {
                    return StringRedisHelper.of(nezhaStringRedisTemplate).valueMultiGet(keys, AdvertStatDo.class, AdvertStatDo::new);
                }
            });

    @Override
    public Map<Long, SlotAdvertInfo> getSlotAdvertInfo(Collection<Long> advertIds, Long slotId) {
        try {
            DBTimeProfile.enter("AdvertStatService.getSlotAdvertInfo");
            Map<Long, String> advertKeyMap = advertIds.stream()
                    .collect(toMap(Function.identity(),
                            advertId -> RedisKeyUtil.getSlotAdvertInfoKey(slotId, advertId)));

            ImmutableMap<String, SlotAdvertInfo> keySlotAdvertInfoMap = slotAdvertCache.getAll(advertKeyMap.values());
            return MapUtils.translate(advertKeyMap, keySlotAdvertInfoMap);
        } catch (Exception e) {
            throw new RecommendEngineException("getSlotAdvertInfo error", e);
        } finally {
            DBTimeProfile.release();
        }

    }

    @Override
    public Map<Query, AdvertStatDo> get7DayStat(Collection<Query> queries) {
        try {
            DBTimeProfile.enter("advertStatService.get7DayStat");
            LocalDate now = LocalDate.now();
            //查询前6天数据
            String nowTimestamp = now.format(DAY_FORMATTER);

            Map<Query, String> query2KeyMap = queries.stream().collect(toMap(Function.identity(),
                    query -> RedisKeyUtil.advertDailyStatKey(query.getAppId(), query.getAdvertId(),
                            query.getPackageId(), query.getMaterialId(), query.getTag(), nowTimestamp)));

            Map<Query, AdvertStatDo> last6DayStatMap = associateAdvert6DayStat(queries);
            //查询当天数据
            ImmutableMap<String, AdvertStatDo> currentDayStatMap = currentDayStatCache.getAll(query2KeyMap.values());
            //聚合七天数据
            Map<Query, AdvertStatDo> record = new HashMap<>(queries.size());
            for (Query query : queries) {
                AdvertStatDo last6DayStat = last6DayStatMap.get(query);
                AdvertStatDo currentDayStat = currentDayStatMap.get(query2KeyMap.get(query));
                AdvertStatDo statDo = associateAdvert7DayStat(last6DayStat, currentDayStat, query.getAppId(), query
                        .getAdvertId());
                record.put(query, statDo);
            }
            return record;
        } catch (Exception e) {
            throw new RecommendEngineException("get7DayStat error", e);
        } finally {
            DBTimeProfile.release();
        }
    }

    @Override
    public Map<Query, AdvertStatDo> getCurrentDayStat(Collection<Query> queries) {
        try {
            DBTimeProfile.enter("advertStatService.getCurrentDayStat");
            LocalDateTime now = LocalDateTime.now();

            //当天
            String dayTimestamp = now.format(DAY_FORMATTER);


            Map<Query, String> query2KeyMap = new HashMap<>(queries.size());

            for (Query query : queries) {
                if (query == null) {
                    continue;
                }
                String key = RedisKeyUtil.advertDailyStatKey(query.getAppId(), query.getAdvertId(),
                        query.getPackageId(), query.getMaterialId(), query.getTag(), dayTimestamp);

                query2KeyMap.put(query, key);
            }
            ImmutableMap<String, AdvertStatDo> currentDayStatMap = currentDayStatCache.getAll(query2KeyMap.values());

            return MapUtils.translate(query2KeyMap, currentDayStatMap);

        } catch (Exception e) {
            throw new RecommendEngineException("get current day stat error", e);
        } finally {
            DBTimeProfile.release();
        }
    }

    @Override
    public Map<Query, AdvertStatDo> getCurrentHourStat(Collection<Query> queries) {
        try {
            DBTimeProfile.enter("advertStatService.getCurrentHourStat");
            LocalDateTime now = LocalDateTime.now();

            //当前小时
            String hourTimestamp = now.format(HOUR_FORMATTER);


            Map<Query, String> query2KeyMap = new HashMap<>(queries.size());

            for (Query query : queries) {
                if (query == null) {
                    continue;
                }
                String key = RedisKeyUtil.advertHourlyStatKey(query.getAppId(), query.getAdvertId(),
                        query.getPackageId(), query.getMaterialId(), hourTimestamp);

                query2KeyMap.put(query, key);
            }
            ImmutableMap<String, AdvertStatDo> currentHourStatMap = currentHourStatCache.getAll(query2KeyMap.values());

            return MapUtils.translate(query2KeyMap, currentHourStatMap);

        } catch (Exception e) {
            throw new RecommendEngineException("get current hour stat error", e);
        } finally {
            DBTimeProfile.release();
        }
    }

    @Override
    public Map<Query, List<AdvertStatDo>> getTodayHourlyStat(Collection<Query> queries) {
        try {
            LocalDateTime now = LocalDateTime.now();

            int hour = now.getHour();
            Map<Query, List<String>> query2KeyListMap = new HashMap<>(queries.size());
            List<String> keyToLoad = new ArrayList<>(queries.size() * hour);

            //获取从0点到当前小时（不包括）的小时时间戳
            List<String> timestamps = hourlyTimestampCache.get(now.format(DAY_FORMATTER)).subList(0, hour);

            for (Query query : queries) {

                Long appId = query.getAppId();
                Long advertId = query.getAdvertId();
                Long packageId = query.getPackageId();
                Long materialId = query.getMaterialId();

                //拼装每小时统计的key
                List<String> hourlyStatKey = timestamps.stream().map(timestamp -> RedisKeyUtil.advertHourlyStatKey
                        (appId, advertId, packageId, materialId, timestamp)).collect(toList());

                keyToLoad.addAll(hourlyStatKey);
                query2KeyListMap.put(query, hourlyStatKey);

            }


            ImmutableMap<String, AdvertStatDo> hourlyStatMap = hourlyStatCache.getAll(keyToLoad);

            Map<Query, List<AdvertStatDo>> todayHourlyStatMap = new HashMap<>(queries.size());

            for (Query query : queries) {

                List<String> keys = query2KeyListMap.get(query);

                todayHourlyStatMap.put(query, keys.stream().map(hourlyStatMap::get).collect(toList()));
            }

            return todayHourlyStatMap;

        } catch (Exception e) {
            throw new RecommendEngineException("get today hourly stat error", e);
        }
    }

    @Override
    public Map<String, CacheInfo> getCacheInfo() {
        Map<String, CacheInfo> map = new HashMap<>();
        map.put("hourlyStatCache", CacheInfo.generate(hourlyStatCache));
        map.put("currentHourStatCache", CacheInfo.generate(currentHourStatCache));
        map.put("currentDayStatCache", CacheInfo.generate(currentDayStatCache));
        map.put("dailyStatCache", CacheInfo.generate(dailyStatCache));
        return map;
    }

    @Override
    public Map<OrientationPackage, AdvertStatDo> getSlotPackageData(Collection<OrientationPackage> trusteeshipOrientationPackages, Long slotId) {

        try {
            DBTimeProfile.enter("AdvertStatService.getSlotPackageData");
            String todayDate = LocalDate.now().format(DAY_FORMATTER);
            Map<OrientationPackage, String> orientationPackageKeyMap = trusteeshipOrientationPackages.stream()
                    .collect(toMap(Function.identity(),
                            orientationPackage -> RedisKeyUtil
                                    .getSlotPackageDataKey(
                                            slotId,
                                            orientationPackage.getAdvertId(),
                                            orientationPackage.getId(),
                                            orientationPackage.getConvertCost(),
                                            todayDate)));
            return MapUtils.translate(orientationPackageKeyMap, slotPackageDataCache.getAll(orientationPackageKeyMap.values()));
        } catch (ExecutionException e) {
            throw new RecommendEngineException("getSlotPackageBlackInfo error", e);
        } finally {
            DBTimeProfile.release();
        }
    }

    @Override
    public Map<OrientationPackage, AdvertStatDo> getPackageData(Collection<OrientationPackage> weakTargetOrientationPackages) {
        try {
            DBTimeProfile.enter("getPackageData");
            Map<OrientationPackage, String> packageKeysMap = weakTargetOrientationPackages.stream()
                    .collect(toMap(Function.identity(), orientationPackage ->
                            RedisKeyUtil.getOrientationPackageData(
                                    orientationPackage.getAdvertId(),
                                    orientationPackage.getId(),
                                    LocalDate.now().format(DAY_FORMATTER))));

            return   MapUtils.translate(packageKeysMap, packageDataCache.getAll(packageKeysMap.values()));

        } catch (ExecutionException e) {
            throw new RecommendEngineException("getPackageData error", e);
        } finally {
            DBTimeProfile.release();
        }
    }

    private Map<Query, AdvertStatDo> associateAdvert6DayStat(Collection<Query> queries) throws
            ExecutionException {
        try {
            DBTimeProfile.enter("associateAdvert6DayStat");
            String dayTimestamp = LocalDateTime.now().format(DAY_FORMATTER);
            Map<Query, Query> queryMap = queries.stream().collect(toMap(Function.identity(),
                    query -> new Query.Builder()
                            .advertId(query.getAdvertId())
                            .appId(query.getAppId())
                            .packageId(query.getPackageId())
                            .tag(query.getTag())
                            .materialId(query.getMaterialId())
                            .timestamp(dayTimestamp).build()));
            ImmutableMap<Query, AdvertStatDo> all = daily6StatCache.getAll(queryMap.values());
            return MapUtils.translate(queryMap, all);
        } finally {
            DBTimeProfile.release();
        }
    }


    /**
     * 聚合数据
     */

    private AdvertStatDo sum(List<AdvertStatDo> advertStatDoList) {
        long sumLaunch = 0;
        long sumClick = 0;
        long sumAction = 0;
        long sumExposure = 0;
        long sumFee = 0;

        for (AdvertStatDo advertStatDo : advertStatDoList) {
            sumLaunch += advertStatDo.getLaunchCnt();
            sumClick += advertStatDo.getChargeCnt();
            sumAction += advertStatDo.getActClickCnt();
            sumExposure += advertStatDo.getActExpCnt();
            sumFee += advertStatDo.getChargeFees();
        }

        double ctr = 0D;
        if (sumLaunch != 0) {
            // 点击率 = 点击数/发券数
            ctr = ((double) sumClick) / sumLaunch;
        }

        double cvr = 0D;
        if (sumClick != 0) {
            // 转化率 = 转化数/点击数
            cvr = ((double) sumAction) / sumClick;
        }
        AdvertStatDo advertStatDo = new AdvertStatDo();
        advertStatDo.setCtr(ctr);
        advertStatDo.setCvr(cvr);
        advertStatDo.setLaunchCnt(sumLaunch);
        advertStatDo.setChargeCnt(sumClick);
        advertStatDo.setActClickCnt(sumAction);
        advertStatDo.setActExpCnt(sumExposure);
        advertStatDo.setChargeFees(sumFee);
        return advertStatDo;
    }

    private AdvertStatDo associateAdvert7DayStat(AdvertStatDo last6DayStat, AdvertStatDo currentDayStat, Long appId,
                                                 Long advertId) {
        // 聚合前6天和今天数据
        AdvertStatDo statDo = this.sum(Lists.newArrayList(last6DayStat, currentDayStat));
        statDo.setAdvertId(advertId);
        statDo.setAppId(appId);
        return statDo;
    }
}


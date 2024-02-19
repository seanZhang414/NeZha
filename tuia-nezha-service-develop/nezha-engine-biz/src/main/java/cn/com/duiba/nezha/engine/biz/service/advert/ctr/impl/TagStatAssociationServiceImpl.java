package cn.com.duiba.nezha.engine.biz.service.advert.ctr.impl;

import cn.com.duiba.nezha.engine.biz.entity.nezha.advert.AdvertStatisticMergeEntity;
import cn.com.duiba.nezha.engine.biz.service.CacheService;
import cn.com.duiba.nezha.engine.biz.service.advert.ctr.TagStatAssociationService;
import cn.com.duiba.nezha.engine.common.utils.RedisKeyUtil;
import cn.com.duiba.nezha.engine.common.utils.StringRedisHelper;
import cn.com.duiba.wolf.perf.timeprofile.DBTimeProfile;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: TagStatAssociationServiceImpl.java , v 0.1 2018/2/5 下午8:34 ZhouFeng Exp $
 */
@Service
public class TagStatAssociationServiceImpl extends CacheService implements TagStatAssociationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagStatAssociationServiceImpl.class);

    /**
     * 每天统计缓存时间
     */
    private static final CacheDuration DAILY_STAT_CACHE_DURATION = new CacheDuration(7L, TimeUnit.DAYS);
    /**
     * 当天统计缓存时间
     */
    private static final CacheDuration CURRENT_DAY_STAT_CACHE_DURATION = new CacheDuration(5L, TimeUnit.MINUTES);
    /**
     * 当前小时
     */
    private static final CacheDuration CURRENT_HOUR_STAT_CACHE_DURATION = new CacheDuration(2L, TimeUnit.MINUTES);


    /**
     * 每天统计缓存
     * key:各维度ke+yyyyyMMdd
     */
    private LoadingCache<String, Double> dailyStatCache = CacheBuilder.newBuilder()
            .expireAfterWrite(DAILY_STAT_CACHE_DURATION.getDuration(), DAILY_STAT_CACHE_DURATION.getTimeUnit())
            .recordStats()
            .build(new CacheLoader<String, Double>() {
                @Override
                public Double load(String key) throws Exception {
                    throw new IllegalAccessException("not suppose single query");
                }

                @Override
                public Map<String, Double> loadAll(Iterable<? extends String> keys) throws Exception {

                    return StringRedisHelper.of(nezhaStringRedisTemplate).valueMultiGet
                            (keys, Double.class, () -> 0D);
                }
            });


    /**
     * 当天统计缓存
     * key:各维度ke+yyyyyMMdd
     */
    private LoadingCache<String, Double> currentDayStatCache = CacheBuilder.newBuilder()
            .expireAfterWrite(CURRENT_DAY_STAT_CACHE_DURATION.getDuration(), CURRENT_DAY_STAT_CACHE_DURATION.getTimeUnit())
            .recordStats()
            .build(new CacheLoader<String, Double>() {
                @Override
                public Double load(String key) throws Exception {
                    throw new IllegalAccessException("not suppose single query");
                }

                @Override
                public Map<String, Double> loadAll(Iterable<? extends String> keys) throws Exception {

                    return StringRedisHelper.of(nezhaStringRedisTemplate).valueMultiGet
                            (keys, Double.class, () -> 0D);
                }
            });


    /**
     * 当前小时统计缓存
     * key:各维度ke+yyyyyMMddHH
     */
    private LoadingCache<String, Double> currentHourStatCache = CacheBuilder.newBuilder()
            .expireAfterWrite(CURRENT_HOUR_STAT_CACHE_DURATION.getDuration(), CURRENT_HOUR_STAT_CACHE_DURATION.getTimeUnit())
            .recordStats()
            .build(new CacheLoader<String, Double>() {
                @Override
                public Double load(String key) throws Exception {
                    throw new IllegalAccessException("not suppose single query");
                }

                @Override
                public Map<String, Double> loadAll(Iterable<? extends String> keys) throws Exception {

                    return StringRedisHelper.of(nezhaStringRedisTemplate).valueMultiGet
                            (keys, Double.class, () -> 0D);
                }
            });

    @Override
    public Map<String, AdvertStatisticMergeEntity> getTagStat(Long appId, Set<String> tagIds) {
        try {
            DBTimeProfile.enter("tagStatAssociationService.getTagStat");
            Map<String, String> appTag2ChKeyMap = new HashMap<>(tagIds.size());
            Map<String, String> globalTag2ChKeyMap = new HashMap<>(tagIds.size());
            Map<String, String> appTag2CdKeyMap = new HashMap<>(tagIds.size());
            Map<String, String> globalTag2CdKeyMap = new HashMap<>(tagIds.size());
            Map<String, List<String>> appTag2Last6DayKeyMap = new HashMap<>(tagIds.size());
            Map<String, List<String>> globalTag2Last6DayKeyMap = new HashMap<>(tagIds.size());
            String nowHour = LocalDateTime.now().format(HOUR_FORMATTER);
            String nowDay = LocalDate.now().format(DAY_FORMATTER);
            for (String tagId : tagIds) {
                appTag2ChKeyMap.put(tagId, RedisKeyUtil.tagHourlyStatKey(appId, tagId, nowHour));
                globalTag2ChKeyMap.put(tagId, RedisKeyUtil.tagHourlyStatKey(null, tagId, nowHour));

                appTag2CdKeyMap.put(tagId, RedisKeyUtil.tagDailyStatKey(appId, tagId, nowDay));
                globalTag2CdKeyMap.put(tagId, RedisKeyUtil.tagDailyStatKey(null, tagId, nowDay));

                List<String> last6DayTimestamps = last6DayTimestampCache.get(nowDay);
                appTag2Last6DayKeyMap.put(tagId, last6DayTimestamps.stream().map(time -> RedisKeyUtil.tagDailyStatKey
                        (appId, tagId, time)).collect(Collectors.toList()));
                globalTag2Last6DayKeyMap.put(tagId, last6DayTimestamps.stream().map(time -> RedisKeyUtil.tagDailyStatKey
                        (null, tagId, time)).collect(Collectors.toList()));
            }

            Set<String> currentHourlyKeys = new HashSet<>(tagIds.size() * 2);
            currentHourlyKeys.addAll(appTag2ChKeyMap.values());
            currentHourlyKeys.addAll(globalTag2ChKeyMap.values());
            ImmutableMap<String, Double> currentHourStat = currentHourStatCache.getAll(currentHourlyKeys);


            Set<String> currentDayKeys = new HashSet<>(tagIds.size() * 2);
            currentDayKeys.addAll(appTag2CdKeyMap.values());
            currentDayKeys.addAll(globalTag2CdKeyMap.values());
            ImmutableMap<String, Double> currentDayStat = currentDayStatCache.getAll(currentDayKeys);

            Set<String> hourlyKeys = new HashSet<>(tagIds.size() * 12);
            hourlyKeys.addAll(appTag2Last6DayKeyMap.values().stream().flatMap(Collection::stream).collect(Collectors
                    .toList()));
            hourlyKeys.addAll(globalTag2Last6DayKeyMap.values().stream().flatMap(Collection::stream).collect(Collectors
                    .toList()));
            ImmutableMap<String, Double> hourlyStat = dailyStatCache.getAll(hourlyKeys);


            Map<String, AdvertStatisticMergeEntity> stat = new HashMap<>(tagIds.size());

            for (String tagId : tagIds) {
                Double appCh = currentHourStat.get(appTag2ChKeyMap.get(tagId));
                Double appCd = currentDayStat.get(appTag2CdKeyMap.get(tagId));
                Double app7d = Stream.concat(appTag2Last6DayKeyMap.get(tagId).stream().map(hourlyStat::get), Lists
                        .newArrayList(appCd).stream()).filter(Objects::nonNull).filter(value -> value > 0)
                        .mapToDouble(x -> x).average().orElse(0D);

                Double globalCh = currentHourStat.get(globalTag2ChKeyMap.get(tagId));
                Double globalCd = currentDayStat.get(globalTag2CdKeyMap.get(tagId));
                Double global7D = Stream.concat(globalTag2Last6DayKeyMap.get(tagId).stream().map(hourlyStat::get), Lists
                        .newArrayList(globalCd).stream()).filter(Objects::nonNull).filter(value -> value > 0)
                        .mapToDouble(x -> x).average().orElse(0D);

                stat.put(tagId, new AdvertStatisticMergeEntity.Builder().appCurrentlyHour(appCh).appCurrentlyDay
                        (appCd).appRecently7Day(app7d).globalCurrentlyHour(globalCh).globalCurrentlyDay(globalCd)
                        .globalRecently7Day(global7D).build());
            }

            return stat;
        } catch (Exception e) {
            LOGGER.error("get tag stat error:{}", e);
            return new HashMap<>();
        }finally {
            DBTimeProfile.release();
        }
    }

    @Override
    public Map<String, CacheInfo> getCacheInfo() {
        Map<String, CacheInfo> map = new HashMap<>();
        map.put("dailyStatCache", CacheInfo.generate(dailyStatCache));
        map.put("currentHourStatCache", CacheInfo.generate(currentHourStatCache));
        map.put("currentDayStatCache", CacheInfo.generate(currentDayStatCache));
        return map;
    }
}

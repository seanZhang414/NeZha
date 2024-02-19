package cn.com.duiba.nezha.engine.biz.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class CacheService extends BaseService {

    protected static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHH");

    protected static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Resource
    protected StringRedisTemplate nezhaStringRedisTemplate;

    @Autowired
    protected ExecutorService executorService;
    /**
     * 前6天时间戳缓存
     * key：yyyyMMdd
     */
    protected LoadingCache<String, List<String>> last6DayTimestampCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1L, TimeUnit.DAYS)
            .maximumSize(60)
            .build(new CacheLoader<String, List<String>>() {
                @Override
                public List<String> load(String key) throws Exception {
                    return Stream.iterate(LocalDate.now(), day -> day.minusDays(1))
                            .skip(1)// 跳过今日
                            .limit(6) // 前 N 日
                            .map(localDate -> localDate.format(DAY_FORMATTER)) // 日期格式化
                            .collect(toList());
                }
            });

    /**
     * 每小时时间戳缓存
     * key：yyyyMMdd
     */
    protected LoadingCache<String, List<String>> hourlyTimestampCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(48)
            .build(new CacheLoader<String, List<String>>() {
                @Override
                public List<String> load(String key) throws Exception {
                    return Stream.iterate(LocalDateTime.of(LocalDate.now(), LocalTime.MIN), time -> time.plusHours(1))
                            .limit(24)
                            .map(localDateTime -> localDateTime.format(HOUR_FORMATTER))
                            .collect(toList());
                }
            });

    public static class CacheDuration {
        private Long duration;
        private TimeUnit timeUnit;


        public CacheDuration(Long duration, TimeUnit timeUnit) {
            this.duration = duration;
            this.timeUnit = timeUnit;
        }

        public Long getDuration() {
            return duration;
        }

        public TimeUnit getTimeUnit() {
            return timeUnit;
        }

    }

    public static class CacheInfo {
        private Long size;
        private Double hitRate;
        private Double averageLoadPenalty;

        public CacheInfo(Long size, Double hitRate, Double averageLoadPenalty) {
            this.size = size;
            this.hitRate = hitRate;
            this.averageLoadPenalty = averageLoadPenalty;
        }

        public Long getSize() {
            return size;
        }

        public Double getHitRate() {
            return hitRate;
        }

        public Double getAverageLoadPenalty() {
            return averageLoadPenalty;
        }

        public static CacheInfo generate(LoadingCache cache) {
            return new CacheInfo(cache.size(), cache.stats().hitRate(), cache.stats().averageLoadPenalty() * 10E-6);
        }
    }

}

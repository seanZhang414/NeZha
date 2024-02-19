package cn.com.duiba.nezha.engine.biz.service.advert.feature.impl;

import cn.com.duiba.nezha.engine.biz.domain.ActivityFeatureDo;
import cn.com.duiba.nezha.engine.biz.service.advert.feature.ActivityFeatureService;
import cn.com.duiba.nezha.engine.common.utils.RedisKeyUtil;
import cn.com.duiba.wolf.perf.timeprofile.DBTimeProfile;
import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class ActivityFeatureServiceImpl implements ActivityFeatureService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityFeatureService.class);
    @Resource
    private StringRedisTemplate nezhaStringRedisTemplate;

    private LoadingCache<Long, ActivityFeatureDo> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(1L, TimeUnit.DAYS)
            .build(new CacheLoader<Long, ActivityFeatureDo>() {
                @Override
                public ActivityFeatureDo load(Long key) throws Exception {
                    return getFromRedis(key);
                }
            });


    private ActivityFeatureDo getFromRedis(Long activityId) {
        String activityFeatureKey = RedisKeyUtil.getActivityFeatureKey(activityId);
        return Optional.ofNullable(nezhaStringRedisTemplate.opsForValue().get(activityFeatureKey))
                .map(json -> JSON.parseObject(json, ActivityFeatureDo.class))
                .orElse(ActivityFeatureDo.DEFAULT);
    }

    @Override
    public ActivityFeatureDo get(Long activityId) {
        try {
            DBTimeProfile.enter("getActivityFeature");
            return cache.get(activityId);
        } catch (Exception e) {
            logger.error("get activityFeature happened:{}",e);
            return ActivityFeatureDo.DEFAULT;
        }finally {
            DBTimeProfile.release();
        }
    }

}

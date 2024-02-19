package cn.com.duiba.nezha.engine.biz.service.advert.material.impl;

import cn.com.duiba.nezha.engine.biz.service.advert.material.AdvertMaterialService;
import cn.com.duiba.nezha.engine.common.utils.MapUtils;
import cn.com.duiba.nezha.engine.common.utils.RedisKeyUtil;
import cn.com.duiba.wolf.perf.timeprofile.DBTimeProfile;
import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: AdvertMaterialServiceImpl.java , v 0.1 2017/7/12 下午2:10 ZhouFeng Exp $
 */
@Service
public class AdvertMaterialServiceImpl implements AdvertMaterialService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvertMaterialServiceImpl.class);

    @Resource(name = "nezhaStringRedisTemplate")
    private StringRedisTemplate stringRedisTemplate;

    private LoadingCache<String, List<Long>> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .build(new CacheLoader<String, List<Long>>() {

                @Override
                public List<Long> load(String key) throws Exception {
                    throw new IllegalAccessException("not suppose single query");
                }

                @Override
                public Map<String, List<Long>> loadAll(Iterable<? extends String> keys) throws Exception {
                    return loadAllRecord(keys);
                }
            });

    @Override
    public Map<Long, List<Long>> getMaterialRankList(Long appId, Collection<Long> advertIds) {
        try {
            DBTimeProfile.enter("getMaterialRankList");

            Map<Long, String> advertKeyMap = advertIds.stream().collect(Collectors.toMap(Function.identity(),
                    advertId -> RedisKeyUtil.materialRankList(appId, advertId)));
            return MapUtils.translate(advertKeyMap, cache.getAll(advertKeyMap.values()));
        } catch (Exception e) {
            LOGGER.error("load material rank list error:{}", e);
            return new HashMap<>();
        } finally {
            DBTimeProfile.release();
        }
    }


    private Map<String, List<Long>> loadAllRecord(Iterable<? extends String> ks) {

        Map<String, List<Long>> map = new HashMap<>();

        List<String> keys = Lists.newArrayList(ks);

        List<String> record = stringRedisTemplate.opsForValue().multiGet(keys);

        if (CollectionUtils.isEmpty(record)) {
            return map;
        }

        map = new HashMap<>(record.size());

        for (int index = 0; index < record.size(); index++) {

            String key = keys.get(index);
            String value = record.get(index);

            if (StringUtils.isNotBlank(value)) {
                map.put(key, JSON.parseArray(value, Long.class));
            } else {
                map.put(key, Collections.emptyList());
            }

        }

        return map;


    }

}

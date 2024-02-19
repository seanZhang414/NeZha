package cn.com.duiba.nezha.engine.biz.service.advert.ctr.impl;

import cn.com.duiba.nezha.engine.biz.domain.AdvertBaseStatDo;
import cn.com.duiba.nezha.engine.biz.domain.AdvertStatFeatureDo;
import cn.com.duiba.nezha.engine.biz.domain.advert.Advert;
import cn.com.duiba.nezha.engine.biz.service.advert.ctr.AdvertStatFeatureService;
import cn.com.duiba.nezha.engine.common.utils.RedisKeyUtil;
import cn.com.duiba.nezha.engine.common.utils.StringRedisHelper;
import cn.com.duiba.wolf.perf.timeprofile.DBTimeProfile;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: AdvertStatFeatureServiceImpl.java , v 0.1 2017/11/30 下午4:27 ZhouFeng Exp $
 */
@Service
public class AdvertStatFeatureServiceImpl implements AdvertStatFeatureService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvertStatFeatureServiceImpl.class);

    private static final String ADVERT_DIM = "ADVERT";
    private static final String ADVERT_APP_DIM = "ADVERT_APP";
    private static final String ADVERT_SLOT_DIM = "ADVERT_SLOT";
    private static final String ADVERT_ACTIVITY_DIM = "ADVERT_ACTIVITY_DIM";

    @Resource
    private StringRedisTemplate nezhaStringRedisTemplate;

    private LoadingCache<String, AdvertBaseStatDo> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(3000000)
            .build(new CacheLoader<String, AdvertBaseStatDo>() {
                @Override
                public AdvertBaseStatDo load(String key) {
                    throw new UnsupportedOperationException("not suppose single query");
                }

                @Override
                public Map<String, AdvertBaseStatDo> loadAll(Iterable<? extends String> keys) {
                    return StringRedisHelper.of(nezhaStringRedisTemplate).valueMultiGet(keys, AdvertBaseStatDo.class, AdvertBaseStatDo::new);
                }
            });

    @Override
    public Map<Long, AdvertStatFeatureDo> get(Long appId, Long slotId, Long activityId, Collection<Advert> adverts) {
        Map<Long, AdvertStatFeatureDo> advertId2FeatureDoMap = new HashMap<>();
        try {

            DBTimeProfile.enter("AdvertStatFeatureService.get");

            Map<Long, Map<String, String>> advertId2MultiDimKey = new HashMap<>(adverts.size());
            Set<String> allKeys = new HashSet<>(adverts.size() * 4);

            //建立key和广告id的映射,为了查询出结果后建立id和结果集的映射
            for (Advert advert : adverts) {
                Long id = advert.getId();
                Map<String, String> multiDimKey = getMultiDimKey(appId, slotId, activityId, id);
                advertId2MultiDimKey.put(id, multiDimKey);
                allKeys.addAll(multiDimKey.values());
            }


            ImmutableMap<String, AdvertBaseStatDo> keyFeatureMap = cache.getAll(allKeys);

            if (MapUtils.isEmpty(keyFeatureMap)) {
                return advertId2FeatureDoMap;
            }

            advertId2FeatureDoMap = new HashMap<>(keyFeatureMap.size());

            for (Advert advert : adverts) {

                Long id = advert.getId();
                Map<String, String> multiDimKey = advertId2MultiDimKey.get(id);

                AdvertStatFeatureDo featureDo = new AdvertStatFeatureDo(
                        keyFeatureMap.get(multiDimKey.get(ADVERT_DIM)),
                        keyFeatureMap.get(multiDimKey.get(ADVERT_APP_DIM)),
                        keyFeatureMap.get(multiDimKey.get(ADVERT_SLOT_DIM)),
                        keyFeatureMap.get(multiDimKey.get(ADVERT_ACTIVITY_DIM)));

                advertId2FeatureDoMap.put(id, featureDo);
            }
        } catch (Exception e) {
            LOGGER.error("get statistic feature error:{}", e);
        } finally {
            DBTimeProfile.release();
        }

        return advertId2FeatureDoMap;


    }


    private Map<String, String> getMultiDimKey(Long appId, Long slotId, Long activityId, Long advertId) {
        Map<String, String> map = new HashMap<>(4);
        map.put(ADVERT_DIM, RedisKeyUtil.advertStatFeatureKey(null, null, null, advertId));
        map.put(ADVERT_APP_DIM, RedisKeyUtil.advertStatFeatureKey(appId, null, null, advertId));
        map.put(ADVERT_SLOT_DIM, RedisKeyUtil.advertStatFeatureKey(null, null, slotId, advertId));
        map.put(ADVERT_ACTIVITY_DIM, RedisKeyUtil.advertStatFeatureKey(null, activityId, null, advertId));

        return map;
    }


}

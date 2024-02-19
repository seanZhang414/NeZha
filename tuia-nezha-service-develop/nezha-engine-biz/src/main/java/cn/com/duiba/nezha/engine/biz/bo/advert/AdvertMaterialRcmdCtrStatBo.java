package cn.com.duiba.nezha.engine.biz.bo.advert;

import cn.com.duiba.nezha.alg.alg.vo.StatDo;
import cn.com.duiba.nezha.engine.api.enums.ResultCodeEnum;
import cn.com.duiba.nezha.engine.api.support.RecommendEngineException;
import cn.com.duiba.nezha.engine.biz.constant.GlobalConstant;
import cn.com.duiba.nezha.engine.biz.entity.nezha.advert.AdvertMaterialRcmdCtrStatEntity;
import cn.com.duiba.nezha.engine.biz.service.advert.ctr.AdvertMergeStatService;
import cn.com.duiba.nezha.engine.biz.support.advert.AdvertCacheKey;
import cn.com.duiba.nezha.engine.common.cache.RedisUtil;
import cn.com.duiba.nezha.engine.common.utils.AssertUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by pc on 2016/10/18.
 */

@Service
public class AdvertMaterialRcmdCtrStatBo {

    private static final Logger logger = LoggerFactory.getLogger(AdvertMaterialRcmdCtrStatBo.class);

    private static final Long EXPIRE_TIME = 20 * 60L;

    @Resource
    private RedisUtil redisUtil;


    @Autowired
    private AdvertMergeStatService advertMergeStatService;

    public List<AdvertMaterialRcmdCtrStatEntity> getMaterials(long appId, long advertId, List<Long> materialIdList) {
        List<AdvertMaterialRcmdCtrStatEntity> advertMaterialRcmdCtrStatEntityList = null;
        if (AssertUtil.isAnyEmpty(appId, advertId, materialIdList)) {
            logger.warn("param invalid {}", ResultCodeEnum.PARAMS_INVALID.getDesc());
            return advertMaterialRcmdCtrStatEntityList;
        }

        try {

            // 获取缓存Key
            String key = AdvertCacheKey.getAdvertMaterialsStatKey(appId, advertId, materialIdList);

            // 从缓存获取推荐
            logger.debug("getMaterialList cache exits,key = {} read redis", key);
            advertMaterialRcmdCtrStatEntityList = redisUtil.getList(key, AdvertMaterialRcmdCtrStatEntity.class);

            //判断对应的推荐候选集是否存在于缓存中
            if (advertMaterialRcmdCtrStatEntityList == null) {
                // 从数据库获取推荐

                advertMaterialRcmdCtrStatEntityList = getAdvertMaterialRcmdCtrStatEntitiesFromMongo(appId, advertId,
                                                                                                    materialIdList);

                // 保存缓存
                redisUtil.setList(key, advertMaterialRcmdCtrStatEntityList, EXPIRE_TIME);

            }


        } catch (Exception e) {
            logger.warn("getMaterialList happen error:{}", e);
            throw new RecommendEngineException("getMaterialList happen error", e);
        }
        return advertMaterialRcmdCtrStatEntityList;
    }

    private List<AdvertMaterialRcmdCtrStatEntity> getAdvertMaterialRcmdCtrStatEntitiesFromMongo(long appId, long
    advertId, List<Long> materialIdList) {
        List<AdvertMaterialRcmdCtrStatEntity> advertMaterialRcmdCtrStatEntityList = new ArrayList<>();
        try {

            List<StatDo> advertMergeStatDoList = advertMergeStatService.get(advertId, appId, materialIdList);

            ImmutableMap<Long, StatDo> materialId2MergeStat = Maps.uniqueIndex(advertMergeStatDoList, StatDo::getMaterialId);

            ImmutableSet<Map.Entry<Long, StatDo>> entries = materialId2MergeStat.entrySet();

            for (Map.Entry<Long, StatDo> entry : entries) {

                Long materialId = entry.getKey();
                StatDo advertMergeStatDo = entry.getValue();

                Double ctr = Optional.ofNullable(advertMergeStatDo.getCtr()).orElse(GlobalConstant.INTERACT_DEFAULT_CTR);

                AdvertMaterialRcmdCtrStatEntity advertMaterialRcmdCtrStatEntity = new AdvertMaterialRcmdCtrStatEntity();

                advertMaterialRcmdCtrStatEntity.setAdvertId(advertId);
                advertMaterialRcmdCtrStatEntity.setAppId(appId);
                advertMaterialRcmdCtrStatEntity.setMaterialId(materialId);
                advertMaterialRcmdCtrStatEntity.setCtr(ctr);

                advertMaterialRcmdCtrStatEntityList.add(advertMaterialRcmdCtrStatEntity);

            }

        } catch (Exception e) {
            logger.info("new material query error:{}", e);
        }
        return advertMaterialRcmdCtrStatEntityList;
    }


}

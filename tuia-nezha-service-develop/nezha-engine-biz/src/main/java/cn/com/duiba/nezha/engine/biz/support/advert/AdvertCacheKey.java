package cn.com.duiba.nezha.engine.biz.support.advert;

import cn.com.duiba.nezha.engine.api.support.RecommendEngineException;
import cn.com.duiba.nezha.engine.common.utils.Md5Util;
import com.alibaba.fastjson.JSON;

import java.util.Collections;
import java.util.List;

/**
 * Created by pc on 2016/9/1.
 */
public class AdvertCacheKey {

    private static final String AD_MT_RCMD_MTS_STAT_KEY_PREFIX = "nz_ad_mt_rcmd_mt_stat_1_";

    private AdvertCacheKey() {
        throw new IllegalAccessError("AdvertCacheKey class");
    }


    /**
     * @param appId
     * @param advertId
     * @param materialIdList
     * @return
     */
    public static String getAdvertMaterialsStatKey(long appId, long advertId, List<Long> materialIdList) {
        String key = null;
        try {
            Collections.sort(materialIdList);
            String md5 = Md5Util.getMD5(JSON.toJSONString(materialIdList));
            if (md5 != null) {
                key = AD_MT_RCMD_MTS_STAT_KEY_PREFIX + appId + "_" + advertId + "_" + md5;
            }
        } catch (Exception e) {
            throw new RecommendEngineException("getAdvertStatKey happened error", e);
        }
        return key;
    }

}

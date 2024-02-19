package cn.com.duiba.nezha.engine.deploy.controller;

import cn.com.duiba.nezha.engine.biz.service.CacheService;
import cn.com.duiba.nezha.engine.biz.service.advert.ctr.AdvertMergeStatService;
import cn.com.duiba.nezha.engine.biz.service.advert.ctr.AdvertStatService;
import cn.com.duiba.nezha.engine.biz.service.advert.ctr.TagStatAssociationService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: AdvertStatCacheController.java , v 0.1 2017/10/20 上午11:28 ZhouFeng Exp $
 */
@RestController
@RequestMapping("/advertStatCache")
public class AdvertStatCacheController {

    @Autowired
    private AdvertMergeStatService advertMergeStatService;
    @Autowired
    private AdvertStatService advertStatService;
    @Autowired
    private TagStatAssociationService tagStatAssociationService;

    @RequestMapping("/cache")
    public String cache() {

        Map<String, Map<String, CacheService.CacheInfo>> cacheData = new HashMap<>();
        Map<String, CacheService.CacheInfo> cacheStats = advertMergeStatService.getCacheInfo();
        Map<String, CacheService.CacheInfo> cacheSize = advertStatService.getCacheInfo();
        Map<String, CacheService.CacheInfo> cacheSize1 = tagStatAssociationService.getCacheInfo();
        cacheData.put("advertMergeStatService", cacheStats);
        cacheData.put("advertStatService", cacheSize);
        cacheData.put("tagStatAssociationService", cacheSize1);
        return JSON.toJSONString(cacheData);
    }

}

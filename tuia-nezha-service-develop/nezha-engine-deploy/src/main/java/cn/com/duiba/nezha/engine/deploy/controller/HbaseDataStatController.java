package cn.com.duiba.nezha.engine.deploy.controller;

import cn.com.duiba.nezha.engine.biz.bo.hbase.ConsumerFeatureBo;
import cn.com.duiba.nezha.engine.biz.service.advert.feature.ConsumerFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: <a href="http://www.panaihua.com">panaihua</a>
 * @date: 2018年05月15日 11:28
 * @descript:
 * @version: 1.0
 */
@RestController
public class HbaseDataStatController {

    @Autowired
    private ConsumerFeatureService consumerFeatureService;

    @Autowired
    private ConsumerFeatureBo consumerFeatureBo;

    @GetMapping("/test/hbase/{consumeId}/{activityId}")
    public Object getConsume(@PathVariable("consumeId") Long consumeId, @PathVariable("activityId") Long activityId) {

        List<String> keys = new ArrayList<>();
        keys.add(consumerFeatureBo.getConsumerOrderFeatureKey(consumeId, null));
        keys.add(consumerFeatureBo.getConsumerOrderFeatureKey(consumeId, activityId));

        return consumerFeatureService.getFeatures(keys);
    }
}

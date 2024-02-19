package cn.com.duiba.nezha.engine.advert;

import cn.com.duiba.nezha.engine.BaseTest;
import cn.com.duiba.nezha.engine.biz.domain.*;
import cn.com.duiba.nezha.engine.biz.domain.advert.Advert;
import cn.com.duiba.nezha.engine.biz.service.advert.ctr.impl.AdvertPredictServiceImpl;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FeatureTest extends BaseTest {


    @Autowired
    private AdvertPredictServiceImpl advertPredictService;


    @Test
    public void test() {
        Collection<Advert> advert = CommendData.getAdvert();
        ActivityDo activityDo = new ActivityDo();
        activityDo.setOperatingId(1L);
        Map<FeatureIndex, Map<String, String>> featureMap = advertPredictService.getFeatureMap(advert,
                new ConsumerDo(), new AppDo(), activityDo, new RequestDo(), new HashMap<>(), new HashMap<>(), new HashMap<>());
        System.out.println(JSON.toJSONString(featureMap));
    }
}

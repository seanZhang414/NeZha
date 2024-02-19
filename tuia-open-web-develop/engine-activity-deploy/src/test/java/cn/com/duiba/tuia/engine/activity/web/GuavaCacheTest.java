package cn.com.duiba.tuia.engine.activity.web;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.com.duiba.nezha.compute.common.model.activityselect.ActivityInfo;
import cn.com.duiba.tuia.constant.RedisKeyConstant;
import cn.com.duiba.tuia.engine.activity.service.LocalCacheService;
import cn.com.duiba.tuia.ssp.center.api.constant.SplitConstant;
import cn.com.duiba.tuia.ssp.center.api.dto.IpAreaLibraryDto;
import cn.com.duiba.tuia.utils.GeoData;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Stopwatch;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import org.assertj.core.util.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(MockitoJUnitRunner.class)
public class GuavaCacheTest {


    private final LoadingCache<Long, Map<String, ActivityInfo>> ACTIVITYINFO_CACHE = CacheBuilder.newBuilder().initialCapacity(1000)
            .refreshAfterWrite(1, TimeUnit.HOURS).build(new CacheLoader<Long, Map<String, ActivityInfo>>() {
                @Override
                public Map<String, ActivityInfo> load(Long slotId)  {
                    Map<String, ActivityInfo> actInfoInRedis =  new HashMap<String, ActivityInfo>();
                    for (int i = 0; i <50 ; i++) {
                        ActivityInfo actInfo = new ActivityInfo();
                        actInfo.setActivityId(i);
                        actInfo.setSource(i);
                        actInfo.setSlotId(slotId);
                        actInfo.setRequest(actInfo.new Val());
                        actInfo.setSend(actInfo.new Val());
                        actInfo.setClick(actInfo.new Val());
                        actInfo.setHisClick(actInfo.new Val());
                        actInfo.setHisRequest(actInfo.new Val());
                        actInfo.setHisSend(actInfo.new Val());
                        actInfo.setLastClick(actInfo.new Val());
                        actInfo.setLastRequest(actInfo.new Val());
                        actInfo.setLastSend(actInfo.new Val());
                        actInfo.setDirectRequest(actInfo.new Val());
                        actInfo.setDirectSend(actInfo.new Val());
                        actInfo.setHisCost(actInfo.new Val());
                        actInfo.setLastCost(actInfo.new Val());
                        actInfo.setCost(actInfo.new Val());
                        actInfo.setDirectClick(actInfo.new Val());
                        actInfo.setDirectCost(actInfo.new Val());
                        actInfoInRedis.put(actInfo.getActivityId() + SplitConstant.SPLIT_HYPHEN + actInfo.getSource(),actInfo);
                    }
                    return actInfoInRedis;
                }
            });
    @Test
    public void testGeo() throws InterruptedException {
        for (long i = 0; i < 10000; i++) {
            ACTIVITYINFO_CACHE.getUnchecked(i);
            System.out.println(ACTIVITYINFO_CACHE.size());

        }
        Stopwatch stopwatch=Stopwatch.createStarted();
        for (long i = 0; i <1000 ; i++) {
           Boolean ct= ACTIVITYINFO_CACHE.asMap().containsKey(i);
        }
        System.out.println(stopwatch.elapsed(TimeUnit.MILLISECONDS));

    }
}

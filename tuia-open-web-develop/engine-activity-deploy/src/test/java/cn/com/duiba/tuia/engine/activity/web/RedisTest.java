package cn.com.duiba.tuia.engine.activity.web;

import cn.com.duiba.nezha.compute.common.model.activityselect.ActivityInfo;
import cn.com.duiba.tuia.constant.RedisKeyConstant;
import cn.com.duiba.tuia.engine.activity.handle.Redis3Handler;
import cn.com.duiba.tuia.engine.activity.handle.Redis4Handler;
import cn.com.duiba.tuia.ssp.center.api.constant.SplitConstant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    private Redis3Handler redis3Handler;
    @Autowired
    private Redis4Handler<String, String, ActivityInfo> redis4Handler;

    @Test
    public void testEntries() {
        ActivityInfo bestAct = new ActivityInfo();
        bestAct.setSlotId(11);
        bestAct.setActivityId(11);
        bestAct.setSource(1);
        String bestKey = bestAct.getActivityId() + SplitConstant.SPLIT_HYPHEN + bestAct.getSource();
        redis4Handler.hSet(RedisKeyConstant.getSlotActInfo(bestAct.getSlotId()), bestKey, bestAct);

        Map<String, ActivityInfo> entries = redis4Handler.entries(RedisKeyConstant.getSlotActInfo(bestAct.getSlotId()));
        System.out.println(entries);

        String[] split = "set 111 22".split(" ");
        byte[][] ss = new byte[split.length - 1][];
        for (int i = 0; i < split.length - 1; i++) {
            ss[i] = split[i + 1].getBytes();
        }
        System.out.println(redis3Handler.execute(split[0], ss));
        redis3Handler.flushAll();
    }
}

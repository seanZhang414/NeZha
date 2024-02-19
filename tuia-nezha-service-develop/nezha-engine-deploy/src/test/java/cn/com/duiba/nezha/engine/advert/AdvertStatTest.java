package cn.com.duiba.nezha.engine.advert;

import cn.com.duiba.nezha.engine.BaseTest;
import cn.com.duiba.nezha.engine.biz.domain.AdvertStatDo;
import cn.com.duiba.nezha.engine.biz.service.advert.ctr.AdvertStatService;
import cn.com.duiba.nezha.engine.common.utils.RedisKeyUtil;
import com.alibaba.fastjson.JSON;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

public class AdvertStatTest extends BaseTest {

    @Autowired
    private StringRedisTemplate nezhaStringRedisTemplate;
    @Autowired
    private AdvertStatService advertStatService;

    @Before
    public void before() {
        final long[] i = {0};
        Stream.iterate(LocalDate.now(), day -> day.minusDays(1))
                .limit(7) // 前 N 日
                .map(localDate -> localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))) // 日期格式化
                .map(time -> RedisKeyUtil.advertDailyStatKey(1L, 1L, null, null, null, time))
                .peek(System.out::println)
                .forEach(key -> {
                    i[0]++;
                    AdvertStatDo advertStatDo = new AdvertStatDo();
                    advertStatDo.setLaunchCnt(i[0]);
                    advertStatDo.setChargeCnt(i[0]);
                    advertStatDo.setActExpCnt(i[0]);
                    advertStatDo.setActClickCnt(i[0]);
                    advertStatDo.setChargeFees(i[0]);
                    nezhaStringRedisTemplate.opsForValue().set(key, JSON.toJSONString(advertStatDo));
                });
    }

    @Test
    public void test() {
        for (int i = 0; i < 2; i++) {

            AdvertStatService.Query query = new AdvertStatService.Query.Builder().appId(1L).advertId(1L).build();
            Map<AdvertStatService.Query, AdvertStatDo> dayStat = advertStatService.get7DayStat(Collections.singleton(query));
            System.out.println(JSON.toJSONString(dayStat.get(query)));
        }
    }
}

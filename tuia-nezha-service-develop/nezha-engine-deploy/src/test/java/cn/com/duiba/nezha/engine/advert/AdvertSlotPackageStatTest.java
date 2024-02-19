package cn.com.duiba.nezha.engine.advert;

import cn.com.duiba.nezha.engine.BaseTest;
import cn.com.duiba.nezha.engine.biz.domain.AdvertStatDo;
import cn.com.duiba.nezha.engine.biz.domain.advert.OrientationPackage;
import cn.com.duiba.nezha.engine.biz.service.advert.ctr.AdvertStatService;
import cn.com.duiba.nezha.engine.common.utils.RedisKeyUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashSet;
import java.util.Map;

public class AdvertSlotPackageStatTest extends BaseTest {

    @Autowired
    private StringRedisTemplate nezhaStringRedisTemplate;
    @Autowired
    private AdvertStatService advertStatService;

    @Before
    public void before() {
        String slotPackageDataKey = RedisKeyUtil.getSlotPackageDataKey(1L, 1L, 1L, 1L, "20181111");
        AdvertStatDo advertStatDo = new AdvertStatDo();
        advertStatDo.setActClickCnt(12L);
        advertStatDo.setChargeFees(23L);
        String value = JSON.toJSONString(advertStatDo);
        System.out.println(value);
        nezhaStringRedisTemplate.opsForValue().set(slotPackageDataKey, value);
    }

    @Test
    public void test() {
        OrientationPackage orientationPackage = new OrientationPackage();
        orientationPackage.setAdvertId(1L);
        orientationPackage.setId(1L);
        orientationPackage.setConvertCost(1L);
        HashSet<OrientationPackage> orientationPackages = Sets.newHashSet(orientationPackage);
        Map<OrientationPackage, AdvertStatDo> slotPackageData = advertStatService.getSlotPackageData(orientationPackages, 1L);
        System.out.println(JSON.toJSONString(slotPackageData.get(orientationPackage)));
    }
}

package UnitTest;

import cn.com.duiba.nezha.engine.biz.service.advert.material.AdvertMaterialService;
import cn.com.duiba.nezha.engine.biz.service.advert.material.impl.AdvertMaterialServiceImpl;
import com.alibaba.fastjson.JSON;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: AdvertMaterialServiceTest.java , v 0.1 2017/12/6 下午3:39 ZhouFeng Exp $
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AdvertMaterialService.class})
public class AdvertMaterialServiceTest {

    @InjectMocks
    private AdvertMaterialServiceImpl advertMaterialService;


    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> stringStringValueOperations;


    @Test
    public void test() {


        List<Long> advertIds = CtrMergeParamters.advertIds;

        Map<Long, List<Long>> map = new HashMap<>(advertIds.size());

        for (Long advertId : advertIds) {

            int times = new Random().nextInt(10) + 1;

            List<Long> materialId = new ArrayList<>(times);

            for (int i = 0; i < times; i++) {
                materialId.add((long) new Random().nextInt(10000));
            }
            map.put(advertId, materialId);
        }


        List<String> record = map.values().stream().map(JSON::toJSONString).collect(Collectors.toList());

        Mockito.when(stringRedisTemplate.opsForValue()).thenReturn(stringStringValueOperations);
        Mockito.when(stringStringValueOperations.multiGet(Matchers.anyList())).thenReturn(record);


        Map<Long, List<Long>> materialRankList = advertMaterialService.getMaterialRankList(1L, advertIds);


        for (Map.Entry<Long, List<Long>> entry : materialRankList.entrySet()) {

            Assert.assertEquals(map.get(entry.getKey()), entry.getValue());

        }


    }


}

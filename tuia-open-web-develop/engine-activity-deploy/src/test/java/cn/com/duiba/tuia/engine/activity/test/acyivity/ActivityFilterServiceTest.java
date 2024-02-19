package cn.com.duiba.tuia.engine.activity.test.acyivity;

import cn.com.duiba.tuia.constant.FlowSplitConstant;
import cn.com.duiba.tuia.engine.activity.remoteservice.SystemConfigService;
import cn.com.duiba.tuia.engine.activity.service.ActivityFilterService;
import cn.com.duiba.tuia.engine.activity.service.impl.ActivityFilterServiceImpl;
import cn.com.duiba.tuia.engine.activity.test.base.BaseJunit4Test;
import cn.com.duiba.tuia.ssp.center.api.dto.ActivitySpmDto;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ActivityFilterServiceTest extends BaseJunit4Test {

    @Autowired
    ActivityFilterService activityFilterService;

    /**
     *
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Test
    public void testSortBySPM() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int NUM = 20;

        Random random = new Random();
        Long[] randomValues = new Long[NUM];
        for (int i = 0; i < NUM; i++) {
            randomValues[i] = random.nextLong();
        }

        List<ActivitySpmDto> spmDtoList = new ArrayList<>();
        for (int i = 0; i < NUM; i++) {
            ActivitySpmDto activitySpmDto = new ActivitySpmDto();
            activitySpmDto.setSpm(randomValues[i]);
            spmDtoList.add(activitySpmDto);
        }

        Arrays.sort(randomValues);

        Method method = ActivityFilterServiceImpl.class.getDeclaredMethod("sortBySPM", List.class);
        method.setAccessible(true);
        method.invoke(activityFilterService, spmDtoList);

        Long[] actuals = new Long[NUM];
        for (int i = 0; i < NUM; i++) {
            actuals[i] = spmDtoList.get(NUM - i - 1).getSpm();
        }

        Assert.assertArrayEquals(randomValues, actuals);

        randomValues[0] = null;
        randomValues[1] = 20L;
        randomValues[2] = null;
        randomValues[3] = 9L;
        randomValues[4] = null;
        randomValues[5] = 10L;
//        randomValues[0] = null;
//        randomValues[1] = null;
//        randomValues[2] = null;
//        randomValues[3] = null;
//        randomValues[4] = null;
//        randomValues[5] = null;

        List<ActivitySpmDto> spmDtoList2 = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            ActivitySpmDto activitySpmDto = new ActivitySpmDto();
            activitySpmDto.setSpm(randomValues[i]);
            spmDtoList2.add(activitySpmDto);
        }

        method.invoke(activityFilterService, spmDtoList2);

        Assert.assertArrayEquals(new Long[]{20L, 10L, 9L, null, null, null}, new Long[]{spmDtoList2.get(0).getSpm(), spmDtoList2.get(1).getSpm(), spmDtoList2.get(2).getSpm(), spmDtoList2.get(3).getSpm(), spmDtoList2.get(4).getSpm(), spmDtoList2.get(5).getSpm()});
//        Assert.assertArrayEquals(new Long[]{null, null, null, null, null, null}, new Long[]{spmDtoList2.get(0).getSpm(), spmDtoList2.get(1).getSpm(), spmDtoList2.get(2).getSpm(), spmDtoList2.get(3).getSpm(), spmDtoList2.get(4).getSpm(), spmDtoList2.get(5).getSpm()});

    }

    @Test
    public void testGetSpmList() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        activityFilterService.getNextActivity(39L, null, FlowSplitConstant.ACT_ENGINE_OUTPUT);
    }

    @Autowired
    SystemConfigService systemConfigService;
    @Test
    public void testGetTuiaHost(){
        Assert.assertTrue("tuia_value1".equals(systemConfigService.getTuiaHost("tuia_key1")));
    }
}

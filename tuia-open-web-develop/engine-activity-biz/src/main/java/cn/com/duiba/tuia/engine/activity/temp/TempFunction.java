/**
 * Copyright (c) 2018, duiba.com.cn All Rights Reserved.
 */
package cn.com.duiba.tuia.engine.activity.temp;

import cn.com.duiba.tuia.engine.activity.service.LocalCacheService;
import cn.com.duiba.tuia.ssp.center.api.dto.IpAreaLibraryDto;
import com.google.common.collect.Sets;
import java.util.Set;

/**
 * 描述: 一些临时的测试需求。
 * <p>
 * 测试完成后直接删除
 *
 * @version v1.0
 * @auther guopengfei@duiba.com.cn
 * @date 2018/6/7 15:00
 */
public class TempFunction {

    public static final Long QS_ACTIVITY_ID_DOWNGRADE = 7510L;

    public static final Long QS_DIRECT_ID_ONE = 1286L;

    public static final Long QS_DIRECT_ID_TWO = 1284L;

    public static final Long QS_DIRECT_ID_THREE = 1285L;

    public static final Long QS_DIRECT_ID_FOUR = 1287L;

    public static final Long QS2_DIRECT_ID_ONE = 1293L;

    public static final Long QS2_DIRECT_ID_TWO = 1294L;

    public static final Long QS2_DIRECT_ID_THREE = 1295L;

    public static final Long QS2_DIRECT_ID_FOUR = 1292L;

    public static final String TEST_CITY = "北京、上海、广州、深圳、天津、杭州、厦门、成都、南京";

    public static final String GD_PROVINCE = "广东";

    public static final String SC_PROVINCE = "四川";

    public static final String ANY_TAG = "*";

    public static Integer getCityLevel(LocalCacheService localCacheService, String ipStr) {
        IpAreaLibraryDto ipAreaLibraryDto = localCacheService.findByIpLong(IpAreaLibraryDto.convertIpLong(ipStr));
        if (TempFunction.TEST_CITY.contains(ipAreaLibraryDto.getCity()) || specialCheck(ipAreaLibraryDto)) {
            //一 二线为1
            return 1;
        } else {
            // 其余城市均为2
            return 2;
        }
    }

    /**
     * 成都、广州
     * 特殊处理
     *
     * @param dto
     * @return
     */
    private static boolean specialCheck(IpAreaLibraryDto dto) {
        if (TempFunction.SC_PROVINCE.equals(dto.getProvince()) && TempFunction.ANY_TAG.equals(dto.getCity())) {
            return true;
        }
        if (TempFunction.GD_PROVINCE.equals(dto.getProvince()) && TempFunction.ANY_TAG.equals(dto.getCity())) {
            return true;
        }
        return false;
    }

    /**
     * 用户维度行为喜好测试
     */
    public static final Set<Long> USER_BEHAVIOR_ADVERT_TEST = Sets.newHashSet(3895L, 4508L, 2139L, 2768L, 406L, 1956L,779L,2140L,3721L,339L,3378L,4139L,2243L,405L,2535L,2815L,4134L,739L,1045L,3738L,1201L,1114L,1113L,3719L,2242L,2871L,1611L,2623L,2626L,184136L,4941L,6452L,7987L,8641L);


    /**
     * 用户维度行为三期测试
     */
    public static final Set<Long> USER_BEHAVIOR_ADVERT_3_TEST = Sets.newHashSet(184136L,4941L,6452L,7987L,8641L);

    public static final Integer ACCESS_SECOND = 2;

    public static final Integer ACCESS_THREE = 3;

    public static final Integer ACCESS_FIVE = 5;

    public static final Integer ACCESS_SIX = 6;

    public static final Integer ACCESS_SEVEN = 7;

    public static final Integer FAIL_PART_IN = 0;

    /** test env : 754L  */
    public static final Long ACCESS_SECOND_ACTIVITY = 1223L;

    /** test env : 758L  */
    public static final Long ACCESS_THREE_ACTIVITY = 1222L;

    /** test env : 260L */
    public static final Long ACCESS_FIVE_ACTIVITY = 1221L;

    /** test env : 759L */
    public static final Long ACCESS_SIX_ACTIVITY = 1310L;

    public static final String USER_ACTIVITY_PRE_TEST_REDIS_FLAG = "PRE_";

    public static final String USER_ACTIVITY_HIT_TEST_REDIS_FLAG = "HIT_";

}

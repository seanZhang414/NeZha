package cn.com.duiba.nezha.engine.biz.remoteservice.impl.advert;

import cn.com.duiba.nezha.alg.alg.vo.NezhaStatDto;
import cn.com.duiba.nezha.alg.alg.vo.StatDo;
import cn.com.duiba.nezha.engine.BaseTest;
import cn.com.duiba.nezha.engine.advert.CommendData;
import cn.com.duiba.nezha.engine.api.dto.*;
import cn.com.duiba.nezha.engine.api.enums.InteractAdvertAlgEnum;
import cn.com.duiba.nezha.engine.api.enums.RedisKey;
import cn.com.duiba.nezha.engine.api.remoteservice.advert.RemoteAdvertRecommendService;
import cn.com.duiba.nezha.engine.biz.domain.AdvertBaseStatDo;
import cn.com.duiba.nezha.engine.biz.domain.AdvertStatDo;
import cn.com.duiba.nezha.engine.common.utils.RedisKeyUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: RemoteAdvertRecommendServiceTest.java , v 0.1 2017/11/8 下午12:02 ZhouFeng Exp $
 */
public class RemoteAdvertRecommendServiceTestNew extends BaseTest {

    @Autowired
    RemoteAdvertRecommendService remoteAdvertRecommendService;

    @Resource
    private StringRedisTemplate nezhaStringRedisTemplate;

    private static Long appId = 12345L;
    private static Long slotId = 11223344L;
    private static Long activityId = 22222L;


    @Before
    public void prepareData() {
        List<AdvertNewDto> advertNewDtos = advertList();

        advertNewDtos.forEach(advertNewDto -> {

            Long advertId = advertNewDto.getAdvertId();
            Long currentCount = advertNewDto.getCurrentCount();
            Long packageId = advertNewDto.getPackageId();


            Map<String, Long> appBackendMap = new HashMap<>();
            Map<String, Long> globalBackendMap = new HashMap<>();

            boolean hasBackendData = new Random().nextInt(10) % 2 == 1;
            if (hasBackendData) {
                appBackendMap.put("1", (long) new Random().nextInt(30) + 30);
                appBackendMap.put("4", (long) new Random().nextInt(10) + 10);
                globalBackendMap.put("1", (long) new Random().nextInt(700) + 700);
                globalBackendMap.put("4", (long) new Random().nextInt(40) + 30);
            }


            // app上的发券数
            int appLaunchCount = new Random().nextInt(1000) + 500;
            // app上的计费点击数
            int appChargeCount = new Random().nextInt(appLaunchCount);
            // app上的落地页曝光数
            int appExposeCount = new Random().nextInt(appChargeCount);
            // app上的转化数
            int appEffectClickCount = new Random().nextInt(appExposeCount);



            // 融合数据(app)
            String k46AppData = RedisKeyUtil.advertMergeStatKey(appId, advertId, null, currentCount);
            StatDo advertMergeStatDo = new StatDo();
            advertMergeStatDo.setBackendCntMap(Maps.newHashMap());
            advertMergeStatDo.setAppId(appId);
            advertMergeStatDo.setAdvertId(advertId);
            advertMergeStatDo.setMaterialId(null);
            advertMergeStatDo.setTimes(currentCount);
            advertMergeStatDo.setLaunchCnt((long) appLaunchCount);
            advertMergeStatDo.setChargeCnt((long) appChargeCount);
            advertMergeStatDo.setActExpCnt((long) appExposeCount);
            advertMergeStatDo.setActClickCnt((long) appEffectClickCount);
            advertMergeStatDo.setCtr(advertMergeStatDo.getChargeCnt().doubleValue() / advertMergeStatDo.getLaunchCnt());
            advertMergeStatDo.setCvr(advertMergeStatDo.getActClickCnt().doubleValue() / advertMergeStatDo.getActExpCnt());
            advertMergeStatDo.setBackendCntMap(appBackendMap);
            String app纬度融合数据json = JSON.toJSONString(advertMergeStatDo);
            System.out.println(app纬度融合数据json);
            nezhaStringRedisTemplate.opsForValue().set(k46AppData, app纬度融合数据json);


            // app上的发券数
            int globalLaunchCount = new Random().nextInt(10000) + 2000;
            // app上的计费点击数
            int globalChargeCount = new Random().nextInt(globalLaunchCount);
            // app上的落地页曝光数
            int globalExposeCount = new Random().nextInt(globalChargeCount);
            // app上的转化数
            int globalEffectClickCount = new Random().nextInt(globalExposeCount);

            String k46GlobalData = RedisKeyUtil.advertMergeStatKey(null, advertId, null, currentCount);
            StatDo advertMergeStatDoGlobal = new StatDo();
            advertMergeStatDoGlobal.setBackendCntMap(Maps.newHashMap());
            advertMergeStatDoGlobal.setAppId(null);
            advertMergeStatDoGlobal.setAdvertId(advertId);
            advertMergeStatDoGlobal.setMaterialId(null);
            advertMergeStatDoGlobal.setTimes(currentCount);
            advertMergeStatDoGlobal.setLaunchCnt((long) globalLaunchCount);
            advertMergeStatDoGlobal.setChargeCnt((long) globalChargeCount);
            advertMergeStatDoGlobal.setActExpCnt((long) globalExposeCount);
            advertMergeStatDoGlobal.setActClickCnt((long) globalEffectClickCount);
            advertMergeStatDoGlobal.setCtr(advertMergeStatDoGlobal.getChargeCnt().doubleValue() / advertMergeStatDoGlobal.getLaunchCnt());
            advertMergeStatDoGlobal.setCvr(advertMergeStatDoGlobal.getActClickCnt().doubleValue() / advertMergeStatDoGlobal.getActExpCnt());
            advertMergeStatDoGlobal.setBackendCntMap(globalBackendMap);
            String 全局广告融合数据 = JSON.toJSONString(advertMergeStatDoGlobal);
            System.out.println(全局广告融合数据);
            nezhaStringRedisTemplate.opsForValue().set(k46GlobalData, 全局广告融合数据);

            // 特征数据
            String advertKey = RedisKeyUtil.advertStatFeatureKey(null, null, null, advertId);
            String advertAppKey = RedisKeyUtil.advertStatFeatureKey(appId, null, null, advertId);
            String advertSlotKey = RedisKeyUtil.advertStatFeatureKey(null, null, slotId, advertId);
            String advertActivityKey = RedisKeyUtil.advertStatFeatureKey(null, activityId, null, advertId);

            int i1 = new Random().nextInt(300);
            int i2 = new Random().nextInt(300) + i1;
            int i3 = new Random().nextInt(300) + i2;
            AdvertBaseStatDo advert = new AdvertBaseStatDo();
            advert.setCtr(i2 / (double) i3);
            advert.setCvr(i1 / (double) i3);
            nezhaStringRedisTemplate.opsForValue().set(advertKey, JSON.toJSONString(advert));

            int i4 = new Random().nextInt(300);
            int i5 = new Random().nextInt(300) + i4;
            int i6 = new Random().nextInt(300) + i5;
            AdvertBaseStatDo advertApp = new AdvertBaseStatDo();
            advertApp.setCtr(i5 / (double) i6);
            advertApp.setCvr(i4 / (double) i6);
            nezhaStringRedisTemplate.opsForValue().set(advertAppKey, JSON.toJSONString(advertApp));

            int i7 = new Random().nextInt(300);
            int i8 = new Random().nextInt(300) + i7;
            int i9 = new Random().nextInt(300) + i8;
            AdvertBaseStatDo advertSlot = new AdvertBaseStatDo();
            advertSlot.setCtr(i8 / (double) i9);
            advertSlot.setCvr(i7 / (double) i9);
            nezhaStringRedisTemplate.opsForValue().set(advertSlotKey, JSON.toJSONString(advertSlot));

            int i10 = new Random().nextInt(300);
            int i11 = new Random().nextInt(300) + i10;
            int i12 = new Random().nextInt(300) + i11;
            AdvertBaseStatDo advertActivity = new AdvertBaseStatDo();
            advertActivity.setCtr(i11 / (double) i12);
            advertActivity.setCvr(i10 / (double) i12);
            nezhaStringRedisTemplate.opsForValue().set(advertActivityKey, JSON.toJSONString(advertActivity));

            // 纠偏数据
            for (InteractAdvertAlgEnum interactAdvertAlgEnum : InteractAdvertAlgEnum.values()) {
                long algType = interactAdvertAlgEnum.getType().longValue();
                String nezhaStatKey = RedisKeyUtil.getNezhaStatKey(algType, advertId, appId);
                String nezhaStatKey1 = RedisKeyUtil.getNezhaStatKey(RedisKey.K67, algType, advertId, appId, slotId);
                String nezhaStatKey2 = RedisKeyUtil.getNezhaStatKey(RedisKey.K68, algType, advertId, appId, slotId);

                NezhaStatDto nezhaStatDto = new NezhaStatDto();
                nezhaStatDto.setId(nezhaStatKey);
                nezhaStatDto.setAlgType(algType);
                nezhaStatDto.setAdvertId(advertId);
                nezhaStatDto.setAppId(appId);
                nezhaStatDto.setCtrLaunchCnt(13L);
                nezhaStatDto.setCvrLaunchCnt(7L);
                nezhaStatDto.setPreCtrAcc(4.32D);
                nezhaStatDto.setPreCvrAcc(2.123D);
                nezhaStatDto.setPreCtrAvg(0.2492D);
                nezhaStatDto.setPreCvrAvg(0.1432D);
                nezhaStatDto.setStatCtrAcc(3.43D);
                nezhaStatDto.setStatCvrAcc(2.03D);
                nezhaStatDto.setStatCtrAvg(3.821D);
                nezhaStatDto.setStatCvrAvg(1.8650D);

                NezhaStatDto nezhaStatDto1 = new NezhaStatDto();
                nezhaStatDto1.setId(nezhaStatKey1);
                nezhaStatDto1.setAlgType(algType);
                nezhaStatDto1.setAdvertId(advertId);
                nezhaStatDto1.setAppId(appId);
                nezhaStatDto1.setCtrLaunchCnt(12L);
                nezhaStatDto1.setCvrLaunchCnt(8L);
                nezhaStatDto1.setPreCtrAcc(4.32D);
                nezhaStatDto1.setPreCvrAcc(2.123D);
                nezhaStatDto1.setPreCtrAvg(0.2492D);
                nezhaStatDto1.setPreCvrAvg(0.1432D);
                nezhaStatDto1.setStatCtrAcc(3.43D);
                nezhaStatDto1.setStatCvrAcc(2.03D);
                nezhaStatDto1.setStatCtrAvg(3.821D);
                nezhaStatDto1.setStatCvrAvg(1.8650D);

                NezhaStatDto nezhaStatDto2 = new NezhaStatDto();
                nezhaStatDto2.setId(nezhaStatKey2);
                nezhaStatDto2.setAlgType(algType);
                nezhaStatDto2.setAdvertId(advertId);
                nezhaStatDto2.setAppId(appId);
                nezhaStatDto2.setCtrLaunchCnt(17L);
                nezhaStatDto2.setCvrLaunchCnt(12L);
                nezhaStatDto2.setPreCtrAcc(5.32D);
                nezhaStatDto2.setPreCvrAcc(3.123D);
                nezhaStatDto2.setPreCtrAvg(0.2492D);
                nezhaStatDto2.setPreCvrAvg(0.1432D);
                nezhaStatDto2.setStatCtrAcc(4.43D);
                nezhaStatDto2.setStatCvrAcc(3.03D);
                nezhaStatDto2.setStatCtrAvg(3.821D);
                nezhaStatDto2.setStatCvrAvg(1.8650D);

                nezhaStringRedisTemplate.opsForValue().set(nezhaStatKey, JSON.toJSONString(nezhaStatDto));
                nezhaStringRedisTemplate.opsForValue().set(nezhaStatKey1, JSON.toJSONString(nezhaStatDto1));
                nezhaStringRedisTemplate.opsForValue().set(nezhaStatKey2, JSON.toJSONString(nezhaStatDto2));
            }

            //小时数据
            Stream.iterate(LocalDateTime.of(LocalDate.now(), LocalTime.MIN), localDateTime -> localDateTime.plusHours(1))
                    .limit(LocalDateTime.now().getHour() + 1)
                    .map(localDateTime -> localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHH")))
                    .forEach(timestamp -> {
                        String appAdvertPackageHourKey = RedisKeyUtil.advertHourlyStatKey(appId, advertId, packageId, null, timestamp);
                        String advertPackageHourKey = RedisKeyUtil.advertHourlyStatKey(null, advertId, packageId, null, timestamp);
                        String appAdvertHourKey = RedisKeyUtil.advertHourlyStatKey(appId, advertId, null, null, timestamp);
                        String advertHourKey = RedisKeyUtil.advertHourlyStatKey(null, advertId, null, null, timestamp);

                        long appAdvertPackageLaunchCount = (new Random().nextInt(500) + 500) / 12 + 1;
                        long appAdvertPackageChargeCount = new Random().nextInt((int) appAdvertPackageLaunchCount) + 1;
                        long appAdvertPackageActCnt = new Random().nextInt((int) appAdvertPackageChargeCount) + 1;
                        long appAdvertPackageActClickCnt = new Random().nextInt((int) appAdvertPackageActCnt) + 1;
                        long appAdvertPackageFees = (new Random().nextInt(3000) + 2000) / 12 + 1;
                        AdvertStatDo appAdvertPackageStatDo = new AdvertStatDo();
                        appAdvertPackageStatDo.setAppId(appId);
                        appAdvertPackageStatDo.setAdvertId(advertId);
                        appAdvertPackageStatDo.setLaunchCnt(appAdvertPackageLaunchCount);
                        appAdvertPackageStatDo.setChargeCnt(appAdvertPackageChargeCount);
                        appAdvertPackageStatDo.setActExpCnt(appAdvertPackageActCnt);
                        appAdvertPackageStatDo.setActClickCnt(appAdvertPackageActClickCnt);
                        appAdvertPackageStatDo.setChargeFees(appAdvertPackageFees);
                        appAdvertPackageStatDo.setCtr(((double) appAdvertPackageStatDo.getChargeCnt()) / appAdvertPackageStatDo.getLaunchCnt());
                        appAdvertPackageStatDo.setCvr(((double) appAdvertPackageStatDo.getActClickCnt()) / appAdvertPackageStatDo.getActExpCnt());


                        long advertPackageLaunchCount = ((new Random().nextInt(500) + 500) / 12 + 1) * 2;
                        long advertPackageChargeCount = new Random().nextInt((int) advertPackageLaunchCount) + 1;
                        long advertPackageActCnt = new Random().nextInt((int) advertPackageChargeCount) + 1;
                        long advertPackageActClickCnt = new Random().nextInt((int) advertPackageActCnt) + 1;
                        long advertPackageFees = ((new Random().nextInt(3000) + 2000) / 12 + 1) * 2;
                        AdvertStatDo advertPackageStatDo = new AdvertStatDo();
                        advertPackageStatDo.setAppId(appId);
                        advertPackageStatDo.setAdvertId(advertId);
                        advertPackageStatDo.setLaunchCnt(advertPackageLaunchCount);
                        advertPackageStatDo.setChargeCnt(advertPackageChargeCount);
                        advertPackageStatDo.setActExpCnt(advertPackageActCnt);
                        advertPackageStatDo.setActClickCnt(advertPackageActClickCnt);
                        advertPackageStatDo.setChargeFees(advertPackageFees);
                        advertPackageStatDo.setCtr(((double) advertPackageStatDo.getChargeCnt()) / advertPackageStatDo.getLaunchCnt());
                        advertPackageStatDo.setCvr(((double) advertPackageStatDo.getActClickCnt()) / advertPackageStatDo.getActExpCnt());

                        long appAdvertLaunchCount = ((new Random().nextInt(500) + 500) / 12 + 1) * 3;
                        long appAdvertChargeCount = new Random().nextInt((int) appAdvertLaunchCount) + 1;
                        long appAdvertActCnt = new Random().nextInt((int) appAdvertChargeCount) + 1;
                        long appAdvertActClickCnt = new Random().nextInt((int) appAdvertActCnt) + 1;
                        long appAdvertFees = ((new Random().nextInt(3000) + 2000) / 12 + 1) * 3;
                        AdvertStatDo appAdvertStatDo = new AdvertStatDo();
                        appAdvertStatDo.setAppId(appId);
                        appAdvertStatDo.setAdvertId(advertId);
                        appAdvertStatDo.setLaunchCnt(appAdvertLaunchCount);
                        appAdvertStatDo.setChargeCnt(appAdvertChargeCount);
                        appAdvertStatDo.setActExpCnt(appAdvertActCnt);
                        appAdvertStatDo.setActClickCnt(appAdvertActClickCnt);
                        appAdvertStatDo.setChargeFees(appAdvertFees);
                        appAdvertStatDo.setCtr(((double) appAdvertStatDo.getChargeCnt()) / appAdvertStatDo.getLaunchCnt());
                        appAdvertStatDo.setCvr(((double) appAdvertStatDo.getActClickCnt()) / appAdvertStatDo.getActExpCnt());


                        long advertLaunchCount = ((new Random().nextInt(500) + 500) / 12 + 1) * 4;
                        long advertChargeCount = new Random().nextInt((int) appAdvertLaunchCount) + 1;
                        long advertActCnt = new Random().nextInt((int) appAdvertChargeCount) + 1;
                        long advertActClickCnt = new Random().nextInt((int) appAdvertActCnt) + 1;
                        long advertFees = ((new Random().nextInt(3000) + 2000) / 12 + 1) * 4;
                        AdvertStatDo advertStatDo = new AdvertStatDo();
                        advertStatDo.setAppId(appId);
                        advertStatDo.setAdvertId(advertId);
                        advertStatDo.setLaunchCnt(advertLaunchCount);
                        advertStatDo.setChargeCnt(advertChargeCount);
                        advertStatDo.setActExpCnt(advertActCnt);
                        advertStatDo.setActClickCnt(advertActClickCnt);
                        advertStatDo.setChargeFees(advertFees);
                        advertStatDo.setCtr(((double) advertStatDo.getChargeCnt()) / advertStatDo.getLaunchCnt());
                        advertStatDo.setCvr(((double) advertStatDo.getActClickCnt()) / advertStatDo.getActExpCnt());


                        nezhaStringRedisTemplate.opsForValue().set(appAdvertPackageHourKey, JSON.toJSONString(appAdvertPackageStatDo));
                        nezhaStringRedisTemplate.opsForValue().set(advertPackageHourKey, JSON.toJSONString(advertPackageStatDo));
                        nezhaStringRedisTemplate.opsForValue().set(appAdvertHourKey, JSON.toJSONString(appAdvertStatDo));
                        nezhaStringRedisTemplate.opsForValue().set(advertHourKey, JSON.toJSONString(advertStatDo));
                    });

            // 天数据
            Stream.iterate(LocalDate.now(), day -> day.minusDays(1)).limit(7).map(localDate -> localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).forEach(day -> {
                String appAdvertPackageDailyStatKey = RedisKeyUtil.advertDailyStatKey(appId, advertId, packageId, null, null, day);
                String advertPackageDailyStatKey = RedisKeyUtil.advertDailyStatKey(null, advertId, packageId, null, null, day);
                String appAdvertDailyStatKey = RedisKeyUtil.advertDailyStatKey(appId, advertId, null, null, null, day);
                String advertDailyStatKey = RedisKeyUtil.advertDailyStatKey(null, advertId, null, null, null, day);

                long appAdvertPackageLaunchCount = new Random().nextInt(5000) + 5000;
                long appAdvertPackageChargeCount = new Random().nextInt((int) appAdvertPackageLaunchCount) + 1;
                long appAdvertPackageActCnt = new Random().nextInt((int) appAdvertPackageChargeCount) + 1;
                long appAdvertPackageActClickCnt = new Random().nextInt((int) appAdvertPackageActCnt) + 1;
                long appAdvertPackageFees = new Random().nextInt(30000) + 2000;
                AdvertStatDo appAdvertPackageStatDo = new AdvertStatDo();
                appAdvertPackageStatDo.setAppId(appId);
                appAdvertPackageStatDo.setAdvertId(advertId);
                appAdvertPackageStatDo.setLaunchCnt(appAdvertPackageLaunchCount);
                appAdvertPackageStatDo.setChargeCnt(appAdvertPackageChargeCount);
                appAdvertPackageStatDo.setActExpCnt(appAdvertPackageActCnt);
                appAdvertPackageStatDo.setActClickCnt(appAdvertPackageActClickCnt);
                appAdvertPackageStatDo.setChargeFees(appAdvertPackageFees);
                appAdvertPackageStatDo.setCtr(((double) appAdvertPackageStatDo.getChargeCnt()) / appAdvertPackageStatDo.getLaunchCnt());
                appAdvertPackageStatDo.setCvr(((double) appAdvertPackageStatDo.getActClickCnt()) / appAdvertPackageStatDo.getActExpCnt());


                long advertPackageLaunchCount = (new Random().nextInt(5000) + 5000) * 2;
                long advertPackageChargeCount = new Random().nextInt((int) advertPackageLaunchCount) + 1;
                long advertPackageActCnt = new Random().nextInt((int) advertPackageChargeCount) + 1;
                long advertPackageActClickCnt = new Random().nextInt((int) advertPackageActCnt) + 1;
                long advertPackageFees = (new Random().nextInt(30000) + 2000) * 2;
                AdvertStatDo advertPackageStatDo = new AdvertStatDo();
                advertPackageStatDo.setAppId(appId);
                advertPackageStatDo.setAdvertId(advertId);
                advertPackageStatDo.setLaunchCnt(advertPackageLaunchCount);
                advertPackageStatDo.setChargeCnt(advertPackageChargeCount);
                advertPackageStatDo.setActExpCnt(advertPackageActCnt);
                advertPackageStatDo.setActClickCnt(advertPackageActClickCnt);
                advertPackageStatDo.setChargeFees(advertPackageFees);
                advertPackageStatDo.setCtr(((double) advertPackageStatDo.getChargeCnt()) / advertPackageStatDo.getLaunchCnt());
                advertPackageStatDo.setCvr(((double) advertPackageStatDo.getActClickCnt()) / advertPackageStatDo.getActExpCnt());

                long appAdvertLaunchCount = (new Random().nextInt(5000) + 5000) * 3;
                long appAdvertChargeCount = new Random().nextInt((int) appAdvertLaunchCount) + 1;
                long appAdvertActCnt = new Random().nextInt((int) appAdvertChargeCount) + 1;
                long appAdvertActClickCnt = new Random().nextInt((int) appAdvertActCnt) + 1;
                long appAdvertFees = (new Random().nextInt(30000) + 2000) * 3;
                AdvertStatDo appAdvertStatDo = new AdvertStatDo();
                appAdvertStatDo.setAppId(appId);
                appAdvertStatDo.setAdvertId(advertId);
                appAdvertStatDo.setLaunchCnt(appAdvertLaunchCount);
                appAdvertStatDo.setChargeCnt(appAdvertChargeCount);
                appAdvertStatDo.setActExpCnt(appAdvertActCnt);
                appAdvertStatDo.setActClickCnt(appAdvertActClickCnt);
                appAdvertStatDo.setChargeFees(appAdvertFees);
                appAdvertStatDo.setCtr(((double) appAdvertStatDo.getChargeCnt()) / appAdvertStatDo.getLaunchCnt());
                appAdvertStatDo.setCvr(((double) appAdvertStatDo.getActClickCnt()) / appAdvertStatDo.getActExpCnt());


                long advertLaunchCount = (new Random().nextInt(5000) + 5000) * 4;
                long advertChargeCount = new Random().nextInt((int) advertLaunchCount) + 1;
                long advertActCnt = new Random().nextInt((int) advertChargeCount) + 1;
                long advertActClickCnt = new Random().nextInt((int) advertActCnt) + 1;
                long advertFees = (new Random().nextInt(30000) + 2000) * 4;
                AdvertStatDo advertStatDo = new AdvertStatDo();
                advertStatDo.setAppId(appId);
                advertStatDo.setAdvertId(advertId);
                advertStatDo.setLaunchCnt(advertLaunchCount);
                advertStatDo.setChargeCnt(advertChargeCount);
                advertStatDo.setActExpCnt(advertActCnt);
                advertStatDo.setActClickCnt(advertActClickCnt);
                advertStatDo.setChargeFees(advertFees);
                advertStatDo.setCtr(((double) advertStatDo.getChargeCnt()) / advertStatDo.getLaunchCnt());
                advertStatDo.setCvr(((double) advertStatDo.getActClickCnt()) / advertStatDo.getActExpCnt());

                nezhaStringRedisTemplate.opsForValue().set(appAdvertPackageDailyStatKey, JSON.toJSONString(appAdvertPackageStatDo));
                nezhaStringRedisTemplate.opsForValue().set(advertPackageDailyStatKey, JSON.toJSONString(advertPackageStatDo));
                nezhaStringRedisTemplate.opsForValue().set(appAdvertDailyStatKey, JSON.toJSONString(appAdvertStatDo));
                nezhaStringRedisTemplate.opsForValue().set(advertDailyStatKey, JSON.toJSONString(advertStatDo));

            });

        });


    }


    private ReqAdvertNewDto createRequest() {
        ReqAdvertNewDto dto = new ReqAdvertNewDto();

        // 媒体信息
        AppDto appDto = new AppDto();
        appDto.setAppId(appId);
        appDto.setSlotId(slotId);
        appDto.setTrafficTagId("TrafficTagId");
        appDto.setTrafficTagPid("TrafficTagPid");
        appDto.setAppIndustryTagId("AppIndustryTagId");
        appDto.setAppIndustryTagPid("AppIndustryTagPId");
        appDto.setSlotIndustryTagId("SlotIndustryTagId");
        appDto.setSlotIndustryTagPid("SlotIndustryTagPid");
        dto.setAppDto(appDto);

        AdvertActivityDto advertActivityDto = new AdvertActivityDto();
        advertActivityDto.setOperatingActivityId(activityId);
        advertActivityDto.setActivityUseType(0);
        advertActivityDto.setActivityId(activityId);
        advertActivityDto.setActivityType(1L);
        advertActivityDto.setTag("activityTag");
        dto.setAdvertActivityDto(advertActivityDto);

        RequestDto requestDto = new RequestDto();
        requestDto.setUa("ua");
        requestDto.setIp("ip");
        requestDto.setCityId(0L);
        requestDto.setOrderId("order");
        requestDto.setPriceSection("0-999");
        requestDto.setPutIndex(1L);
        requestDto.setModel("nex");
        requestDto.setConnectionType("2G");
        requestDto.setOperatorType("中国移动");
        requestDto.setPhoneBrand("oppo");
        requestDto.setPhoneModel("PhoneModel");
        dto.setRequestDto(requestDto);


        ConsumerDto consumerDto = new ConsumerDto();
        consumerDto.setConsumerId(540803720L);
        consumerDto.setMemberId(12L);
        consumerDto.setShipArea("ShipArea");
        consumerDto.setUserLastlogbigintime(new Date());
        consumerDto.setMobileprivate("Mobileprivate");
        consumerDto.setDeviceId("DeviceId");
        consumerDto.setInstallApps(Lists.newArrayList("a.b.c.android.xiaoshuo.hhsqgy", "aa.aaaaa.android", "addingfriend.net.mail", "air.com.flipline.mypapaswingeria1"));
        consumerDto.setTagStats(Lists.newArrayList());
        consumerDto.setClickIntersredTags("ClickIntersredTags");
        consumerDto.setConverIntersredTags("ConverIntersredTags");
        consumerDto.setClickUnintersredTags("ClickUnintersredTags");
        consumerDto.setConverUnintersredTags("ConverUnintersredTags");
        consumerDto.setSex("男");
        consumerDto.setAge("28");
        consumerDto.setWorkStatus("白领");
        consumerDto.setStudentStatus("大专");
        consumerDto.setMarriageStatus("未婚");
        consumerDto.setBear("未育");
        consumerDto.setIntersetList(Lists.newArrayList("1", "2"));
        dto.setConsumerDto(consumerDto);

        dto.setAdvertList(this.advertList());

        return dto;
    }

    private ReqAdvertNewDto createBatchRequest(){
        ReqAdvertNewDto request = this.createRequest();
        request.getRequestDto().setExistCount(1L);
        request.getRequestDto().setNeedCount(2L);
        request.getRequestDto().setOrderIds(Lists.newArrayList("1","2"));
        return request;
    }

    private List<AdvertNewDto> advertList() {

        return CommendData.advertList();

    }

    @Test
    public void test() {
        InteractAdvertAlgEnum[] values = InteractAdvertAlgEnum.values();
        for (InteractAdvertAlgEnum interactAdvertAlgEnum : values) {
            System.out.println(interactAdvertAlgEnum);
            remoteAdvertRecommendService.recommend(this.createRequest(), interactAdvertAlgEnum.getType().toString());
        }
    }
    @Test
    public void batchTest(){
        remoteAdvertRecommendService.batchRecommend(this.createBatchRequest(), "301");
    }

    @Test
    public void materialPredictTest(){
        remoteAdvertRecommendService.recommend(this.createRequest(), InteractAdvertAlgEnum.BTM_AND_PC_34.getType().toString());
    }
}

package cn.com.duiba.nezha.compute.biz.server.biz;

import cn.com.duiba.nezha.compute.api.cachekey.AdvertStatKey;
import cn.com.duiba.nezha.compute.api.constant.GlobalConstant;
import cn.com.duiba.nezha.compute.api.dto.AdvertAppStatDto;
import cn.com.duiba.nezha.compute.api.dto.AdvertCtrStatDto;
import cn.com.duiba.nezha.compute.api.enums.AdvertStatDimTypeEnum;
import cn.com.duiba.nezha.compute.api.enums.StatIntervalTypeEnum;
import cn.com.duiba.nezha.compute.api.enums.StatTypeEnum;
import cn.com.duiba.nezha.compute.biz.bo.AdvertStatBo;
import cn.com.duiba.nezha.compute.biz.constant.htable.AdvertStatConstant;
import cn.com.duiba.nezha.compute.biz.log.AdvertOrderLog;
import cn.com.duiba.nezha.compute.biz.vo.AdvertCtrStatVo;
import cn.com.duiba.nezha.compute.biz.vo.AdvertStatVo;
import cn.com.duiba.nezha.compute.biz.vo.MapVo;
import cn.com.duiba.nezha.compute.common.enums.DateStyle;
import cn.com.duiba.nezha.compute.common.params.Params;
import cn.com.duiba.nezha.compute.common.util.DateUtil;
import cn.com.duiba.nezha.compute.common.util.MyStringUtil2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AdvertStatUpdateServer {


    /**
     * @param advertStatVoList
     */
    public static void syncES(List<AdvertStatVo> advertStatVoList, String biz) {
        try {

            List<AdvertAppStatDto> advertAppStatDtoList = new ArrayList<>();
            List<AdvertCtrStatDto> advertCtrStatDtoList = new ArrayList<>();

            if (advertStatVoList != null) {
                for (AdvertStatVo advertStatVo : advertStatVoList) {
                    if (advertStatVo.getAdvertAppStatDtoList() != null) {
                        advertAppStatDtoList.addAll(advertStatVo.getAdvertAppStatDtoList());
                    }
                    if (advertStatVo.getAdvertCtrStatDtoList() != null) {
                        advertCtrStatDtoList.addAll(advertStatVo.getAdvertCtrStatDtoList());
                    }

                }
            }


            AdvertStatBo.syncAdvertAppStat(advertAppStatDtoList, GlobalConstant.ADVERT_STAT_COLLECTION_NAME, biz);

            AdvertStatBo.syncAdvertCtrStat(advertCtrStatDtoList, GlobalConstant.ADVERT_INTERVAL_STAT_COLLECTION_NAME, biz);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * @param advertStatVo
     */
    public static void syncES(AdvertStatVo advertStatVo, String biz) {
        try {
            syncES(Arrays.asList(advertStatVo), biz);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 推啊广告发券处理
     *
     * @param advertOrderLog
     * @throws Exception
     */
    public static AdvertStatVo logProcess(AdvertOrderLog advertOrderLog, String family, Params.AdvertLogParams params) {

        AdvertStatVo advertStatVo = new AdvertStatVo();

        int statType = params.statType();
        boolean isTest = params.isTest();

        try {
            advertStatVo.setAppId(advertOrderLog.getAppId());

            Long times = getAdvertTimes(advertOrderLog.getLogExtMap());

            if (statType == StatTypeEnum.ADVERT.getIndex() || statType == StatTypeEnum.ADVERT_AND_MATERIAL.getIndex()) {

                // 0 广告 && 媒体 times = 0
                AdvertStatVo advertStatVo0 = logSubProcess(advertOrderLog,
                        advertOrderLog.getAdvertId(),
                        null,
                        advertOrderLog.getGmtDate(),
                        advertOrderLog.getCurrentTime(),
                        StatTypeEnum.ADVERT,
                        0L,
                        AdvertStatDimTypeEnum.APP,
                        advertOrderLog.getAppId(),
                        family,
                        advertOrderLog.getAppId(),
                        advertOrderLog.getTime(),
                        isTest,
                        true);

                if (advertStatVo0 != null) {
                    advertStatVo.addAdvertAppStatDtoList(advertStatVo0.getAdvertAppStatDtoList());
                    advertStatVo.addAdvertCtrStatDtoList(advertStatVo0.getAdvertCtrStatDtoList());
                }


                // 1 广告 && 媒体
                AdvertStatVo advertStatVo1 = logSubProcess(advertOrderLog,
                        advertOrderLog.getAdvertId(),
                        null,
                        advertOrderLog.getGmtDate(),
                        advertOrderLog.getCurrentTime(),
                        StatTypeEnum.ADVERT,
                        times,
                        AdvertStatDimTypeEnum.APP,
                        advertOrderLog.getAppId(),
                        family,
                        advertOrderLog.getAppId(),
                        advertOrderLog.getTime(),
                        isTest,
                        false);

                if (advertStatVo1 != null) {
                    advertStatVo.addAdvertAppStatDtoList(advertStatVo1.getAdvertAppStatDtoList());
                    advertStatVo.addAdvertCtrStatDtoList(advertStatVo1.getAdvertCtrStatDtoList());
                }


                // 2 广告 && 全局
                AdvertStatVo advertStatVo2 = logSubProcess(advertOrderLog,
                        advertOrderLog.getAdvertId(),
                        null,
                        advertOrderLog.getGmtDate(),
                        advertOrderLog.getCurrentTime(),
                        StatTypeEnum.ADVERT,
                        times,
                        AdvertStatDimTypeEnum.GLOBAL,
                        AdvertStatConstant.COL_GLOBAL,
                        family,
                        advertOrderLog.getAppId(),
                        advertOrderLog.getTime(),
                        isTest,
                        false);
                if (advertStatVo2 != null) {
                    advertStatVo.addAdvertAppStatDtoList(advertStatVo2.getAdvertAppStatDtoList());
                    advertStatVo.addAdvertCtrStatDtoList(advertStatVo2.getAdvertCtrStatDtoList());
                }


                // 3 媒体
                AdvertStatVo advertStatVo4 = logSubProcess(advertOrderLog,
                        "0",
                        null,
                        advertOrderLog.getGmtDate(),
                        advertOrderLog.getCurrentTime(),
                        StatTypeEnum.ADVERT,
                        times,
                        AdvertStatDimTypeEnum.APP,
                        advertOrderLog.getAppId(),
                        family,
                        advertOrderLog.getAppId(),
                        advertOrderLog.getTime(),
                        isTest,
                        false);

                if (advertStatVo4 != null) {
                    advertStatVo.addAdvertAppStatDtoList(advertStatVo4.getAdvertAppStatDtoList());
                    advertStatVo.addAdvertCtrStatDtoList(advertStatVo4.getAdvertCtrStatDtoList());
                }


            }


            // 素材统计
            if (statType == StatTypeEnum.MATERIAL.getIndex() || statType == StatTypeEnum.ADVERT_AND_MATERIAL.getIndex()) {
                // a 素材 && 媒体
                AdvertStatVo materialStatVo1 = logSubProcess(advertOrderLog,
                        advertOrderLog.getAdvertId(),
                        advertOrderLog.getMaterialId(),
                        advertOrderLog.getGmtDate(),
                        advertOrderLog.getCurrentTime(),
                        StatTypeEnum.MATERIAL,
                        times,
                        AdvertStatDimTypeEnum.APP,
                        advertOrderLog.getAppId(),
                        family,
                        advertOrderLog.getAppId(),
                        advertOrderLog.getTime(),
                        isTest,
                        false);

                if (materialStatVo1 != null) {
                    advertStatVo.addAdvertAppStatDtoList(materialStatVo1.getAdvertAppStatDtoList());
                    advertStatVo.addAdvertCtrStatDtoList(materialStatVo1.getAdvertCtrStatDtoList());
                }


                // b 素材 && 全局
                AdvertStatVo materialStatVo2 = logSubProcess(advertOrderLog,
                        advertOrderLog.getAdvertId(),
                        advertOrderLog.getMaterialId(),
                        advertOrderLog.getGmtDate(),
                        advertOrderLog.getCurrentTime(),
                        StatTypeEnum.MATERIAL,
                        times,
                        AdvertStatDimTypeEnum.GLOBAL,
                        AdvertStatConstant.COL_GLOBAL,
                        family,
                        advertOrderLog.getAppId(),
                        advertOrderLog.getTime(),
                        isTest,
                        false);

                if (materialStatVo2 != null) {
                    advertStatVo.addAdvertAppStatDtoList(materialStatVo2.getAdvertAppStatDtoList());
                    advertStatVo.addAdvertCtrStatDtoList(materialStatVo2.getAdvertCtrStatDtoList());
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return advertStatVo;
    }

    /**
     * 推啊广告发券处理 按时间维度处理
     *
     * @throws Exception
     */
    public static AdvertStatVo logSubProcess(AdvertOrderLog advertOrderLog,
                                             String advertId,
                                             String materialId,
                                             String gmtDate,
                                             String currentTime,
                                             StatTypeEnum statTypeEnum,
                                             Long advertTimes,
                                             AdvertStatDimTypeEnum advertStatDimTypeEnum,
                                             String statDimId,
                                             String family,
                                             String appId,
                                             String time,
                                             boolean isTest,
                                             boolean hourStat
    ) {
        AdvertStatVo ret = new AdvertStatVo();

        try {

            // 1 更新 计数 全局、日粒度、小时粒度
            if (!isTest) {
                AdvertStatBo.incrementCount(advertId, materialId, statTypeEnum, advertTimes, advertStatDimTypeEnum, statDimId, family, currentTime, time, advertOrderLog.getFee());
            }
            // 2 读取 特征数据 全局和日粒度(读取多日)
            ret = prepareSyncDate(advertOrderLog, advertId, materialId, time, currentTime, statTypeEnum, advertTimes, advertStatDimTypeEnum, statDimId, appId, hourStat);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }


    /**
     * 推啊广告计费处理
     *
     * @throws Exception
     */
    public static AdvertStatVo prepareSyncDate(AdvertOrderLog advertOrderLog,
                                               String advertId,
                                               String materialId,
                                               String gmtTime,
                                               String currentTime,
                                               StatTypeEnum statTypeEnum,
                                               Long advertTimes,
                                               AdvertStatDimTypeEnum advertStatDimTypeEnum,
                                               String statDimId,
                                               String appId,
                                               boolean hourStat) {
        AdvertStatVo advertStatVo = new AdvertStatVo();
        try {

            // 读取当日起的近期数据
            AdvertStatVo advertStatVoRet = prepareSyncStatDate(advertOrderLog,
                    advertId,
                    materialId,
                    gmtTime,
                    currentTime,
                    advertTimes,
                    advertStatDimTypeEnum,
                    statDimId,
                    appId,
                    hourStat);
            advertStatVo.addAdvertAppStatDtoList(advertStatVoRet.getAdvertAppStatDtoList());
            advertStatVo.addAdvertCtrStatDtoList(advertStatVoRet.getAdvertCtrStatDtoList());
//            System.out.println("advertStatVo= "+JSON.toJSONString(advertStatVo));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return advertStatVo;
    }


    /**
     * 推啊广告计费处理
     *
     * @throws Exception
     */
    public static AdvertStatVo prepareSyncStatDate(AdvertOrderLog advertOrderLog,
                                                   String advertId,
                                                   String materialId,
                                                   String gmtTime,
                                                   String currentTime,
                                                   Long advertTimes,
                                                   AdvertStatDimTypeEnum advertStatDimTypeEnum,
                                                   String statDimId,
                                                   String appId,
                                                   boolean hourStat) {

        AdvertStatVo advertStatVo = new AdvertStatVo();

        AdvertAppStatDto advertAppStatDto = new AdvertAppStatDto();

        try {

            // mongodb
            String docKey = AdvertStatKey.getAdvertStatMongoDbKey(advertId, materialId, statDimId, advertTimes);

            advertAppStatDto.setKey(docKey);
            advertAppStatDto.setAdvertId(MyStringUtil2.string2Long(advertId));

            advertAppStatDto.setMaterialId(MyStringUtil2.string2Long(materialId));

            if (advertStatDimTypeEnum == AdvertStatDimTypeEnum.APP) {
                advertAppStatDto.setAppId(MyStringUtil2.string2Long(appId));
            }

            // 读取 特征数据 全局和发券日期的数据

            // 读取当日起的近期数据

            Map<Long, String> dateMap = DateUtil.getDaySubTimeStringMap(currentTime, DateStyle.YYYY_MM_DD_HH_MM_SS, DateStyle.YYYY_MM_DD, StatIntervalTypeEnum.RECENT_7_DAY.getIndex());
            Map<Long, String> hourMap = DateUtil.getHourSubTimeStringMap(currentTime, DateStyle.YYYY_MM_DD_HH_MM_SS, DateStyle.YYYY_MM_DD_HH, StatIntervalTypeEnum.RECENT_2_HOUR.getIndex());
////
//            System.out.println("dateMap"+JSON.toString(dateMap));
//            System.out.println("hourMap"+JSON.toString(hourMap));

            Map<Long, AdvertCtrStatDto> advertStatDtoDateMap = AdvertStatBo.getAdvertCtrStatDto(
                    advertId,
                    materialId,
                    advertTimes,
                    advertStatDimTypeEnum,
                    statDimId,
                    dateMap);

            Map<Long, AdvertCtrStatDto> advertStatDtoHourMap = AdvertStatBo.getAdvertCtrStatDto(
                    advertId,
                    materialId,
                    advertTimes,
                    advertStatDimTypeEnum,
                    statDimId,
                    hourMap);
//            System.out.println("advertStatDtoHourMap="+ JSON.toJSONString(advertStatDtoHourMap));

            // 计算当日CTR
            AdvertCtrStatVo advertCdCtrStatVo = getIntervalWeightCtr(advertId,
                    appId,
                    materialId,
                    advertTimes,
                    advertStatDimTypeEnum,
                    statDimId,
                    advertStatDtoDateMap,
                    StatIntervalTypeEnum.CURRENT_DAY,
                    StatIntervalTypeEnum.CURRENT_DAY,
                    1000
            );
            if (advertCdCtrStatVo != null) {

                advertAppStatDto.setAppCdStat(advertCdCtrStatVo.getAdvertCtrStatDto());

            }


            // 计算 近2日CTR
            AdvertCtrStatVo advertR2dCtrStatVo = getIntervalWeightCtr(advertId,
                    appId,
                    materialId,
                    advertTimes,
                    advertStatDimTypeEnum,
                    statDimId,
                    advertStatDtoDateMap,
                    StatIntervalTypeEnum.CURRENT_DAY,
                    StatIntervalTypeEnum.RECENT_2_DAY,
                    1000
            );

            if (advertR2dCtrStatVo != null) {

                advertAppStatDto.setAppR2dStat(advertR2dCtrStatVo.getAdvertCtrStatDto());
            }


            // 计算 近7日CTR
            AdvertCtrStatVo advertR7dCtrStatVo = getIntervalWeightCtr(advertId,
                    appId,
                    materialId,
                    advertTimes,
                    advertStatDimTypeEnum,
                    statDimId,
                    advertStatDtoDateMap,
                    StatIntervalTypeEnum.CURRENT_DAY,
                    StatIntervalTypeEnum.RECENT_7_DAY,
                    1000
            );

            if (advertR7dCtrStatVo != null) {

                advertAppStatDto.setAppR7dStat(advertR7dCtrStatVo.getAdvertCtrStatDto());

            }


            // 计算当小时CTR
            AdvertCtrStatVo advertChStatVo = getIntervalWeightCtr(advertId,
                    appId,
                    materialId,
                    advertTimes,
                    advertStatDimTypeEnum,
                    statDimId,
                    advertStatDtoHourMap,
                    StatIntervalTypeEnum.CURRENT_HOUR,
                    StatIntervalTypeEnum.CURRENT_HOUR,
                    500
            );
            if (advertChStatVo != null) {
                advertAppStatDto.setAppChStat(advertChStatVo.getAdvertCtrStatDto());

            }


            // 计算 近2小时CTR
            AdvertCtrStatVo advertR2hStatVo = getIntervalWeightCtr(advertId,
                    appId,
                    materialId,
                    advertTimes,
                    advertStatDimTypeEnum,
                    statDimId,
                    advertStatDtoHourMap,
                    StatIntervalTypeEnum.CURRENT_HOUR,
                    StatIntervalTypeEnum.RECENT_2_HOUR,
                    500
            );

            if (advertR2hStatVo != null) {

                advertAppStatDto.setAppR2hStat(advertR2hStatVo.getAdvertCtrStatDto());
            }


            // 每小时数据


//            System.out.println("advertCtrStatVoList=" + JSON.toJSONString(advertCtrStatVoList));


            advertStatVo.getAdvertAppStatDtoList().add(advertAppStatDto);


            if (hourStat) {
                // 计算当小时CTR hour 数据
                AdvertCtrStatVo advertHourStatVo = getIntervalWeightCtr(advertId,
                        appId,
                        materialId,
                        advertTimes,
                        advertStatDimTypeEnum,
                        statDimId,
                        advertStatDtoHourMap,
                        StatIntervalTypeEnum.CURRENT_HOUR,
                        StatIntervalTypeEnum.CURRENT_HOUR,
                        1
                );
                if (advertHourStatVo != null) {
                    if (advertHourStatVo.getAdvertCtrStatDto() != null) {
                        String timeInterval = getHourTime(gmtTime);
//                    System.out.println("timeInterval"+timeInterval);
                        String id = AdvertStatKey.getAdvertSubStatMongoDbKey(advertId, appId, timeInterval);
                        advertHourStatVo.getAdvertCtrStatDto().setId(id);
                        advertStatVo.addAdvertCtrStatDtoList(Arrays.asList(advertHourStatVo.getAdvertCtrStatDto()));

//                    System.out.println("appId" + appId);
//                    if(advertId.equals("7260")){
//                    System.out.println("2222222222222   advertHourStatVo.getAdvertCtrStatDto()" + JSONObject.toJSONString(advertHourStatVo.getAdvertCtrStatDto()));
//                    }
                    }
                }
            }




        } catch (Exception e) {
            e.printStackTrace();
        }
        return advertStatVo;
    }


    /**
     * @param advertCtrStatDtoMap
     * @return
     */
    public static AdvertCtrStatVo getIntervalWeightCtr(String advertId,
                                                       String appId,
                                                       String materialId,
                                                       Long advertTimes,
                                                       AdvertStatDimTypeEnum advertStatDimTypeEnum,
                                                       String statDimId,
                                                       Map<Long, AdvertCtrStatDto> advertCtrStatDtoMap,
                                                       StatIntervalTypeEnum startCtrStatTypeEnum,
                                                       StatIntervalTypeEnum endCtrStatTypeEnum,
                                                       long confidenceThreshold
    ) {
        AdvertCtrStatVo ret = null;
        try {
            AdvertCtrStatDto advertCtrStatDto = getWeightCtr(
                    advertCtrStatDtoMap,
                    startCtrStatTypeEnum.getIndex(),
                    endCtrStatTypeEnum.getIndex());


            if (advertCtrStatDto != null && confidence(advertCtrStatDto.getLaunchCnt(), confidenceThreshold)) {


                ret = new AdvertCtrStatVo();
                ret.setAdvertId(advertId);
                ret.setMaterial(materialId);
                ret.setAdvertType(advertTimes + "");
                ret.setStatDimId(statDimId);
                ret.setAdvertStatDimType(advertStatDimTypeEnum.getDesc());
                ret.setStatIntervalId(endCtrStatTypeEnum.getDesc());
                ret.setAdvertCtrStatDto(advertCtrStatDto);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }


    /**
     * @param advertCtrStatDtoMap
     * @return
     */
    public static AdvertCtrStatDto getWeightCtr(Map<Long, AdvertCtrStatDto> advertCtrStatDtoMap,
                                                long startInterval,
                                                long endInterval
    ) {
        AdvertCtrStatDto ret = null;
        try {

            if (advertCtrStatDtoMap != null) {
                ret = new AdvertCtrStatDto();

                for (Map.Entry<Long, AdvertCtrStatDto> entry : advertCtrStatDtoMap.entrySet()) {
                    Long interval = entry.getKey();
                    AdvertCtrStatDto dto = entry.getValue();
                    if (interval >= startInterval && interval <= endInterval) {

                        ret.setLaunchCnt(addLongCnt(
                                        ret.getLaunchCnt(),
                                        dto.getLaunchCnt())
                        );

                        ret.setChargeCnt(addLongCnt(
                                        ret.getChargeCnt(),
                                        dto.getChargeCnt())
                        );

                        // cvr
                        ret.setActClickCnt(addLongCnt(
                                        ret.getActClickCnt(),
                                        dto.getActClickCnt())
                        );

                        ret.setActExpCnt(addLongCnt(
                                        ret.getActExpCnt(),
                                        dto.getActExpCnt())
                        );

                        // fee
                        ret.setChargeFees(addLongCnt(
                                        ret.getChargeFees(),
                                        dto.getChargeFees())
                        );


                    }
                }


                ret.setCtr(AdvertStatBo.getCtr(ret.getChargeCnt(), ret.getLaunchCnt(), 5));
                ret.setCvr(AdvertStatBo.getCtr(ret.getActClickCnt(), ret.getChargeCnt(), 5));
                ret.setTime(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }


    /**
     * 判断广告对用户的发券类型   首次发  重复发
     *
     * @param mapVo
     * @return
     */
    public static Long getAdvertTimes(MapVo mapVo) {
        Long ret = 1L;
        try {
            if (null != mapVo) {
                mapVo.getTimes();
                Long ret2 = mapVo.getTimes();
                if (ret2 != null) {
                    ret = ret2;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }


    public static String getHourTime(String gmtTime) {

        String ret = null;
        try {

            if (gmtTime != null) {
                ret = DateUtil.getDateTime(DateUtil.getDate(gmtTime, DateStyle.YYYY_MM_DD_HH_MM_SS), DateStyle.YYYYMMDDHH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }


    /**
     * 判断广告对用户的发券类型   首次发  重复发
     *
     * @return
     */
    public static Long addLongCnt(Long cnt1, Long cnt2) {
        Long ret = 0L;
        try {
            if (cnt1 != null) {
                ret = ret + cnt1;
            }
            if (cnt2 != null) {
                ret = ret + cnt2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }


    /**
     * 判断广告对用户的发券类型   首次发  重复发
     *
     * @return
     */
    public static boolean confidence(Long launchCnt, long confidenceThreshold) {
        boolean ret = false;
        try {
            if (launchCnt != null && launchCnt >= confidenceThreshold) {
                ret = true;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }


}

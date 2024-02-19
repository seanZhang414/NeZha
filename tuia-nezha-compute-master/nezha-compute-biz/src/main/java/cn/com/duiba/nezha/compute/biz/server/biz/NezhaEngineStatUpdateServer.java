package cn.com.duiba.nezha.compute.biz.server.biz;

import cn.com.duiba.nezha.compute.api.cachekey.NezhaStatKey;
import cn.com.duiba.nezha.compute.api.constant.GlobalConstant;
import cn.com.duiba.nezha.compute.api.dto.NezhaStatDto;
import cn.com.duiba.nezha.compute.api.dto.NezhaStatMergeDto;
import cn.com.duiba.nezha.compute.api.enums.StatIntervalTypeEnum;
import cn.com.duiba.nezha.compute.biz.bo.NezhaStatBo;
import cn.com.duiba.nezha.compute.biz.constant.htable.NezhaStatConstant;
import cn.com.duiba.nezha.compute.biz.log.NezhaLog;
import cn.com.duiba.nezha.compute.biz.vo.MapVo;
import cn.com.duiba.nezha.compute.common.enums.DateStyle;
import cn.com.duiba.nezha.compute.common.params.Params;
import cn.com.duiba.nezha.compute.common.util.AssertUtil;
import cn.com.duiba.nezha.compute.common.util.DataUtil;
import cn.com.duiba.nezha.compute.common.util.DateUtil;

import java.util.*;

public class NezhaEngineStatUpdateServer {


    /**
     * @param nezhaStatDtoList
     */
    public static void syncES(List<NezhaStatDto> nezhaStatDtoList, String biz) {
        try {
            NezhaStatBo.syncStat(nezhaStatDtoList, GlobalConstant.NEZHA_STAT_COLLECTION_NAME, biz);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * @param nezhaStatDto
     */
    public static void syncES(NezhaStatDto nezhaStatDto, String biz) {
        try {
            syncES(Arrays.asList(nezhaStatDto), biz);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 推啊广告发券处理
     *
     * @param nezhaLog
     * @throws Exception
     */
    public static NezhaStatDto logProcess(NezhaLog nezhaLog, Params.AdvertLogParams params) {


        NezhaStatDto nezhaStatDto = new NezhaStatDto();
        int statType = params.statType();
        try {


            boolean status = false;
            nezhaStatDto.setAdvertId(nezhaLog.getAdvertId());
            nezhaStatDto.setAlgType(nezhaLog.getAlgType());
            nezhaStatDto.setAppId(nezhaLog.getAppId());

            String docKey = NezhaStatKey.getNezhaStatMongoDbKey(nezhaLog.getAlgType(), nezhaLog.getAdvertId(), nezhaLog.getAppId());

            nezhaStatDto.setId(docKey);

            // 1 策略 && 单个广告 && 媒体
            NezhaStatMergeDto adAndAppNezhaStatMergeDto = logSubProcess(nezhaLog.getAlgType(),
                    nezhaLog.getAdvertId(),
                    nezhaLog.getAppId(),
                    nezhaLog.getChargeType(),
                    nezhaLog.getCurrentTime(),
                    nezhaLog.getPreCtr(),
                    nezhaLog.getPreCvr(),
                    nezhaLog.getStatCtr(),
                    nezhaLog.getStatCvr());


            // 2 策略 && 单个广告 && 全局
            NezhaStatMergeDto adAndGlobalNezhaStatMergeDto = logSubProcess(nezhaLog.getAlgType(),
                    nezhaLog.getAdvertId(),
                    0L,
                    nezhaLog.getChargeType(),
                    nezhaLog.getCurrentTime(),
                    nezhaLog.getPreCtr(),
                    nezhaLog.getPreCvr(),
                    nezhaLog.getStatCtr(),
                    nezhaLog.getStatCvr());

            // 3 策略 && 所有广告 && 媒体
//            NezhaStatMergeDto globalAndAppNezhaStatMergeDto = logSubProcess(nezhaLog.getAlgType(),
//                    0L,
//                    nezhaLog.getAppId(),
//                    nezhaLog.getChargeType(),
//                    nezhaLog.getCurrentTime(),
//                    nezhaLog.getPreCtr(),
//                    nezhaLog.getPreCvr(),
//                    nezhaLog.getStatCtr(),
//                    nezhaLog.getStatCvr());


//            if (preCtrValid(globalAndAppNezhaStatMergeDto)) {
//                nezhaStatDto.setPreCtrAvg(globalAndAppNezhaStatMergeDto.getMergePreCtr());
//                nezhaStatDto.setStatCtrAvg(globalAndAppNezhaStatMergeDto.getMergeStatCtr());
//                status=true;
//            }
            if (preCtrValid(adAndGlobalNezhaStatMergeDto)) {
                nezhaStatDto.setPreCtrAvg(adAndGlobalNezhaStatMergeDto.getMergePreCtr());
                nezhaStatDto.setStatCtrAvg(adAndGlobalNezhaStatMergeDto.getMergeStatCtr());
                status=true;
            }
            if (preCtrValid(adAndAppNezhaStatMergeDto)) {
                nezhaStatDto.setPreCtrAvg(adAndAppNezhaStatMergeDto.getMergePreCtr());
                nezhaStatDto.setStatCtrAvg(adAndAppNezhaStatMergeDto.getMergeStatCtr());
                status=true;
            }


//            if (preCvrValid(globalAndAppNezhaStatMergeDto)) {
//                nezhaStatDto.setPreCvrAvg(globalAndAppNezhaStatMergeDto.getMergePreCvr());
//                nezhaStatDto.setStatCvrAvg(globalAndAppNezhaStatMergeDto.getMergeStatCvr());
//                status=true;
//            }
            if (preCvrValid(adAndGlobalNezhaStatMergeDto)) {
                nezhaStatDto.setPreCvrAvg(adAndGlobalNezhaStatMergeDto.getMergePreCvr());
                nezhaStatDto.setStatCvrAvg(adAndGlobalNezhaStatMergeDto.getMergeStatCvr());
                status=true;
            }
            if (preCvrValid(adAndAppNezhaStatMergeDto)) {
                nezhaStatDto.setPreCvrAvg(adAndAppNezhaStatMergeDto.getMergePreCvr());
                nezhaStatDto.setStatCvrAvg(adAndAppNezhaStatMergeDto.getMergeStatCvr());
                status=true;
            }


            if(status==false){
                nezhaStatDto=null;
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

        return nezhaStatDto;
    }

    /**
     * 推啊广告发券处理 按时间维度处理
     *
     * @throws Exception
     */
    public static NezhaStatMergeDto logSubProcess(Long algType,
                                                  Long advertId,
                                                  Long appId,
                                                  Long chargeType,
                                                  String currentTime,
                                                  Double preCtr,
                                                  Double preCvr,
                                                  Double statCtr,
                                                  Double statCvr
    ) {
        NezhaStatMergeDto ret = new NezhaStatMergeDto();

        try {

            // 1 更新 计数 全局、日粒度、小时粒度
            List<String> statIntervalList = new ArrayList<>();
            statIntervalList.add(DateUtil.string2String(currentTime, DateStyle.YYYY_MM_DD_HH_MM_SS, DateStyle.YYYYMMDDHH));
            statIntervalList.add(DateUtil.string2String(currentTime, DateStyle.YYYY_MM_DD_HH_MM_SS, DateStyle.YYYYMMDD));

            NezhaStatBo.accumulation(algType, advertId, appId, chargeType, statIntervalList, preCtr, preCvr, statCtr, statCvr);
            // 2 读取 特征数据 全局和日粒度(读取多日)
            ret = prepareSyncStatDate(algType, advertId, appId, currentTime);

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
    public static NezhaStatMergeDto prepareSyncStatDate(Long algType,
                                                        Long advertId,
                                                        Long appId,
                                                        String currentTime) {

        NezhaStatMergeDto nezhaStatMergeDto = new NezhaStatMergeDto();


        try {

            // mongodb
//            String docKey = NezhaStatKey.getNezhaStatMongoDbKey(algType, advertId, appId);

            nezhaStatMergeDto.setAlgType(algType);
            nezhaStatMergeDto.setAdvertId(advertId);
            nezhaStatMergeDto.setAppId(appId);

//            nezhaStatMergeDto.setKey(docKey);


            // 读取 特征数据 全局和发券日期的数据


            // 读取当日起的近期数据
            Map<Long, String> dateMap = DateUtil.getDaySubTimeStringMap(currentTime, DateStyle.YYYY_MM_DD_HH_MM_SS, DateStyle.YYYYMMDD, StatIntervalTypeEnum.RECENT_7_DAY.getIndex());
            Map<Long, String> dateHourMap = DateUtil.getHourSubTimeStringMap(currentTime, DateStyle.YYYY_MM_DD_HH_MM_SS, DateStyle.YYYYMMDDHH, StatIntervalTypeEnum.RECENT_6_HOUR.getIndex());

//            System.out.println("dateMap=" + JSON.toString(dateMap));
//            System.out.println("dateHourMap=" + JSON.toString(dateHourMap));


            Map<Long, NezhaStatDto> nezhaStatDtoDateMap = NezhaStatBo.getNezhaStatDto(
                    algType,
                    advertId,
                    appId,
                    dateMap);

            Map<Long, NezhaStatDto> nezhaStatDtoDateHourMap = NezhaStatBo.getNezhaStatDto(
                    algType,
                    advertId,
                    appId,
                    dateHourMap);
//            System.out.println("advertStatDtoHourMap="+ JSON.toJSONString(advertStatDtoHourMap));

            Map<StatIntervalTypeEnum, Double> preCtrValueMap = new HashMap<>();
            Map<StatIntervalTypeEnum, Double> preCvrValueMap = new HashMap<>();
            Map<StatIntervalTypeEnum, Double> statCtrValueMap = new HashMap<>();
            Map<StatIntervalTypeEnum, Double> statCvrValueMap = new HashMap<>();

            // 近2日
            NezhaStatDto r2dNezhaStatDto = getIntervalWeightCtr(algType,
                    advertId,
                    appId,
                    nezhaStatDtoDateMap,
                    StatIntervalTypeEnum.CURRENT_DAY,
                    StatIntervalTypeEnum.RECENT_2_DAY,
                    NezhaStatConstant.confidenceThreshold
            );

            if (r2dNezhaStatDto != null) {
                nezhaStatMergeDto.setR2dNezhaStatDto(r2dNezhaStatDto);
            }
            Double r2dPreCtr = getPreCtr(r2dNezhaStatDto, null);
            putMap(preCtrValueMap, StatIntervalTypeEnum.RECENT_2_DAY, r2dPreCtr);

            Double r2dPreCvr = getPreCvr(r2dNezhaStatDto, null);
            putMap(preCvrValueMap, StatIntervalTypeEnum.RECENT_2_DAY, r2dPreCvr);

            Double r2dStatCtr = getStatCtr(r2dNezhaStatDto, null);
            putMap(statCtrValueMap, StatIntervalTypeEnum.RECENT_2_DAY, r2dStatCtr);
            Double r2dStatCvr = getStatCvr(r2dNezhaStatDto, null);
            putMap(statCvrValueMap, StatIntervalTypeEnum.RECENT_2_DAY, r2dStatCvr);


            // 近1日
            NezhaStatDto r1dNezhaStatDto = getIntervalWeightCtr(algType,
                    advertId,
                    appId,
                    nezhaStatDtoDateMap,
                    StatIntervalTypeEnum.CURRENT_DAY,
                    StatIntervalTypeEnum.RECENT_1_DAY,
                    NezhaStatConstant.confidenceThreshold
            );
            if (r1dNezhaStatDto != null) {
                nezhaStatMergeDto.setR1dNezhaStatDto(r1dNezhaStatDto);
            }
            Double r1dPreCtr = getPreCtr(r1dNezhaStatDto, r2dPreCtr);
            putMap(preCtrValueMap, StatIntervalTypeEnum.RECENT_1_DAY, r1dPreCtr);

            Double r1dPreCvr = getPreCvr(r1dNezhaStatDto, r2dPreCvr);
            putMap(preCvrValueMap, StatIntervalTypeEnum.RECENT_1_DAY, r1dPreCvr);

            Double r1dStatCtr = getStatCtr(r1dNezhaStatDto, r2dStatCtr);
            putMap(statCtrValueMap, StatIntervalTypeEnum.RECENT_1_DAY, r1dStatCtr);

            Double r1dStatCvr = getStatCvr(r1dNezhaStatDto, r2dStatCvr);
            putMap(statCvrValueMap, StatIntervalTypeEnum.RECENT_1_DAY, r1dStatCvr);


            // 当日
            NezhaStatDto cdNezhaStatDto = getIntervalWeightCtr(algType,
                    advertId,
                    appId,
                    nezhaStatDtoDateMap,
                    StatIntervalTypeEnum.CURRENT_DAY,
                    StatIntervalTypeEnum.CURRENT_DAY,
                    NezhaStatConstant.confidenceThreshold
            );

            if (cdNezhaStatDto != null) {
                nezhaStatMergeDto.setCdNezhaStatDto(cdNezhaStatDto);
            }
            Double cdPreCtr = getPreCtr(cdNezhaStatDto, r1dPreCtr);
            putMap(preCtrValueMap, StatIntervalTypeEnum.CURRENT_DAY, cdPreCtr);

            Double cdPreCvr = getPreCvr(cdNezhaStatDto, r1dPreCvr);
            putMap(preCvrValueMap, StatIntervalTypeEnum.CURRENT_DAY, cdPreCvr);

            Double cdStatCtr = getStatCtr(cdNezhaStatDto, r1dStatCtr);
            putMap(statCtrValueMap, StatIntervalTypeEnum.CURRENT_DAY, cdStatCtr);

            Double cdStatCvr = getStatCvr(cdNezhaStatDto, r1dStatCvr);
            putMap(statCvrValueMap, StatIntervalTypeEnum.CURRENT_DAY, cdStatCvr);


            // 近1小时
            NezhaStatDto r1hNezhaStatDto = getIntervalWeightCtr(algType,
                    advertId,
                    appId,
                    nezhaStatDtoDateHourMap,
                    StatIntervalTypeEnum.CURRENT_HOUR,
                    StatIntervalTypeEnum.RECENT_1_HOUR,
                    NezhaStatConstant.confidenceThreshold
            );

            if (r1hNezhaStatDto != null) {
                nezhaStatMergeDto.setR1hNezhaStatDto(r1hNezhaStatDto);
            }
            Double r1hPreCtr = getPreCtr(r1hNezhaStatDto, cdPreCtr);
            putMap(preCtrValueMap, StatIntervalTypeEnum.RECENT_1_HOUR, r1hPreCtr);

            Double r1hPreCvr = getPreCvr(r1hNezhaStatDto, cdPreCvr);
            putMap(preCvrValueMap, StatIntervalTypeEnum.RECENT_1_HOUR, r1hPreCvr);

            Double r1hStatCtr = getStatCtr(r1hNezhaStatDto, cdStatCtr);
            putMap(statCtrValueMap, StatIntervalTypeEnum.RECENT_1_HOUR, r1hStatCtr);

            Double r1hStatCvr = getStatCvr(r1hNezhaStatDto, cdStatCvr);
            putMap(statCvrValueMap, StatIntervalTypeEnum.RECENT_1_HOUR, r1hStatCvr);


            // 当前小时
            NezhaStatDto chNezhaStatDto = getIntervalWeightCtr(algType,
                    advertId,
                    appId,
                    nezhaStatDtoDateHourMap,
                    StatIntervalTypeEnum.CURRENT_HOUR,
                    StatIntervalTypeEnum.CURRENT_HOUR,
                    NezhaStatConstant.confidenceThreshold
            );

            if (chNezhaStatDto != null) {
                nezhaStatMergeDto.setChNezhaStatDto(chNezhaStatDto);
            }
            Double chPreCtr = getPreCtr(chNezhaStatDto, r1hPreCtr);
            putMap(preCtrValueMap, StatIntervalTypeEnum.CURRENT_HOUR, chPreCtr);

            Double chPreCvr = getPreCvr(chNezhaStatDto, r1hPreCvr);
            putMap(preCvrValueMap, StatIntervalTypeEnum.CURRENT_HOUR, chPreCvr);

            Double chStatCtr = getStatCtr(chNezhaStatDto, r1hStatCtr);
            putMap(statCtrValueMap, StatIntervalTypeEnum.CURRENT_HOUR, chStatCtr);

            Double chStatCvr = getStatCvr(chNezhaStatDto, r1hStatCvr);
            putMap(statCvrValueMap, StatIntervalTypeEnum.CURRENT_HOUR, chStatCvr);


            // 融合

            Double mergePreCtr = mergeDto(preCtrValueMap, NezhaStatConstant.statIntervalWeightMap);
            Double mergePreCvr = mergeDto(preCvrValueMap, NezhaStatConstant.statIntervalWeightMap);

            Double mergeStatCtr = mergeDto(statCtrValueMap, NezhaStatConstant.statIntervalWeightMap);
            Double mergeStatCvr = mergeDto(statCvrValueMap, NezhaStatConstant.statIntervalWeightMap);


            nezhaStatMergeDto.setMergePreCtr(mergePreCtr);
            nezhaStatMergeDto.setMergePreCvr(mergePreCvr);

            nezhaStatMergeDto.setMergeStatCtr(mergeStatCtr);
            nezhaStatMergeDto.setMergeStatCvr(mergeStatCvr);


//            if(mergePreCtr>0.0 && mergePreCvr>0.0){
//                System.out.println("key=" + NezhaStatKey.getNezhaStatMongoDbKey(algType, advertId, appId));
//                System.out.println("nezhaStatHourDto" + com.alibaba.fastjson.JSONObject.toJSONString(nezhaStatDtoDateHourMap.get(0L)));
//                System.out.println("ctrValueMap=" + JSON.toString(preCtrValueMap));
//                System.out.println("cvrValueMap=" + JSON.toString(preCvrValueMap));
//
//                System.out.println("statctrValueMap=" + JSON.toString(statCtrValueMap));
//                System.out.println("statcvrValueMap=" + JSON.toString(statCvrValueMap));
//                System.out.println("mergePreCtr=" + mergePreCtr);
//                System.out.println("mergePreCvr=" + mergePreCvr);
//                System.out.println("mergeStatCtr=" + mergeStatCtr);
//                System.out.println("mergeStatCvr=" + mergeStatCvr);
//            }

//            System.out.println("nezhaStatMergeDto = "+ JSONObject.toJSONString(nezhaStatMergeDto));


        } catch (Exception e) {
            e.printStackTrace();
        }
        return nezhaStatMergeDto;
    }


    public static NezhaStatDto getIntervalWeightCtr(Long algType,
                                                    Long advertId,
                                                    Long appId,
                                                    Map<Long, NezhaStatDto> nezhaStatDtoMap,
                                                    StatIntervalTypeEnum startCtrStatTypeEnum,
                                                    StatIntervalTypeEnum endCtrStatTypeEnum,
                                                    long confidenceThreshold
    ) {
        NezhaStatDto ret = null;
        try {
            NezhaStatDto nezhaStatDto = accDto(nezhaStatDtoMap, startCtrStatTypeEnum.getIndex(), endCtrStatTypeEnum.getIndex());

            if (nezhaStatDto != null && confidence(nezhaStatDto.getCtrLaunchCnt(), confidenceThreshold)) {
                nezhaStatDto.setAlgType(algType);
                nezhaStatDto.setAdvertId(advertId);
                nezhaStatDto.setAppId(appId);
                ret = nezhaStatDto;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }


    public static void putValueMap(Long algType,
                                   Long advertId,
                                   Long appId,
                                   Map<Long, NezhaStatDto> nezhaStatDtoDateMap,
                                   StatIntervalTypeEnum statIntervalTypeEnum,
                                   StatIntervalTypeEnum endIntervalTypeEnum,
                                   long confidenceThreshold,
                                   NezhaStatMergeDto nezhaStatMergeDto,
                                   Map<StatIntervalTypeEnum, Double> preCtrValueMap,
                                   Map<StatIntervalTypeEnum, Double> preCvrValueMap,
                                   Map<StatIntervalTypeEnum, Double> statCtrValueMap,
                                   Map<StatIntervalTypeEnum, Double> statCvrValueMap) {

        // 近2日
        NezhaStatDto r2dNezhaStatDto = getIntervalWeightCtr(algType,
                advertId,
                appId,
                nezhaStatDtoDateMap,
                StatIntervalTypeEnum.CURRENT_DAY,
                StatIntervalTypeEnum.RECENT_2_DAY,
                NezhaStatConstant.confidenceThreshold
        );

        if (r2dNezhaStatDto != null) {
            nezhaStatMergeDto.setR2dNezhaStatDto(r2dNezhaStatDto);
        }
        Double r2dPreCtr = getPreCtr(r2dNezhaStatDto, null);
        putMap(preCtrValueMap, StatIntervalTypeEnum.RECENT_2_DAY, r2dPreCtr);

        Double r2dPreCvr = getPreCvr(r2dNezhaStatDto, null);
        putMap(preCvrValueMap, StatIntervalTypeEnum.RECENT_2_DAY, r2dPreCvr);

        Double r2dStatCtr = getStatCtr(r2dNezhaStatDto, null);
        putMap(statCtrValueMap, StatIntervalTypeEnum.RECENT_2_DAY, r2dStatCtr);
        Double r2dStatCvr = getStatCvr(r2dNezhaStatDto, null);
        putMap(statCvrValueMap, StatIntervalTypeEnum.RECENT_2_DAY, r2dStatCvr);
    }


    /**
     * @param nezhaStatDtoDateMap
     * @return
     */
    public static NezhaStatDto accDto(Map<Long, NezhaStatDto> nezhaStatDtoDateMap,
                                      long startInterval,
                                      long endInterval
    ) {
        NezhaStatDto ret = null;
        try {

            if (AssertUtil.isNotEmpty(nezhaStatDtoDateMap)) {
                ret = new NezhaStatDto();

                for (Map.Entry<Long, NezhaStatDto> entry : nezhaStatDtoDateMap.entrySet()) {
                    Long interval = entry.getKey();
                    NezhaStatDto dto = entry.getValue();
                    if (interval >= startInterval && interval <= endInterval) {

                        ret.setCtrLaunchCnt(DataUtil.addLong(ret.getCtrLaunchCnt(), dto.getCtrLaunchCnt()));
                        ret.setCvrLaunchCnt(DataUtil.addLong(ret.getCvrLaunchCnt(), dto.getCvrLaunchCnt()));

                        ret.setPreCtrAcc(DataUtil.addDouble(ret.getPreCtrAcc(), dto.getPreCtrAcc(), 6));
                        ret.setPreCvrAcc(DataUtil.addDouble(ret.getPreCvrAcc(), dto.getPreCvrAcc(), 6));

                        ret.setStatCtrAcc(DataUtil.addDouble(ret.getStatCtrAcc(), dto.getStatCtrAcc(), 6));
                        ret.setStatCvrAcc(DataUtil.addDouble(ret.getStatCvrAcc(), dto.getStatCvrAcc(), 6));
                    }
                }

                ret.setPreCtrAvg(DataUtil.division(ret.getPreCtrAcc(), ret.getCtrLaunchCnt(), 6));
                ret.setPreCvrAvg(DataUtil.division(ret.getPreCvrAcc(), ret.getCvrLaunchCnt(), 6));

                ret.setStatCtrAvg(DataUtil.division(ret.getStatCtrAcc(), ret.getCtrLaunchCnt(), 6));
                ret.setStatCvrAvg(DataUtil.division(ret.getStatCvrAcc(), ret.getCvrLaunchCnt(), 6));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }


    /**
     * @param valueMap
     * @return
     */
    public static Double mergeDto(Map<StatIntervalTypeEnum, Double> valueMap, Map<StatIntervalTypeEnum, Double> weightMap) {
        Double ret = null;
        try {
            if (AssertUtil.isNotEmpty(valueMap) && AssertUtil.isNotEmpty(weightMap)) {


                Double accWeight = 0.0;
                Double accValue = 0.0;
                for (StatIntervalTypeEnum key : valueMap.keySet()) {

                    Double weight = weightMap.get(key);
                    Double value = valueMap.get(key);

                    if (weight != null && value != null) {
                        accWeight += weight;
                        accValue += value * weight;
                    }

                }
                if (accWeight > 0.0) {
                    ret = DataUtil.division(accValue, accWeight, 6);
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }


    public static Double getPreCtr(NezhaStatDto dto, Double defaultValue) {
        Double ret = defaultValue;
        if (dto != null) {
            ret = dto.getPreCtrAvg();
        }
        return ret;
    }

    public static Double getPreCvr(NezhaStatDto dto, Double defaultValue) {
        Double ret = defaultValue;
        if (dto != null) {
            ret = dto.getPreCvrAvg();
        }
        return ret;
    }

    public static Double getStatCtr(NezhaStatDto dto, Double defaultValue) {
        Double ret = defaultValue;
        if (dto != null) {
            ret = dto.getStatCtrAvg();
        }
        return ret;
    }

    public static Double getStatCvr(NezhaStatDto dto, Double defaultValue) {
        Double ret = defaultValue;
        if (dto != null) {
            ret = dto.getStatCvrAvg();
        }
        return ret;
    }


    public static void putMap(Map<StatIntervalTypeEnum, Double> map, StatIntervalTypeEnum key, Double value) {
        if (map != null && key != null && value != null) {
            map.put(key, value);
        }
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


    public static String getDateHourTime(String gmtTime) {

        String ret = null;
        try {

            if (gmtTime != null) {
                ret = DateUtil.getDateTime(DateUtil.getDateTime(gmtTime), DateStyle.YYYYMMDDHH);
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


    public static boolean preCtrValid(NezhaStatMergeDto nezhaStatMergeDto) {
        boolean ret = false;
        try {
            if (nezhaStatMergeDto != null &&
                    nezhaStatMergeDto.getMergePreCtr() != null &&
                    nezhaStatMergeDto.getMergePreCtr() > 0.0 &&
                    nezhaStatMergeDto.getMergeStatCtr() != null &&
                    nezhaStatMergeDto.getMergeStatCtr() > 0.0
                    ) {
                ret = true;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static boolean preCvrValid(NezhaStatMergeDto nezhaStatMergeDto) {
        boolean ret = false;
        try {
            if (nezhaStatMergeDto != null &&
                    nezhaStatMergeDto.getMergePreCvr() != null &&
                    nezhaStatMergeDto.getMergePreCvr() > 0.0 &&
                    nezhaStatMergeDto.getMergeStatCvr() != null &&
                    nezhaStatMergeDto.getMergeStatCvr() > 0.0
                    ) {
                ret = true;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

}

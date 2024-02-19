package cn.com.duiba.nezha.compute.biz.bo;

import cn.com.duiba.nezha.compute.api.cachekey.NezhaStatKey;
import cn.com.duiba.nezha.compute.api.dto.NezhaStatDto;
import cn.com.duiba.nezha.compute.biz.constant.htable.NezhaStatConstant;
import cn.com.duiba.nezha.compute.biz.utils.hbase.HbaseUtil;
import cn.com.duiba.nezha.compute.biz.utils.mongodb.MongoUtil;
import cn.com.duiba.nezha.compute.common.enums.DateStyle;
import cn.com.duiba.nezha.compute.common.util.AssertUtil;
import cn.com.duiba.nezha.compute.common.util.DateUtil;
import cn.com.duiba.nezha.compute.common.util.MyStringUtil2;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class NezhaStatBo {



    public static void syncStat(List<NezhaStatDto> nezhaStatDtoList, String collectionName, String biz) throws Exception {


        Map<String, NezhaStatDto> mongoDocMap = new HashMap<>();
        String startTime = DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS);
        // 同步
        if (nezhaStatDtoList != null) {

            for (NezhaStatDto nezhaStatDto : nezhaStatDtoList) {

                // mongodb
                String docKey = nezhaStatDto.getId();
                mongoDocMap.put(docKey, nezhaStatDto);
            }

        }


//        System.out.println("mongoDocMap" + JSONObject.toJSONString(mongoDocMap));

//        // 同步mongodb
        MongoUtil.bulkWriteUpdateT(collectionName, mongoDocMap, biz);
        String endTime = DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS);
        System.out.println("syncAdvertAppStat mongoDocMap.size= " + mongoDocMap.size() + ",startTime=" + startTime + ",endTime=" + endTime);
    }


    public static <T> Map<String, String> getMap(T t) {
        Map<String, String> ret = null;
        String jString = JSON.toJSONString(t);

        ret = (Map) JSON.parse(jString);

        return ret;

    }


    /**
     * 哪吒预估值统计计数
     *
     * @throws Exception
     */
    public static void accumulation(Long algType,
                                    Long advertId,
                                    Long appId,
                                    Long chargeType,
                                    List<String> statIntervalList,
                                    Double preCtr,
                                    Double preCvr,
                                    Double statCtr,
                                    Double statCvr) throws Exception {


        if (AssertUtil.isEmpty(statIntervalList)) {
            return;
        }
        HbaseUtil hbaseUtil = HbaseUtil.getInstance();

        String rowKey = NezhaStatKey.getNezhaStatHbaseKey(algType,
                advertId,
                appId);


        Map<String, Map<String, Long>> fcvMap = new HashMap<>();

        Map<String, Long> ctrLaunchColValMap = new HashMap<>();
        Map<String, Long> preCtrColValMap = new HashMap<>();
        Map<String, Long> statCtrColValMap = new HashMap<>();
        for (String statInterval : statIntervalList) {

            // 1 累加 CTR 发券
            ctrLaunchColValMap.put(statInterval, 1L);

            // 2 累加 CTR 预估值
            preCtrColValMap.put(statInterval, double2Long(preCtr, NezhaStatConstant.DOUBLE_ACC_FACTOR));

            // 3 累加 CTR 统计值
            statCtrColValMap.put(statInterval, double2Long(statCtr, NezhaStatConstant.DOUBLE_ACC_FACTOR));

        }
        fcvMap.put(NezhaStatConstant.FM_CTR_LAUNCH, ctrLaunchColValMap);
        fcvMap.put(NezhaStatConstant.FM_PRE_CTR_ACC, preCtrColValMap);
        fcvMap.put(NezhaStatConstant.FM_STAT_CTR_ACC, statCtrColValMap);


        if (chargeType.equals(2L)) {

            Map<String, Long> cvrLaunchColValMap = new HashMap<>();
            Map<String, Long> preCvrColValMap = new HashMap<>();
            Map<String, Long> statCvrColValMap = new HashMap<>();

            for (String statInterval : statIntervalList) {
                // 4 累加 CTR 发券
                cvrLaunchColValMap.put(statInterval, 1L);

                // 5 累加 CVR 预估值
                preCvrColValMap.put(statInterval, double2Long(preCvr, NezhaStatConstant.DOUBLE_ACC_FACTOR));

                // 6 累加 CVR 统计值
                statCvrColValMap.put(statInterval, double2Long(statCvr, NezhaStatConstant.DOUBLE_ACC_FACTOR));
            }
            fcvMap.put(NezhaStatConstant.FM_CVR_LAUNCH, cvrLaunchColValMap);
            fcvMap.put(NezhaStatConstant.FM_PRE_CVR_ACC, preCvrColValMap);
            fcvMap.put(NezhaStatConstant.FM_STAT_CVR_ACC, statCvrColValMap);


        }

        hbaseUtil.incrementColumeValues(NezhaStatConstant.TABLE_NAME, rowKey, fcvMap);


//        System.out.println("gmtHour="+gmtHour+",gmtDate="+gmtDate);

    }


    /**
     * @param algType
     * @param advertId
     * @param appId
     * @param statIntervalMap
     * @return
     * @throws Exception
     */
    public static Map<Long, NezhaStatDto> getNezhaStatDto(final Long algType,
                                                          final Long advertId,
                                                          final Long appId,
                                                          final Map<Long, String> statIntervalMap) throws Exception {

        final Map<Long, NezhaStatDto> nezhaStatDtoMap = new HashMap<>();

        //
        if (AssertUtil.isEmpty(statIntervalMap)) {
            return nezhaStatDtoMap;
        }


        HbaseUtil hbaseUtil = HbaseUtil.getInstance();
        String rowKey = NezhaStatKey.getNezhaStatHbaseKey(algType,
                advertId,
                appId);

        // 封装请求Map
        Map<String, Set<String>> fcMap = new HashMap<>();


        Set<String> colSet = new HashSet(statIntervalMap.values());

        fcMap.put(NezhaStatConstant.FM_CTR_LAUNCH, colSet);
        fcMap.put(NezhaStatConstant.FM_CVR_LAUNCH, colSet);
        fcMap.put(NezhaStatConstant.FM_PRE_CTR_ACC, colSet);
        fcMap.put(NezhaStatConstant.FM_PRE_CVR_ACC, colSet);
        fcMap.put(NezhaStatConstant.FM_STAT_CTR_ACC, colSet);
        fcMap.put(NezhaStatConstant.FM_STAT_CVR_ACC, colSet);

        // 数据读取
        hbaseUtil.getOneRow(NezhaStatConstant.TABLE_NAME, rowKey, fcMap, new HbaseUtil.QueryCallback() {

            @Override
            public void process(List<Result> retList) throws Exception {

                if (retList != null) {

                    Result ret = retList.get(0);

                    for (Map.Entry<Long, String> statInterval : statIntervalMap.entrySet()) {

                        NezhaStatDto nezhaStatDto = new NezhaStatDto();

                        nezhaStatDto.setAlgType(algType);
                        nezhaStatDto.setAdvertId(advertId);
                        nezhaStatDto.setAppId(appId);
                        //


                        byte[] ctrLaunchCntB = ret.getValue(
                                Bytes.toBytes(NezhaStatConstant.FM_CTR_LAUNCH),
                                Bytes.toBytes(statInterval.getValue()));

                        byte[] cvrLaunchCntB = ret.getValue(
                                Bytes.toBytes(NezhaStatConstant.FM_CVR_LAUNCH),
                                Bytes.toBytes(statInterval.getValue()));

                        byte[] preCtrAccB = ret.getValue(
                                Bytes.toBytes(NezhaStatConstant.FM_PRE_CTR_ACC),
                                Bytes.toBytes(statInterval.getValue()));


                        byte[] preCvrAccB = ret.getValue(
                                Bytes.toBytes(NezhaStatConstant.FM_PRE_CVR_ACC),
                                Bytes.toBytes(statInterval.getValue()));

                        byte[] statCtrAccB = ret.getValue(
                                Bytes.toBytes(NezhaStatConstant.FM_STAT_CTR_ACC),
                                Bytes.toBytes(statInterval.getValue()));


                        byte[] statCvrAccB = ret.getValue(
                                Bytes.toBytes(NezhaStatConstant.FM_STAT_CVR_ACC),
                                Bytes.toBytes(statInterval.getValue()));


                        Long ctrLaunchCnt = MyStringUtil2.bytesToLong(ctrLaunchCntB);
                        Long cvrLaunchCnt = MyStringUtil2.bytesToLong(cvrLaunchCntB);

                        Long preCtrAcc = MyStringUtil2.bytesToLong(preCtrAccB);
                        Long preCvrAcc = MyStringUtil2.bytesToLong(preCvrAccB);

                        Long statCtrAcc = MyStringUtil2.bytesToLong(statCtrAccB);
                        Long statCvrAcc = MyStringUtil2.bytesToLong(statCvrAccB);


                        nezhaStatDto.setCtrLaunchCnt(ctrLaunchCnt);
                        nezhaStatDto.setCvrLaunchCnt(cvrLaunchCnt);

                        nezhaStatDto.setPreCtrAcc(long2Double(preCtrAcc, NezhaStatConstant.DOUBLE_ACC_FACTOR, 5));
                        nezhaStatDto.setPreCvrAcc(long2Double(preCvrAcc, NezhaStatConstant.DOUBLE_ACC_FACTOR, 5));

                        nezhaStatDto.setPreCtrAvg(getAvg(preCtrAcc, ctrLaunchCnt, NezhaStatConstant.DOUBLE_ACC_FACTOR, 5));
                        nezhaStatDto.setPreCvrAvg(getAvg(preCvrAcc, cvrLaunchCnt, NezhaStatConstant.DOUBLE_ACC_FACTOR, 5));


                        nezhaStatDto.setStatCtrAcc(long2Double(statCtrAcc, NezhaStatConstant.DOUBLE_ACC_FACTOR, 5));
                        nezhaStatDto.setStatCvrAcc(long2Double(statCvrAcc, NezhaStatConstant.DOUBLE_ACC_FACTOR, 5));

                        nezhaStatDto.setStatCtrAvg(getAvg(statCtrAcc, ctrLaunchCnt, NezhaStatConstant.DOUBLE_ACC_FACTOR, 5));
                        nezhaStatDto.setStatCvrAvg(getAvg(statCvrAcc, cvrLaunchCnt, NezhaStatConstant.DOUBLE_ACC_FACTOR, 5));



                        nezhaStatDtoMap.put(statInterval.getKey(), nezhaStatDto);

                    }

                }

            }

        });

        return nezhaStatDtoMap;
    }


    /**
     * @param valueCnt
     * @param launchCnt
     * @param newScala
     * @return
     */
    public static Double getAvg(Long valueCnt, Long launchCnt, Long amplification, int newScala) {
        Double ret = null;
        if (launchCnt != null && launchCnt >= 1) {
            ret = long2Double(valueCnt) / long2Double(launchCnt) / amplification;
            if (ret != null) {
                ret = formatDouble(ret.doubleValue(), newScala);
                if (ret > 1.0) {
                    ret = 1.0;
                }
            }
        }
        return ret;
    }

    /**
     * @param chargeSrc
     * @return
     */
    public static Double long2Double(Long chargeSrc) {
        Double ret = 0.0;
        if (chargeSrc != null) {
            ret = chargeSrc + 0.0;
        }
        return ret;
    }


    /**
     * 这个方法挺简单的。
     * DecimalFormat is a concrete subclass of NumberFormat that formats decimal numbers.
     *
     * @param d
     * @return
     */
    public static double formatDouble(double d, int newScala) {
        BigDecimal bg = new BigDecimal(d).setScale(newScala, RoundingMode.UP);

        return bg.doubleValue();
    }


    /**
     * @param value
     * @param amplification
     * @return
     */
    public static Long double2Long(Double value, Long amplification) {
        Long ret = null;
        if (amplification == null) {
            amplification = 1L;
        }

        if (value != null) {
            ret = Math.round(value * amplification);
        }
        return ret;
    }

    /**
     * @param value
     * @param amplification
     * @return
     */
    public static Double long2Double(Long value, Long amplification, int newScala) {
        Double ret = null;
        if (value != null) {
            ret = (value + 0.0) / amplification;

            if (ret != null) {
                ret = formatDouble(ret.doubleValue(), newScala);
            }

        }
        return ret;
    }

}

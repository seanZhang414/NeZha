package cn.com.duiba.nezha.compute.biz.bo;

import cn.com.duiba.nezha.compute.api.cachekey.AdvertStatKey;

import cn.com.duiba.nezha.compute.api.dto.AdvertAppStatDto;
import cn.com.duiba.nezha.compute.api.dto.AdvertCtrStatDto;
import cn.com.duiba.nezha.compute.api.enums.AdvertStatDimTypeEnum;
import cn.com.duiba.nezha.compute.api.enums.StatTypeEnum;

import cn.com.duiba.nezha.compute.biz.constant.htable.AdvertStatConstant;
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

public class AdvertStatBo {


    private static String tableName = AdvertStatConstant.TABLE_NAME;


    /**
     * @param advertAppStatDtoList
     * @throws Exception
     */
    public static void syncAdvertAppStat(List<AdvertAppStatDto> advertAppStatDtoList, String collectionName, String biz) throws Exception {


        Map<String, AdvertAppStatDto> mongoDocMap = new HashMap<>();
        String startTime = DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS);
        // 同步
        if (advertAppStatDtoList != null) {

            for (AdvertAppStatDto advertAppStatDto : advertAppStatDtoList) {

                // mongodb
                String docKey = advertAppStatDto.getKey();
                mongoDocMap.put(docKey, advertAppStatDto);
            }

        }


//        System.out.println("mongoDocMap" + JSONObject.toJSONString(mongoDocMap));

//        // 同步mongodb
        MongoUtil.bulkWriteUpdateT(collectionName, mongoDocMap, biz);
        String endTime = DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS);
        System.out.println("syncAdvertAppStat mongoDocMap.size= " + mongoDocMap.size() + ",startTime=" + startTime + ",endTime=" + endTime);
    }

    /**
     * @param advertCtrStatDtoList
     * @throws Exception
     */
    public static void syncAdvertCtrStat(List<AdvertCtrStatDto> advertCtrStatDtoList, String collectionName, String biz) throws Exception {


        Map<String, AdvertCtrStatDto> mongoDocMap = new HashMap<>();
        String startTime = DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS);
        // 同步
        if (advertCtrStatDtoList != null) {

            for (AdvertCtrStatDto advertCtrStatDto : advertCtrStatDtoList) {

                // mongodb
                String docKey = advertCtrStatDto.getId();
                mongoDocMap.put(docKey, advertCtrStatDto);
            }

        }


//        System.out.println("mongoDocMap" + JSONObject.toJSONString(mongoDocMap));

//        // 同步mongodb
        MongoUtil.bulkWriteUpdateT(collectionName, mongoDocMap, biz);
        String endTime = DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS);
        System.out.println("syncAdvertCtrStat mongoDocMap.size= " + mongoDocMap.size() + ",startTime=" + startTime + ",endTime=" + endTime);
    }




    public static <T> Map<String, String> getMap(T t) {
        Map<String, String> ret = null;
        String jString = JSON.toJSONString(t);

        ret = (Map) JSON.parse(jString);

        return ret;

    }


    /**
     * 更新ctr
     *
     * @param statDimId
     * @param gmtDate
     * @param globalCtr
     * @param dtCtr
     * @throws Exception
     */
    public static void updateCtr(String advertId,
                                 String materialId,
                                 StatTypeEnum statTypeEnum,
                                 Long advertTimes,
                                 AdvertStatDimTypeEnum advertStatDimTypeEnum,
                                 String statDimId,
                                 String gmtDate,
                                 Double globalCtr,
                                 Double dtCtr

    ) throws Exception {

        if (advertId != null) {


            HbaseUtil hbaseUtil = HbaseUtil.getInstance();

            String rowKey = AdvertStatKey.getAdvertStatRowKey(advertId,
                    materialId,
                    advertTimes,
                    advertStatDimTypeEnum.getDesc(),
                    statDimId);

            Map<String, Map<String, String>> rowMap = new HashMap<>();

            Map<String, Map<String, String>> fvMap = new HashMap<>();

            Map<String, String> ctrColValMap = new HashMap<>();

            ctrColValMap.put(gmtDate, MyStringUtil2.double2String(globalCtr));
            ctrColValMap.put(AdvertStatConstant.COL_GLOBAL, MyStringUtil2.double2String(dtCtr));

            fvMap.put(AdvertStatConstant.FM_CTR, ctrColValMap);

            //
            hbaseUtil.insert(tableName, rowKey, fvMap);

        }

    }


    /**
     * 计数
     *
     * @param advertStatDimTypeEnum
     * @param statDimId
     * @param familyName
     * @throws Exception
     */
    public static void incrementCount(String adevertId,
                                      String materialId,
                                      StatTypeEnum statTypeEnum,
                                      Long advertTimes,
                                      AdvertStatDimTypeEnum advertStatDimTypeEnum,
                                      String statDimId,
                                      String familyName,
                                      String currentTime,
                                      String time,
                                      Long fee) throws Exception {


        // 1 更新 计数 全局、日粒度、小时粒度
        String gmtHour = null;
        String gmtDate = null;

        if (time != null && time.length() == 19) {
            gmtHour = time.substring(0, 13);
            gmtDate = time.substring(0, 10);

        } else {
            gmtHour = currentTime.substring(0, 13);
            gmtDate = currentTime.substring(0, 10);
        }

//        System.out.println("gmtHour="+gmtHour+",gmtDate="+gmtDate);

        if (gmtDate != null) {

            HbaseUtil hbaseUtil = HbaseUtil.getInstance();

            String rowKey = AdvertStatKey.getAdvertStatRowKey(adevertId,
                    materialId,
                    advertTimes,
                    advertStatDimTypeEnum.getDesc(),
                    statDimId);

//            System.out.println("incrementCount.rowKey="+rowKey);


            Map<String, Map<String, Long>> fcvMap = new HashMap<>();

            // 计数
            Map<String, Long> cntColValMap = new HashMap<>();
            // 全局
            cntColValMap.put(AdvertStatConstant.COL_GLOBAL, 1L);
            // 当日
            cntColValMap.put(gmtDate, 1L);
            // 当小时
            cntColValMap.put(gmtHour, 1L);

            fcvMap.put(familyName, cntColValMap);



            // 计费
//            System.out.println("familyName.equals(AdvertStatConstant.FM_CHARGE) "+ familyName.equals(AdvertStatConstant.FM_CHARGE));
//            System.out.println("fee!=null "+ fee!=null);
            if(familyName.equals(AdvertStatConstant.FM_CHARGE) && fee!=null){

                Map<String, Long> feeColValMap = new HashMap<>();
                // 全局
                feeColValMap.put(AdvertStatConstant.COL_GLOBAL, fee);
                // 当日
                feeColValMap.put(gmtDate, fee);
                // 当小时
                feeColValMap.put(gmtHour, fee);

                fcvMap.put(AdvertStatConstant.FM_CHARGE_FEE, feeColValMap);
//                System.out.println("add fee "+ JSON.toJSONString(feeColValMap));
            }


            // 封装

            // 批量
            hbaseUtil.incrementColumeValues(tableName, rowKey, fcvMap);




        }

    }






    /**
     * @param statDimId
     * @param dateMap
     * @return
     * @throws Exception
     */
    public static Map<Long, AdvertCtrStatDto> getAdvertCtrStatDto(final String advertId,
                                                                  final String materialId,
                                                                  final Long advertTimes,
                                                                  final AdvertStatDimTypeEnum advertStatDimTypeEnum,
                                                                  final String statDimId,
                                                                  final Map<Long, String> dateMap) throws Exception {

        final Map<Long, AdvertCtrStatDto> advertCtrStatDtoMap = new HashMap<>();

        //
        if (AssertUtil.isAnyEmpty(dateMap)) {
            return advertCtrStatDtoMap;
        }


        HbaseUtil hbaseUtil = HbaseUtil.getInstance();
        String rowKey = AdvertStatKey.getAdvertStatRowKey(advertId,
                materialId,
                advertTimes,
                advertStatDimTypeEnum.getDesc(),
                statDimId);

        // 封装请求Map
        Map<String, Set<String>> fcMap = new HashMap<>();

//        Set<String> colSet = new HashSet();
//        // 0 launch
//        Set<String> fLaunchSet = new HashSet();
//        // 1 exposure
//        Set<String> fExposureSet = new HashSet();
//        // 2 click
//        Set<String> fClickSet = new HashSet();
//        // 3 charge
//        Set<String> fChargeSet = new HashSet();
//
//        // 4 act exp
//        Set<String> fActExpSet = new HashSet();
//
//        // 5 act click
//        Set<String> fActClickSet = new HashSet();
//
//        // 6 charge fee
//        Set<String> fChargeFeeSet = new HashSet();


//        for (Map.Entry<Long, String> entry : dateMap.entrySet()) {
//            String gmtDate = entry.getValue();
//            fLaunchSet.add(gmtDate);
//            fExposureSet.add(gmtDate);
//            fClickSet.add(gmtDate);
//            fChargeSet.add(gmtDate);
//            fActExpSet.add(gmtDate);
//            fActClickSet.add(gmtDate);
//            fChargeFeeSet.add(gmtDate);

//        }
//        fcMap.put(AdvertStatConstant.FM_LAUNCH, fLaunchSet);
//        fcMap.put(AdvertStatConstant.FM_EXPOSURE, fExposureSet);
//        fcMap.put(AdvertStatConstant.FM_CLICK, fClickSet);
//        fcMap.put(AdvertStatConstant.FM_CHARGE, fChargeSet);
//        fcMap.put(AdvertStatConstant.FM_ACT_CLICK, fActClickSet);
//        fcMap.put(AdvertStatConstant.FM_ACT_EXP, fActExpSet);
//        fcMap.put(AdvertStatConstant.FM_CHARGE_FEE, fChargeFeeSet);

        Set<String> colSet = new HashSet(dateMap.values());

        fcMap.put(AdvertStatConstant.FM_LAUNCH, colSet);
        fcMap.put(AdvertStatConstant.FM_EXPOSURE, colSet);
        fcMap.put(AdvertStatConstant.FM_CLICK, colSet);
        fcMap.put(AdvertStatConstant.FM_CHARGE, colSet);
        fcMap.put(AdvertStatConstant.FM_ACT_CLICK, colSet);
        fcMap.put(AdvertStatConstant.FM_ACT_EXP, colSet);
        fcMap.put(AdvertStatConstant.FM_CHARGE_FEE, colSet);


        // 数据读取
        hbaseUtil.getOneRow(tableName, rowKey, fcMap, new HbaseUtil.QueryCallback() {

            @Override
            public void process(List<Result> retList) throws Exception {

                if (retList != null) {

                    Result ret = retList.get(0);

                    for (Map.Entry<Long, String> entry : dateMap.entrySet()) {
                        Long lastDayInterval = entry.getKey();
                        String gmtDate = entry.getValue();
                        AdvertCtrStatDto advertCtrStatDto = new AdvertCtrStatDto();

                        advertCtrStatDto.setAdvertId(advertId);
                        advertCtrStatDto.setMaterialId(materialId);
                        //
                        byte[] launchCntB = ret.getValue(
                                Bytes.toBytes(AdvertStatConstant.FM_LAUNCH),
                                Bytes.toBytes(gmtDate));

                        byte[] chargeCntB = ret.getValue(
                                Bytes.toBytes(AdvertStatConstant.FM_CHARGE),
                                Bytes.toBytes(gmtDate));

                        byte[] actExpCntB = ret.getValue(
                                Bytes.toBytes(AdvertStatConstant.FM_ACT_EXP),
                                Bytes.toBytes(gmtDate));


                        byte[] actClickCntB = ret.getValue(
                                Bytes.toBytes(AdvertStatConstant.FM_ACT_CLICK),

                                Bytes.toBytes(gmtDate));

                        byte[] chargeFeeCntB = ret.getValue(
                                Bytes.toBytes(AdvertStatConstant.FM_CHARGE_FEE),
                                Bytes.toBytes(gmtDate));


                        Long launchCnt = MyStringUtil2.bytesToLong(launchCntB);
                        Long chargeCnt = MyStringUtil2.bytesToLong(chargeCntB);

                        Long actExpCnt = MyStringUtil2.bytesToLong(actExpCntB);
                        Long actClickCnt = MyStringUtil2.bytesToLong(actClickCntB);

                        Long chargeFeeCnt = MyStringUtil2.bytesToLong(chargeFeeCntB);


                        advertCtrStatDto.setLaunchCnt(launchCnt);
                        advertCtrStatDto.setChargeCnt(chargeCnt);

                        advertCtrStatDto.setActExpCnt(actExpCnt);
                        advertCtrStatDto.setActClickCnt(actClickCnt);

                        advertCtrStatDto.setChargeFees(chargeFeeCnt);

                        advertCtrStatDto.setCtr(getCtr(chargeCnt, launchCnt, 5));

                        advertCtrStatDto.setCvr(getCtr(actClickCnt, chargeCnt, 5));
                        advertCtrStatDtoMap.put(lastDayInterval, advertCtrStatDto);
                    }

                }

            }

        });

        return advertCtrStatDtoMap;
    }


    public static Double getCtr(Long chargeCnt, Long launchCnt, int newScala) {
        Double ret = null;
        if (launchCnt != null && launchCnt >= 1) {
            ret = long2Double(chargeCnt) / long2Double(launchCnt);
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


}

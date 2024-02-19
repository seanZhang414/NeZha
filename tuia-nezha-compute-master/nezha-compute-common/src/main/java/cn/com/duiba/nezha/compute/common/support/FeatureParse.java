package cn.com.duiba.nezha.compute.common.support;

import cn.com.duiba.nezha.compute.api.dto.FeatureDto;
import cn.com.duiba.nezha.compute.common.enums.DateStyle;
import cn.com.duiba.nezha.compute.common.util.AssertUtil;
import cn.com.duiba.nezha.compute.common.util.DataUtil;
import cn.com.duiba.nezha.compute.common.util.DateUtil;
import cn.com.duiba.nezha.compute.common.util.MyStringUtil2;
import com.alibaba.fastjson.JSON;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pc on 2017/2/17.
 */
public class FeatureParse {

    public static int[] dayOrderRankBucket = {1, 2, 3, 4, 5, 6, 7, 8, 10, 12, 15, 20, 30, 60, 100};
    public static int[] orderRankBucket = {1, 2, 3, 4, 5, 6, 7, 8, 10, 12, 15, 20, 25, 30, 35, 50, 70, 100, 200, 500};

    public static int[] dayActOrderRankBucket = {1, 2, 3, 4, 5, 6, 7, 8, 9, 20};
    public static int[] orderActRankBucket = {1, 2, 3, 4, 5, 6, 7, 8, 10, 12, 15, 20, 30, 50};

    public static int[] orderGmtIntervelBucket = {1, 2, 3, 4, 5, 10, 60, 60 * 12, 60 * 24, 60 * 24 * 7};

    public static int[] orderActGmtIntervelBucket = {1, 2, 3, 4, 5, 10, 60, 60 * 12, 60 * 24, 60 * 24 * 7};

    public static double[] userCtrBucket = {0.05, 0.1, 0.15, 0.2, 0.3, 0.4, 0.45, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};

    public static double[] userCvrBucket = {0.01, 0.05, 0.1, 0.15, 0.2, 0.3, 0.4, 0.45, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};

    public static int[] cntBucket = {0, 1, 3, 5, 10, 15, 20};
    public static int[] ctrAndCvrLevelBucket = {1, 4, 8};

    public static double[] statCtrBucket = {0.001, 0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.40, 0.45, 0.50, 0.6, 0.7, 0.8, 0.9, 0.99};

    public static double[] statCvrBucket = {0.001, 0.01, 0.02, 0.03, 0.04, 0.05, 0.1, 0.15, 0.20, 0.25, 0.30, 0.35, 0.4, 0.45, 0.5, 0.6, 0.7, 0.8, 0.9, 0.99};


    public static boolean generateFeatureMapStatic(FeatureDto cf, Map<String, String> retMap) {
        boolean ret = false;

        try {
            if (cf != null) {

                cf.setCurrentGmtCreateTime(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS));

//                retMap.put("f101001", MyStringUtil2.Long2String(cf.getAdvertId()));

//                retMap.put("f106001", MyStringUtil2.Long2String(cf.getAccountId()));
                retMap.put("f108001", MyStringUtil2.Long2String(cf.getSlotId()));
                retMap.put("f109001", MyStringUtil2.Long2String(cf.getSlotType()));

                retMap.put("f201001", MyStringUtil2.Long2String(cf.getAppId()));
                retMap.put("f202001", cf.getAppCategory());

                retMap.put("f301001", MyStringUtil2.Long2String(cf.getOperatingActivityId()));
                retMap.put("f302001", MyStringUtil2.Long2String(cf.getActivityId()));
                retMap.put("f303001", MyStringUtil2.Long2String(cf.getActivityType()));

                retMap.put("f501001", cf.getUa());
                Integer hour = DateUtil.getHour(cf.getCurrentGmtCreateTime(), DateStyle.YYYY_MM_DD_HH_MM_SS);
                retMap.put("f502001", MyStringUtil2.Integer2String(hour));

                Integer weekDay = DateUtil.getWeekNumber(cf.getCurrentGmtCreateTime(), DateStyle.YYYY_MM_DD_HH_MM_SS);
                retMap.put("f502002", MyStringUtil2.Integer2String(weekDay));
                retMap.put("f503001", MyStringUtil2.Long2String(cf.getCityId()));

                // rank 转level
                retMap.put("f601001", MyStringUtil2.Long2String(getDayRankLevel(cf.getDayOrderRank())));
                retMap.put("f602001", MyStringUtil2.Long2String(getRankLevel(cf.getOrderRank())));
                retMap.put("f603001", MyStringUtil2.Long2String(getDayRankLevel(cf.getDayActivityOrderRank())));
                retMap.put("f604001", MyStringUtil2.Long2String(getRankLevel(cf.getActivityOrderRank())));


                // 计算时间差
                Long orderGmtIntervelLevel =
                        getOrderGmtIntervelLevel(cf.getCurrentGmtCreateTime(), cf.getLastGmtCreateTime());
                retMap.put("f605001", MyStringUtil2.Long2String(orderGmtIntervelLevel));

                // 计算活动时间差
                Long activityOrderGmtIntervelLevel =
                        getOrderGmtIntervelLevel(cf.getCurrentGmtCreateTime(), cf.getActivityLastGmtCreateTime());
                retMap.put("f606001", MyStringUtil2.Long2String(activityOrderGmtIntervelLevel));

                // 计算活动上次计费情况
                Long activityLastChargeStatus = getChargeStatus(cf.getActivityLastChargeNums());
                retMap.put("f607001", MyStringUtil2.Long2String(activityLastChargeStatus));

                // 计算上次计费情况
                Long lastChargeStatus = getChargeStatus(cf.getLastChargeNums());
                retMap.put("f608001", MyStringUtil2.Long2String(lastChargeStatus));

                //计算上次与当期活动是否相同
                Long activityChangeStatus = getActivityChangeStatus(cf.getOperatingActivityId(), cf.getLastOperatingActivityId());
                retMap.put("f609001", MyStringUtil2.Long2String(activityChangeStatus));


                //
//                String crossAppAndAdvertID = "" + cf.getAdvertId() + cf.getAppId();
//                retMap.put("cf101201", crossAppAndAdvertID);
                //
//                String crossActivityAndAdvertID = "" + cf.getAdvertId() + cf.getOperatingActivityId();
//                retMap.put("cf101301", crossActivityAndAdvertID);


                // 20170602
                // 设备型号
                retMap.put("f504001", cf.getModel());
                // 设备价格区间
                retMap.put("f505001", cf.getPriceSection());
                // 网络类型（2G，3G，4G）
                retMap.put("f506001", cf.getConnectionType());
                // 运营商（中国联通，中国移动，中国电信）
                retMap.put("f507001", cf.getOperatorType());
                // 发券次序，该用户当日发券次数
                retMap.put("f611001", MyStringUtil2.Long2String(cf.getPutIndex()));
                // 活动来源
                retMap.put("f306001", cf.getActivityUseType());
                // 	会员id  cvr
                retMap.put("f403001", cf.getMemberId());
                // 手机号  cvr
                retMap.put("f403005", cf.getMobile());


                // 是否老用户  注册距离最近登录时间大于1天  cvr
                Long iso = isOld(cf.getUserLastlogbigintime(), cf.getUserRegtime());
                retMap.put("f403004", MyStringUtil2.Long2String(iso));


                // 广告位标签
                retMap.put("f114001", cf.getSlotIndustryTagPid());
                retMap.put("f114002", cf.getSlotIndustryTagId());
                // 媒体标签
                retMap.put("f205001", cf.getAppIndustryTagPid());
                retMap.put("f205002", cf.getAppIndustryTagId());

                // 流量标签
                retMap.put("f206001", cf.getTrafficTagPid());
                retMap.put("f206002", cf.getTrafficTagId());

                //
                retMap.put("f9902", cf.getAppList2());
                retMap.put("f9906", cf.getCategoryIdList1());
                retMap.put("f9907", cf.getCategoryIdList2());
                retMap.put("f9908", cf.getIsGame());

                // 20180123
                retMap.put("f508001", cf.getPhoneBrand());
                retMap.put("f508002", cf.getPhoneModelNum());


                // 20180223
                retMap.put("f9914", cf.getImportantApp());
                retMap.put("f9915", cf.getClusterId());


                //20180306
                retMap.put("f9916", getLevel("launch_pv", cf.getUIIds(), cf.getUILaunchPV()));
                retMap.put("f9917", getLevel("click_pv", cf.getUIIds(), cf.getUIClickPv()));
                retMap.put("f9918", getLevel("effect_pv", cf.getUIIds(), cf.getUIEffectPv()));
                retMap.put("f9919", getLevel("score", cf.getUIIds(), cf.getUIScore()));


                retMap.put("f9921", cf.getUICtr());
                retMap.put("f9922", cf.getUICvr());
                retMap.put("f9923", cf.getUUnICtr());
                retMap.put("f9924", cf.getUUnICvr());

                retMap.put("f9927", countFeatures(cf.getUICtr()) + "");
                retMap.put("f9928", countFeatures(cf.getUICvr()) + "");
                retMap.put("f9929", countFeatures(cf.getUUnICtr()) + "");
                retMap.put("f9930", countFeatures(cf.getUUnICvr()) + "");


                // 20180314
                retMap.put("f404001", cf.getSex());
                retMap.put("f404002", cf.getAge());
                retMap.put("f404003", cf.getWorkStatus());
                retMap.put("f404004", cf.getStudentStatus());
                retMap.put("f404005", cf.getMarriageStatus());
                retMap.put("f404006", cf.getBear());
                retMap.put("f404007", cf.getInterestList());


                // 20180423，用户行为数据
                retMap.put("f601002", MyStringUtil2.Long2String(bucket(cf.getDayOrderRank(), dayOrderRankBucket)));
                retMap.put("f602002", MyStringUtil2.Long2String(bucket(cf.getOrderRank(), orderRankBucket)));
                retMap.put("f603002", MyStringUtil2.Long2String(bucket(cf.getDayActivityOrderRank(), dayActOrderRankBucket)));
                retMap.put("f604002", MyStringUtil2.Long2String(bucket(cf.getActivityOrderRank(), orderActRankBucket)));

                Long orderGmtIntervel =
                        getOrderGmtIntervel(cf.getCurrentGmtCreateTime(), cf.getLastGmtCreateTime());
                retMap.put("f605002", MyStringUtil2.Long2String(bucket(orderGmtIntervel, orderGmtIntervelBucket)));

                // 计算活动时间差
                Long activityOrderGmtIntervel =
                        getOrderGmtIntervel(cf.getCurrentGmtCreateTime(), cf.getActivityLastGmtCreateTime());
                retMap.put("f606002", MyStringUtil2.Long2String(bucket(activityOrderGmtIntervel, orderActGmtIntervelBucket)));


                // 20180424  添加安装列表类别及数目特征
                retMap.put("f9925", categoryIdAndCnt(cf.getCategory1idCntList(), 1));
                retMap.put("f9926", categoryIdAndCnt(cf.getCategory2idCntList(), 2));

                // 20180423
                Map<String, Map<String, Long>> ubpMap = getUserBehavioralPreference(
                        cf.getUIIds(), cf.getUILaunchPV(), cf.getUIClickPv(), cf.getUIEffectPv());

//                System.out.println("ubpMap="+JSON.toJSONString(ubpMap));
                ///全局
                retMap.put("f809001", getUserBehavioralPreference(0, "ctr", ubpMap, null));
                retMap.put("f809002", getUserBehavioralPreference(0, "cvr", ubpMap, null));

                //行业
                retMap.put("f808001", getUserBehavioralPreference(2, "ctr", ubpMap, null));
                retMap.put("f808002", getUserBehavioralPreference(2, "cvr", ubpMap, null));

                //统计
                retMap.put("f811001", getUserBehavioralPreference(3, "ctr", ubpMap, null));
                retMap.put("f811002", getUserBehavioralPreference(3, "cvr", ubpMap, null));


                cf.setUbpMap(ubpMap);

                ret = true;

            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return ret;
    }


    public static boolean generateFeatureMapDynamic(FeatureDto cf, Map<String, String> retMap) {

        boolean ret = false;
        try {
            if (cf != null) {
                generateFeatureMapDynamic(cf, cf, retMap);
                ret = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }


    public static boolean generateFeatureMapDynamic(FeatureDto cf, FeatureDto staticCf, Map<String, String> retMap) {

        boolean ret = false;
        try {
            if (cf != null) {


                // 广告ID
                retMap.put("f101001", MyStringUtil2.Long2String(cf.getAdvertId()));


                // 20170602
                // 广告主账号
                retMap.put("f106001", MyStringUtil2.Long2String(cf.getAccountId()));
                // 推广网址
                retMap.put("f111001", cf.getPromoteUrl());
                // 广告特征标签列表 以英文逗号分割
                retMap.put("f102001", cf.getMatchTagNums());
                // 商品id  cvr
                retMap.put("f801001", cf.getGoodsId());
                // 商品类目  cvr
                retMap.put("f802001", cf.getCatId());
                // 商品品牌 cvr
                retMap.put("f802002", cf.getBrandId());
                // 成本价 cvr
                retMap.put("f803001", MyStringUtil2.Long2String(getCost(cf.getCost(), cf.getPrice())));
                // 售卖价 cvr
                retMap.put("f803002", MyStringUtil2.Long2String(getLog(cf.getPrice())));
                // 浏览售卖比 cvr
                retMap.put("f803003", MyStringUtil2.Long2String(getCost(cf.getViewCount(), cf.getBuyCount())));
                //售卖量 cvr
                retMap.put("f803004", MyStringUtil2.Long2String(getLog(cf.getBuyCount())));


                //广告投放次数
                retMap.put("f110001", MyStringUtil2.Long2String(cf.getTimes()));
                //前后两单广告标签是否一致
                Long tagChangeStatus = getLastTagChangeStatus(cf.getMatchTagNums(), cf.getDayLastMatchTagNums());
                retMap.put("f610001", MyStringUtil2.Long2String(tagChangeStatus));

                // 20170728
                // 广告素材ID
                retMap.put("f104001", cf.getMaterialId());
                // 广告素材标签
                retMap.put("f112001", cf.getMaterialTags());

                // 广告描述标签
                retMap.put("f113001", cf.getAdvertTags());


                // 20171121
                retMap.put("f804001", MyStringUtil2.Long2String(getCtrIntervelLevel(cf.getAdvertCtr())));
                retMap.put("f804002", MyStringUtil2.Long2String(getCvrIntervelLevel(cf.getAdvertCvr())));

                retMap.put("f805001", MyStringUtil2.Long2String(getCtrIntervelLevel(cf.getAdvertAppCtr())));
                retMap.put("f805002", MyStringUtil2.Long2String(getCvrIntervelLevel(cf.getAdvertAppCvr())));

                retMap.put("f806001", MyStringUtil2.Long2String(getCtrIntervelLevel(cf.getAdvertSlotCtr())));
                retMap.put("f806002", MyStringUtil2.Long2String(getCvrIntervelLevel(cf.getAdvertSlotCvr())));

                retMap.put("f807001", MyStringUtil2.Long2String(getCtrIntervelLevel(cf.getAdvertActivityCtr())));
                retMap.put("f807002", MyStringUtil2.Long2String(getCvrIntervelLevel(cf.getAdvertActivityCvr())));

                // 20180507
                retMap.put("f804003", MyStringUtil2.Long2String(bucket(cf.getAdvertCtr(), statCtrBucket)));
                retMap.put("f804004", MyStringUtil2.Long2String(bucket(cf.getAdvertCvr(), statCvrBucket)));

                retMap.put("f805003", MyStringUtil2.Long2String(bucket(cf.getAdvertAppCtr(), statCtrBucket)));
                retMap.put("f805004", MyStringUtil2.Long2String(bucket(cf.getAdvertAppCvr(), statCvrBucket)));

                retMap.put("f806003", MyStringUtil2.Long2String(bucket(cf.getAdvertSlotCtr(), statCtrBucket)));
                retMap.put("f806004", MyStringUtil2.Long2String(bucket(cf.getAdvertSlotCvr(), statCvrBucket)));

                retMap.put("f807003", MyStringUtil2.Long2String(bucket(cf.getAdvertActivityCtr(), statCtrBucket)));
                retMap.put("f807004", MyStringUtil2.Long2String(bucket(cf.getAdvertActivityCvr(), statCvrBucket)));


                // 20180112
                retMap.put("f9912", cf.getTradeId());
                retMap.put("f9913", cf.getTradeId2());


                // 20180423
                Map<String, Map<String, Long>> ubpMap = staticCf.getUbpMap();

                retMap.put("f810001", getUserBehavioralPreference(1, "ctr", ubpMap, cf.getMatchTagNums()));
                retMap.put("f810002", getUserBehavioralPreference(1, "cvr", ubpMap, cf.getMatchTagNums()));


                retMap.put("f115001", cf.getBankEndType());

                ret = true;
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

        return ret;
    }


    public static Map<String, String> getFeatureMap(FeatureDto cf) {

        Map<String, String> retMap = new HashMap<>();
        try {
            generateFeatureMapStatic(cf, retMap);
            generateFeatureMapDynamic(cf, retMap);

        } catch (Exception e) {

            e.printStackTrace();
        }

        return retMap;
    }


    public static Long getCtrIntervelLevel(Double ctr) {

        Long ret = null;

        if (ctr == null) {
            ret = -1L;
        } else if (ctr <= 0.999) {
            ret = Math.round((ctr * 100) / 3);
        } else {
            ret = -1L;
        }
        return ret;
    }

    public static Long getCvrIntervelLevel(Double cvr) {

        Long ret = null;


        if (cvr == null) {
            ret = -1L;
        } else if (cvr <= 0.04) {
            ret = Math.round((cvr * 1000) / 4);
        } else if (cvr <= 0.999) {
            ret = 11 + Math.round(((cvr - 0.04) * 100) / 3);
        } else {
            ret = -1L;
        }
        return ret;
    }

    public static Long getOrderGmtIntervel(String date, String otherDate) {

        Long ret = null;
        Integer minutes = DateUtil.getIntervalMinutes(date, otherDate, DateStyle.YYYY_MM_DD_HH_MM_SS, DateStyle.YYYY_MM_DD_HH_MM_SS);
        if (minutes != null) {
            ret = minutes + 0L;
        }

        return ret;

    }


    public static Long getOrderGmtIntervelLevel(String date, String otherDate) {

        Long ret = null;
        Integer minutes = DateUtil.getIntervalMinutes(date, otherDate, DateStyle.YYYY_MM_DD_HH_MM_SS, DateStyle.YYYY_MM_DD_HH_MM_SS);

        if (minutes == null) {
            ret = -1L;
        } else if (minutes <= 5) {
            ret = 1L;
        } else if (minutes <= 10) {
            ret = 2L;
        } else if (minutes <= 30) {
            ret = 3L;
        } else if (minutes <= 60) {
            ret = 4L;
        } else {
            ret = 99L;
        }
        return ret;

    }

    public static Long getDayRankLevel(Long rank) {

        Long ret = null;
        if (rank == null) {
            ret = null;
        } else if (rank <= 7) {
            ret = rank;
        } else {
            ret = -1L;
        }
        return ret;
    }


    public static Long getRankLevel(Long rank) {

        Long ret = null;
        if (rank == null) {
            ret = null;
        } else if (rank <= 15) {
            ret = rank;
        } else {
            ret = -1L;
        }
        return ret;
    }

    public static Long getChargeStatus(Long chargeNums) {

        Long ret = null;
        if (chargeNums == null || chargeNums < 1) {
            ret = 0L;
        } else {
            ret = 1L;
        }
        return ret;
    }

    public static Long getActivityChangeStatus(Long operatingActivityId, Long lastOperatingActivityId) {

        Long ret = null;
        if (operatingActivityId == null || lastOperatingActivityId == null) {
            ret = 0L;
        } else if (operatingActivityId.equals(lastOperatingActivityId)) {
            ret = 1L;
        } else {
            ret = 0L;
        }
        return ret;
    }


    public static Long getLastTagChangeStatus(String matchTagNums, String lastMatchTagNums) {

        Long ret = null;
        if (matchTagNums == null || lastMatchTagNums == null) {
            ret = 0L;
        } else if (matchTagNums.equals(lastMatchTagNums)) {
            ret = 1L;
        } else {
            ret = 0L;
        }
        return ret;
    }

    /**
     * 是否大于1天
     *
     * @param userLastlogbigintime
     * @param userRegtime
     * @return
     */
    public static Long isOld(Date userLastlogbigintime, Date userRegtime) {
        Long ret = (long) 0;

        Integer dayIntervals = DateUtil.getIntervalDays(userLastlogbigintime, userRegtime);

        if (dayIntervals != null) {
            ret = dayIntervals > 1 ? (long) 1 : (long) 0;
        }
        return ret;

    }


    public static Long getCost(Long cost, Long price) {
        Long ret = null;
        if (cost != null && price != null && cost > 0) {
            ret = (long) (new Double(Math.ceil((price + 0.000001) / (cost))).intValue());
        }
        return ret;

    }

    public static Long getLog(Long lvalue) {
        Long ret = null;
        if (lvalue != null && lvalue > 0) {
            ret = (long) (new Double(Math.ceil(log(lvalue.doubleValue(), 2 / 0))).intValue());
        }
        return ret;

    }


    public static String getLevel(String key, String idlist, String valueList) {
        String s = "";

        if ((idlist == null || valueList == null) || (idlist.isEmpty() || valueList.isEmpty())) {
            return null;
        }
        String idArr[] = idlist.split(",");
        String valueArr[] = valueList.split(",");
        if (idArr.length != valueArr.length) {
            return null;
        }

        for (int i = 0; i < idArr.length; ++i) {

            double value = 0;
            try {
                value = Double.parseDouble(valueArr[i]);
            } catch (NumberFormatException e) {
                continue;
            } catch (NullPointerException e) {
                continue;
            }
            s += idArr[i] + "&" + String.valueOf(discretization(key, value)) + ",";


        }
        if (!s.isEmpty()) {
            s = s.substring(0, s.length() - 1);
        }
        return s;

    }

    public static int discretization(String key, double value) {
        int class_id = 0;
        switch (key) {
            case "launch_pv":
                if (value > -100000000 & value <= 0) class_id = 1;
                else if (value > 0 & value <= 1) class_id = 2;
                else if (value > 1 & value <= 2) class_id = 3;
                else if (value > 2 & value <= 3) class_id = 4;
                else if (value > 3 & value <= 4) class_id = 5;
                else if (value > 4 & value <= 5) class_id = 6;
                else if (value > 5 & value <= 6) class_id = 7;
                else if (value > 6 & value <= 7) class_id = 8;
                else if (value > 7 & value <= 18) class_id = 9;
                else if (value > 18 & value <= 50) class_id = 10;
                else if (value > 50 & value <= 150) class_id = 11;
                else if (value > 150 & value <= 300) class_id = 12;
                else class_id = 13;
                break;
            case "click_pv":
                if (value > -100000000 & value <= 0) class_id = 1;
                else if (value > 0 & value <= 1) class_id = 2;
                else if (value > 2 & value <= 3) class_id = 3;
                else if (value > 3 & value <= 4) class_id = 4;
                else if (value > 4 & value <= 5) class_id = 5;
                else if (value > 5 & value <= 6) class_id = 6;
                else if (value > 6 & value <= 8) class_id = 7;
                else if (value > 8 & value <= 15) class_id = 8;
                else if (value > 15 & value <= 80) class_id = 9;
                else if (value > 80 & value <= 225) class_id = 10;
                else class_id = 11;
                break;
            case "effect_pv":
                if (value > -100000000 & value <= 0) class_id = 1;
                else if (value > 0 & value <= 1) class_id = 2;
                else if (value > 1 & value <= 2) class_id = 3;
                else if (value > 2 & value <= 3) class_id = 4;
                else if (value > 4 & value <= 15.0) class_id = 5;
                else if (value > 15.0 & value <= 25) class_id = 6;
                else if (value > 25.0 & value <= 70) class_id = 7;
                else if (value > 70 & value <= 150) class_id = 8;
                else if (value > 150 & value <= 225) class_id = 9;
                else class_id = 10;
                break;
            case "score":
                if (value > -100000000 & value <= -471) class_id = 1;
                else if (value > -471 & value <= -331) class_id = 2;
                else if (value > -331 & value <= -168) class_id = 3;
                else if (value > -168 & value <= -52) class_id = 4;
                else if (value > -52 & value <= -19) class_id = 5;
                else if (value > -19 & value <= -8) class_id = 6;
                else if (value > -8 & value <= -4) class_id = 7;
                else if (value > -4 & value <= 1) class_id = 8;
                else if (value > 1 & value <= 2) class_id = 9;
                else if (value > 2 & value <= 17) class_id = 10;
                else if (value > 17 & value <= 42) class_id = 11;
                else if (value > 42 & value <= 73) class_id = 12;
                else if (value > 73 & value <= 126) class_id = 13;
                else if (value > 126 & value <= 284) class_id = 14;
                else class_id = 15;
                break;

        }
        return class_id;
    }


    /**
     * 用户行为数据解析
     *
     * @return
     */
    public static String getUserBehavioralPreference(int dimType, String statType, Map<String, Map<String, Long>> ubpMap, String tags) {

        String ret = null;
        try {
            // 全局
            if (dimType == 0 && ubpMap != null) {
                ret = MyStringUtil2.Long2String(ubpMap.get(statType).get("0"));


            }
            // 当前广告
            if (dimType == 1 && ubpMap != null) {
                String tag = getTag(tags);

                if (tag != null) {
                    ret = ubpMap.get(statType).get(tag) + "";

                    if (tag.length() == 10 && ret == null) {
                        String sTag = tag.substring(0, 5);
                        ret = MyStringUtil2.Long2String(ubpMap.get(statType).get(sTag));
                    }


                }
            }

            // 其他行业
            if (dimType == 2 && AssertUtil.isNotEmpty(ubpMap.get(statType))) {
                ret = "";
                for (Map.Entry<String, Long> entry : ubpMap.get(statType).entrySet()) {
                    String v = entry.getKey() + "_" + entry.getValue() + ",";
                    ret += v;
                }

                if (ret.endsWith(",")) {
                    ret = ret.substring(0, ret.length() - 1);
                }

            }


            //统计计数
            if (dimType == 3 && AssertUtil.isNotEmpty(ubpMap.get(statType))) {

                ret = "";
                Map<Long, Long> levelCntMap = new HashMap<>();

//                System.out.println("statType=" + statType);

//                for (long i = 0; i < ctrAndCvrLevelBucket.length + 2; i++) {
//                    levelCntMap.put(i, 0L);
//                }


                for (Map.Entry<String, Long> entry : ubpMap.get(statType).entrySet()) {
                    String tag = entry.getKey();
                    Long ctrLevel = bucket(entry.getValue(), ctrAndCvrLevelBucket);
                    if (ctrLevel != null && tag != "0") {
                        if (!levelCntMap.containsKey(ctrLevel)) {
                            levelCntMap.put(ctrLevel, 0L);
                        }
                        levelCntMap.put(ctrLevel, levelCntMap.get(ctrLevel) + 1);
//                        System.out.println("entry.getValue()=" + entry.getValue() + ",ctrLevel=" + ctrLevel + ",+1");
                    }
                }

                for (Map.Entry<Long, Long> entry : levelCntMap.entrySet()) {
                    if (entry.getKey() != null && entry.getValue() != null) {

                        Long cntLevel = bucket(entry.getValue(), cntBucket);
//                        System.out.println("entry.getKey()=" + entry.getKey() + "entry.getValue()=" + entry.getValue() + ",cntLevel=" + cntLevel + "+1");
                        String v = entry.getKey() * 100 + cntLevel + ",";
                        ret += v;
                    }

                }


                if (ret.endsWith(",")) {
                    ret = ret.substring(0, ret.length() - 1);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static String getTag(String tags) {
        String ret = null;
        if (AssertUtil.isNotEmpty(tags)) {
            String[] tagArr = tags.split(",");
            if (AssertUtil.isNotEmpty(tagArr)) {
                for (int i = 0; i < tagArr.length; i++) {
                    String tmp = validTag(tagArr[i]);
                    if (tmp != null) {
                        ret = validTag(tagArr[i]);
                    }

                }
            }
        }

        return ret;
    }

    public static String validTag(String tag) {
        String ret = null;
        if (AssertUtil.isNotEmpty(tag)) {
            if (tag.contains(".")) {
                ret = tag;
            }
        }
        return ret;
    }


    /**
     * 用户行为数据解析
     *
     * @param uIIds
     * @param uILaunchPv
     * @param uIClickPv
     * @param uIEffectPv
     * @return
     */
    public static Map<String, Map<String, Long>> getUserBehavioralPreference(String uIIds,
                                                                             String uILaunchPv,
                                                                             String uIClickPv,
                                                                             String uIEffectPv) {
        Map<String, Map<String, Long>> ret = new HashMap<>();
        ret.put("ctr", new HashMap<String, Long>());
        ret.put("cvr", new HashMap<String, Long>());
        if (AssertUtil.isAllNotEmpty(uIIds, uILaunchPv, uIClickPv, uIEffectPv)) {
            String[] uIIdsArr = uIIds.split(",");
            String[] uILaunchPvArr = uILaunchPv.split(",");
            String[] uIClickPvArr = uIClickPv.split(",");
            String[] uIEffectPvArr = uIEffectPv.split(",");

            if (uIIdsArr.length > 0 &&
                    uIIdsArr.length == uILaunchPvArr.length &&
                    uIIdsArr.length == uIClickPvArr.length &&
                    uIIdsArr.length == uIEffectPvArr.length) {

                Long launchAccPv = 0L;
                Long clickAccPv = 0L;
                Long effectAccPv = 0L;


                for (int i = 0; i < uIIdsArr.length; i++) {
                    String id = uIIdsArr[i];
//                    System.out.println("uIIdsArr[i]=" + uIIdsArr[i]);
//                    System.out.println("uILaunchPvArr[i]=" + uILaunchPvArr[i]);
//                    System.out.println("uIClickPvArr[i]=" + uIClickPvArr[i]);
//                    System.out.println("uIEffectPvArr[i]=" + uIEffectPvArr[i]);

                    Long launchPv = DataUtil.str2Long(uILaunchPvArr[i], 0L);
                    Long clickPv = DataUtil.str2Long(uIClickPvArr[i], 0L);
                    Long effectPv = DataUtil.str2Long(uIEffectPvArr[i], 0L);

//                    System.out.println("launchPv=" + launchPv);
//                    System.out.println("clickPv=" + clickPv);
//                    System.out.println("effectPv=" + effectPv);

                    Double ctr = getCtrOrCvr(launchPv, clickPv);
                    Double cvr = getCtrOrCvr(clickPv, effectPv);

                    Long ctrBucketLevel = bucket(ctr, userCtrBucket);
                    Long cvrBucketLevel = bucket(cvr, userCvrBucket);

                    ret.get("ctr").put(id, ctrBucketLevel);
                    ret.get("cvr").put(id, cvrBucketLevel);

                    launchAccPv += launchPv;
                    clickAccPv += clickPv;
                    effectAccPv += effectPv;

                }
                Double ctr = getCtrOrCvr(launchAccPv, clickAccPv);
                Double cvr = getCtrOrCvr(clickAccPv, effectAccPv);

                Long ctrBucketLevel = bucket(ctr, userCtrBucket);
                Long cvrBucketLevel = bucket(cvr, userCvrBucket);

                ret.get("ctr").put("0", ctrBucketLevel);
                ret.get("cvr").put("0", cvrBucketLevel);

            }


        }

        return ret;
    }


    /**
     * @param firstPv  分母 非空
     * @param secondPv 分子 非空
     * @return
     */
    public static Double getCtrOrCvr(Long firstPv, Long secondPv) {
        Double ret = null;
        if (AssertUtil.isAllNotEmpty(firstPv, secondPv) && firstPv > 0)
            ret = DataUtil.division(secondPv, firstPv);

        return ret;
    }

    /**
     * 分桶函数1
     * <p>
     * 左开又闭区间
     * 其他情况下的闭合区间设计需注意！！
     *
     * @param value
     * @param bucketList 不为空，且不含有空值（未判断）
     * @return
     */
    public static Long bucket(Long value, int[] bucketList) {
        long ret = 0;
        if (value != null && bucketList != null && bucketList.length > 0) {
            ret = bucketList.length + 1;
            for (int i = 0; i < bucketList.length; i++) {
                int bound = bucketList[i];

                if (value <= bound) {
                    ret = i + 1;
                    break;
                }
            }


        }
        return ret;
    }

    /**
     * 分桶函数2
     * <p>
     * 左开又闭区间
     * 其他情况下的闭合区间设计需注意！！
     *
     * @param value
     * @param bucketList 不为空，且不含有空值（未判断）
     * @return
     */
    public static Long bucket(Double value, double[] bucketList) {
        long ret = 0;
        if (value != null && bucketList != null && bucketList.length > 0) {
            ret = bucketList.length + 1;
            for (int i = 0; i < bucketList.length; i++) {
                double bound = bucketList[i];

                if (value <= bound) {
                    ret = i + 1;
                    break;
                }
            }


        }
        return ret;
    }

    public static double log(double value, double base) {
        return Math.log(value) / Math.log(base);
    }


    /**
     * @param categoryCount
     * @param categoryType
     * @return
     */
    public static String categoryIdAndCnt(Map<String, Long> categoryCount, Integer categoryType) {
        String ret1 = null;
        if (categoryCount == null) {
            return ret1;
        }

        if (categoryType == 1) {
            for (Map.Entry<String, Long> entry : categoryCount.entrySet()) {
                String key = entry.getKey();
                try {
                    Long value = entry.getValue();
                    String valueRet = null;
                    if (value >= 1 && value <= 3) {
                        valueRet = "g1";
                    } else if (value >= 4 && value <= 10) {
                        valueRet = "g2";
                    } else if (value >= 11 && value <= 20) {
                        valueRet = "g3";
                    } else {
                        valueRet = "g4";
                    }
                    String ret = key + "&" + valueRet;
                    ret1 = ret1 + "," + ret;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } else {

            for (Map.Entry<String, Long> entry : categoryCount.entrySet()) {
                String key = entry.getKey();
                try {
                    Long value = entry.getValue();
                    String valueRet = null;
                    if (value == 1) {
                        valueRet = "g1";
                    } else if (value == 2 || value == 3) {
                        valueRet = "g2";
                    } else if (value >= 4 && value <= 8) {
                        valueRet = "g3";
                    } else {
                        valueRet = "g4";
                    }
                    String ret = key + "&" + valueRet;
                    ret1 = ret1 + "," + ret;

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return ret1;
    }


    public static void main(String[] args) {


////        Map<String, Map<String, String>> ret2 = getUserBehavioralPreference("02.01.0001,02.01.0008,02.05,02.15", "2,7,5,2", "2,4,3,2", "1,1,1,1");
////        Map<String, Map<String, String>> ret2 = getUserBehavioralPreference("1,", "null,", "1,", "1,");
//        System.out.println("ret2 = " + JSON.toJSONString(ret2));
//
//        String ret3 = getUserBehavioralPreference(0, "ctr", ret2, null);
//        String ret4 = getUserBehavioralPreference(1, "ctr", ret2, "02.34.0001,34");
//        String ret5 = getUserBehavioralPreference(2, "ctr", ret2, null);
//        System.out.println("ret3=" + ret3);
//        System.out.println("ret4=" + ret4);
//        System.out.println("ret5=" + ret5);

//        Long orderGmtIntervel =
//                getOrderGmtIntervel("2018-04-22 12:00:00", "2018-04-21 10:00:00");
//        System.out.println(MyStringUtil2.Long2String(bucket(orderGmtIntervel, orderActGmtIntervelBucket)));
        FeatureDto featureDto = new FeatureDto();

        featureDto.setUIIds("02.01.0001,02.01.0002,02.01.0003,02.23.0001");
        featureDto.setUILaunchPV("10,9,9,3,");
        featureDto.setUIClickPv("1,1,1,1,");
        featureDto.setUIEffectPv("0,0,0,0,");
        FeatureDto featureDto2 = new FeatureDto();
        featureDto2.setMatchTagNums("05.01.0005,02.01.0001");
        Map<String, String> staticMap = new HashMap<>();
        FeatureParse.generateFeatureMapStatic(featureDto, staticMap);
//        System.out.println("staticMap=" + JSON.toJSONString(staticMap));

        Map<String, String> dynamicMap = new HashMap<>();
        FeatureParse.generateFeatureMapDynamic(featureDto2, featureDto, dynamicMap);
        dynamicMap.putAll(staticMap);
        System.out.println("dynamicMap=" + JSON.toJSONString(dynamicMap));


    }


    public static int countFeatures(String valueList) {
        if (valueList == null) {
            return -1;
        }
        if (valueList.isEmpty()) {
            return 0;
        }
        String idArr[] = valueList.split(",");
        if (idArr == null) {
            return -1;
        }
        return idArr.length;
    }

}
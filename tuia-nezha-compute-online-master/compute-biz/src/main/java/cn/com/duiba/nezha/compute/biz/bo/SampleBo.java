package cn.com.duiba.nezha.compute.biz.bo;

import cn.com.duiba.nezha.compute.biz.dto.PsModelSample;
import cn.com.duiba.nezha.compute.biz.support.SampleParse;
import cn.com.duiba.nezha.compute.core.LabeledFeature;
import cn.com.duiba.nezha.compute.core.LabeledPoint;
import cn.com.duiba.nezha.compute.core.enums.DateStyle;
import cn.com.duiba.nezha.compute.core.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SampleBo {

    public static LabeledPoint[] getSampleByOrderIdList(String modelId, boolean isCtr, List<String> orderIdList) throws Exception {


        // 3 读取样本
        List<PsModelSample> samples = PsBo.getPsSample(orderIdList);
        // 4 解析样本
        List<LabeledPoint> data = SampleParse.parse(samples, modelId, isCtr);
//        System.out.println("data.size="+data.size());

        LabeledPoint[] dataArray = data.toArray(new LabeledPoint[data.size()]);


        return dataArray;
    }

    public static LabeledFeature[] getSampleStrByOrderIdList(boolean isCtr, List<String> orderIdList) throws Exception {


        // 3 读取样本
        List<PsModelSample> samples = PsBo.getPsSample(orderIdList);
        // 4 解析样本
        List<LabeledFeature> data = SampleParse.parse(samples, isCtr);
//        System.out.println("data.size="+data.size());

        LabeledFeature[] dataArray = data.toArray(new LabeledFeature[data.size()]);


        return dataArray;
    }


    public static String[] getOrderList(List<String> timeList) throws Exception {
        String[] ret = null;
        if (timeList != null) {
            List<String> retTmp = PsBo.getOrderList(timeList);
            if (retTmp != null) {
                ret = retTmp.toArray(new String[retTmp.size()]);
            }
        }


        return ret;

    }


    public static List<String> getTimeInterval(String baseTime, int minute) {
        List<String> dateList = new ArrayList<>();
        Date baseDate = DateUtil.getDate(baseTime, DateStyle.YYYY_MM_DD_HH_MM_SS);
        Date baseDate2 = DateUtil.addMinute(baseDate, minute);
        System.out.println("time=" + DateUtil.getDate(baseDate2, DateStyle.YYYY_MM_DD_HH_MM));
        for (int i = 0; i < 60; i++) {
            Date addDate = DateUtil.addSecond(baseDate2, i);
            String dateStr = DateUtil.getDateTime(addDate, DateStyle.YYYYMMDDHHMMSS);
            dateList.add(dateStr);
        }

        return dateList;
    }

    public static List<String> getTimeIntervalWithRatio(String baseTime, int minute, double ratio) {
        List<String> dateList = new ArrayList<>();
        Date baseDate = DateUtil.getDate(baseTime, DateStyle.YYYY_MM_DD_HH_MM_SS);
        Date baseDate2 = DateUtil.addMinute(baseDate, minute);
        System.out.println("time=" + DateUtil.getDate(baseDate2, DateStyle.YYYY_MM_DD_HH_MM));
        for (int i = 0; i < 60; i++) {
            if (Math.random() < ratio) {
                Date addDate = DateUtil.addSecond(baseDate2, i);
                String dateStr = DateUtil.getDateTime(addDate, DateStyle.YYYYMMDDHHMMSS);
                dateList.add(dateStr);
            }
        }

        return dateList;
    }
}

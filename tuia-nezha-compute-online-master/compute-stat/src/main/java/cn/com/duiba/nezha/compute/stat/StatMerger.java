package cn.com.duiba.nezha.compute.stat;

import cn.com.duiba.nezha.compute.stat.dto.StatDto;
import cn.com.duiba.nezha.compute.stat.utils.AssertUtil;
import cn.com.duiba.nezha.compute.stat.utils.MathUtil;
import cn.com.duiba.nezha.compute.stat.utils.MergerUtil;

import java.util.Map;

/**
 * Created by pc on 2017/9/12.
 */
public class StatMerger {


    /**
     * 维度融合
     *
     * @param appDto
     * @param activityDto
     * @param slotDto
     * @return
     * @throws Exception
     */
    public static StatDto dimsMerge(StatDto appDto,
                                    StatDto activityDto,
                                    StatDto slotDto,
                                    StatDto globalDto) throws Exception {
        StatDto ret = null;
        if (globalDto != null && globalDto.getLaunchCnt() > 50) {
            ret = globalDto;
        }
        if (activityDto != null && activityDto.getLaunchCnt() > 50) {
            ret = activityDto;
        }
        if (appDto != null && appDto.getLaunchCnt() > 50) {
            ret = appDto;
        }
        if (slotDto != null && slotDto.getLaunchCnt() > 50) {
            ret = slotDto;
        }
        return ret;
    }


    /**
     * 单独维度融合
     *
     * @param dimDto
     * @param globalDto
     * @return
     * @throws Exception
     */
    public static StatDto dimMerge(StatDto dimDto,
                                   StatDto globalDto) throws Exception {
        StatDto ret = null;
        if (globalDto != null && globalDto.getLaunchCnt() > 50) {
            ret = globalDto;
        }
        if (dimDto != null && dimDto.getLaunchCnt() > 20) {
            ret = dimDto;
        }
        return ret;
    }


    /**
     * 时间维度融合
     *
     * @throws Exception
     */
    public static StatDto intervalMerge(
            Map<Long, StatDto> statDtoHourMap,
            Map<Long, StatDto> statDtoDayMap
    ) throws Exception {

        StatDto ret = null;
        if (AssertUtil.isAllEmpty(statDtoHourMap, statDtoDayMap)) {
            return ret;
        }

        ret = new StatDto();

        StatDto hourMergeDto = intervalMerge(statDtoHourMap, 2, 1L);
        StatDto dayMergeDto = intervalMerge(statDtoDayMap, 3, 2L);
//        System.out.println("hourMergeDto=" + JSON.toJSONString(hourMergeDto));
//        System.out.println("dayMergeDto=" + JSON.toJSONString(dayMergeDto));
        long hourLaunchCnt = 0;
        long hourChargeCnt = 0;
        long hourActClickCnt = 0;
        long hourActExpCnt = 0;


        long dayLaunchCnt = 0;
        long dayChargeCnt = 0;
        long dayActClickCnt = 0;
        long dayActExpCnt = 0;

        if (hourMergeDto != null) {
            hourLaunchCnt = MathUtil.tolong(hourMergeDto.getLaunchCnt());
            hourChargeCnt = MathUtil.tolong(hourMergeDto.getChargeCnt());
            hourActClickCnt = MathUtil.tolong(hourMergeDto.getActClickCnt());
            hourActExpCnt = MathUtil.tolong(hourMergeDto.getActExpCnt());
        }


        if (dayMergeDto != null) {
            dayLaunchCnt = MathUtil.tolong(dayMergeDto.getLaunchCnt());
            dayChargeCnt = MathUtil.tolong(dayMergeDto.getChargeCnt());
            dayActClickCnt = MathUtil.tolong(dayMergeDto.getActClickCnt());
            dayActExpCnt = MathUtil.tolong(dayMergeDto.getActExpCnt());
        }

        double historyCtr = MergerUtil.getCtrWithBias(dayChargeCnt, dayLaunchCnt, 200, 0.3, 100, 5);
        double historyCvr = MergerUtil.getCtrWithBias(dayActClickCnt, dayChargeCnt, 200, 0.05, 100, 5);

        System.out.println("historyCtr=" + historyCtr);
        System.out.println("historyCvr=" + historyCvr);

        double mergeCtr = MergerUtil.getCtrWithBias(hourChargeCnt, hourLaunchCnt, 200, historyCtr, 200, 5);
        double mergeCvr = MergerUtil.getCtrWithBias(hourActClickCnt, hourChargeCnt, 200, historyCvr, 200, 5);

//        System.out.println("mergeCtr=" + mergeCtr);
//        System.out.println("mergeCvr=" + mergeCvr);


        ret.setLaunchCnt(hourLaunchCnt);
        ret.setChargeCnt(hourChargeCnt);
        ret.setActClickCnt(hourActClickCnt);
        ret.setActExpCnt(hourActExpCnt);

        ret.setMergeCtr(mergeCtr);
        ret.setMergeCvr(mergeCvr);


        return ret;
    }


    /**
     * 小时数据融合
     *
     * @param statDtoMap
     * @param type       1:hour 2:day
     * @throws Exception
     */
    private static StatDto intervalMerge(Map<Long, StatDto> statDtoMap, int upLimit, Long type) throws Exception {

        StatDto ret = null;
        if (AssertUtil.isEmpty(statDtoMap) || type == null) {
            return ret;
        }


        ret = new StatDto();

        Long weightCnt = 0L;
        Long launchCnt = 0L;
        Long chargeCnt = 0L;
        Long actClickCnt = 0L;
        Long actExpCnt = 0L;

        int i = 0;
        for (Map.Entry<Long, StatDto> entry : statDtoMap.entrySet()) {

            Long index = entry.getKey();
            Long weight = mergeWeight(index, type, upLimit);
            StatDto dto = entry.getValue();
            //权重融合处理
            if (weight != null && weight > 0.0 && dto != null) {
                weightCnt += weight;
                launchCnt = MathUtil.add(MathUtil.dot(dto.getLaunchCnt(), weight), launchCnt);
                chargeCnt = MathUtil.add(MathUtil.dot(dto.getChargeCnt(), weight), chargeCnt);
                actClickCnt = MathUtil.add(MathUtil.dot(dto.getActClickCnt(), weight), actClickCnt);
                actExpCnt = MathUtil.add(MathUtil.dot(dto.getActExpCnt(), weight), actExpCnt);
                i++;
            }


        }

        // 去除权重
        ret.setLaunchCnt(MathUtil.division(launchCnt, weightCnt / i));
        ret.setChargeCnt(MathUtil.division(chargeCnt, weightCnt / i));
        ret.setActExpCnt(MathUtil.division(actExpCnt, weightCnt / i));
        ret.setActClickCnt(MathUtil.division(actClickCnt, weightCnt / i));

        return ret;
    }

    /**
     * 融合权重
     *
     * @param lastNum
     * @param type
     * @return
     */
    public static Long mergeWeight(Long lastNum, Long type, long upLimit) {
        Long ret = null;

        if (lastNum > upLimit) {
            return ret;
        }
        if (type.equals(1L)) {
            ret = MergerUtil.hourMergeWeight(lastNum);
        }
        if (type.equals(2L)) {
            ret = MergerUtil.dayMergeWeight(lastNum);
        }
        return ret;
    }


}

package cn.com.duiba.nezha.compute.stat;

import cn.com.duiba.nezha.compute.stat.dto.CorrectStatDto;
import cn.com.duiba.nezha.compute.stat.utils.AssertUtil;
import cn.com.duiba.nezha.compute.stat.utils.MathUtil;
import cn.com.duiba.nezha.compute.stat.utils.MergerUtil;
import com.alibaba.fastjson.JSON;

import java.util.Map;

/**
 * Created by pc on 2017/9/12.
 */
public class CorrectStatMerger {


    /**
     * 单独维度融合
     *
     * @param dimDto
     * @param globalDto
     * @return
     * @throws Exception
     */
    public static CorrectStatDto dimMerge(CorrectStatDto dimDto,
                                          CorrectStatDto globalDto) throws Exception {
        CorrectStatDto ret = null;
        if (globalDto != null && globalDto.getCtrLaunchCnt() > 10) {
            ret = globalDto;
        }
        if (dimDto != null && dimDto.getCtrLaunchCnt() > 10) {
            ret = dimDto;
        }
        return ret;
    }


    /**
     * 时间轴融合  小时、日粒度
     *
     * @param correctStatDtoHourMap
     * @param correctStatDtoDayMap
     * @return
     * @throws Exception
     */
    public static CorrectStatDto intervalMerge(Map<Long, CorrectStatDto> correctStatDtoHourMap,
                                               Map<Long, CorrectStatDto> correctStatDtoDayMap) throws Exception {
        CorrectStatDto ret = null;
        if (AssertUtil.isAllEmpty(correctStatDtoHourMap, correctStatDtoDayMap)) {
            return ret;
        }
        ret = new CorrectStatDto();

        CorrectStatDto hourMergeDto = statMerge(correctStatDtoHourMap, 6, 1L);
        CorrectStatDto dayMergeDto = statMerge(correctStatDtoDayMap, 3, 2L);
//        System.out.println("hourMergeDto="+ JSON.toJSONString(hourMergeDto));
//        System.out.println("dayMergeDto="+ JSON.toJSONString(dayMergeDto));

        long hourCtrLaunchCnt = 0;
        long hourCvrLaunchCnt = 0;
        double hourPreCtrAcc = 0.0;
        double hourPreCvrAcc = 0.0;
        double hourStatCtrAcc = 0.0;
        double hourStatCvrAcc = 0.0;


        long dayCtrLaunchCnt = 0;
        long dayCvrLaunchCnt = 0;
        double dayPreCtrAcc = 0.0;
        double dayPreCvrAcc = 0.0;
        double dayStatCtrAcc = 0.0;
        double dayStatCvrAcc = 0.0;


        if (hourMergeDto != null) {
            hourCtrLaunchCnt = MathUtil.tolong(hourMergeDto.getCtrLaunchCnt());
            hourCvrLaunchCnt = MathUtil.tolong(hourMergeDto.getCvrLaunchCnt());

            hourPreCtrAcc = MathUtil.toddouble(hourMergeDto.getPreCtrAcc());
            hourPreCvrAcc = MathUtil.toddouble(hourMergeDto.getPreCvrAcc());

            hourStatCtrAcc = MathUtil.toddouble(hourMergeDto.getStatCtrAcc());
            hourStatCvrAcc = MathUtil.toddouble(hourMergeDto.getStatCvrAcc());
        }


        if (dayMergeDto != null) {

            dayCtrLaunchCnt = MathUtil.tolong(dayMergeDto.getCtrLaunchCnt());
            dayCvrLaunchCnt = MathUtil.tolong(dayMergeDto.getCvrLaunchCnt());

            dayPreCtrAcc = MathUtil.toddouble(dayMergeDto.getPreCtrAcc());
            dayPreCvrAcc = MathUtil.toddouble(dayMergeDto.getPreCvrAcc());

            dayStatCtrAcc = MathUtil.toddouble(dayMergeDto.getStatCtrAcc());
            dayStatCvrAcc = MathUtil.toddouble(dayMergeDto.getStatCvrAcc());

        }



        Double historyPreCtrAvg = MergerUtil.getCtrWithBias(dayPreCtrAcc, dayCtrLaunchCnt, 100, null,100, 6);
        Double historyPreCvrAvg = MergerUtil.getCtrWithBias(dayPreCvrAcc, dayCvrLaunchCnt, 100, null,100, 6);

        Double historyStatCtrAvg = MergerUtil.getCtrWithBias(dayStatCtrAcc, dayCtrLaunchCnt, 100, null,100, 6);
        Double historyStatCvrAvg = MergerUtil.getCtrWithBias(dayStatCvrAcc, dayCvrLaunchCnt, 100, null,100, 6);

//        System.out.println("historyPreCtrAvg="+ historyPreCtrAvg);
//        System.out.println("historyPreCvrAvg="+ historyPreCvrAvg);
//        System.out.println("historyStatCtrAvg="+ historyStatCtrAvg);
//        System.out.println("historyStatCvrAvg="+ historyStatCvrAvg);

        Double hourPreCtrAvg = MergerUtil.getCtrWithBias(hourPreCtrAcc, hourCtrLaunchCnt, 100, historyPreCtrAvg,100, 6);
        Double hourPreCvrAvg = MergerUtil.getCtrWithBias(hourPreCvrAcc, hourCvrLaunchCnt, 100, historyPreCvrAvg,100, 6);

        Double hourStatCtrAvg = MergerUtil.getCtrWithBias(hourStatCtrAcc, hourCtrLaunchCnt, 100, historyStatCtrAvg,100, 6);
        Double hourStatCvrAvg = MergerUtil.getCtrWithBias(hourStatCvrAcc, hourCvrLaunchCnt, 100, historyStatCvrAvg,100, 6);

//
//        System.out.println("hourPreCtrAvg="+ hourPreCtrAvg);
//        System.out.println("hourPreCvrAvg="+ hourPreCvrAvg);
//        System.out.println("hourStatCtrAvg="+ hourStatCtrAvg);
//        System.out.println("hourStatCvrAvg="+ hourStatCvrAvg);

        ret.setCtrLaunchCnt(hourCtrLaunchCnt);
        ret.setCvrLaunchCnt(hourCvrLaunchCnt);

        ret.setPreCtrAcc(hourPreCtrAcc);
        ret.setPreCvrAcc(hourPreCvrAcc);

        ret.setStatCtrAcc(hourStatCtrAcc);
        ret.setStatCvrAcc(hourStatCvrAcc);

        ret.setPreCtrAvg(hourPreCtrAvg);
        ret.setPreCvrAvg(hourPreCvrAvg);
        ret.setStatCtrAvg(hourStatCtrAvg);
        ret.setStatCvrAvg(hourStatCvrAvg);

        return ret;
    }

    /**
     * 小时数据融合
     *
     * @param statDtoMap
     * @param type       1:hour 2:day
     * @throws Exception
     */
    private static CorrectStatDto statMerge(Map<Long, CorrectStatDto> statDtoMap, int upLimit, Long type) throws Exception {

        CorrectStatDto ret = null;
        if (AssertUtil.isEmpty(statDtoMap) || type == null) {
            return ret;
        }


        ret = new CorrectStatDto();

        Long weightCnt = 0L;


        Long ctrLaunchCnt = 0L;
        Long cvrLaunchCnt = 0L;
        Double preCtrAcc = 0.0;
        Double preCvrAcc = 0.0;
        Double statCtrAcc = 0.0;
        Double statCvrAcc = 0.0;


        int i = 0;
        for (Map.Entry<Long, CorrectStatDto> entry : statDtoMap.entrySet()) {

            Long index = entry.getKey();
            Long weight = mergeWeight(index, type, upLimit);

//            System.out.println("index="+index+",weight="+weight);
            CorrectStatDto dto = entry.getValue();
            //权重融合处理
            if (weight != null && dto != null) {
                weightCnt += weight;
                ctrLaunchCnt = MathUtil.add(MathUtil.dot(dto.getCtrLaunchCnt(), weight), ctrLaunchCnt);
                cvrLaunchCnt = MathUtil.add(MathUtil.dot(dto.getCvrLaunchCnt(), weight), cvrLaunchCnt);
                preCtrAcc = MathUtil.add(MathUtil.dot(dto.getPreCtrAcc(), weight), preCtrAcc);
                preCvrAcc = MathUtil.add(MathUtil.dot(dto.getPreCvrAcc(), weight), preCvrAcc);
                statCtrAcc = MathUtil.add(MathUtil.dot(dto.getStatCtrAcc(), weight), statCtrAcc);
                statCvrAcc = MathUtil.add(MathUtil.dot(dto.getStatCvrAcc(), weight), statCvrAcc);

                i++;
            }


        }

        // 去除权重
        ret.setCtrLaunchCnt(MathUtil.division(ctrLaunchCnt, weightCnt / i));
        ret.setCvrLaunchCnt(MathUtil.division(cvrLaunchCnt, weightCnt / i));

        ret.setPreCtrAcc(MathUtil.division(preCtrAcc, weightCnt / i, 5));
        ret.setPreCvrAcc(MathUtil.division(preCvrAcc, weightCnt / i, 5));

        ret.setStatCtrAcc(MathUtil.division(statCtrAcc, weightCnt / i, 5));
        ret.setStatCvrAcc(MathUtil.division(statCvrAcc, weightCnt / i, 5));

        return ret;
    }


    /**
     * 融合权重
     *
     * @param lastNum
     * @param type
     * @return
     */
    private static Long mergeWeight(long lastNum, Long type, long upLimit) {
        Long ret = null;

        if(lastNum>upLimit){
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


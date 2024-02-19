package cn.com.duiba.nezha.compute.common.model.pacing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.List;

/**
 * Created by jiali on 2017/8/31.
 */
public class Pacing {

    private static final Logger logger = LoggerFactory.getLogger(Pacing.class);

    public boolean checkParam()
    {
        return true;
    }

    public enum Status {
        ocpc_normal, cpc_normal,dollmachine, automatch, autothrow, unkown;
    }

    public boolean pacing(AdInfo adInfo, CvrInfo cvrInfo, CtrInfo ctrInfo, StatInfo adClkInfo, StatInfo orientClkInfo, TimeInfo timeInfo)
    {
        //init
        boolean isGiveUp = false;
        boolean isWarn = false;

        Status stat = Status.unkown;
        if(adInfo.type.chargeType == 2 && (adInfo.type.pid & 2) == 0 && (adInfo.type.pid & 4) == 0)
        {
            stat = Status.ocpc_normal;
        }
        else if(adInfo.type.chargeType == 1 && (adInfo.type.pid & 2) == 0 && (adInfo.type.pid & 4) == 0)
        {
            stat = Status.cpc_normal;
        }
        else if((adInfo.type.pid.intValue() & 2) > 0){
            stat = Status.dollmachine;
        }
        else if((adInfo.type.pid.intValue() & 4) > 0)
        {
            stat = Status.automatch;
        }

        //prepare data
        double fee1d = 0, clk1d = 0;
        for(double fee1h : timeInfo.hourFee)
        {
            if(fee1h != -1) fee1d += fee1h;
        }
        for(double clk1h : timeInfo.hourClk)
        {
            if(clk1d != -1) clk1d += clk1h;
        }
        long tag = 0;
        switch (stat)
        {
            case ocpc_normal:
                Double  competence = competence(cvrInfo,ctrInfo,adClkInfo,timeInfo);
                Double priceScore = priceScore(adInfo.type.chargeType, adInfo.fee, adInfo.factor, adClkInfo,timeInfo);
                Double prob = competence * priceScore;
                double risk = risk(cvrInfo,ctrInfo,adClkInfo,timeInfo);
                boolean isRisk = Math.random() < risk ? true : false;

                if(!isRisk)
                {
                    isGiveUp = Math.random() > competence ? true : false;
                }
                if(!isGiveUp)
                {
                    isGiveUp = Math.random() > priceScore ? true : false;
                }

                if(isGiveUp) {
                    tag = 3;
                }


                if(Math.random()<0.0001 && isGiveUp)
                {
                    logger.info("pacing advertId:{} giveup:{} competence:{} pricescore:{} risk:{}", adInfo.advertId, isGiveUp,competence,priceScore,risk);
                }
                else if(Math.random()<0.00001 && !isGiveUp)
                {
                    logger.info("pacing advertId:{} giveup:{} competence:{} pricescore:{} risk:{}", adInfo.advertId, isGiveUp,competence,priceScore,risk);
                }
                break;
            case cpc_normal:
                if(orientClkInfo.g1d != null && cvrInfo.orientCvrInfo.g1d!=null && adInfo.autoMatchInfo.enable == true)
                {
                    double dayConvert = orientClkInfo.g1d * cvrInfo.orientCvrInfo.g1d;
                    if(adInfo.type.chargeType == 1 && cvrInfo.cvr < 0.3 * cvrInfo.orientCvrInfo.g1d && dayConvert > 3) //流量放弃
                    {
                        isGiveUp = true;
                        tag = 1;
                        if(Math.random() < 0.0001)
                            logger.info("automatch advertId:{} orientId:{} appid:{} giveup:{} convert:{} fee1d:{} clk1d:{} fee:{} cvr:{} gcvr:{} ccvr:{} cgcvr:{} pack:{} ctr:{}", adInfo.advertId, adInfo.orientId, adInfo.appId, isGiveUp, dayConvert, fee1d, clk1d, adInfo.fee,cvrInfo.cvr,cvrInfo.orientCvrInfo.g1d,cvrInfo.competerCvrInfo.app1d,cvrInfo.competerCvrInfo.g1d,adInfo.type.packageType,ctrInfo.ctr);
                    }
                }
                break;
            case dollmachine:
                if(adInfo.advertId == 21357 || adInfo.advertId == 26159 || adInfo.advertId == 24230)
                {
                    double hourConvert = adClkInfo.g1h * cvrInfo.adCvrInfo.g1h;
                    double maxConvert = 4000;
                    if(adInfo.advertId == 21357)
                        maxConvert = 9000;
                    Calendar calendar = Calendar.getInstance();
                    int minutes = calendar.get(Calendar.MINUTE);
                    double speed = 0;
                    if(minutes > 0)
                    {
                        speed = hourConvert / minutes;
                        if(speed > 0.001) {
                            double p = (maxConvert *0.7) / (speed * 60);
                            p = p * p;
                            isGiveUp = Math.random() > p ? true : false;
                            if (hourConvert >= maxConvert) isGiveUp = true;
                        }
                    }
                    if(Math.random() < 0.0005)
                        logger.info("pacing advertId:{} convert:{} giveup:{} speed:{}", adInfo.advertId, hourConvert, isGiveUp, speed);
                }
                if(adInfo.advertId == 25504 || adInfo.advertId == 26537 || adInfo.advertId == 26466)
                {
                    double hourClk = adClkInfo.g1h;
                    double maxClk = 2500;
                    if(adInfo.advertId == 26537 || adInfo.advertId == 26466)
                    {
                        maxClk = 500;
                    }
                    Calendar calendar = Calendar.getInstance();
                    int minutes = calendar.get(Calendar.MINUTE);
                    double speed = 0;
                    if(minutes > 0)
                    {
                        speed = hourClk / minutes;
                        if(speed > 0.001) {
                            double p = (maxClk *0.7) / (speed * 60);
                            p = p * p;
                            isGiveUp = Math.random() > p ? true : false;
                            if (hourClk >= maxClk) isGiveUp = true;
                        }
                    }
                    if(Math.random() < 0.0005)
                        logger.info("pacing advertId:{} click:{} giveup:{} speed:{}", adInfo.advertId, hourClk, isGiveUp, speed);
                }
                if(adInfo.advertId == 25887)
                {
                    double hourClk = adClkInfo.g1h;
                    double maxClk = 260;
                    Calendar calendar = Calendar.getInstance();
                    int minutes = calendar.get(Calendar.MINUTE);
                    double speed = 0;
                    if(minutes > 0)
                    {
                        speed = hourClk / minutes;
                        if(speed > 0.001) {
                            double p = (maxClk *0.7) / (speed * 60);
                            p = p * p;
                            isGiveUp = Math.random() > p ? true : false;
                            if (hourClk >= maxClk) isGiveUp = true;
                        }
                    }
                    if(Math.random() < 0.0005)
                        logger.info("pacing advertId:{} click:{} giveup:{} speed:{}", adInfo.advertId, hourClk, isGiveUp, speed);
                }

                break;
            case automatch:
                boolean isTrace = isTrace(cvrInfo);
                isGiveUp = true;

                //开关
                if(adInfo.autoMatchInfo.enable != true)
                {
                    isGiveUp = true;
                    break;
                }

                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                long time = (long) Math.pow(2,hour);
                Long period = adInfo.autoMatchInfo.enablePeriod;

                if(period != null) {
                    if((time & period) == 0)
                    {
                        isGiveUp = true;
                        break;
                    }
                }

                //兴趣包加成
                double ratio = 1;
                if((adInfo.type.packageType & 2)>0) //转化有兴趣
                {
                    ratio = ratio * 0.8;
                }
                if((adInfo.type.packageType & 1)>0)
                {
                    ratio = ratio * 1.2;
                }

                double dayConvert = orientClkInfo.g1d * cvrInfo.orientCvrInfo.g1d;
                double convertCost = adInfo.autoMatchInfo.convertCost != null ? adInfo.autoMatchInfo.convertCost : 10000000;

                double realConvertCost = dayConvert > 0 ? adInfo.orientCost / dayConvert : adInfo.orientCost;
                if(adInfo.type.chargeType == 2 && dayConvert > 1)
                {
                    convertCost = Math.min(adInfo.target,convertCost);
                    double fac = convertCost > 0 ? realConvertCost / convertCost : 1;
                    if(fac > 1)
                        ratio = ratio * fac;
                }
                else if(adInfo.type.chargeType == 1 && dayConvert > 1)
                {
                    double fac = convertCost > 0 ? realConvertCost / convertCost : 1;
                    if(fac > 1)
                        ratio = ratio * fac;
                }

                if(isTrace)
                {
                    //流量采买
                    if(cvrInfo.cvr > 1.3 * ratio * cvrInfo.orientCvrInfo.g1d && dayConvert > 3)
                    {
                        if(adInfo.type.chargeType == 2 && adInfo.factor > 0.65)
                        {
                            isGiveUp = false;
                            tag = 2;
                        }
                        if(adInfo.type.chargeType == 1)
                        {
                            isGiveUp = false;
                            tag = 2;
                        }
                    }
                    else if(adInfo.type.chargeType == 1 && cvrInfo.cvr < 0.5 * ratio * cvrInfo.orientCvrInfo.g1d && dayConvert > 3) //流量放弃
                    {
                        isGiveUp = true;
                    }

                    //黑名单
                    if(adInfo.quailityLevel >=1 && adInfo.quailityLevel <=3)
                    {
                        isGiveUp = true;
                    }

                    //白名单
                    if(adInfo.quailityLevel == 8)
                    {
                        //isGiveUp = false;
                    }

                    //熔断
                    if(adInfo.type.chargeType == 1) //cpc_cvr
                    {
                        if(realConvertCost > 1.3 * convertCost && adInfo.orientCost > 5 * convertCost) {
                            //adInfo.autoMatchInfo.enable = false;
                            logger.info("automatch fusing advertId:{} orientId:{} appid:{} giveup:{} convert:{} fee1d:{} clk1d:{} fee:{} cvr:{} gcvr:{} ccvr:{} cgcvr:{} pack:{} quality:{}", adInfo.advertId, adInfo.orientId, adInfo.appId, isGiveUp, dayConvert, fee1d, clk1d, adInfo.fee,cvrInfo.cvr,cvrInfo.orientCvrInfo.g1d,cvrInfo.competerCvrInfo.app1d,cvrInfo.competerCvrInfo.g1d,adInfo.type.packageType, adInfo.quailityLevel);
                            isGiveUp = true;
                        }
                    }
                    else //ocpc
                    {
                        convertCost = Math.min(adInfo.target,convertCost);
                        if(realConvertCost > 1.3 * convertCost  && adInfo.orientCost > 5 * convertCost) {
                            adInfo.autoMatchInfo.enable = false;
                            logger.info("automatch fusing advertId:{} orientId:{} appid:{} giveup:{} convert:{} fee1d:{} clk1d:{} fee:{} cvr:{} gcvr:{} ccvr:{} cgcvr:{} pack:{} quality:{}", adInfo.advertId, adInfo.orientId, adInfo.appId, isGiveUp, dayConvert, fee1d, clk1d, adInfo.fee,cvrInfo.cvr,cvrInfo.orientCvrInfo.g1d,cvrInfo.competerCvrInfo.app1d,cvrInfo.competerCvrInfo.g1d,adInfo.type.packageType,adInfo.quailityLevel);
                            isGiveUp = true;
                        }
                    }
                    if(adInfo.quailityLevel == 16)
                    {
                        adInfo.autoMatchInfo.enable = false;
                        logger.info("automatch fusing advertId:{} orientId:{} appid:{} giveup:{} convert:{} fee1d:{} clk1d:{} fee:{} cvr:{} gcvr:{} ccvr:{} cgcvr:{} pack:{} quality:{}", adInfo.advertId, adInfo.orientId, adInfo.appId, isGiveUp, dayConvert, fee1d, clk1d, adInfo.fee,cvrInfo.cvr,cvrInfo.orientCvrInfo.g1d,cvrInfo.competerCvrInfo.app1d,cvrInfo.competerCvrInfo.g1d,adInfo.type.packageType,adInfo.quailityLevel);
                    }
                }
                else //cpc_no_cvr
                {
                    if(cvrInfo.competerCvrInfo.app1d > 1.5  * cvrInfo.competerCvrInfo.g1d && adInfo.orientCost > convertCost)
                    {
                        tag = 2;
                        isGiveUp = false;
                    }
                }

                //log_info
                if(!isGiveUp)
                {
                    if(Math.random() < 0.01)
                        logger.info("automatch advertId:{} orientId:{} appid:{} giveup:{} convert:{} fee1d:{} clk1d:{} fee:{} cvr:{} gcvr:{} ccvr:{} cgcvr:{} pack:{} quality:{}", adInfo.advertId, adInfo.orientId, adInfo.appId, isGiveUp, dayConvert, fee1d, clk1d, adInfo.fee,cvrInfo.cvr,cvrInfo.orientCvrInfo.g1d,cvrInfo.competerCvrInfo.app1d,cvrInfo.competerCvrInfo.g1d,adInfo.type.packageType,adInfo.quailityLevel);
                }

                break;
        }

        AutoMatchInfo.EffectLog effectLog = adInfo.autoMatchInfo.new EffectLog();
        effectLog.tag = tag;
        adInfo.autoMatchInfo.effectLog = effectLog;
        return isGiveUp;
    }

    /**
     * 广告是否有转化埋点
     * */
    public boolean isTrace(CvrInfo cvrInfo)
    {
        return  cvrInfo.adCvrInfo.g7d > 0 ? true : false;
    }


    public double wilsonRoofLeft(Double ratio, Double num)
    {
        if(ratio == null || num == null)
            return 0;
        long fenzi = (long) (ratio * num);
        long fenmu = num.longValue();
        WilsonPair pair = WilsonInterval.wilsonCalc(fenzi, fenmu);
        return pair.upperBound;
    }

    public double wilsonRoofRight(Double num, Double ratio)
    {
        if(ratio == null || num == null)
            return 0;
        if(ratio == 0)
        {
            ratio = 0.00000001;
        }
        long fenmu = (long) (num/ratio);
        long fenzi = num.longValue();
        WilsonPair pair = WilsonInterval.wilsonCalc(fenzi, fenmu);
        return pair.upperBound;
    }

    public double wilsonBottom(Long fenzi, Long fenmu)
    {
        if(fenzi == null || fenmu == null)
            return 0;
        WilsonPair pair = WilsonInterval.wilsonCalc(fenzi.longValue(), fenmu.longValue());
        return pair.lowerBound;
    }

    public double competence(CvrInfo cvrInfo, CtrInfo ctrInfo, StatInfo clkInfo, TimeInfo timeInfo)
    {
        double app7dCvr = wilsonRoofLeft(cvrInfo.adCvrInfo.app7d,clkInfo.app7d);
        double app1dCvr = wilsonRoofLeft(cvrInfo.adCvrInfo.app1d,clkInfo.app1d);
        double app1hCvr = wilsonRoofLeft(cvrInfo.adCvrInfo.app1h,clkInfo.app1h);
        double global7dCvr = wilsonRoofLeft(cvrInfo.adCvrInfo.g7d,clkInfo.g7d);
        double global1dCvr = wilsonRoofLeft(cvrInfo.adCvrInfo.g1d,clkInfo.g1d);
        double global1hCvr = wilsonRoofLeft(cvrInfo.adCvrInfo.g1h,clkInfo.g1h);
        double cvr = cvrInfo.cvr;
        double coefApp = 0.7 * Math.ceil(app7dCvr) + 0.21 * Math.ceil(app1dCvr) + 0.09 * Math.ceil(app1hCvr);
        double coefGlobal = 0.7 * Math.ceil(global7dCvr) + 0.21 * Math.ceil(global1dCvr) + 0.09 * Math.ceil(global1hCvr);
        double competenceApp =  coefApp > 0 ? coefApp * cvr / (0.7 * app7dCvr + 0.21 * app1dCvr + 0.09 * app1hCvr) : 1;
        double competenceGlobal = coefGlobal > 0 ? coefGlobal * cvr / (0.7 * global7dCvr + 0.21 * global1dCvr + 0.09 * global1hCvr) : 1;

        //System.out.println(competenceGlobal+"\t"+cvrInfo.cvrInfo.app1h+"\t"+app1hCvr);
        if((competenceGlobal < 0.3) || (competenceApp < 0.7 && competenceGlobal < 0.5))
        {
            return competenceGlobal;
        }

        return 1;
    }

    public double risk(CvrInfo cvrInfo, CtrInfo ctrInfo, StatInfo clkInfo, TimeInfo timeInfo)
    {
        double risk = 0;
        double arpuRisk;
        double costRisk;
        double app7dCtr = wilsonRoofRight(clkInfo.app7d,ctrInfo.adCtrInfo.app7d);
        double app1dCtr = wilsonRoofRight(clkInfo.app1d,ctrInfo.adCtrInfo.app1d);
        double app1hCtr = wilsonRoofRight(clkInfo.app1h,ctrInfo.adCtrInfo.app1h);
        double global7dCtr = wilsonRoofRight(clkInfo.g7d,ctrInfo.adCtrInfo.g7d);
        double global1dCtr = wilsonRoofRight(clkInfo.g1d,ctrInfo.adCtrInfo.g1d);
        double global1hCtr = wilsonRoofRight(clkInfo.g1h,ctrInfo.adCtrInfo.g1h);
        double ctr = ctrInfo.ctr;
        double coefApp = 0.7 * app7dCtr > 0 ? 1 : 0 + 0.21 * app1dCtr > 0 ? 1 : 0 + 0.09 * app1hCtr > 0 ? 1 : 0;
        double coefGlobal = 0.7 * global7dCtr > 0 ? 1 : 0 + 0.21 * global1dCtr > 0 ? 1 : 0 + 0.09 * global1hCtr > 0 ? 1 : 0;
        double competenceApp =  coefApp * ctr / (0.7 * app7dCtr + 0.21 * app1dCtr + 0.09 * app1hCtr);
        double competenceGlobal = coefGlobal * ctr / (0.7 * global7dCtr + 0.21 * global1dCtr + 0.09 * global1hCtr);
        if(competenceGlobal > 1)
        {
            risk = 1;
        }

        double budget = timeInfo.packageBudget;
        if(budget <= 0)
        {
            risk = 1;
        }

        return risk;
    }

    public double priceScore(int type , Long fee, Double factor, StatInfo clkInfo, TimeInfo timeInfo)
    {
        double confidence = 0;

        double fee1d = 0, clk1d = 0;
        for(double fee1h : timeInfo.hourFee)
        {
            if(fee1h != -1) fee1d += fee1h;
        }
        for(double clk1h : timeInfo.hourClk)
        {
            if(clk1d != -1) clk1d += clk1h;
        }
        double cpc = clk1d > 0 ? fee1d / clk1d : 50;

        if(cpc >= 20) {
            double feeOri = factor != 0 ? fee / factor : fee;
            if(fee <= 10)
            {
                return 0.1 * fee / 10;
            }
            else if((fee > 10 && fee<=15) && feeOri < 20)
            {
                return 0.5;
            }
        }

        return 1;
    }
}

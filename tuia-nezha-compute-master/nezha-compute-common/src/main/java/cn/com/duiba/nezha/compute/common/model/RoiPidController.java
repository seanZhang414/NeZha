package cn.com.duiba.nezha.compute.common.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;


/**
 * Created by jiali on 2017/6/7.
 */
public class RoiPidController {

    private static final Logger logger = LoggerFactory.getLogger(RoiPidController.class);

    private double P = 0;
    private double I = 0;
    private double D = 0;
    private double F = 0;

    private boolean firstRun=true;
    private double errorSum=0;
    private double lastOutput=0;
    private double lastActual=0;
    private double lastFactor=0;

    private double maxIOutput=0;
    private double maxError=0;
    private double maxOutput=0;
    private double minOutput=0;

    private double outputRampRate=0;
    private double outputFilter=0;

    public enum LifeStatus{
        NORMAL,DIE
    }

    public enum RunStatus{
        COLDBOOT,CHANGING,STAMBLE
    }

    static class Constant{

        static double DEFAULT_FACTOR = 1.0;
        static double THRESHOLD = 20;

        //控制器参数
        static double DEFAULT_P = 0.5;
        static double DEFAULT_I = 0.01;
        static double DEFAULT_D = 0.5;
    }

    public RoiPidController(){
        P = Constant.DEFAULT_P;
        I = Constant.DEFAULT_I;
        D = Constant.DEFAULT_D;
        checkSigns();
    }

    private double getInitFactor(List<StatInfo> infos, double targetCpa)
    {
        double initFactor = Constant.DEFAULT_FACTOR;
        for(StatInfo info : infos)
        {
            if(info.id.contains("DEFAULT"))
            {
                double actual = info.sumConv > 0 ? info.sumFee / info.sumConv : targetCpa;

                initFactor = targetCpa / actual;

                if(info.sumConv >= 3 && info.sumConv < 5)
                {
                    initFactor = initFactor > 1.2 ? 1.2 : initFactor;
                    initFactor = initFactor < 0.8 ? 0.8 : initFactor;
                }
                else if(info.sumConv >= 5 && info.sumConv < 10)
                {
                    if(initFactor < 1)
                    {
                        initFactor = Math.pow(initFactor, 1.5);
                    }
                    initFactor = initFactor > 1.5 ? 1.5 : initFactor;
                    initFactor = initFactor < 0.7 ? 0.7 : initFactor;
                }
                else if(info.sumConv >= 10)
                {
                    if(initFactor < 1)
                    {
                        initFactor = Math.pow(initFactor, 2);
                    }
                    initFactor = initFactor > 2 ? 2 : initFactor;
                    initFactor = initFactor < 0.5 ? 0.5 : initFactor;
                }
                else
                {
                    if(info.sumFee < 5 * targetCpa)
                        initFactor = 1;
                    else {
                        initFactor = targetCpa /info.sumFee;
                        initFactor = initFactor > 1.5 ? 1.5 : initFactor;
                        initFactor = initFactor < 0.5 ? 0.5 : initFactor;
                    }
                }

                break;
            }
        }
        return initFactor;
    }

    public List<StatInfo> getPriceFactor(List<StatInfo> infos, double targetCpa, double budget)
    {
        List<StatInfo> newInfos = new ArrayList<>();

        double initFactor = getInitFactor(infos,targetCpa);

        for(StatInfo info : infos)
        {
            if(info.sumClick < Constant.THRESHOLD)
                continue;

            if(info.id.contains("ACTIVITY"))
            {
                info.factor = 1;
                info.lastSumFee = info.sumFee;
                info.lastSumConv = info.sumConv;
                newInfos.add(info);
                continue;
            }

            if(!checkParam(info, targetCpa) || !checkStart(info, targetCpa)) {
                continue;
            }

            if(info.id.contains("APP") || info.id.contains("SLOT"))
            {
                info.factor = getOnePriceFactor(info, targetCpa, budget, initFactor);
            }
            else if(info.id.contains("DEFAULT"))
            {
                double lastfactor = info.factor;
                if(info.lastSumFee == 0) {
                    lastfactor = 1;
                }
                info.factor = Math.max(lastfactor * 0.8, initFactor);
                info.factor = Math.min(lastfactor * 1.2, initFactor);
            }


            //update
            info.lastSumFee = info.sumFee;
            info.lastSumConv = info.sumConv;
            newInfos.add(info);

        }
        return newInfos;
    }

    private double init(StatInfo info, double targetCpa, double budget, double initFactor) {

        lastFactor = info.factor;

        //1、start
        if (info.lastSumFee == 0 && Math.abs(info.factor - 1) < 0.00001) //from default
        {
            info.factor = initFactor;
        } else if (info.lastSumFee == 0) // from yesterday
        {
            info.factor = info.factor < initFactor * 0.6 ? initFactor * 0.6 : info.factor;
            info.factor = info.factor > initFactor * 1.4 ? initFactor * 1.4 : info.factor;
            if(info.factor > 1 && initFactor < 1)
                info.factor = 1;
        }

        double beforeFactor = info.factor;
        info.factor = smoothInitWith7d(info, targetCpa, budget, info.factor);
        if(Math.abs(beforeFactor - info.factor)>0.1)
            logger.info("pid controller smooth Id:{} beforeFactor:{} afterFactor:{} ", info.id, beforeFactor ,info.factor);

        //2、controller
        if (info.sumConv >= 5) {
            double actual = info.sumFee / info.sumConv;
            setOutputLimits(targetCpa);
            lastOutput = info.lastSumConv > 0 ? lastFactor * targetCpa - 1 : 0; //近似算法
            lastActual = info.lastSumConv > 0 ? info.lastSumFee / info.lastSumConv : actual;
            maxError = 100;
            errorSum = constrain(info.lastSumConv * targetCpa - info.lastSumFee, -maxError, maxError);//近似算法

            double output = getOutput(actual, targetCpa);
            info.factor = adjust(output,info.factor,lastFactor,targetCpa,0.9,1.1);

            //3、smooth
            double rFactor = targetCpa / actual;
            info.factor = output < 0 && info.factor > rFactor ? rFactor : info.factor;
            info.factor = output > 0 && info.factor < rFactor ? rFactor : info.factor;
            info.factor = output < 0 ? Math.max(info.factor, lastFactor * 0.8) : Math.min(info.factor, lastFactor * 1.4);
        }

        return info.factor;
    }

    private double boot(StatInfo info, double targetCpa, double budget, double initFactor)
    {
        lastFactor = info.factor;

        if(info.sumConv > 0) {
            double actual = info.sumFee / info.sumConv;
            setOutputLimits(targetCpa);

            lastOutput = info.lastSumConv > 0 ? lastFactor * targetCpa - 1 : 0; //近似算法
            lastActual = info.lastSumConv > 0 ? info.lastSumFee / info.lastSumConv : actual;
            maxError = 100;
            errorSum = constrain(info.lastSumConv * targetCpa - info.lastSumFee, -maxError, maxError);//近似算法

            double output = getOutput(actual, targetCpa);
            info.factor = adjust(output, info.factor, lastFactor, targetCpa, 0.9, 1.1);

        }
        else if(info.sumFee > 2 * targetCpa)//没有转化
        {
           info.factor = Math.min(initFactor * 0.9,info.factor);
        }
        else
        {
            info.factor = Math.min(initFactor,info.factor);
        }

        info.factor = Math.max(info.factor, initFactor * 0.5);
        info.factor = Math.min(info.factor, initFactor * 1.4);

        return  info.factor;
    }

    private double run(StatInfo info, double targetCpa, double budget, double initFactor)
    {
        lastFactor = info.factor;
        //1、controller
        double actual = info.sumConv > 0 ? info.sumFee / info.sumConv : info.sumFee * 2;
        setOutputLimits(targetCpa);

        lastOutput = info.lastSumConv > 0 ? lastFactor * targetCpa - 1 : 0; //近似算法
        lastActual = info.lastSumConv > 0 ? info.lastSumFee / info.lastSumConv : actual;
        maxError = 100;
        errorSum = constrain(info.lastSumConv * targetCpa - info.lastSumFee,-maxError,maxError);//近似算法

        double output = getOutput(actual, targetCpa);
        if(info.factor > 0.6 && info.factor < 1)
            info.factor = adjust(output,info.factor,lastFactor,targetCpa,0.8,1.1);
        else
            info.factor = adjust(output,info.factor,lastFactor,targetCpa,0.9,1.1);

        //2、smooth
        double rFactor = targetCpa / actual;
        if(targetCpa >= 3000 && info.sumFee < 5 * targetCpa) {
            info.factor = Math.max(info.factor, initFactor * 0.5);
        }

        info.factor = Math.max(info.factor, initFactor * 0.2);


        info.factor = Math.min(info.factor, initFactor * 2);
        if(info.sumConv >= 5 && info.sumConv < 10) {
            info.factor = output < 0 && info.factor > rFactor ? rFactor : info.factor;
            info.factor = output > 0 && info.factor < rFactor ? rFactor : info.factor;
            info.factor = output < 0 ? Math.max(info.factor, lastFactor * 0.85) : Math.min(info.factor, lastFactor * 1.3);
        }
        else if(info.sumConv >= 10) {
            info.factor = output < 0 && info.factor > rFactor ? rFactor : info.factor;
            info.factor = output > 0 && info.factor < rFactor ? rFactor : info.factor;
            info.factor = output < 0 ? Math.max(info.factor, lastFactor * 0.7) : Math.min(info.factor, lastFactor * 1.4);
        }
        //info.factor = smoothWith7d(info, targetCpa, budget, info.factor);
        return  info.factor;
    }



    public enum InitStatus {
        low, high, contradict, unkown;
    }


    private  double smoothInitWith7d(StatInfo info,double targetCpa, double budget, double initFactor)
    {
        class LocalConstant{
            double minConfi = 0.8, minCpcGap = 0.3, defaultFactor = 1.0;
        }
        LocalConstant constant = new LocalConstant();

        double cpa7d = info.conv7d > 0 ? info.fee7d / info.conv7d : -1;
        double cpa1d = info.lastSumConv > 0 ? info.lastSumFee / info.lastSumConv : -1;
        double cpc1d = info.sumClick > 0 ? (info.sumFee - info.lastSumFee) / info.sumClick : -1;
        double cpc7d = info.click7d > 0 ? info.fee7d / info.click7d : -1;
        double confi7d = getConfidence(info.fee7d / targetCpa,10);
        double confi1d = getConfidence(info.lastSumConv,10);
        double bias7d =  targetCpa / cpa7d;
        double bias1d =  targetCpa / cpa1d;

        InitStatus stat = InitStatus.unkown;

        if(cpa7d == -1 || cpa1d == -1 || cpc1d == -1 || cpc7d == -1)
            return initFactor;

        if(Math.abs(cpc1d - cpc7d) < cpc1d * constant.minCpcGap && confi7d > constant.minConfi)
        {
            if(bias7d < constant.defaultFactor && initFactor < constant.defaultFactor)
                stat = InitStatus.low;
            else if(bias7d > constant.defaultFactor && initFactor > constant.defaultFactor)
                stat = InitStatus.high;
            else
                stat = InitStatus.contradict;
        }

        double newInitFactor = initFactor;
        switch (stat)
        {
            case low:
                newInitFactor = Math.min(bias7d, initFactor); break;
            case high:
                newInitFactor = Math.max(bias7d, initFactor); break;
            case contradict:
                newInitFactor = (1 - confi1d) * bias7d + confi1d * initFactor; break;
            default:
                newInitFactor = initFactor; break;
        }

        return  newInitFactor;
    }

    private  double smoothWith7d(StatInfo info,double targetCpa, double budget, double factor)
    {
        class LocalConstant{
            double minConfi = 0.8, minCpcGap = 0.1, defaultFactor = 1.0;
        }
        LocalConstant constant = new LocalConstant();
        double cpa7d = info.conv7d > 0 ? info.fee7d / info.conv7d : -1;
        double cpa1d = info.lastSumConv > 0 ? info.lastSumFee / info.lastSumConv : -1;
        double cpc1d = info.sumClick > 0 ? (info.sumFee - info.lastSumFee) / info.sumClick : -1;
        double cpc7d = info.click7d > 0 ? info.fee7d / info.click7d : -1;
        double confi7d = getConfidence(info.fee7d / targetCpa,50);
        double confi1d = getConfidence(info.lastSumConv,20);
        double bias7d = cpa7d / targetCpa;
        double bias1d = cpa1d / targetCpa;

        if(cpa7d == -1 || cpa1d == -1 || cpc1d == -1 || cpc7d == -1)
            return factor;


        InitStatus stat = InitStatus.unkown;
        if(Math.abs(cpc1d - cpc7d) < constant.minCpcGap && confi7d > constant.minConfi && confi1d > constant.minConfi)
        {
            if(bias7d < constant.defaultFactor && factor < constant.defaultFactor)
                stat = InitStatus.low;
            else if(bias7d > constant.defaultFactor && factor > constant.defaultFactor)
                stat = InitStatus.high;
            else
                stat = InitStatus.contradict;
        }

        switch (stat)
        {
            case low:
                factor = Math.min(bias7d, factor); break;
            case high:
                factor = Math.max(bias7d, factor); break;
            case contradict:
                factor = confi7d * bias7d + (1-confi7d) * factor; break;
            default:
                factor = factor; break;
        }

        return  factor;
    }

    private double getConfidence(double cur, double max)
    {
        return cur < max ? cur / max : 1;
    }

    private double getOnePriceFactor(StatInfo info, double targetCpa, double budget, double initFactor)
    {
        //1、start
        reset();
        if(initFactor < 0.9)
        {
            targetCpa = targetCpa * 0.85;
        }
        if(info.lastSumFee ==0) {
            return init(info, targetCpa, budget, initFactor);
        }
        if(checkColdboot(info, targetCpa))
        {
            return boot(info, targetCpa, budget, initFactor);
        }
        return run(info, targetCpa, budget, initFactor);
    }


    private double adjust(double output, double factor, double lastFactor, double targetCpa, double negtive, double positive)
    {
        if(output < 0) {
            factor = lastFactor * Math.max(1 + output / targetCpa, negtive);
        }
        else {
            factor = lastFactor * Math.min(1 + output / targetCpa, positive);
        }
        return  factor;
    }


    private boolean checkParam(StatInfo info, double targetCpa)
    {
        if(info.sumFee <= 0 || info.sumConv < 0 || info.lastSumFee < 0 || info.lastSumConv <0 || info.factor <= 0 || targetCpa <= 0)
        {
            return false;
        }
        return true;
    }

    private boolean checkStart(StatInfo info, double targetCpa)
    {
        if(info.sumFee <= 3000 && info.sumConv < 1)
            return false;
        return true;
    }


    private boolean checkColdboot(StatInfo info, double targetCpa)
    {
        if(info.sumConv < 5 && info.sumFee/targetCpa < 5)
            return true;
        return false;
    }

    private RoiPidController(double p, double i, double d){
        P=p; I=i; D=d;
        checkSigns();
    }

    private RoiPidController(double p, double i, double d, double f){
        P=p; I=i; D=d; F=f;
        checkSigns();
    }

    private void setI(double i){  /*I翻倍，累计错误减半*/
        if(I != 0){
            errorSum = errorSum * I / i;
        }
        if(maxIOutput!=0){
            maxError = maxIOutput / i;
        }
        I = i;
        checkSigns();
    }


    private void setPID(double p, double i, double d){
        P=p;D=d;
        setI(i);    //积分项具有额外的计算，需要专门set
        checkSigns();
    }
    private void setPID(double p, double i, double d,double f){
        P=p;D=d;F=f;
        setI(i);
        checkSigns();
    }

    private void setMaxIOutput(double maximum){
        maxIOutput = maximum;
        if(I!=0){
            maxError = maxIOutput/I;
        }
    }

    private void setOutputLimits(double output){
        setOutputLimits(-output,output);
    }

    private void setOutputLimits(double minimum,double maximum){
        if(maximum < minimum)return;
        maxOutput=maximum;
        minOutput=minimum;

        // Ensure the bounds of the I term are within the bounds of the allowable output swing
        if(maxIOutput==0 || maxIOutput>(maximum-minimum) ){
            setMaxIOutput(maximum-minimum);
        }
    }

    private double getOutput(double actual, double setpoint){
        double output;
        double Poutput;
        double Ioutput;
        double Doutput;
        double Foutput;

        double error = setpoint - actual;

        Foutput = F * setpoint;
        Poutput = P * error;

        if(firstRun){
            lastActual = actual;
            lastOutput = Poutput + Foutput;
            firstRun = false;
        }

        // D：负数，减缓控制系统，防止调整信号突变与超调
        Doutput = - D * (actual - lastActual);
        lastActual = actual;

        // I: 积分项
        // 1. maxIoutput 限制了I项的权重。
        // 2. prevent windup by not increasing errorSum if we're already running against our max Ioutput
        // 3. prevent windup by not increasing errorSum if output is output= maxOutput
        Ioutput = I * errorSum;
        if(maxIOutput != 0){
            Ioutput = constrain(Ioutput, -maxIOutput, maxIOutput);
        }

        output = Foutput + Poutput + Ioutput + Doutput;

        // error平滑
        if(minOutput != maxOutput && !bounded(output, minOutput,maxOutput) ){ //error超出限制重置，让系统更平滑。P充分大，I开始作用。
            errorSum = error;
        }
        else if(outputRampRate!=0 && !bounded(output, lastOutput-outputRampRate,lastOutput+outputRampRate) ){
            errorSum = error;
        }
        else if(maxIOutput!=0){
            errorSum = constrain(errorSum + error,-maxError,maxError);
        }
        else{
            errorSum += error;
        }

        // 控制信号平滑
        if(outputRampRate!=0){
            output = constrain(output, lastOutput - outputRampRate,lastOutput + outputRampRate);
        }
        if(minOutput!=maxOutput){
            output = constrain(output, minOutput,maxOutput);
        }
        if(outputFilter!=0){
            output = lastOutput*outputFilter+output*(1-outputFilter);
        }

        lastOutput = output;
        return output;
    }

    private void reset(){
        firstRun=true;
        errorSum=0;
    }

    private void setOutputRampRate(double rate){
        outputRampRate=rate;
    }


    private void setOutputFilter(double strength){
        if(strength==0 || bounded(strength,0,1)){
            outputFilter=strength;
        }
    }

    private double constrain(double value, double min, double max){
        if(value > max){ return max;}
        if(value < min){ return min;}
        return value;
    }

    private boolean bounded(double value, double min, double max){
        return (min<value) && (value < max);
    }

    private void checkSigns(){
        if(P<0) P *= -1;
        if(I<0) I *= -1;
        if(D<0) D *= -1;
        if(F<0) F *= -1;
    }

    public static void main(String[] args) {
        String test = "11789_0_ACTIVITY_23032_1_1092";
       // System.out.print(test.split("_")[3]);


        RoiPidController PID = new RoiPidController();

        StatInfo s1 = new StatInfo();
        s1.id = "11789_0_APP_2303";
        s1.sumFee = 3000;
        s1.sumConv = 1;
        s1.lastSumConv = 0;
        s1.lastSumFee = 0;
        s1.sumClick = 100;
        s1.factor = 0.8;
        s1.parentFactor = 1;

        StatInfo s2 = new StatInfo();
        s2.id = "11789_0_ACTIVITY_23032_1_1092";
        s2.sumFee = 3000;
        s2.sumConv = 0;
        s2.lastSumConv = 0;
        s2.lastSumFee = 0;
        s2.sumClick = 100;
        s2.factor = 1;

        List<StatInfo> infos = new ArrayList<>();
        infos.add(s1);
        infos.add(s2);

        double bidOld = 1500;
        double maxConv = 100;
        double targetCpa = 3000;
        double actualCpa = s1.sumFee / s1.sumConv;
        double budget = 30000;
        double bid = bidOld;

        //System.err.printf("Target\tActual\tFactor\tBid\tsumFee\tsumConv\tlsumFee\tlsumConv\n");

        for (int i = 0; i < 200; i++){

            //PID.getOnePriceFactor(s1,targetCpa,30000);
            double factor = 1;

            System.err.printf("%3.2f\t%3.2f\t%3.2f\t%3.2f\t%3.0f\t%3.0f\t%3.0f\t%3.0f\t%3.0f\n", targetCpa, actualCpa, s1.factor,s2.factor, bid, s1.sumFee, s1.sumConv, s1.lastSumFee,s1.lastSumConv);
            List<StatInfo> newinfos = PID.getPriceFactor(infos, targetCpa, budget);


            double random = 1;
            double random1 = Math.random();
            double random2 = Math.random();
            if(random1 > random2)
                random = 1 + random1;
            else
                random = 1 - random2;
            bid = bidOld * s1.factor ;

            bid = Math.max(bid,1);
            bid = Math.min(3000,bid);



            double conv = Math.min(Math.pow(1.5, bid/100), maxConv);
            s1.sumFee += bid * conv;
            s1.sumConv += conv;
            actualCpa =  s1.sumFee / s1.sumConv;
        }
    }


}


package cn.com.duiba.nezha.compute.common.model.pacing;

import java.util.ArrayList;
import java.util.List;

public class PacingTest {

    public static void main(String[] args){
        PacingTest t = new PacingTest();
        Pacing pacing = new Pacing();


        //点击单价（历史）
        List<Double> hourCtr = new ArrayList<Double>();
        hourCtr.add(0.1);
//        hourCtr.add(0.6);
//        hourCtr.add(0.6);
//        hourCtr.add(0.6);
//        hourCtr.add(0.6);
//        hourCtr.add(0.6);
//        hourCtr.add(0.6);
//        hourCtr.add(0.6);

        List<Double> hourCvr = new ArrayList<Double>();
        hourCvr.add(0.001);
//        hourCvr.add(0.5);
//        hourCvr.add(0.5);
//        hourCvr.add(0.5);
//        hourCvr.add(0.5);
//        hourCvr.add(0.5);
//        hourCvr.add(0.5);
//        hourCvr.add(0.5);

        List<Double> hourClk = new ArrayList<Double>();
        hourClk.add(208.0);//6.0
//        hourClk.add(400000.0);
//        hourClk.add(400000.0);
//        hourClk.add(400000.0);
//        hourClk.add(400000.0);
//        hourClk.add(400000.0);
//        hourClk.add(400000.0);
//        hourClk.add(400000.0);
        List<Double> hourFee = new ArrayList<Double>();
        hourFee.add(50500.0);//585.0
//        hourFee.add(1000.0);
//        hourFee.add(1000.0);
//        hourFee.add(1000.0);
//        hourFee.add(1000.0);
//        hourFee.add(1000.0);
//        hourFee.add(1000.0);
//        hourFee.add(1000.0);
        List<Double> hourExp = new ArrayList<Double>();//  /24
        hourExp.add(500.0);//13
//        hourExp.add(100.0);
//        hourExp.add(100.0);
//        hourExp.add(100.0);
//        hourExp.add(100.0);
//        hourExp.add(100.0);
//        hourExp.add(100.0);
//        hourExp.add(100.0);
        List<Double> hourBudgetFee = new ArrayList<Double>(); //    /24
        hourBudgetFee.add(4166.6);//4166.6
//        hourBudgetFee.add(100000.0);
//        hourBudgetFee.add(100000.0);
//        hourBudgetFee.add(100000.0);
//        hourBudgetFee.add(100000.0);
//        hourBudgetFee.add(100000.0);
//        hourBudgetFee.add(100000.0);
//        hourBudgetFee.add(100000.0);
        List<Double> hourBudgetExp = new ArrayList<Double>();   //  总预算／点击单价／ctr（app1d）
        hourBudgetExp.add(402.0);//402.0
//        hourBudgetExp.add(100000.0);
//        hourBudgetExp.add(100000.0);
//        hourBudgetExp.add(100000.0);
//        hourBudgetExp.add(100000.0);
//        hourBudgetExp.add(100000.0);
//        hourBudgetExp.add(100000.0);
//        hourBudgetExp.add(100000.0);


//        Pacing.StatInfo statInfo_ctr =  t.getStatInfo(pacing,  0.23378182851253321,0.26569928737923004,0.22443520933035535,0.32131689833048771,0.35787330734873402,0.3434350009140304);
//        Pacing.StatInfo statInfo_cvr =  t.getStatInfo(pacing,0.0090434438564635893,0.0084285135682462542,0.01873872464966125,0.02639351603365209,0.028998760783471279,0.027926762476092871);
        StatInfo statInfo_ctr =  t.getStatInfo(pacing,  0.23378182851253321,0.26569928737923004,0.22443520933035535,0.12131689833048771,0.15787330734873402,0.1434350009140304);
        StatInfo statInfo_cvr =  t.getStatInfo(pacing,0.0090434438564635893,0.0084285135682462542,0.001873872464966125,0.002639351603365209,0.0028998760783471279,0.027926762476092871);
        StatInfo statInfo_cli =  t.getStatInfo(pacing,141539,246900,716164,3148690,8043792,23363324);
        CtrInfo ctrInfo = t.getCtrInfo(pacing,statInfo_ctr,0.10);
        CvrInfo cvrInfo = t.getCvrInfo(pacing,statInfo_cvr,0.00006);
        TimeInfo timeInfo = t.getTimeInfo(pacing, hourCtr,  hourCvr,  hourClk,  hourFee,  hourExp, 1000,  hourBudgetFee,  hourBudgetExp);
        AdInfo adInfo = t.getAdInfo(pacing, true, 1,1,1,100,2);



        int i=1000;
        int tr=0;
        int fa=0;


        while(i>0){
            //fee很低放弃，小时纬度的实际出价，fee是本次出价，有些广告出价小于两毛，但实际出价高于两毛
            boolean bl = true;
            bl = pacing.pacing(adInfo,cvrInfo,ctrInfo,statInfo_cli,statInfo_cli,timeInfo);
            //System.out.println(bl);

            if(bl)tr++;
            else fa++;
            i--;

        }
        System.out.println("true:"+tr);
        System.out.println("false:"+fa);


    }

    public StatInfo getStatInfo(Pacing pacing,double app1h,double app1d,double app7d,double g1h,double g1d,double g7d){

        StatInfo statInfo = new StatInfo();
        statInfo.setApp1h(app1h);
        statInfo.setApp1d(app1d);
        statInfo.setApp7d(app7d);
        statInfo.setG1h(g1h);
        statInfo.setG1d(g1d);
        statInfo.setG7d(g7d);
        return statInfo;
    }

    public AdInfo getAdInfo(Pacing pacing,boolean isWeakFilted,long advertId,long appId,double factor,long fee, int ty){

        AdInfo adInfo = new AdInfo();
        adInfo.advertId = advertId;
        adInfo.appId = appId;
        adInfo.factor = factor;
        adInfo.fee = fee;
        AdInfo.Type type = adInfo.getType();
        type.packageType = 0b1000;
        type.chargeType = 2;
        type.pid = 4;
        adInfo.type = type;
        return adInfo;
    }

    public  CvrInfo  getCvrInfo(Pacing pacing,StatInfo cvrInfo,double cvr){
        CvrInfo cvrInfo1 = new CvrInfo();
        cvrInfo1.setCvr(cvr);
        cvrInfo1.setAdCvrInfo(cvrInfo);
        cvrInfo1.orientCvrInfo = cvrInfo;
        return cvrInfo1;
    }

    public  CtrInfo  getCtrInfo(Pacing pacing,StatInfo ctrInfo,double ctr){
        CtrInfo ctrInfo1 = new CtrInfo();
        ctrInfo1.setAdCtrInfo(ctrInfo);
        ctrInfo1.setCtr(ctr);
        ctrInfo1.orientCtrInfo = ctrInfo;
        return ctrInfo1;
    }

    public TimeInfo getTimeInfo(Pacing pacing, List<Double> hourCtr, List<Double> hourCvr, List<Double> hourClk, List<Double> hourFee, List<Double> hourExp, double packageBudget, List<Double> hourBudgetFee, List<Double> hourBudgetExp){
        TimeInfo timeInfo = new TimeInfo();
        timeInfo.setHourCtr(hourCtr);
        timeInfo.setHourCvr(hourCvr);
        timeInfo.setHourClk(hourClk);
        timeInfo.setHourFee(hourFee);
        timeInfo.setHourExp(hourExp);
        timeInfo.setPackageBudget(packageBudget);
        timeInfo.setHourBudgetFee(hourBudgetFee);
        timeInfo.setHourBudgetExp(hourBudgetExp);
        return timeInfo;
    }
}

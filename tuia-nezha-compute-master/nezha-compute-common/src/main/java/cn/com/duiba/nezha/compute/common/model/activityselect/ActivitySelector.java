package cn.com.duiba.nezha.compute.common.model.activityselect;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Queue;


public class ActivitySelector {

    private static final Logger logger = LoggerFactory.getLogger(ActivitySelector.class);

    static class Constant {
        static double MIN_REWARD = 0.1;
        static long DISCOUNT = 2;
        static  int MAX_HIS_VAL = 10000;
        static double DECAY = 0.99;  //100次以前的观察，无效
        static int SEARANK_TOPN = 30;
    }

    static class RankInfo {
        double grpm;
        double gexp;
        double hrpm;
        double hexp;
        double arpm;
        double aexp;
    }

    static class MatchInfo {
        double score;
        ActivityInfo act;
    }

    public static  int getCoef(double hisRequest, double request, double hisClick, double click)
    {
        int coef = 1;
        int i = 1,j=1;
        if(request < 0)
        {
            for(i = 1;i<144;i++)
            {
                request += hisRequest/144;
                if(request > 0)
                    break;
            }
        }
        if(click < 0)
        {
            for(j = 1;j<144;j++)
            {
                click += hisClick/144;
                if(click > 0)
                    break;
            }
        }
        coef = coef + Math.max(i,j);
        return coef;
    }

    public static Comparator<MatchInfo> iComparator = new Comparator<MatchInfo>() {

        @Override
        public int compare(MatchInfo c1, MatchInfo c2) {
            return (int) (c2.score - c1.score >= 0 ? 1 : -1);
        }
    };

    //recall
    public static List<ActivityInfo> match(List<ActivityInfo> activityInfos, int topn) {

        List<ActivityInfo> result = new ArrayList<>();

        Queue<MatchInfo> candis = new PriorityQueue<>(activityInfos.size(), iComparator);
        Queue<MatchInfo> slotCandis = new PriorityQueue<>(activityInfos.size(), iComparator);
        Queue<MatchInfo> appCandis = new PriorityQueue<>(activityInfos.size(), iComparator);
        Queue<MatchInfo> globalCandis = new PriorityQueue<>(activityInfos.size(), iComparator);

        for (ActivityInfo activity : activityInfos) {
            try {
                if (activity.source != 1)
                    continue;
                if(activity.valid == false)
                    continue;

                activity.hisClick.globalVal = activity.click.globalVal;
                activity.hisClick.appVal = activity.click.appVal;
                activity.hisClick.slotVal = activity.directClick.slotVal;
                activity.hisCost.globalVal = activity.cost.globalVal;
                activity.hisCost.appVal = activity.cost.appVal;
                activity.hisCost.slotVal = activity.directCost.slotVal;
                activity.hisRequest.globalVal = activity.request.globalVal;
                activity.hisRequest.appVal = activity.request.appVal;
                activity.hisRequest.slotVal = activity.directRequest.slotVal;

                //新活动试投
                int limit = topn;
                if (activity.hisRequest.globalVal < 100) {
                    //continue;
                    if(System.currentTimeMillis() - activity.createTime < 60 * 1000 * 60 * 24 * 3)
                    {
                        if (Math.random() < 0.0001) {
                            result.add(activity);
                            topn--;
                        }
                    }
                    else if (activityInfos.size() > limit) {
                        if (Math.random() < 0.00001) {
                            result.add(activity);
                            topn--;
                        }
                    }
                } else {
                    //计算matchscore
                    double slotScore = WilsonInterval.wilsonCalc((long) activity.hisClick.slotVal, (long) activity.hisRequest.slotVal * 3).lowerBound;
                    double globalScore = WilsonInterval.wilsonCalc((long) activity.hisClick.globalVal, (long) activity.hisRequest.globalVal * 3).lowerBound;
                    double appScore = WilsonInterval.wilsonCalc((long) activity.hisClick.appVal, (long) activity.hisRequest.appVal * 3).lowerBound;

                    double coef = 0, matchscore = 0;

                    double sconfidence = Math.min(activity.hisRequest.slotVal / 100, 1);
                    double aconfidence = Math.min(activity.hisRequest.appVal / 100, 1);
                    double gconfidence = Math.min(activity.hisRequest.globalVal / 1000, 1);

                    matchscore = sconfidence * slotScore
                            + (1 - sconfidence) * aconfidence * appScore * 0.9
                            + (1 - sconfidence - (1 - sconfidence) * aconfidence) * globalScore * Math.max(0.5, gconfidence);

                    MatchInfo info = new MatchInfo();
                    info.act = activity;
                    info.score = matchscore;
                    candis.add(info);

                    if (sconfidence > 0.99 || activity.hisClick.slotVal > 5) {
                        MatchInfo info2 = new MatchInfo();
                        info2.act = activity;
                        info2.score = activity.hisRequest.slotVal > 0 ? activity.hisCost.slotVal / activity.hisRequest.slotVal : 0;
                        slotCandis.add(info2);
                    }

                    if (aconfidence > 0.99 || activity.hisClick.slotVal > 5) {
                        MatchInfo info2 = new MatchInfo();
                        info2.act = activity;
                        info2.score = activity.hisRequest.appVal > 0 ? activity.hisCost.appVal / activity.hisRequest.appVal : 0;
                        appCandis.add(info2);
                    }
                }
            }catch (Exception e)
            {
                logger.error("error, act:{}", JSON.toJSONString(activity));
                logger.error(e.getMessage(), e);
            }
        }

        HashSet<Long> idset = new HashSet();

        int size1 = slotCandis.size();
        for (int i = 0; i < 10 && i < size1; i++) {
            ActivityInfo act =  slotCandis.poll().act;
            result.add(act);
            idset.add(act.activityId);
        }

        int size = candis.size();
        for (int i = 0; i < topn && i < size; i++) {
            ActivityInfo act =  candis.poll().act;
            if(slotCandis.contains(act.activityId))
                continue;
            result.add(act);
            idset.add(act.activityId);
        }

        if(result.size() < topn)
        {
            int count = topn;
            for (ActivityInfo activity : activityInfos){
                if (activity.source != 1)
                    continue;
                if(activity.valid == false)
                    continue;
                if(idset.contains(activity.activityId))
                    continue;
                idset.add(activity.activityId);
                activity.hisClick.globalVal = activity.click.globalVal;
                activity.hisClick.appVal = activity.click.appVal;
                activity.hisClick.slotVal = activity.directClick.slotVal;

                activity.hisCost.globalVal = activity.cost.globalVal;
                activity.hisCost.appVal = activity.cost.appVal;
                activity.hisCost.slotVal = activity.directCost.slotVal;

                activity.hisRequest.globalVal = activity.request.globalVal;
                activity.hisRequest.appVal = activity.request.appVal;
                activity.hisRequest.slotVal = activity.directRequest.slotVal;

                result.add(activity);
                count++;
                if(count >= topn)
                    break;
            }
        }

        return result;
    }

    //100 - 10 - 1
    public static ActivityInfo select(List<ActivityInfo> activityInfos) {


        //1、init
        ArrayList<Double> rewards = new ArrayList<>();
        ArrayList<Double> counts = new ArrayList<>();
        ArrayList<Double> alphas = new ArrayList<>();
        ArrayList<Double> betas = new ArrayList<>();

        List<ActivityInfo> candiList = new ArrayList<>();

        double decay = Constant.DECAY;

        int size = 0;

        HashMap<Long, RankInfo> mMap = new HashMap();
        double maxG = Constant.MIN_REWARD, maxH = Constant.MIN_REWARD, maxA = Constant.MIN_REWARD;
        ActivityInfo result = new ActivityInfo();

        try {

            //2、match
            List<ActivityInfo> matchActivityInfos = match(activityInfos, Constant.SEARANK_TOPN);
            size = matchActivityInfos.size();

            //3、rank
            //3.1 get info
            for (ActivityInfo act : matchActivityInfos) {
                //get global data
                RankInfo info = mMap.containsKey(act.activityId) ? mMap.get(act.activityId) : new RankInfo();
                double grpm = act.hisRequest.globalVal > 0 ? act.hisClick.globalVal / act.hisRequest.globalVal : 0;
                info.grpm = grpm;
                info.gexp = act.hisRequest.globalVal;
                maxG = Math.max(grpm, maxG);

                //System.out.println(act.hisClick.globalVal+" "+act.hisRequest.globalVal+" "+grpm);

                //get app data
                double arpm = act.hisRequest.appVal > 0 ? act.hisCost.appVal / act.hisRequest.appVal : 0;
                info.arpm = arpm;
                info.aexp = act.hisRequest.appVal;
                if (info.hexp > 50)
                    maxA = Math.max(arpm, maxA);

                //get slot data
                double hrpm = act.hisRequest.slotVal > 0 ? act.hisCost.slotVal / act.hisRequest.slotVal : 0;
                info.hrpm = hrpm;
                info.hexp = act.hisRequest.slotVal;
                if (info.hexp > 50)
                    maxH = Math.max(hrpm, maxH);

                //System.out.println(hrpm+"\t"+maxH);

                mMap.put(act.activityId, info);
            }

            for (ActivityInfo act : matchActivityInfos) {
                if (act.request.globalVal > 0) {
                    double reward = Constant.MIN_REWARD;
                    double sconfidence = Math.min(act.hisRequest.slotVal / 60, 1);
                    double aconfidence = Math.min(act.hisRequest.appVal / 60, 1);

                    reward = sconfidence * normlize(mMap.get(act.activityId).hrpm * 0.8, maxH, 0.8) +
                            (1 - sconfidence) * aconfidence * normlize(mMap.get(act.activityId).arpm * 0.7, maxA, 0.7) +
                            (1 - sconfidence - (1 - sconfidence) * aconfidence) * normlize(mMap.get(act.activityId).grpm * 0.5, maxG, 0.6);

                    reward = reward * reward;

                    reward = Math.max(reward, Constant.MIN_REWARD);

                    act.reward = act.reward * decay + reward;
                    act.count = act.count * decay + 1.0;

                    long oneday = 24 * 60 * 60 * 1000;
                    if ((sconfidence > 0.99 || System.currentTimeMillis() - act.updateTime > oneday))    //临时脏数据修复代码
                    {
                        if (act.reward / act.count < 0.6 * reward || act.reward / act.count > 1.6 * reward) {
                            act.reward = 10 * reward;
                            act.count = 10;
                        }
                    }

                    act.alpha = 1.5 + act.reward;
                    act.beta = 2.0 + (act.count - act.reward);
                }

                rewards.add(act.reward);
                counts.add(act.count);
                alphas.add(act.alpha);
                betas.add(act.beta);
                candiList.add(act);
            }

            //4、select

            int numMachines = candiList.size();

            result = candiList.get(selectMachine(alphas, betas, numMachines));
            //System.out.println(rewards.toString()+" "+alphas.toString()+" "+betas.toString()+" "+numMachines+" "+selectMachine(alphas, betas, numMachines));

            if(System.currentTimeMillis() - result.updateTime > 60 * 1000) //1分钟更新一次
            {
                result.isUpdate = true;
            }
            mMap.clear();

            //System.out.println(result.activityId);

            result.lastRequest.appVal = result.request.appVal;
            result.lastRequest.slotVal = result.request.slotVal;
            result.lastRequest.globalVal = result.request.globalVal;

            result.lastClick.appVal = result.click.appVal;
            result.lastClick.slotVal = result.click.slotVal;
            result.lastClick.globalVal = result.click.globalVal;

            result.lastCost.appVal = result.cost.appVal;
            result.lastCost.slotVal = result.cost.slotVal;
            result.lastCost.globalVal = result.cost.globalVal;
            //print(result);

            return result;
        }catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            logger.error("error, size:{},candi:{},list:{},", size, JSON.toJSONString(candiList), JSON.toJSONString(activityInfos));
        }
        return result;
    }

    public static  void print(ActivityInfo act)
    {
        System.out.println("-----------"+act.activityId+"-----------");
        System.out.println("request = "+act.request.slotVal+"\t"+act.request.appVal+"\t"+act.request.globalVal);
        System.out.println("click = "+act.click.slotVal+"\t"+act.click.appVal+"\t"+act.click.globalVal);
        System.out.println("lastRequest = "+act.lastRequest.slotVal+"\t"+act.lastRequest.appVal+"\t"+act.lastRequest.globalVal);
        System.out.println("lastClick = "+act.lastClick.slotVal+"\t"+act.lastClick.appVal+"\t"+act.lastClick.globalVal);
        System.out.println("hisRequest = "+act.hisRequest.slotVal+"\t"+act.hisRequest.appVal+"\t"+act.hisRequest.globalVal);
        System.out.println("hisClick = "+act.hisClick.slotVal+"\t"+act.hisClick.appVal+"\t"+act.hisClick.globalVal);
    }

    public static double normlize(double val, double max, double limit) {
        double norm = Math.min(val * limit / max, limit);
        return norm;
    }


    public ActivitySelector() { //下周

    }

    private static int selectMachine(List<Double> alphas, List<Double> betas, int numMachines) {
        int selectMachine = 0;
        double maxTheta = 0;
        for (int i = 0; i < numMachines; i++) {
            double theta = BetaDistribution.BetaDist(alphas.get(i), betas.get(i));
            if (theta > maxTheta) {
                maxTheta = theta;
                selectMachine = i;
            }
        }
        return selectMachine;
    }

    private double getCtr(double exp, double clk) {
        return exp > 0 ? clk / exp : 0;
    }

    private double sum(List<Long> list) {
        double sum = 0;
        for (Long val : list) {
            sum += val;
        }
        return sum;
    }

    public static double wilsonRoofLeft(Double ratio, Double num) {
        if (ratio == null || num == null)
            return 0;
        long fenzi = (long) (ratio * num);
        long fenmu = num.longValue();
        WilsonPair pair = WilsonInterval.wilsonCalc(fenzi, fenmu);
        return pair.upperBound;
    }

    public double wilsonRoofRight(Double num, Double ratio) {
        if (ratio == null || num == null)
            return 0;
        if (ratio == 0) {
            ratio = 0.00000001;
        }
        long fenmu = (long) (num / ratio);
        long fenzi = num.longValue();
        WilsonPair pair = WilsonInterval.wilsonCalc(fenzi, fenmu);
        return pair.upperBound;
    }

    public double wilsonBottom(Long fenzi, Long fenmu) {
        if (fenzi == null || fenmu == null)
            return 0;
        WilsonPair pair = WilsonInterval.wilsonCalc(fenzi.longValue(), fenmu.longValue());
        return pair.lowerBound;
    }

    public static class SingletonHolder {
        private static final ActivitySelector instance = new ActivitySelector();
    }

    private static ActivitySelector getInstance() {
        return SingletonHolder.instance;
    }
}
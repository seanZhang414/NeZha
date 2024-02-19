package cn.com.duiba.nezha.compute.common.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.List;

import cn.com.duiba.nezha.compute.common.vo.GenericPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * Created by jiali on 2017/6/9.
 */

public class BayesianBandit {

    private static final Logger logger = LoggerFactory.getLogger(BayesianBandit.class);

    private ArrayList<Double> rewards;
    private ArrayList<Double> counts;
    private ArrayList<Double> alphas;
    private ArrayList<Double> betas;
    private int numMachines;
    private double decay = 1;

    static class Constant{
        static double RT_REWARD_WEIGHT = 0;
        static double MIN_REWARD = 0.01;
        static long MAX_CLICK = 300;
        static long DISCOUNT = 2;
        static double DECAY = 0.999;  //1000次以前的观察，无效
    }

    class Info{
        long id;
        double gctr;
        double gexp;
        double hctr;
        double hexp;
        double rctr;
        double rexp;
        MaterialInfo gMaterial;
    }

    public List<MaterialInfo> selectMaterial(List<MaterialInfo>  materialList,Long appid)
    {
        //1、init
        clear();
        this.decay = Constant.DECAY;
        HashMap<Long,Info> mMap = new HashMap();
        double maxG = 0.01, maxH = 0.01;

        //2、info
        for(MaterialInfo m : materialList)
        {
            double click = sum(m.click);
            double exposure = sum(m.exposure);
            double gclick = sum(m.globalClick);
            double gexposure = sum(m.globalExposure);

            //不发券不会更新
            if (m.lastClick > Constant.MAX_CLICK) {
                m.lastClick = m.lastClick / Constant.DISCOUNT;
                m.lastExposure = m.lastExposure / Constant.DISCOUNT;
            }
            m.lastClick = m.lastClick + getCtr(exposure, click);
            m.lastExposure = m.lastExposure + 1;
            m.globalLastClick = m.lastClick + getCtr(gexposure, gclick);
            m.globalLastExposure = m.globalLastExposure + 1;


            Info info = mMap.containsKey(m.materialId) ? mMap.get(m.materialId) : new Info();
            double gCtr = getCtr(m.globalLastExposure,m.globalLastClick);
            double rCtr = getCtr(exposure,click);
            double hCtr = getCtr(m.lastExposure,m.lastClick);
            info.gctr = gCtr;
            info.hctr = hCtr;
            info.rctr = rCtr;
            info.gexp = m.globalLastExposure;
            info.hexp = m.lastExposure;
            info.rexp = exposure;

            info.id = m.materialId;
            mMap.put(m.materialId,info);

            if(info.hexp > 50)
                maxH = Math.max(hCtr, maxH);
            maxG = Math.max(gCtr, maxG);
        }

        List<MaterialInfo> candiList = new ArrayList<>();

        for(MaterialInfo m : materialList)
        {
            double exposure = sum(m.exposure);
            if(exposure > 0) {
                double reward =  Constant.MIN_REWARD;
                double confidence = m.lastClick > 50 ? 1 : m.lastClick/50;

                reward =  (1-confidence) * normlize(mMap.get(m.materialId).gctr, maxG,0.5)
                            + confidence * normlize(mMap.get(m.materialId).hctr, maxH,0.5);
                reward = Math.max(reward, Constant.MIN_REWARD);

                if(m.materialId == 11118 && Math.random() < 0.01)
                {
                    logger.info("bandit mlist:{} materialId:{} appid:{} clk:{} hctr:{} maxh:{} reward:{} gctr:{} maxg:{}",materialList.size(), m.materialId,m.appId,m.lastClick,mMap.get(m.materialId).hctr,maxH,reward,mMap.get(m.materialId).gctr,maxG);
                }
                else if(Math.random() < 0.0001) {
                    logger.info("bandit mlist:{} materialId:{} appid:{} clk:{} hctr:{} maxh:{} reward:{} gctr:{} maxg:{}",materialList.size(), m.materialId,m.appId,m.lastClick,mMap.get(m.materialId).hctr,maxH,reward,mMap.get(m.materialId).gctr,maxG);
                }

                //init
                if(m.count < 20)
                {
                    m.count = 20;
                    m.reward = Math.min(mMap.get(m.materialId).gctr * 0.5 * m.count / maxG, 0.5 * m.count);
                }

                m.reward = m.reward * decay + reward;
                m.count = m.count * decay + 1.0;
                m.alpha = 1.0 + m.reward;
                m.beta = 1.0 + (m.count - m.reward);
            }

            rewards.add(m.reward);
            counts.add(m.count);
            alphas.add(m.alpha);
            betas.add(m.beta);
            candiList.add(m);

        }

        //3、select
        numMachines = candiList.size();
       // MaterialInfo material = candiList.get(selectMachine());
        List<Integer> ranklist = new ArrayList();
        ranklist = rankMachine();
        List<MaterialInfo> result = new ArrayList<>();
        for(Integer i : rankMachine())
        {
            MaterialInfo material = candiList.get(i);
            material.rank = i;
            result.add(material);
        }
        mMap.clear();
        return result;
    }

    public double normlize(double val, double  max, double limit)
    {
        double norm = Math.min(val*limit/max, limit);
        return norm;
    }


    public BayesianBandit() { //下周
        rewards = new ArrayList<Double>();
        counts = new ArrayList<Double>();
        alphas = new ArrayList<Double>();
        betas = new ArrayList<Double>();
    }

    private void clear()
    {
        rewards = new ArrayList<Double>();
        counts = new ArrayList<Double>();
        alphas = new ArrayList<Double>();
        betas = new ArrayList<Double>();
    }



    /*对各个备选策略的后验Beta分布采样得到当次最优策略*/
    private int selectMachine() {
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

    public List<Integer> rankMachine(){

        List<Integer> rankList = new ArrayList<>();
        List<GenericPair<Integer,Double>> pairList = new ArrayList<>();
        int selectMachine = 0;
        double maxTheta = 0;
        for (int i = 0; i < numMachines; i++) {
            double theta = BetaDistribution.BetaDist(alphas.get(i), betas.get(i));

            GenericPair pair = new GenericPair<>(i, theta);
            pairList.add(pair);
        }

        Collections.sort(pairList);
        for(GenericPair<Integer,Double> pair:pairList)
        {
            rankList.add(pair.first);
        }

        return rankList;
    }

    private double getCtr(double exp, double clk)
    {
        return exp > 0 ? clk / exp : 0;
    }

    private double sum(List<Long> list){
        double sum = 0;
        for(Long val: list)
        {
            sum += val;
        }
        return sum;
    }
}
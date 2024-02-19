package cn.com.duiba.nezha.compute.common.model.qscore;

/**
 * Created by jiali on 2017/10/31.
 */
public class Qscore {

    public double getQscore(QualityInfo info)
    {
        double qscore = 0.01;
        double cvr  = info.cvr.isNaN() || info.cvr == null ? 0 : info.cvr;
        double ctr = info.ctr.isNaN() || info.ctr == null ? 0 : info.ctr;
        double target = info.target;

        int cvrLevel = (int) (cvr / 0.06);

        if(info.type == 1) { //cpc
            qscore = ctr  + Math.max(Math.min(0.2, cvrLevel * cvr),0.1) * Math.min(cvr, 1.5 * ctr);
        }
        else //cpa
        {
            qscore = ctr  + Math.max(Math.min(0.2, cvrLevel * cvr),0.1) * Math.min(cvr, 1.5 * ctr);
        }
        return qscore;
    }

    public static void main(String[] args)
    {
        double ctr =0.345245655;
        double cvr = 0.04;
        double fee =38.6244204;
        int cvrLevel = (int) (cvr / 0.06);
        double qscore = ctr  + Math.max(Math.min(0.3, cvrLevel * cvr),0.1) * Math.min(cvr, 1.5 * ctr);
       System.out.println(qscore);
    }
}

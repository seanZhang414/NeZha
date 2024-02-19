package cn.com.duiba.nezha.compute.common.model.activityselect;

/**
 * Created by jiali on 2017/6/23.
 */
public class BernoulliMachine {

    private double p = 0.5;

    BernoulliMachine(double p)
    {
        this.p = p;
    }

    public double Pull()
    {
        if(Math.random() > p)
        {
            return 0;
        }
        return 1;
    }
}

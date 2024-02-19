package cn.com.duiba.nezha.engine.biz.vo.advert;

import java.io.Serializable;

/**
 * Created by xuezhaoming on 16/8/2. <br>
 * 权重对象</br>
 */
public class TagStatDimWeightVo implements Serializable {

    private static final long serialVersionUID = -316104112618444933L;

    //应用维度得分
    private double appMatchWeight;

    //活动类型维度得分
    private double activityMatchWeight;

    //应用和活动类型维度得分
    private double appAndActivityMatchWeight;

    //应用和活动类型混合维度得分
    private double appAndActivityMixMatchWeight;

    //全局维度得分
    private double globalMatchWeight;

    //应用维度,权重
    private double appMixWeight;

    //活动类型维度,权重
    private double activityMixWeight;

    public double getAppMatchWeight() { return appMatchWeight; }

    public void setAppMatchWeight(double appMatchWeight) { this.appMatchWeight = appMatchWeight;}

    public double getActivityMatchWeight() { return activityMatchWeight; }

    public void setActivityMatchWeight(double activityMatchWeight) { this.activityMatchWeight = activityMatchWeight;}

    public double getAppAndActivityMatchWeight() { return appAndActivityMatchWeight; }

    public void setAppAndActivityMatchWeight(double appAndActivityMatchWeight) { this.appAndActivityMatchWeight = appAndActivityMatchWeight;}

    public double getAppAndActivityMixMatchWeight() { return appAndActivityMixMatchWeight; }

    public void setAppAndActivityMixMatchWeight(double appAndActivityMixMatchWeight) { this.appAndActivityMixMatchWeight = appAndActivityMixMatchWeight;}

    public double getGlobalMatchWeight() { return globalMatchWeight; }

    public void setGlobalMatchWeight(double globalMatchWeight) { this.globalMatchWeight = globalMatchWeight;}

    public double getAppMixWeight() { return appMixWeight; }

    public void setAppMixWeight(double appWeight) { this.appMixWeight = appWeight;}

    public double getActivityMixWeight() { return activityMixWeight; }

    public void setActivityMixWeight(double activityMixWeight) { this.activityMixWeight = activityMixWeight;}

}


package cn.com.duiba.nezha.compute.common.model.pacing;

import java.util.List;

/**
 * Created by jiali on 2018/2/6.  广告+app维度
 */
public class TimeInfo
{
    List<Double> hourCtr;
    List<Double> hourCvr;
    List<Double> hourClk;
    List<Double> hourExp;
    List<Double> hourFee;
    List<Double> hourBudgetFee;
    List<Double> hourBudgetExp;
    Double packageBudget;

    public void setHourBudgetExp(List<Double> hourBudgetExp) {
        this.hourBudgetExp = hourBudgetExp;
    }

    public void setHourBudgetFee(List<Double> hourBudgetFee) {
        this.hourBudgetFee = hourBudgetFee;
    }

    public void setHourExp(List<Double> hourExp) {
        this.hourExp = hourExp;
    }

    public void setPackageBudget(Double packageBudget) {
        this.packageBudget = packageBudget;
    }

    public void setHourClk(List<Double> hourClk) {
        this.hourClk = hourClk;
    }

    public void setHourCtr(List<Double> hourCtr) {
        this.hourCtr = hourCtr;
    }

    public void setHourCvr(List<Double> hourCvr) {
        this.hourCvr = hourCvr;
    }

    public Double getPackageBudget() {
        return packageBudget;
    }

    public List<Double> getHourBudgetExp() {
        return hourBudgetExp;
    }

    public List<Double> getHourBudgetFee() {
        return hourBudgetFee;
    }

    public List<Double> getHourExp() {
        return hourExp;
    }

    public List<Double> getHourClk() {
        return hourClk;
    }

    public List<Double> getHourCtr() {
        return hourCtr;
    }

    public List<Double> getHourCvr() {
        return hourCvr;
    }

    public void setHourFee(List<Double> hourFee) {
        this.hourFee = hourFee;
    }

    public List<Double> getHourFee() {
        return hourFee;
    }
}

package cn.com.duiba.nezha.compute.common.model;

/**
 * Created by jiali on 2017/7/21.
 */

public class StatInfo{
    String id;
    double sumFee;
    double sumConv;
    double sumClick;
    double lastSumFee;
    double lastSumConv;
    double factor;
    double parentFactor;
    double conv7d;  //package传入global  slot/app传入app的值
    double fee7d;
    double click7d;

    public double getLastSumConv() {
        return lastSumConv;
    }

    public double getLastSumFee() {
        return lastSumFee;
    }

    public double getFactor() {
        return factor;
    }

    public double getSumConv() {
        return sumConv;
    }

    public double getSumFee() {
        return sumFee;
    }

    public String getId() {
        return id;
    }

    public void setLastSumConv(double lastSumConv) {
        this.lastSumConv = lastSumConv;
    }

    public void setLastSumFee(double lastSumFee) {
        this.lastSumFee = lastSumFee;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSumConv(double sumConv) {
        this.sumConv = sumConv;
    }

    public void setSumFee(double sumFee) {
        this.sumFee = sumFee;
    }

    public double getParentFactor() {
        return parentFactor;
    }


    public void setParentFactor(double parentFactor) {
        this.parentFactor = parentFactor;
    }

    public void setSumClick(double sumClick) {
        this.sumClick = sumClick;
    }

    public double getSumClick() {
        return sumClick;
    }

    public double getClick7d() {
        return click7d;
    }

    public double getConv7d() {
        return conv7d;
    }

    public double getFee7d() {
        return fee7d;
    }

    public void setClick7d(double click7d) {
        this.click7d = click7d;
    }

    public void setConv7d(double conv7d) {
        this.conv7d = conv7d;
    }

    public void setFee7d(double fee7d) {
        this.fee7d = fee7d;
    }
}
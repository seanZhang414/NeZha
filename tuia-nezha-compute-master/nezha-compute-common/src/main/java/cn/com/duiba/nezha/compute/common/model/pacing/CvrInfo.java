package cn.com.duiba.nezha.compute.common.model.pacing;

/**
 * Created by jiali on 2018/2/6.
 */
public class CvrInfo
{
    Double cvr;                 //融合值
    Double precvr;
    StatInfo adCvrInfo;
    StatInfo orientCvrInfo;		//配置级别cvr
    StatInfo competerCvrInfo;   //同行信息

    public void setCvr(Double cvr) {
        this.cvr = cvr;
    }

    public void setAdCvrInfo(StatInfo adCvrInfo) {
        this.adCvrInfo = adCvrInfo;
    }

    public void setOrientCvrInfo(StatInfo orientCvrInfo) {
        this.orientCvrInfo = orientCvrInfo;
    }

    public Double getCvr() {
        return cvr;
    }

    public void setCompeterCvrInfo(StatInfo competerCvrInfo) {
        this.competerCvrInfo = competerCvrInfo;
    }

    public StatInfo getCompeterCvrInfo() {
        return competerCvrInfo;
    }
}

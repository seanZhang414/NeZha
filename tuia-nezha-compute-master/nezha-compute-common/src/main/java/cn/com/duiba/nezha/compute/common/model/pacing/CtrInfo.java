package cn.com.duiba.nezha.compute.common.model.pacing;

/**
 * Created by jiali on 2018/2/6.
 */
public class CtrInfo
{
    Double ctr; //融合值
    StatInfo adCtrInfo;
    StatInfo orientCtrInfo;

    public void setCtr(Double ctr) {
        this.ctr = ctr;
    }

    public void setAdCtrInfo(StatInfo adCtrInfo) {
        this.adCtrInfo = adCtrInfo;
    }

    public void setOrientCtrInfo(StatInfo orientCtrInfo) {
        this.orientCtrInfo = orientCtrInfo;
    }

    public Double getCtr() {
        return ctr;
    }
}

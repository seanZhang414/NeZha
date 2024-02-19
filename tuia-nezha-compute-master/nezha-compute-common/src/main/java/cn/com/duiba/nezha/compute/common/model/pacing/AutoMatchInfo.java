package cn.com.duiba.nezha.compute.common.model.pacing;

/**
 * Created by jiali on 2018/4/12.
 */
public  class  AutoMatchInfo
{
    Long enablePeriod; //开启时间
    Boolean enable; //开关
    Long convertCost; //转化成本
    EffectLog effectLog; //效果日志

    public void setConvertCost(Long convertCost) {
        this.convertCost = convertCost;
    }

    public void setEffectLog(EffectLog effectLog) {
        this.effectLog = effectLog;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setEnablePeriod(Long enablePeriod) {
        this.enablePeriod = enablePeriod;
    }


    public class EffectLog
    {
        Long tag = 0l;     // 0 = 普通流量 1 = 劣质流量  2 = 优质流量
        double pcvr;  //劣质流量预估转化率
        double pctr;
        double arpu;

        public void setArpu(double arpu) {
            this.arpu = arpu;
        }

        public void setPctr(double pctr) {
            this.pctr = pctr;
        }

        public void setPcvr(double pcvr) {
            this.pcvr = pcvr;
        }

        public void setTag(Long tag) {
            this.tag = tag;
        }

        public double getArpu() {
            return arpu;
        }

        public double getPctr() {
            return pctr;
        }

        public double getPcvr() {
            return pcvr;
        }

        public Long getTag() {
            return tag;
        }
    }

    public EffectLog getEffectLog() {
        return effectLog;
    }

    public boolean getEnable() {
        return this.enable;
    }
}

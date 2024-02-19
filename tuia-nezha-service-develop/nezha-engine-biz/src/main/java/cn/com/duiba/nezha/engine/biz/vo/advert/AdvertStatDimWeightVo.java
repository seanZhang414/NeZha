package cn.com.duiba.nezha.engine.biz.vo.advert;

/**
 * Created by xuezhaoming on 16/8/2. <br>
 * 权重对象</br>
 */
public class AdvertStatDimWeightVo {


    public static final AdvertStatDimWeightVo advertStatDimWeightVoA = new AdvertStatDimWeightVo.Builder()
            .statCtrWeight(0.1)
            .preCtrWeight(0.9)
            .statCvrWeight(0.1)
            .preCvrWeight(0.9)
            .build();


    public static final AdvertStatDimWeightVo advertStatDimWeightVoB = new AdvertStatDimWeightVo.Builder()
            .statCtrWeight(0.3)
            .preCtrWeight(0.7)
            .preCvrWeight(0.7)
            .statCvrWeight(0.3)
            .build();

    //统计ctr权重
    private double  statCtrWeight;
    //预估ctr权重
    private double preCtrWeight;

    //统计cvr权重
    private double statCvrWeight;
    //预估cvr权重
    private double preCvrWeight;

    private AdvertStatDimWeightVo(Builder builder) {
        setStatCtrWeight(builder.statCtrWeight);
        setPreCtrWeight(builder.preCtrWeight);
        setStatCvrWeight(builder.statCvrWeight);
        setPreCvrWeight(builder.preCvrWeight);
    }

    public double getStatCtrWeight() { return statCtrWeight; }

    public void setStatCtrWeight(double statCtrWeight) { this.statCtrWeight = statCtrWeight;}

    public double getPreCtrWeight() { return preCtrWeight; }

    public void setPreCtrWeight(double preCtrWeight) { this.preCtrWeight = preCtrWeight;}

    public double getStatCvrWeight() {
        return statCvrWeight;
    }

    public void setStatCvrWeight(double statCvrWeight) {
        this.statCvrWeight = statCvrWeight;
    }

    public double getPreCvrWeight() {
        return preCvrWeight;
    }

    public void setPreCvrWeight(double preCvrWeight) {
        this.preCvrWeight = preCvrWeight;
    }


    public static final class Builder {
        private double statCtrWeight;
        private double preCtrWeight;
        private double statCvrWeight;
        private double preCvrWeight;

        public Builder() {
            // 构造器
        }

        public Builder statCtrWeight(double val) {
            statCtrWeight = val;
            return this;
        }

        public Builder preCtrWeight(double val) {
            preCtrWeight = val;
            return this;
        }

        public Builder statCvrWeight(double val) {
            statCvrWeight = val;
            return this;
        }

        public Builder preCvrWeight(double val) {
            preCvrWeight = val;
            return this;
        }

        public AdvertStatDimWeightVo build() {
            return new AdvertStatDimWeightVo(this);
        }
    }
}


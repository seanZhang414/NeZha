package cn.com.duiba.nezha.compute.common.model.pacing;

/**
 * Created by jiali on 2017/9/4.
 */
public class WilsonPair {
    public Double upperBound;
    public Double lowerBound;

    public WilsonPair(double l, double u) {
        this.upperBound = u;
        this.lowerBound = l;
    }

    @Override
    public String toString() {
        return "lowerBound: " + lowerBound + " upperBound: " + upperBound;
    }
}

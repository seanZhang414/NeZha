package cn.com.duiba.nezha.engine.biz.entity.nezha.advert;

import java.util.List;

public class SlotAdvertInfo {
    private List<Double> priceSection;  //候选集新加出价区间字段。cpa比较转化出价，cpc比较点击出价 如："1.2,1.5"
    private List<Double> cvrSet;           // 如："0.1522,0.2324,0.1156"
    private List<Double> confidenceSet;   //置信度 如："0.2,0.8,1.0"
    private List<Double> biasSet;         //偏差   如："0.8555,1.5786,1.1132"

    public List<Double> getPriceSection() {
        return priceSection;
    }

    public void setPriceSection(List<Double> priceSection) {
        this.priceSection = priceSection;
    }

    public List<Double> getCvrSet() {
        return cvrSet;
    }

    public void setCvrSet(List<Double> cvrSet) {
        this.cvrSet = cvrSet;
    }

    public List<Double> getConfidenceSet() {
        return confidenceSet;
    }

    public void setConfidenceSet(List<Double> confidenceSet) {
        this.confidenceSet = confidenceSet;
    }

    public List<Double> getBiasSet() {
        return biasSet;
    }

    public void setBiasSet(List<Double> biasSet) {
        this.biasSet = biasSet;
    }

    @Override
    public String toString() {
        return "SlotAdvertInfo{" +
                "priceSection=" + priceSection +
                ", cvrSet=" + cvrSet +
                ", confidenceSet=" + confidenceSet +
                ", biasSet=" + biasSet +
                '}';
    }
}

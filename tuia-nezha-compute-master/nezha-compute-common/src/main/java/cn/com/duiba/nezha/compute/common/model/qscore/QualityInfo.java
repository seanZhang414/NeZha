package cn.com.duiba.nezha.compute.common.model.qscore;

/**
 * Created by jiali on 2017/11/7.
 */
public class QualityInfo {

    Double cvr;     //融合值
    Double ctr;
    Integer type;   //1 = cpc 2 = cpa
    Double target;
    Long budget;
    String tags;

    public Double getCtr() {
        return ctr;
    }

    public Double getCvr() {
        return cvr;
    }

    public Double getTarget() {
        return target;
    }

    public Integer getType() {
        return type;
    }

    public long getBudget() {
        return budget;
    }

    public String getTags() {
        return tags;
    }

    public void setBudget(Long budget) {
        this.budget = budget;
    }

    public void setCtr(Double ctr) {
        this.ctr = ctr;
    }

    public void setCvr(Double cvr) {
        this.cvr = cvr;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setTarget(Double target) {
        this.target = target;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}

package cn.com.duiba.nezha.engine.biz.vo.advert;

import java.io.Serializable;

/**
 * Created by lee on 2016/8/28.
 */
public class AdvertMaterialResortVo {

    private static final long serialVersionUID = -316104112618444933L;
    private long materialId;    // 广告素材ID
    private double weight;      // 流量分配权重
    private double rankScore;   // 排序得分,ctr
    private long rank;      // 排序值

    public long getMaterialId() { return materialId; }

    public void setMaterialId(long materialId) { this.materialId = materialId; }

    public double getWeight() { return weight;}

    public void setWeight(double weight) { this.weight = weight;}

    public double getRankScore() { return rankScore; }

    public void setRankScore(double rankScore) { this.rankScore = rankScore; }

    public long getRank() { return rank; }

    public void setRank(long rank) { this.rank = rank; }
}

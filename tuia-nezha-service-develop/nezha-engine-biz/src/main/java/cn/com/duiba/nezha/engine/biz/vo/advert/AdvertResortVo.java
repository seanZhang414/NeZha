package cn.com.duiba.nezha.engine.biz.vo.advert;

import java.util.Set;

/**
 * Created by lee on 2016/8/28.
 */
public class AdvertResortVo {

    //广告ID
    private Long advertId;

    // 配置包id
    private Long packageId;

    // 广告帐号
    private Long accountId;

    // 是否是新增广告
    private Boolean newStatus;

    // 广告权重
    private Double weight;

    // 素材id
    protected Long materialId;

    // 计费类型 1-cpc 2-cpa
    private Integer chargeType;

    // 调价因子
    private Double factor;

    // 后端印子
    private Double backendFactor;

    // 广告发券次数
    private Long launchCountToUser;

    // 最终出价
    private Long finalFee;

    // 目标出价
    private Long originalFee;

    // ctr
    private Double ctr;

    // 统计ctr
    private Double statCtr;

    // 预估ctr
    private Double preCtr;

    // cvr
    private Double cvr;

    // 统计cvr
    private Double statCvr;

    // 预估cvr
    private Double preCvr;

    // 后端预估cvr
    private Double preBackendCvr;

    // 排名分
    private Double rankScore;

    // 排名
    private Long rank;

    // 是否放弃
    private Boolean giveUp;

    // 0 = 普通流量 1 = 劣质流量  2 = 优质流量
    private Long tag;

    // 备用广告
    private Set<Long> backupAdvertIds;

    private Double qScore;

    private Double tagWeight;

    private Double discountRate;

    private Double ctrReconstructionFactor;
    private Double cvrReconstructionFactor;

    private Integer qualityLevel;

    private String backendType;

    private Long budgetType;

    private Double budgetRatio;

    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getBudgetType() {
        return budgetType;
    }

    public void setBudgetType(Long budgetType) {
        this.budgetType = budgetType;
    }

    public Double getBudgetRatio() {
        return budgetRatio;
    }

    public void setBudgetRatio(Double budgetRatio) {
        this.budgetRatio = budgetRatio;
    }

    public Long getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Long advertId) {
        this.advertId = advertId;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Boolean getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(Boolean newStatus) {
        this.newStatus = newStatus;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public Integer getChargeType() {
        return chargeType;
    }

    public void setChargeType(Integer chargeType) {
        this.chargeType = chargeType;
    }

    public Double getFactor() {
        return factor;
    }

    public void setFactor(Double factor) {
        this.factor = factor;
    }

    public Double getBackendFactor() {
        return backendFactor;
    }

    public void setBackendFactor(Double backendFactor) {
        this.backendFactor = backendFactor;
    }

    public Long getLaunchCountToUser() {
        return launchCountToUser;
    }

    public void setLaunchCountToUser(Long launchCountToUser) {
        this.launchCountToUser = launchCountToUser;
    }

    public Long getFinalFee() {
        return finalFee;
    }

    public void setFinalFee(Long finalFee) {
        this.finalFee = finalFee;
    }

    public Long getOriginalFee() {
        return originalFee;
    }

    public void setOriginalFee(Long originalFee) {
        this.originalFee = originalFee;
    }

    public Double getCtr() {
        return ctr;
    }

    public void setCtr(Double ctr) {
        this.ctr = ctr;
    }

    public Double getStatCtr() {
        return statCtr;
    }

    public void setStatCtr(Double statCtr) {
        this.statCtr = statCtr;
    }

    public Double getPreCtr() {
        return preCtr;
    }

    public void setPreCtr(Double preCtr) {
        this.preCtr = preCtr;
    }

    public Double getCvr() {
        return cvr;
    }

    public void setCvr(Double cvr) {
        this.cvr = cvr;
    }

    public Double getStatCvr() {
        return statCvr;
    }

    public void setStatCvr(Double statCvr) {
        this.statCvr = statCvr;
    }

    public Double getPreCvr() {
        return preCvr;
    }

    public void setPreCvr(Double preCvr) {
        this.preCvr = preCvr;
    }

    public Double getPreBackendCvr() {
        return preBackendCvr;
    }

    public void setPreBackendCvr(Double preBackendCvr) {
        this.preBackendCvr = preBackendCvr;
    }

    public Double getRankScore() {
        return rankScore;
    }

    public void setRankScore(Double rankScore) {
        this.rankScore = rankScore;
    }

    public Long getRank() {
        return rank;
    }

    public void setRank(Long rank) {
        this.rank = rank;
    }

    public Boolean getGiveUp() {
        return giveUp;
    }

    public void setGiveUp(Boolean giveUp) {
        this.giveUp = giveUp;
    }

    public Long getTag() {
        return tag;
    }

    public void setTag(Long tag) {
        this.tag = tag;
    }

    public Set<Long> getBackupAdvertIds() {
        return backupAdvertIds;
    }

    public void setBackupAdvertIds(Set<Long> backupAdvertIds) {
        this.backupAdvertIds = backupAdvertIds;
    }

    public Double getqScore() {
        return qScore;
    }

    public void setqScore(Double qScore) {
        this.qScore = qScore;
    }

    public Double getTagWeight() {
        return tagWeight;
    }

    public void setTagWeight(Double tagWeight) {
        this.tagWeight = tagWeight;
    }

    public Double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(Double discountRate) {
        this.discountRate = discountRate;
    }

    public Double getCtrReconstructionFactor() {
        return ctrReconstructionFactor;
    }

    public void setCtrReconstructionFactor(Double ctrReconstructionFactor) {
        this.ctrReconstructionFactor = ctrReconstructionFactor;
    }

    public Double getCvrReconstructionFactor() {
        return cvrReconstructionFactor;
    }

    public void setCvrReconstructionFactor(Double cvrReconstructionFactor) {
        this.cvrReconstructionFactor = cvrReconstructionFactor;
    }

    public Integer getQualityLevel() {
        return qualityLevel;
    }

    public void setQualityLevel(Integer qualityLevel) {
        this.qualityLevel = qualityLevel;
    }

    public String getBackendType() {
        return backendType;
    }

    public void setBackendType(String backendType) {
        this.backendType = backendType;
    }
}

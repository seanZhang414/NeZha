package cn.com.duiba.nezha.engine.biz.entity.nezha.advert;

/**
 * 业务日志
 */
public class BizLogEntity {

    private String time;
    private Long advertId;
    private Long packageId;
    private Boolean isNew;
    private Long originalFee;
    private Long fee;
    private Integer chargeType;
    private Long count;

    private Long appId;
    private Long slotId;
    private Long duibaSlotId;

    private Long activityId;

    private String statRedisKey;

    private Double statCtr;
    private Double preCtr;
    private Double ctr;
    private Double statCvr;
    private Double preCvr;
    private Double cvr;
    private Double preBackendCvr;
    private Double factor;
    private Double backendFactor;
    private String backendType;//:1安装APP,2启动APP,3注册账户,4激活账户,5登录账户,6用户付费,7用户进件,8用户完件

    private Long materialId;

    private String cvrFeatureMap;
    private Integer algType;
    private String orderId;
    private Double advertWeight;

    private Double ctrCorrectionFactor = 1.0;
    private Double ctrReconstructionFactor = 1.0;
    private Double cvrCorrectionFactor = 1.0;
    private Double cvrReconstructionFactor = 1.0;

    private Integer notFreeAdvertNum;
    private Integer biddingAdvertNum;

    private Double preCtrAvg;
    private Double preCvrAvg;
    private Double statCtrAvg;
    private Double statCvrAvg;

    private Double qScore;
    private Double tagWeight;
    private Double discountRate;

    private Double rankScore;
    private Long rank;


    private Double advertCtr;
    private Double advertCvr;
    private Double advertAppCtr;
    private Double advertAppCvr;
    private Double advertSlotCtr;
    private Double advertSlotCvr;
    private Double advertActivityCtr;
    private Double advertActivityCvr;
    private String slotIndustryTagPid;
    private String slotIndustryTagId;
    private String appIndustryTagPid;
    private String appIndustryTagId;
    private String trafficTagPid;
    private String trafficTagId;
    // 0 = 普通流量 1 = 劣质流量  2 = 优质流量
    private Long tag;
    private double arpu;
    private Integer qualityLevel;

    private Long budgetType;

    private Double budgetRatio;

    public Long getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Long advertId) {
        this.advertId = advertId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public Long getDuibaSlotId() {
        return duibaSlotId;
    }

    public void setDuibaSlotId(Long duibaSlotId) {
        this.duibaSlotId = duibaSlotId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getOriginalFee() {
        return originalFee;
    }

    public void setOriginalFee(Long originalFee) {
        this.originalFee = originalFee;
    }

    public Integer getChargeType() {
        return chargeType;
    }

    public void setChargeType(Integer chargeType) {
        this.chargeType = chargeType;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getStatRedisKey() {
        return statRedisKey;
    }

    public void setStatRedisKey(String statRedisKey) {
        this.statRedisKey = statRedisKey;
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

    public Double getCtr() {
        return ctr;
    }

    public void setCtr(Double ctr) {
        this.ctr = ctr;
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

    public Double getCvr() {
        return cvr;
    }

    public void setCvr(Double cvr) {
        this.cvr = cvr;
    }

    public Double getPreBackendCvr() {
        return preBackendCvr;
    }

    public void setPreBackendCvr(Double preBackendCvr) {
        this.preBackendCvr = preBackendCvr;
    }

    public Long getFee() {
        return fee;
    }

    public void setFee(Long fee) {
        this.fee = fee;
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

    public Boolean getNew() {
        return isNew;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getCvrFeatureMap() {
        return cvrFeatureMap;
    }

    public void setCvrFeatureMap(String cvrFeatureMap) {
        this.cvrFeatureMap = cvrFeatureMap;
    }

    public Integer getAlgType() {
        return algType;
    }

    public void setAlgType(Integer algType) {
        this.algType = algType;
    }

    public String getBackendType() {
        return backendType;
    }

    public void setBackendType(String backendType) {
        this.backendType = backendType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Double getAdvertWeight() {
        return advertWeight;
    }

    public void setAdvertWeight(Double advertWeight) {
        this.advertWeight = advertWeight;
    }

    public Double getCtrCorrectionFactor() {
        return ctrCorrectionFactor;
    }

    public void setCtrCorrectionFactor(Double ctrCorrectionFactor) {
        this.ctrCorrectionFactor = ctrCorrectionFactor;
    }

    public Double getCtrReconstructionFactor() {
        return ctrReconstructionFactor;
    }

    public void setCtrReconstructionFactor(Double ctrReconstructionFactor) {
        this.ctrReconstructionFactor = ctrReconstructionFactor;
    }

    public Double getCvrCorrectionFactor() {
        return cvrCorrectionFactor;
    }

    public void setCvrCorrectionFactor(Double cvrCorrectionFactor) {
        this.cvrCorrectionFactor = cvrCorrectionFactor;
    }

    public Double getCvrReconstructionFactor() {
        return cvrReconstructionFactor;
    }

    public void setCvrReconstructionFactor(Double cvrReconstructionFactor) {
        this.cvrReconstructionFactor = cvrReconstructionFactor;
    }

    public Integer getNotFreeAdvertNum() {
        return notFreeAdvertNum;
    }

    public void setNotFreeAdvertNum(Integer notFreeAdvertNum) {
        this.notFreeAdvertNum = notFreeAdvertNum;
    }

    public Integer getBiddingAdvertNum() {
        return biddingAdvertNum;
    }

    public void setBiddingAdvertNum(Integer biddingAdvertNum) {
        this.biddingAdvertNum = biddingAdvertNum;
    }

    public Double getPreCtrAvg() {
        return preCtrAvg;
    }

    public void setPreCtrAvg(Double preCtrAvg) {
        this.preCtrAvg = preCtrAvg;
    }

    public Double getPreCvrAvg() {
        return preCvrAvg;
    }

    public void setPreCvrAvg(Double preCvrAvg) {
        this.preCvrAvg = preCvrAvg;
    }

    public Double getStatCtrAvg() {
        return statCtrAvg;
    }

    public void setStatCtrAvg(Double statCtrAvg) {
        this.statCtrAvg = statCtrAvg;
    }

    public Double getStatCvrAvg() {
        return statCvrAvg;
    }

    public void setStatCvrAvg(Double statCvrAvg) {
        this.statCvrAvg = statCvrAvg;
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

    public Double getAdvertCtr() {
        return advertCtr;
    }

    public void setAdvertCtr(Double advertCtr) {
        this.advertCtr = advertCtr;
    }

    public Double getAdvertCvr() {
        return advertCvr;
    }

    public void setAdvertCvr(Double advertCvr) {
        this.advertCvr = advertCvr;
    }

    public Double getAdvertAppCtr() {
        return advertAppCtr;
    }

    public void setAdvertAppCtr(Double advertAppCtr) {
        this.advertAppCtr = advertAppCtr;
    }

    public Double getAdvertAppCvr() {
        return advertAppCvr;
    }

    public void setAdvertAppCvr(Double advertAppCvr) {
        this.advertAppCvr = advertAppCvr;
    }

    public Double getAdvertSlotCtr() {
        return advertSlotCtr;
    }

    public void setAdvertSlotCtr(Double advertSlotCtr) {
        this.advertSlotCtr = advertSlotCtr;
    }

    public Double getAdvertSlotCvr() {
        return advertSlotCvr;
    }

    public void setAdvertSlotCvr(Double advertSlotCvr) {
        this.advertSlotCvr = advertSlotCvr;
    }

    public Double getAdvertActivityCtr() {
        return advertActivityCtr;
    }

    public void setAdvertActivityCtr(Double advertActivityCtr) {
        this.advertActivityCtr = advertActivityCtr;
    }

    public Double getAdvertActivityCvr() {
        return advertActivityCvr;
    }

    public void setAdvertActivityCvr(Double advertActivityCvr) {
        this.advertActivityCvr = advertActivityCvr;
    }

    public String getSlotIndustryTagPid() {
        return slotIndustryTagPid;
    }

    public void setSlotIndustryTagPid(String slotIndustryTagPid) {
        this.slotIndustryTagPid = slotIndustryTagPid;
    }

    public String getSlotIndustryTagId() {
        return slotIndustryTagId;
    }

    public void setSlotIndustryTagId(String slotIndustryTagId) {
        this.slotIndustryTagId = slotIndustryTagId;
    }

    public String getAppIndustryTagPid() {
        return appIndustryTagPid;
    }

    public void setAppIndustryTagPid(String appIndustryTagPid) {
        this.appIndustryTagPid = appIndustryTagPid;
    }

    public String getAppIndustryTagId() {
        return appIndustryTagId;
    }

    public void setAppIndustryTagId(String appIndustryTagId) {
        this.appIndustryTagId = appIndustryTagId;
    }

    public String getTrafficTagPid() {
        return trafficTagPid;
    }

    public void setTrafficTagPid(String trafficTagPid) {
        this.trafficTagPid = trafficTagPid;
    }

    public String getTrafficTagId() {
        return trafficTagId;
    }

    public void setTrafficTagId(String trafficTagId) {
        this.trafficTagId = trafficTagId;
    }

    public Long getTag() {
        return tag;
    }

    public void setTag(Long tag) {
        this.tag = tag;
    }

    public double getArpu() {
        return arpu;
    }

    public void setArpu(double arpu) {
        this.arpu = arpu;
    }

    public Integer getQualityLevel() {
        return qualityLevel;
    }

    public void setQualityLevel(Integer qualityLevel) {
        this.qualityLevel = qualityLevel;
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
}
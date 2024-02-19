package cn.com.duiba.nezha.compute.biz.log;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by pc on 2016/11/16.
 */
public class NezhaLog implements Serializable {

    private Long advertId;
    private Long appId;
    private Long packageId;
    private Long slotId;
    private Long activityId;
    private Long originalFee;
    private Long chargeType;
    private Set<Long> materialIdList;
    private Long count;
    private Double statCtr;
    private List<Double> statCtrs;
    private Double preCtr;
    private Double ctr;
    private Double statCvr;
    private List<Double> statCvrs;
    private Double preCvr;
    private Double cvr;
    private Long fee;
    private Double factor;
    private Boolean isNew;
    private Long materialId;
    private String cvrFeatureMap;
    private Long algType;
    private Long ctrNewFeatureNums;
    private Long ctrTotalFeatureNums;
    private Long cvrNewFeatureNums;
    private Long cvrTotalFeatureNums;
    private String orderId;
    private Double       advertWeight;
    private String       modelKey;

    private String currentTime;


    public Long getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Long advertId) {
        this.advertId = advertId;
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

    public Long getChargeType() {
        return chargeType;
    }

    public void setChargeType(Long chargeType) {
        this.chargeType = chargeType;
    }

    public Set<Long> getMaterialIdList() {
        return materialIdList;
    }

    public void setMaterialIdList(Set<Long> materialIdList) {
        this.materialIdList = materialIdList;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Double getStatCtr() {
        return statCtr;
    }

    public void setStatCtr(Double statCtr) {
        this.statCtr = statCtr;
    }

    public List<Double> getStatCtrs() {
        return statCtrs;
    }

    public void setStatCtrs(List<Double> statCtrs) {
        this.statCtrs = statCtrs;
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

    public List<Double> getStatCvrs() {
        return statCvrs;
    }

    public void setStatCvrs(List<Double> statCvrs) {
        this.statCvrs = statCvrs;
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

    public Long getAlgType() {
        return algType;
    }

    public void setAlgType(Long algType) {
        this.algType = algType;
    }

    public Long getCtrNewFeatureNums() {
        return ctrNewFeatureNums;
    }

    public void setCtrNewFeatureNums(Long ctrNewFeatureNums) {
        this.ctrNewFeatureNums = ctrNewFeatureNums;
    }

    public Long getCtrTotalFeatureNums() {
        return ctrTotalFeatureNums;
    }

    public void setCtrTotalFeatureNums(Long ctrTotalFeatureNums) {
        this.ctrTotalFeatureNums = ctrTotalFeatureNums;
    }

    public Long getCvrNewFeatureNums() {
        return cvrNewFeatureNums;
    }

    public void setCvrNewFeatureNums(Long cvrNewFeatureNums) {
        this.cvrNewFeatureNums = cvrNewFeatureNums;
    }

    public Long getCvrTotalFeatureNums() {
        return cvrTotalFeatureNums;
    }

    public void setCvrTotalFeatureNums(Long cvrTotalFeatureNums) {
        this.cvrTotalFeatureNums = cvrTotalFeatureNums;
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

    public String getModelKey() {
        return modelKey;
    }

    public void setModelKey(String modelKey) {
        this.modelKey = modelKey;
    }

    public String getCurrentTime() {return currentTime;}

    public void setCurrentTime(String currentTime) {this.currentTime = currentTime;}

}
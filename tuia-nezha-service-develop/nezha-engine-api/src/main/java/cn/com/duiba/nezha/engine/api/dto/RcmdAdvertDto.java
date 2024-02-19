package cn.com.duiba.nezha.engine.api.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by lwj on 2016/8/30.
 * 请求广告结果
 */
public class RcmdAdvertDto implements Serializable {

    private static final long serialVersionUID = -316104112618944933L;

    private Long packageId;

    //订单ID
    private String orderId;

    //广告ID
    private Long advertId;

    //单价
    private Long fee;

    //点击率
    private Double ctr;

    //统计点击率
    private Double statCtr;

    //预估点击率
    private Double preCtr;

    //转换率
    private Double cvr;

    //统计转换率
    private Double statCvr;

    //预估转换率
    private Double preCvr;

    //广告素材ID
    private Long materialId;

    //key->广告 id,value 配置包 id 列表
    @Deprecated
    private Map<Long, List<Long>> needClosePackage;

    // 流量类型
    private Long tag;

    // 原始广告id
    private Long originalAdvertId;

    private Boolean needReplace;

    // 推荐(定向/屏蔽)的媒体
    private Set<RecommendAppDto> recommendApps;

    // 需要熔断的配置包
    private Set<FusingOrientationPackageDto> fusingOrientationPackages;

    public Long getOriginalAdvertId() {
        return originalAdvertId;
    }

    public Boolean getNeedReplace() {
        return Optional.ofNullable(needReplace).orElse(false);
    }

    public void setNeedReplace(Boolean needReplace) {
        this.needReplace = needReplace;
    }

    public void setOriginalAdvertId(Long originalAdvertId) {
        this.originalAdvertId = originalAdvertId;
    }

    public Long getTag() {
        return tag;
    }

    public void setTag(Long tag) {
        this.tag = tag;
    }

    public Map<Long, List<Long>> getNeedClosePackage() {
        return needClosePackage;
    }

    public void setNeedClosePackage(Map<Long, List<Long>> needClosePackage) {
        this.needClosePackage = needClosePackage;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Long advertId) {
        this.advertId = advertId;
    }

    public Long getFee() {
        return fee;
    }

    public void setFee(Long fee) {
        this.fee = fee;
    }

    public Double getCtr() {
        return ctr;
    }

    public void setCtr(Double ctr) {
        this.ctr = ctr;
    }

    public Double getCvr() {
        return cvr;
    }

    public void setCvr(Double cvr) {
        this.cvr = cvr;
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

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public Set<RecommendAppDto> getRecommendApps() {
        return recommendApps;
    }

    public void setRecommendApps(Set<RecommendAppDto> recommendApps) {
        this.recommendApps = recommendApps;
    }

    public Set<FusingOrientationPackageDto> getFusingOrientationPackages() {
        return fusingOrientationPackages;
    }

    public void setFusingOrientationPackages(Set<FusingOrientationPackageDto> fusingOrientationPackages) {
        this.fusingOrientationPackages = fusingOrientationPackages;
    }
}

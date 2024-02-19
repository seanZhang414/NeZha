package cn.com.duiba.nezha.engine.biz.domain.advert;

import cn.com.duiba.nezha.engine.api.dto.AdvertNewDto;
import cn.com.duiba.nezha.engine.api.enums.ChargeTypeEnum;

import java.util.*;

public class OrientationPackage {
    // 配置包id
    private Long id;
    // 广告id
    private Long advertId;
    // 点击单价
    private Long fee;
    // 转化成本
    private Long convertCost;
    // 配置包类型 1-人工包 2-托管包
    private Integer packageType;
    // 投放类型 1-人工投放 2-自动托管 3-优投模式
    private Integer targetAppLimit;
    // 本次配置是否是强定向
    private Boolean strongTarget;
    // 预算
    private Long budget;
    // 计费类型 1-cpc, 2-cpa
    private Integer chargeType;
    // 素材列表
    private Set<Material> materials;
    // 0点到当前每小时预算（不包括当前小时）
    private List<Double> hourlyBudgetFees;
    // 0点到当前每小时预算发券（不包括当前小时）
    private List<Double> hourlyBudgetCounts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getBudget() {
        return budget;
    }

    public void setBudget(Long budget) {
        this.budget = budget;
    }

    public Integer getChargeType() {
        return chargeType;
    }

    public void setChargeType(Integer chargeType) {
        this.chargeType = chargeType;
    }

    public Set<Material> getMaterials() {
        return Optional.ofNullable(materials).orElseGet(HashSet::new);
    }

    public void setMaterials(Set<Material> materials) {
        this.materials = materials;
    }

    public List<Double> getHourlyBudgetFees() {
        return hourlyBudgetFees;
    }

    public void setHourlyBudgetFees(List<Double> hourlyBudgetFees) {
        this.hourlyBudgetFees = hourlyBudgetFees;
    }

    public List<Double> getHourlyBudgetCounts() {
        return hourlyBudgetCounts;
    }

    public void setHourlyBudgetCounts(List<Double> hourlyBudgetCounts) {
        this.hourlyBudgetCounts = hourlyBudgetCounts;
    }

    public Long getConvertCost() {
        return convertCost;
    }

    public void setConvertCost(Long convertCost) {
        this.convertCost = convertCost;
    }

    public Integer getPackageType() {
        return packageType;
    }

    public void setPackageType(Integer packageType) {
        this.packageType = packageType;
    }

    public Integer getTargetAppLimit() {
        return targetAppLimit;
    }

    public void setTargetAppLimit(Integer targetAppLimit) {
        this.targetAppLimit = targetAppLimit;
    }

    public Boolean getStrongTarget() {
        return strongTarget;
    }

    public void setStrongTarget(Boolean strongTarget) {
        this.strongTarget = strongTarget;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrientationPackage that = (OrientationPackage) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(advertId, that.advertId);
    }

    public Boolean isWeakTarget() {
        return !strongTarget;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, advertId);
    }

    public Boolean isCpa() {
        return this.chargeType.equals(ChargeTypeEnum.CPA.getValue());
    }


    public static OrientationPackage convert(AdvertNewDto advertDto) {
        OrientationPackage orientationPackage = new OrientationPackage();
        orientationPackage.setId(advertDto.getPackageId());
        orientationPackage.setAdvertId(advertDto.getAdvertId());
        orientationPackage.setPackageType(advertDto.getPackageType());
        orientationPackage.setFee(advertDto.getFee());
        orientationPackage.setConvertCost(advertDto.getConvertCost());
        orientationPackage.setTargetAppLimit(advertDto.getTargetAppLimit());
        orientationPackage.setStrongTarget(advertDto.getStrongTarget());
        orientationPackage.setBudget(advertDto.getPackageBudget());
        orientationPackage.setChargeType(advertDto.getChargeType());

        // 配置包时段数据
        orientationPackage.setHourlyBudgetFees(advertDto.getHourlyBudgetFees());
        orientationPackage.setHourlyBudgetCounts(advertDto.getHourlyBudgetCounts());
        return orientationPackage;
    }

}

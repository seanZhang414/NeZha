package cn.com.duiba.nezha.engine.api.dto;

import java.io.Serializable;
import java.util.*;

import static cn.com.duiba.nezha.engine.api.enums.ChargeTypeEnum.CPC;

/**
 * Created by lwj on 2016/8/30.
 * 请求广告结果
 */
public class AdvertNewDto implements Serializable {

    private static final long serialVersionUID = 523827427472072954L;

    /**
     * 广告ID
     */
    private Long advertId;

    /**
     * 配置包id
     */
    private Long packageId;

    /**
     * 包类型
     */
    private Integer packageType;

    /**
     * 定向媒体类型
     */
    private Integer targetAppLimit;

    /**
     * 是否是强定向
     */
    private Boolean strongTarget;

    /**
     * 广告出价
     */
    private Long fee;

    /**
     * 对该用户的最近发放次数
     */
    private Long launchCountToUser;

    /**
     * 计费类型 1.CPC 2.CPA
     */
    private Integer chargeType;

    /**
     * 推广链接标签
     */
    private Set<String> spreadTags;

    /**
     * 广告主账号
     */
    private String accountId;

    /**
     * 特征标签
     */
    private String matchTagNums;

    /**
     * 素材列表
     */
    private Set<MaterialDto> materials;

    /**
     * 素材及其对应的标签列表
     */
    private Map<Long, Set<String>> materialMapNew;

    /**
     * 标签权重
     */
    private Double weight;

    /**
     * 0点到当前每小时预算（不包括当前小时）
     */
    private List<Double> hourlyBudgetFees;

    /**
     * 0点到当前每小时预算发券（不包括当前小时）
     */
    private List<Double> hourlyBudgetCounts;

    /**
     * 定向配置总预算
     */
    private Long packageBudget;

    /**
     * 折扣率
     */
    private Double discountRate;

    /**
     * 行业标签
     */
    private String industryTag;

    /**
     * 行业标签(新)
     */
    private String industryTagNew;

    /**
     * 转换成本
     */
    private Long convertCost;

    /**
     * 备选广告id列表
     */
    private Set<Long> backupAdvertIds;

    /**
     * 是否开启自动托管
     */
    @Deprecated
    private Boolean trusteeship;

    /**
     * 自动托管转化成本
     */
    @Deprecated
    private Long trusteeshipConvertCost;

    private AdvertNewDto(Builder builder) {
        setAdvertId(builder.advertId);
        setPackageId(builder.packageId);
        setFee(builder.fee);
        setLaunchCountToUser(builder.launchCountToUser);
        setChargeType(builder.chargeType);
        setSpreadTags(builder.spreadTags);
        setAccountId(builder.accountId);
        setMatchTagNums(builder.matchTagNums);
        setMaterials(builder.materials);
        setMaterialMapNew(builder.materialMapNew);
        setWeight(builder.weight);
        setHourlyBudgetFees(builder.hourlyBudgetFees);
        setHourlyBudgetCounts(builder.hourlyBudgetCounts);
        setPackageBudget(builder.packageBudget);
        setDiscountRate(builder.discountRate);
        setIndustryTag(builder.industryTag);
        setIndustryTagNew(builder.industryTagNew);
        setConvertCost(builder.convertCost);
        setBackupAdvertIds(builder.backupAdvertIds);
        setTrusteeship(builder.trusteeship);
        setTrusteeshipConvertCost(builder.trusteeshipConvertCost);
        setPackageType(builder.packageType);
        setTargetAppLimit(builder.targetAppLimit);
        setStrongTarget(builder.strongTarget);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    // todo 下个版本删除兼容代码
    public Integer getPackageType() {

        // 如果packageType不为null,则说明新代码上线.得到什么就返回什么
        return Optional.ofNullable(packageType).orElseGet(() -> {
            if (getTrusteeship()) {//到这里说明新代码没有上线.如果是自动托管.则直接返回包类型为托管包
                return 2;
            } else {// 非自动托管
                if (chargeType.equals(CPC.getValue())) {//判断该配置包是不是cpc
                    return 1;
                } else {//如果是cpa配置.一定是托管包
                    return 2;
                }
            }
        });
    }

    public void setPackageType(Integer packageType) {
        this.packageType = packageType;
    }

    // todo 下个版本删除兼容代码
    public Integer getTargetAppLimit() {
        // 如果targetAppLimit不为null,则说明新代码上线.得到什么就返回什么
        return Optional.ofNullable(targetAppLimit).orElseGet(() -> {
            if (getTrusteeship()) {//到这里说明新代码没有上线.如果是自动托管.则直接返回为优投模式
                return 2;
            } else {
                if (chargeType.equals(CPC.getValue())) {//判断该配置是否是cpc
                    return 1;
                } else {
                    return 3;// 如果是cpa配置.则一定是优投模式
                }
            }
        });
    }

    public void setTargetAppLimit(Integer targetAppLimit) {
        this.targetAppLimit = targetAppLimit;
    }

    public Boolean getStrongTarget() {
        // 如果strongTarget为空.则说明新代码没有上线.
        return Optional.ofNullable(strongTarget).orElseGet(() -> !getTrusteeship());
    }

    public void setStrongTarget(Boolean strongTarget) {
        this.strongTarget = strongTarget;
    }

    public Long getTrusteeshipConvertCost() {
        return trusteeshipConvertCost;
    }

    public void setTrusteeshipConvertCost(Long trusteeshipConvertCost) {
        this.trusteeshipConvertCost = trusteeshipConvertCost;
    }

    public Boolean getTrusteeship() {
        return Optional.ofNullable(trusteeship).orElse(false);
    }

    public Long getPackageId() {
        return this.packageId;
    }

    public void setTrusteeship(Boolean trusteeship) {
        this.trusteeship = trusteeship;
    }

    public Set<Long> getBackupAdvertIds() {
        return Optional.ofNullable(backupAdvertIds).orElseGet(HashSet::new);
    }

    public void setBackupAdvertIds(Set<Long> backupAdvertIds) {
        this.backupAdvertIds = backupAdvertIds;
    }

    public Set<MaterialDto> getMaterials() {
        return Optional.ofNullable(materials).orElseGet(HashSet::new);
    }

    public void setMaterials(Set<MaterialDto> materials) {
        this.materials = materials;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public Long getLaunchCountToUser() {
        return this.launchCountToUser;
    }

    public void setLaunchCountToUser(Long launchCountToUser) {
        this.launchCountToUser = launchCountToUser;
    }

    public AdvertNewDto() {
    }

    public Long getCurrentCount() {
        long currentCount = Optional.ofNullable(this.launchCountToUser).orElse(0L) + 1;
        if (currentCount > 10) {
            currentCount = 10;
        }
        return currentCount;
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

    public Integer getChargeType() {
        return chargeType;
    }

    public void setChargeType(Integer chargeType) {
        this.chargeType = chargeType;
    }

    public Set<String> getSpreadTags() {
        return spreadTags;
    }

    public void setSpreadTags(Set<String> spreadTags) {
        this.spreadTags = spreadTags;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getMatchTagNums() {
        return matchTagNums;
    }

    public void setMatchTagNums(String matchTagNums) {
        this.matchTagNums = matchTagNums;
    }

    public Map<Long, Set<String>> getMaterialMapNew() {
        return Optional.ofNullable(materialMapNew).orElseGet(HashMap::new);
    }

    public void setMaterialMapNew(Map<Long, Set<String>> materialMapNew) {
        this.materialMapNew = materialMapNew;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
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

    public Long getPackageBudget() {
        return packageBudget;
    }

    public void setPackageBudget(Long packageBudget) {
        this.packageBudget = packageBudget;
    }

    public Double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(Double discountRate) {
        this.discountRate = discountRate;
    }

    public String getIndustryTag() {
        return industryTag;
    }

    public void setIndustryTag(String industryTag) {
        this.industryTag = industryTag;
    }

    public String getIndustryTagNew() {
        return industryTagNew;
    }

    public void setIndustryTagNew(String industryTagNew) {
        this.industryTagNew = industryTagNew;
    }

    // todo 下个版本删除兼容代码
    public Long getConvertCost() {
        // 如果有包类型.则说明新代码上线.直接返回转化成本
        if (packageType != null) {
            return this.convertCost;
        } else {// 否则.新代码没有上线
            if (getTrusteeship()) {// 如果是自动托管.则返回自动托管成本
                return trusteeshipConvertCost;
            } else {
                if (chargeType.equals(1)) {
                    return null;
                } else {
                    return fee;
                }
            }
        }
    }

    public void setConvertCost(Long convertCost) {
        this.convertCost = convertCost;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdvertNewDto that = (AdvertNewDto) o;
        return Objects.equals(advertId, that.advertId) &&
                Objects.equals(this.packageId, that.packageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(advertId, packageId);
    }


    public static final class Builder {
        private Long advertId;
        private Long packageId;
        private Long fee;
        private Long launchCountToUser;
        private Integer chargeType;
        private Set<String> spreadTags;
        private String accountId;
        private String matchTagNums;
        private Set<MaterialDto> materials;
        private Map<Long, Set<String>> materialMapNew;
        private Double weight;
        private List<Double> hourlyBudgetFees;
        private List<Double> hourlyBudgetCounts;
        private Long packageBudget;
        private Double discountRate;
        private String industryTag;
        private String industryTagNew;
        private Long convertCost;
        private Set<Long> backupAdvertIds;
        private Boolean trusteeship;
        private Long trusteeshipConvertCost;
        private Integer packageType;
        private Integer targetAppLimit;
        private Boolean strongTarget;

        private Builder() {
        }

        public Builder advertId(Long val) {
            advertId = val;
            return this;
        }

        public Builder packageId(Long val) {
            packageId = val;
            return this;
        }

        public Builder fee(Long val) {
            fee = val;
            return this;
        }

        public Builder launchCountToUser(Long val) {
            launchCountToUser = val;
            return this;
        }

        public Builder chargeType(Integer val) {
            chargeType = val;
            return this;
        }

        public Builder spreadTags(Set<String> val) {
            spreadTags = val;
            return this;
        }

        public Builder accountId(String val) {
            accountId = val;
            return this;
        }

        public Builder matchTagNums(String val) {
            matchTagNums = val;
            return this;
        }

        public Builder materials(Set<MaterialDto> val) {
            materials = val;
            return this;
        }

        public Builder materialMapNew(Map<Long, Set<String>> val) {
            materialMapNew = val;
            return this;
        }

        public Builder weight(Double val) {
            weight = val;
            return this;
        }

        public Builder hourlyBudgetFees(List<Double> val) {
            hourlyBudgetFees = val;
            return this;
        }

        public Builder hourlyBudgetCounts(List<Double> val) {
            hourlyBudgetCounts = val;
            return this;
        }

        public Builder packageBudget(Long val) {
            packageBudget = val;
            return this;
        }

        public Builder discountRate(Double val) {
            discountRate = val;
            return this;
        }

        public Builder industryTag(String val) {
            industryTag = val;
            return this;
        }

        public Builder industryTagNew(String val) {
            industryTagNew = val;
            return this;
        }

        public Builder convertCost(Long val) {
            convertCost = val;
            return this;
        }

        public Builder backupAdvertIds(Set<Long> val) {
            backupAdvertIds = val;
            return this;
        }

        public Builder trusteeship(Boolean val) {
            trusteeship = val;
            return this;
        }

        public Builder trusteeshipConvertCost(Long val) {
            trusteeshipConvertCost = val;
            return this;
        }

        public Builder packageType(Integer val) {
            packageType = val;
            return this;
        }

        public Builder targetAppLimit(Integer val) {
            targetAppLimit = val;
            return this;
        }

        public Builder strongTarget(Boolean val) {
            strongTarget = val;
            return this;
        }

        public AdvertNewDto build() {
            return new AdvertNewDto(this);
        }
    }
}

package cn.com.duiba.nezha.engine.biz.domain.advert;

import cn.com.duiba.nezha.engine.api.dto.AdvertNewDto;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Advert {
    private Long id;

    private Long accountId;
    // 推广标签
    private Set<String> spreadTags;
    // 定向配置包
    private Set<OrientationPackage> orientationPackages;
    // 已经对用户发的次数
    private Long launchCountToUser;
    // 权重
    private Double weight;
    // 行业标签
    private String industryTag;
    // 备用广告列表
    private Set<Long> backupAdvertIds;
    // 属性标签
    private String matchTags;
    // 折扣率
    private Double discountRate;

    public Double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(Double discountRate) {
        this.discountRate = discountRate;
    }

    public String getMatchTags() {
        return matchTags;
    }

    public void setMatchTags(String matchTags) {
        this.matchTags = matchTags;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Set<String> getSpreadTags() {
        return Optional.ofNullable(spreadTags).orElseGet(HashSet::new);
    }

    public void setSpreadTags(Set<String> spreadTags) {
        this.spreadTags = spreadTags;
    }

    public Set<OrientationPackage> getOrientationPackages() {
        return Optional.ofNullable(orientationPackages).orElseGet(HashSet::new);
    }

    public void setOrientationPackages(Set<OrientationPackage> orientationPackages) {
        this.orientationPackages = orientationPackages;
    }

    public Long getLaunchCountToUser() {
        return launchCountToUser;
    }

    public void setLaunchCountToUser(Long launchCountToUser) {
        this.launchCountToUser = launchCountToUser;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getIndustryTag() {
        return industryTag;
    }

    public void setIndustryTag(String industryTag) {
        this.industryTag = industryTag;
    }

    public Set<Long> getBackupAdvertIds() {
        return backupAdvertIds;
    }

    public void setBackupAdvertIds(Set<Long> backupAdvertIds) {
        this.backupAdvertIds = backupAdvertIds;
    }

    public Long getCurrentLaunchCountToUser() {
        return Optional.ofNullable(this.getLaunchCountToUser()).orElse(0L) + 1;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Advert advert = (Advert) o;
        return Objects.equals(id, advert.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Boolean hasCpaOrientationPackage() {
        return this.getOrientationPackages().stream().anyMatch(OrientationPackage::isCpa);
    }

    public static Advert convert(AdvertNewDto advertDto) {
        Advert newAdvert = new Advert();
        newAdvert.setId(advertDto.getAdvertId());
        newAdvert.setAccountId(Long.parseLong(advertDto.getAccountId()));
        newAdvert.setSpreadTags(advertDto.getSpreadTags());
        newAdvert.setLaunchCountToUser(advertDto.getLaunchCountToUser());
        newAdvert.setWeight(advertDto.getWeight());
        newAdvert.setIndustryTag(advertDto.getIndustryTagNew());
        newAdvert.setBackupAdvertIds(advertDto.getBackupAdvertIds());
        newAdvert.setMatchTags(advertDto.getMatchTagNums());
        newAdvert.setDiscountRate(advertDto.getDiscountRate());
        return newAdvert;
    }
}

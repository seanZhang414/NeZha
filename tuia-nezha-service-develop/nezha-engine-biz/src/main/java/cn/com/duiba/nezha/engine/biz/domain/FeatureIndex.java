package cn.com.duiba.nezha.engine.biz.domain;

import java.util.Objects;

public class FeatureIndex {
    private Long advertId;
    private Long packageId;
    private Long materialId;

    public FeatureIndex() {
    }

    public FeatureIndex(Long advertId) {
        this.advertId = advertId;
    }

    public FeatureIndex(Long advertId, Long materialId) {
        this.advertId = advertId;
        this.materialId = materialId;
    }

    private FeatureIndex(Builder builder) {
        setAdvertId(builder.advertId);
        setPackageId(builder.packageId);
        setMaterialId(builder.materialId);
    }

    public static Builder newBuilder() {
        return new Builder();
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

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeatureIndex that = (FeatureIndex) o;
        return Objects.equals(advertId, that.advertId) &&
                Objects.equals(packageId, that.packageId) &&
                Objects.equals(materialId, that.materialId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(advertId, packageId, materialId);
    }

    public static final class Builder {
        private Long advertId;
        private Long packageId;
        private Long materialId;

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

        public Builder materialId(Long val) {
            materialId = val;
            return this;
        }

        public FeatureIndex build() {
            return new FeatureIndex(this);
        }
    }
}

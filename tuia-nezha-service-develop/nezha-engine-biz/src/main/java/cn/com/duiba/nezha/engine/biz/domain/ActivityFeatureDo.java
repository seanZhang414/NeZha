package cn.com.duiba.nezha.engine.biz.domain;

public class ActivityFeatureDo {
    public static final ActivityFeatureDo DEFAULT = ActivityFeatureDo.newBuilder()
            .performance(-1L)
            .basicInfo(-1L)
            .launchInfo(-1L)
            .totalInfo(-1L)
            .build();
    private Long performance;
    private Long launchInfo;
    private Long basicInfo;
    private Long totalInfo;

    public ActivityFeatureDo() {
        // do nothing
    }

    private ActivityFeatureDo(Builder builder) {
        setPerformance(builder.performance);
        setLaunchInfo(builder.launchInfo);
        setBasicInfo(builder.basicInfo);
        setTotalInfo(builder.totalInfo);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getPerformance() {
        return performance;
    }

    public void setPerformance(Long performance) {
        this.performance = performance;
    }

    public Long getLaunchInfo() {
        return launchInfo;
    }

    public void setLaunchInfo(Long launchInfo) {
        this.launchInfo = launchInfo;
    }

    public Long getBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(Long basicInfo) {
        this.basicInfo = basicInfo;
    }

    public Long getTotalInfo() {
        return totalInfo;
    }

    public void setTotalInfo(Long totalInfo) {
        this.totalInfo = totalInfo;
    }

    public static final class Builder {
        private Long performance;
        private Long launchInfo;
        private Long basicInfo;
        private Long totalInfo;

        private Builder() {
        }

        public Builder performance(Long val) {
            performance = val;
            return this;
        }

        public Builder launchInfo(Long val) {
            launchInfo = val;
            return this;
        }

        public Builder basicInfo(Long val) {
            basicInfo = val;
            return this;
        }

        public Builder totalInfo(Long val) {
            totalInfo = val;
            return this;
        }

        public ActivityFeatureDo build() {
            return new ActivityFeatureDo(this);
        }
    }
}

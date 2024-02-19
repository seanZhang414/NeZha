package cn.com.duiba.nezha.engine.biz.entity.nezha.advert;

/**
 * 广告统计融合数据
 *
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: AdvertStatisticMergeEntity.java , v 0.1 2017/7/5 下午2:17 ZhouFeng Exp $
 */
public class AdvertStatisticMergeEntity {

    public static final AdvertStatisticMergeEntity DEFAULT = new AdvertStatisticMergeEntity.Builder().build();
    /**
     * app维度当前小时
     */
    private Double appCurrentlyHour;

    /**
     * app维度当天
     */
    private Double appCurrentlyDay;

    /**
     * app维度最近7天
     */
    private Double appRecently7Day;

    /**
     * 全局维度当前小时
     */
    private Double globalCurrentlyHour;

    /**
     * 全局维度当天
     */
    private Double globalCurrentlyDay;


    /**
     * 全局维度最近7天
     */
    private Double globalRecently7Day;

    private AdvertStatisticMergeEntity(Builder builder) {
        appCurrentlyHour = builder.appCurrentlyHour;
        appCurrentlyDay = builder.appCurrentlyDay;
        appRecently7Day = builder.appRecently7Day;
        globalCurrentlyHour = builder.globalCurrentlyHour;
        globalCurrentlyDay = builder.globalCurrentlyDay;
        globalRecently7Day = builder.globalRecently7Day;
    }

    public Double getAppCurrentlyHour() {
        return appCurrentlyHour;
    }


    public Double getAppCurrentlyDay() {
        return appCurrentlyDay;
    }

    public Double getAppRecently7Day() {
        return appRecently7Day;
    }

    public Double getGlobalCurrentlyDay() {
        return globalCurrentlyDay;
    }

    public Double getGlobalRecently7Day() {
        return globalRecently7Day;
    }

    public Double getGlobalCurrentlyHour() {
        return globalCurrentlyHour;
    }


    public static final class Builder {
        private Double appCurrentlyHour;
        private Double appCurrentlyDay;
        private Double appRecently7Day;
        private Double globalCurrentlyHour;
        private Double globalCurrentlyDay;
        private Double globalRecently7Day;

        public Builder() {
            // 默认构造方法
        }

        public Builder appCurrentlyHour(Double val) {
            appCurrentlyHour = val;
            return this;
        }

        public Builder appCurrentlyDay(Double val) {
            appCurrentlyDay = val;
            return this;
        }

        public Builder appRecently7Day(Double val) {
            appRecently7Day = val;
            return this;
        }

        public Builder globalCurrentlyHour(Double val) {
            globalCurrentlyHour = val;
            return this;
        }

        public Builder globalCurrentlyDay(Double val) {
            globalCurrentlyDay = val;
            return this;
        }

        public Builder globalRecently7Day(Double val) {
            globalRecently7Day = val;
            return this;
        }

        public AdvertStatisticMergeEntity build() {
            return new AdvertStatisticMergeEntity(this);
        }
    }
}

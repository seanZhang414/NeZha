package cn.com.duiba.nezha.engine.biz.entity.nezha.advert;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: AdvertRequestBasicParameter.java , v 0.1 2017/12/15 下午3:14 ZhouFeng Exp $
 */
public class AdvertRequestBasicParameter {

    private Long appId;
    private Long slotId;
    private Long activityId;
    /**
     * 活动类型
     */
    private Integer activityUserType;

    private AdvertRequestBasicParameter(Builder builder) {
        appId = builder.appId;
        slotId = builder.slotId;
        activityId = builder.activityId;
        activityUserType = builder.activityUserType;
    }


    public Long getAppId() {
        return appId;
    }

    public Long getSlotId() {
        return slotId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public Integer getActivityUserType() {
        return activityUserType;
    }

    public static final class Builder {
        private Long appId;
        private Long slotId;
        private Long activityId;
        private Integer activityUserType;

        public Builder() {
            //builder
        }

        public Builder appId(Long val) {
            appId = val;
            return this;
        }

        public Builder slotId(Long val) {
            slotId = val;
            return this;
        }

        public Builder activityId(Long val) {
            activityId = val;
            return this;
        }

        public Builder activityUserType(Integer val) {
            activityUserType = val;
            return this;
        }

        public AdvertRequestBasicParameter build() {
            return new AdvertRequestBasicParameter(this);
        }
    }
}

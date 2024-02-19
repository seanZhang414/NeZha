package cn.com.duiba.nezha.engine.api.dto;

import java.io.Serializable;

/**
 * 广告推荐活动参数
 *
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: AdvertActivityDto.java , v 0.1 2017/6/8 下午6:57 ZhouFeng Exp $
 */
public class AdvertActivityDto implements Serializable {
    private static final long serialVersionUID = -3258192891024908925L;

    /**
     * 运营活动id
     */
    private Long operatingActivityId;

    /**
     * 活动类型(0兑吧普通，1兑吧商业，2推啊商业）
     */
    private Integer activityUseType;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 活动类型
     */
    private Long activityType;

    /**
     * 活动标签
     */
    private String tag;


    public Long getOperatingActivityId() {
        return operatingActivityId;
    }

    public AdvertActivityDto setOperatingActivityId(Long operatingActivityId) {
        this.operatingActivityId = operatingActivityId;
        return this;
    }

    public Integer getActivityUseType() {
        return activityUseType;
    }

    public AdvertActivityDto setActivityUseType(Integer activityUseType) {
        this.activityUseType = activityUseType;
        return this;
    }

    public Long getActivityId() {
        return activityId;
    }

    public AdvertActivityDto setActivityId(Long activityId) {
        this.activityId = activityId;
        return this;
    }

    public Long getActivityType() {
        return activityType;
    }

    public AdvertActivityDto setActivityType(Long activityType) {
        this.activityType = activityType;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public AdvertActivityDto setTag(String tag) {
        this.tag = tag;
        return this;
    }
}

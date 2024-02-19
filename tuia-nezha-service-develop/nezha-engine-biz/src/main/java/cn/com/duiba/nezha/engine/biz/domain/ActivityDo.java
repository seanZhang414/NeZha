package cn.com.duiba.nezha.engine.biz.domain;

import cn.com.duiba.nezha.engine.api.dto.AdvertActivityDto;

public class ActivityDo {
    // 活动id
    private Long id;

    // 运营活动id
    private Long operatingId;

    // 活动类型(0兑吧普通，1兑吧商业，2推啊商业）
    private Integer useType;

    // 活动类型
    private Long type;

    // 活动标签
    private String tag;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOperatingId() {
        return operatingId;
    }

    public void setOperatingId(Long operatingId) {
        this.operatingId = operatingId;
    }

    public Integer getUseType() {
        return useType;
    }

    public void setUseType(Integer useType) {
        this.useType = useType;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }


    public static ActivityDo convert(AdvertActivityDto activityDto) {
        ActivityDo activityDo = new ActivityDo();
        activityDo.setId(activityDto.getActivityId());
        activityDo.setOperatingId(activityDto.getOperatingActivityId());
        activityDo.setUseType(activityDto.getActivityUseType());
        activityDo.setType(activityDto.getActivityType());
        activityDo.setTag(activityDto.getTag());
        return activityDo;
    }
}

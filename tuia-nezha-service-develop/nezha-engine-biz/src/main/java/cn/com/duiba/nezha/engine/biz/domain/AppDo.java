package cn.com.duiba.nezha.engine.biz.domain;

import cn.com.duiba.nezha.engine.api.dto.AppDto;

public class AppDo {
    private Long id;

    // app类型
    private String category;

    // 广告位id
    private Long slotId;

    // 广告位宽度
    private Integer slotWidth;

    // 广告位高度
    private Integer slotHeight;

    // 广告位类型
    private Long slotType;

    // 广告位标签父ID
    private String slotIndustryTagPid;

    // 广告位标签ID
    private String slotIndustryTagId;

    // 媒体标签父ID
    private String industryTagPid;

    // 媒体标签ID
    private String industryTagId;

    // 流量标签ID
    private String trafficTagId;

    // 流量标签父ID
    private String trafficTagPid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public Integer getSlotWidth() {
        return slotWidth;
    }

    public void setSlotWidth(Integer slotWidth) {
        this.slotWidth = slotWidth;
    }

    public Integer getSlotHeight() {
        return slotHeight;
    }

    public void setSlotHeight(Integer slotHeight) {
        this.slotHeight = slotHeight;
    }

    public Long getSlotType() {
        return slotType;
    }

    public void setSlotType(Long slotType) {
        this.slotType = slotType;
    }

    public String getSlotIndustryTagPid() {
        return slotIndustryTagPid;
    }

    public void setSlotIndustryTagPid(String slotIndustryTagPid) {
        this.slotIndustryTagPid = slotIndustryTagPid;
    }

    public String getSlotIndustryTagId() {
        return slotIndustryTagId;
    }

    public void setSlotIndustryTagId(String slotIndustryTagId) {
        this.slotIndustryTagId = slotIndustryTagId;
    }

    public String getIndustryTagPid() {
        return industryTagPid;
    }

    public void setIndustryTagPid(String industryTagPid) {
        this.industryTagPid = industryTagPid;
    }

    public String getIndustryTagId() {
        return industryTagId;
    }

    public void setIndustryTagId(String industryTagId) {
        this.industryTagId = industryTagId;
    }

    public String getTrafficTagId() {
        return trafficTagId;
    }

    public void setTrafficTagId(String trafficTagId) {
        this.trafficTagId = trafficTagId;
    }

    public String getTrafficTagPid() {
        return trafficTagPid;
    }

    public void setTrafficTagPid(String trafficTagPid) {
        this.trafficTagPid = trafficTagPid;
    }

    public static AppDo convert(AppDto appDto) {
        AppDo appDo = new AppDo();
        appDo.setId(appDto.getAppId());
        appDo.setCategory(appDto.getAppCategory());
        appDo.setSlotId(appDto.getSlotId());
        appDo.setSlotWidth(appDto.getSlotWidth());
        appDo.setSlotHeight(appDto.getSlotHeight());
        appDo.setSlotType(appDto.getSlotType());
        appDo.setSlotIndustryTagPid(appDto.getSlotIndustryTagPid());
        appDo.setSlotIndustryTagId(appDto.getSlotIndustryTagId());
        appDo.setIndustryTagPid(appDto.getAppIndustryTagPid());
        appDo.setIndustryTagId(appDto.getAppIndustryTagId());
        appDo.setTrafficTagId(appDto.getTrafficTagId());
        appDo.setTrafficTagPid(appDto.getTrafficTagPid());
        return appDo;
    }
}

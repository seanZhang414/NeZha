package cn.com.duiba.nezha.engine.api.dto;

import java.io.Serializable;

/**
 * app参数
 *
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: AppDto.java , v 0.1 2017/6/8 下午6:55 ZhouFeng Exp $
 */
public class AppDto implements Serializable {
    private static final long serialVersionUID = -6117135769384442700L;

    private Long appId;

    /**
     * app类型
     */
    private String appCategory;

    /**
     * 广告位id
     */
    private Long slotId;

    /**
     * 广告位宽度
     */
    private Integer slotWidth;

    /**
     * 广告位高度
     */
    private Integer slotHeight;

    /**
     * 广告位类型
     */
    private Long slotType;

    /**
     * 广告位标签父ID
     */
    private String slotIndustryTagPid;

    /**
     * 广告位标签ID
     */
    private String slotIndustryTagId;

    /**
     * 媒体标签父ID
     */
    private String appIndustryTagPid;

    /**
     * 媒体标签ID
     */
    private String appIndustryTagId;

    /**
     * 流量标签ID
     */
    private String trafficTagId;

    /**
     * 流量标签父ID
     */
    private String trafficTagPid;


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

    public Long getAppId() {
        return appId;
    }

    public AppDto setAppId(Long appId) {
        this.appId = appId;
        return this;
    }

    public String getAppCategory() {
        return appCategory;
    }

    public AppDto setAppCategory(String appCategory) {
        this.appCategory = appCategory;
        return this;
    }


    public Long getSlotId() {
        return slotId;
    }

    public AppDto setSlotId(Long slotId) {
        this.slotId = slotId;
        return this;
    }

    public Long getSlotType() {
        return slotType;
    }

    public AppDto setSlotType(Long slotType) {
        this.slotType = slotType;
        return this;
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

    public String getAppIndustryTagPid() {
        return appIndustryTagPid;
    }

    public void setAppIndustryTagPid(String appIndustryTagPid) {
        this.appIndustryTagPid = appIndustryTagPid;
    }

    public String getAppIndustryTagId() {
        return appIndustryTagId;
    }

    public void setAppIndustryTagId(String appIndustryTagId) {
        this.appIndustryTagId = appIndustryTagId;
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
}

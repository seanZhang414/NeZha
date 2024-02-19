package cn.com.duiba.nezha.compute.biz.vo;

import cn.com.duiba.nezha.compute.api.dto.AdvertCtrStatDto;
import cn.com.duiba.nezha.compute.api.enums.AdvertStatDimTypeEnum;
import cn.com.duiba.nezha.compute.api.enums.StatIntervalTypeEnum;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class AdvertCtrStatVo implements Serializable {

    private String material;

    private String advertId;
    private String appId;


    private Long advertTimes;

    private String advertType;

    private String statDimId;

    private AdvertStatDimTypeEnum advertStatDimTypeEnum;

    private String advertStatDimType;

    private StatIntervalTypeEnum statIntervalTypeEnum;

    private String statIntervalId;

    private AdvertCtrStatDto advertCtrStatDto;


    public Long getAdvertTimes() {
        return advertTimes;
    }

    public void setAdvertTimes(Long advertTimes) {
        this.advertTimes = advertTimes;
    }


    public AdvertStatDimTypeEnum getAdvertStatDimTypeEnum() {
        return advertStatDimTypeEnum;
    }

    public void setAdvertStatDimTypeEnum(AdvertStatDimTypeEnum advertStatDimTypeEnum) {
        this.advertStatDimTypeEnum = advertStatDimTypeEnum;
    }

    public StatIntervalTypeEnum getStatIntervalTypeEnum() {
        return statIntervalTypeEnum;
    }

    public void setStatIntervalTypeEnum(StatIntervalTypeEnum statIntervalTypeEnum) {
        this.statIntervalTypeEnum = statIntervalTypeEnum;
    }


    


    public String getAdvertId() {
        return advertId;
    }

    public void setAdvertId(String advertId) {
        this.advertId = advertId;
    }


    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }



    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }


    public String getStatDimId() {
        return statDimId;
    }

    public void setStatDimId(String statDimID) {
        this.statDimId = statDimID;
    }

    public String getAdvertType() {
        return advertType;
    }

    public void setAdvertType(String advertType) {
        this.advertType = advertType;
    }


    public String getAdvertStatDimType() {
        return advertStatDimType;
    }

    public void setAdvertStatDimType(String advertStatDimType) {
        this.advertStatDimType = advertStatDimType;
    }


    public String getStatIntervalId() {
        return statIntervalId;
    }

    public void setStatIntervalId(String statIntervalId) {
        this.statIntervalId = statIntervalId;
    }



    public AdvertCtrStatDto getAdvertCtrStatDto() {
        return advertCtrStatDto;
    }

    public void setAdvertCtrStatDto(AdvertCtrStatDto advertCtrStatDto) {
        this.advertCtrStatDto = advertCtrStatDto;
    }


}

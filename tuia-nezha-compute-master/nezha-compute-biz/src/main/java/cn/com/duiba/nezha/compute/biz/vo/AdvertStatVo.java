package cn.com.duiba.nezha.compute.biz.vo;

import cn.com.duiba.nezha.compute.api.dto.AdvertAppStatDto;
import cn.com.duiba.nezha.compute.api.dto.AdvertCtrStatDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2016/11/16.
 */
public class AdvertStatVo implements Serializable {

    private String appId;

    private List<AdvertAppStatDto> advertAppStatDtoList;
    private List<AdvertCtrStatDto> advertCtrStatDtoList;


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }


    public void addAdvertAppStatDtoList(List<AdvertAppStatDto> list) {
        if (list != null) {
            getAdvertAppStatDtoList().addAll(list);
        }
    }

    public List<AdvertAppStatDto> getAdvertAppStatDtoList() {
        if (advertAppStatDtoList == null) {
            setAdvertAppStatDtoList(new ArrayList<AdvertAppStatDto>());
        }
        return advertAppStatDtoList;
    }

    public void setAdvertAppStatDtoList(List<AdvertAppStatDto> advertAppStatDtoList) {
        this.advertAppStatDtoList = advertAppStatDtoList;
    }


    public void addAdvertCtrStatDtoList(List<AdvertCtrStatDto> list) {
        if (list != null) {
            getAdvertCtrStatDtoList().addAll(list);
        }
    }

    public List<AdvertCtrStatDto> getAdvertCtrStatDtoList() {

        if (advertCtrStatDtoList == null) {
            setAdvertCtrStatDtoList(new ArrayList<AdvertCtrStatDto>());
        }
        return advertCtrStatDtoList;
    }

    public void setAdvertCtrStatDtoList(List<AdvertCtrStatDto> advertCtrStatDtoList) {
        this.advertCtrStatDtoList = advertCtrStatDtoList;
    }


}

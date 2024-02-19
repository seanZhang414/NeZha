package cn.com.duiba.nezha.compute.api.vo;

import cn.com.duiba.nezha.compute.api.dto.AdvertAppStatDto;
import cn.com.duiba.nezha.compute.api.dto.AdvertCtrStatDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pc on 2016/11/16.
 */
public class AdvertStatVo implements Serializable {

    private static final long serialVersionUID = -316102112618444133L;

    private AdvertAppStatDto advertAppStatDto;

    private List<AdvertCtrStatDto> advertCtrStatDtoList;

    private Double ctr;

    public AdvertAppStatDto getAdvertAppStatDto() {
        return advertAppStatDto;
    }

    public void setAdvertAppStatDto(AdvertAppStatDto advertAppStatDto) {
        this.advertAppStatDto = advertAppStatDto;
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

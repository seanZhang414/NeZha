package cn.com.duiba.nezha.compute.biz.vo;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class MapVo implements Serializable {
    private static final long serialVersionUID = -316102112618444133L;
    private Long times;
    private Long cityId;

    public Long getTimes() {return times;}

    public void setTimes(Long times) {this.times= times;}

    public Long getCityId() {return cityId;}
    public void setCityId(Long cityId) {this.cityId= cityId;}

}

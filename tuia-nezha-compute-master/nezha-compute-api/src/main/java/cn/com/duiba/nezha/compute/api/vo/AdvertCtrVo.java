package cn.com.duiba.nezha.compute.api.vo;

import cn.com.duiba.nezha.compute.api.dto.AdvertCtrStatDto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pc on 2016/11/16.
 */
public class AdvertCtrVo implements Serializable {

    private static final long serialVersionUID = -316102112618444133L;

    private Long advertId;
    private Double ctr;
    private String globalCdCtrKey; // 广告全局维度,首次投放,当日 CTR
    private String globalCdRepeatCtrKey; // 广告全局维度,重复投放,当日 CTR
    private String appCdCtrKey;// 广告媒体维度,首次投放,当日 CTR
    private String appCdRepeatCtrKey;// 广告媒体维度,重复投放,当日 CTR


    private String globalR7dCtrKey; // 广告全局维度,首次投放,近7日CTR
    private String globalR7dRepeatCtrKey; // 广告全局维度,重复投放,近7日CTR
    private String appR7dCtrKey;// 广告媒体维度,首次投放,近7日CTR
    private String appR7dRepeatCtrKey;// 广告媒体维度,重复投放,近7日CTR


    private Map<String, AdvertCtrStatDto> ctrMap;

    public Long getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Long advertId) {
        this.advertId = advertId;
    }

    private Map<String, AdvertCtrStatDto> getCtrMap() {
        return ctrMap;
    }

    public void setCtrMap(Map<String, AdvertCtrStatDto> ctrMap) {
        this.ctrMap = ctrMap;
    }


    public void addCtrMap(String key, AdvertCtrStatDto ctrDto) {
        if (this.ctrMap == null) {
            this.ctrMap = new HashMap<>();
        }
        this.ctrMap.put(key, ctrDto);
    }




    
    public String getGlobalCdCtrKey() {
        return globalCdCtrKey;
    }

    public void setGlobalCdCtrKey(String globalCdCtrKey) {
        this.globalCdCtrKey = globalCdCtrKey;
    }

    public String getGlobalCdRepeatCtrKey() {
        return globalCdRepeatCtrKey;
    }

    public void setGlobalCdRepeatCtrKey(String globalCdRepeatCtrKey) {
        this.globalCdRepeatCtrKey = globalCdRepeatCtrKey;
    }

    public String getAppCdCtrKey() {
        return appCdCtrKey;
    }

    public void setAppCdCtrKey(String appCdCtrKey) {
        this.appCdCtrKey = appCdCtrKey;
    }

    public String getAppCdRepeatCtrKey() {
        return appCdRepeatCtrKey;
    }

    public void setAppCdRepeatCtrKey(String appCdRepeatCtrKey) {
        this.appCdRepeatCtrKey = appCdRepeatCtrKey;
    }






    public String getGlobalR7dCtrKey() {
        return globalR7dCtrKey;
    }

    public void setGlobalR7dCtrKey(String globalR7dCtrKey) {
        this.globalR7dCtrKey = globalR7dCtrKey;
    }

    public String getGlobalR7dRepeatCtrKey() {
        return globalR7dRepeatCtrKey;
    }

    public void setGlobalR7dRepeatCtrKey(String globalR7dRepeatCtrKey) {
        this.globalR7dRepeatCtrKey = globalR7dRepeatCtrKey;
    }

    public String getAppR7dCtrKey() {
        return appR7dCtrKey;
    }

    public void setAppR7dCtrKey(String appR7dCtrKey) {
        this.appR7dCtrKey = appR7dCtrKey;
    }

    public String getAppR7dRepeatCtrKey() {
        return appR7dRepeatCtrKey;
    }

    public void setAppR7dRepeatCtrKey(String appR7dRepeatCtrKey) {
        this.appR7dRepeatCtrKey = appR7dRepeatCtrKey;
    }



}

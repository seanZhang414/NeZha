package cn.com.duiba.nezha.compute.common.model.pacing;

import java.io.Serializable;

/**
 * Created by jiali on 2018/2/6.
 */
public class AdInfo
{
    Long advertId;
    Long appId;
    Type  type; 		//流量描述符
    Long orientId; 		//配置包id
    Long target = 0l;		//ocpc广告的目标成本
    Long fee;
    Double factor;		//ocpc调价因子
    Double orientCost = 0.0;        //配置消耗
    AutoMatchInfo autoMatchInfo;
    int quailityLevel = 0; //0 = 默认 1,2,3=黑名单 8 = 白名单  16=熔断


    public class Type implements Serializable {
        Integer chargeType;   //1=cpc 2=ocpc
        Integer pid;          //000 = 默认  010 = 娃娃机  100 = 智能匹配
        Integer packageType;    //默认 = 0000   用户点击感兴趣转化不感兴趣 1001

        public void setChargeType(Integer chargeType) {
            this.chargeType = chargeType;
        }

        public void setPackageType(Integer packageType) {
            this.packageType = packageType;
        }

        public void setPid(Integer pid) {
            this.pid = pid;
        }
    }

    public void setQuailityLevel(int quailityLevel) {
        this.quailityLevel = quailityLevel;
    }

    public int getQuailityLevel() {
        return quailityLevel;
    }

    public void setAdvertId(Long advertId) {
        this.advertId = advertId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public void setOrientId(Long orientId) {
        this.orientId = orientId;
    }

    public Long getOrientId() {
        return orientId;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Long getTarget() {
        return target;
    }

    public void setTarget(Long target) {
        this.target = target;
    }

    public void setFactor(Double factor) {
        this.factor = factor;
    }

    public void setFee(Long fee) {
        this.fee = fee;
    }

    public Double getFactor() {
        return factor;
    }

    public Long getAdvertId() {
        return advertId;
    }

    public Long getAppId() {
        return appId;
    }

    public Long getFee() {
        return fee;
    }

    public Type getType() {
        return new Type();
    }

    public AutoMatchInfo getAutoMatchInfo() {
        return autoMatchInfo;
    }

    public Double getOrientCost() {
        return orientCost;
    }

    public void setOrientCost(Double orientCost) {
        this.orientCost = orientCost;
    }

    public void setAutoMatchInfo(AutoMatchInfo autoMatchInfo) {
        this.autoMatchInfo = autoMatchInfo;
    }
}

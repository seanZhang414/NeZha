package cn.com.duiba.nezha.compute.api.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created by pc on 2016/11/16.
 */
public class FeatureDto implements Serializable {
    private static final long serialVersionUID = -316102112618444133L;
    private Long consumerId;


    private Long advertId;  //f.请求 f101000-f101001

    private String matchTagNums;  //f.请求 f102000-f102001
    private String tagNumCategory;  //f.请求 f103000-f103001
    private String materialId; // f.请求 f104000-f104001
    private Long accountId;  //f.请求 f106000-f106001
    private Long slotId;  //f.请求 f108000-f108001
    private Long slotType;  //f.请求 f109000-f109001
    private Long times;  //f.请求 f110000-f110001
    private String promoteUrl;  //f.请求 f111000-f111001
    private String materialTags; // f.请求 f112000-f112001
    private String advertTags; // f.请求 f113000-f113001

    private Long appId; //f.请求 f201000-f201001
    private String appCategory; //f.请求 f202000-f202001
    private String appCategory2; //f.请求 f203000-f203001
    private String developerId; //f.请求 f204000-f204001

    private Long operatingActivityId; //f.请求 f301000-f301001
    private Long activityId; //f.请求 f302000-f302001
    private Long activityType; //f.请求 f303000-f303001
    private String activitySubType; //f.请求 f305000-f305001
    private String activityUseType; //f.请求 f306000-f306001
    private String sourceId; //f.请求 f307000-f307001
    private String sourceType; //f.请求 f308000-f308001
    private String appBannerId; //f.请求 f309000-f309001


    private String memberId; //f.请求 f403000-f403001
    private String province; //f.请求 f403002-f403002
    private String city; //f.请求 f403003-f403003
    private String mobile; //f.请求 f403005-f403005


    private String ua; //f.请求 f501000-f501001
    private String currentGmtCreateTime; //f.请求 f502000-f502001 f502002
    private Long cityId; //f.请求 f503001
    private String provinceCode; //f.请求 f503003
    private String model; //f.请求 f504000-f504001
    private String priceSection; //f.请求 f505000-f505001
    private String connectionType; //f.请求 f506000-f506001
    private String operatorType; //f.请求 f507000-f507001


    private Long dayOrderRank; //f.实时 f601000-f601001
    private Long orderRank; // f602000-f602001
    private Long dayActivityOrderRank; // f603000-f603001
    private Long activityOrderRank; // f604000-f604001
    private String activityLastGmtCreateTime; // f605000-f605001
    private String lastGmtCreateTime; // f606000-f606001
    private Long activityLastChargeNums; // f607000-f607001
    private Long lastChargeNums; // f608000-f608001
    private Long lastOperatingActivityId; // f609000-f609001
    private String dayLastMatchTagNums; // f610000-f610001
    private Long putIndex; // f611000-f611001


    private String goodsId; //f.请求 f801001-f801001
    private String catId; //f.请求 f802001-802001
    private String brandId; //f.请求 f802002-f802002
    private Long cost; //f.请求 f803001-803001
    private Long price; //f.请求 f803002-f803002
    private Long viewCount; //f.请求 f803003-f803003
    private Long buyCount; //f.请求 f803004-f803004


    private String shipArea; //
    private Date userLastlogbigintime; //  f403004-f403004
    private Date userRegtime; //


    private Double advertCtr; // f804001-f804001
    private Double advertCvr; // f804002-f804002


    private Double advertAppCtr; // f805001-f805001
    private Double advertAppCvr; // f805001-f805001

    private Double advertSlotCtr; // f806001-f806001
    private Double advertSlotCvr; // f806001-f806001


    private Double advertActivityCtr; // f807001-f807001
    private Double advertActivityCvr; // f807001-f807001

    private String slotIndustryTagPid; // f114000-f114001
    private String slotIndustryTagId; // f114000-f114002

    private String appIndustryTagPid; // f205000-f205001
    private String appIndustryTagId; // f205000-f205002

    private String trafficTagPid; // f206000-f206001
    private String trafficTagId; // f206000-f206002

    private String appList2; // f9902
    private String categoryIdList1; // f9906  app一级分类ID列表
    private String categoryIdList2; // f9907  app二级分类ID列表
    private String isGame; // f9908 是否游戏
    private String tradeId; // f9912 广告行业标签

    private String phoneBrand;// f508001
    private String phoneModelNum;//f508002手机机型
    private String tradeId2;//f9913 新行业ID


    private String importantApp;//f9914
    private String clusterId;//f9915


    private String uIIds;//
    private String uILaunchPV;//f9916
    private String uIClickPv;//f9917
    private String uIEffectPv;//f9918
    private String uIScore;//f9919

    private String uICtr;//ctr_intersred:用户ctr感兴趣的行业id，逗号分隔 f9921
    private String uICvr;//cvr_intereste 用户cvr感兴趣的行业id，逗号分隔 f9922
    private String uUnICtr;//ctr_unintersred用户ctr不感兴趣的行业id，逗号分隔 f9923
    private String uUnICvr;//ctr_unintersred用户ctr不感兴趣的行业id，逗号分隔 f9924


    private String sex;// string  '性别'  "f404001"
    private String age;// string  '年龄'  "f404002"
    private String workStatus;// string  '工作状态'  "f404003"
    private String studentStatus;// string  '学历状态'  "f404004"
    private String marriageStatus;// string  '婚姻状态'  "f404005"
    private String bear;//string  '生育状态'  "f404006"
    private String interestList;// string  兴趣爱好列表  "f404007"

    private String bankEndType;//:1安装APP,2启动APP,3注册账户,4激活账户,5登录账户,6用户付费,7用户进件,8用户完件



    private Map<String,Long> category1idCntList; // f9925
    private Map<String,Long> category2idCntList; // f9926


    private Map<String, Map<String, Long>> ubpMap ;



    public Long getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(Long consumerId) {
        this.consumerId = consumerId;
    }


    public Long getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Long advertId) {
        this.advertId = advertId;
    }

    public String getMatchTagNums() {
        return matchTagNums;
    }

    public void setMatchTagNums(String matchTagNums) {
        this.matchTagNums = matchTagNums;
    }

    public String getTagNumCategory() {
        return tagNumCategory;
    }

    public void setTagNumCategory(String tagNumCategory) {
        this.tagNumCategory = tagNumCategory;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public Long getTimes() {
        return times;
    }

    public void setTimes(Long times) {
        this.times = times;
    }

    public String getPromoteUrl() {
        return promoteUrl;
    }

    public void setPromoteUrl(String promoteUrl) {
        this.promoteUrl = promoteUrl;
    }


    public Long getSlotType() {
        return slotType;
    }

    public void setSlotType(Long slotType) {
        this.slotType = slotType;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getMaterialTags() {
        return materialTags;
    }

    public void setMaterialTags(String materialTags) {
        this.materialTags = materialTags;
    }

    public String getAdvertTags() {
        return advertTags;
    }

    public void setAdvertTags(String advertTags) {
        this.advertTags = advertTags;
    }


    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }


    public String getAppCategory() {
        return appCategory;
    }

    public void setAppCategory(String appCategory) {
        this.appCategory = appCategory;
    }

    public String getAppCategory2() {
        return appCategory2;
    }

    public void setAppCategory2(String appCategory2) {
        this.appCategory2 = appCategory2;
    }

    public String getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(String developerId) {
        this.developerId = developerId;
    }


    public Long getOperatingActivityId() {
        return operatingActivityId;
    }

    public void setOperatingActivityId(Long operatingActivityId) {
        this.operatingActivityId = operatingActivityId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getActivityType() {
        return activityType;
    }

    public void setActivityType(Long activityType) {
        this.activityType = activityType;
    }

    public String getActivitySubType() {
        return activitySubType;
    }

    public void setActivitySubType(String activitySubType) {
        this.activitySubType = activitySubType;
    }

    public String getActivityUseType() {
        return activityUseType;
    }

    public void setActivityUseType(String activityUseType) {
        this.activityUseType = activityUseType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getAppBannerId() {
        return appBannerId;
    }

    public void setAppBannerId(String appBannerId) {
        this.appBannerId = appBannerId;
    }


    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public String getCurrentGmtCreateTime() {
        return currentGmtCreateTime;
    }

    public void setCurrentGmtCreateTime(String currentGmtCreateTime) {
        this.currentGmtCreateTime = currentGmtCreateTime;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }


    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }


    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPriceSection() {
        return priceSection;
    }

    public void setPriceSection(String priceSection) {
        this.priceSection = priceSection;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public String getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(String operatorType) {
        this.operatorType = operatorType;
    }


    public Long getDayOrderRank() {
        return dayOrderRank;
    }

    public void setDayOrderRank(Long dayOrderRank) {
        this.dayOrderRank = dayOrderRank;
    }

    public Long getOrderRank() {
        return orderRank;
    }

    public void setOrderRank(Long orderRank) {
        this.orderRank = orderRank;
    }

    public Long getDayActivityOrderRank() {
        return dayActivityOrderRank;
    }

    public void setDayActivityOrderRank(Long dayActivityOrderRank) {
        this.dayActivityOrderRank = dayActivityOrderRank;
    }

    public Long getActivityOrderRank() {
        return activityOrderRank;
    }

    public void setActivityOrderRank(Long activityOrderRank) {
        this.activityOrderRank = activityOrderRank;
    }

    public String getActivityLastGmtCreateTime() {
        return activityLastGmtCreateTime;
    }

    public void setActivityLastGmtCreateTime(String activityLastGmtCreateTime) {
        this.activityLastGmtCreateTime = activityLastGmtCreateTime;
    }

    public String getLastGmtCreateTime() {
        return lastGmtCreateTime;
    }

    public void setLastGmtCreateTime(String lastGmtCreateTime) {
        this.lastGmtCreateTime = lastGmtCreateTime;
    }


    public Long getActivityLastChargeNums() {
        return activityLastChargeNums;
    }

    public void setActivityLastChargeNums(Long activityLastChargeNums) {
        this.activityLastChargeNums = activityLastChargeNums;
    }

    public Long getLastChargeNums() {
        return lastChargeNums;
    }

    public void setLastChargeNums(Long lastChargeNums) {
        this.lastChargeNums = lastChargeNums;
    }

    public Long getLastOperatingActivityId() {
        return lastOperatingActivityId;
    }

    public void setLastOperatingActivityId(Long lastOperatingActivityId) {
        this.lastOperatingActivityId = lastOperatingActivityId;
    }


    public String getDayLastMatchTagNums() {
        return dayLastMatchTagNums;
    }

    public void setDayLastMatchTagNums(String dayLastMatchTagNums) {
        this.dayLastMatchTagNums = dayLastMatchTagNums;
    }


    public Long getPutIndex() {
        return putIndex;
    }

    public void setPutIndex(Long putIndex) {
        this.putIndex = putIndex;
    }


    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Long getBuyCount() {
        return buyCount;
    }

    public void setBuyCount(Long buyCount) {
        this.buyCount = buyCount;
    }


    public String getShipArea() {
        return shipArea;
    }

    public void setShipArea(String shipArea) {
        this.shipArea = shipArea;
    }

    public Date getUserLastlogbigintime() {
        return userLastlogbigintime;
    }

    public void setUserLastlogbigintime(Date userLastlogbigintime) {
        this.userLastlogbigintime = userLastlogbigintime;
    }

    public Date getUserRegtime() {
        return userRegtime;
    }

    public void setUserRegtime(Date userRegtime) {
        this.userRegtime = userRegtime;
    }


    public Double getAdvertCtr() {
        return advertCtr;
    }

    public void setAdvertCtr(Double advertCtr) {
        this.advertCtr = advertCtr;
    }

    public Double getAdvertCvr() {
        return advertCvr;
    }

    public void setAdvertCvr(Double advertCvr) {
        this.advertCvr = advertCvr;
    }


    public Double getAdvertSlotCtr() {
        return advertSlotCtr;
    }

    public void setAdvertSlotCtr(Double advertSlotCtr) {
        this.advertSlotCtr = advertSlotCtr;
    }

    public Double getAdvertSlotCvr() {
        return advertSlotCvr;
    }

    public void setAdvertSlotCvr(Double advertSlotCvr) {
        this.advertSlotCvr = advertSlotCvr;
    }


    public Double getAdvertAppCtr() {
        return advertAppCtr;
    }

    public void setAdvertAppCtr(Double advertAppCtr) {
        this.advertAppCtr = advertAppCtr;
    }

    public Double getAdvertAppCvr() {
        return advertAppCvr;
    }

    public void setAdvertAppCvr(Double advertAppCvr) {
        this.advertAppCvr = advertAppCvr;
    }


    public Double getAdvertActivityCtr() {
        return advertActivityCtr;
    }

    public void setAdvertActivityCtr(Double advertActivityCtr) {
        this.advertActivityCtr = advertActivityCtr;
    }

    public Double getAdvertActivityCvr() {
        return advertActivityCvr;
    }

    public void setAdvertActivityCvr(Double advertActivityCvr) {
        this.advertActivityCvr = advertActivityCvr;
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

    public String getTrafficTagPid() {
        return trafficTagPid;
    }

    public void setTrafficTagPid(String trafficTagPid) {
        this.trafficTagPid = trafficTagPid;
    }


    public String getTrafficTagId() {
        return trafficTagId;
    }

    public void setTrafficTagId(String trafficTagId) {
        this.trafficTagId = trafficTagId;
    }


    public String getAppList2() {
        return appList2;
    }

    public void setAppList2(String appList2) {
        this.appList2 = appList2;
    }


    public String getCategoryIdList1() {
        return categoryIdList1;
    }

    public void setCategoryIdList1(String categoryIdList1) {
        this.categoryIdList1 = categoryIdList1;
    }

    public String getCategoryIdList2() {
        return categoryIdList2;
    }

    public void setCategoryIdList2(String categoryIdList2) {
        this.categoryIdList2 = categoryIdList2;
    }

    public String getIsGame() {
        return isGame;
    }

    public void setIsGame(String isGame) {
        this.isGame = isGame;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }


    public String getPhoneBrand() {
        return phoneBrand;
    }

    public void setPhoneBrand(String phoneBrand) {
        this.phoneBrand = phoneBrand;
    }


    public String getPhoneModelNum() {
        return phoneModelNum;
    }

    public void setPhoneModelNum(String phoneModelNum) {
        this.phoneModelNum = phoneModelNum;
    }


    public String getTradeId2() {
        return tradeId2;
    }

    public void setTradeId2(String tradeId2) {
        this.tradeId2 = tradeId2;
    }


    public String getImportantApp() {
        return importantApp;
    }

    public void setImportantApp(String importantApp) {
        this.importantApp = importantApp;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }


    public String getUIIds() {
        return uIIds;
    }

    public void setUIIds(String uIIds) {
        this.uIIds = uIIds;
    }


    public String getUILaunchPV() {
        return uILaunchPV;
    }

    public void setUILaunchPV(String uILaunchPV) {
        this.uILaunchPV = uILaunchPV;
    }

    public String getUIClickPv() {
        return uIClickPv;
    }

    public void setUIClickPv(String uIClickPv) {
        this.uIClickPv = uIClickPv;
    }


    public String getUIEffectPv() {
        return uIEffectPv;
    }

    public void setUIEffectPv(String uIEffectPv) {
        this.uIEffectPv = uIEffectPv;
    }


    public String getUIScore() {
        return uIScore;
    }

    public void setUIScore(String uIScore) {
        this.uIScore = uIScore;
    }


    public String getUICtr() {
        return uICtr;
    }

    public void setUICtr(String uICtr) {
        this.uICtr = uICtr;
    }


    public String getUICvr() {
        return uICvr;
    }

    public void setUICvr(String uICvr) {
        this.uICvr = uICvr;
    }


    public String getUUnICtr() {
        return uUnICtr;
    }

    public void setUUnICtr(String uUnICtr) {
        this.uUnICtr = uUnICtr;
    }


    public String getUUnICvr() {
        return uUnICvr;
}

    public void setUUnICvr(String uUnICvr) {
        this.uUnICvr = uUnICvr;
    }


    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }


    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }


    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }


    public String getStudentStatus() {
        return studentStatus;
    }

    public void setStudentStatus(String studentStatus) {
        this.studentStatus= studentStatus;
    }


    public String getMarriageStatus() {
        return marriageStatus;
    }

    public void setMarriageStatus(String marriageStatus) {
        this.marriageStatus = marriageStatus;
    }

  public String getBear() {
        return bear;
    }

    public void setBear(String bear) {
        this.bear = bear;
    }


    public String getInterestList() {
        return interestList;
    }

    public void setInterestList(String interestList) {
        this.interestList = interestList;
    }

    public String getBankEndType() {
        return bankEndType;
    }

    public void setBankEndType(String bankEndType) {
        this.bankEndType = bankEndType;
    }

    public Map<String,Long> getCategory1idCntList() {return category1idCntList;}
    public void setCategory1idCntList(Map<String,Long> category1idCntList) {this.category1idCntList = category1idCntList;}

    public Map<String,Long> getCategory2idCntList() {return category2idCntList;}
    public void setCategory2idCntList(Map<String,Long> category2idCntList) {this.category2idCntList = category2idCntList;}

    public Map<String, Map<String, Long>> getUbpMap() {return ubpMap;}
    public void setUbpMap(Map<String, Map<String, Long>> ubpMap) {this.ubpMap = ubpMap;}

}

package cn.com.duiba.nezha.compute.biz.log;

import java.io.Serializable;

/**
 * Created by pc on 2016/11/16.
 */
public class UserInfoLandingPageLog implements Serializable {
    private String land_id;//	落地页ID
    private String channel;//	接口渠道
    private String consumer_id;//	用户id
    private String app_id;//	appid
    private String app_name;//	app名称
    private String advert_id;//	广告ID
    private String activity_id;//	活动ID
    private String advert_plan_id;//	广告计划ID
    private String advert_media_id;//	广告媒体ID
    private String user_name;//	用户名
    private String user_phone;//	用户手机号
    private String identification;//	身份证号
    private String sex;//	性别
    private String address;//	住址
    private String birthday;//	生日
    private String region;//	地区
    private String city;//	城市
    private String province;//	省
    private String phone_city;//	手机归属城市
    private String phone_province;//	手机归属省
    private String info;//	其他信息json

    public String getLand_id() {return land_id;}

    public void setLand_id(String land_id) {this.land_id = land_id;}

    public String getChannel() {return channel;}

    public void setChannel(String channel) {this.channel = channel;}

    public String getConsumer_id() {return consumer_id;}

    public void setConsumer_id(String consumer_id) {this.consumer_id = consumer_id;}

    public String getApp_id() {return app_id;}

    public void setApp_id(String app_id) {this.app_id = app_id;}

    public String getApp_name() {return app_name;}

    public void setApp_name(String app_name) {this.app_name = app_name;}

    public String getAdvert_id() {return advert_id;}

    public void setAdvert_id(String advert_id) {this.advert_id = advert_id;}

    public String getActivity_id() {return activity_id;}

    public void setActivity_id(String activity_id) {this.activity_id = activity_id;}

    public String getAdvert_plan_id() {return advert_plan_id;}

    public void setAdvert_plan_id(String advert_plan_id) {this.advert_plan_id = advert_plan_id;}

    public String getAdvert_media_id() {return advert_media_id;}

    public void setAdvert_media_id(String advert_media_id) {this.advert_media_id = advert_media_id;}

    public String getUser_name() {return user_name;}

    public void setUser_name(String user_name) {this.user_name = user_name;}

    public String getUser_phone() {return user_phone;}

    public void setUser_phone(String user_phone) {this.user_phone = user_phone;}

    public String getIdentification() {return identification;}

    public void setIdentification(String identification) {this.identification = identification;}

    public String getSex() {return sex;}

    public void setSex(String sex) {this.sex = sex;}

    public String getAddress() {return address;}

    public void setAddress(String address) {this.address = address;}

    public String getBirthday() {return birthday;}

    public void setBirthday(String birthday) {this.birthday = birthday;}

    public String getRegion() {return region;}

    public void setRegion(String region) {this.region = region;}

    public String getCity() {return city;}

    public void setCity(String city) {this.city = city;}

    public String getProvince() {return province;}

    public void setProvince(String province) {this.province = province;}

    public String getPhone_city() {return phone_city;}

    public void setPhone_city(String phone_city) {this.phone_city = phone_city;}

    public String getPhone_province() {return phone_province;}

    public void setPhone_province(String phone_province) {this.phone_province = phone_province;}

    public String getInfo() {return info;}

    public void setInfo(String info) {this.info = info;}

}

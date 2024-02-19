package cn.com.duiba.nezha.engine.biz.domain;

import cn.com.duiba.nezha.engine.api.dto.ConsumerDto;
import cn.com.duiba.nezha.engine.api.dto.TagStat;

import java.util.Date;
import java.util.List;

public class ConsumerDo {

    // 用户id
    private Long id;

    // 会员id
    private Long memberId;

    // 地址
    private String shipArea;

    // 最后登陆时间
    private Date lastLoginTime;

    // 注册时间
    private Date registerTime;

    // 手机号
    private String phoneNumber;

    // 用户安装app列表
    private List<String> installApps;

    // 标签统计数
    private List<TagStat> tagStats;

    // 点击感兴趣标签列表,用逗号分隔
    private String clickInterestedTags;

    // 转化感兴趣标签列表,用逗号分隔
    private String convertInterestedTags;

    // 点击不感兴趣标签列表,用逗号分隔
    private String clickUninterestedTags;

    // 转化不感兴趣标签列表,用逗号分隔
    private String convertUninterestedTags;

    // 性别
    private String sex;

    // 年龄
    private String age;

    // 工作状态
    private String workStatus;

    // 学历状态
    private String studentStatus;

    // 婚姻状态
    private String marriageStatus;

    // 生育状态
    private String bear;

    // 兴趣列表
    private List<String> interestedList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getShipArea() {
        return shipArea;
    }

    public void setShipArea(String shipArea) {
        this.shipArea = shipArea;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public List<String> getInstallApps() {
        return installApps;
    }

    public void setInstallApps(List<String> installApps) {
        this.installApps = installApps;
    }

    public List<TagStat> getTagStats() {
        return tagStats;
    }

    public void setTagStats(List<TagStat> tagStats) {
        this.tagStats = tagStats;
    }

    public String getClickInterestedTags() {
        return clickInterestedTags;
    }

    public void setClickInterestedTags(String clickInterestedTags) {
        this.clickInterestedTags = clickInterestedTags;
    }

    public String getConvertInterestedTags() {
        return convertInterestedTags;
    }

    public void setConvertInterestedTags(String convertInterestedTags) {
        this.convertInterestedTags = convertInterestedTags;
    }

    public String getClickUninterestedTags() {
        return clickUninterestedTags;
    }

    public void setClickUninterestedTags(String clickUninterestedTags) {
        this.clickUninterestedTags = clickUninterestedTags;
    }

    public String getConvertUninterestedTags() {
        return convertUninterestedTags;
    }

    public void setConvertUninterestedTags(String convertUninterestedTags) {
        this.convertUninterestedTags = convertUninterestedTags;
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
        this.studentStatus = studentStatus;
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

    public List<String> getInterestedList() {
        return interestedList;
    }

    public void setInterestedList(List<String> interestedList) {
        this.interestedList = interestedList;
    }

    public static ConsumerDo convert(ConsumerDto consumerDto) {
        ConsumerDo consumerDo = new ConsumerDo();
        consumerDo.setId(consumerDto.getConsumerId());
        consumerDo.setMemberId(consumerDto.getMemberId());
        consumerDo.setShipArea(consumerDto.getShipArea());
        consumerDo.setLastLoginTime(consumerDto.getUserLastlogbigintime());
        consumerDo.setRegisterTime(consumerDto.getUserRegtime());
        consumerDo.setPhoneNumber(consumerDto.getMobileprivate());
        consumerDo.setInstallApps(consumerDto.getInstallApps());
        consumerDo.setTagStats(consumerDto.getTagStats());
        consumerDo.setClickInterestedTags(consumerDto.getClickIntersredTags());
        consumerDo.setConvertInterestedTags(consumerDto.getConverIntersredTags());
        consumerDo.setClickUninterestedTags(consumerDto.getClickUnintersredTags());
        consumerDo.setConvertUninterestedTags(consumerDto.getConverUnintersredTags());
        consumerDo.setSex(consumerDto.getSex());
        consumerDo.setAge(consumerDto.getAge());
        consumerDo.setWorkStatus(consumerDto.getWorkStatus());
        consumerDo.setStudentStatus(consumerDto.getStudentStatus());
        consumerDo.setMarriageStatus(consumerDto.getMarriageStatus());
        consumerDo.setBear(consumerDto.getBear());
        consumerDo.setInterestedList(consumerDto.getIntersetList());
        return consumerDo;
    }
}

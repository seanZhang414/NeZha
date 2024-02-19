package cn.com.duiba.nezha.engine.api.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户信息
 *
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: ConsumerDto.java , v 0.1 2017/6/8 下午6:54 ZhouFeng Exp $
 */
public class ConsumerDto implements Serializable {
    private static final long serialVersionUID = -3671574726638721514L;

    /**
     * 用户id
     */
    private Long consumerId;

    /**
     * 会员id
     */
    private Long memberId;

    /**
     * 地址
     */
    private String shipArea;

    /**
     * 最后登陆时间
     */
    private Date userLastlogbigintime;

    /**
     * 注册时间
     */
    private Date userRegtime;

    /**
     * 手机号
     */
    private String mobileprivate;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 用户安装app列表
     */
    private List<String> installApps;

    /**
     * 标签统计数
     */
    private List<TagStat> tagStats;

    /**
     * 点击感兴趣标签列表,用逗号分隔
     */
    private String clickIntersredTags;

    /**
     * 转化感兴趣标签列表,用逗号分隔
     */
    private String converIntersredTags;

    /**
     * 点击不感兴趣标签列表,用逗号分隔
     */
    private String clickUnintersredTags;

    /**
     * 转化不感兴趣标签列表,用逗号分隔
     */
    private String converUnintersredTags;

    /**
     * 性别
     */
    private String sex;
    /**
     * 年龄
     */
    private String age;

    /**
     * 工作状态
     */
    private String workStatus;

    /**
     * 学历状态
     */
    private String studentStatus;

    /**
     * 婚姻状态
     */
    private String marriageStatus;

    /**
     * 生育状态
     */
    private String bear;

    /**
     * 兴趣列表
     */
    private List<String> intersetList;


    public Long getConsumerId() {
        return consumerId;
    }

    public ConsumerDto setConsumerId(Long consumerId) {
        this.consumerId = consumerId;
        return this;
    }

    public Long getMemberId() {
        return memberId;
    }

    public ConsumerDto setMemberId(Long memberId) {
        this.memberId = memberId;
        return this;
    }

    public String getShipArea() {
        return shipArea;
    }

    public ConsumerDto setShipArea(String shipArea) {
        this.shipArea = shipArea;
        return this;
    }

    public Date getUserLastlogbigintime() {
        return userLastlogbigintime;
    }

    public ConsumerDto setUserLastlogbigintime(Date userLastlogbigintime) {
        this.userLastlogbigintime = userLastlogbigintime;
        return this;
    }

    public Date getUserRegtime() {
        return userRegtime;
    }

    public ConsumerDto setUserRegtime(Date userRegtime) {
        this.userRegtime = userRegtime;
        return this;
    }

    public String getMobileprivate() {
        return mobileprivate;
    }

    public ConsumerDto setMobileprivate(String mobileprivate) {
        this.mobileprivate = mobileprivate;
        return this;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public List<String> getInstallApps() {
        return installApps;
    }

    public void setInstallApps(List<String> installApps) {
        this.installApps = installApps;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public List<TagStat> getTagStats() {
        return tagStats;
    }

    public void setTagStats(List<TagStat> tagStats) {
        this.tagStats = tagStats;
    }

    public String getClickIntersredTags() {
        return clickIntersredTags;
    }

    public void setClickIntersredTags(String clickIntersredTags) {
        this.clickIntersredTags = clickIntersredTags;
    }

    public String getConverIntersredTags() {
        return converIntersredTags;
    }

    public void setConverIntersredTags(String converIntersredTags) {
        this.converIntersredTags = converIntersredTags;
    }

    public String getClickUnintersredTags() {
        return clickUnintersredTags;
    }

    public void setClickUnintersredTags(String clickUnintersredTags) {
        this.clickUnintersredTags = clickUnintersredTags;
    }

    public String getConverUnintersredTags() {
        return converUnintersredTags;
    }

    public void setConverUnintersredTags(String converUnintersredTags) {
        this.converUnintersredTags = converUnintersredTags;
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

    public List<String> getIntersetList() {
        return intersetList;
    }

    public void setIntersetList(List<String> intersetList) {
        this.intersetList = intersetList;
    }
}

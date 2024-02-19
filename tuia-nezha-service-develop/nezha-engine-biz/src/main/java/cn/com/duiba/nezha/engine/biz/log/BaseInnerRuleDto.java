package cn.com.duiba.nezha.engine.biz.log;

import java.io.Serializable;

public class BaseInnerRuleDto implements Serializable {

    private static final long serialVersionUID = 5308607633188780089L;

    /**
     * 分组id
     */
    private String group;

    /**
     * 分组子类型
     */
    private String type;

    /**
     * 时间
     */
    private String time;

    /**
     * 位置信息
     */
    private String dpm;

    /**
     * 非必填 内容信息
     */
    private String dcm;

    /**
     * 非必填 JSON格式，内容根据group和type自定义
     */
    private String json;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDpm() {
        return dpm;
    }

    public void setDpm(String dpm) {
        this.dpm = dpm;
    }

    public String getDcm() {
        return dcm;
    }

    public void setDcm(String dcm) {
        this.dcm = dcm;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

}
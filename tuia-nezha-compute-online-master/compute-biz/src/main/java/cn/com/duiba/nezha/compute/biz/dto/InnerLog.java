package cn.com.duiba.nezha.compute.biz.dto;

import java.io.Serializable;

public class InnerLog implements Serializable {
    private String group;
    private String type;
    private String time;
    private String json;


    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }


    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}

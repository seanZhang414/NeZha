package cn.com.duiba.tuia.engine.activity.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * InnerLog
 */
public class InnerLog2 {

    private static Logger logger = LoggerFactory.getLogger(InnerLog.class);

    private String group;                                           // 必填 大类分组
    private String type;                                            // 必填 小类分组
    private String time;                                            // 必填 yyyy-MM-dd HH:mm:ss
    private String dpm;                                             // 非必填 位置信息
    private String dcm;                                             // 非必填 内容信息
    private JSONObject json;                                            // 非必填 JSON格式，内容根据group和type自定义

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

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }


    /**
     * log
     *
     * @param type
     * @param data
     */
    public static void log(String type, String data) {
        if (data == null) {
            return;
        }

        InnerLog2 innerlog = new InnerLog2();
        innerlog.setGroup("1"); //1 (广告平台业务)
        innerlog.setType(type);
        innerlog.setJson(JSONObject.parseObject(data));
        innerlog.setTime((new DateTime()).toString("yyyy-MM-dd HH:mm:ss"));
        String log = JSON.toJSONString(innerlog);
        logger.info(log);
    }
}

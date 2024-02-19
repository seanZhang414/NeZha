package cn.com.duiba.tuia.engine.activity.tongdun;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FraudApiResponse
 */
public class FraudApiResponse implements Serializable {

    private static final long             serialVersionUID = 4152462611121573434L;
    private Boolean                       success          = false;               // 执行是否成功，不成功时对应reason_code
    private String                        reason_code;                            // NOSONAR
    // 错误码及原因描述，正常执行完扫描时为空
    private Integer                       final_score;                            // NOSONAR
    // 风险分数
    private String                        final_decision;                         // NOSONAR
    // 最终的风险决策结果
    private String                        policy_name;                            // NOSONAR
    // 策略名称
    private List<HitRule>                 hit_rules        = new ArrayList<>();   // NOSONAR
    // 命中规则列表
    private String                        seq_id;                                 // NOSONAR
    // 请求序列号，每个请求进来都分配一个全局唯一的id
    private Integer                       spend_time;                             // NOSONAR
    // 花费的时间，单位ms
    private Map<String, String>           geoip_info       = new HashMap<>();     // NOSONAR
    // 地理位置信息
    private transient Map<String, Object> device_info      = new HashMap<>();     // NOSONAR
    // 设备指纹信息
    private transient Map<String, Object> attribution      = new HashMap<>();
    // 归属地信息
    private List<Policy>                  policy_set       = new ArrayList<>();   // NOSONAR
    // 策略集信息
    private String                        policy_set_name;                        // NOSONAR
    // 策略集名称
    private String                        risk_type;                              // NOSONAR
    // 风险类型

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getReason_code() {// NOSONAR
        return reason_code;
    }

    public void setReason_code(String reason_code) {// NOSONAR
        this.reason_code = reason_code;
    }

    public Integer getFinal_score() {// NOSONAR
        return final_score;
    }

    public void setFinal_score(Integer final_score) {// NOSONAR
        this.final_score = final_score;
    }

    public String getFinal_decision() {// NOSONAR
        return final_decision;
    }

    public void setFinal_decision(String final_decision) {// NOSONAR
        this.final_decision = final_decision;
    }

    public String getPolicy_name() {// NOSONAR
        return policy_name;
    }

    public void setPolicy_name(String policy_name) {// NOSONAR
        this.policy_name = policy_name;
    }

    public List<HitRule> getHit_rules() {// NOSONAR
        return hit_rules;
    }

    public void setHit_rules(List<HitRule> hit_rules) {// NOSONAR
        this.hit_rules = hit_rules;
    }

    public String getSeq_id() {// NOSONAR
        return seq_id;
    }

    public void setSeq_id(String seq_id) {// NOSONAR
        this.seq_id = seq_id;
    }

    public Integer getSpend_time() {// NOSONAR
        return spend_time;
    }

    public void setSpend_time(Integer spend_time) {// NOSONAR
        this.spend_time = spend_time;
    }

    public Map<String, String> getGeoip_info() {// NOSONAR
        return geoip_info;
    }

    public void setGeoip_info(Map<String, String> geoip_info) {// NOSONAR
        this.geoip_info = geoip_info;
    }

    public Map<String, Object> getDevice_info() {// NOSONAR
        return device_info;
    }

    public void setDevice_info(Map<String, Object> device_info) {// NOSONAR
        this.device_info = device_info;
    }

    public Map<String, Object> getAttribution() {
        return attribution;
    }

    public void setAttribution(Map<String, Object> attribution) {
        this.attribution = attribution;
    }

    public List<Policy> getPolicy_set() {// NOSONAR
        return policy_set;
    }

    public void setPolicy_set(List<Policy> policy_set) {// NOSONAR
        this.policy_set = policy_set;
    }

    public String getPolicy_set_name() {// NOSONAR
        return policy_set_name;
    }

    public void setPolicy_set_name(String policy_set_name) {// NOSONAR
        this.policy_set_name = policy_set_name;
    }

    public String getRisk_type() {// NOSONAR
        return risk_type;
    }

    public void setRisk_type(String risk_type) {// NOSONAR
        this.risk_type = risk_type;
    }

    public String getDevice_id() {// NOSONAR
        if (getDevice_info() != null) {
            Object deviceId = getDevice_info().get("deviceId");
            return deviceId != null ? deviceId.toString() : null;
        }
        return null;
    }

    @Override
    public String toString() {
        return "FraudApiResponse [success=" + success + ", reason_code=" + reason_code + ", final_score=" + final_score
               + ", final_decision=" + final_decision + ", policy_name=" + policy_name // NOSONAR
               + ", seq_id=" + seq_id + ", spend_time=" + spend_time + ", policy_set_name=" + policy_set_name
               + ", risk_type=" + risk_type + "]";// NOSONAR
    }

}

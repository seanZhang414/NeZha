package cn.com.duiba.tuia.engine.activity.tongdun;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Policy
 */
public class Policy implements Serializable {

    private static final long serialVersionUID = 2971731835604653516L;
    private String            policy_uuid;                            // NOSONAR
    // 策略uuid
    private String            policy_decision;                        // NOSONAR
    // 策略结果
    private String            policy_mode;                            // NOSONAR
    // 策略模式
    private String            policy_name;                            // NOSONAR
    // 策略名称
    private int               policy_score;                           // NOSONAR
    // 策略分数
    private String            risk_type;                              // NOSONAR
    // 风险类型

    private List<HitRule>     hit_rules        = new ArrayList<>();   // NOSONAR
    // 命中规则列表

    public String getPolicy_uuid() {// NOSONAR
        return policy_uuid;
    }

    public void setPolicy_uuid(String policy_uuid) {// NOSONAR
        this.policy_uuid = policy_uuid;
    }

    public String getPolicy_decision() {// NOSONAR
        return policy_decision;
    }

    public void setPolicy_decision(String policy_decision) {// NOSONAR
        this.policy_decision = policy_decision;
    }

    public String getPolicy_mode() {// NOSONAR
        return policy_mode;
    }

    public void setPolicy_mode(String policy_mode) {// NOSONAR
        this.policy_mode = policy_mode;
    }

    public String getPolicy_name() {// NOSONAR
        return policy_name;
    }

    public void setPolicy_name(String policy_name) {// NOSONAR
        this.policy_name = policy_name;
    }

    public int getPolicy_score() {// NOSONAR
        return policy_score;
    }

    public void setPolicy_score(int policy_score) {// NOSONAR
        this.policy_score = policy_score;
    }

    public String getRisk_type() {// NOSONAR
        return risk_type;
    }

    public void setRisk_type(String risk_type) {// NOSONAR
        this.risk_type = risk_type;
    }

    public List<HitRule> getHit_rules() {// NOSONAR
        return hit_rules;
    }

    public void setHit_rules(List<HitRule> hit_rules) {// NOSONAR
        this.hit_rules = hit_rules;
    }

    @Override
    public String toString() {
        return "policy_name:" + this.policy_name + "\npolicy_mode:" + this.policy_mode + "\nhit_rules:" // NOSONAR
               + this.hit_rules; // NOSONAR
    }
}

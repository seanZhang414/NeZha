package cn.com.duiba.tuia.engine.activity.temp;

/**
 * Created by weny.cai on 2018/6/8.
 */
public class ActivityAccessTemp{

    /**
     * 访问次数
     */
    private Integer accessNum;

    /**
     * 上次是否参与
     */
    private Integer joinFlag;

    private Integer onceJoinFlag;

    private Boolean isHit;

    public Integer getAccessNum() {
        return accessNum;
    }

    public void setAccessNum(Integer accessNum) {
        this.accessNum = accessNum;
    }

    public Integer getJoinFlag() {
        return joinFlag;
    }

    public void setJoinFlag(Integer joinFlag) {
        this.joinFlag = joinFlag;
    }

    public Integer getOnceJoinFlag() {
        return onceJoinFlag;
    }

    public void setOnceJoinFlag(Integer onceJoinFlag) {
        this.onceJoinFlag = onceJoinFlag;
    }

    public Boolean getHit() {
        return isHit;
    }

    public void setHit(Boolean hit) {
        isHit = hit;
    }
}

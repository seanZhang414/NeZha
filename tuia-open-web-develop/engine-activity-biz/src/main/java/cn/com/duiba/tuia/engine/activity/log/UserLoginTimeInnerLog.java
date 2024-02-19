package cn.com.duiba.tuia.engine.activity.log;

/**
 * 用户登录时长内部日志
 */
public class UserLoginTimeInnerLog {
    private String dcm;
    private String dpm;
    private String dsm;
    private Long slotId;
    private Long appId;
    /**
     * 请求类型 1 广告位点击 2 游戏大厅 3 进入游戏 4 游戏开始按钮 5 匹配成功 6成功出券 7进入游戏大厅或者游戏
     */
    private Integer reqLocation;
    /**
     * 用户ID
     */
    private Long userId;
    private String uid;

    private static final class ExtInfo {

    }

    public String getDcm() {
        return dcm;
    }

    public void setDcm(String dcm) {
        this.dcm = dcm;
    }

    public String getDpm() {
        return dpm;
    }

    public void setDpm(String dpm) {
        this.dpm = dpm;
    }

    public String getDsm() {
        return dsm;
    }

    public void setDsm(String dsm) {
        this.dsm = dsm;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Integer getReqLocation() {
        return reqLocation;
    }

    public void setReqLocation(Integer reqLocation) {
        this.reqLocation = reqLocation;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

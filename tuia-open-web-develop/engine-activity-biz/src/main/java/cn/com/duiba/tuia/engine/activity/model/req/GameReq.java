package cn.com.duiba.tuia.engine.activity.model.req;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class GameReq {
    private Long adslotId;

    private String appKey;

    /**
     * 媒体用户ID
     */
    private String muId;

    /**
     * 签名
     */
    private String sign;

    /**
     * timestamp:时间戳
     */
    private Long timestamp;

    private String tokenId;

    private String deviceId;

    //游戏ID
    private Long gameId;

    //来源：20：游戏；21：游戏大厅
    private Integer source;

    private String nickName;

    private String headUrl;

    private Integer sex;

    private String createTime;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    // 来源: 趣晒
    private String contextToken;

    // 来源: 趣晒
    private String share_way;

    //分享深度
    private Integer deep;

    //分享文案ID
    private Integer shareTextId;

    public Integer getDeep() {
        return deep;
    }

    public void setDeep(Integer deep) {
        this.deep = deep;
    }

    public Integer getShareTextId() {
        return shareTextId;
    }

    public void setShareTextId(Integer shareTextId) {
        this.shareTextId = shareTextId;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }


    public Long getAdslotId() {
        return adslotId;
    }

    public void setAdslotId(Long adslotId) {
        this.adslotId = adslotId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getMuId() {
        return muId;
    }

    public void setMuId(String muId) {
        this.muId = muId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getContextToken() {
        return contextToken;
    }

    public void setContextToken(String contextToken) {
        this.contextToken = contextToken;
    }

    public String getShare_way() {
        return share_way;
    }

    public void setShare_way(String share_way) {
        this.share_way = share_way;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

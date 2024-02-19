package cn.com.duiba.nezha.engine.api.dto;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * 请求参数
 *
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: RequestDto.java , v 0.1 2017/6/8 下午6:54 ZhouFeng Exp $
 */
public class RequestDto implements Serializable {
    private static final long serialVersionUID = 5050061063037262986L;

    /**
     * 点击按钮的客户端的UA
     */
    private String ua;

    /**
     * ip
     */
    private String ip;

    /**
     * 城市id
     */
    private Long cityId;

    /**
     * 订单id
     */
    private String orderId;
    /**
     * 设备价格区间
     */
    private String priceSection;

    /**
     * 发券次序，该用户当日发券次数
     */
    private Long putIndex;

    /**
     * 机型
     */
    private String model;

    /**
     * 网络类型（2G，3G，4G）
     */
    private String connectionType;

    /**
     * 运营商（中国联通，中国移动，中国电信）
     */
    private String operatorType;

    /**
     * 手机品牌
     */
    private String phoneBrand;

    /**
     * 手机型号
     */
    private String phoneModel;

    // 订单列表
    private List<String> orderIds;

    // 需要的广告数
    private Long needCount;

    // 已存在广告数
    private Long existCount;

    public Long getExistCount() {
        return Optional.ofNullable(existCount).orElse(0L);
    }

    public void setExistCount(Long existCount) {
        this.existCount = existCount;
    }

    public String getUa() {
        return ua;
    }

    public List<String> getOrderIds() {
        return Optional.ofNullable(orderIds).orElse(Lists.newArrayList(orderId));
    }

    public void setOrderIds(List<String> orderIds) {
        this.orderIds = orderIds;
    }

    public Long getNeedCount() {
        return Optional.ofNullable(needCount).orElse(1L);
    }

    public void setNeedCount(Long needCount) {
        this.needCount = needCount;
    }

    public RequestDto setUa(String ua) {
        this.ua = ua;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public RequestDto setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public Long getCityId() {
        return cityId;
    }

    public RequestDto setCityId(Long cityId) {
        this.cityId = cityId;
        return this;
    }

    public String getOrderId() {
        return orderId;
    }

    public RequestDto setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public String getPriceSection() {
        return priceSection;
    }

    public RequestDto setPriceSection(String priceSection) {
        this.priceSection = priceSection;
        return this;
    }

    public Long getPutIndex() {
        return putIndex;
    }

    public RequestDto setPutIndex(Long putIndex) {
        this.putIndex = putIndex;
        return this;
    }

    public String getModel() {
        return model;
    }

    public RequestDto setModel(String model) {
        this.model = model;
        return this;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public RequestDto setConnectionType(String connectionType) {
        this.connectionType = connectionType;
        return this;
    }

    public String getOperatorType() {
        return operatorType;
    }

    public RequestDto setOperatorType(String operatorType) {
        this.operatorType = operatorType;
        return this;
    }

    public String getPhoneBrand() {
        return phoneBrand;
    }

    public void setPhoneBrand(String phoneBrand) {
        this.phoneBrand = phoneBrand;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }
}

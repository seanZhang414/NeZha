package cn.com.duiba.nezha.engine.biz.domain;

import cn.com.duiba.nezha.engine.api.dto.RequestDto;

import java.util.List;
import java.util.Optional;

public class RequestDo {
    // 点击按钮的客户端的UA
    private String ua;

    // 城市id
    private Long cityId;

    // 订单id
    private String orderId;

    // 设备价格区间
    private String priceSection;

    // 发券次序，该用户当日发券次数
    private Long putIndex;

    // 机型
    private String model;

    // 网络类型（2G，3G，4G）
    private String connectionType;

    // 运营商（中国联通，中国移动，中国电信）
    private String operatorType;

    // 手机品牌
    private String phoneBrand;

    // 手机型号
    private String phoneModel;

    // 订单列表
    private List<String> orderIds;

    // 需要的广告数
    private Long needCount;

    // 已存在广告数
    private Long existCount;

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPriceSection() {
        return priceSection;
    }

    public void setPriceSection(String priceSection) {
        this.priceSection = priceSection;
    }

    public Long getPutIndex() {
        return putIndex;
    }

    public void setPutIndex(Long putIndex) {
        this.putIndex = putIndex;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public String getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(String operatorType) {
        this.operatorType = operatorType;
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

    public List<String> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(List<String> orderIds) {
        this.orderIds = orderIds;
    }

    public Long getNeedCount() {
        return needCount;
    }

    public void setNeedCount(Long needCount) {
        this.needCount = needCount;
    }

    public Long getExistCount() {
        return existCount;
    }

    public void setExistCount(Long existCount) {
        this.existCount = existCount;
    }

    public Long getStartCount(){
        return Optional.ofNullable(existCount).orElse(0L) + 1L;
    }

    public static RequestDo convert(RequestDto requestDto) {
        RequestDo requestDo = new RequestDo();
        requestDo.setUa(requestDto.getUa());
        requestDo.setCityId(requestDto.getCityId());
        requestDo.setPriceSection(requestDto.getPriceSection());
        requestDo.setPutIndex(requestDto.getPutIndex());
        requestDo.setModel(requestDto.getModel());
        requestDo.setConnectionType(requestDto.getConnectionType());
        requestDo.setOperatorType(requestDto.getOperatorType());
        requestDo.setPhoneBrand(requestDto.getPhoneBrand());
        requestDo.setPhoneModel(requestDto.getPhoneModel());
        requestDo.setOrderIds(requestDto.getOrderIds());
        requestDo.setNeedCount(requestDto.getNeedCount());
        requestDo.setExistCount(requestDto.getExistCount());
        requestDo.setOrderId(requestDto.getOrderId());
        return requestDo;
    }
}

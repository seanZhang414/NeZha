package cn.com.duiba.nezha.engine.biz.domain;

import cn.com.duiba.nezha.engine.common.utils.HBaseField;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: ConsumerFeatureDO.java , v 0.1 2017/10/30 上午11:33 ZhouFeng Exp $
 */
public class ConsumerFeatureDO {

    /**
     * 订单总数
     */
    @HBaseField(alias = "order_cnt")
    private Long orderCount;

    /**
     * 当天订单数
     */
    private Long currentDayOrderCount;

    /**
     * 首单时间
     */
    private String firstOrderTime;

    /**
     * 最新订单时间
     */
    private String lastOrderTime;

    /**
     * 最新订单ID
     */
    private String lastOrderId;

    /**
     * 最新活动ID
     */
    private String lastActivityId;

    /**
     * 是否有计费订单 0：没有 1：有
     */
    private String isBilling;

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    public Long getCurrentDayOrderCount() {
        return currentDayOrderCount;
    }

    public void setCurrentDayOrderCount(Long currentDayOrderCount) {
        this.currentDayOrderCount = currentDayOrderCount;
    }

    public String getFirstOrderTime() {
        return firstOrderTime;
    }

    public void setFirstOrderTime(String firstOrderTime) {
        this.firstOrderTime = firstOrderTime;
    }

    public String getLastOrderTime() {
        return lastOrderTime;
    }

    public void setLastOrderTime(String lastOrderTime) {
        this.lastOrderTime = lastOrderTime;
    }

    public String getLastOrderId() {
        return lastOrderId;
    }

    public void setLastOrderId(String lastOrderId) {
        this.lastOrderId = lastOrderId;
    }

    public String getLastActivityId() {
        return lastActivityId;
    }

    public void setLastActivityId(String lastActivityId) {
        this.lastActivityId = lastActivityId;
    }

    public String getIsBilling() {
        return isBilling;
    }

    public void setIsBilling(String isBilling) {
        this.isBilling = isBilling;
    }
}

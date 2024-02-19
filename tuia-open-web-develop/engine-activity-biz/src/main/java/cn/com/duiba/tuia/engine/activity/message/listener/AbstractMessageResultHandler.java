/**
 * Project Name:engine-billing-biz
 * File Name:AbstractMessageResultHandler.java
 * Package Name:cn.com.duiba.tuia.engine.billing.task.ons
 * Date:2017年4月1日下午4:11:32
 * Copyright (c) 2017, duiba.com.cn All Rights Reserved.
 */

package cn.com.duiba.tuia.engine.activity.message.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;


public abstract class AbstractMessageResultHandler implements InitializingBean{

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取消费tag
     * getListenTag:(这里用一句话描述这个方法的作用). <br/>
     *
     * @return
     * @author zf
     * @since JDK 1.6
     */
    public abstract String getListenTag();

    /**
     * 消费消息
     *
     * @param message
     * @author zf
     * @since JDK 1.6
     */
    public abstract void consumer(String message);
}


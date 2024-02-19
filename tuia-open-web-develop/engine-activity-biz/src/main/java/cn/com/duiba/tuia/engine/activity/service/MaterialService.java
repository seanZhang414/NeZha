/*
 * Project Name:engine-activity-biz
 * File Name:MaterialService.java
 * Package Name:cn.com.duiba.tuia.engine.activity.service
 * Date:2017年11月6日上午11:05:30
 * Copyright (c) 2017, duiba.com.cn All Rights Reserved.
 */

package cn.com.duiba.tuia.engine.activity.service;

/**
 * ClassName:素材service
 *
 * @author Administrator
 * @since JDK 1.6
 */
public interface MaterialService {

    String getMaterialUrl(String scheme, Long slotId, String deviceId);
}

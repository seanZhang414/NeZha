package cn.com.duiba.nezha.engine.biz.service.advert.feature;

import cn.com.duiba.nezha.engine.biz.entity.nezha.advert.AppInstallFeature;

import java.util.List;

/**
 * @author ElinZhou
 * @version $Id: ConsumerAppInstallService.java , v 0.1 2018/1/10 下午7:41 ElinZhou Exp $
 */
public interface ConsumerAppInstallService {


    /**
     * 获取app安装特征
     *
     * @param appNames
     * @return
     */
    AppInstallFeature getFeature(List<String> appNames);


}

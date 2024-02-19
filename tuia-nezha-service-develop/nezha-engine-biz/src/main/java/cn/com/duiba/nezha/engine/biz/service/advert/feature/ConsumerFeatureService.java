package cn.com.duiba.nezha.engine.biz.service.advert.feature;

import java.util.List;
import java.util.Map;

import cn.com.duiba.nezha.engine.biz.domain.ConsumerFeatureDO;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: ConsumerFeatureService.java , v 0.1 2017/10/27 下午5:13 ZhouFeng Exp $
 */
public interface ConsumerFeatureService {

    /**
     * 获取特征
     * 
     * @param rowKeys key列表
     * @return key对应的特征
     */
    Map<String, ConsumerFeatureDO> getFeatures(List<String> rowKeys);

}

package cn.com.duiba.nezha.engine.biz.service.advert.ctr;

import cn.com.duiba.nezha.engine.api.enums.StatDataTypeEnum;
import cn.com.duiba.nezha.engine.biz.domain.FeatureIndex;
import cn.com.duiba.nezha.engine.biz.domain.PredictParameter;
import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertRecommendRequestVo;

import java.util.Map;

/**
 * 广告预估服务
 * <p>
 * Created by pc on 2017/2/27.
 */
public interface AdvertPredictService {


    Map<StatDataTypeEnum, Map<FeatureIndex, Double>> predict(AdvertRecommendRequestVo advertRecommendRequestVo, PredictParameter predictParameter);
}

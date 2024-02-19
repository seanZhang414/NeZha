package cn.com.duiba.nezha.engine.biz.service.advert.ctr;

import cn.com.duiba.nezha.alg.alg.vo.NezhaStatDto;
import cn.com.duiba.nezha.engine.api.enums.AdvertAlgEnum;
import cn.com.duiba.nezha.engine.api.enums.StatDataTypeEnum;
import cn.com.duiba.nezha.engine.biz.domain.CorrectResult;
import cn.com.duiba.nezha.engine.biz.domain.FeatureIndex;
import cn.com.duiba.nezha.engine.biz.domain.advert.Advert;
import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertRecommendRequestVo;

import java.util.Collection;
import java.util.Map;

/**
 * @author ElinZhou
 * @version $Id: AdvertPredictCorrectService.java , v 0.1 2017/9/13 下午5:00 ElinZhou Exp $
 */
public interface AdvertPredictCorrectService {

    CorrectResult correct(AdvertRecommendRequestVo advertRecommendRequestVo,
                          Map<StatDataTypeEnum, Map<FeatureIndex, Double>> advertPredictValueMap);

    Map<Long, NezhaStatDto> loadNezhaStatDto(AdvertAlgEnum advertAlgEnum, Collection<Advert> advertIds, Long appId);

}

package cn.com.duiba.nezha.engine.biz.service.advert.strategy;


import cn.com.duiba.nezha.engine.api.enums.ResultCodeEnum;
import cn.com.duiba.nezha.engine.api.support.RecommendEngineException;
import cn.com.duiba.nezha.engine.biz.support.Roulette;
import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertMaterialResortGroupVo;
import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertMaterialResortVo;
import cn.com.duiba.nezha.engine.common.utils.AssertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pc on 2016/12/22.
 */
@Service
public class AdvertMaterialRcmdStrategyService {

    private static final Logger logger = LoggerFactory.getLogger(AdvertMaterialRcmdStrategyService.class);

    public AdvertMaterialResortVo getMaterial(List<AdvertMaterialResortGroupVo> inputVoList) {

        AdvertMaterialResortVo advertMaterialResortVo = null;


        // 1 参数检验
        if (AssertUtil.isAllEmpty(inputVoList)) {
            logger.warn("getMaterialId param cheak invalid", ResultCodeEnum.PARAMS_INVALID.getDesc());
            return advertMaterialResortVo;
        }
        try {

            // 2 素材组轮询
            Map<AdvertMaterialResortGroupVo, Long> groupPollingMap = new HashMap<>();
            for (AdvertMaterialResortGroupVo vo : inputVoList) {
                groupPollingMap.put(vo, vo.getMaterialGroupTraffic());
            }

            AdvertMaterialResortGroupVo advertMaterialResortGroupVo = Roulette.longMap(groupPollingMap);

            // 3 素材列表轮询
            Map<AdvertMaterialResortVo, Double> materialPollingMap = new HashMap<>();
            for (AdvertMaterialResortVo vo : advertMaterialResortGroupVo.getMaterialResortVoList()) {
                materialPollingMap.put(vo, vo.getWeight());
            }
            advertMaterialResortVo = Roulette.doubleMap(materialPollingMap);
        } catch (Exception e) {
            logger.error("getMaterial happened error:{}", e);
            throw new RecommendEngineException("getMaterial  happend error", e);
        }

        // 4 返回结果
        return advertMaterialResortVo;
    }


}

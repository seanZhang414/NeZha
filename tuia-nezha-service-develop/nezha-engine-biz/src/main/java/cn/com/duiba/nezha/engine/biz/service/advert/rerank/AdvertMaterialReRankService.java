package cn.com.duiba.nezha.engine.biz.service.advert.rerank;

import cn.com.duiba.nezha.engine.api.enums.AdvertMaterialGroupTypeEnum;
import cn.com.duiba.nezha.engine.api.enums.ResultCodeEnum;
import cn.com.duiba.nezha.engine.api.support.RecommendEngineException;
import cn.com.duiba.nezha.engine.biz.support.advert.ComparatorAdvertMaterialResortVo;
import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertMaterialResortGroupVo;
import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertMaterialResortVo;
import cn.com.duiba.nezha.engine.common.utils.AssertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by pc on 2016/12/22.
 */
@Service
public class AdvertMaterialReRankService {
    private static final Logger logger = LoggerFactory.getLogger(AdvertMaterialReRankService.class);

    public List<AdvertMaterialResortGroupVo> reRank(List<AdvertMaterialResortGroupVo> inputVoList,
                                                    Long oldMaterialTraffic,
                                                    Long oldMaterialRatio) {

        List<AdvertMaterialResortGroupVo> retVoList = null;

        // 1 参数检验
        if (AssertUtil.isAnyEmpty(inputVoList, oldMaterialTraffic, oldMaterialRatio)) {
            logger.warn("reRank param cheak invalid", ResultCodeEnum.PARAMS_INVALID.getDesc());
            return retVoList;
        }
        try {
            // 2 分组拆分 计算权重
            for (AdvertMaterialResortGroupVo resortGroupVo : inputVoList) {

                // 老素材列表
                if (resortGroupVo.getMaterialGroupId() == AdvertMaterialGroupTypeEnum.WITH_WEIGHT_MATERIAL_TYPE.getIndex()) {
                    resortGroupVo.setMaterialGroupTraffic(oldMaterialTraffic);
                    resortGroupVo.getMaterialResortVoList().sort(new ComparatorAdvertMaterialResortVo());
                    Double threshold = resortGroupVo.getMaterialResortVoList().get(0).getRankScore() * oldMaterialRatio / 100;
                    assignRank(resortGroupVo, threshold);

                }
                // 新素材列表
                if (resortGroupVo.getMaterialGroupId() == AdvertMaterialGroupTypeEnum.WITHOUT_WEIGHT_MATERIAL_TYPE.getIndex()) {
                    resortGroupVo.setMaterialGroupTraffic(100 - oldMaterialTraffic);
                    for (AdvertMaterialResortVo vo : resortGroupVo.getMaterialResortVoList()) {
                        vo.setWeight(1.0);
                    }
                }
            }
            retVoList = inputVoList;
        } catch (Exception e) {
            logger.error("reRank happen error:{}", e);
            throw new RecommendEngineException("reRank happen error", e);
        }
        return retVoList;

    }

    private void assignRank(AdvertMaterialResortGroupVo resortGroupVo, Double threshold) {
        long rank = 0;
        for (AdvertMaterialResortVo vo : resortGroupVo.getMaterialResortVoList()) {
            vo.setRank(rank++);
            if (vo.getRankScore() >= threshold) {
                vo.setWeight(vo.getRankScore());
            }
        }
    }


}

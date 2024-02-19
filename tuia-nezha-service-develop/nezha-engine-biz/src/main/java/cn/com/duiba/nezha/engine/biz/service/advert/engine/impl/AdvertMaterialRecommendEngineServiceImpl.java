package cn.com.duiba.nezha.engine.biz.service.advert.engine.impl;

import cn.com.duiba.nezha.engine.api.dto.ReqAdvertMaterialDto;
import cn.com.duiba.nezha.engine.api.support.RecommendEngineException;
import cn.com.duiba.nezha.engine.biz.entity.nezha.advert.AdvertMaterialRcmdCtrStatEntity;
import cn.com.duiba.nezha.engine.biz.service.advert.ctr.AdvertMaterialCtrByStatService;
import cn.com.duiba.nezha.engine.biz.service.advert.engine.AdvertMaterialRecommendEngineService;
import cn.com.duiba.nezha.engine.biz.service.advert.merge.AdvertMaterialMergeService;
import cn.com.duiba.nezha.engine.biz.service.advert.rerank.AdvertMaterialReRankService;
import cn.com.duiba.nezha.engine.biz.service.advert.strategy.AdvertMaterialRcmdStrategyService;
import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertMaterialResortGroupVo;
import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertMaterialResortVo;
import cn.com.duiba.nezha.engine.common.utils.AssertUtil;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * Created by xuezhaoming on 16/8/2.
 */
@Service
public class AdvertMaterialRecommendEngineServiceImpl implements AdvertMaterialRecommendEngineService {

    private static final Logger logger = LoggerFactory.getLogger(AdvertMaterialRecommendEngineServiceImpl.class);

    private static Random random = new Random();

    @Autowired
    AdvertMaterialCtrByStatService advertMaterialCtrByStatService;

    @Autowired
    AdvertMaterialMergeService advertMaterialMergeService;

    @Autowired
    AdvertMaterialReRankService advertMaterialReRankService;

    @Autowired
    AdvertMaterialRcmdStrategyService advertMaterialRcmdStrategyService;

    /**
     * 广告素材推荐
     *
     * @param req 请求对象
     * @return 广告素材对象
     */
    @Override
    public Long recommendMaterial(ReqAdvertMaterialDto req) {

        Long materialId = null;

        // 请求时间戳
        String reqId = System.currentTimeMillis() + "-" + random.nextInt(1000);

        // 1 参数检验
        if (!paramCheck(req)) {
            logger.warn("req is invalid");
            throw new RecommendEngineException("req is invalid");
        }
        // 2 读取统计信息
        List<AdvertMaterialRcmdCtrStatEntity> materialCtrEntityList =
                advertMaterialCtrByStatService.getMaterialList(req.getAppId(),
                        req.getAdvertId(),
                        req.getOldMaterialList(),
                        req.getNewMaterialList());

        // 3 素材重新分组
        List<AdvertMaterialResortGroupVo> materialGroupVoList =
                advertMaterialMergeService.getMergeMaterialMap(materialCtrEntityList,
                        req.getOldMaterialList(),
                        req.getNewMaterialList());

        // 4 重排权重配置

        List<AdvertMaterialResortGroupVo> materialResortGroupVoList =
                advertMaterialReRankService.reRank(materialGroupVoList,
                        req.getOldMaterialTraffic(),
                        req.getOldMaterialRatio());

        if (logger.isDebugEnabled()) {
            logger.debug("{},materialResortGroupVoList = {}", reqId, JSON.toJSONString(materialResortGroupVoList));
        }
        // 5 出券策略
        AdvertMaterialResortVo advertMaterialResortVo = advertMaterialRcmdStrategyService.getMaterial(materialResortGroupVoList);


        // 6 返回结果
        if (AssertUtil.isNotEmpty(advertMaterialResortVo)) {
            materialId = advertMaterialResortVo.getMaterialId();
        }
        logger.debug("{},rsp={}", reqId, materialId);
        return materialId;
    }

    /**
     * 请求参数检查
     *
     * @param req 请求对象
     */
    private boolean paramCheck(ReqAdvertMaterialDto req) {
        try {

            if (AssertUtil.isAnyEmpty(req)) {
                logger.error(" paramCheck 0 error, req = [{}], please check the req ", req);
                return false;
            }

            if (AssertUtil.isAnyEmpty(
                    req.getAppId(),
                    req.getAdvertId(),
                    req.getOldMaterialRatio(),
                    req.getOldMaterialTraffic())) {
                logger.error(" paramCheck 1 error, req = [{}], please check the req ", req);
                return false;
            }

            if (AssertUtil.isAllEmpty(req.getOldMaterialList(), req.getNewMaterialList())) {
                logger.error("paramCheck 2 error, req = [{}], please check the req ", req);
                return false;
            }

            if (req.getOldMaterialTraffic() > 100 || req.getOldMaterialRatio() > 100) {
                logger.error("paramCheck 3 error, req = [{}], please check the req ", req);
                return false;
            }


        } catch (Exception e) {
            logger.error("paramCheck happened error :{}", e);
            return false;
        }
        return true;
    }
}

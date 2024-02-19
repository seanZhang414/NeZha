package cn.com.duiba.nezha.engine.biz.service.advert;

import cn.com.duiba.nezha.engine.api.enums.ModelKeyEnum;
import cn.com.duiba.nezha.engine.api.enums.PredictCorrectType;
import cn.com.duiba.nezha.engine.api.enums.ShowAdvertAlgEnum;
import cn.com.duiba.nezha.engine.biz.enums.RecommendMaterialType;
import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertRecommendRequestVo;
import org.springframework.stereotype.Service;

import static cn.com.duiba.nezha.engine.biz.vo.advert.AdvertStatDimWeightVo.advertStatDimWeightVoB;

@Service
public class ShowAdvertRecommendService extends AbstractAdvertRecommendService {
    @Override
    public void prepareStrategyParameter(AdvertRecommendRequestVo advertRecommendRequestVo) {
        ShowAdvertAlgEnum showAdvertAlgEnum = (ShowAdvertAlgEnum) advertRecommendRequestVo.getAdvertAlgEnum();
        //CTR预估模型Key
        ModelKeyEnum ctrModelKey;

        //CVR预估模型Key
        ModelKeyEnum cvrModelKey;

        PredictCorrectType predictCorrectType = PredictCorrectType.NONE;

        RecommendMaterialType recommendMaterialType = RecommendMaterialType.NONE;

        switch (showAdvertAlgEnum) {
            case SHOW_AND_PC_1:
                ctrModelKey = ModelKeyEnum.FTRL_FM_CTR_MODEL_v009;
                cvrModelKey = ModelKeyEnum.FTRL_FM_CVR_MODEL_v009;
                break;
            default:
                throw new RuntimeException("策略不存在");
        }

        advertRecommendRequestVo.setAdvertStatDimWeightVo(advertStatDimWeightVoB);
        advertRecommendRequestVo.setCtrModelKey(ctrModelKey);
        advertRecommendRequestVo.setCvrModelKey(cvrModelKey);
        advertRecommendRequestVo.setRecommendMaterialType(recommendMaterialType);
        advertRecommendRequestVo.setPredictCorrectType(predictCorrectType);
        advertRecommendRequestVo.setAdvertMultiDimScoreEffective(false);
        advertRecommendRequestVo.setInvokeWeakFilter(true);
        advertRecommendRequestVo.setNeedPredict(true);
    }
}

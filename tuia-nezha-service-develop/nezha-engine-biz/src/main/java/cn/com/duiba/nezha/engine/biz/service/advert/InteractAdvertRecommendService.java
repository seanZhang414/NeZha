package cn.com.duiba.nezha.engine.biz.service.advert;

import cn.com.duiba.nezha.engine.api.enums.DeepTfServer;
import cn.com.duiba.nezha.engine.api.enums.InteractAdvertAlgEnum;
import cn.com.duiba.nezha.engine.api.enums.ModelKeyEnum;
import cn.com.duiba.nezha.engine.api.enums.PredictCorrectType;
import cn.com.duiba.nezha.engine.biz.enums.RecommendMaterialType;
import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertRecommendRequestVo;
import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertStatDimWeightVo;
import org.springframework.stereotype.Service;

import static cn.com.duiba.nezha.engine.biz.vo.advert.AdvertStatDimWeightVo.advertStatDimWeightVoA;
import static cn.com.duiba.nezha.engine.biz.vo.advert.AdvertStatDimWeightVo.advertStatDimWeightVoB;

@Service
public class InteractAdvertRecommendService extends AbstractAdvertRecommendService {
    @Override
    public void prepareStrategyParameter(AdvertRecommendRequestVo advertRecommendRequestVo) {
        // 算法类型
        InteractAdvertAlgEnum interactAdvertAlgEnum = (InteractAdvertAlgEnum) advertRecommendRequestVo.getAdvertAlgEnum();

        //CTR预估模型Key
        ModelKeyEnum ctrModelKey = null;

        //CVR预估模型Key
        ModelKeyEnum cvrModelKey = null;

        // 深度学习ctr模型key
        DeepTfServer deepCtrModelKey = null;

        // 深度学习cvr模型key
        DeepTfServer deepCvrModelKey = null;

        // 广告融合权重参数对象
        AdvertStatDimWeightVo advertStatDimWeightVo = advertStatDimWeightVoB;

        //素材推荐方式
        RecommendMaterialType recommendMaterialType = RecommendMaterialType.NONE;

        PredictCorrectType predictCorrectType = PredictCorrectType.NONE;

        //广告多维度质量分是否启用
        boolean advertMultiDimScoreEffective = false;

        boolean needPredict = true;

        boolean invokeWeakFilter = false;


        switch (interactAdvertAlgEnum) {
            case BTM_AND_SC_6:
                needPredict = false;
                invokeWeakFilter = true;
                break;
            case BTM_AND_SC_7:
                needPredict = false;
                recommendMaterialType = RecommendMaterialType.STATIC;
                invokeWeakFilter = true;
                break;
            case BTM_AND_PC_1:
                ctrModelKey = ModelKeyEnum.LR_CTR_MODEL_v004;
                cvrModelKey = ModelKeyEnum.LR_CVR_MODEL_v004;
                break;
            case BTM_AND_PC_2:
                ctrModelKey = ModelKeyEnum.FM_CTR_MODEL_v003;
                cvrModelKey = ModelKeyEnum.FM_CVR_MODEL_v003;
                break;
            case BTM_AND_PC_6:
                ctrModelKey = ModelKeyEnum.FM_CTR_MODEL_v003;
                cvrModelKey = ModelKeyEnum.FM_CVR_MODEL_v003;
                predictCorrectType = PredictCorrectType.CORRECT;
                break;
            case BTM_AND_PC_7:
                ctrModelKey = ModelKeyEnum.FM_CTR_MODEL_v003;
                cvrModelKey = ModelKeyEnum.FM_CVR_MODEL_v003;
                advertStatDimWeightVo = advertStatDimWeightVoA;
                predictCorrectType = PredictCorrectType.CORRECT_REFACTOR;
                break;
            case BTM_AND_PC_8:
                ctrModelKey = ModelKeyEnum.FM_CTR_MODEL_v004;
                cvrModelKey = ModelKeyEnum.FM_CVR_MODEL_v004;
                break;
            case BTM_AND_PC_9:
                ctrModelKey = ModelKeyEnum.FM_CTR_MODEL_v005;
                cvrModelKey = ModelKeyEnum.FM_CVR_MODEL_v005;
                break;
            case BTM_AND_PC_10:
                ctrModelKey = ModelKeyEnum.FM_CTR_MODEL_v006;
                cvrModelKey = ModelKeyEnum.FM_CVR_MODEL_v006;
                recommendMaterialType = RecommendMaterialType.STATIC;
                break;
            case BTM_AND_PC_11:
                ctrModelKey = ModelKeyEnum.LR_CTR_MODEL_v007;
                cvrModelKey = ModelKeyEnum.LR_CVR_MODEL_v007;
                break;
            case BTM_AND_PC_12:
                ctrModelKey = ModelKeyEnum.LR_CTR_MODEL_v008;
                cvrModelKey = ModelKeyEnum.LR_CVR_MODEL_v008;
                break;
            case BTM_AND_PC_13:
                ctrModelKey = ModelKeyEnum.FM_CTR_MODEL_v003;
                cvrModelKey = ModelKeyEnum.FM_CVR_MODEL_v003;
                advertMultiDimScoreEffective = true;
                break;
            case BTM_AND_PC_14:
                ctrModelKey = ModelKeyEnum.FM_CTR_MODEL_v007;
                cvrModelKey = ModelKeyEnum.FM_CVR_MODEL_v007;
                break;
            case BTM_AND_PC_15:
                ctrModelKey = ModelKeyEnum.FM_CTR_MODEL_v601;
                cvrModelKey = ModelKeyEnum.FM_CVR_MODEL_v601;
                break;
            case BTM_AND_PC_16:
                ctrModelKey = ModelKeyEnum.FM_CTR_MODEL_v600;
                cvrModelKey = ModelKeyEnum.FM_CVR_MODEL_v600;
                break;
            case BTM_AND_PC_17:
                ctrModelKey = ModelKeyEnum.FM_CTR_MODEL_v003;
                cvrModelKey = ModelKeyEnum.FM_CVR_MODEL_v003;
                break;
            case BTM_AND_PC_18:
                ctrModelKey = ModelKeyEnum.FM_CTR_MODEL_v610;
                cvrModelKey = ModelKeyEnum.FM_CVR_MODEL_v610;
                break;
            case BTM_AND_PC_20:
                ctrModelKey = ModelKeyEnum.FM_CTR_MODEL_v611;
                cvrModelKey = ModelKeyEnum.FM_CVR_MODEL_v611;
                break;
            case BTM_AND_PC_21:
                ctrModelKey = ModelKeyEnum.FTRL_FM_CTR_MODEL_v001;
                cvrModelKey = ModelKeyEnum.FTRL_FM_CVR_MODEL_v001;
                advertStatDimWeightVo = advertStatDimWeightVoA;
                break;
            case BTM_AND_PC_22:
                ctrModelKey = ModelKeyEnum.FM_CTR_MODEL_v602;
                cvrModelKey = ModelKeyEnum.FM_CVR_MODEL_v602;
                break;
            case BTM_AND_PC_23:
                ctrModelKey = ModelKeyEnum.FTRL_FM_CTR_MODEL_v002;
                cvrModelKey = ModelKeyEnum.FTRL_FM_CVR_MODEL_v002;
                advertStatDimWeightVo = advertStatDimWeightVoA;
                break;
            case BTM_AND_PC_24:
                ctrModelKey = ModelKeyEnum.FTRL_FM_CTR_MODEL_v003;
                cvrModelKey = ModelKeyEnum.FTRL_FM_CVR_MODEL_v003;
                advertStatDimWeightVo = advertStatDimWeightVoA;
                break;
            case BTM_AND_PC_25:
                ctrModelKey = ModelKeyEnum.FTRL_FM_CTR_MODEL_v004;
                cvrModelKey = ModelKeyEnum.FTRL_FM_CVR_MODEL_v004;
                advertStatDimWeightVo = advertStatDimWeightVoA;
                break;
            case BTM_AND_PC_26:
                ctrModelKey = ModelKeyEnum.FTRL_FM_CTR_MODEL_v008;
                cvrModelKey = ModelKeyEnum.FTRL_FM_CVR_MODEL_v008;
                advertStatDimWeightVo = advertStatDimWeightVoA;
                break;
            case BTM_AND_PC_27:
                ctrModelKey = ModelKeyEnum.FM_CTR_MODEL_v603;
                cvrModelKey = ModelKeyEnum.FM_CVR_MODEL_v603;
                break;
            case BTM_AND_PC_28:
                ctrModelKey = ModelKeyEnum.FM_CTR_MODEL_v612;
                cvrModelKey = ModelKeyEnum.FM_CVR_MODEL_v612;
                break;
            case BTM_AND_PC_29:
                ctrModelKey = ModelKeyEnum.FM_CTR_MODEL_v003;
                cvrModelKey = ModelKeyEnum.FM_CVR_MODEL_v003;
                break;
            case BTM_AND_PC_30:
                ctrModelKey = ModelKeyEnum.FM_CTR_MODEL_v613;
                cvrModelKey = ModelKeyEnum.FM_CVR_MODEL_v613;
                break;
            case BTM_AND_PC_31:
                ctrModelKey = ModelKeyEnum.FTRL_FM_CTR_MODEL_v007;
                cvrModelKey = ModelKeyEnum.FTRL_FM_CVR_MODEL_v007;
                advertStatDimWeightVo = advertStatDimWeightVoA;
                break;
            case BTM_AND_PC_32:
                ctrModelKey = ModelKeyEnum.FTRL_FM_CTR_MODEL_v001;
                cvrModelKey = ModelKeyEnum.FTRL_FM_CVR_MODEL_v001;
                deepCtrModelKey = DeepTfServer.FNN_CTR_001;
                deepCvrModelKey = DeepTfServer.FNN_CVR_001;
                advertStatDimWeightVo = advertStatDimWeightVoA;
                break;
            case BTM_AND_PC_33:
                ctrModelKey = ModelKeyEnum.FTRL_FM_CTR_MODEL_v001;
                cvrModelKey = ModelKeyEnum.FTRL_FM_CVR_MODEL_v001;
                deepCtrModelKey = DeepTfServer.DEEP_FM_CTR_001;
                deepCvrModelKey = DeepTfServer.DEEP_FM_CVR_001;
                advertStatDimWeightVo = advertStatDimWeightVoA;
                break;
            case BTM_AND_PC_34:
                ctrModelKey = ModelKeyEnum.FM_CTR_MODEL_v006;
                cvrModelKey = ModelKeyEnum.FM_CVR_MODEL_v006;
                recommendMaterialType = RecommendMaterialType.PREDICT;
                break;
            case BTM_AND_PC_35:
                ctrModelKey = ModelKeyEnum.FM_CTR_MODEL_v003;
                cvrModelKey = ModelKeyEnum.FM_CVR_MODEL_v003;
                predictCorrectType = PredictCorrectType.CORRECT_NEW1;
                break;
            case BTM_AND_PC_36:
                ctrModelKey = ModelKeyEnum.FM_CTR_MODEL_v003;
                cvrModelKey = ModelKeyEnum.FM_CVR_MODEL_v003;
                predictCorrectType = PredictCorrectType.CORRECT_NEW2;
                break;
            case BTM_AND_PC_37:
                ctrModelKey = ModelKeyEnum.FM_CTR_MODEL_v501;
                cvrModelKey = ModelKeyEnum.FM_CVR_MODEL_v501;
                break;
            case BTM_AND_PC_38:
                ctrModelKey = ModelKeyEnum.FM_CTR_MODEL_v502;
                cvrModelKey = ModelKeyEnum.FM_CVR_MODEL_v502;
                recommendMaterialType = RecommendMaterialType.PREDICT;
                break;
            case BTM_AND_PC_39:
                ctrModelKey = ModelKeyEnum.FTRL_FM_CTR_MODEL_v001;
                cvrModelKey = ModelKeyEnum.FTRL_FM_CVR_MODEL_v001;
                deepCtrModelKey = DeepTfServer.OPNN_CTR_001;
                deepCvrModelKey = DeepTfServer.OPNN_CVR_001;
                advertStatDimWeightVo = advertStatDimWeightVoA;
                break;
            case BTM_AND_PC_40:
                ctrModelKey = ModelKeyEnum.FM_CTR_MODEL_v614;
                cvrModelKey = ModelKeyEnum.FM_CVR_MODEL_v614;
                break;
            case BTM_AND_PC_41:
                ctrModelKey = ModelKeyEnum.FTRL_FM_CTR_MODEL_v007;
                cvrModelKey = ModelKeyEnum.FTRL_FM_CVR_MODEL_v007;
                deepCtrModelKey = DeepTfServer.FNN_CTR_002;
                deepCvrModelKey = DeepTfServer.FNN_CVR_002;
                advertStatDimWeightVo = advertStatDimWeightVoA;
                break;
            case BTM_AND_PC_42:
                ctrModelKey = ModelKeyEnum.FTRL_FM_CTR_MODEL_v007;
                cvrModelKey = ModelKeyEnum.FTRL_FM_CVR_MODEL_v007;
                deepCtrModelKey = DeepTfServer.DEEP_FM_CTR_002;
                deepCvrModelKey = DeepTfServer.DEEP_FM_CVR_002;
                advertStatDimWeightVo = advertStatDimWeightVoA;
                break;
            case BTM_AND_PC_43:
                ctrModelKey = ModelKeyEnum.FTRL_FM_CTR_MODEL_v001;
                cvrModelKey = ModelKeyEnum.FTRL_FM_CVR_MODEL_v001;
                deepCtrModelKey = DeepTfServer.DCN_CTR_001;
                deepCvrModelKey = DeepTfServer.DCN_CVR_001;
                advertStatDimWeightVo = advertStatDimWeightVoA;
                break;
            case BTM_AND_PC_44:
                ctrModelKey = ModelKeyEnum.FTRL_FM_CTR_MODEL_v007;
                cvrModelKey = ModelKeyEnum.FTRL_FM_CVR_MODEL_v007;
                deepCtrModelKey = DeepTfServer.DCN_CTR_002;
                deepCvrModelKey = DeepTfServer.DCN_CVR_002;
                advertStatDimWeightVo = advertStatDimWeightVoA;
                break;
            case BTM_AND_PC_45:
                ctrModelKey = ModelKeyEnum.FTRL_FM_CTR_MODEL_v007;
                cvrModelKey = ModelKeyEnum.FTRL_FM_CVR_MODEL_v007;
                deepCtrModelKey = DeepTfServer.DCN_CTR_003;
                deepCvrModelKey = DeepTfServer.DCN_CVR_003;
                advertStatDimWeightVo = advertStatDimWeightVoA;
                break;
            case BTM_AND_PC_46:
                ctrModelKey = ModelKeyEnum.FTRL_FM_CTR_MODEL_v007;
                cvrModelKey = ModelKeyEnum.FTRL_FM_CVR_MODEL_v007;
                deepCtrModelKey = DeepTfServer.XDEEP_FM_CTR_002;
                deepCvrModelKey = DeepTfServer.XDEEP_FM_CVR_002;
                advertStatDimWeightVo = advertStatDimWeightVoA;
                break;
            case BTM_AND_PC_47:
                ctrModelKey = ModelKeyEnum.FTRL_FM_CTR_MODEL_v007;
                cvrModelKey = ModelKeyEnum.FTRL_FM_CVR_MODEL_v007;
                deepCtrModelKey = DeepTfServer.XDEEP_FM_CTR_003;
                deepCvrModelKey = DeepTfServer.XDEEP_FM_CVR_003;
                advertStatDimWeightVo = advertStatDimWeightVoA;
                break;
            default:
                throw new RuntimeException();
        }

        advertRecommendRequestVo.setNeedPredict(needPredict);
        advertRecommendRequestVo.setAdvertStatDimWeightVo(advertStatDimWeightVo);
        advertRecommendRequestVo.setCtrModelKey(ctrModelKey);
        advertRecommendRequestVo.setCvrModelKey(cvrModelKey);
        advertRecommendRequestVo.setBackendCtrModelKey(ModelKeyEnum.FM_BE_CVR_MODEL_v001);
        advertRecommendRequestVo.setRecommendMaterialType(recommendMaterialType);
        advertRecommendRequestVo.setPredictCorrectType(predictCorrectType);
        advertRecommendRequestVo.setAdvertMultiDimScoreEffective(advertMultiDimScoreEffective);
        advertRecommendRequestVo.setInvokeWeakFilter(invokeWeakFilter);
        advertRecommendRequestVo.setDeepCtrModelKey(deepCtrModelKey);
        advertRecommendRequestVo.setDeepCvrModelKey(deepCvrModelKey);
    }
}

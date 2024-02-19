package cn.com.duiba.nezha.compute.biz.bo;

import cn.com.duiba.nezha.compute.alg.ftrl.FMModel;
import cn.com.duiba.nezha.compute.biz.ps.PsAgent;
import cn.com.duiba.nezha.compute.biz.save.ModelSave;
import cn.com.duiba.nezha.compute.core.CollectionUtil;
import cn.com.duiba.nezha.compute.core.LabeledFeature;
import cn.com.duiba.nezha.compute.core.enums.DateStyle;
import cn.com.duiba.nezha.compute.core.model.local.LocalModel;
import cn.com.duiba.nezha.compute.core.util.DateUtil;
import cn.com.duiba.nezha.compute.feature.FeatureCoder;
import cn.com.duiba.nezha.compute.feature.constant.FeatureListConstant;
import cn.com.duiba.nezha.compute.feature.enums.ModelKeyEnum;
import cn.com.duiba.nezha.compute.mllib.fm.ftrl.FMFTRL;
import cn.com.duiba.nezha.compute.mllib.fm.ftrl.FMFTRLHyperParams;
import cn.com.duiba.nezha.compute.mllib.fm.ftrl.SparseFMWithFTRL;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SyncBo {

    public static void delete(String modelId) throws Exception {
        PsAgent psAgent = new PsAgent();
        psAgent.setModelId(modelId);
        psAgent.setParSize(psAgent.getPsParSize());
        psAgent.setDim(psAgent.getPsDim());

        FMFTRLHyperParams hyperParams = getFMFTRLHyperParams(modelId);

        psAgent.delete(
                SparseFMWithFTRL.searchModel(
                        psAgent.getDim(), hyperParams.factorNum()));


    }


    public static LocalModel getSearchModel(String modelId) throws Exception {

        PsAgent psAgent = new PsAgent();
        psAgent.setModelId(modelId);
        psAgent.setParSize(psAgent.getPsParSize());
        psAgent.setDim(psAgent.getPsDim());

        FMFTRLHyperParams hyperParams = getFMFTRLHyperParams(modelId);


        boolean status = psAgent.pull(SparseFMWithFTRL.searchModel(
                psAgent.getDim(), hyperParams.factorNum()), true);
        LocalModel localModel = psAgent.getLocalModel();


        return localModel;
    }

    public static FMFTRL getLocalModel(String modelId) throws Exception {

        PsAgent psAgent = new PsAgent();
        psAgent.setModelId(modelId);
        psAgent.setParSize(psAgent.getPsParSize());
        psAgent.setDim(psAgent.getPsDim());

        FMFTRLHyperParams hyperParams = getFMFTRLHyperParams(modelId);


        boolean status = psAgent.pull(SparseFMWithFTRL.searchModel(
                psAgent.getDim(), hyperParams.factorNum()), true);
        LocalModel localModel = psAgent.getLocalModel();

        FMFTRL model = new FMFTRL();

        model.setAlpha(hyperParams.alpha()).
                setBeta(hyperParams.beta()).
                setLambda1(hyperParams.lambda1()).
                setLambda2(hyperParams.lambda2()).
                setLocalModel(localModel);

        model.setDim(psAgent.getDim()).
                setFacotrNum(hyperParams.factorNum());

        return model;
    }


    public static FMModel getJModel(String modelId) throws Exception {


        FMModel fmModel = getLocalModel(modelId).toJFM();

        fmModel.setModelId(modelId);
        fmModel.setUpdateTime(DateUtil.getCurrentTime());
        fmModel.setFeatureBaseCode(FeatureCoder.getFeatureCode(modelId));

        return fmModel;
    }

    public static String getFeatureCode(FMModel fmModel, LabeledFeature row) {
        String ret = null;
        try {
            if (fmModel != null && row != null) {
                Map<String, String> map = (Map) JSON.parseObject(row.feature());

                double[] paramsCode = fmModel.getModelParams(map);
                String str = Arrays.toString(paramsCode);

                ret = row.label() + ":" + str;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static List<String> getFeatureCode(String modelId, List<LabeledFeature> rowList) {
        List<String> ret = new ArrayList<>();
        try {
            FMModel fmModel = getJModel(modelId);
            if (fmModel != null && rowList != null) {
                for (LabeledFeature labeledFeature : rowList) {
                    Map<String, String> map = (Map) JSON.parseObject(labeledFeature.feature());

                    double[] paramsCode = fmModel.getModelParams(map);
                    String strTmp = CollectionUtil.toString(paramsCode);
                    if (strTmp != null) {
                        System.out.println("labeledFeature.label()="+labeledFeature.label());
                        String retSub = labeledFeature.label() + strTmp;
                        ret.add(retSub);
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static FMModel getModelFromMD(String modelId) throws Exception {
        return ModelSave.getModelByKeyFromMD(modelId);
    }

    public static void syncModel(String onLinemodelId, String featureModelId, boolean isSync) throws Exception {

        if (isSync) {
            System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + "  sync model,featuerModelId=" + featureModelId + ",onlineModelId=" + onLinemodelId);
            FMModel fmModel = getJModel(featureModelId);

            System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + "  save model");
            ModelSave.saveModelByKeyToMD(onLinemodelId, fmModel);
        }

    }


    public static FMFTRLHyperParams getFMFTRLHyperParams(String featureModelId) {

        FMFTRLHyperParams pa =
                new FMFTRLHyperParams(0.1, 1.0, 6, 0.00001, 0.01, 1.0, 1.0);
        if (featureModelId.equals(ModelKeyEnum.FTRL_FM_CTR_MODEL_v001.getFeatureIndex())
                || featureModelId.equals(ModelKeyEnum.FTRL_FM_CVR_MODEL_v001.getFeatureIndex())
                ) {
            pa = new FMFTRLHyperParams(0.5, 1.0, 6, 0.00001, 0.01, 0.999999, 1.0);
        }

        if (featureModelId.equals(ModelKeyEnum.FTRL_FM_CTR_MODEL_v002.getFeatureIndex())
                || featureModelId.equals(ModelKeyEnum.FTRL_FM_CVR_MODEL_v002.getFeatureIndex())
                ) {
            pa = new FMFTRLHyperParams(0.5, 1.0, 6, 0.00001, 0.01, 0.999999, 1.0);
        }

        if (featureModelId.equals(ModelKeyEnum.FTRL_FM_CTR_MODEL_v003.getFeatureIndex())
                || featureModelId.equals(ModelKeyEnum.FTRL_FM_CVR_MODEL_v003.getFeatureIndex())
                ) {
            pa = new FMFTRLHyperParams(0.5, 1.0, 6, 0.00001, 0.01, 0.999999, 1.0);
        }

        if (featureModelId.equals(ModelKeyEnum.FTRL_FM_CTR_MODEL_v004.getFeatureIndex())
                || featureModelId.equals(ModelKeyEnum.FTRL_FM_CVR_MODEL_v004.getFeatureIndex())
                ) {
            pa = new FMFTRLHyperParams(0.5, 1.0, 6, 0.00001, 0.01, 1.0, 1.0);
        }

        if (featureModelId.equals(ModelKeyEnum.FTRL_FM_CTR_MODEL_v005.getFeatureIndex())
                || featureModelId.equals(ModelKeyEnum.FTRL_FM_CVR_MODEL_v005.getFeatureIndex())
                ) {
            pa = new FMFTRLHyperParams(0.1, 1.0, 6, 0.00001, 0.01, 0.999999, 1.0);
        }

        return pa;

    }
}

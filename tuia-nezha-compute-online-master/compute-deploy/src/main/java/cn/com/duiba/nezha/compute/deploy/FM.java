package cn.com.duiba.nezha.compute.deploy;

import cn.com.duiba.nezha.compute.alg.ftrl.FMModel;
import cn.com.duiba.nezha.compute.biz.bo.PsBo;

import java.util.HashMap;

public class FM {


    public static void main(String[] args) {
//        PSFM.main(args);

        try {

//            String modelId = ModelKeyEnum.FTRL_FM_CTR_MODEL_v002.getIndex();
//
//            FMModel fmModel = new FMModel();
//            List<FeatureBaseCode> codeList = FeatureCoder.getFeatureCode(modelId);
//            fmModel.setFeatureBaseCode(codeList);
//
////            System.out.println("codeList=" + JSONObject.toJSONString(codeList));
//
//            System.out.println("fmModel=" + JSONObject.toJSONString(fmModel));
//
//            String ms = JSON.toJSONString(fmModel);
//            FMModel fmModel2 = JSON.parseObject(ms, FMModel.class);
////
////
////
//
//            System.out.println("fmModel2=" + JSONObject.toJSONString(fmModel2));

//
            FMModel model = PsBo.getModel("mid_ftrl_fm_ctr_v002");
            System.out.println("model.key=" + model.getModelId() +
                    ",updateTime=" + model.getUpdateTime() +
//                    ",codeList="+ JSON.toJSONString(model.getFeatureBaseCode())+
                    ",pred null=" + model.predict(new HashMap<>()));


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

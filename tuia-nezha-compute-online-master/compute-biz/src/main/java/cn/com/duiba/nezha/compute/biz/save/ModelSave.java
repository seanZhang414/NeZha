package cn.com.duiba.nezha.compute.biz.save;

import cn.com.duiba.nezha.compute.alg.ftrl.FMModel;
import cn.com.duiba.nezha.compute.biz.constant.PsConstant;
import cn.com.duiba.nezha.compute.biz.utils.mongodb.MongoUtil;
import cn.com.duiba.nezha.compute.core.util.AssertUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.Map;

public class ModelSave {







    /**
     * @param modelKey
     * @return
     */
    private static String getLastModelKey(String modelKey) {

        return "nz_last_model_" + modelKey + "_";
    }


    /**
     * @param modelKey
     * @param model
     * @return
     */
    public static void saveModelByKeyToMD(String modelKey, FMModel model) {

        if (AssertUtil.isAnyEmpty(modelKey, model)) {
            System.out.println("saveCTRLastModelByKeyToES empty,modelKey=" + modelKey);
            return;
        }
        try {
            // 获取缓存Key
            String key = getLastModelKey(modelKey);
            // 保存
            Map<String, FMModel> map = new HashMap<>();
            map.put(key, model);
            System.out.println("save model with key" + key);
            MongoUtil.bulkWriteUpdateT(PsConstant.MODEL_TYPE, map,"model update");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * @param modelKey
     * @return
     */
    public static FMModel getModelByKeyFromMD(String modelKey) {
        FMModel ret = null;

        if (AssertUtil.isAnyEmpty(modelKey)) {
            System.out.println("getCTRModelByKeyFromMD empty,modelKey=" + modelKey);
            return ret;
        }
        try {
            // 获取缓存Key
            String key = getLastModelKey(modelKey);
            System.out.println("read model with key="+key);
            // 保存
            ret = MongoUtil.findByIdT(PsConstant.MODEL_TYPE, key, FMModel.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}

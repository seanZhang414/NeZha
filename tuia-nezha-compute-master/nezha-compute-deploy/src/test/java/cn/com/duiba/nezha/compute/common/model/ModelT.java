package cn.com.duiba.nezha.compute.common.model;

import cn.com.duiba.nezha.compute.alg.FM;
import cn.com.duiba.nezha.compute.alg.util.ReplayerUtil;
import cn.com.duiba.nezha.compute.api.dto.AdvertModelEntity;
import cn.com.duiba.nezha.compute.api.enums.ModelKeyEnum;
import cn.com.duiba.nezha.compute.biz.bo.AdvertCtrLrModelBo;
import cn.com.duiba.nezha.compute.common.enums.DateStyle;
import cn.com.duiba.nezha.compute.common.util.DateUtil;
import com.alibaba.fastjson.JSON;

import java.util.Map;

/**
 * Created by pc on 2017/10/12.
 */
public class ModelT {

    private static String offLineFeaturemapStr="{\"f602001\":\"4\",\"f605001\":\"1\",\"f603001\":\"4\",\"f604001\":\"4\",\"f606001\":\"1\",\"f607001\":\"0\",\"f110001\":\"1\",\"f501001\":\"ios\",\"f608001\":\"0\",\"f502001\":\"11\",\"f108001\":\"2587\",\"f502002\":\"3\",\"f611001\":\"4\",\"f201001\":\"26893\",\"f102001\":\"21815\",\"f101001\":\"13277\",\"f505001\":\"4500+\",\"f106001\":\"2943\",\"f503001\":\"2302\",\"f609001\":\"1\",\"f601001\":\"4\",\"f301001\":\"173\",\"f306001\":\"2\"}";

    private static String onLineFeaturemapStr="{\"f602001\":\"3\",\"f604001\":\"3\",\"f606001\":\"1\",\"f501001\":\"IOS\",\"f608001\":\"0\",\"f113001\":\"40103\",\"f610001\":\"0\",\"cf101201\":\"1327726893\",\"cf101301\":\"13277173\",\"f201001\":\"26893\",\"f102001\":\"21815\",\"f106001\":\"2943\",\"f104001\":\"null\",\"f504001\":\"IPHONE\",\"f609001\":\"0\",\"f601001\":\"4\",\"f301001\":\"173\",\"f303001\":\"1\",\"f605001\":\"1\",\"f603001\":\"4\",\"f110001\":\"1\",\"f607001\":\"0\",\"f502001\":\"11\",\"f108001\":\"2587\",\"f502002\":\"3\",\"f611001\":\"4\",\"f403004\":\"0\",\"f101001\":\"13277\",\"f505001\":\"4500+\",\"f503001\":\"2302\",\"f302001\":\"173\",\"f306001\":\"2\"}";

    public static void main(String[] args){

        testPredict();
        testFeatureDiff();
    }

    public static  void testPredict() {

        AdvertModelEntity entity = AdvertCtrLrModelBo.getCTRDtModelByKeyToMD(ModelKeyEnum.FM_CTR_MODEL_v003.getIndex(), "2017-10-12");
        if (null == entity) {
            System.out.println("the model entity is null.");
            return;
        }
        System.out.println("entity.getDt()=" + JSON.toJSONString(entity.getDt()));

        final FM lrModel = new FM(entity);


        Map<String, String> featureIdxMap = (Map) JSON.parseObject(onLineFeaturemapStr);

        System.out.println("start time = " + DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS));
        for (int i = 0; i < 2; i++) {
            System.out.println("i = " + i);
            Double ret3 = lrModel.predict(featureIdxMap);
            System.out.println("ret3 = " + ret3);
        }
        System.out.println("end time = " + DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS));

        ReplayerUtil replayerUtil = new ReplayerUtil();
        replayerUtil.setModel(lrModel);
        replayerUtil.predictWithInfo(featureIdxMap);


    }

    public static  void testFeatureDiff() {


        Map<String, String> offLineFeaturemap = (Map) JSON.parseObject(offLineFeaturemapStr);
        Map<String, String> onLineFeaturemap = (Map) JSON.parseObject(onLineFeaturemapStr);

        for(Map.Entry<String,String> entry:offLineFeaturemap.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();
            boolean equal = true;
            if(value==null && onLineFeaturemap.get(key)!=null){
                equal = false;
            }

            if(value!=null && !value.equalsIgnoreCase(onLineFeaturemap.get(key))){
                equal = false;
            }

            if(!equal){
                print(key,value,onLineFeaturemap.get(key),equal);
            }

        }

    }
    public static  void print(String feature,String offlineValue,String onlineValue,boolean equal) {
        System.out.println("Feature="+feature+",equal="+equal+",offlinevalue="+offlineValue+",onlinevalue="+onlineValue);
    }
}

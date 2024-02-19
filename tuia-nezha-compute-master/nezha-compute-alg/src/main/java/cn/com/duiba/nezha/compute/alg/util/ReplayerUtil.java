package cn.com.duiba.nezha.compute.alg.util;


import cn.com.duiba.nezha.compute.alg.FM;
import cn.com.duiba.nezha.compute.api.PredResultVo;
import cn.com.duiba.nezha.compute.api.point.Point;
import cn.com.duiba.nezha.compute.api.vo.AlgFeatureVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.spark.mllib.linalg.Matrix;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pc on 2016/12/21.
 */
public class ReplayerUtil implements Serializable {

    private static final long serialVersionUID = -316102112618444922L;


    private FM model = null;
    private Map<String, AlgFeatureVo> featuremap = null;

    public void setModel(FM model) {
        this.model = model;
    }

    public FM getModel() {
        return this.model;
    }

    public void setFeaturemap() {
        this.featuremap = setAlgFeatureMap();
    }

    public Map<String, AlgFeatureVo> getFeaturemap() {
        if (featuremap == null) {
            setFeaturemap();
        }
        return this.featuremap;
    }


    public List<Point.ModelFeature> getFeatureInfoList() throws Exception {
        List<Point.ModelFeature> ret = new ArrayList<>();
        if (featuremap == null) {
            setFeaturemap();
        }
        for (AlgFeatureVo algFeatureVo : featuremap.values()) {
            Point.ModelFeature modelFeature = new Point.ModelFeature(algFeatureVo.getFeatureId(),
                    algFeatureVo.getCategory(),
                    algFeatureVo.getFeatureCategorySize(),
                    algFeatureVo.getIndex(),
                    algFeatureVo.getSubIndex(),
                    algFeatureVo.getIntercept(),
                    algFeatureVo.getWeight(),
                    algFeatureVo.getFactor());

            ret.add(modelFeature);

        }
        return ret;
    }


    public void predictWithInfo(Map<String, String> featureIdxMap) {
        PredResultVo ret = new PredResultVo();

        Map<String, AlgFeatureVo> featureMap = new HashMap<>();
        try {

            List<String> featureIdxList = getModel().getFeatureIdxList();
            List<String> featureCollectionList = getModel().getFeatureCollectionList();


            for (String featureIdx : featureIdxList) {

                if (featureCollectionList.contains(featureIdx) && featureIdxMap.get(featureIdx) != null) {
                    String[] categoryArray = featureIdxMap.get(featureIdx).split(",", 0);
                    for (String category : categoryArray) {
                        print(featureIdx, category);
                    }
                } else {
                    print(featureIdx, featureIdxMap.get(featureIdx));
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void print(String feature, String category) {
        try {

            if (category != null) {
                category = category.toLowerCase();
            }

            if (getFeaturemap().containsKey(feature + "_" + category)) {

                System.out.println(feature + "_" + category + "," + JSONObject.toJSONString(getFeaturemap().get(feature + "_" + category)));
            } else {
                System.out.println(feature + "_" + category + "," + JSONObject.toJSONString(getFeaturemap().get(feature + "_" + null)));
            }
        } catch (Exception e) {
        }

    }


    public Map<String, AlgFeatureVo> setAlgFeatureMap() {
        Map<String, AlgFeatureVo> ret = new HashMap<>();


        try {

            List<String> featureIdxList = getModel().getFeatureIdxList();

            int totalFeatureNums = featureIdxList.size();

            Point.FMModelParams params = getModel().getModelUtil().getModel().getFMModelParams();

            org.apache.spark.mllib.linalg.Vector w = params.w();
            Matrix v = params.v();
            Double w0 = params.w0();

            int sizeSum = 0;
            for (int i = 0; i < totalFeatureNums; i++) {


                String featureIdx = featureIdxList.get(i);

                List<String> categoryList = getModel().getDictUtil().getFeature(featureIdx);

                if (categoryList == null) {
                    categoryList = new ArrayList<>();
                }
                int categorySize = categoryList.size();
                int categoryIdxNull = sizeSum;
                Double categoryWNull = w.apply(categoryIdxNull);
                List<Double> categoryVNull = new ArrayList<>();

                for (int k = 0; k < v.numCols(); k++) {
                    categoryVNull.add(v.apply(categoryIdxNull, k));
                }


                //组装
                AlgFeatureVo algFeatureVo1 = new AlgFeatureVo();
                algFeatureVo1.setFeatureId(featureIdx);
                algFeatureVo1.setFactor(JSONObject.toJSONString(categoryVNull));
                algFeatureVo1.setIndex((long) categoryIdxNull);
                algFeatureVo1.setWeight(categoryWNull);
                algFeatureVo1.setCategory(null);
                algFeatureVo1.setFeatureCategorySize((long) categorySize);
                algFeatureVo1.setSubIndex(-1L);
                algFeatureVo1.setIntercept(w0);

                ret.put(featureIdx + "_" + null, algFeatureVo1);


                for (int j = 0; j < categorySize; j++) {
                    String category = categoryList.get(j);
                    int categoryIdx = sizeSum + j + 1;
                    Double categoryW = w.apply(categoryIdx);
                    List<Double> categoryV = new ArrayList<>();

                    for (int k2 = 0; k2 < v.numCols(); k2++) {
                        categoryV.add(v.apply(categoryIdx, k2));
                    }

                    //组装
                    AlgFeatureVo algFeatureVo = new AlgFeatureVo();
                    algFeatureVo.setFeatureId(featureIdx);
                    algFeatureVo.setFactor(JSONObject.toJSONString(categoryV));
                    algFeatureVo.setIndex((long) categoryIdx);
                    algFeatureVo.setWeight(categoryW);
                    algFeatureVo.setCategory(category);
                    algFeatureVo.setFeatureCategorySize((long) categorySize);
                    algFeatureVo.setSubIndex((long) j);
                    algFeatureVo.setIntercept(w0);
                    ret.put(featureIdx + "_" + category, algFeatureVo);


                }

                sizeSum += categorySize + 1;
            }


        } catch (Exception e) {

            e.printStackTrace();
        }
        return ret;
    }


    public static void getFactorList(Matrix v) {

    }

}

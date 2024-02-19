package cn.com.duiba.nezha.compute.alg;


import cn.com.duiba.nezha.compute.alg.util.CategoryFeatureDictUtil;
import cn.com.duiba.nezha.compute.api.dict.CategoryFeatureDict;
import cn.com.duiba.nezha.compute.api.enums.SerializerEnum;
import cn.com.duiba.nezha.compute.mllib.model.SparseFMModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

/**
 * Created by pc on 2016/12/22.
 */
public class BaseAlgorithm implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(BaseAlgorithm.class);
    private CategoryFeatureDictUtil categoryFeatureDictUtil = null;
    private List<String> featureIdxList = null;

    private List<String> featureCollectionList = null;

    public BaseAlgorithm() {
    }


    public List<String> getFeatureIdxList() {
        if (this.featureIdxList == null) {
            return null;
        } else {
            return this.featureIdxList;
        }
    }


    public CategoryFeatureDictUtil getDictUtil() {
        if (this.categoryFeatureDictUtil == null) {
            this.categoryFeatureDictUtil = new CategoryFeatureDictUtil();
        }
        return this.categoryFeatureDictUtil;
    }


    public void setFeatureDict(CategoryFeatureDict dict) {
        getDictUtil().setFeatureDict(dict);
    }

    public void setFeatureDict(String dict, SerializerEnum serializerEnum) {
        System.out.println("setFeatureDict");
        getDictUtil().setFeatureDict(dict, serializerEnum);
    }

    public void setModel(SparseFMModel model) {
    }

    public void setModel(String modelStr, SerializerEnum serializerEnum) {
    }


    public void setFeatureIdxList(List<String> featureIdxList) {

        this.featureIdxList = featureIdxList;
    }

    public void setFeatureIdxList(String featureIdxStr, SerializerEnum serializerEnum) {
        System.out.println("setFeatureIdxList");
        this.featureIdxList = getDictUtil().getFeatureIdxList(featureIdxStr, serializerEnum);

    }

    public String getFeatureIdxListStr(SerializerEnum serializerEnum) {
        return getDictUtil().featureIdxList2Str(getFeatureIdxList(), serializerEnum);
    }

    public void setFeatureCollectionList(List<String> featureCollectionList) {

        this.featureCollectionList = featureCollectionList;
    }

    public void setFeatureCollectionList(String featureCollectionStr, SerializerEnum serializerEnum) {
        System.out.println("setFeatureCollectionList");
        this.featureCollectionList = getDictUtil().getFeatureCollectionList(featureCollectionStr, serializerEnum);

    }

    public String getFeatureCollectionListStr(SerializerEnum serializerEnum) {
        return getDictUtil().featureCollectionList2Str(getFeatureCollectionList(), serializerEnum);
    }

    public List<String> getFeatureCollectionList() {
        if (this.featureCollectionList == null) {
            return null;
        } else {
            return this.featureCollectionList;
        }
    }


    public String getFeatureDictStr(SerializerEnum serializerEnum) {
        return getDictUtil().getFeatureDictStr(serializerEnum);
    }

    public String getModelStr(SerializerEnum serializerEnum) {
        return "this model";
    }


}

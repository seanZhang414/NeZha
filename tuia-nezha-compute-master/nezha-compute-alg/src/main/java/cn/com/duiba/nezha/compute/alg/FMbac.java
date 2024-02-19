package cn.com.duiba.nezha.compute.alg;


import cn.com.duiba.nezha.compute.alg.util.CategoryFeatureDictUtil;
import cn.com.duiba.nezha.compute.alg.util.FMModelUtil;
import cn.com.duiba.nezha.compute.alg.vo.VectorResult;
import cn.com.duiba.nezha.compute.api.PredResultVo;
import cn.com.duiba.nezha.compute.api.dict.CategoryFeatureDict;
import cn.com.duiba.nezha.compute.api.dto.AdvertModelEntity;
import cn.com.duiba.nezha.compute.api.enums.SerializerEnum;
import cn.com.duiba.nezha.compute.mllib.model.SparseFMModel;
import org.apache.spark.mllib.linalg.SparseVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by pc on 2016/12/22.
 */
public class FMbac implements Serializable ,IAlgorithm{
    private static final Logger logger = LoggerFactory.getLogger(FMbac.class);
    private CategoryFeatureDictUtil categoryFeatureDictUtil = null;
    private FMModelUtil modelUtil = null;
    private List<String> featureIdxList = null;

    public FMbac() {
    }

    public FMbac(String featureIdxList, String dict, String modelStr, SerializerEnum serializerEnum) {
        setFeatureDict(dict, serializerEnum);
        setModel(modelStr, serializerEnum);
        setFeatureIdxList(featureIdxList, serializerEnum);
    }

    public FMbac(AdvertModelEntity entity) {
        SerializerEnum serializerEnum = entity.getSerializerId() == SerializerEnum.KRYO.getIndex() ? SerializerEnum.KRYO : SerializerEnum.JAVA_ORIGINAL;
        setFeatureDict(entity.getFeatureDictStr(), serializerEnum);
        setModel(entity.getModelStr(), serializerEnum);
        setFeatureIdxList(entity.getFeatureIdxListStr(), serializerEnum);
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

    public FMModelUtil getModelUtil() {
        if (this.modelUtil == null) {
            this.modelUtil = new FMModelUtil();
        }
        return this.modelUtil;
    }

    public void setFeatureDict(CategoryFeatureDict dict) {
        getDictUtil().setFeatureDict(dict);
    }

    public void setFeatureDict(String dict, SerializerEnum serializerEnum) {
        getDictUtil().setFeatureDict(dict, serializerEnum);
    }

    public void setModel(SparseFMModel model) {
        getModelUtil().setModel(model);
    }

    public void setModel(String modelStr, SerializerEnum serializerEnum) {
        getModelUtil().setModel(modelStr, serializerEnum);
    }


    public void setFeatureIdxList(List<String> featureIdxList) {

        this.featureIdxList = featureIdxList;
    }

    public void setFeatureIdxList(String featureIdxStr, SerializerEnum serializerEnum) {

        this.featureIdxList = getDictUtil().getFeatureIdxList(featureIdxStr, serializerEnum);

    }

    public String getFeatureIdxListStr(SerializerEnum serializerEnum) {
        return getDictUtil().featureIdxList2Str(getFeatureIdxList(), serializerEnum);
    }

    public String getFeatureDictStr(SerializerEnum serializerEnum) {
        return getDictUtil().getFeatureDictStr(serializerEnum);
    }

    public String getModelStr(SerializerEnum serializerEnum) {

        return getModelUtil().getModelStr(serializerEnum);
    }


    public Double predict(List<String> categoryList) {
        Double ret = null;
        try {
            SparseVector sv = getDictUtil().oneHotSparseVectorEncode(getFeatureIdxList(), categoryList);
            if (sv != null) {
                ret = getModelUtil().predict(sv);
            }
        } catch (Exception e) {
            logger.error("predict happend error", e);
        }
        return ret;
    }

    public Double predict(Map<String, String> categoryMap) {
        Double ret = null;
        try {

            SparseVector sv = getDictUtil().oneHotSparseVectorEncodeWithMap(getFeatureIdxList(), categoryMap);
            if (sv != null) {
                ret = getModelUtil().predict(sv);
            }
        } catch (Exception e) {
            logger.error("predict happend error", e);
        }

        return ret;
    }
    public PredResultVo predictWithInfo(Map<String, String> categoryMap) {
        PredResultVo ret = new PredResultVo();

        return ret;
    }


    public Double predictWithVector(SparseVector vector) {
        Double ret = null;
        try {
            if (vector != null) {
                System.out.println("vector " + vector);
                ret = getModelUtil().predict(vector);
            }
        } catch (Exception e) {
            logger.error("predict happend error", e);
        }

        return ret;
    }
}

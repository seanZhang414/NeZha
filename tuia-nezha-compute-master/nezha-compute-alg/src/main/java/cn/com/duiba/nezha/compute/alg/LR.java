package cn.com.duiba.nezha.compute.alg;


import cn.com.duiba.nezha.compute.alg.util.LogisticRegressionModelUtil;
import cn.com.duiba.nezha.compute.alg.vo.VectorResult;
import cn.com.duiba.nezha.compute.api.PredResultVo;
import cn.com.duiba.nezha.compute.api.dto.AdvertModelEntity;
import cn.com.duiba.nezha.compute.api.enums.SerializerEnum;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.linalg.SparseVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by pc on 2016/12/22.
 */
public class LR extends BaseAlgorithm implements Serializable, IAlgorithm {
    private static final Logger logger = LoggerFactory.getLogger(LR.class);
    private LogisticRegressionModelUtil modelUtil = null;

    public LogisticRegressionModelUtil getModelUtil() {
        if (this.modelUtil == null) {
            this.modelUtil = new LogisticRegressionModelUtil();
        }
        return this.modelUtil;
    }

    public LR() {
    }


    public LR(String featureIdxList, String dict, String modelStr, String featureCollectionList, SerializerEnum serializerEnum) {
        setFeatureDict(dict, serializerEnum);
        setModel(modelStr, serializerEnum);
        setFeatureIdxList(featureIdxList, serializerEnum);
        setFeatureCollectionList(featureCollectionList, serializerEnum);
    }

    public LR(AdvertModelEntity entity) {
        SerializerEnum serializerEnum = entity.getSerializerId() == SerializerEnum.KRYO.getIndex() ? SerializerEnum.KRYO : SerializerEnum.JAVA_ORIGINAL;
        setFeatureDict(entity.getFeatureDictStr(), serializerEnum);
        setModel(entity.getModelStr(), serializerEnum);
        setFeatureIdxList(entity.getFeatureIdxListStr(), serializerEnum);
        setFeatureCollectionList(entity.getFeatureCollectListStr(), serializerEnum);
    }
    public void setEntity(AdvertModelEntity entity) {
        SerializerEnum serializerEnum = entity.getSerializerId() == SerializerEnum.KRYO.getIndex() ? SerializerEnum.KRYO : SerializerEnum.JAVA_ORIGINAL;
        setFeatureDict(entity.getFeatureDictStr(), serializerEnum);
        setFeatureIdxList(entity.getFeatureIdxListStr(), serializerEnum);
        setFeatureCollectionList(entity.getFeatureCollectListStr(), serializerEnum);
        setModel(entity.getModelStr(), serializerEnum);
    }

    public void setModel(LogisticRegressionModel model) {
        getModelUtil().setModel(model);
    }

    public void setModel(String modelStr, SerializerEnum serializerEnum) {
        getModelUtil().setModel(modelStr, serializerEnum);
    }
    public String getModelStr(SerializerEnum serializerEnum) {
        return getModelUtil().getModelStr(serializerEnum);
    }

    public Double predict(List<String> categoryList) {
        Double ret = null;

        try {
            VectorResult vr = getDictUtil().oneHotSparseVectorEncode(getFeatureIdxList(), categoryList, getFeatureCollectionList());
            if (vr != null && vr.getVector() != null) {
                ret = getModelUtil().predict(vr.getVector());
            }
        } catch (Exception e) {
            logger.error("predict happend error", e);
        }
        return ret;
    }

    public Double predict(Map<String, String> categoryMap) {
        Double ret = null;
        try {
            VectorResult vr = getDictUtil().oneHotSparseVectorEncodeWithMap(getFeatureIdxList(), categoryMap, getFeatureCollectionList());
            if (vr != null && vr.getVector() != null) {
                ret = getModelUtil().predict(vr.getVector());
            }
        } catch (Exception e) {
            logger.error("predict happend error", e);
        }

        return ret;
    }

    @Override
    public PredResultVo predictWithInfo(Map<String, String> categoryMap) {
        PredResultVo ret = new PredResultVo();
        try {

            VectorResult vr = getDictUtil().oneHotSparseVectorEncodeWithMap(getFeatureIdxList(), categoryMap, getFeatureCollectionList());
            if (vr != null && vr.getVector() != null) {
                Double predValue = predictWithVector(vr.getVector());
                ret.setPredValue(predValue);
                ret.setNewFeatureNums(vr.getNewFeatureNums());
                ret.setTotalFeatureNums(vr.getTotalFeatureNums());


            }
        } catch (Exception e) {
            logger.error("predict happend error", e);
        }

        return ret;
    }

    public Double predictWithVector(SparseVector vector) {
        Double ret = null;
        try {
            if (vector != null) {
                ret = getModelUtil().predict(vector);
            }
        } catch (Exception e) {
            logger.error("predict happend error", e);
        }

        return ret;
    }
}

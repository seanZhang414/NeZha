package cn.com.duiba.nezha.compute.alg.util;

import cn.com.duiba.nezha.compute.api.enums.SerializerEnum;
import cn.com.duiba.nezha.compute.common.util.serialize.KryoUtil;
import cn.com.duiba.nezha.compute.common.util.serialize.SerializeTool;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.linalg.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Created by pc on 2016/12/21.
 */
public class LogisticRegressionModelUtil implements Serializable {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private static final long serialVersionUID = -316102112618444922L;

    private LogisticRegressionModel model = null;


    public void setModel(LogisticRegressionModel model) {
        this.model = model;
    }

    public void setModel(String modelStr, SerializerEnum serializerEnum) {
        this.model = getModel(modelStr, serializerEnum);
    }


    public LogisticRegressionModel getModel() {
        return this.model;
    }

    public LogisticRegressionModel getModel(String src, SerializerEnum serializerEnum) {

        LogisticRegressionModel ret = null;

        if (serializerEnum == SerializerEnum.JAVA_ORIGINAL) {
            ret = SerializeTool.getObjectFromString(src);
        }
        if (serializerEnum == SerializerEnum.KRYO) {
            ret = KryoUtil.deserializationObject(src, LogisticRegressionModel.class);
        }

        return ret;
    }

    public String getModelStr(LogisticRegressionModel model, SerializerEnum serializerEnum) {
        String ret = null;
        if (serializerEnum == SerializerEnum.JAVA_ORIGINAL) {
            ret = SerializeTool.object2String(model);
        }
        if (serializerEnum == SerializerEnum.KRYO) {
            ret = KryoUtil.serializationObject(model);
        }
        return ret;
    }

    public String getModelStr(SerializerEnum serializerEnum) {
        return getModelStr(model, serializerEnum);
    }

    public Double predict(Vector data) {
        if (getModel() == null) {
            return null;
        } else {
            return getModel().predict(data);
        }
    }


}

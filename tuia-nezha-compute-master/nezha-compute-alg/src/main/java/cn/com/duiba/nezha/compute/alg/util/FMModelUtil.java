package cn.com.duiba.nezha.compute.alg.util;



import cn.com.duiba.nezha.compute.api.enums.SerializerEnum;
import cn.com.duiba.nezha.compute.common.util.serialize.KryoUtil;
import cn.com.duiba.nezha.compute.common.util.serialize.SerializeTool;
import cn.com.duiba.nezha.compute.mllib.model.SparseFMModel;

import org.apache.spark.mllib.linalg.SparseVector;

import java.io.Serializable;

/**
 * Created by pc on 2016/12/21.
 */
public class FMModelUtil implements Serializable {

    private static final long serialVersionUID = -316102112618444922L;

    private SparseFMModel model = null;


    public void setModel(SparseFMModel model) {
        this.model = model;
    }

    public void setModel(String modelStr, SerializerEnum serializerEnum) {
        this.model = getModel(modelStr, serializerEnum);
    }


    public SparseFMModel getModel() {
//        this.model.clearThreshold();
        return this.model;
    }

    public SparseFMModel getModel(String src, SerializerEnum serializerEnum) {

        SparseFMModel ret = null;

        if (serializerEnum == SerializerEnum.JAVA_ORIGINAL) {
            ret = SerializeTool.getObjectFromString(src);
        }
        if (serializerEnum == SerializerEnum.KRYO) {
            ret = KryoUtil.deserializationObject(src, SparseFMModel.class);
        }

        return ret;
    }

    public String getModelStr(SparseFMModel model, SerializerEnum serializerEnum) {
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

    public Double predict(SparseVector data) {
        if (getModel() == null) {
            return null;
        } else {
            return getModel().predict(data);
        }
    }


}

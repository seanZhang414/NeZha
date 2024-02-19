package cn.com.duiba.nezha.compute.alg;


import cn.com.duiba.nezha.compute.api.dto.AdvertModelEntity;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * Created by pc on 2016/12/22.
 */
public class FMTest implements Serializable {
    private static final long serialVersionUID = -316102112618444133L;
    public FM model = null;
    public  AdvertModelEntity entity = null;


    public  FM getModel() {
        if (model == null) {
            System.out.println("getModel");

            model = new FM();
            model.setEntity(entity);
            System.out.print("model.getFeatureIdxList()=");
            System.out.println(JSONObject.toJSONString(model.getFeatureIdxList()));
        }

        return model;
    }

    public  void setEntity(AdvertModelEntity entity2) {
        entity = entity2;
    }

    public  AdvertModelEntity getEntity() {
        return entity;
    }


}

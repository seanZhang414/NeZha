package cn.com.duiba.nezha.compute.biz.dao;


import cn.com.duiba.nezha.compute.biz.entity.model.AdvertCtrLrModelEvaluateEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by pc on 2016/11/16.
 */
public interface IAdvertCtrLrModelEvaluateDao {

    void insert(AdvertCtrLrModelEvaluateEntity entity);

    void insertBatch(List<AdvertCtrLrModelEvaluateEntity> entityList);

    AdvertCtrLrModelEvaluateEntity selectModelByKey(String modelKey);

    List<AdvertCtrLrModelEvaluateEntity> selectModelByKeyAndDt(Map<String, Object> map);

    void deleteModelByKeyAndDt(Map<String, Object> map);

}
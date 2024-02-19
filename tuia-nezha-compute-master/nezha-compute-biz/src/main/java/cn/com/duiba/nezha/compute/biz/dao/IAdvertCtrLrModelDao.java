package cn.com.duiba.nezha.compute.biz.dao;


import cn.com.duiba.nezha.compute.api.dto.AdvertModelEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by pc on 2016/11/16.
 */
public interface IAdvertCtrLrModelDao {

    void insert(AdvertModelEntity entity);

    void insertBatch(List<AdvertModelEntity> entityList);

    AdvertModelEntity selectModelByKey(String modelKey);

    List<AdvertModelEntity> selectModelByKeyAndDt(Map<String, Object> map);

    void deleteModelByKeyAndDt(Map<String, Object> map);

}
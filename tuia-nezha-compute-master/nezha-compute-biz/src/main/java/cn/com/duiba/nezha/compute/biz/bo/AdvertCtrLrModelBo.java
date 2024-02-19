package cn.com.duiba.nezha.compute.biz.bo;

import cn.com.duiba.nezha.compute.api.cachekey.ModelKey;
import cn.com.duiba.nezha.compute.api.constant.GlobalConstant;
import cn.com.duiba.nezha.compute.api.dto.AdvertModelEntity;
import cn.com.duiba.nezha.compute.biz.conf.ElasticSearchUtilConf;
import cn.com.duiba.nezha.compute.biz.conf.JedisPoolConf;
import cn.com.duiba.nezha.compute.biz.constant.ProjectConstant;
import cn.com.duiba.nezha.compute.biz.dao.BaseDao;
import cn.com.duiba.nezha.compute.biz.dao.IAdvertCtrLrModelDao;
import cn.com.duiba.nezha.compute.biz.utils.jedis.JedisUtil;
import cn.com.duiba.nezha.compute.biz.utils.mongodb.MongoUtil;
import cn.com.duiba.nezha.compute.common.util.AssertUtil;
import cn.com.duiba.nezha.compute.biz.utils.es.ElasticSearchUtil;
import com.alibaba.fastjson.JSON;
import org.apache.ibatis.session.SqlSession;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pc on 2017/3/6.
 */
public class AdvertCtrLrModelBo {
    private static JedisUtil jedisCient = new JedisUtil(JedisPoolConf.jedisConfig1);

    private static String index = GlobalConstant.LR_MODEL_ES_INDEX;
    private static String type = GlobalConstant.LR_MODEL_ES_TYPE;

    public static ElasticSearchUtil elasticSearchUtil = new ElasticSearchUtil(ElasticSearchUtilConf.esConfig);
//    public static JedisUtil jedisUtil = new JedisUtil(JedisPoolConf.jedisConfig1);
//    public static MongoClientUtil mongoClientUtil = new MongoClientUtil(MongoDbConf.config);

    /**
     * @param modelKey
     * @return
     */
    public static AdvertModelEntity getCTRLastModelByKeyFromES(final String modelKey) {

        AdvertModelEntity advertModelEntity = null;
        List<AdvertModelEntity> entityList = null;
        if (AssertUtil.isAnyEmpty(modelKey)) {
            return advertModelEntity;
        }

        try {
            // 获取缓存Key
            String key = ModelKey.getLastModelKey(modelKey);
            //判断对应的推荐候选集是否存在于缓存中
            advertModelEntity = elasticSearchUtil.getValueT(index, type, key, AdvertModelEntity.class);
            // 从缓存获取推荐
            System.out.println("getCTRLastModelByKeyFromES ,key = " + key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return advertModelEntity;
    }

    /**
     * @param modelKey
     * @return
     */
    public static AdvertModelEntity getCTRDtModelByKeyFromES(final String modelKey, String dt) {

        AdvertModelEntity advertModelEntity = null;
        List<AdvertModelEntity> entityList = null;
        if (AssertUtil.isAnyEmpty(modelKey)) {
            return advertModelEntity;
        }

        try {
            // 获取缓存Key
            String key = ModelKey.getDtModelKey(modelKey, dt);
            //判断对应的推荐候选集是否存在于缓存中
            advertModelEntity = elasticSearchUtil.getValueT(index, type, key, AdvertModelEntity.class);
            // 从缓存获取推荐
            System.out.println("getCTRLastModelByKeyFromES ,key = " + key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return advertModelEntity;
    }


    /**
     * @param modelKey
     * @param entity
     * @return
     */
    public static void saveCTRLastModelByKeyToRedis(String modelKey, AdvertModelEntity entity) {

        if (AssertUtil.isAnyEmpty(modelKey, entity)) {
            System.out.println("saveCTRLastModelByKeyToES empty,modelKey=" + modelKey);
            return;
        }
        try {
            // 获取缓存Key
            String lastKey = ModelKey.getLastModelKey(modelKey);
            //保存
//            jedisUtil.setex(lastKey, JSON.toJSONString(entity), ProjectConstant.WEEK_2_EXPIRE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param modelKey
     * @param dt
     * @param entity
     * @return
     */
    public static void saveCTRDtModelByKeyToRedis(String modelKey, String dt, AdvertModelEntity entity) {

        if (AssertUtil.isAnyEmpty(modelKey, entity)) {
            System.out.println("saveCTRLastModelByKeyToES empty,modelKey=" + modelKey);
            return;
        }
        try {
            // 获取缓存Key
            String key = ModelKey.getDtModelKey(modelKey, dt);
            //保存
//            jedisUtil.setex(key, JSON.toJSONString(entity), ProjectConstant.WEEK_2_EXPIRE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param modelKey
     * @param entity
     * @return
     */
    public static void saveCTRLastModelByKeyToES(String modelKey, AdvertModelEntity entity) {

        if (AssertUtil.isAnyEmpty(modelKey, entity)) {
            System.out.println("saveCTRLastModelByKeyToES empty,modelKey=" + modelKey);
            return;
        }
        try {
            // 获取缓存Key
            String lastKey = ModelKey.getLastModelKey(modelKey);
            //保存
            elasticSearchUtil.upsertT(index, type, lastKey, entity, ProjectConstant.MONTH_1_EXPIRE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param modelKey
     * @param dt
     * @param entity
     * @return
     */
    public static void saveCTRDtModelByKeyToES(String modelKey, String dt, AdvertModelEntity entity) {

        if (AssertUtil.isAnyEmpty(modelKey, entity)) {
            System.out.println("saveCTRLastModelByKeyToES empty,modelKey=" + modelKey);
            return;
        }
        try {
            // 获取缓存Key
            String key = ModelKey.getDtModelKey(modelKey, dt);
            //保存
            elasticSearchUtil.upsertT(index, type, key, entity, ProjectConstant.MONTH_1_EXPIRE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param modelKey
     * @return
     */
    public static AdvertModelEntity getCTRLastModelByKey(final String modelKey) {

        AdvertModelEntity advertModelEntity = null;
        List<AdvertModelEntity> entityList = null;
        if (AssertUtil.isAnyEmpty(modelKey)) {
            return advertModelEntity;
        }

        try {
            // 获取缓存Key
            String key = ModelKey.getLastModelKey(modelKey);

            //判断对应的推荐候选集是否存在于缓存中

            if (jedisCient.exists(key)) {
                // 从缓存获取推荐
                System.out.println("getCTRLastModelByKey cache exits,key = " + key + "read redis");
                advertModelEntity = jedisCient.get(key, AdvertModelEntity.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (AssertUtil.isNotEmpty(entityList)) {
//            advertModelEntity = entityList.get(0);
        }

        return advertModelEntity;

    }

    /**
     * @param modelKey
     * @return
     */
    public static AdvertModelEntity getCTRDtModelByKey(String modelKey, String dt) {

        AdvertModelEntity advertModelEntity = null;
        List<AdvertModelEntity> entityList = null;
        if (AssertUtil.isAnyEmpty(modelKey, dt)) {
            System.out.println("cn.com.duiba.nezha.compute.biz.param invalid ");
            return advertModelEntity;
        }

        try {
            // 获取缓存Key
            String key = ModelKey.getDtModelKey(modelKey, dt);

            //判断对应的推荐候选集是否存在于缓存中

            if (jedisCient.exists(key)) {
                // 从缓存获取推荐
                System.out.println("getCTRDtModelByKey cache exits,key = " + key + "read redis");
                entityList = jedisCient.getList(key, AdvertModelEntity.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (AssertUtil.isNotEmpty(entityList)) {
            advertModelEntity = entityList.get(0);
        }
        return advertModelEntity;
    }

    /**
     * @param modelKey
     * @return
     */
    public static void savaCTRLastModelByKey(String modelKey, AdvertModelEntity entity) {

        AdvertModelEntity advertModelEntity = null;
        List<AdvertModelEntity> entityList = null;
        if (AssertUtil.isAnyEmpty(modelKey, entity)) {
        }
        try {
            // 获取缓存Key
            String lastKey = ModelKey.getLastModelKey(modelKey);
            //保存
//            jedisCient.setex(lastKey, JSON.toJSONString(entity), ProjectConstant.MONTH_1_EXPIRE);
            jedisCient.setex(lastKey, JSON.toJSONString(entity), ProjectConstant.YEAR_1_EXPIRE);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param modelKey
     * @return
     */
    public static void savaCTRDtModelByKey(String modelKey, String dt, AdvertModelEntity entity) {

        List<AdvertModelEntity> entityList = null;
        if (AssertUtil.isAnyEmpty(modelKey, entity)) {
            return;
        }
        try {
            // 获取缓存Key
            String dtKey = ModelKey.getDtModelKey(modelKey, dt);
            entityList = Arrays.asList(entity);
            //保存
            jedisCient.setex(dtKey, JSON.toJSONString(entityList), ProjectConstant.MONTH_1_EXPIRE);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * @param entity
     * @return
     */
    public static void savaCTRLastModelLocal(AdvertModelEntity entity) {


        SqlSession session = BaseDao.getSessionFactory().openSession();
        try {
            System.out.println("save model ");
            IAdvertCtrLrModelDao dao = session.getMapper(IAdvertCtrLrModelDao.class);
            dao.insert(entity);
        } finally {
            session.close();
        }
    }

    /**
     * @param modelKey
     * @return
     */
    public static AdvertModelEntity getCTRLastModelLocal(String modelKey) {
        AdvertModelEntity entity = null;

        SqlSession session = BaseDao.getSessionFactory().openSession();
        try {
            System.out.println("get model ");
            IAdvertCtrLrModelDao dao = session.getMapper(IAdvertCtrLrModelDao.class);
            entity = dao.selectModelByKey(modelKey);
        } finally {
            session.close();
        }
        return entity;
    }


    /**
     * @param modelKey
     * @param entity
     * @return
     */
    public static void saveCTRLastModelByKeyToMD(String modelKey, AdvertModelEntity entity) {

        if (AssertUtil.isAnyEmpty(modelKey, entity)) {
            System.out.println("saveCTRLastModelByKeyToES empty,modelKey=" + modelKey);
            return;
        }
        try {
            // 获取缓存Key
            String key = ModelKey.getLastModelKey(modelKey);
            // 保存
            Map<String, AdvertModelEntity> map = new HashMap<>();
            map.put(key, entity);
            System.out.println("save model with key" + key);
            MongoUtil.bulkWriteUpdateT(type, map,"model update");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param modelKey
     * @param dt
     * @param entity
     * @return
     */
    public static void saveCTRDtModelByKeyToMD(String modelKey, String dt, AdvertModelEntity entity) {

        if (AssertUtil.isAnyEmpty(modelKey, entity)) {
            System.out.println("saveCTRLastModelByKeyToES empty,modelKey=" + modelKey);
            return;
        }
        try {
            // 获取缓存Key
            String key = ModelKey.getDtModelKey(modelKey, dt);
            // 保存
            Map<String, AdvertModelEntity> map = new HashMap<>();
            map.put(key, entity);

            System.out.println("save model with key="+key);
            MongoUtil.bulkWriteUpdateT(type, map,"model update");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param modelKey
     * @return
     */
    public static AdvertModelEntity getCTRModelByKeyFromMD(String modelKey) {
        AdvertModelEntity ret = null;

        if (AssertUtil.isAnyEmpty(modelKey)) {
            System.out.println("getCTRModelByKeyFromMD empty,modelKey=" + modelKey);
            return ret;
        }
        try {
            // 获取缓存Key
            String key = ModelKey.getLastModelKey(modelKey);
            System.out.println("read model with key="+key);
            // 保存
            ret = MongoUtil.findByIdT(type, key, AdvertModelEntity.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * @param modelKey
     * @param dt
     * @return
     */
    public static AdvertModelEntity getCTRDtModelByKeyToMD(String modelKey, String dt) {
        AdvertModelEntity ret = null;
        if (AssertUtil.isAnyEmpty(modelKey)) {
            System.out.println("getCTRDtModelByKeyToMD empty,modelKey=" + modelKey);
            return ret;
        }
        try {
            // 获取缓存Key
            String key = ModelKey.getDtModelKey(modelKey, dt);
            // 保存
            ret = MongoUtil.findByIdT(type, key, AdvertModelEntity.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }


}

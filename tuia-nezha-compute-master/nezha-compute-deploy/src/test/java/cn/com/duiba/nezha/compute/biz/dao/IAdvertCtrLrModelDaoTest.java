package cn.com.duiba.nezha.compute.biz.dao;

import cn.com.duiba.nezha.compute.alg.util.CategoryFeatureDictUtil;
import cn.com.duiba.nezha.compute.api.dict.CategoryFeatureDict;
import cn.com.duiba.nezha.compute.api.dto.AdvertModelEntity;
import cn.com.duiba.nezha.compute.api.enums.SerializerEnum;
import cn.com.duiba.nezha.compute.biz.bo.AdvertCtrLrModelBo;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pc on 2017/2/4.
 */
public class IAdvertCtrLrModelDaoTest extends TestCase {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    public void testInsert() throws Exception {
        SqlSession session = BaseDao.getSessionFactory().openSession();
        try {
            CategoryFeatureDict dicto = new CategoryFeatureDict();
            dicto.setFeature("f01", Arrays.asList("10", "20"));
            dicto.setFeature("f02", Arrays.asList("11","20"));
            dicto.setFeature("f03", Arrays.asList("12","20"));
            dicto.setFeature("f04", Arrays.asList("13", "20"));
            CategoryFeatureDictUtil util = new CategoryFeatureDictUtil();
            String dictStr = util.getFeatureDictStr(dicto, SerializerEnum.JAVA_ORIGINAL);


            IAdvertCtrLrModelDao dao = session.getMapper(IAdvertCtrLrModelDao.class);
            AdvertModelEntity c = new AdvertModelEntity();
            c.setDt("20170117");
            c.setFeatureDictStr(dictStr);
            c.setFeatureIdxListStr("13rw ");
            c.setModelKey("t001");
            c.setModelStr("1215111211");
            dao.insert(c);

            AdvertModelEntity entity = AdvertCtrLrModelBo.getCTRLastModelLocal("t002");

            util.setFeatureDict(entity.getFeatureDictStr(), SerializerEnum.JAVA_ORIGINAL);
            System.out.println("entity = " + JSON.toJSONString(entity));
            System.out.println("dict = " + JSON.toJSONString(util.getFeatureDict()));

        } finally {
            session.close();
        }
    }

    @Test
    public void testInsertBatch() throws Exception {
        SqlSession session = BaseDao.getSessionFactory().openSession();
        try {
            IAdvertCtrLrModelDao dao = session.getMapper(IAdvertCtrLrModelDao.class);
            AdvertModelEntity c = new AdvertModelEntity();
            c.setDt("20170117");
            c.setFeatureDictStr("sdadad");
            c.setFeatureIdxListStr("13rw ");
            c.setModelKey("k003");
            c.setModelStr("1215111211");
            dao.insert(c);

            dao.insertBatch(Arrays.asList(c));

        } finally {
            session.close();
        }
    }

    @Test
    public void testSelectModelByKey() throws Exception {
        SqlSession session = BaseDao.getSessionFactory().openSession();
        try {
            IAdvertCtrLrModelDao user = session.getMapper(IAdvertCtrLrModelDao.class);
            AdvertModelEntity c = user.selectModelByKey("k001");
            if (null == c) {
                System.out.println("the result is null.");
            } else {
                System.out.println(JSON.toJSONString(c));
            }
        } finally {
            session.close();
        }
    }

    @Test
    public void testSelectModelByKeyAndDt() throws Exception {
        SqlSession session = BaseDao.getSessionFactory().openSession();
        try {
            IAdvertCtrLrModelDao dao = session.getMapper(IAdvertCtrLrModelDao.class);
            Map<String,Object> map = new HashMap<>();
            map.put("modelKey","k002");
            map.put("dt","20170117");
            List<AdvertModelEntity> c = dao.selectModelByKeyAndDt(map);
            if (null == c) {
                System.out.println("the result is null.");
            } else {
                System.out.println(JSON.toJSONString(c));
            }
        } finally {
            session.close();
        }
    }

    public void testDeleteModelByKeyAndDt() throws Exception {

        SqlSession session = BaseDao.getSessionFactory().openSession();
        try {
            IAdvertCtrLrModelDao dao = session.getMapper(IAdvertCtrLrModelDao.class);
            Map<String,Object> map = new HashMap<>();
            map.put("modelKey","k002");
            map.put("dt","20170117");
            dao.deleteModelByKeyAndDt(map);
        } finally {
            session.close();
        }

    }
}
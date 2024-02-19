package cn.com.duiba.nezha.compute.biz.dao;

import cn.com.duiba.nezha.compute.biz.entity.model.AdvertCtrLrModelEvaluateEntity;
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
 * Created by pc on 2017/1/17.
 */
public class IAdvertCtrLrModelEvaluateDaoTest extends TestCase {

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
            IAdvertCtrLrModelEvaluateDao dao = session.getMapper(IAdvertCtrLrModelEvaluateDao.class);
            AdvertCtrLrModelEvaluateEntity c = new AdvertCtrLrModelEvaluateEntity();
            c.setDt("20170117");
            c.setModelKey("f001");
            c.setTestAuprc(0.7);
            c.setTestAuroc(0.75);
            c.setTestNums(100);
            c.setTraingNums(2000);
            dao.insert(c);

        } finally {
            session.close();
        }
    }

    @Test
    public void testInsertBatch() throws Exception {
        SqlSession session = BaseDao.getSessionFactory().openSession();
        try {
            IAdvertCtrLrModelEvaluateDao dao = session.getMapper(IAdvertCtrLrModelEvaluateDao.class);
            AdvertCtrLrModelEvaluateEntity c = new AdvertCtrLrModelEvaluateEntity();
            c.setDt("20170117");
            c.setModelKey("f004");
            c.setTestAuprc(0.7);
            c.setTestAuroc(0.75);
            c.setTestNums(100);
            c.setTraingNums(2000);

            dao.insertBatch(Arrays.asList(c));

        } finally {
            session.close();
        }
    }

    @Test
    public void testSelectModelByKey() throws Exception {
        SqlSession session = BaseDao.getSessionFactory().openSession();
        try {
            IAdvertCtrLrModelEvaluateDao dao = session.getMapper(IAdvertCtrLrModelEvaluateDao.class);
            AdvertCtrLrModelEvaluateEntity c = dao.selectModelByKey("f001");
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
            IAdvertCtrLrModelEvaluateDao dao = session.getMapper(IAdvertCtrLrModelEvaluateDao.class);
            Map<String, Object> map = new HashMap<>();
            map.put("modelKey", "f002");
            map.put("dt", "20170117");
            List<AdvertCtrLrModelEvaluateEntity> c = dao.selectModelByKeyAndDt(map);
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
            IAdvertCtrLrModelEvaluateDao dao = session.getMapper(IAdvertCtrLrModelEvaluateDao.class);
            Map<String, Object> map = new HashMap<>();
            map.put("modelKey", "f002");
            map.put("dt", "20170117");
            dao.deleteModelByKeyAndDt(map);
        } finally {
            session.close();
        }
    }

}
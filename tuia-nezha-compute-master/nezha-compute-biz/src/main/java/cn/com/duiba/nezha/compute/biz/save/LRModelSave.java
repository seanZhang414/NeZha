package cn.com.duiba.nezha.compute.biz.save;


import cn.com.duiba.nezha.compute.api.dto.AdvertModelEntity;
import cn.com.duiba.nezha.compute.biz.dao.BaseDao;
import cn.com.duiba.nezha.compute.biz.dao.IAdvertCtrLrModelDao;
import cn.com.duiba.nezha.compute.biz.dao.IAdvertCtrLrModelEvaluateDao;
import cn.com.duiba.nezha.compute.biz.entity.model.AdvertCtrLrModelEvaluateEntity;
import cn.com.duiba.nezha.compute.common.util.AssertUtil;
import org.apache.ibatis.session.SqlSession;

/**
 * Created by pc on 2017/1/17.
 */
public class LRModelSave {


    public static void modelSave(AdvertModelEntity entity) {
        SqlSession session = BaseDao.getSessionFactory().openSession();
        try {

            if (AssertUtil.isNotEmpty(entity)) {
                IAdvertCtrLrModelDao dao = session.getMapper(IAdvertCtrLrModelDao.class);
                dao.insert(entity);
            }


        } catch (Exception e) {
            System.out.println("modelSave happend error");
        } finally {
            session.close();
        }
    }

    public static void modelEvaluateSave(AdvertCtrLrModelEvaluateEntity entity) {
        SqlSession session = BaseDao.getSessionFactory().openSession();
        try {
            if (AssertUtil.isNotEmpty(entity)) {
                IAdvertCtrLrModelEvaluateDao dao = session.getMapper(IAdvertCtrLrModelEvaluateDao.class);
                dao.insert(entity);
            }

        } catch (Exception e) {
            System.out.println("modelEvaluateSave happend error");
        } finally {
            session.close();
        }
    }
}



package cn.com.duiba.nezha.compute.biz.dao;

import cn.com.duiba.nezha.compute.biz.constant.ProjectConstant;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.Reader;

/**
 * Created by pc on 2016/11/16.
 */
public class BaseDao {
    private static SqlSessionFactory sqlSessionFactory;
    private static Reader reader;

    static {
        try {
            reader = Resources.getResourceAsReader(ProjectConstant.MYBATIS_CONFIG_PATH);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SqlSessionFactory getSessionFactory() {
        return sqlSessionFactory;
    }

}

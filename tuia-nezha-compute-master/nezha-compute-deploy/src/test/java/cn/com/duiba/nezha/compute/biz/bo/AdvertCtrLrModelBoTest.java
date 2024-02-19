package cn.com.duiba.nezha.compute.biz.bo;

import cn.com.duiba.nezha.compute.api.dto.AdvertModelEntity;
import junit.framework.TestCase;

/**
 * Created by pc on 2017/3/7.
 */
public class AdvertCtrLrModelBoTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testSavaCTRLastModel() throws Exception {
        AdvertModelEntity entity = new AdvertModelEntity();
        entity.setDt("2017");
        entity.setFeatureDictStr("ffdd");
        entity.setFeatureIdxListStr("idx");
        entity.setModelKey("test");
        entity.setModelStr("str");
        AdvertCtrLrModelBo.savaCTRLastModelLocal(entity);


    }
}
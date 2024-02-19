package cn.com.duiba.nezha.compute.biz.utils.mongodb;

import cn.com.duiba.api.HttpClient;

import cn.com.duiba.api.MongodbCollection;
import cn.com.duiba.api.MongodbDts;
import cn.com.duiba.nezha.compute.api.cachekey.AdvertStatKey;
import cn.com.duiba.nezha.compute.api.cachekey.FeatureKey;
import cn.com.duiba.nezha.compute.api.cachekey.ModelKey;
import cn.com.duiba.nezha.compute.api.constant.GlobalConstant;

import cn.com.duiba.nezha.compute.api.dto.AdvertModelEntity;
import cn.com.duiba.nezha.compute.api.enums.ModelKeyEnum;
import cn.com.duiba.nezha.compute.biz.bo.AdvertCtrLrModelBo;
import cn.com.duiba.nezha.compute.biz.replay.BaseReplayer;
import cn.com.duiba.nezha.compute.common.util.DateUtil;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pc on 2017/6/21.
 */
public class MongoUtilTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testGetMongoDb() throws Exception {

//        Map<String, String> value = new HashMap<>();
//        value.put("a","1");
//        value.put("b","2");
//        value.put("c","3");
//        value.put("d","4");
//        value.put("e","5");
//        Map<String, Map<String, String>> data = new HashMap<>();
//        data.put("test", value);
//        MongoUtil.getMongoDb().batchSave(MongodbCollection.userFeature, data,"test");
//        System.out.println(MongoUtil.getMongoDb().findById(MongodbCollection.userFeature, "test"));
    }

    public void testBulkWriteUpdateT() throws Exception {

//    AdvertCtrLrModelBo.saveCTRLastModelByKeyToMD(ModelKeyEnum.FM_CVR_MODEL_v002.getIndex(), entity);
    }

    public void testFindByIdT() throws Exception {
//
//        String docKey = AdvertStatKey.getAdvertStatMongoDbKey("9357","30108");
//
//        String ret = MongoUtil.getMongoDb().findById(GlobalConstant.AD_CTR_STAT_ES_TYPE, docKey);
//        System.out.println("coll=" + GlobalConstant.AD_CTR_STAT_ES_TYPE + ",key=" + docKey + "," + ret);
//        String consumerId2 = "1658965433";
//        String activityDimId = null;
//
//        String mainKey = FeatureKey.getConsumerOrderFeatureRedisKey(
//                consumerId2,
//                activityDimId);
//
//        String ret = MongoUtil.getMongoDb().findById(GlobalConstant.CONSUMER_FEATURE_ES_TYPE, mainKey);
//
//        System.out.println("ConsumerOrderFeatureDto="+ret);

    }


    public void testModelSYNC() throws Exception {
//
        // http://10.111.200.11
        MongodbDts onlineMongoDb = new MongodbDts("http://10.10.93.196", "c31de3ab31034259bba06658682954f3");

        String model = ModelKeyEnum.FM_BE_CVR_MODEL_v001.getIndex();

        // 获取缓存Key  LR_CVR_MODEL_002   LR_CTR_MODEL_005 nz_last_model_mid_fm_ctr_v001_
        String key = ModelKey.getLastModelKey(model);
//        String key = ModelKey.getDtModelKey(model, "2018-03-22");

        // 读取
        String rstr = onlineMongoDb.findById(GlobalConstant.LR_MODEL_ES_TYPE, key);

        AdvertModelEntity entity = JSON.parseObject(rstr, AdvertModelEntity.class);
        System.out.println("entity = "+entity.getDt());
//
        // 保存
        System.out.println("start time = " + DateUtil.getCurrentTime());
        AdvertCtrLrModelBo.saveCTRLastModelByKeyToMD(model, entity);
        System.out.println("end time = " + DateUtil.getCurrentTime());
    }


    public void testGetMap() throws Exception {
//        BaseReplayer.replay(ModelKeyEnum.LR_CTR_MODEL_v004.getIndex());
    }
}
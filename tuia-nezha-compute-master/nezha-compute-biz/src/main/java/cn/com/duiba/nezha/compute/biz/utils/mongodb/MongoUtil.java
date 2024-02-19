package cn.com.duiba.nezha.compute.biz.utils.mongodb;

import cn.com.duiba.api.MongodbDts;
import cn.com.duiba.nezha.compute.biz.conf.MongoDbConf;
import cn.com.duiba.nezha.compute.common.util.AssertUtil;
import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pc on 2017/6/20.
 */
public class MongoUtil {

    public static MongodbDts mongoDb = new MongodbDts(MongoDbConf.whost, MongoDbConf.wkey);

    public static MongodbDts getMongoDb() {
        return mongoDb;
    }

    public static <T> void bulkWriteUpdateT(String collectionName, Map<String, T> map, String biz, int bulkSize) {

        try {
            if (AssertUtil.isNotEmpty(map)) {

                int count = 0;
                Map<String, Map<String, String>> mongoMap = new HashMap<>();
                for (Map.Entry<String, T> entry : map.entrySet()) {
                    count++;
                    String key = entry.getKey();
                    T valueT = entry.getValue();
                    Map<String, String> value = getMap(valueT);

                    if (AssertUtil.isNotEmpty(value)) {
                        mongoMap.put(key, value);
                    }else{
                        System.out.println("WARN key=" + key+",value=null");
                    }

                    if (count >= bulkSize) {
                        bulkWriteUpdate(collectionName, mongoMap, biz);
                        mongoMap.clear();
                        count = 0;
                    }


                }

                if (AssertUtil.isNotEmpty(mongoMap)) {
                    bulkWriteUpdate(collectionName, mongoMap, biz);
                }

            }

        } catch (Exception e) {

            System.out.println("e=" + e);

        }
    }


    public static <T> void bulkWriteUpdateT(String collectionName, Map<String, T> map, String biz) {
        bulkWriteUpdateT(collectionName, map, biz, 500);
    }

    public static void bulkWriteUpdate(String collectionName, Map<String, Map<String, String>> mongoMap, String biz) {
        if (AssertUtil.isNotEmpty(mongoMap)) {
            getMongoDb().batchSave(collectionName, mongoMap, biz);
        }

    }

    public static <T> T findByIdT(String collectionName, String id, Class<T> clazz) {
        T ret = null;
        try {
            if (AssertUtil.isNotEmpty(id)) {
                String rstr = getMongoDb().findById(collectionName, id);
                ret = JSON.parseObject(rstr, clazz);
            }

        } catch (Exception e) {

            System.out.println("e=" + e);

        }
        return ret;
    }


    public static <T> Map<String, String> getMap(T t) {
        Map<String, String> ret = null;
        String jString = JSON.toJSONString(t);

        ret = (Map) JSON.parse(jString);

        return ret;

    }

}

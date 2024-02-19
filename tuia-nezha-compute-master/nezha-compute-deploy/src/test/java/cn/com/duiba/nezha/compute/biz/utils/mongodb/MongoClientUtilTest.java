package cn.com.duiba.nezha.compute.biz.utils.mongodb;

import cn.com.duiba.nezha.compute.api.dto.AdvertAppStatDto;
import cn.com.duiba.nezha.compute.api.dto.AdvertCtrStatDto;
import cn.com.duiba.nezha.compute.biz.conf.MongoDbConf;
import com.alibaba.fastjson.JSON;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import junit.framework.TestCase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pc on 2017/5/22.
 */
public class MongoClientUtilTest extends TestCase {
    MongoClientUtil util = null;

    public void setUp() throws Exception {
        super.setUp();
        System.out.println("mongoConfig=" + JSON.toJSONString(MongoDbConf.config));
        util = new MongoClientUtil(MongoDbConf.config);


    }

    public void tearDown() throws Exception {

    }

    public void testBulkWriteUpdateString() throws Exception {
        String dbName = "tuia-prd";
        String collectionName = "test";
        AdvertAppStatDto dto1 = new AdvertAppStatDto();
        Map<String, String> map = new HashMap<>();
        dto1.setAdvertId(1001L);
        dto1.setAppId(1000L);
        AdvertCtrStatDto sub = new AdvertCtrStatDto();
        sub.setCtr(0.69);
        dto1.setAppCdStat(sub);
        dto1.setKey("test_03");

        map.put("test_03", JSON.toJSONString(dto1));

        util.bulkWriteUpdateString(dbName, collectionName, map);

        MongoCollection<Document> mc = util.getCollection(dbName, collectionName);
        List<Document> documents = new ArrayList<>();
        Document doc= Document.parse(JSON.toJSONString(dto1));
        doc.append("_id", "test_03");

        documents.add(doc);
        FindIterable<Document> iterable = mc.find();
        printResult("find all", iterable);

    }
    //打印查询的结果集
    public void printResult(String doing, FindIterable<Document> iterable) {
        System.out.println(doing);
        iterable.forEach(new Block<Document>() {
            public void apply(final Document document) {
                System.out.println(document);
            }
        });
        System.out.println("------------------------------------------------------");
        System.out.println();
    }
}
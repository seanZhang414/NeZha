package cn.com.duiba.nezha.compute.biz.utils.mongodb;


import cn.com.duiba.nezha.compute.api.constant.GlobalConstant;
import cn.com.duiba.nezha.compute.common.util.AssertUtil;
import com.alibaba.fastjson.JSON;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by pc on 2016/11/18.
 */
public class MongoClientUtil {

    private MongoConfig config;

    public MongoClientUtil(MongoConfig mongoConfig) {
        this.config = mongoConfig;
    }

    public MongoClient getClient() {
        MongoClient mongoClient = null;
        try {
            mongoClient = MongoPoolUtil.getInstance().getClient(config);
        } catch (Exception e) {
            System.out.println("mongoClient error " + e);
        }
        return mongoClient;
    }

    public MongoDatabase getDb(String dbName) {
        return getClient().getDatabase(dbName);
    }

    public MongoCollection<Document> getCollection(String dbName, String collectionName) {
        return getDb(dbName).getCollection(collectionName);
    }


    /**
     * 批量插入
     *
     * @param filterDocument
     */
    public <T> List<T> find(Document filterDocument, Class<T> clazz, MongoCollection<Document> collection) {
        FindIterable<T> iterable = collection.find(filterDocument, clazz);
        return parseFindIterable(iterable);

    }

    /**
     * 批量插入
     *
     * @param iterable
     */
    public <T> List<T> parseFindIterable(FindIterable<T> iterable) {
        final List<T> ret = new ArrayList<>();
        if (iterable != null) {
            iterable.forEach(new Block<T>() {
                public void apply(final T t) {
                    ret.add(t);
                }
            });
        }
        return ret;
    }


    /**
     * 批量插入
     *
     * @param document
     */
    public void insertOne(Document document, MongoCollection<Document> collection) {
        collection.insertOne(document);
        System.out.println("文档插入成功");
    }


    /**
     * 批量插入
     *
     * @param documents
     */
    public void insertMany(List<Document> documents, MongoCollection<Document> collection) {
        collection.insertMany(documents);
        System.out.println("文档插入成功");
    }

    /**
     * 批量插入
     *
     * @param documents
     */
    public void bulkWriteInsert(List<Document> documents, MongoCollection<Document> collection) {
        List<WriteModel<Document>> requests = new ArrayList<WriteModel<Document>>();
        for (Document document : documents) {
            //构造插入单个文档的操作模型
            InsertOneModel<Document> iom = new InsertOneModel<Document>(document);
            requests.add(iom);
        }
        BulkWriteResult bulkWriteResult = collection.bulkWrite(requests);
        System.out.println(bulkWriteResult.toString());
    }

    /**
     * 批量删除
     *
     * @param documents
     */
    private void bulkWriteDelete(List<Document> documents, MongoCollection<Document> collection) {
        List<WriteModel<Document>> requests = new ArrayList<WriteModel<Document>>();
        for (Document document : documents) {
            //删除条件
            Document queryDocument = new Document("_id", document.get("_id"));
            //构造删除单个文档的操作模型，
            DeleteOneModel<Document> dom = new DeleteOneModel<Document>(queryDocument);
            requests.add(dom);
        }
        BulkWriteResult bulkWriteResult = collection.bulkWrite(requests);
    }

    /**
     * 批量更新
     *
     * @param documents
     */
    public void bulkWriteUpdate(List<Document> documents, MongoCollection<Document> collection) {
        List<WriteModel<Document>> requests = new ArrayList<WriteModel<Document>>();
        for (Document document : documents) {
            //更新条件
            Document queryDocument = new Document("_id", document.get("_id"));
            document.append(GlobalConstant.MD_CAT_INDEX, new Date());
            Document updateDocument = new Document("$set", document);

            //构造更新单个文档的操作模型
            UpdateOneModel<Document> uom = new UpdateOneModel<Document>(queryDocument, updateDocument, new UpdateOptions().upsert(true));
            //UpdateOptions代表批量更新操作未匹配到查询条件时的动作，默认false，什么都不干，true时表示将一个新的Document插入数据库，他是查询部分和更新部分的结合
            requests.add(uom);
        }
        BulkWriteResult bulkWriteResult = collection.bulkWrite(requests);
    }

    /**
     * 更新
     *
     * @param document
     */
    public void writeUpdate(Document document, MongoCollection<Document> collection) {
        List<WriteModel<Document>> requests = new ArrayList<WriteModel<Document>>();

        //更新条件
        Document queryDocument = new Document("_id", document.get("_id"));
        document.append(GlobalConstant.MD_CAT_INDEX, new Date());
        Document updateDocument = new Document("$set", document);
        System.out.println("doc"+JSON.toJSONString(document));
        UpdateResult result = collection.updateOne(queryDocument, updateDocument, new UpdateOptions().upsert(true));

    }


    public void bulkWriteUpdateString(String dbName, String collectionName, Map<String, String> map) {

        try {
            if (AssertUtil.isNotEmpty(map)) {
                List<Document> documents = new ArrayList<>();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (AssertUtil.isNotEmpty(value)) {
                        Document doc = Document.parse(value);
                        doc.append("_id", key);
                        documents.add(doc);
                    }
                }
                if (documents.size() > 0) {
                    bulkWriteUpdate(documents, getCollection(dbName, collectionName));
                }
            }

        } catch (Exception e) {

        }
    }

    public <T> void bulkWriteUpdateT(String dbName, String collectionName, Map<String, T> map) {

        try {
            if (AssertUtil.isNotEmpty(map)) {
                List<Document> documents = new ArrayList<>();
                for (Map.Entry<String, T> entry : map.entrySet()) {
                    String key = entry.getKey();
                    T value = entry.getValue();
                    if (AssertUtil.isNotEmpty(value)) {
                        Document doc = Document.parse(JSON.toJSONString(value));
                        doc.append("_id", key);
                        documents.add(doc);
                    }
                }
                if (documents.size() > 0) {
                    bulkWriteUpdate(documents, getCollection(dbName, collectionName));
                }
            }

        } catch (Exception e) {

        }
    }


    /**
     * 更新
     */
    public void writeUpdateString(String dbName, String collectionName, String key, String jsonStr) {

        try {
            if (AssertUtil.isAllNotEmpty(key, jsonStr)) {
                Document doc = Document.parse(jsonStr);
                doc.append("_id", key);
                doc.append("key", key);
                writeUpdate(doc, getCollection(dbName, collectionName));
            }

        } catch (Exception e) {

        }
    }

    /**
     * 更新
     */
    public <T> void writeUpdateT(String dbName, String collectionName, String key, T object) {

        try {
            String jsonStr = JSON.toJSONString(object);
            writeUpdateString(dbName, collectionName, key, jsonStr);

        } catch (Exception e) {

        }
    }

}



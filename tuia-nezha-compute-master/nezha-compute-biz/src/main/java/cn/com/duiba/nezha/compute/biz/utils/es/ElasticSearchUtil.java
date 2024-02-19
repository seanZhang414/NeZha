package cn.com.duiba.nezha.compute.biz.utils.es;

/**
 * Created by pc on 2016/11/21.
 */

import cn.com.duiba.nezha.compute.common.util.AssertUtil;
import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ElasticSearchUtil {


    private ESConfig esConfig;

    private int TTLTime = 60 * 24 * 30 * 12;

    private static ElasticSearchUtil instance = null;

    public ElasticSearchUtil(ESConfig esConfig) {
        this.esConfig = esConfig;
    }


    /**
     * @return
     */
    private TransportClient getClient() {
        TransportClient client = null;
        try {
            client = ESPool.getClient(esConfig);
        } catch (Exception e) {
            System.out.println(e);
        }
        return client;
    }


    /**
     * 添加文档,并设置有效时长 Seconds粒度
     *
     * @param index
     * @param type
     * @param keyId
     * @param t
     * @return
     */

    public <T> IndexResponse indexT(String index, String type, String keyId, T t) {
        IndexResponse response = null;
        try {
            response = indexT(index, type, keyId, t, TTLTime);

        } catch (Exception e) {
            System.out.println(e);
        }
        return response;
    }

    /**
     * 添加文档,并设置有效时长 Seconds粒度
     *
     * @param index
     * @param type
     * @param keyId
     * @param t
     * @return
     */

    public <T> IndexResponse indexT(String index, String type, String keyId, T t, int ttl) {
        IndexResponse response = null;
        try {
            String jsonValue = JSON.toJSONString(t);
            response = index(index, type, keyId, jsonValue, ttl);
        } catch (Exception e) {
            System.out.println(e);
        }
        return response;
    }


    /**
     * 添加文档,并设置有效时长 分钟粒度
     *
     * @param index
     * @param type
     * @param keyId
     * @param jsonValue
     * @return
     */

    public IndexResponse index(String index, String type, String keyId, String jsonValue, int ttl) {
        IndexResponse response = null;
        try {
            if (AssertUtil.isAllNotEmpty(index,type,keyId,jsonValue)) {
                response = getClient().prepareIndex(index, type)
                        .setId(keyId)
                        .setSource(jsonValue)
                        .setTTL(TimeValue.timeValueMinutes(ttl))
                        .setTimeout(TimeValue.timeValueSeconds(5))
                        .execute()
                        .actionGet();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return response;
    }


    /**
     * 添加文档
     *
     * @param index
     * @param type
     * @param keyId
     * @param jsonValue
     * @return
     */

    public IndexResponse index(String index, String type, String keyId, String jsonValue) {
        IndexResponse response = null;
        try {
            if (AssertUtil.isAllNotEmpty(index, type, keyId)) {
                response = index(index, type, keyId, jsonValue, TTLTime);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return response;
    }


    /**
     * 添加文档
     *
     * @param index
     * @param type
     * @param jsonValueMap
     * @return
     */
    public <T> BulkResponse multiIndexT(String index, String type, Map<String, T> jsonValueMap, int bulkSize) {
        BulkResponse bulkResponse = null;
        try {
            bulkResponse = multiIndexT(index, type, jsonValueMap, bulkSize, TTLTime);
        } catch (Exception e) {
            System.out.println(e);
        }
        return bulkResponse;
    }


    /**
     * 添加文档
     *
     * @param index
     * @param type
     * @param tMap
     * @return
     */
    public <T> BulkResponse multiIndexT(String index, String type, Map<String, T> tMap, int bulkSize, int ttl) {
        BulkResponse bulkResponse = null;
        try {
            Map<String, String> jsonValueMap = new HashMap<>();
            if (tMap != null) {
                for (Map.Entry<String, T> entry : tMap.entrySet()) {
                    String keyId = entry.getKey();
                    T t = entry.getValue();
                    String jsonValue = JSON.toJSONString(t);
                    if (jsonValue != null) {
                        jsonValueMap.put(keyId, jsonValue);
                    }
                }
                bulkResponse = multiIndex(index, type, jsonValueMap, bulkSize, ttl);

            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return bulkResponse;
    }


    /**
     * 添加文档
     *
     * @param index
     * @param type
     * @param jsonValueMap
     * @return
     */
    public BulkResponse multiIndex(String index, String type, Map<String, String> jsonValueMap, int bulkSize) {
        BulkResponse bulkResponse = null;
        try {
            BulkRequestBuilder bulkRequest = getClient().prepareBulk();
            if (jsonValueMap != null) {
                bulkResponse = multiIndex(index, type, jsonValueMap, bulkSize, TTLTime);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return bulkResponse;
    }

    /**
     * 添加文档
     *
     * @param index
     * @param type
     * @param jsonValueMap
     * @return
     */
    public BulkResponse multiIndex(String index, String type, Map<String, String> jsonValueMap, int bulkSize, int ttl) {
        BulkResponse bulkResponse = null;
        try {
            BulkRequestBuilder bulkRequest = getClient().prepareBulk();
            int count = 0;
            if (jsonValueMap != null) {
                for (Map.Entry<String, String> entry : jsonValueMap.entrySet()) {
                    String keyId = entry.getKey();
                    String jsonValue = entry.getValue();
                    if (jsonValue != null) {
                        bulkRequest.add(getClient().prepareIndex(index, type)
                                        .setId(keyId)
                                        .setSource(jsonValue)
                                        .setTimeout(TimeValue.timeValueSeconds(5))
                                        .setTTL(TimeValue.timeValueSeconds(ttl)
                                        )
                        );
                        if (count % getBulkSize(bulkSize) == 0) {
                            bulkRequest.execute().actionGet();
                        }
                        count++;
                    }

                }
                bulkResponse = bulkRequest.execute().actionGet();

            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return bulkResponse;
    }


    /**
     * 更新文档
     *
     * @param index
     * @param type
     * @param keyId
     * @param t
     * @return
     */
    public <T> UpdateResponse upsertT(String index, String type, String keyId, T t) {
        UpdateResponse response = null;
        try {
            String jsonValue = JSON.toJSONString(t);
            response = upsert(index, type, keyId, jsonValue, TTLTime);

        } catch (Exception e) {
            System.out.println(e);
        }
        return response;
    }

    /**
     * 更新文档
     *
     * @param index
     * @param type
     * @param keyId
     * @param t
     * @return
     */
    public <T> UpdateResponse upsertT(String index, String type, String keyId, T t, int ttl) {
        UpdateResponse response = null;
        try {
            String jsonValue = JSON.toJSONString(t);
            response = upsert(index, type, keyId, jsonValue, ttl);

        } catch (Exception e) {
            System.out.println(e);
        }
        return response;
    }


    /**
     * 更新文档
     *
     * @param index
     * @param type
     * @param keyId
     * @param jsonValue
     * @return
     */
    public UpdateResponse update(String index, String type, String keyId, String jsonValue) {
        UpdateResponse response = null;
        try {
            response = getClient().prepareUpdate(index, type, keyId)
                    .setDoc(jsonValue).get();

        } catch (Exception e) {
            System.out.println(e);
        }
        return response;
    }


    /**
     * 更新文档
     *
     * @param index
     * @param type
     * @param keyId
     * @param jsonValue
     * @return
     */
    public UpdateResponse upsert(String index, String type, String keyId, String jsonValue) {
        UpdateResponse response = null;
        try {
            response = upsert(index, type, keyId, jsonValue, TTLTime);

        } catch (Exception e) {
            System.out.println(e);
        }
        return response;
    }


    /**
     * 更新文档
     *
     * @param index
     * @param type
     * @param keyId
     * @param jsonValue
     * @return
     */
    public UpdateResponse upsert(String index, String type, String keyId, String jsonValue, int ttl) {
        UpdateResponse response = null;
        try {
            if (AssertUtil.isAllNotEmpty(index, type, keyId, jsonValue)) {
                response = getClient().prepareUpdate(index, type, keyId)
                        .setDoc(jsonValue)
                        .setUpsert(jsonValue)
                        .setTimeout(TimeValue.timeValueSeconds(2))
                        .setTtl(TimeValue.timeValueSeconds(ttl))
                        .execute().actionGet();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return response;
    }


    /**
     * 批量更新
     *
     * @param index
     * @param type
     * @param jsonValueMap
     * @return
     */
    public BulkResponse multiUpsert(String index, String type, Map<String, String> jsonValueMap, int bulkSize, int ttl) {
        BulkResponse bulkResponse = null;
        try {
            BulkRequestBuilder bulkRequest = getClient().prepareBulk();
            int count = 0;
            if (AssertUtil.isAllNotEmpty(index, type, jsonValueMap)) {
                for (Map.Entry<String, String> entry : jsonValueMap.entrySet()) {
                    String keyId = entry.getKey();
                    String jsonValue = entry.getValue();


                    if (jsonValue != null) {
                        bulkRequest.add(getClient().prepareUpdate(index, type, keyId)
                                        .setUpsert(jsonValue)
                                        .setDoc(jsonValue)
                                        .setTimeout(TimeValue.timeValueSeconds(2))
                                        .setTtl(TimeValue.timeValueSeconds(ttl))
                        );
                        if (count % getBulkSize(bulkSize) == 0) {
                            bulkRequest.get();
                        }
                        count++;
                    }
                }

                bulkResponse = bulkRequest.get();

            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return bulkResponse;
    }


    /**
     * 批量更新
     *
     * @param index
     * @param type
     * @param jsonValueMap
     * @return
     */
    public BulkResponse multiUpsert(String index, String type, Map<String, String> jsonValueMap, int bulkSize) {
        BulkResponse bulkResponse = null;
        try {
            bulkResponse = multiUpsert(index, type, jsonValueMap, bulkSize, TTLTime);
        } catch (Exception e) {
            System.out.println(e);
        }

        return bulkResponse;
    }

    /**
     * 批量更新
     *
     * @param index
     * @param type
     * @param tMap
     * @return
     */
    public <T> BulkResponse multiUpsertT(String index, String type, Map<String, T> tMap, int bulkSize) {
        BulkResponse bulkResponse = null;
        try {
            bulkResponse = multiUpsertT(index, type, tMap, bulkSize, TTLTime);
        } catch (Exception e) {
            System.out.println(e);
        }

        return bulkResponse;
    }

    /**
     * 批量更新
     *
     * @param index
     * @param type
     * @param tMap
     * @return
     */
    public <T> BulkResponse multiUpsertT(String index, String type, Map<String, T> tMap, int bulkSize, int ttl) {
        BulkResponse bulkResponse = null;
        try {
            Map<String, String> jsonValueMap = new HashMap<>();
            if (tMap != null) {
                for (Map.Entry<String, T> entry : tMap.entrySet()) {
                    String keyId = entry.getKey();
                    T t = entry.getValue();
                    String jsonValue = JSON.toJSONString(t);

                    if (jsonValue != null) {
                        jsonValueMap.put(keyId, jsonValue);
                    }
                }
                bulkResponse = multiUpsert(index, type, jsonValueMap, bulkSize, ttl);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return bulkResponse;
    }

    /**
     * 查询文档
     *
     * @param index
     * @param type
     * @param keyId
     * @return
     */
    public Map<String, Object> getValueMap(String index, String type, String keyId) {
        Map<String, Object> ret = null;
        try {
            GetResponse response = get(index, type, keyId);
            if (response != null) {
                ret = response.getSourceAsMap();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return ret;
    }

    /**
     * 查询文档
     *
     * @param index
     * @param type
     * @param keyId
     * @return
     */
    public String getValueString(String index, String type, String keyId) {
        String ret = null;
        try {
            GetResponse response = get(index, type, keyId);
            if (response != null) {
                ret = response.getSourceAsString();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return ret;
    }

    /**
     * 查询文档
     *
     * @param index
     * @param type
     * @param keyId
     * @return
     */
    public <T> T getValueT(String index, String type, String keyId, Class<T> clazz) {
        T ret = null;
        try {
            GetResponse response = get(index, type, keyId);
            if (response != null) {
                String retStr = response.getSourceAsString();
                if (retStr != null) {
                    ret = JSON.parseObject(retStr, clazz);
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return ret;
    }


    /**
     * 查询文档
     *
     * @param index
     * @param type
     * @param keyId
     * @return
     */
    public <T> T getValueT(String index, String type, String keyId, Class<T> clazz,int timeOut) {
        T ret = null;
//        try {
            GetResponse response = get(index, type, keyId);
            if (response != null) {
                String retStr = response.getSourceAsString();
                if (retStr != null) {
                    ret = JSON.parseObject(retStr, clazz);
                }
            }

//        } catch (Exception e) {
//            System.out.println(e);
//        }
        return ret;
    }


    /**
     * 查询文档
     *
     * @param index
     * @param type
     * @param keyId
     * @return
     */
    public GetResponse get(String index, String type, String keyId) {
        GetResponse response = null;
        try {
            if (AssertUtil.isAllNotEmpty(index, type, keyId)) {
                response = get(index, type, keyId, 2);

            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return response;
    }

    /**
     * 查询文档
     *
     * @param index
     * @param type
     * @param keyId
     * @return
     */
    public GetResponse get(String index, String type, String keyId,int timeOut) {
        GetResponse response = null;
        try {
            if (AssertUtil.isAllNotEmpty(index, type, keyId)) {
                response = getClient().prepareGet(index, type, keyId)
                        .execute()
                        .actionGet(TimeValue.timeValueSeconds(timeOut));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return response;
    }




    /**
     * 查询文档
     *
     * @param index
     * @param type
     * @param keyIdList
     * @return
     */
    public <T> Map<String, T> multiGetValueT(String index, String type, List<String> keyIdList, Class<T> clazz) {
        Map<String, T> ret = null;

            Map<String, String> retMap = multiGetValueString(index, type, keyIdList);
            if (retMap != null) {
                ret = new HashMap<>();
                for (Map.Entry<String, String> entry : retMap.entrySet()) {
                    String keyId = entry.getKey();
                    String jsonStr = entry.getValue();
                    if (keyId != null && jsonStr != null) {
                        T t = JSON.parseObject(jsonStr, clazz);
                        ret.put(keyId, t);
                    }

                }
            }


        return ret;
    }


    /**
     * 查询文档
     *
     * @param index
     * @param type
     * @param keyIdList
     * @return
     */
    public Map<String, String> multiGetValueString(String index, String type, List<String> keyIdList) {
        Map<String, String> ret = null;
//        try {
            MultiGetResponse multiGetItemResponses = multiGet(index, type, keyIdList);

            if (multiGetItemResponses != null) {
                ret = new HashMap<>();
                for (MultiGetItemResponse itemResponse : multiGetItemResponses) {       //注释4
                    GetResponse response = itemResponse.getResponse();
                    if (response.isExists()) {                   //注释5
                        String id = response.getId();
                        String json = response.getSourceAsString();    //注释6
                        if (id != null && json != null) {
                            ret.put(id, json);
                        }
                    }
                }
            }

//        } catch (Exception e) {
//            System.out.println(e);
//        }
        return ret;
    }


    /**
     * 查询文档
     *
     * @param index
     * @param type
     * @param keyIdList
     * @return
     */
    public MultiGetResponse multiGet(String index, String type, List<String> keyIdList) {
        MultiGetResponse response = null;

        if (AssertUtil.isAllNotEmpty(index, type, keyIdList)) {
            response = getClient().prepareMultiGet()
                    .add(index, type, keyIdList)
                    .execute()
                    .actionGet(TimeValue.timeValueSeconds(1));
        }

        return response;
    }


    /**
     * 删除文档
     *
     * @param index
     * @param type
     * @param keyId
     * @return
     */
    public DeleteResponse delete(String index, String type, String keyId) {
        DeleteResponse response = null;
        try {
            if (AssertUtil.isAllNotEmpty(index, type, keyId)) {
                response = getClient().prepareDelete(index, type, keyId)
                        .setTimeout(TimeValue.timeValueSeconds(5))
                        .execute()
                        .actionGet();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return response;
    }


    /**
     * 删除文档
     *
     * @param index
     * @param type
     * @param keyIdList
     * @return
     */
    public BulkResponse multiDelete(String index, String type, List<String> keyIdList, int bulkSize) {
        BulkResponse bulkResponse = null;
        try {

            BulkRequestBuilder bulkRequest = getClient().prepareBulk();
            int count = 0;
            if (AssertUtil.isAllNotEmpty(index, type, keyIdList)) {
                for (String keyId : keyIdList) {
                    bulkRequest.add(getClient().prepareDelete(index, type, keyId).setTimeout(TimeValue.timeValueSeconds(5)));
                    if (count % getBulkSize(bulkSize) == 0) {
                        bulkRequest.get();
                    }
                    count++;
                }
                bulkResponse = bulkRequest.get();
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return bulkResponse;
    }


    private int getBulkSize(int bulkSize) {
        int ret = 1;
        if (bulkSize > 0) {
            ret = bulkSize;
        }
        return ret;
    }
}
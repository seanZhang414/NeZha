package cn.com.duiba.nezha.compute.biz.bo;

import cn.com.duiba.nezha.compute.biz.utils.hbase.HbaseUtil;
import cn.com.duiba.nezha.compute.common.util.AssertUtil;
import cn.com.duiba.nezha.compute.common.util.ObjectDynamicCreator;
import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HbaseBaseBo {


    /**
     * 插入最近活动信息
     *
     * @param rowKey
     * @param family
     * @param log
     * @throws Exception
     */
    public static <T> void insert(String tableName, String rowKey, String family, T log) throws Exception {

        if (AssertUtil.isAllNotEmpty(tableName, rowKey, family, log)) {

            HbaseUtil hbaseUtil = HbaseUtil.getInstance();

            Map<String, String> value = ObjectDynamicCreator.getFieldVlaue(log, null);
            if (AssertUtil.isNotEmpty(value)) {
                hbaseUtil.insert(tableName, rowKey, family, value);
            }
        }
    }


    public static  <T> T get(String tableName, String rowKey, final String family, final Class<T> clazz) throws Exception {

        T retT = null;
        final Map<String, String> retMap = new HashMap<>();

        //
        if (AssertUtil.isAnyEmpty(tableName, rowKey, family, clazz)) {
            return retT;
        }
        HbaseUtil hbaseUtil = HbaseUtil.getInstance();

        final Set<String> fieldSet = ObjectDynamicCreator.getFieldSet(clazz);
        // 数据读取
        hbaseUtil.getOneRow(tableName, rowKey, family, fieldSet, new HbaseUtil.QueryCallback() {

            @Override
            public void process(List<Result> retList) throws Exception {
                if (retList != null) {
                    Result ret = retList.get(0);
                    for (String field : fieldSet) {
                        byte[] fieldValueB = ret.getValue(
                                Bytes.toBytes(family),
                                Bytes.toBytes(field));
                        String fieldValue = Bytes.toString(fieldValueB);
                        if(fieldValue!=null){
                            retMap.put(field, fieldValue);
                        }

                    }

                }

            }
        });
        if(!retMap.isEmpty()){
            retT = JSONObject.parseObject(JSONObject.toJSONString(retMap), clazz);
        }
        return retT;
    }


}

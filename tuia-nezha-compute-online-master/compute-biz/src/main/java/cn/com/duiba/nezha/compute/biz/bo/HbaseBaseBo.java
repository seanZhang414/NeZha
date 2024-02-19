package cn.com.duiba.nezha.compute.biz.bo;

import cn.com.duiba.nezha.compute.biz.utils.hbase.HbaseUtil;

import cn.com.duiba.nezha.compute.core.CollectionUtil;
import cn.com.duiba.nezha.compute.core.util.AssertUtil;
import cn.com.duiba.nezha.compute.core.ObjectDynamicCreator;
import cn.com.duiba.nezha.compute.core.util.DataUtil;
import cn.com.duiba.nezha.compute.core.model.ops.VectorOps;
import cn.com.duiba.nezha.compute.core.util.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.mllib.linalg.SparseVector;

import java.util.*;

public class HbaseBaseBo {

    public static long MULTIPLES = 1000000L;
    public static int NEW_SCALA = 4;

    /**
     * 插入信息
     *
     * @param rowKey
     * @param family
     * @param object
     * @throws Exception
     */
    public static void insertSparseVector(String tableName, String rowKey, String family, SparseVector object) throws Exception {

        if (AssertUtil.isAllNotEmpty(tableName, rowKey, family, object)) {

            HbaseUtil hbaseUtil = HbaseUtil.getInstance();

            String[] indices = VectorOps.toStrIndex(object);
            long[] values = CollectionUtil.double2long(object.values(), MULTIPLES);

            if (AssertUtil.isNotEmpty(indices)) {
                hbaseUtil.insertColumeValues(tableName, rowKey, family, indices, values);
            }
        }
    }


    /**
     * 删除信息
     *
     * @param rowKey
     * @throws Exception
     */
    public static void deleteSparseVector(String tableName, String rowKey) throws Exception {

        if (AssertUtil.isAllNotEmpty(tableName, rowKey)) {

            HbaseUtil hbaseUtil = HbaseUtil.getInstance();
            hbaseUtil.deleteRow(tableName, rowKey);
        }
    }

    /**
     * 插入信息
     *
     * @param rowKey
     * @param family
     * @param object
     * @throws Exception
     */
    public static void incrementSparseVector(String tableName, String rowKey, String family, SparseVector object) throws Exception {

        if (AssertUtil.isAllNotEmpty(tableName, rowKey, family, object)) {

            HbaseUtil hbaseUtil = HbaseUtil.getInstance();

            String[] indices = VectorOps.toStrIndex(object);
            Long[] values = CollectionUtil.double2Long(object.values(), MULTIPLES);

            if (AssertUtil.isNotEmpty(indices)) {

                hbaseUtil.incrementColumeValues(tableName, rowKey, family, indices, values);
            }
        }
    }


    public static SparseVector searchSparseVector(String tableName, String rowKey, final String family, SparseVector object) throws Exception {

        if (AssertUtil.isAllNotEmpty(tableName, rowKey, family, object)) {

            HbaseUtil hbaseUtil = HbaseUtil.getInstance();

//            final int[] indices = object.indices();
            final String[] cols = VectorOps.toStrIndex(object);

            final Set<String> fieldSet = Sets.newHashSet(cols);

            final List<Long> idx = new ArrayList<>();
            final List<Long> val = new ArrayList<>();


            // 数据读取
            hbaseUtil.getOneRow(tableName, rowKey, family, fieldSet, new HbaseUtil.QueryCallback() {
                @Override
                public void process(List<Result> retList) throws Exception {
                    if (retList != null) {
                        Result ret = retList.get(0);
                        for (int i = 0; i < cols.length; i++) {
                            byte[] fieldValueB = ret.getValue(
                                    Bytes.toBytes(family),
                                    Bytes.toBytes(cols[i]));

                            if (fieldValueB != null) {
                                idx.add((long) object.indices()[i]);
                                val.add(Bytes.toLong(fieldValueB));
//                                System.out.println("rowk=" + rowKey + ",fam=" + family + ",col=" + cols[i] + ",ret=" + Bytes.toLong(fieldValueB));
                            }


                        }
                    }
                }

            });

            double[] valuesDouble = CollectionUtil.long22double(val, MULTIPLES, NEW_SCALA);
            int[] indices = CollectionUtil.toArray(idx);

            return VectorOps.toVector(object.size(), indices, valuesDouble);
        } else {
            return null;
        }
    }


    public static SparseVector searchSparseVector(String tableName, String rowKey, final String family, int vSize) throws Exception {

        if (AssertUtil.isAllNotEmpty(tableName, rowKey, family)) {

            HbaseUtil hbaseUtil = HbaseUtil.getInstance();

            final Map<Long, Double> mapV = new HashMap<>();
//            final Long[] values = new Long[cols.length];
//
//            final Set<String> fieldSet = Sets.newHashSet(cols);

            // 数据读取
            hbaseUtil.getOneRow(tableName, rowKey, family, new HbaseUtil.QueryCallback() {
                @Override
                public void process(List<Result> retList) throws Exception {
                    if (retList != null) {
                        Result ret = retList.get(0);

                        List<Cell> cs = ret.listCells();


                        String row = Bytes.toString(ret.getRow());


                        if (null != cs && cs.size() > 0) {

                            for (Cell cell : cs) {
//                                System.out.println("rowKeyC="+Bytes.toString(CellUtil.cloneRow(cell))+",fal="+Bytes.toString(CellUtil.cloneFamily(cell))+",qua="+Bytes.toString(CellUtil.cloneQualifier(cell)));

                                String rowKeyC = Bytes.toString(CellUtil.cloneRow(cell));// 取行健
                                Long timestampC = cell.getTimestamp();// 取到时间戳
                                String familyC = Bytes.toString(CellUtil.cloneFamily(cell));// 取到列族
                                String qualifierC = Bytes.toString(CellUtil.cloneQualifier(cell));// 取到列

                                Long valueC = Bytes.toLong(CellUtil.cloneValue(cell));// 取到值

                                if (rowKey.equals(rowKeyC) && family.equals(familyC) && qualifierC != null) {
                                    mapV.put(Long.valueOf(qualifierC), DataUtil.long2double(valueC, MULTIPLES, NEW_SCALA));
                                }

                            }
                        }
                    }
                }

            });

            List<Long> ids = new ArrayList<Long>(mapV.keySet());
            int[] indices = new int[ids.size()];
            double[] values = new double[ids.size()];
            if (ids.size() > 0) {
                Collections.sort(ids);
                for (int i = 0; i < ids.size(); i++) {
                    indices[i] = ids.get(i).intValue();
                    values[i] = DataUtil.todouble(mapV.get(ids.get(i)));
                }

            }
            return VectorOps.toVector(vSize, indices, values);
        } else {
            return null;
        }
    }


    /**
     * 插入信息
     *
     * @param rowKey
     * @param family
     * @throws Exception
     */
    public static void insertSingleValue(String tableName, String rowKey, String family, String[] indices, double[] values) throws Exception {

        if (AssertUtil.isAllNotEmpty(tableName, rowKey, family, indices, values) && indices.length == values.length) {

            HbaseUtil hbaseUtil = HbaseUtil.getInstance();

            long[] valueLong = CollectionUtil.double2long(values, MULTIPLES);

            if (AssertUtil.isNotEmpty(indices)) {
                hbaseUtil.insertColumeValues(tableName, rowKey, family, indices, valueLong);
            }
        }
    }


    /**
     * 删除信息
     *
     * @param rowKey
     * @throws Exception
     */
    public static void deleteSingleValue(String tableName, String rowKey) throws Exception {

        if (AssertUtil.isAllNotEmpty(tableName, rowKey)) {

            HbaseUtil hbaseUtil = HbaseUtil.getInstance();
            hbaseUtil.deleteRow(tableName, rowKey);
        }
    }


    /**
     * 插入信息
     *
     * @param rowKey
     * @param family
     * @throws Exception
     */
    public static void incrementSingleValue(String tableName, String rowKey, String family, String[] indices, double[] values) throws Exception {

        if (AssertUtil.isAllNotEmpty(tableName, rowKey, family, indices, values) && indices.length == values.length) {

            HbaseUtil hbaseUtil = HbaseUtil.getInstance();

            Long[] valueLong = CollectionUtil.double2Long(values, MULTIPLES);
            if (AssertUtil.isNotEmpty(indices)) {
                hbaseUtil.incrementColumeValues(tableName, rowKey, family, indices, valueLong);
            }
        }
    }


    public static Map<String, Double> searchSingleValue(String tableName, String rowKey, final String family, Set<String> keySet) throws Exception {
        final Map<String, Double> retMap = new HashMap<>();
        //
        if (AssertUtil.isAnyEmpty(tableName, rowKey, family, keySet)) {
            return new HashMap<>();
        }
        HbaseUtil hbaseUtil = HbaseUtil.getInstance();

        final Set<String> fieldSet = keySet;
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
                        if (fieldValueB != null) {
                            Long fieldValue = Bytes.toLong(fieldValueB);
                            if (fieldValue != null) {
                                retMap.put(field, DataUtil.long2double(fieldValue, MULTIPLES, NEW_SCALA));
                            }
                        }

                    }

                }

            }
        });

        return retMap;
    }


    /**
     * 插入信息
     *
     * @param rowKey
     * @param family
     * @param object
     * @throws Exception
     */
    public static <T> void insert(String tableName, String rowKey, String family, T object) throws Exception {

        if (AssertUtil.isAllNotEmpty(tableName, rowKey, family, object)) {

            HbaseUtil hbaseUtil = HbaseUtil.getInstance();

            Map<String, String> value = ObjectDynamicCreator.getFieldVlaue(object, null);

            if (AssertUtil.isNotEmpty(value)) {
                hbaseUtil.insert(tableName, rowKey, family, value);
            }
        }
    }

    /**
     * 插入信息
     *
     * @param rowKey
     * @param family
     * @param value
     * @throws Exception
     */
    public static <T> void insert(String tableName, String rowKey, String family, String col, String value) throws Exception {

        if (AssertUtil.isAllNotEmpty(tableName, rowKey, family, value)) {

            HbaseUtil hbaseUtil = HbaseUtil.getInstance();


            if (AssertUtil.isNotEmpty(value)) {
                hbaseUtil.insert(tableName, rowKey, family, col, value);
            }
        }
    }


    public static <T> List<T> mget(String tableName, List<String> rowKey, final String family, final Class<T> clazz) throws Exception {

        List<T> retT = new ArrayList<>();
        //
        if (AssertUtil.isAnyEmpty(tableName, rowKey, family, clazz)) {
            return retT;
        }
        Set<String> fieldSet = ObjectDynamicCreator.getFieldSet(clazz);
        Map<String, Map<String, String>> retMap = mget(tableName, rowKey, family, fieldSet);


        if (!retMap.isEmpty()) {
            for (String rk : retMap.keySet()) {
                Map<String, String> vMap = retMap.get(rk);
                if (vMap != null) {

                    T tmp = JSONObject.parseObject(JSONObject.toJSONString(vMap), clazz);
                    if (tmp != null) {
                        retT.add(tmp);


//            }
                    }

                }

            }

        }
        return retT;
    }


    public static Map<String, Map<String, String>> mget(String tableName, List<String> rowKeyList, final String family) throws Exception {
        final Map<String, Map<String, String>> retMap = new HashMap<>();
        //
        if (AssertUtil.isAnyEmpty(tableName, rowKeyList, family)) {
            return new HashMap<>();
        }
        HbaseUtil hbaseUtil = HbaseUtil.getInstance();
        // 数据读取
        hbaseUtil.getRows(tableName, rowKeyList, family, new HbaseUtil.QueryCallback() {

            @Override
            public void process(List<Result> retList) throws Exception {
                if (retList != null) {
                    for (Result ret : retList) {
                        Map<String, String> tmpMap = new HashMap<>();
                        String row = Bytes.toString(ret.getRow());
                        if (ret.listCells() != null) {
                            for (Cell kv : ret.listCells()) { // 遍历每一行的各列
                                tmpMap.put(Bytes.toString(kv.getQualifier()), Bytes.toString(kv.getValue()));
                            }
                        }


                        if (row != null) {
                            retMap.put(row, tmpMap);
//                            if(row.contains("t")){
//                                System.out.println("sample,key="+row+",map="+ JSONObject.toJSONString(tmpMap));
//                            }

                        }


                    }
                }

            }
        });

        return retMap;
    }


    public static Map<String, Map<String, String>> mget(String tableName, List<String> rowKeyList, final String family, Set<String> fSet) throws Exception {
        final Map<String, Map<String, String>> retMap = new HashMap<>();
        //
        if (AssertUtil.isAnyEmpty(tableName, rowKeyList, family, fSet)) {
            return new HashMap<>();
        }
        HbaseUtil hbaseUtil = HbaseUtil.getInstance();

        final Set<String> fieldSet = fSet;

        // 数据读取
        hbaseUtil.getRows(tableName, rowKeyList, family, fieldSet, new HbaseUtil.QueryCallback() {

            @Override
            public void process(List<Result> retList) throws Exception {
                if (retList != null) {
                    for (Result ret : retList) {
                        Map<String, String> tmpMap = new HashMap<>();
                        String row = Bytes.toString(ret.getRow());

                        for (String field : fieldSet) {
                            byte[] fieldValueB = ret.getValue(
                                    Bytes.toBytes(family),
                                    Bytes.toBytes(field));
                            String fieldValue = Bytes.toString(fieldValueB);
                            if (fieldValue != null) {
                                tmpMap.put(field, fieldValue);
                            }

                        }
                        if (row != null) {
                            retMap.put(row, tmpMap);
                        }


                    }
                }

            }
        });

        return retMap;
    }


    public static <T> T get(String tableName, String rowKey, final String family, final Class<T> clazz) throws Exception {

        T retT = null;
        //
        if (AssertUtil.isAnyEmpty(tableName, rowKey, family, clazz)) {
            return retT;
        }
        Set<String> fieldSet = ObjectDynamicCreator.getFieldSet(clazz);
        Map<String, String> retMap = get(tableName, rowKey, family, fieldSet);
        if (!retMap.isEmpty()) {

//            System.out.println("rowKey=" + rowKey + ",isClick="+retMap.get("isClick")+",retMap=" + retMap);

            retT = JSONObject.parseObject(JSONObject.toJSONString(retMap), clazz);
        }
        return retT;
    }

    public static <T> T get(String tableName, String rowKey, final String family, final String col, final Class<T> clazz) throws Exception {

        T retT = null;
        //
        if (AssertUtil.isAnyEmpty(tableName, rowKey, family, clazz)) {
            return retT;
        }
        Set<String> fieldSet = new HashSet<>();
        fieldSet.add(col);
        Map<String, String> retMap = get(tableName, rowKey, family, fieldSet);
        if (!retMap.isEmpty()) {

//            System.out.println("rowKey=" + rowKey + ",isClick="+retMap.get("isClick")+",retMap=" + retMap);
            String retS = retMap.getOrDefault(col, null);
            if(retS!=null){
                retT = JSONObject.parseObject(retS, clazz);
            }

        }
        return retT;
    }


    public static Map<String, String> get(String tableName, String rowKey, final String family, Set<String> keySet) throws Exception {
        final Map<String, String> retMap = new HashMap<>();
        //
        if (AssertUtil.isAnyEmpty(tableName, rowKey, family, keySet)) {
            return new HashMap<>();
        }
        HbaseUtil hbaseUtil = HbaseUtil.getInstance();

        final Set<String> fieldSet = keySet;
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
                        if (fieldValue != null) {
                            retMap.put(field, fieldValue);
                        }

                    }

                }

            }
        });

        return retMap;
    }

}


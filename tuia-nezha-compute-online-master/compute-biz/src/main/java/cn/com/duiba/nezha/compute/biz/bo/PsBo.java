package cn.com.duiba.nezha.compute.biz.bo;


import cn.com.duiba.nezha.compute.alg.ftrl.FMModel;
import cn.com.duiba.nezha.compute.biz.dto.PsModelBaseInfo;
import cn.com.duiba.nezha.compute.biz.constant.PsConstant;
import cn.com.duiba.nezha.compute.biz.dto.PsModelSample;
import cn.com.duiba.nezha.compute.biz.enums.HbaseOpsEnum;
import cn.com.duiba.nezha.compute.biz.utils.cachekey.PsKey;


import cn.com.duiba.nezha.compute.core.CollectionUtil;
import cn.com.duiba.nezha.compute.core.model.ps.PsMatrix;
import cn.com.duiba.nezha.compute.core.model.ps.PsModel;
import cn.com.duiba.nezha.compute.core.model.ps.PsVector;
import cn.com.duiba.nezha.compute.core.model.ops.CollectionOps;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.apache.spark.mllib.linalg.SparseVector;
import scala.collection.Iterator;
import scala.collection.immutable.Map;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PsBo extends HbaseBaseBo {


    public static void insertModel(FMModel fmModel, String modelId) throws Exception {

        if (fmModel != null) {


            String rowKey = PsKey.getRowKeyOfModel(modelId);
            insert(PsConstant.TABLE_NAME_MODEL, rowKey, PsConstant.FAMILY_MODEL, PsConstant.COL_MODEL, JSON.toJSONString(fmModel));
        }

    }


    public static FMModel getModel(String modelId) throws Exception {
        FMModel fmModel = null;
        if (modelId != null) {
            String rowKey = PsKey.getRowKeyOfModel(modelId);

            fmModel = get(PsConstant.TABLE_NAME_MODEL, rowKey, PsConstant.FAMILY_MODEL, PsConstant.COL_MODEL, FMModel.class);
        }

        return fmModel;
    }


    public static List<String> getOrderList(List<String> timeList) throws Exception {
        List<String> ret = new ArrayList<>();
        List<String> rowKeyList = PsKey.getRowKeyOfOrderIds(timeList);

        java.util.Map<String, java.util.Map<String, String>> retTmp = mget(PsConstant.TABLE_NAME_ORDER_LIST, rowKeyList, PsConstant.FAMILY_ORDER_LIST);

        if (retTmp != null) {
            for (java.util.Map<String, String> kv : retTmp.values()) {
                if (kv != null) {
                    ret.addAll(kv.keySet());
                }

            }
        }

        return ret;
    }

    public static List<PsModelSample> getPsSample(List<String> orderIdList) throws Exception {

        List<String> rowKeyList = PsKey.getRowKeyOfPsSamples(orderIdList);

        List<PsModelSample> ret = mget(PsConstant.TABLE_NAME_SAMPLE, rowKeyList, PsConstant.FAMILY_SAMPLE, PsModelSample.class);


        return ret;
    }

    public static List<PsModelSample> getPsSample(Iterator<String> partitionOfRecords) throws Exception {

        return getPsSample(CollectionUtil.toList(partitionOfRecords));
    }


    public static PsModelSample getPsSample(String orderId) throws Exception {

        String rowKey = PsKey.getRowKeyOfPsSample(orderId);
        PsModelSample ret = get(PsConstant.TABLE_NAME_SAMPLE, rowKey, PsConstant.FAMILY_SAMPLE, PsModelSample.class);

        return ret;
    }

    public static PsModelBaseInfo getPsBaseInfo(String modelId) throws Exception {
        String rowKey = PsKey.getRowKeyOfPsBaseInfo(modelId);
        PsModelBaseInfo ret = get(PsConstant.TABLE_NAME_BASE_INFO, rowKey, PsConstant.FAMILY_BASE_INFO, PsModelBaseInfo.class);
        return ret;
    }

    public static void deletePsBaseInfo(String modelId) throws Exception {
        String rowKey = PsKey.getRowKeyOfPsBaseInfo(modelId);
        deleteSingleValue(PsConstant.TABLE_NAME_BASE_INFO, rowKey);
        ;
    }

    public static void updatePsBaseInfo(String modelId, PsModelBaseInfo psModelBaseInfo) throws Exception {
        String rowKey = PsKey.getRowKeyOfPsBaseInfo(modelId);
        if (psModelBaseInfo != null) {
            insert(PsConstant.TABLE_NAME_BASE_INFO, rowKey, PsConstant.FAMILY_BASE_INFO, psModelBaseInfo);
        }
    }


    public static PsModel searchPsModel(PsModel psModel, PsModelBaseInfo psModelBaseInfo, boolean isAll) throws Exception {

        //判断是否为空
        if (psModel == null) {
            return null;
        }

        // value 数据处理

        Map<String, Object> valMap = psModel.getValueMap();


        String valRowKey = PsKey.getValKey(psModelBaseInfo.getModelId(), psModelBaseInfo.getVersion());

        Map<String, Object> valNewMap = searchSingleValue(CollectionOps.getStringKeys(valMap), valRowKey);

//        System.out.println("pull w0,key ="+valRowKey+", cols= " +valMap.keys()+",value="+valNewMap.values());


        // Vector 数据处理
        Map<String, PsVector> vMap = psModel.getVectorMap();

        String[] vMapKeys = CollectionOps.getPsVectorMapKeys(vMap);
        PsVector[] vMapValues = CollectionOps.getPsVectorMapValues(vMap, vMapKeys);

        PsVector[] vMapNewValues = new PsVector[vMapKeys.length];

        for (int i = 0; i < vMapKeys.length; i++) {

            String vRowKeyPrefix = PsKey.getVectorParRowKeyPrefix(psModelBaseInfo.getModelId(), psModelBaseInfo.getVersion(), vMapKeys[i]);
            vMapNewValues[i] = searchPsVector(vMapValues[i], vRowKeyPrefix, isAll);

//            System.out.println("pull w,key prefix ="+vRowKeyPrefix+", mKey= " +vMapKeys[i]+",value="+vMapNewValues[i].parVectorMap());
        }


        // Matrix 数据处理
        Map<String, PsMatrix> mMap = psModel.getMatrixMap();

        String[] mMapKeys = CollectionOps.getPsMatrixMapKeys(mMap);
        PsMatrix[] mMapValues = CollectionOps.getPsMatrixMapValues(mMap, mMapKeys);


        PsMatrix[] mMapNewValues = new PsMatrix[mMapKeys.length];
        for (int i = 0; i < mMapKeys.length; i++) {
            String mRowKeySubPrefix = PsKey.getMatrixParRowKeySubPrefix(psModelBaseInfo.getModelId(), psModelBaseInfo.getVersion(), mMapKeys[i]);
            mMapNewValues[i] = searchPsMatrix(mMapValues[i], mRowKeySubPrefix, isAll);
//            System.out.println("subPrefixKey"+mRowKeySubPrefix+","+mMapNewValues[i].toLocalMatrix().getMap());
        }

        PsModel ret = new PsModel(valNewMap,
                CollectionOps.toPsVectorMap(vMapKeys, vMapNewValues),
                CollectionOps.toPsMatrixMap(mMapKeys, mMapNewValues),
                psModelBaseInfo.getParSize());

        return ret;
    }

    public static void updatePsModel(PsModel psModel, PsModelBaseInfo psModelBaseInfo, HbaseOpsEnum hbaseOpsEnum) throws Exception {

        //判断是否为空
        if (psModel == null) {
            return;
        }

        // siglevalue数据处理

        Map<String, Object> valMap = psModel.getValueMap();

        String valRowKey = PsKey.getValKey(psModelBaseInfo.getModelId(), psModelBaseInfo.getVersion());
        updateSingleValue(valRowKey, valMap, hbaseOpsEnum);

//        System.out.println("push w0,key ="+valRowKey+", cols= " +valMap.keys()+",value="+valMap.values());


        // Vector 数据处理
        Map<String, PsVector> vMap = psModel.getVectorMap();

        String[] vMapKeys = CollectionOps.getPsVectorMapKeys(vMap);
        PsVector[] vMapValues = CollectionOps.getPsVectorMapValues(vMap, vMapKeys);


        for (int i = 0; i < vMapKeys.length; i++) {
            String vRowKeyPrefix = PsKey.getVectorParRowKeyPrefix(psModelBaseInfo.getModelId(), psModelBaseInfo.getVersion(), vMapKeys[i]);

            updatePsVector(vMapValues[i], vRowKeyPrefix, hbaseOpsEnum);
//            System.out.println("push w,key prefix ="+vRowKeyPrefix+", mKey= " +vMapKeys[i]+",value="+vMapValues[i].parVectorMap());
        }


        // Matrix 数据处理
        Map<String, PsMatrix> mMap = psModel.getMatrixMap();

        String[] mMapKeys = CollectionOps.getPsMatrixMapKeys(mMap);
        PsMatrix[] mMapValues = CollectionOps.getPsMatrixMapValues(mMap, mMapKeys);

//        System.out.println("v_n.1 = "+mMap.get("v_n").get().psVectorMap().get(1).get().parVectorMap());
//        System.out.println("v_z.1 = "+mMap.get("v_z").get().psVectorMap().get(1).get().parVectorMap());
        for (int i = 0; i < mMapKeys.length; i++) {
            String mRowKeySubPrefix = PsKey.getMatrixParRowKeySubPrefix(psModelBaseInfo.getModelId(), psModelBaseInfo.getVersion(), mMapKeys[i]);
            updatePsMatrix(mMapValues[i], mRowKeySubPrefix, hbaseOpsEnum);

        }


    }


    public static PsMatrix searchPsMatrix(PsMatrix psMatrix, String subPrefixKey, boolean isAll) throws Exception {

        //判断是否为空
        if (psMatrix == null) {
            return null;
        }
        Map<Object, PsVector> psVectorMap = psMatrix.psVectorMap();

        int[] mapKeys = CollectionOps.getPsVectorMapIntKeys(psVectorMap);
        PsVector[] mapValues = CollectionOps.getPsVectorMapValues(psVectorMap, mapKeys);
        PsVector[] mapNewValues = new PsVector[mapKeys.length];
        for (int i = 0; i < mapKeys.length; i++) {
            String rowKeyPrefix = PsKey.getMatrixParRowKeyPrefix(subPrefixKey, mapKeys[i]);
            mapNewValues[i] = searchPsVector(mapValues[i], rowKeyPrefix, isAll);
        }
        PsMatrix ret = new PsMatrix(CollectionOps.toPsVectorMap(mapKeys, mapNewValues), psMatrix.numRows(), psMatrix.numCols());

        return ret;
    }


    public static void updatePsMatrix(PsMatrix psMatrix, String subPrefixKey, HbaseOpsEnum hbaseOpsEnum) throws Exception {

        //判断是否为空
        if (psMatrix == null) {
            return;
        }
        Map<Object, PsVector> psVectorMap = psMatrix.psVectorMap();


        int[] mapKeys = CollectionOps.getPsVectorMapIntKeys(psVectorMap);

        PsVector[] mapValues = CollectionOps.getPsVectorMapValues(psVectorMap, mapKeys);


        for (int i = 0; i < mapKeys.length; i++) {

            String rowKeyPrefix = PsKey.getMatrixParRowKeyPrefix(subPrefixKey, mapKeys[i]);
            updatePsVector(mapValues[i], rowKeyPrefix, hbaseOpsEnum);

        }
    }


    public static PsVector searchPsVector(PsVector psVector, String rowKeyPrefix, boolean isAll) throws Exception {

        //判断是否为空
        if (psVector == null) {
            return null;
        }

        Map<Object, SparseVector> parVectorMap = psVector.parVectorMap();

        int[] mapKeys = CollectionOps.getSparseVectorMapKeys(parVectorMap);
        SparseVector[] mapValues = CollectionOps.getSparseVectorMapValues(parVectorMap, mapKeys);
        SparseVector[] mapNewValues = new SparseVector[mapKeys.length];
        for (int i = 0; i < mapKeys.length; i++) {

            String rowKey = PsKey.getParRowKey(rowKeyPrefix, mapKeys[i]);


            if (isAll) {
                mapNewValues[i] = searchSparseVector(PsConstant.TABLE_NAME_MODEL, rowKey, PsConstant.FAMILY_MODEL, psVector.vectorSize());
            } else {

                mapNewValues[i] = searchSparseVector(PsConstant.TABLE_NAME_MODEL, rowKey, PsConstant.FAMILY_MODEL, mapValues[i]);

            }
//            System.out.println("pull ,rowKey=" + rowKey + ",col="+mapValues[i]+",ret="+mapNewValues[i]);
        }
        PsVector ret = new PsVector(CollectionOps.toSparseVectorMap(mapKeys, mapNewValues), psVector.parSize(), psVector.vectorSize());
        return ret;
    }


    public static void updatePsVector(PsVector psVector, String rowKeyPrefix, HbaseOpsEnum hbaseOpsEnum) throws Exception {

        //判断是否为空
        if (psVector == null) {
            return;
        }

        Map<Object, SparseVector> parVectorMap = psVector.parVectorMap();
        int[] mapKeys = CollectionOps.getSparseVectorMapKeys(parVectorMap);

        SparseVector[] mapValues = CollectionOps.getSparseVectorMapValues(parVectorMap, mapKeys);
        for (int i = 0; i < mapKeys.length; i++) {
            String rowKey = PsKey.getParRowKey(rowKeyPrefix, mapKeys[i]);
//            System.out.println("rowKey   "+rowKey+","+mapValues[i]);
//            System.out.println("push v,key prefix =" + rowKey+ ",value="+mapValues[i] );
            // 自增
            if (HbaseOpsEnum.INCREMENT.equals(hbaseOpsEnum)) {
                incrementSparseVector(PsConstant.TABLE_NAME_MODEL, rowKey, PsConstant.FAMILY_MODEL, mapValues[i]);
            }
            // 插入&& 更新
            if (HbaseOpsEnum.INSERT_AND_UPDATE.equals(hbaseOpsEnum)) {
                insertSparseVector(PsConstant.TABLE_NAME_MODEL, rowKey, PsConstant.FAMILY_MODEL, mapValues[i]);
            }
            // 删除
            if (HbaseOpsEnum.INSERT_AND_UPDATE.equals(hbaseOpsEnum)) {
                deleteSparseVector(PsConstant.TABLE_NAME_MODEL, rowKey);
            }
        }
    }


    public static Map<String, Object> searchSingleValue(String[] colKeys, String rowKey) throws Exception {

        //判断是否为空
        if (colKeys == null) {
            return null;
        }

        Set set = new HashSet();
        CollectionUtils.addAll(set, colKeys);


        java.util.Map<String, Double> map = searchSingleValue(PsConstant.TABLE_NAME_MODEL, rowKey, PsConstant.FAMILY_MODEL, set);

        double[] values = new double[colKeys.length];
        for (int i = 0; i < colKeys.length; i++) {
            String key = colKeys[i];
            Double val = map.get(key);
            if (val != null) {
                values[i] = val;
            } else {
                values[i] = 0.0;
            }
        }

        return CollectionOps.toMap(colKeys, values);

    }


    public static void updateSingleValue(String rowKey, Map<String, Object> map, HbaseOpsEnum hbaseOpsEnum) throws Exception {

        //判断是否为空
        if (rowKey == null) {
            return;
        }

        String[] mapKeys = CollectionOps.getStringKeys(map);
        double[] mapValues = CollectionOps.getStringMapValues(map, mapKeys);

//        System.out.println("rowKey"+rowKey);

        // 自增
        if (HbaseOpsEnum.INCREMENT.equals(hbaseOpsEnum)) {
            incrementSingleValue(PsConstant.TABLE_NAME_MODEL, rowKey, PsConstant.FAMILY_MODEL, mapKeys, mapValues);
        }
        // 插入&& 更新
        if (HbaseOpsEnum.INSERT_AND_UPDATE.equals(hbaseOpsEnum)) {
            insertSingleValue(PsConstant.TABLE_NAME_MODEL, rowKey, PsConstant.FAMILY_MODEL, mapKeys, mapValues);
        }
        // 删除
        if (HbaseOpsEnum.INSERT_AND_UPDATE.equals(hbaseOpsEnum)) {
            deleteSingleValue(PsConstant.TABLE_NAME_MODEL, rowKey);
        }

    }

}

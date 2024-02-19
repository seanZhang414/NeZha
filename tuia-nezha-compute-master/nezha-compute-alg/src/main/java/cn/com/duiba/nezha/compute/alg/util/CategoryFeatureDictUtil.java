package cn.com.duiba.nezha.compute.alg.util;

import cn.com.duiba.nezha.compute.alg.vo.VectorResult;
import cn.com.duiba.nezha.compute.api.dict.CategoryFeatureDict;
import cn.com.duiba.nezha.compute.api.enums.SerializerEnum;
import cn.com.duiba.nezha.compute.common.util.AssertUtil;
import cn.com.duiba.nezha.compute.common.util.OneHotUtil;
import cn.com.duiba.nezha.compute.common.util.serialize.KryoUtil;
import cn.com.duiba.nezha.compute.common.util.serialize.SerializeTool;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.spark.mllib.linalg.SparseVector;
import org.apache.spark.mllib.linalg.Vectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * Created by pc on 2016/12/16.
 */
public class CategoryFeatureDictUtil implements Serializable {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private static final long serialVersionUID = -316102112618444930L;
    private CategoryFeatureDict dict = null;
    private Map<String, Map<String, Long>> cache = new HashMap<>();


    public void setFeatureDict(CategoryFeatureDict dict) {
        this.dict = dict;
    }

    public void setFeatureDict(String dictStr, SerializerEnum serializerEnum) {
        this.dict = getFeatureDict(dictStr, serializerEnum);
    }


    public CategoryFeatureDict getFeatureDict() {
        return this.dict;
    }

    public CategoryFeatureDict getFeatureDict(String dictStr, SerializerEnum serializerEnum) {
        CategoryFeatureDict dict = null;
        if (serializerEnum == SerializerEnum.JAVA_ORIGINAL) {
            dict = SerializeTool.getObjectFromString(dictStr);
        }
        if (serializerEnum == SerializerEnum.KRYO) {
            dict = KryoUtil.deserializationObject(dictStr, CategoryFeatureDict.class);
        }


        return dict;
    }

    public String featureIdxList2Str(List<String> idxStr, SerializerEnum serializerEnum) {
        String ret = null;
        if (serializerEnum == SerializerEnum.JAVA_ORIGINAL) {
            ret = SerializeTool.object2String(idxStr);
        }
        if (serializerEnum == SerializerEnum.KRYO) {
            ret = KryoUtil.serializationListObject(idxStr);
        }


        return ret;
    }

    public List<String> getFeatureIdxList(String featureIdxStr, SerializerEnum serializerEnum) {
        List<String> ret = null;

        if (serializerEnum == SerializerEnum.JAVA_ORIGINAL) {
            ret = (List<String>) SerializeTool.getObjectFromStr(featureIdxStr);
        }
        if (serializerEnum == SerializerEnum.KRYO) {
            ret = KryoUtil.deserializationListObject(featureIdxStr);
        }

        return ret;
    }


    public List<String> getFeatureCollectionList(String featureCollectionStr, SerializerEnum serializerEnum) {
        List<String> ret = null;

        if (serializerEnum == SerializerEnum.JAVA_ORIGINAL) {
            ret = (List<String>) SerializeTool.getObjectFromStr(featureCollectionStr);
        }
        if (serializerEnum == SerializerEnum.KRYO) {
            ret = KryoUtil.deserializationListObject(featureCollectionStr);
        }

        return ret;
    }


    public String featureCollectionList2Str(List<String> idxCollectionStr, SerializerEnum serializerEnum) {
        String ret = null;
        if (serializerEnum == SerializerEnum.JAVA_ORIGINAL) {
            ret = SerializeTool.object2String(idxCollectionStr);
        }
        if (serializerEnum == SerializerEnum.KRYO) {
            ret = KryoUtil.serializationListObject(idxCollectionStr);
        }


        return ret;
    }


    public String getFeatureDictStr(CategoryFeatureDict dict, SerializerEnum serializerEnum) {
        String ret = null;

        if (serializerEnum == SerializerEnum.JAVA_ORIGINAL) {
            ret = SerializeTool.object2String(dict);
        }
        if (serializerEnum == SerializerEnum.KRYO) {
            ret = KryoUtil.serializationObject(dict);
        }

        return ret;
    }

    public String getFeatureDictStr(SerializerEnum serializerEnum) {
        return getFeatureDictStr(dict, serializerEnum);
    }


    //
    public List<String> getFeature(String featureIdx) {
        if (this.dict == null) {
            logger.warn("dict is null");
            return null;
        }

        if (AssertUtil.isEmpty(featureIdx)) {
            logger.warn("getFeature cn.com.duiba.nezha.compute.biz.param invalid");
            return null;
        }
        return this.dict.getFeatureDict().get(featureIdx);
    }


    //
    public int getFeatureSize(String featureIdx) {
        int ret = 0;
        List<String> featureMap = getFeature(featureIdx);
        if (AssertUtil.isNotEmpty(featureMap)) {
            ret = featureMap.size();
        }
        return ret;
    }


//    public int getFeatureCategoryIndex(String featureIdx, String category) {
//        int ret = -1;
//        List<String> featureMap = getFeature(featureIdx);
//        if (AssertUtil.isNotEmpty(featureMap)) {
//            ret = featureMap.indexOf(category);
//        }
//        return ret;
//    }


    public int getFeatureCategoryIndex(String featureIdx, String category) {
        int ret = -1;
        if (AssertUtil.isNotEmpty(featureIdx)) {
            Map<String, Long> featureMap = cache.get(featureIdx);
            if (featureMap == null) {
                List<String> featureList = getFeature(featureIdx);
                featureMap = categoryList2Map(featureList);
                cache.put(featureIdx, featureMap);
            }
            Long ret2 = featureMap.get(category);
            if (ret2 != null) {
                ret = ret2.intValue();
            }

        }
        return ret;
    }

    public List<Long> getFeatureCategoryIndex(String featureIdx, String category, boolean isCollection) {
        List<Long> ret = new ArrayList<>();
        ret.add(-1L);
        if (AssertUtil.isNotEmpty(featureIdx)) {
            Map<String, Long> featureMap = cache.get(featureIdx);
            if (featureMap == null) {
                List<String> featureList = getFeature(featureIdx);
                featureMap = categoryList2Map(featureList);
                cache.put(featureIdx, featureMap);
            }

            if (isCollection && AssertUtil.isNotEmpty(category)) {
//                System.out.println("isCollection,"+category);
                String[] categoryArray = category.split(",", 0);
                if (categoryArray != null) {

                    Set<Long> retSet = new HashSet<>();
                    for (String c : categoryArray) {
                        Long r = featureMap.get(c);
                        if (r != null) {
                            retSet.add(r);
                        } else {
                            retSet.add(-1L);
                        }
                    }
                    List<Long> ret2 = new ArrayList<>(retSet);
                    Collections.sort(ret2);
                    ret = ret2;
                }

            }
            if (!isCollection && AssertUtil.isNotEmpty(category)) {
                Long ret3 = featureMap.get(category);
                if (ret3 != null) {
                    ret.clear();
                    ret.add(ret3);
                }
            }


        }
        return ret;
    }


    public Map<String, Long> categoryList2Map(List<String> featureCateList) {
        Map<String, Long> ret = new HashMap<>();
        if (AssertUtil.isNotEmpty(featureCateList)) {
            int size = featureCateList.size();
            for (int i = 0; i < size; i++) {
                ret.put(featureCateList.get(i), (long) i);
            }
        }
        return ret;
    }


    public double[] featureOneHotDoubleEncode(String featureIdx, String category) {
        double[] ret = null;
        int idx = getFeatureCategoryIndex(featureIdx, category);
        int size = getFeatureSize(featureIdx);
        if (AssertUtil.isNotEmpty(idx)) {
            ret = OneHotUtil.getOneHotDouble(idx, size);
        }
        return ret;
    }

    /**
     * @param featureIdxList
     * @param categoryList
     * @return
     */
    public double[] oneHotDoubleEncode(List<String> featureIdxList, List<String> categoryList) {
        double[] ret = null;
        if (AssertUtil.isAnyEmpty(featureIdxList, categoryList) ||
                categoryList.size() != featureIdxList.size()) {
            return null;
        }
        try {
            for (int i = 0; i < featureIdxList.size(); i++) {
                String featureIdx = featureIdxList.get(i);
                String category = categoryList.get(i);
                double[] partRet = featureOneHotDoubleEncode(featureIdx, category);
                if (partRet == null) {
                    return null;
                } else {
                    ret = ArrayUtils.addAll(ret, partRet);
                }
            }
        } catch (Exception e) {
            logger.error("oneHotDoubleEncode happend error", e);
        }

        return ret;
    }


    /**
     * @param featureIdxList
     * @param categoryList
     * @return
     */
    public SparseVector oneHotSparseVectorEncode(List<String> featureIdxList, List<String> categoryList) {
        SparseVector ret = null;
        if (AssertUtil.isAnyEmpty(featureIdxList, categoryList) ||
                categoryList.size() != featureIdxList.size()) {
            return null;
        }
        try {
            int[] sizeArray = new int[featureIdxList.size()];
            int[] indexArray = new int[featureIdxList.size()];

            int[] r_indices = new int[featureIdxList.size()];
            double[] r_values = new double[featureIdxList.size()];
            int sizeSum = 0;
            for (int i = 0; i < featureIdxList.size(); i++) {
                String featureIdx = featureIdxList.get(i);
                String category = categoryList.get(i);

                sizeArray[i] = getFeatureSize(featureIdx);
                indexArray[i] = getFeatureCategoryIndex(featureIdx, category);

                r_indices[i] = sizeSum + getFeatureCategoryIndex(featureIdx, category) + 1;
                sizeSum += getFeatureSize(featureIdx) + 1;
                r_values[i] = 1.0;
            }


            ret = (SparseVector) Vectors.sparse(sizeSum, r_indices, r_values);

        } catch (Exception e) {
            logger.error("oneHotDoubleEncode happend error", e);
        }

        return ret;
    }

    public boolean isCollection(String featureIdx, Set<String> featureCollectionSet) {
        boolean ret = false;
        if (featureIdx != null && featureCollectionSet != null && featureCollectionSet.contains(featureIdx)) {
            ret = true;
        }
        return ret;
    }

    public int[] toIntArray(List<Long> oList) {
        int[] ret = null;
        if (AssertUtil.isNotEmpty(oList)) {
            ret = new int[oList.size()];

            for (int i = 0; i < oList.size(); i++) {
                Long v = oList.get(i);
                if (v != null) {
                    ret[i] = v.intValue();
                } else {
                    ret[i] = 0;
                }

            }
        }
        return ret;
    }

    public double[] toDoubleArray(List<Double> oList) {
        double[] ret = null;
        if (AssertUtil.isNotEmpty(oList)) {
            ret = new double[oList.size()];

            for (int i = 0; i < oList.size(); i++) {
                Double v = oList.get(i);
                if (v != null) {
                    ret[i] = v.doubleValue();
                } else {
                    ret[i] = 0;
                }

            }
        }
        return ret;
    }


    /**
     * @param featureIdxList
     * @param categoryList
     * @return
     */
    public VectorResult oneHotSparseVectorEncode(List<String> featureIdxList, List<String> categoryList, List<String> featureCollectionList) {
        VectorResult ret = new VectorResult();

        if (AssertUtil.isAnyEmpty(featureIdxList, categoryList) ||
                categoryList.size() != featureIdxList.size()) {
            return null;
        }
        int newFeatureNums = 0;
        int totalFeatureNums = featureIdxList.size();


        try {

            List<Long> r_indices = new ArrayList<>();
            List<Double> r_values = new ArrayList<>();
            int sizeSum = 0;
            Set<String> featureCollectionSet = new HashSet<>();
            if(featureCollectionList!=null){
                featureCollectionSet.addAll(featureCollectionList);
            }
            for (int i = 0; i < totalFeatureNums; i++) {
                String featureIdx = featureIdxList.get(i);
                String category = categoryList.get(i);
                boolean isc = isCollection(featureIdx, featureCollectionSet);


                List<Long> indexList = getFeatureCategoryIndex(featureIdx, category, isc);
                int idxSize = indexList.size();
                for (Long idx : indexList) {
                    if (idx == -1) {
                        newFeatureNums++;
                    }
                    r_indices.add(idx + sizeSum + 1);
                    r_values.add(1.0 / idxSize);
                }

                sizeSum += getFeatureSize(featureIdx) + 1;


            }


            SparseVector vector = (SparseVector) Vectors.sparse(sizeSum, toIntArray(r_indices), toDoubleArray(r_values));

            ret.setVector(vector);
            ret.setNewFeatureNums(newFeatureNums);
            ret.setTotalFeatureNums(totalFeatureNums);
//            System.out.println("newFeatureNums=" + newFeatureNums);

        } catch (Exception e) {
            logger.error("oneHotDoubleEncode happend error", e);
        }

        return ret;
    }


    /**
     * @param featureIdxList
     * @param categoryMap    <featureIdx,category>
     * @return
     */

    public double[] oneHotDoubleEncodeWithMap(List<String> featureIdxList, Map<String, String> categoryMap) {
        double[] ret = null;

        if (AssertUtil.isAnyEmpty(featureIdxList, categoryMap)) {
            return null;
        }
        try {
            ret = oneHotDoubleEncodeWithMap(featureIdxList, categoryMap, true);
        } catch (Exception e) {
            logger.error("oneHotIntEncode happend error", e);

        }

        return ret;
    }

    /**
     * @param featureIdxList
     * @param categoryMap    <featureIdx,category>
     * @return
     */

    public double[] oneHotDoubleEncodeWithMap(List<String> featureIdxList, Map<String, String> categoryMap, boolean ignoreNull) {
        double[] ret = null;

        if (AssertUtil.isAnyEmpty(featureIdxList, categoryMap)) {
            return null;
        }
        try {
            int status = 1;
            List<String> categoryList = new ArrayList<>();
            for (int i = 0; i < featureIdxList.size(); i++) {

                String featureIdx = featureIdxList.get(i);

                if (!categoryMap.containsKey(featureIdx) && !ignoreNull) {
                    status = -1;
                    break;
                }

                String category = categoryMap.get(featureIdx);


                if (category != null) {
                    category = category.toLowerCase();
                }
                categoryList.add(category);
            }
            if (status == 1) {
                ret = oneHotDoubleEncode(featureIdxList, categoryList);
            }
        } catch (Exception e) {
            logger.error("oneHotIntEncode happend error", e);

        }

        return ret;
    }


    /**
     * @param featureIdxList
     * @param categoryMap    <featureIdx,category>
     * @return
     */

    public SparseVector oneHotSparseVectorEncodeWithMap(List<String> featureIdxList, Map<String, String> categoryMap) {
        SparseVector ret = null;

        if (AssertUtil.isAnyEmpty(featureIdxList, categoryMap)) {
            return null;
        }
        try {
            ret = oneHotSparseVectorEncodeWithMap(featureIdxList, categoryMap, true);
        } catch (Exception e) {
            logger.error("oneHotIntEncode happend error", e);

        }

        return ret;
    }

    /**
     * @param featureIdxList
     * @param categoryMap    <featureIdx,category>
     * @return
     */

    public VectorResult oneHotSparseVectorEncodeWithMap(List<String> featureIdxList, Map<String, String> categoryMap, List<String> featureCollectionList) {
        VectorResult ret = new VectorResult();

        if (AssertUtil.isAnyEmpty(featureIdxList, categoryMap)) {
            System.out.println("isAnyEmpty(featureIdxList, categoryMap)");
            return null;
        }
        try {
            ret = oneHotSparseVectorEncodeWithMap(featureIdxList, categoryMap, true, featureCollectionList);
        } catch (Exception e) {
            logger.error("oneHotIntEncode happend error", e);

        }

        return ret;
    }

    /**
     * @param featureIdxList
     * @param categoryMap    <featureIdx,category>
     * @return
     */

    public VectorResult oneHotSparseVectorEncodeWithMap(List<String> featureIdxList, Map<String, String> categoryMap, boolean ignoreNull, List<String> featureCollectionList) {
        VectorResult ret = new VectorResult();

        if (AssertUtil.isAnyEmpty(featureIdxList, categoryMap)) {
            return null;
        }
        try {
            int status = 1;
            List<String> categoryList = new ArrayList<>();
            for (int i = 0; i < featureIdxList.size(); i++) {

                String featureIdx = featureIdxList.get(i);

                if (!categoryMap.containsKey(featureIdx) && !ignoreNull) {
                    status = -1;
                    break;
                }

                String category = categoryMap.get(featureIdx);

                if (category != null) {
                    category = category.toLowerCase();
                }
                categoryList.add(category);

            }
            if (status == 1) {
                ret = oneHotSparseVectorEncode(featureIdxList, categoryList, featureCollectionList);
            }
        } catch (Exception e) {
            logger.error("oneHotIntEncode happend error", e);

        }

        return ret;
    }


    /**
     * @param featureIdxList
     * @param categoryMap    <featureIdx,category>
     * @return
     */

    public SparseVector oneHotSparseVectorEncodeWithMap(List<String> featureIdxList, Map<String, String> categoryMap, boolean ignoreNull) {
        SparseVector ret = null;

        if (AssertUtil.isAnyEmpty(featureIdxList, categoryMap)) {
            return null;
        }
        try {
            int status = 1;
            List<String> categoryList = new ArrayList<>();
            for (int i = 0; i < featureIdxList.size(); i++) {

                String featureIdx = featureIdxList.get(i);

                if (!categoryMap.containsKey(featureIdx) && !ignoreNull) {
                    status = -1;
                    break;
                }

                String category = categoryMap.get(featureIdx);

                if (category != null) {
                    category = category.toLowerCase();
                }
                categoryList.add(category);
            }
            if (status == 1) {
                ret = oneHotSparseVectorEncode(featureIdxList, categoryList);
            }
        } catch (Exception e) {
            logger.error("oneHotIntEncode happend error", e);

        }

        return ret;
    }


}

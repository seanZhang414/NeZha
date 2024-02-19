package cn.com.duiba.nezha.compute.common.util;

import cn.com.duiba.nezha.compute.alg.util.CategoryFeatureDictUtil;
import cn.com.duiba.nezha.compute.alg.vo.VectorResult;
import cn.com.duiba.nezha.compute.api.dict.CategoryFeatureDict;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;

import java.util.*;

/**
 * Created by pc on 2017/6/21.
 */
public class CategoryFeatureDictUtilTest extends TestCase {

    public void testSetFeatureDict() throws Exception {

    }

    public void testSetFeatureDict1() throws Exception {

    }

    public void testGetFeatureDict() throws Exception {

    }

    public void testGetFeatureDict1() throws Exception {

    }

    public void testFeatureIdxList2Str() throws Exception {

    }

    public void testGetFeatureIdxList() throws Exception {

    }

    public void testGetFeatureDictStr() throws Exception {

    }

    public void testGetFeatureDictStr1() throws Exception {

    }

    public void testGetFeature() throws Exception {

    }

    public void testGetFeatureSize() throws Exception {

    }

    public void testGetFeatureCategoryIndex() throws Exception {

    }

    public void testFeatureOneHotDoubleEncode() throws Exception {

    }

    public void testOneHotDoubleEncode() throws Exception {

    }

    public void testOneHotSparseVectorEncode() throws Exception {

    }

    public void testOneHotDoubleEncodeWithMap() throws Exception {

    }

    public void testOneHotDoubleEncodeWithMap1() throws Exception {

    }

    public void testOneHotSparseVectorEncodeWithMap() throws Exception {
        CategoryFeatureDictUtil util = new CategoryFeatureDictUtil();
        CategoryFeatureDict dict = new CategoryFeatureDict();
        dict.setFeature("f001", Arrays.asList("f1c1", "f1c2", "f1c3", "f1c4"));
        dict.setFeature("f002", Arrays.asList("f2c1", "f2c2"));
        dict.setFeature("f003",Arrays.asList("f3c1", "f3c2"));
        util.setFeatureDict(dict);


        Map<String, String> map = new HashMap<>();
        map.put("f001", "f1c9,null");
        map.put("f002", "f2c2,null");
        map.put("f003", "f3c1");

        List<String> cList = new ArrayList<>();
        cList.add("f002");
        cList.add("f001");
        VectorResult ret = util.oneHotSparseVectorEncodeWithMap(Arrays.asList("f001", "f002", "f003"), map, cList);
        System.out.println("vector "+ret.getVector());
        System.out.println("ret = " + JSON.toJSONString(ret));


    }

    public void testOneHotSparseVectorEncodeWithMap1() throws Exception {

    }
}
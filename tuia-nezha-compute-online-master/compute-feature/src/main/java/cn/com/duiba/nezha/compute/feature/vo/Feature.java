package cn.com.duiba.nezha.compute.feature.vo;

import com.alibaba.fastjson.JSON;

import java.util.List;

public class Feature {
    public int size;
    public int[] indices;
    public double[] values;

    public Feature(int size, int[] indices, double[] values) throws Exception {

//        System.out.println("size="+size+"indices="+ JSON.toJSONString(indices)+"value="+ JSON.toJSONString(values));
        if (size <= 0) {
            throw new Exception("size<=0,input invalid");
        }
        if (indices == null || values == null) {
            throw new Exception("indices or values is null,input invalid");
        }

        if (indices.length != values.length) {
            throw new Exception("indices.length != values.length,input invalid");
        }
        if (indices.length>0 && size < indices[indices.length - 1]) {
            throw new Exception("indices beyond the boundary,input invalid");
        }
        this.size = size;
        this.indices = indices;
        this.values = values;
    }


}

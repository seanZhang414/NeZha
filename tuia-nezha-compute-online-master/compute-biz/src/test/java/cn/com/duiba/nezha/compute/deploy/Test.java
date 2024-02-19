package cn.com.duiba.nezha.compute.deploy;

import cn.com.duiba.nezha.compute.core.CollectionUtil;

import java.util.Arrays;

public class Test {
    public static void main(String[] args) throws Exception{

        double[] data = new double[10];

        data[1] = 2.0;
        data[2] = 1.0;
        String str = CollectionUtil.toString(data);
        System.out.println(str);

    }
}

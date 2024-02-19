package cn.com.duiba.nezha.compute.core.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashUtilTest {

    @Test
    public void hash() throws Exception {
        HashUtil util1 = new HashUtil();
        HashUtil util2 = new HashUtil();
        Map<Long, Long> map = new HashMap<>();
        List<Long> a = new ArrayList<>();
        for (int i = -1; i < 8; i++) {
            String val = i+"";
//            int ret = FeatureUtil.getSubFId(val, 100);
//            int ret2 = FeatureUtil.getSubFId(val, 5);
//            System.out.println("ori=" + val + ",out=" + ret[0]+","+ret[1]+","+ret[2]+",ret2="+ret2);
//
//            System.out.println("i="+i+"，"+ret);
//            System.out.println("a="+a.get(a.size()-1));
//            setValue(a,i);
        }

//        System.out.println("map=" + map);
//        System.out.println("i600-1099=" + FeatureUtil.getHashSubFId("600-1099", 10, 1)[0]);
//        System.out.println("4500+=" + FeatureUtil.getHashSubFId("4500+", 10, 1)[0]);
//        System.out.println("2700-4499=" + FeatureUtil.getHashSubFId("unknow", 10, 1)[0]);
//        System.out.println("null=" + FeatureUtil.getHashSubFId(null, 10, 1)[0]);

    }

    @Test
    public void additiveHash() {
    }


    public static void setValue(List<Long> a, int b) {
        a.add((long) b);
    }
}
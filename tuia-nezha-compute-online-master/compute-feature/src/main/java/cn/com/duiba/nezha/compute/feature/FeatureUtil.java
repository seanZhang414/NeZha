package cn.com.duiba.nezha.compute.feature;

import cn.com.duiba.nezha.compute.core.util.BloomFilter;
import cn.com.duiba.nezha.compute.core.util.HashUtil;
import com.alibaba.fastjson.JSON;

import java.lang.reflect.Array;
import java.util.*;

public class FeatureUtil {

    public static Map<String, Map<Long, Set<String>>> hash = new HashMap<>();


    public static int[] seed = {3, 7, 11, 17, 19, 31, 41};

    public static void printConflictTest() {
        for (String feature : hash.keySet()) {

            int[] test = new int[100];
            Map<Long, Set<String>> valMap = hash.get(feature);

            boolean status = false;
            for (Long fId : valMap.keySet()) {
                int setSize = Math.min(valMap.get(fId).size(), 99);
                test[setSize] = test[setSize] + 1;
                if (setSize > 1) {
                    status = true;
                    System.out.println("feature=" + feature + ",fId=" + fId + ",set=" + JSON.toJSONString(valMap.get(fId)));
                }

            }


            System.out.println("feature=" + feature + ",conflict status=" + JSON.toJSONString(test));


        }
    }

    public static void addHashConflictTest(String feature, String fStr, Long fId) {

//        if (hash.get(feature) == null) {
//            hash.put(feature, new HashMap<>());
//        }
//
//        if (hash.get(feature).get(fId) == null) {
//            hash.get(feature).put(fId, new HashSet<>());
//        }
//        hash.get(feature).get(fId).add(fStr);

    }


    public static int[] getHashSubFId(String feature, String str, int size, int nums) throws Exception {

        if (nums > seed.length) {
            System.out.println("nums is larger than seed size,invalid");
            return null;
        }
        str = std(str);
        int[] ret = new int[nums];
        for (int i = 0; i < nums; i++) {
            ret[i] = i * size + HashUtil.hash(str, size, seed[i]);

//            System.out.println("featureId="+feature+",str="+str+",ret="+ret[i]);

            addHashConflictTest(feature, str, ret[i] + 0L);
        }
        Arrays.sort(ret);


        return ret;
    }

    public static int[] getHashSubFIds(String feature, String[] str, int size, int nums) throws Exception {

        if (nums > seed.length) {
            System.out.println("hash nums is larger than seed size,invalid");
            return null;
        }

        if (str == null || str.length == 0) {
            str = new String[]{null};
        }


        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < str.length; i++) {
            int[] tmp = getHashSubFId(feature, str[i], size, nums);
            for (int j = 0; j < tmp.length; j++) {
                set.add(tmp[j]);
            }
        }
        Integer[] retI = set.toArray(new Integer[set.size()]);

        int[] ret = new int[retI.length];
        for (int i = 0; i < retI.length; i++) {
            ret[i] = retI[i].intValue();
        }

        Arrays.sort(ret);
        return ret;
    }


    public static int getDictSubFId(String feature, String str, int size, Map<String, Long> dict) throws Exception {
        int ret = 0;
        str = std(str);
        if (dict != null && str != null) {
            Long tmp = dict.getOrDefault(str, 0L);
            ret = tmp.intValue();
        }
        addHashConflictTest(feature, str, ret + 0L);
        return ret;
    }

    public static int[] getDictSubFIds(String feature, String[] str, int size, Map<String, Long> dict) throws Exception {

        if (str == null) {
            str = new String[]{null};
        }

        Set<Integer> set = new HashSet<>();

        for (int i = 0; i < str.length; i++) {
            int tmp = getDictSubFId(feature, str[i], size, dict);
            set.add(tmp);
        }
        Integer[] retI = set.toArray(new Integer[set.size()]);

        int[] ret = new int[retI.length];
        for (int i = 0; i < retI.length; i++) {
            ret[i] = retI[i].intValue();
        }

        Arrays.sort(ret);
        return ret;

    }


    public static int[] getSubFIds(String feature, String[] str, int size) throws Exception {

        if (str == null) {
            str = new String[]{null};
        }
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < str.length; i++) {
            int tmp = getSubFId(feature, str[i], size);
            set.add(tmp);
        }
        Integer[] retI = set.toArray(new Integer[set.size()]);

        int[] ret = new int[retI.length];
        for (int i = 0; i < retI.length; i++) {
            ret[i] = retI[i].intValue();
        }

        Arrays.sort(ret);
        return ret;
    }


    public static int getSubFId(String feature, String str, int size) throws Exception {
        int ret = 0;
        str = std(str);

        if (str != null) {
            Long tmp = Long.valueOf(str);

            if (tmp != null && tmp >= 0) {
//                System.out.println(((size - 1) + tmp.intValue()) & (size - 1));
                ret = ((size - 1) + tmp.intValue()) % (size - 1);
                ret += 1;

            }

        }
        addHashConflictTest(feature, str, ret + 0L);
        return ret;
    }


    public static String std(String oStr) {
        if (oStr == null) {
            return null;
        }
        String lowStr = oStr.toLowerCase();
        if (lowStr.length() == 0 ||
                lowStr.equals("\\n") ||
                lowStr.equals("\n") ||
                lowStr.equals("null") ||
                lowStr.equals("none")
                ) {
            return null;
        }
        return lowStr;
    }

    public static String[] toFeatures(String str, String seq) throws Exception {
        String[] ret = null;
        if (str != null) {
            ret = str.split(seq, 0);
        }
        return ret;
    }


}

package cn.com.duiba.nezha.compute.ps.ops;

import org.junit.Test;
import scala.collection.Map;

public class ArrayOpsTest {

    @Test
    public void binarySearch() {
        int[] z2 = {0,1,3,5,7,9,16,17};
//        int[] z2={};

        int[] index = {0,5,10,15,20};
        for(int i=0;i<index.length;i++){
            BS ret = ArrayOps.binarySearch(z2,index[i]);
            System.out.println(index[i]+":"+ArrayOps.binarySearch(z2,index[i]));
        }

    }

    @Test
    public void getParArrays() {
//        int[] z2 = {0,1,3,5,7,9,16,17};
        int[] z2 = {0,5,9};

       Map<Object,ParBoundInfo> ret= ArrayOps.getParArrays(z2,5,20);
        System.out.println(":"+ret);

//        for(Object s:ret.keys()){
////            System.out.println(s+":"+ret);
////        }

    }

    @Test
    public void getSubArray() {
                int[] z2 = {0,1,3,5,7,9,16,17};
//
        print(ArrayOps.getSubArray(z2,2,6));

    }

    public static void print(int[] scr){
        System.out.printf("\n sub[");
        for(int i :scr){
            System.out.printf(i+",");
        }
        System.out.printf("]\n");

    }
}
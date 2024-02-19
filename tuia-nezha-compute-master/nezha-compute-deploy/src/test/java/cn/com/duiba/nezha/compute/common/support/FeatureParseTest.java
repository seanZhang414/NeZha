package cn.com.duiba.nezha.compute.common.support;

import junit.framework.TestCase;

/**
 * Created by pc on 2017/12/14.
 */
public class FeatureParseTest extends TestCase {

//    public void testGetCtrIntervelLevel() throws Exception {
//        for(int i=0;i<100;i++){
//            System.out.println("i="+(i*0.01)+",level="+FeatureParse.getCtrIntervelLevel(i*0.01));
//        }
//    }
//
//    public void testGetCvrIntervelLevel() throws Exception {
//        for(int i=0;i<1000;i++){
//            System.out.println("i="+(i*0.001)+",level="+FeatureParse.getCvrIntervelLevel(i*0.001));
//        }
//    }

    public void testGetLevel() throws Exception {
        String key ="launch_pv";
        String idlist="a12";
        String valueList="44,";
        String ret = FeatureParse.getLevel(key,idlist,valueList);
        System.out.println("ret = "+ret);

    }


}
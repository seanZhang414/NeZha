package cn.com.duiba.nezha.compute.common.model;

import junit.framework.TestCase;
import java.util.List;
import java.util.ArrayList;

public class BayesianBanditTest extends TestCase {
    protected void setUp() throws Exception {
        super.setUp();
        System.out.println();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        System.out.println();
    }

    public void testBayesianBandit() throws Exception { //几个候选集

        BayesianBandit bandit = new BayesianBandit();


        //初始化两个素材
        MaterialInfo m1 = new MaterialInfo();
        ArrayList<Long> clkList = new ArrayList<>();
        clkList.add(0,10l);
        clkList.add(1,0l);
        ArrayList<Long> expList = new ArrayList<>();
        expList.add(0,20l);
        expList.add(1,0l);
        ArrayList<Long> clkList3 = new ArrayList<>();
        clkList3.add(0,20l);
        clkList3.add(1,0l);
        ArrayList<Long> expList3 = new ArrayList<>();
        expList3.add(0,100l);
        expList3.add(1,0l);
        m1.appId = 1;
        m1.setClick(clkList);
        m1.setExposure(expList);
        m1.setMaterialId(1);
        m1.setCount(0);
        m1.setLastClick(0);
        m1.setLastExposure(0);
        m1.setReward(0);
        m1.globalClick = clkList3;
        m1.globalExposure = expList3;
        m1.globalLastClick = 0;
        m1.globalLastExposure = 0;


        MaterialInfo m2 = new MaterialInfo();
        ArrayList<Long> clkList2 = new ArrayList<>();
        clkList2.add(0,15l);
        clkList2.add(1,0l);
        ArrayList<Long> expList2 = new ArrayList<>();
        expList2.add(0,20l);
        expList2.add(1,0l);
        m2.appId = 1;
        m2.setMaterialId(2);
        m2.setClick(clkList2);
        m2.setExposure(expList2);
        m2.setCount(0);
        m2.setLastClick(0);
        m2.setLastExposure(0);
        m2.setReward(0);
        ArrayList<Long> clkList4 = new ArrayList<>();
        clkList4.add(0,20l);
        clkList4.add(1,0l);
        ArrayList<Long> expList4 = new ArrayList<>();
        expList4.add(0,100l);
        expList4.add(1,0l);
        m2.globalClick = clkList4;
        m2.globalExposure = expList4;
        m2.globalLastClick = 0;
        m2.globalLastExposure = 0;

        ArrayList<MaterialInfo> mlist = new ArrayList<>();
        mlist.add(m1);
        mlist.add(m2);

        int cnt1 = 0,cnt2 = 0;
        System.out.println("materialId\talpha\tm.beta\tm.reward\tm.hctr");
        for(int i = 0;i<100;i++)
        {
            List<MaterialInfo> rlist = bandit.selectMaterial(mlist,1l);

            if(rlist.get(0).materialId == 1)
            {
                cnt1++;
                System.out.println(rlist.get(0).materialId+"\t"+rlist.get(0).rank);
            }
            else {
                cnt2++;
                System.out.println(rlist.get(0).materialId+"\t"+rlist.get(0).rank);
            }

           // System.out.println(m.materialId+"\t"+m.alpha+"\t"+m.beta+"\t"+m.reward/m.count+"\t"+m.lastClick+"\t"+m.lastExposure);
        }
        System.out.println("cnt1 = "+cnt1+" cnt2="+cnt2);

    }
}
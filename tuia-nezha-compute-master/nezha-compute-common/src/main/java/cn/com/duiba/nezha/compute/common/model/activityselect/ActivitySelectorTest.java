package cn.com.duiba.nezha.compute.common.model.activityselect;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Comparator;

public class ActivitySelectorTest extends TestCase {
    protected void setUp() throws Exception {
        super.setUp();
        System.out.println();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        System.out.println();
    }

    public void testBayesianBandit() throws Exception { //几个候选集

        ActivitySelector bandit = new ActivitySelector();


        //初始化两个素材
        ActivityInfo a1 = new ActivityInfo();
        a1.activityId = 1816;
        a1.alpha = 1.990099501;
        a1.beta = 6.4108955089999995;
        a1.count = 4.90099501;
        a1.reward = 0;
        a1.source = 1;

        ActivityInfo.Val val = a1.getVal();
        val.globalVal = 0;
        val.appVal = 0;
        val.slotVal = 0;
        a1.hisRequest = val;

        val = a1.getVal();
        val.globalVal = 0;
        val.appVal = 0;
        val.slotVal = 0;
        a1.hisClick = val;

        val = a1.getVal();
        val.globalVal = 0;
        val.appVal = 0;
        val.slotVal = 0;
        a1.hisCost = val;

        val = a1.getVal();
        val.globalVal = 0;
        val.appVal = 0;
        val.slotVal = 0;
        a1.hisSend = val;

        val = a1.getVal();
        val.globalVal = 5723;
        val.appVal = 1118;
        val.slotVal = 338;
        a1.send = val;

        val = a1.getVal();
        val.globalVal = 5723;
        val.appVal = 1118;
        val.slotVal = 338;
        a1.directSend = val;

        val = a1.getVal();
        val.globalVal = 4410;
        val.appVal = 782;
        val.slotVal = 273;
        a1.request = val;

        val = a1.getVal();
        val.globalVal = 4410;
        val.appVal = 782;
        val.slotVal = 273;
        a1.directRequest = val;

        val = a1.getVal();
        val.globalVal = 4410;
        val.appVal = 782;
        val.slotVal = 273;
        a1.click = val;

        val = a1.getVal();
        val.globalVal = 4410;
        val.appVal = 782;
        val.slotVal = 273;
        a1.directClick = val;

        val = a1.getVal();
        val.globalVal = 4410;
        val.appVal = 782;
        val.slotVal = 273;
        a1.cost = val;

        val = a1.getVal();
        val.globalVal = 4410;
        val.appVal = 782;
        val.slotVal = 273;
        a1.directCost = val;

        val = a1.getVal();
        val.globalVal = 0;
        val.appVal = 0;
        val.slotVal = 0;
        a1.lastSend = val;

        val = a1.getVal();
        val.globalVal = 0;
        val.appVal = 0;
        val.slotVal = 0;
        a1.lastRequest = val;

        val = a1.getVal();
        val.globalVal = 0;
        val.appVal = 0;
        val.slotVal = 0;
        a1.lastClick = val;

        val = a1.getVal();
        val.globalVal = 0;
        val.appVal = 0;
        val.slotVal = 0;
        a1.lastCost = val;

        a1.updateTime = 1511319423773l;
        a1.isUpdate = true;

        ActivityInfo a2 = new ActivityInfo();
        a2.activityId = 1977;
        a2.alpha = 1.990099501;
        a2.beta = 6.4108955089999995;
        a2.count = 4.90099501;
        a2.reward = 0;
        a2.source = 1;
        val = a2.getVal();
        val.globalVal = 0;
        val.appVal = 0;
        val.slotVal = 0;
        a2.hisRequest = val;

        val = a2.getVal();
        val.globalVal = 0;
        val.appVal = 0;
        val.slotVal = 0;
        a2.hisSend = val;

        val = a2.getVal();
        val.globalVal = 0;
        val.appVal = 0;
        val.slotVal = 0;
        a2.hisClick = val;

        val = a2.getVal();
        val.globalVal = 0;
        val.appVal = 0;
        val.slotVal = 0;
        a2.hisCost = val;

        val = a2.getVal();
        val.globalVal = 70803;
        val.appVal = 4640;
        val.slotVal = 4636;
        a2.send = val;

        val = a2.getVal();
        val.globalVal = 70803;
        val.appVal = 4640;
        val.slotVal = 4636;
        a2.directSend = val;

        val = a2.getVal();
        val.globalVal = 50527;
        val.appVal = 3903;
        val.slotVal = 3423;
        a2.request = val;

        val = a2.getVal();
        val.globalVal = 50527;
        val.appVal = 3903;
        val.slotVal = 3423;
        a2.directRequest = val;

        val = a2.getVal();
        val.globalVal = 50527;
        val.appVal = 3903;
        val.slotVal = 3423;
        a2.click = val;

        val = a2.getVal();
        val.globalVal = 50527;
        val.appVal = 3903;
        val.slotVal = 3423;
        a2.directClick = val;

        val = a2.getVal();
        val.globalVal = 50527;
        val.appVal = 3903;
        val.slotVal = 3423;
        a2.cost = val;

        val = a2.getVal();
        val.globalVal = 50527;
        val.appVal = 3903;
        val.slotVal = 3423;
        a2.directCost = val;

        val = a2.getVal();
        val.globalVal = 0;
        val.appVal = 0;
        val.slotVal = 0;
        a2.lastSend = val;

        val = a2.getVal();
        val.globalVal = 0;
        val.appVal = 0;
        val.slotVal = 0;
        a2.lastRequest = val;

        val = a2.getVal();
        val.globalVal = 0;
        val.appVal = 0;
        val.slotVal = 0;
        a2.lastClick = val;

        val = a2.getVal();
        val.globalVal = 0;
        val.appVal = 0;
        val.slotVal = 0;
        a2.lastCost = val;

        a2.updateTime = 1511319423773l;
        a2.isUpdate = true;

        ArrayList<ActivityInfo> alist = new ArrayList<>();
        alist.add(a1);
        alist.add(a2);


        int cnt1 = 0,cnt2 = 0;
        for(int i = 0;i<10;i++)
        {
            ActivityInfo act = bandit.select(alist);

            //System.out.println("act = "+act.activityId);

            if (act.activityId == 1977) {
                alist.set(1, act);//update
                cnt2++;
            } else if (act.activityId == 1816) {
                alist.set(0, act);
                cnt1++;
            }

            //System.out.println(m.materialId+"\t"+m.alpha+"\t"+m.beta+"\t"+m.reward/m.count+"\t"+m.lastClick+"\t"+m.lastExposure);
        }
        System.out.println("cnt1 = "+cnt1+" cnt2="+cnt2);
    }

    public Comparator<Integer> iComparator = new Comparator<Integer>() {

        @Override
        public int compare(Integer c1, Integer c2) {
            return (int) (c2 - c1);
        }
    };
}
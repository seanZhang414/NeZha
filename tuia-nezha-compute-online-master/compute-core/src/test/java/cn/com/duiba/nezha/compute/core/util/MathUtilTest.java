package cn.com.duiba.nezha.compute.core.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class MathUtilTest {

    @Test
    public void sigmoid() {
    }

    @Test
    public void stdwithBoundary() {
        System.out.println("t1="+MathUtil.stdwithBoundary(-2.2,-1.0,1.0));
        System.out.println("t2="+MathUtil.stdwithBoundary(0.2,-1.0,1.0));
        System.out.println("t3="+MathUtil.stdwithBoundary(1.2,-1.0,1.0));

    }
}